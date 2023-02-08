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

package views

import controllers._
import base.ViewSpecBase
import play.twirl.api.Html
import support.ViewMatchers
import views.html.TaskListView
import viewmodels.{TaskListRow, TaskListViewModel}
import models.RelatesTo
import org.scalacheck.Arbitrary.arbitrary
import generators.Generators

class TaskListViewSpec extends ViewSpecBase with ViewMatchers with Generators {

  val page: TaskListView = inject[TaskListView]

  val testRow1 = TaskListRow(
    id = "task-list", 
    sectionTitle = "Section Title", 
    status = "Not Started", 
    link = routes.TaskListController.onPageLoad
  )

  val testRow2 = TaskListRow(
    id = "task-list", 
    sectionTitle = "Section Title", 
    status = "Not Started", 
    link = routes.TaskListController.onPageLoad
  )

  val testRow3 = TaskListRow(
    id = "task-list", 
    sectionTitle = "Section Title", 
    status = "Not Started", 
    link = routes.TaskListController.onPageLoad
  )

  "view with incomplete passed" should {

    val operationKey = "add"
    val entityKey = "agent"
    val notificationSectionKey = s"taskList.$entityKey.$operationKey.heading.first"

    val personalDetailsTask = Seq(testRow1) 
    val liabilitiesInformation = Seq(testRow2)
    val additionalInformation = Seq(testRow3)
    val list = TaskListViewModel(personalDetailsTask, liabilitiesInformation, additionalInformation)
    
    val entity = arbitrary[RelatesTo].sample.value.toString
    val isTheUserAgent = arbitrary[Boolean].sample.value
    
    def createView: Html = page(list, notificationSectionKey, isTheUserAgent, entity, false, 0)(request, messages)

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("taskList.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-l").text() mustBe messages("taskList.heading")
    }

    "have a first paragraph" in {
      view.getElementById("first-paragraph").text() mustBe messages("taskList.paragraph.first.incomplete")
    }

    "have a second paragraph" in {
      view.getElementById("second-paragraph").text() mustBe messages("taskList.paragraph.second", 0)
    }

    "have a third paragraph" in {
      view.getElementById("third-paragraph").text() mustBe messages("taskList.paragraph.third")
    }

    "contain list of section" in {
      view.getElementsByClass("app-task-list__section").get(0).text() mustBe "1. " + messages(notificationSectionKey)
      view.getElementsByClass("app-task-list__section").get(1).text() mustBe "2. " + messages("taskList.heading.second")
      view.getElementsByClass("app-task-list__section").get(2).text() mustBe "3. " + messages("taskList.heading.third")
    }

    "have a view details link" in {
      view.getElementById("link-view-a-copy").attr("href") mustBe controllers.routes.PdfGenerationController.generate.url
    }

  }

  "view with completed passed" should {

    val operationKey = "add"
    val entityKey = "agent"
    val notificationSectionKey = s"taskList.$entityKey.$operationKey.heading.first"

    val personalDetailsTask = Seq(testRow1) 
    val liabilitiesInformation = Seq(testRow2)
    val additionalInformation = Seq(testRow3)
    val list = TaskListViewModel(personalDetailsTask, liabilitiesInformation, additionalInformation)
    
    val entity = arbitrary[RelatesTo].sample.value.toString
    val isTheUserAgent = arbitrary[Boolean].sample.value
    
    def createView: Html = page(list, notificationSectionKey, isTheUserAgent, entity, true, 3)(request, messages)

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("taskList.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-l").text() mustBe messages("taskList.heading")
    }

    "have a first paragraph" in {
      view.getElementById("first-paragraph").text() mustBe messages("taskList.paragraph.first.complete")
    }

    "have a second paragraph" in {
      view.getElementById("second-paragraph").text() mustBe messages("taskList.paragraph.second", 3)
    }

    "have a third paragraph" in {
      view.getElementById("third-paragraph").text() mustBe messages("taskList.paragraph.third")
    }

    "contain list of section" in {
      view.getElementsByClass("app-task-list__section").get(0).text() mustBe "1. " + messages(notificationSectionKey)
      view.getElementsByClass("app-task-list__section").get(1).text() mustBe "2. " + messages("taskList.heading.second")
      view.getElementsByClass("app-task-list__section").get(2).text() mustBe "3. " + messages("taskList.heading.third")
    }

    "have a view details link" in {
      view.getElementById("link-view-a-copy").attr("href") mustBe controllers.routes.PdfGenerationController.generate.url
    }

    "contain the sub heading" in {
      view.getElementsByClass("govuk-heading-m").text() mustBe messages("taskList.subheading")
    }

    s"have a forth paragraph when $isTheUserAgent & $entity" in {
      if(isTheUserAgent){
        view.getElementById("forth-paragraph").text() mustBe messages("taskList.agent.paragraph.forth")
      } else {
        view.getElementById("forth-paragraph").text() mustBe messages(s"taskList.${entity}.paragraph.forth")
      }
    }

    "have a fifth paragraph" in {
      view.getElementById("fifth-paragraph").text() mustBe messages("taskList.paragraph.fifth") + messages("taskList.paragraph.fifth.link") + messages("site.dot")
    }

  }

}