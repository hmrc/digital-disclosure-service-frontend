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

import base.ViewSpecBase
import play.twirl.api.Html
import support.ViewMatchers
import views.html.MakingNilDisclosureView
import models.RelatesTo

class MakingNilDisclosureViewSpec extends ViewSpecBase with ViewMatchers {

  val page: MakingNilDisclosureView = inject[MakingNilDisclosureView]

  "view with 20 years & agent" should {

    val areTheyTheIndividual = true
    val entity = RelatesTo.AnIndividual
    val years = 20

    def createView: Html = page(areTheyTheIndividual, entity, years)(request, messages)
    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("makingNilDisclosure.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("makingNilDisclosure.heading")
    }

    "contain body" in {
      view.getElementById("first-paragraph").text() mustBe messages("makingNilDisclosure.you.body.first", years)
      view.getElementById("second-paragraph").text() mustBe messages("makingNilDisclosure.body.second")
      view.getElementById("third-paragraph").text() mustBe messages("makingNilDisclosure.body.third")
    }

    "display the button" in {
      view.getElementsByClass("govuk-button").text() mustBe messages("makingNilDisclosure.button")
      view.getElementById("govuk-button").attr("href") mustBe controllers.routes.TaskListController.onPageLoad.url
    }

  }

  "view with 20 years & entity individual" should {

    val areTheyTheIndividual = false
    val entity = RelatesTo.AnIndividual
    val years = 20

    def createView: Html = page(areTheyTheIndividual, entity, years)(request, messages)
    val view = createView

    "contain body" in {
      view.getElementById("first-paragraph").text() mustBe messages(s"makingNilDisclosure.${entity}.body.first", years)
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

    "have a task list link" in {
      view.getElementById("task-list-link").attr("href") mustBe controllers.routes.TaskListController.onPageLoad.url
    }
  }

  "view with 20 years & entity estate" should {

    val areTheyTheIndividual = false
    val entity = RelatesTo.AnEstate
    val years = 20

    def createView: Html = page(areTheyTheIndividual, entity, years)(request, messages)
    val view = createView

    "contain body" in {
      view.getElementById("first-paragraph").text() mustBe messages(s"makingNilDisclosure.${entity}.body.first", years)
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

    "have a task list link" in {
      view.getElementById("task-list-link").attr("href") mustBe controllers.routes.TaskListController.onPageLoad.url
    }
  }

  "view with 20 years & entity company" should {

    val areTheyTheIndividual = false
    val entity = RelatesTo.ACompany
    val years = 20

    def createView: Html = page(areTheyTheIndividual, entity, years)(request, messages)
    val view = createView

    "contain body" in {
      view.getElementById("first-paragraph").text() mustBe messages(s"makingNilDisclosure.${entity}.body.first", years)
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

    "have a task list link" in {
      view.getElementById("task-list-link").attr("href") mustBe controllers.routes.TaskListController.onPageLoad.url
    }
  }

  "view with 20 years & entity llp" should {

    val areTheyTheIndividual = false
    val entity = RelatesTo.ALimitedLiabilityPartnership
    val years = 20

    def createView: Html = page(areTheyTheIndividual, entity, years)(request, messages)
    val view = createView

    "contain body" in {
      view.getElementById("first-paragraph").text() mustBe messages(s"makingNilDisclosure.${entity}.body.first", years)
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

    "have a task list link" in {
      view.getElementById("task-list-link").attr("href") mustBe controllers.routes.TaskListController.onPageLoad.url
    }
  }

  "view with 20 years & entity trust" should {

    val areTheyTheIndividual = false
    val entity = RelatesTo.ATrust
    val years = 20

    def createView: Html = page(areTheyTheIndividual, entity, years)(request, messages)
    val view = createView

    "contain body" in {
      view.getElementById("first-paragraph").text() mustBe messages(s"makingNilDisclosure.${entity}.body.first", years)
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

    "have a task list link" in {
      view.getElementById("task-list-link").attr("href") mustBe controllers.routes.TaskListController.onPageLoad.url
    }
  }

}