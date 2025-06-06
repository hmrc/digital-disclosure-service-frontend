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

package views.offshore

import base.ViewSpecBase
import forms.YouHaveNotSelectedCertainTaxYearFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.offshore.YouHaveNotSelectedCertainTaxYearView
import models.{NormalMode, TaxYearStarting}

class YouHaveNotSelectedCertainTaxYearViewSpec extends ViewSpecBase with ViewMatchers {

  val form                                       = new YouHaveNotSelectedCertainTaxYearFormProvider()()
  val page: YouHaveNotSelectedCertainTaxYearView = inject[YouHaveNotSelectedCertainTaxYearView]

  val selectedYears: List[TaxYearStarting]    = List(TaxYearStarting(2021), TaxYearStarting(2019), TaxYearStarting(2017))
  val notSelectedYears: List[TaxYearStarting] = List(TaxYearStarting(2020), TaxYearStarting(2018))

  private def createView: Html = page(form, NormalMode, selectedYears, notSelectedYears)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("youHaveNotSelectedCertainTaxYear.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("youHaveNotSelectedCertainTaxYear.heading")
    }

    "contain selected certain years body" in {
      view.getElementById("selected-body").text() mustBe messages("youHaveNotSelectedCertainTaxYear.selected.body")
    }

    "contain not selected certain years body" in {
      view.getElementById("not-selected-body").text() mustBe messages(
        "youHaveNotSelectedCertainTaxYear.notSelected.body"
      )
    }

    "contain label" in {
      view.getElementsByClass("govuk-label").text() mustBe messages("youHaveNotSelectedCertainTaxYear.label")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

    "have a task list link" in {
      view.getElementById("task-list-link").attr("href") mustBe controllers.routes.TaskListController.onPageLoad.url
    }

  }

}
