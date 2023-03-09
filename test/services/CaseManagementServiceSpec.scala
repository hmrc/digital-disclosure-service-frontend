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

import base.ViewSpecBase
import views.html.components.link
import java.time.{Instant, LocalDate, ZoneId, LocalDateTime}
import java.time.format.DateTimeFormatter
import models.store._
import models.store.notification._
import models.store.disclosure._
import uk.gov.hmrc.govukfrontend.views.viewmodels.content._
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.TableRow
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination.{PaginationItem, PaginationLink}

class CaseManagementServiceSpec extends ViewSpecBase {

  val dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy HH:mma")

  val linkHtml = inject[link]
  val sut = new CaseManagementServiceImpl(linkHtml)

  val createdInstant = LocalDate.of(1990, 8, 23).atStartOfDay.atZone(ZoneId.systemDefault).toInstant
  val lastUpdatedInstant = LocalDate.of(2012, 1, 1).atStartOfDay.atZone(ZoneId.systemDefault).toInstant
  val submittedDateTime = LocalDateTime.ofInstant(createdInstant, ZoneId.systemDefault);

  "getCreatedDate" should {
    "format retrieve the created date from a submission and format it correctly" in {
      val expectedDate = LocalDateTime.of(1990,8,23, 0, 0).format(dateFormatter)
      val notification = Notification("userId", "submissionId", lastUpdatedInstant, Metadata(), PersonalDetails(Background(), AboutYou()), created = createdInstant)
      sut.getCreatedDate(notification) mustEqual expectedDate
    }
  }

  "getAccessUntilDate" should {
    "format retrieve the created date from a submission and format it correctly" in {
      val expectedDate = LocalDateTime.of(2012,1,31, 0, 0).format(dateFormatter)
      val notification = Notification("userId", "submissionId", lastUpdatedInstant, Metadata(), PersonalDetails(Background(), AboutYou()), created = createdInstant)
      sut.getAccessUntilDate(notification) mustEqual expectedDate
    }
  }

  "getAccessKey" should {
    "return makeDisclosure where the status is SentNotification" in {
      sut.getAccessKey(SentNotification) mustEqual "caseManagement.access.makeDisclosure"
    }

    "return edit where the status is StartedNotification" in {
      sut.getAccessKey(StartedNotification) mustEqual "caseManagement.access.edit"
    }

    "return view where the status is SentDisclosure" in {
      sut.getAccessKey(SentDisclosure) mustEqual "caseManagement.access.view"
    }

    "return edit where the status is StartedDisclosure" in {
      sut.getAccessKey(StartedDisclosure) mustEqual "caseManagement.access.edit"
    }
  }

  "getStatusKey" should {
    "return notification.sent where the status is SentNotification" in {
      sut.getStatusKey(SentNotification) mustEqual "caseManagement.notification.sent"
    }

    "return notification.started where the status is StartedNotification" in {
      sut.getStatusKey(StartedNotification) mustEqual "caseManagement.notification.started"
    }

    "return disclosure.sent where the status is SentDisclosure" in {
      sut.getStatusKey(SentDisclosure) mustEqual "caseManagement.disclosure.sent"
    }

    "return disclosure.started where the status is StartedDisclosure" in {
      sut.getStatusKey(StartedDisclosure) mustEqual "caseManagement.disclosure.started"
    }
  }

  "getStatus" should {
    "return SentDisclosure where it's a disclosure and has been sent" in {
      val submission = FullDisclosure("123", "123", Instant.now(), Metadata(reference = Some("SomeRef"), submissionTime = Some(submittedDateTime)), CaseReference(), PersonalDetails(Background(), AboutYou()), None, OffshoreLiabilities(), OtherLiabilities(), ReasonForDisclosingNow())
      sut.getStatus(submission) mustEqual SentDisclosure
    }

    "return StartedDisclosure where it's a disclosure and has not been sent" in {
      val submission = FullDisclosure("123", "123", Instant.now(), Metadata(), CaseReference(), PersonalDetails(Background(), AboutYou()), None, OffshoreLiabilities(), OtherLiabilities(), ReasonForDisclosingNow())
      sut.getStatus(submission) mustEqual StartedDisclosure
    }

    "return SentNotification where it's a notification and has been sent" in {
      val submission = Notification("userId", "submissionId", lastUpdatedInstant, Metadata(reference = Some("SomeRef"), submissionTime = Some(submittedDateTime)), PersonalDetails(Background(), AboutYou()), created = createdInstant)
      sut.getStatus(submission) mustEqual SentNotification
    }

    "return StartedNotification where it's a notification and has not been sent" in {
      val submission = Notification("userId", "submissionId", lastUpdatedInstant, Metadata(), PersonalDetails(Background(), AboutYou()), created = createdInstant)
      sut.getStatus(submission) mustEqual StartedNotification
    }
  }

