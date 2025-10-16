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

package views.onshore

import base.ViewSpecBase
import forms.NotIncludedSingleTaxYearFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.onshore.NotIncludedSingleTaxYearView
import models.NormalMode

class NotIncludedSingleTaxYearViewSpec extends ViewSpecBase with ViewMatchers {

  val missingYear                        = "2020"
  val firstYear                          = "2019"
  val lastYear                           = "2021"
  val form                               = new NotIncludedSingleTaxYearFormProvider()(missingYear)
  val page: NotIncludedSingleTaxYearView = inject[NotIncludedSingleTaxYearView]

  private def createView: Html = page(form, NormalMode, missingYear, firstYear, lastYear)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("youHaveNotIncludedTheTaxYear.title", missingYear))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages(
        "youHaveNotIncludedTheTaxYear.heading",
        missingYear
      )
    }

    "contain body" in {
      view.getElementById("body").text() mustBe messages(
        "youHaveNotIncludedTheTaxYear.body",
        firstYear,
        lastYear,
        missingYear
      )
    }

    "contain label" in {
      view.getElementsByClass("govuk-label").text() mustBe messages("youHaveNotIncludedTheTaxYear.label")
    }

    "display the continue button" in {
      view.getElementById("continue").text() mustBe messages("site.saveAndContinue")
    }

    "have a task list link" in {
      view.getElementById("task-list-link").attr("href") mustBe controllers.routes.TaskListController.onPageLoad.url
    }

  }

}
