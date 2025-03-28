/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package services

import com.google.inject.{ImplementedBy, Inject, Singleton}
import models.store.{FullDisclosure, Notification, Submission}
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.ZoneId
import uk.gov.hmrc.govukfrontend.views.viewmodels.content._
import models.store.notification._
import play.api.mvc.Call
import views.html.components.linkWithVisuallyHiddenContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{HeadCell, Table, TableRow}
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination.{Pagination, PaginationItem, PaginationLink}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text

@Singleton
class CaseManagementServiceImpl @Inject() (linkWithVisuallyHiddenContent: linkWithVisuallyHiddenContent)
    extends CaseManagementService {

  private val ROWS_ON_PAGE = 10
  private val zoneId       = ZoneId.of("Europe/London")

  def getRedirection(submission: Submission): Call = {
    val status = getStatus(submission)

    status match {
      case SentNotification    => controllers.routes.NotificationSubmittedController.onSubmit
      case StartedNotification => controllers.routes.NotificationStartedController.onSubmit
      case SentDisclosure      => controllers.routes.PdfGenerationController.generateForSubmissionId(submission.submissionId)
      case StartedDisclosure   => controllers.routes.TaskListController.onPageLoad
    }
  }

  def getNumberOfPages(submissions: Seq[Submission]): Int = submissions.grouped(ROWS_ON_PAGE).size

  def generateCaseManagementTable(paginationIndex: Int, submissions: Seq[Submission])(implicit
    messages: Messages
  ): Option[Table] = {
    val paginatedSubmissions: Seq[Seq[Submission]] =
      submissions.sortWith(_.created isAfter _.created).grouped(ROWS_ON_PAGE).toSeq
    val currentPage: Option[Seq[Submission]]       = paginatedSubmissions.lift(paginationIndex - 1)

    currentPage.map { pageSubmissions =>
      val rows = submissionsToRows(pageSubmissions)
      createTable(rows)
    }
  }

  def submissionsToRows(submissions: Seq[Submission])(implicit messages: Messages): Seq[Seq[TableRow]] = {
    val sorted = submissions
    sorted.map(storeEntryToTableRow)
  }

  def createTable(rows: Seq[Seq[TableRow]])(implicit messages: Messages): Table =
    Table(
      rows = rows,
      head = Some(
        Seq(
          HeadCell(Text(messages("caseManagement.column.reference"))),
          HeadCell(Text(messages("caseManagement.column.type"))),
          HeadCell(Text(messages("caseManagement.column.created"))),
          HeadCell(Text(messages("caseManagement.column.status"))),
          HeadCell(Text(messages("caseManagement.column.accessUntil"))),
          HeadCell(Text(messages("caseManagement.column.access")))
        )
      ),
      firstCellIsHeader = true,
      attributes = Map("id" -> "case-table"),
      caption = Some(messages("caseManagement.caption")),
      captionClasses = "govuk-heading-l"
    )

  def storeEntryToTableRow(submission: Submission)(implicit messages: Messages): Seq[TableRow] = {

    val status    = getStatus(submission)
    val statusKey = getStatusKey(status)
    val accessKey = getAccessKey(status)

    val reference  = getReference(submission)
    val hiddenText = getVisuallyHiddenText(submission, status, reference)

    Seq(
      TableRow(Text(reference)),
      TableRow(getCaseType(submission)),
      TableRow(Text(getCreatedDate(submission)(messages))),
      TableRow(Text(messages(statusKey))),
      TableRow(Text(getAccessUntilDate(submission)(messages))),
      TableRow(
        HtmlContent(
          linkWithVisuallyHiddenContent(
            s"access-$reference",
            messages(accessKey),
            if (status == SentDisclosure)
              controllers.routes.PdfGenerationController.generateForSubmissionId(submission.submissionId)
            else controllers.routes.CaseManagementController.navigateToSubmission(submission.submissionId),
            showId = false,
            visuallyHiddenText = hiddenText,
            download = status == SentDisclosure
          )
        )
      )
    )
  }

  private def getVisuallyHiddenText(submission: Submission, status: CaseStatus, ref: String)(implicit
    messages: Messages
  ): String =
    getReferenceOption(submission) match {
      case Some(reference) if status == SentNotification => messages("caseManagement.hidden.make.disclosure", reference)
      case Some(reference)                               => reference
      case _                                             => messages("caseManagement.hidden.edit.no.reference", getCreatedDate(submission))
    }

  def getReference(submission: Submission)(implicit messages: Messages): String =
    getReferenceOption(submission).getOrElse(messages("caseManagement.incomplete"))
  def getReferenceOption(submission: Submission): Option[String]                =
    submission.personalDetails.background.disclosureEntity.flatMap(_ match {
      case DisclosureEntity(Individual, _) =>
        submission.personalDetails.aboutTheIndividual
          .flatMap(_.fullName)
          .orElse(submission.personalDetails.aboutYou.fullName)
      case DisclosureEntity(Estate, _)     => submission.personalDetails.aboutTheEstate.flatMap(_.fullName)
      case DisclosureEntity(Company, _)    => submission.personalDetails.aboutTheCompany.flatMap(_.name)
      case DisclosureEntity(LLP, _)        => submission.personalDetails.aboutTheLLP.flatMap(_.name)
      case DisclosureEntity(Trust, _)      => submission.personalDetails.aboutTheTrust.flatMap(_.name)
    })

  def getCaseType(submission: Submission)(implicit messages: Messages): Content = {
    val offshoreAnswer = submission.personalDetails.background.offshoreLiabilities
    val onshoreAnswer  = submission.personalDetails.background.onshoreLiabilities

    (offshoreAnswer, onshoreAnswer) match {
      case (Some(true), Some(false)) => Text(messages("liabilities.offshore"))
      case (Some(false), _)          => Text(messages("liabilities.onshore"))
      case (Some(true), Some(true))  => Text(messages("liabilities.offshoreOnshore"))
      case _                         => Text(messages("caseManagement.incomplete"))
    }
  }

  def getStatus(submission: Submission): CaseStatus =
    submission match {
      case _: FullDisclosure => if (submission.metadata.submissionTime.isDefined) SentDisclosure else StartedDisclosure
      case _: Notification   =>
        if (submission.metadata.submissionTime.isDefined) SentNotification else StartedNotification
    }

  def getStatusKey(status: CaseStatus): String =
    status match {
      case SentNotification    => "caseManagement.notification.sent"
      case StartedNotification => "caseManagement.notification.started"
      case SentDisclosure      => "caseManagement.disclosure.sent"
      case StartedDisclosure   => "caseManagement.disclosure.started"
    }

  def getAccessKey(status: CaseStatus): String =
    status match {
      case SentNotification    => "caseManagement.access.makeDisclosure"
      case StartedNotification => "caseManagement.access.edit"
      case SentDisclosure      => "caseManagement.access.view"
      case StartedDisclosure   => "caseManagement.access.edit"
    }

  def getAccessUntilDate(submission: Submission)(implicit messages: Messages): String = {
    val date            = submission.lastUpdated.atZone(zoneId).toLocalDateTime
    val finalAccessDate = date.plusDays(30)
    val dateFormatter   = DateTimeFormatter.ofPattern("d MMM yyyy HH:mma", new Locale(messages.lang.code))
    finalAccessDate.format(dateFormatter)
  }

  def getCreatedDate(submission: Submission)(implicit messages: Messages): String = {
    val date          = submission.created.atZone(zoneId).toLocalDateTime
    val dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy HH:mma", new Locale(messages.lang.code))
    date.format(dateFormatter)
  }

  def generatePagination(paginationIndex: Int, numberOfPages: Int)(implicit messages: Messages): Option[Pagination] =
    if (numberOfPages < 2) None
    else
      Some(
        Pagination(
          items = Some(generatePaginationItems(paginationIndex, numberOfPages)),
          previous = generatePreviousLink(paginationIndex, numberOfPages),
          next = generateNextLink(paginationIndex, numberOfPages),
          landmarkLabel = None,
          classes = "",
          attributes = Map.empty
        )
      )

  def generatePaginationItems(paginationIndex: Int, numberOfPages: Int): Seq[PaginationItem] =
    Range
      .inclusive(1, numberOfPages)
      .map(pageIndex =>
        PaginationItem(
          href = controllers.routes.CaseManagementController.onPageLoad(pageIndex).url,
          number = Some(pageIndex.toString),
          visuallyHiddenText = None,
          current = Some(pageIndex == paginationIndex),
          ellipsis = None,
          attributes = Map.empty
        )
      )

  def generatePreviousLink(paginationIndex: Int, numberOfPages: Int)(implicit
    messages: Messages
  ): Option[PaginationLink] =
    if (paginationIndex == 1) None
    else {
      Some(
        PaginationLink(
          href = controllers.routes.CaseManagementController.onPageLoad(paginationIndex - 1).url,
          text = Some(messages("caseManagement.pagination.previous")),
          labelText = Some(messages("caseManagement.pagination.previous.hidden")),
          attributes = Map.empty
        )
      )
    }

  def generateNextLink(paginationIndex: Int, numberOfPages: Int)(implicit messages: Messages): Option[PaginationLink] =
    if (paginationIndex == numberOfPages) None
    else {
      Some(
        PaginationLink(
          href = controllers.routes.CaseManagementController.onPageLoad(paginationIndex + 1).url,
          text = Some(messages("caseManagement.pagination.next")),
          labelText = Some(messages("caseManagement.pagination.next.hidden")),
          attributes = Map.empty
        )
      )
    }

}

@ImplementedBy(classOf[CaseManagementServiceImpl])
trait CaseManagementService {
  def getRedirection(submission: Submission): Call
  def generateCaseManagementTable(paginationIndex: Int, submissions: Seq[Submission])(implicit
    messages: Messages
  ): Option[Table]
  def getNumberOfPages(submissions: Seq[Submission]): Int
  def generatePagination(paginationIndex: Int, numberOfPages: Int)(implicit messages: Messages): Option[Pagination]
}

sealed trait CaseStatus
case object SentNotification extends CaseStatus
case object StartedNotification extends CaseStatus
case object SentDisclosure extends CaseStatus
case object StartedDisclosure extends CaseStatus
