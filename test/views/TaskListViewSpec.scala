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

package views

import controllers._
import base.ViewSpecBase
import play.twirl.api.Html
import support.ViewMatchers
import views.html.TaskListView
import viewmodels.{TaskListRow, TaskListViewModel}

class TaskListViewSpec extends ViewSpecBase with ViewMatchers {

  val page: TaskListView = inject[TaskListView]

  val testRow1 = TaskListRow(
    id = "task-list", 
    operation = "Add",
    sectionTitle = "Section Title", 
    status = "Not Started", 
    link = routes.TaskListController.onPageLoad
  )

  val personalDetailsTask = Seq(testRow1) 
  val liabilitiesInformation = Seq(testRow1)
  val additionalInformation = Seq(testRow1)
  val list = TaskListViewModel(personalDetailsTask, liabilitiesInformation, additionalInformation)
  private def createView: Html = page(list)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("taskList.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-label--xl").text() mustBe messages("taskList.heading")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

  }

}