  "getCaseType" should {
    "return offshore where only offshore is selected" in {
      val submission = Notification("userId", "submissionId", lastUpdatedInstant, Metadata(), PersonalDetails(Background(offshoreLiabilities = Some(true), onshoreLiabilities = Some(false)), AboutYou()), created = createdInstant)
      sut.getCaseType(submission) mustEqual Text(messages("liabilities.offshore"))
    }

    "return onshore where offshore is not selected" in {
      val submission = Notification("userId", "submissionId", lastUpdatedInstant, Metadata(), PersonalDetails(Background(offshoreLiabilities = Some(false)), AboutYou()), created = createdInstant)
      sut.getCaseType(submission) mustEqual Text(messages("liabilities.onshore"))
    }

    "return offshoreOnshore where both are selected" in {
      val submission = Notification("userId", "submissionId", lastUpdatedInstant, Metadata(), PersonalDetails(Background(offshoreLiabilities = Some(true), onshoreLiabilities = Some(true)), AboutYou()), created = createdInstant)
      sut.getCaseType(submission) mustEqual Text(messages("liabilities.offshoreOnshore"))
    }

    "return incomplete where the offshore/onshore questions are yet to be answered" in {
      val submission = Notification("userId", "submissionId", lastUpdatedInstant, Metadata(), PersonalDetails(Background(), AboutYou()), created = createdInstant)
      sut.getCaseType(submission) mustEqual Text(messages("caseManagement.incomplete"))
    }
  }

  "getReference" should {
    "return your full name where you are the individual" in {
      val submission = Notification("userId", "submissionId", lastUpdatedInstant, Metadata(), PersonalDetails(Background(disclosureEntity = Some(DisclosureEntity(Individual, None))), AboutYou(fullName = Some("Some name"))))
      sut.getReference(submission) mustEqual "Some name"
    }


    "return the individuals full name where you are not the individual" in {
      val aboutTheIndividual = AboutTheIndividual(fullName = Some("Some ind name"))
      val submission = Notification("userId", "submissionId", lastUpdatedInstant, Metadata(), PersonalDetails(Background(disclosureEntity = Some(DisclosureEntity(Individual, None))), AboutYou(), aboutTheIndividual = Some(aboutTheIndividual)))
      sut.getReference(submission) mustEqual "Some ind name"
    }

    "return the deceased's full name where it's an estate" in {
      val aboutTheEstate = AboutTheEstate(fullName = Some("Some estate name"))
      val submission = Notification("userId", "submissionId", lastUpdatedInstant, Metadata(), PersonalDetails(Background(disclosureEntity = Some(DisclosureEntity(Estate, None))), AboutYou(), aboutTheEstate = Some(aboutTheEstate)))
      sut.getReference(submission) mustEqual "Some estate name" 
    }

    "return the company name where it's a company" in {
      val aboutTheCompany = AboutTheCompany(name = Some("Some com name"))
      val submission = Notification("userId", "submissionId", lastUpdatedInstant, Metadata(), PersonalDetails(Background(disclosureEntity = Some(DisclosureEntity(Company, None))), AboutYou(), aboutTheCompany = Some(aboutTheCompany)))
      sut.getReference(submission) mustEqual "Some com name"        
    }

    "return the trust name where it's a trust" in {
      val aboutTheTrust = AboutTheTrust(name = Some("Some trust name"))
      val submission = Notification("userId", "submissionId", lastUpdatedInstant, Metadata(), PersonalDetails(Background(disclosureEntity = Some(DisclosureEntity(Trust, None))), AboutYou(), aboutTheTrust = Some(aboutTheTrust)))
      sut.getReference(submission) mustEqual "Some trust name"      
    }

    "return the LLP name where it's a LLP" in {
      val aboutTheLLP = AboutTheLLP(name = Some("Some llp name"))
      val submission = Notification("userId", "submissionId", lastUpdatedInstant, Metadata(), PersonalDetails(Background(disclosureEntity = Some(DisclosureEntity(LLP, None))), AboutYou(), aboutTheLLP = Some(aboutTheLLP)))
      sut.getReference(submission) mustEqual "Some llp name"     
    }

    "return incomplete where the name questions haven't been answered yet" in {
      val submission = Notification("userId", "submissionId", lastUpdatedInstant, Metadata(), PersonalDetails(Background(), AboutYou()))
      sut.getReference(submission) mustEqual messages("caseManagement.incomplete")
    }
  }

