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

  val testRow2 = TaskListRow(
    id = "task-list", 
    operation = "Add",
    sectionTitle = "Section Title", 
    status = "Not Started", 
    link = routes.TaskListController.onPageLoad
  )

  val testRow3 = TaskListRow(
    id = "task-list", 
    operation = "Add",
    sectionTitle = "Section Title", 
    status = "Not Started", 
    link = routes.TaskListController.onPageLoad
  )

  "view" should {

    val personalDetailsTask = Seq(testRow1) 
    val liabilitiesInformation = Seq(testRow2)
    val additionalInformation = Seq(testRow3)
    val list = TaskListViewModel(personalDetailsTask, liabilitiesInformation, additionalInformation)
    
    def createView: Html = page(list)(request, messages)

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("taskList.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-l").text() mustBe messages("taskList.heading")
    }

    "have a first paragraph" in {
      view.getElementById("first-paragraph").text() mustBe messages("taskList.paragraph.first")
    }

    "have a second paragraph" in {
      view.getElementById("second-paragraph").text() mustBe messages("taskList.paragraph.second")
    }

    "have a third paragraph" in {
      view.getElementById("third-paragraph").text() mustBe messages("taskList.paragraph.third")
    }

    "contain list of section" in {
      view.getElementsByClass("app-task-list__section").get(0).text() mustBe "1. " + messages("taskList.heading.first")
      view.getElementsByClass("app-task-list__section").get(1).text() mustBe "2. " + messages("taskList.heading.second")
      view.getElementsByClass("app-task-list__section").get(2).text() mustBe "3. " + messages("taskList.heading.third")
    }

  }

}