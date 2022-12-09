/*
 * Copyright 2022 HM Revenue & Customs
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

class TaskListControllerSpec extends SpecBase with MockitoSugar {

  "TaskList Controller" - {

    "must return OK and the correct view for a GET when userAnswers is empty" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.TaskListController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TaskListView]

        implicit val mess = messages(application)

        val personalDetailsTask = Seq(buildCaseReferenceRow()(mess))
        val liabilitiesInformation = Seq.empty
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
  }

  private def buildCaseReferenceRow()(implicit messages: Messages): TaskListRow = {
    TaskListRow(
      id = "personal-detail-task-list", 
      operation = messages("taskList.op.add"),
      sectionTitle = messages("taskList.sectionTitle.first"), 
      status = messages("taskList.status.notStarted"), 
      link = routes.TaskListController.onPageLoad
    )
  }

  private def buildTheReasonForComingForwardNowRow()(implicit messages: Messages): TaskListRow = {
    TaskListRow(
      id = "reason-for-coming-forward-now-liabilitie-task-list", 
      operation = messages("taskList.op.add"),
      sectionTitle = messages("taskList.sectionTitle.forth"), 
      status = messages("taskList.status.notStarted"), 
      link = routes.TaskListController.onPageLoad
    )
  }
}