  "getRedirection" should {
    "return SentDisclosure where it's a disclosure and has been sent" in {
      val submission = FullDisclosure("123", "123", Instant.now(), Metadata(reference = Some("SomeRef"), submissionTime = Some(submittedDateTime)), CaseReference(), PersonalDetails(Background(), AboutYou()), None, OffshoreLiabilities(), OtherLiabilities(), ReasonForDisclosingNow())
      sut.getRedirection(submission) mustEqual controllers.routes.PdfGenerationController.generateForSubmissionId("123")
    }

    "return StartedDisclosure where it's a disclosure and has not been sent" in {
      val submission = FullDisclosure("123", "123", Instant.now(), Metadata(), CaseReference(), PersonalDetails(Background(), AboutYou()), None, OffshoreLiabilities(), OtherLiabilities(), ReasonForDisclosingNow())
      sut.getRedirection(submission) mustEqual controllers.routes.TaskListController.onPageLoad
    }

    "return SentNotification where it's a notification and has been sent" in {
      val submission = Notification("userId", "submissionId", lastUpdatedInstant, Metadata(reference = Some("SomeRef"), submissionTime = Some(submittedDateTime)), PersonalDetails(Background(), AboutYou()), created = createdInstant)
      sut.getRedirection(submission) mustEqual controllers.routes.NotificationSubmittedController.onSubmit
    }

    "return StartedNotification where it's a notification and has not been sent" in {
      val submission = Notification("userId", "submissionId", lastUpdatedInstant, Metadata(), PersonalDetails(Background(), AboutYou()), created = createdInstant)
      sut.getRedirection(submission) mustEqual controllers.routes.NotificationStartedController.onSubmit
    }
  }

  "storeEntryToTableRow" should {
    "generate the row" in {
      val expectedCreatedDate = LocalDateTime.of(1990,8,23, 0, 0).format(dateFormatter)
      val expectedDate = LocalDateTime.of(2012,1,31, 0, 0).format(dateFormatter)
      val submission = FullDisclosure("123", "123", lastUpdatedInstant, Metadata(), CaseReference(), PersonalDetails(Background(), AboutYou()), None, OffshoreLiabilities(), OtherLiabilities(), ReasonForDisclosingNow(), created = createdInstant)
      val expected = Seq(
        TableRow(HtmlContent(messages("caseManagement.incomplete"))),
        TableRow(Text(messages("caseManagement.incomplete"))),
        TableRow(Text(expectedCreatedDate)),
        TableRow(Text(messages("caseManagement.disclosure.started"))),
        TableRow(Text(expectedDate)),
        TableRow(HtmlContent(linkHtml(s"access-${messages("caseManagement.incomplete")}", messages("caseManagement.access.edit"), controllers.routes.CaseManagementController.navigateToSubmission("123"))))
      )
      sut.storeEntryToTableRow(submission) mustEqual expected

    }

  }

  "getNumberOfPages" should {

    val submission = Notification("userId", "submissionId", lastUpdatedInstant, Metadata(), PersonalDetails(Background(), AboutYou()), created = createdInstant)
    def listOfNSubmissions(n: Int): Seq[Submission] = Range.inclusive(1, n).map(_ => submission).toSeq

    "return 1 where there is 1 submission" in {
      val submissions: Seq[Submission] = listOfNSubmissions(1)
      sut.getNumberOfPages(submissions) mustEqual 1
    }

    "return 1 where there are 10 submissions" in {
      val submissions: Seq[Submission] = listOfNSubmissions(10)
      sut.getNumberOfPages(submissions) mustEqual 1
    }

    "return 2 where there are 11 submissions" in {
      val submissions: Seq[Submission] = listOfNSubmissions(11)
      sut.getNumberOfPages(submissions) mustEqual 2
    }

    "return 2 where there are 20 submissions" in {
      val submissions: Seq[Submission] = listOfNSubmissions(20)
      sut.getNumberOfPages(submissions) mustEqual 2
    }

    "return 3 where there are 21 submissions" in {
      val submissions: Seq[Submission] = listOfNSubmissions(21)
      sut.getNumberOfPages(submissions) mustEqual 3
    }

    "return 3 where there are 30 submissions" in {
      val submissions: Seq[Submission] = listOfNSubmissions(30)
      sut.getNumberOfPages(submissions) mustEqual 3
    }

    "return 10 where there are 100 submissions" in {
      val submissions: Seq[Submission] = listOfNSubmissions(100)
      sut.getNumberOfPages(submissions) mustEqual 10
    }

  }

