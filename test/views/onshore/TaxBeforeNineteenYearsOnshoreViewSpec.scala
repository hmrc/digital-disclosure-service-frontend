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
import forms.TaxBeforeNineteenYearsOnshoreFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.onshore.TaxBeforeNineteenYearsOnshoreView
import models.NormalMode

class TaxBeforeNineteenYearsOnshoreViewSpec extends ViewSpecBase with ViewMatchers {

  val year = "2023"
  val form = new TaxBeforeNineteenYearsOnshoreFormProvider()(year)
  val page: TaxBeforeNineteenYearsOnshoreView = inject[TaxBeforeNineteenYearsOnshoreView]
  private def createView: Html = page(form, NormalMode, year)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("taxBeforeNineteenYears.title", year))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("taxBeforeNineteenYears.heading", year)
    }

    "contain body" in {
      view.getElementsByClass("govuk-label").text() mustBe messages("taxBeforeNineteenYears.body")
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