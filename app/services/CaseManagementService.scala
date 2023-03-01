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

import com.google.inject.{Inject, Singleton, ImplementedBy}
import models.store.{Notification, FullDisclosure, Submission}
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, ZoneId}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content._
import models.store.notification._
import play.twirl.api.HtmlFormat
import java.util.UUID
import play.api.mvc.Call
import models.SubmissionType
import scala.concurrent.{ExecutionContext, Future}
import views.html.components.link
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{Table, HeadCell, TableRow}
import play.api.i18n.Messages

@Singleton
class CaseManagementServiceImpl @Inject()(link: link) extends CaseManagementService {

  def getRedirection(submission: Submission): Call = {
    val status = submission match {
      case _: FullDisclosure => if (submission.metadata.submissionTime.isDefined) SentDisclosure else StartedDisclosure
      case _: Notification => if (submission.metadata.submissionTime.isDefined) SentNotification else StartedNotification
    }

    status match {
      case SentNotification => controllers.routes.NotificationSubmittedController.onSubmit
      case StartedNotification => controllers.routes.NotificationStartedController.onSubmit
      case SentDisclosure => controllers.routes.TaskListController.onPageLoad
      case StartedDisclosure => controllers.routes.TaskListController.onPageLoad
    }
  }

  def generateCaseManagementTable(submissions: Seq[Submission])(implicit messages: Messages): Table = {
    val rows = submissionsToRows(submissions)
    createTable(rows)
  }

  def submissionsToRows(submissions: Seq[Submission])(implicit messages: Messages): Seq[Seq[TableRow]] = {
    val sorted = submissions.sortWith(_.created isAfter _.created)
    sorted.map(storeEntryToTableRow)
  }

  def createTable(rows: Seq[Seq[TableRow]])(implicit messages: Messages): Table = 
    Table(
      rows = rows,
      head = Some(Seq(
        HeadCell(Text(messages("caseManagement.column.reference"))),
        HeadCell(Text(messages("caseManagement.column.type"))),
        HeadCell(Text(messages("caseManagement.column.created"))),
        HeadCell(Text(messages("caseManagement.column.status"))),
        HeadCell(Text(messages("caseManagement.column.accessUntil"))),
        HeadCell(Text(messages("caseManagement.column.access"))),
      )),
      attributes = Map("id" -> "case-table")
    )

  def storeEntryToTableRow(submission: Submission)(implicit messages: Messages): Seq[TableRow] = {

    val status = getStatus(submission)
    val statusKey = getStatusKey(status)
    val accessKey = getAccessKey(status)

    val reference = getReference(submission)

    Seq(
      TableRow(HtmlContent(HtmlFormat.escape(reference))),
      TableRow(getCaseType(submission)),
      TableRow(Text(getCreatedDate(submission))),
      TableRow(Text(messages(statusKey))),
      TableRow(Text(getAccessUntilDate(submission))),
      TableRow(HtmlContent(link(s"access-$reference", messages(accessKey), controllers.routes.CaseManagementController.navigateToSubmission(submission.submissionId))))
    )
  }

  def getReference(submission: Submission)(implicit messages: Messages): String = 
    submission.personalDetails.background.disclosureEntity.flatMap(_ match {
      case DisclosureEntity(Individual, _) => submission.personalDetails.aboutTheIndividual.flatMap(_.fullName).orElse(submission.personalDetails.aboutYou.fullName)
      case DisclosureEntity(Estate, _) => submission.personalDetails.aboutTheEstate.flatMap(_.fullName)
      case DisclosureEntity(Company, _) => submission.personalDetails.aboutTheCompany.flatMap(_.name)
      case DisclosureEntity(LLP, _) => submission.personalDetails.aboutTheLLP.flatMap(_.name)
      case DisclosureEntity(Trust, _) => submission.personalDetails.aboutTheTrust.flatMap(_.name)
    }).getOrElse(messages("caseManagement.incomplete"))

  def getCaseType(submission: Submission)(implicit messages: Messages): Content = {
    val offshoreAnswer = submission.personalDetails.background.offshoreLiabilities
    val onshoreAnswer = submission.personalDetails.background.onshoreLiabilities

    (offshoreAnswer, onshoreAnswer) match {
      case (Some(true), Some(false)) => Text(messages("liabilities.offshore"))
      case (Some(false), _) => Text(messages("liabilities.onshore"))
      case (Some(true), Some(true)) => Text(messages("liabilities.offshoreOnshore"))
      case _ => Text(messages("caseManagement.incomplete"))
    }
  }

  def getStatus(submission: Submission): CaseStatus = {
    submission match {
      case _: FullDisclosure => if (submission.metadata.submissionTime.isDefined) SentDisclosure else StartedDisclosure
      case _: Notification => if (submission.metadata.submissionTime.isDefined) SentNotification else StartedNotification
    }
  }

  def getStatusKey(status: CaseStatus): String = 
    status match {
      case SentNotification => "caseManagement.notification.sent"
      case StartedNotification => "caseManagement.notification.started"
      case SentDisclosure => "caseManagement.disclosure.sent"
      case StartedDisclosure => "caseManagement.disclosure.started"
    }

  def getAccessKey(status: CaseStatus): String = 
    status match {
      case SentNotification => "caseManagement.access.makeDisclosure"
      case StartedNotification => "caseManagement.access.edit"
      case SentDisclosure => "caseManagement.access.view"
      case StartedDisclosure => "caseManagement.access.edit"
    }

  def getAccessUntilDate(submission: Submission): String = {
    val date = submission.lastUpdated.atZone(ZoneId.systemDefault).toLocalDateTime()
    val finalAccessDate = date.plusDays(30)
    val dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy HH:mma")
    finalAccessDate.format(dateFormatter)
  }

  def getCreatedDate(submission: Submission): String = {
    val date = submission.created.atZone(ZoneId.systemDefault).toLocalDateTime()
    val dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy HH:mma")
    date.format(dateFormatter)
  } 

}

@ImplementedBy(classOf[CaseManagementServiceImpl])
trait CaseManagementService {
  def getRedirection(submission: Submission): Call
  def generateCaseManagementTable(submissions: Seq[Submission])(implicit messages: Messages): Table
}

sealed trait CaseStatus 
case object SentNotification extends CaseStatus
case object StartedNotification extends CaseStatus
case object SentDisclosure extends CaseStatus
case object StartedDisclosure extends CaseStatus