  "generateNextLink" should {

    "return None where we're at the final page" in {
      sut.generateNextLink(1, 1) mustEqual None
      sut.generateNextLink(2, 2) mustEqual None
      sut.generateNextLink(3, 3) mustEqual None
    }

    "return a Next link where we are on the first page of multiple" in {
      val expectedLink = Some(PaginationLink(
        href = controllers.routes.CaseManagementController.onPageLoad(2).url,
        text = Some(messages("caseManagement.pagination.next")),
        labelText = Some(messages("caseManagement.pagination.next.hidden")),
        attributes = Map.empty
      ))
      sut.generateNextLink(1, 3) mustEqual expectedLink
    }

    "return a Next link where we are on the second page of multiple" in {
      val expectedLink = Some(PaginationLink(
        href = controllers.routes.CaseManagementController.onPageLoad(3).url,
        text = Some(messages("caseManagement.pagination.next")),
        labelText = Some(messages("caseManagement.pagination.next.hidden")),
        attributes = Map.empty
      ))
      sut.generateNextLink(2, 3) mustEqual expectedLink
    }

    "return a Next link where we are many pages in" in {
      val expectedLink = Some(PaginationLink(
        href = controllers.routes.CaseManagementController.onPageLoad(6).url,
        text = Some(messages("caseManagement.pagination.next")),
        labelText = Some(messages("caseManagement.pagination.next.hidden")),
        attributes = Map.empty
      ))
      sut.generateNextLink(5, 6) mustEqual expectedLink
    }

  }

  "generatePreviousLink" should {

    "return None where we're on the first page" in {
      sut.generatePreviousLink(1, 1) mustEqual None
      sut.generatePreviousLink(1, 2) mustEqual None
      sut.generatePreviousLink(1, 3) mustEqual None
      sut.generatePreviousLink(1, 10) mustEqual None
    }

    "return a Previous link where we are on the second page of three" in {
      val expectedLink = Some(PaginationLink(
        href = controllers.routes.CaseManagementController.onPageLoad(1).url,
        text = Some(messages("caseManagement.pagination.previous")),
        labelText = Some(messages("caseManagement.pagination.previous.hidden")),
        attributes = Map.empty
      ))
      sut.generatePreviousLink(2, 3) mustEqual expectedLink
    }

    "return a Previous link where we are on the final page of three" in {
      val expectedLink = Some(PaginationLink(
        href = controllers.routes.CaseManagementController.onPageLoad(2).url,
        text = Some(messages("caseManagement.pagination.previous")),
        labelText = Some(messages("caseManagement.pagination.previous.hidden")),
        attributes = Map.empty
      ))
      sut.generatePreviousLink(3, 3) mustEqual expectedLink
    }

    "return a Previous link where we are many pages in" in {
      val expectedLink = Some(PaginationLink(
        href = controllers.routes.CaseManagementController.onPageLoad(4).url,
        text = Some(messages("caseManagement.pagination.previous")),
        labelText = Some(messages("caseManagement.pagination.previous.hidden")),
        attributes = Map.empty
      ))
      sut.generatePreviousLink(5, 6) mustEqual expectedLink
    }

  }

  "generatePaginationItems" should {

    def createItem(index: Int, isCurrent: Boolean) = PaginationItem(
      href = controllers.routes.CaseManagementController.onPageLoad(index).url,
      number = Some(index.toString),
      visuallyHiddenText = None,
      current = Some(isCurrent),
      ellipsis = None,
      attributes = Map.empty
    )

    "generate numbers from 1 to the total number of pages, highlighting the current page - for 1 page" in {
      sut.generatePaginationItems(1, 1) mustEqual Seq(createItem(1, true))
    }

    "generate numbers from 1 to the total number of pages, highlighting the current page - for 3 pages" in {
      val expectedItems = Seq(
        createItem(1, false),
        createItem(2, true),
        createItem(3, false)
      )
      sut.generatePaginationItems(2, 3) mustEqual expectedItems
    }

    "generate numbers from 1 to the total number of pages, highlighting the current page - for 6 pages" in {
      val expectedItems = Seq(
        createItem(1, false),
        createItem(2, false),
        createItem(3, false),
        createItem(4, true),
        createItem(5, false),
        createItem(6, false)
      )
      sut.generatePaginationItems(4, 6) mustEqual expectedItems
    }

    "generate numbers from 1 to the total number of pages, highlighting the current page - for 10 pages" in {
      val expectedItems = Seq(
        createItem(1, false),
        createItem(2, false),
        createItem(3, false),
        createItem(4, false),
        createItem(5, false),
        createItem(6, false),        
        createItem(7, false),
        createItem(8, false),
        createItem(9, true),
        createItem(10, false)
      )
      sut.generatePaginationItems(9, 10) mustEqual expectedItems
    }

  }

  "generatePagination" should {

    "return None where there is only 1 page" in {
      sut.generatePagination(1, 1) mustEqual None
    }

    "return Some where there is more than 1 page" in {
      sut.generatePagination(2, 5).isDefined mustEqual true
    }

    "return Some where there is many pages" in {
      sut.generatePagination(10, 10).isDefined mustEqual true
    }  

  }

}
