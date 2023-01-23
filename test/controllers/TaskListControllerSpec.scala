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

        val personalDetailsTask = Seq(buildYourPersonalDetailsRow()(mess))
        val liabilitiesInformation = buildLiabilitiesInformationRow(UserAnswers("id"))(mess)
        val additionalInformation = Seq(buildTheReasonForComingForwardNowRow()(mess))
        val list = TaskListViewModel(personalDetailsTask, liabilitiesInformation, additionalInformation)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(list)(request, messages(application)).toString
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

        val personalDetailsTask = Seq(buildYourPersonalDetailsRow()(mess))
        val liabilitiesInformation = buildLiabilitiesInformationRow(ua)(mess)
        val additionalInformation = Seq(buildTheReasonForComingForwardNowRow()(mess))
        val list = TaskListViewModel(personalDetailsTask, liabilitiesInformation, additionalInformation)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(list)(request, messages(application)).toString
      }
    }

    "must return the correct view when TaskListPage is populated with both liabilities information" in {
      val userAnswer = (for {
        ua <- UserAnswers("id").set(OffshoreLiabilitiesPage, true)
        updatedUa <- ua.set(OnshoreLiabilitiesPage, true)
      } yield updatedUa).success.value
      rowIsDisplayedWhenPageIsPopulated(userAnswer)
    }

    "must return the correct view when TaskListPage is populated with onshore liabilities row" in {
      val userAnswer = (for {
        ua <- UserAnswers("id").set(OffshoreLiabilitiesPage, false)
        updatedUa <- ua.set(OnshoreLiabilitiesPage, true)
      } yield updatedUa).success.value
      rowIsDisplayedWhenPageIsPopulated(userAnswer)
    }

    "must return the correct view when TaskListPage is populated with offshore liabilities row" in {
      val userAnswer = (for {
        ua <- UserAnswers("id").set(OffshoreLiabilitiesPage, true)
        updatedUa <- ua.set(OnshoreLiabilitiesPage, false)
      } yield updatedUa).success.value
      rowIsDisplayedWhenPageIsPopulated(userAnswer)
    }

    "must return the correct view when TaskListPage is populated with no liabilities information row" in {
      val userAnswer = UserAnswers("id")
      rowIsDisplayedWhenPageIsPopulated(userAnswer)
    }
  }

  private def buildYourPersonalDetailsRow()(implicit messages: Messages): TaskListRow = {
    TaskListRow(
      id = "personal-detail-task-list", 
      operation = messages("taskList.op.add"),
      sectionTitle = messages("taskList.sectionTitle.first"), 
      status = messages("taskList.status.notStarted"), 
      link = controllers.notification.routes.ReceivedALetterController.onPageLoad(NormalMode)
    )
  }

  private def buildCaseReferenceRow()(implicit messages: Messages): TaskListRow = {
    TaskListRow(
      id = "case-reference-task-list", 
      operation = messages("taskList.op.add"),
      sectionTitle = messages("taskList.sectionTitle.second"), 
      status = messages("taskList.status.notStarted"), 
      link = routes.TaskListController.onPageLoad
    )
  }

  private def buildOnshoreLiabilitieDetailRow()(implicit messages: Messages): TaskListRow = {
    TaskListRow(
      id = "onshore-liabilitie-task-list", 
      operation = messages("taskList.op.add"),
      sectionTitle = messages("taskList.sectionTitle.third"), 
      status = messages("taskList.status.notStarted"), 
      link = routes.TaskListController.onPageLoad
    )
  }

  private def buildOffshoreLiabilitieDetailRow(userAnswers: UserAnswers)(implicit messages: Messages): TaskListRow = {
    TaskListRow(
      id = "offshore-liabilitie-task-list", 
      operation = messages("taskList.op.add"),
      sectionTitle = messages("taskList.sectionTitle.forth"), 
      status = messages("taskList.status.notStarted"), 
      link = offshore.routes.WhyAreYouMakingThisDisclosureController.onPageLoad(NormalMode)
    )
  }

  private def buildOtherLiabilityIssueRow()(implicit messages: Messages): TaskListRow = {
    TaskListRow(
      id = "other-liability-issue-task-list", 
      operation = messages("taskList.op.add"),
      sectionTitle = messages("taskList.sectionTitle.fifth"), 
      status = messages("taskList.status.notStarted"), 
      link = otherLiabilities.routes.OtherLiabilityIssuesController.onPageLoad(NormalMode)
    )
  }

  private def buildTheReasonForComingForwardNowRow()(implicit messages: Messages): TaskListRow = {
    TaskListRow(
      id = "reason-for-coming-forward-now-liabilitie-task-list", 
      operation = messages("taskList.op.add"),
      sectionTitle = messages("taskList.sectionTitle.sixth"), 
      status = messages("taskList.status.notStarted"), 
      link = routes.TaskListController.onPageLoad
    )
  }

  private def buildLiabilitiesInformationRow(userAnswers: UserAnswers)(implicit messages: Messages): Seq[TaskListRow] = {
    val offshore = userAnswers.get(OffshoreLiabilitiesPage) 
    val onshore = userAnswers.get(OnshoreLiabilitiesPage)

    (offshore, onshore) match {
      case (Some(true),  Some(true))  => Seq(buildCaseReferenceRow, buildOnshoreLiabilitieDetailRow, buildOffshoreLiabilitieDetailRow(userAnswers), buildOtherLiabilityIssueRow)
      case (Some(true),  _)           => Seq(buildCaseReferenceRow, buildOffshoreLiabilitieDetailRow(userAnswers), buildOtherLiabilityIssueRow)
      case (_,           Some(true))  => Seq(buildCaseReferenceRow, buildOnshoreLiabilitieDetailRow, buildOtherLiabilityIssueRow)
      case (Some(false), _)           => Seq(buildCaseReferenceRow, buildOnshoreLiabilitieDetailRow, buildOtherLiabilityIssueRow)
      case (_,           _)           => Seq(buildCaseReferenceRow, buildOtherLiabilityIssueRow)
    }
  }

}