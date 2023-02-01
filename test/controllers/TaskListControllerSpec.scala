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

package controllers

import base.SpecBase
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.TaskListView
import viewmodels.{TaskListRow, TaskListViewModel}
import play.api.i18n.Messages
import pages._
import models._

class TaskListControllerSpec extends SpecBase with MockitoSugar {

  "TaskList Controller" - {

    "must return OK and the correct view for a GET when userAnswers is empty" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.TaskListController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TaskListView]

        implicit val mess = messages(application)

        val isSectionComplete = false
        val operationKey = "add"
        val entityKey = "individual"
        val entity = RelatesTo.AnIndividual
        val isTheUserAgent = true
        val isAllTaskCompleted = false
        val notificationSectionKey = s"taskList.$entityKey.$operationKey.heading.first"
        val notificationTitleKey = s"taskList.$entityKey.$operationKey.sectionTitle.first"

        val personalDetailsTask = Seq(buildYourPersonalDetailsRow(notificationTitleKey, isSectionComplete)(mess))
        val liabilitiesInformation = buildLiabilitiesInformationRow(UserAnswers("id"))(mess)
        val additionalInformation = Seq(buildOtherLiabilityIssueRow(UserAnswers("id"))(mess), buildTheReasonForComingForwardNowRow(UserAnswers("id"))(mess))
        val list = TaskListViewModel(personalDetailsTask, liabilitiesInformation, additionalInformation)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(list, notificationSectionKey, isTheUserAgent, entity, isAllTaskCompleted)(request, messages(application)).toString
      }
    }

    "must redirect to Index for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, routes.TaskListController.onPageLoad.url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

    def rowIsDisplayedWhenPageIsPopulated(ua: UserAnswers) = {
      val application = applicationBuilder(userAnswers = Some(ua)).build()

      running(application) {
        val request = FakeRequest(GET, routes.TaskListController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TaskListView]

        implicit val mess = messages(application)

        val isSectionComplete = false
        val operationKey = "add"
        val entityKey = "agent"
        val entity = RelatesTo.AnIndividual
        val isTheUserAgent = true
        val isAllTaskCompleted = false
        val notificationSectionKey = s"taskList.$entityKey.$operationKey.heading.first"
        val notificationTitleKey = s"taskList.$entityKey.$operationKey.sectionTitle.first"

        val personalDetailsTask = Seq(buildYourPersonalDetailsRow(notificationTitleKey, isSectionComplete)(mess))
        val liabilitiesInformation = buildLiabilitiesInformationRow(ua)(mess)
        val additionalInformation = Seq(buildOtherLiabilityIssueRow(ua)(mess), buildTheReasonForComingForwardNowRow(ua)(mess))
        val list = TaskListViewModel(personalDetailsTask, liabilitiesInformation, additionalInformation)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(list, notificationSectionKey, isTheUserAgent, entity, isAllTaskCompleted)(request, messages(application)).toString
      }
    }

    "must return the correct view when TaskListPage is populated with both liabilities information" in {
      val userAnswer = (for {
        ua <- UserAnswers("id").set(OffshoreLiabilitiesPage, true)
        ua1 <- ua.set(OnshoreLiabilitiesPage, true)
        ua2 <- ua1.set(AreYouTheIndividualPage, true)
      } yield ua2).success.value
      rowIsDisplayedWhenPageIsPopulated(userAnswer)
    }

    "must return the correct view when TaskListPage is populated with onshore liabilities row" in {
      val userAnswer = (for {
        ua <- UserAnswers("id").set(OffshoreLiabilitiesPage, false)
        ua1 <- ua.set(OnshoreLiabilitiesPage, true)
        ua2 <- ua1.set(AreYouTheIndividualPage, true)
      } yield ua2).success.value
      rowIsDisplayedWhenPageIsPopulated(userAnswer)
    }

    "must return the correct view when TaskListPage is populated with offshore liabilities row" in {
      val userAnswer = (for {
        ua <- UserAnswers("id").set(OffshoreLiabilitiesPage, true)
        ua1 <- ua.set(OnshoreLiabilitiesPage, false)
        ua2 <- ua1.set(AreYouTheIndividualPage, true)
      } yield ua2).success.value
      rowIsDisplayedWhenPageIsPopulated(userAnswer)
    }

    "must return the correct view when TaskListPage is populated with no liabilities information row" in {
      val userAnswer = UserAnswers("id").set(AreYouTheIndividualPage, true).success.value
      rowIsDisplayedWhenPageIsPopulated(userAnswer)
    }
  }

  private def buildYourPersonalDetailsRow(notificationTitleKey: String, isSectionComplete: Boolean)(implicit messages: Messages): TaskListRow = {

    val link = if (isSectionComplete) controllers.notification.routes.CheckYourAnswersController.onPageLoad
      else controllers.notification.routes.ReceivedALetterController.onPageLoad(NormalMode)

    TaskListRow(
      id = "personal-detail-task-list", 
      sectionTitle = messages(notificationTitleKey), 
      status = messages("taskList.status.notStarted"), 
      link = link
    )
  }

  private def buildCaseReferenceRow(userAnswer: UserAnswers)(implicit messages: Messages): TaskListRow = {

    val operationKey = "add"
    val caseReferenceTitleKey = s"taskList.$operationKey.sectionTitle.second"

    TaskListRow(
      id = "case-reference-task-list", 
      sectionTitle = messages(caseReferenceTitleKey), 
      status = messages("taskList.status.notStarted"), 
      link = controllers.reference.routes.DoYouHaveACaseReferenceController.onPageLoad(NormalMode)
    )
  }

  private def buildOnshoreLiabilitieDetailRow()(implicit messages: Messages): TaskListRow = {
    TaskListRow(
      id = "onshore-liabilitie-task-list", 
      sectionTitle = messages("taskList.sectionTitle.third"), 
      status = messages("taskList.status.notStarted"), 
      link = routes.TaskListController.onPageLoad
    )
  }

  private def buildOffshoreLiabilitieDetailRow(userAnswers: UserAnswers)(implicit messages: Messages): TaskListRow = {
    TaskListRow(
      id = "offshore-liabilitie-task-list", 
      sectionTitle = messages("taskList.sectionTitle.forth"), 
      status = messages("taskList.status.notStarted"), 
      link = offshore.routes.WhyAreYouMakingThisDisclosureController.onPageLoad(NormalMode)
    )
  }

  private def buildOtherLiabilityIssueRow(userAnswers: UserAnswers)(implicit messages: Messages): TaskListRow = {
    TaskListRow(
      id = "other-liability-issue-task-list", 
      sectionTitle = messages("taskList.sectionTitle.fifth"), 
      status = messages("taskList.status.notStarted"), 
      link = otherLiabilities.routes.OtherLiabilityIssuesController.onPageLoad(NormalMode)
    )
  }

  private def buildTheReasonForComingForwardNowRow(userAnswers: UserAnswers)(implicit messages: Messages): TaskListRow = {

    val operationKey = "add"
    val reasonForComingForwardNowTitleKey = s"taskList.$operationKey.sectionTitle.sixth"

    TaskListRow(
      id = "reason-for-coming-forward-now-liabilitie-task-list", 
      sectionTitle = messages(reasonForComingForwardNowTitleKey), 
      status = messages("taskList.status.notStarted"),
      link = reason.routes.WhyAreYouMakingADisclosureController.onPageLoad(NormalMode)
    )
  }

  private def buildLiabilitiesInformationRow(userAnswers: UserAnswers)(implicit messages: Messages): Seq[TaskListRow] = {
    val offshore = userAnswers.get(OffshoreLiabilitiesPage) 
    val onshore = userAnswers.get(OnshoreLiabilitiesPage)

    (offshore, onshore) match {
      case (Some(true),  Some(true))  => Seq(buildCaseReferenceRow(userAnswers), buildOnshoreLiabilitieDetailRow, buildOffshoreLiabilitieDetailRow(userAnswers))
      case (Some(true),  _)           => Seq(buildCaseReferenceRow(userAnswers), buildOffshoreLiabilitieDetailRow(userAnswers))
      case (_,           Some(true))  => Seq(buildCaseReferenceRow(userAnswers), buildOnshoreLiabilitieDetailRow)
      case (Some(false), _)           => Seq(buildCaseReferenceRow(userAnswers), buildOnshoreLiabilitieDetailRow)
      case (_,           _)           => Seq(buildCaseReferenceRow(userAnswers))
    }
  }

}