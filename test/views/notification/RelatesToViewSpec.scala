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

package views.notification

import base.ViewSpecBase
import forms.RelatesToFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.notification.RelatesToView
import models.NormalMode

class RelatesToViewSpec extends ViewSpecBase with ViewMatchers {

  val form                = new RelatesToFormProvider()()
  val page: RelatesToView = inject[RelatesToView]

  private def createView: Html = page(form, NormalMode, false)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("relatesTo.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-fieldset__heading").text() mustBe messages("relatesTo.heading")
    }

    "have An individual as an option" in {
      view.getElementsByClass("govuk-radios__label").get(0).text() mustBe messages("relatesTo.individual")
    }

    "have hint text for individual option" in {
      view.select("div#value_0-item-hint").text() must include(messages("relatesTo.hint.individual"))
    }

    "have An estate as an option" in {
      view.getElementsByClass("govuk-radios__label").get(1).text() mustBe messages("relatesTo.estate")
    }

    "have hint text for estate option" in {
      view.select("div#value_1-item-hint").text() must include(messages("relatesTo.hint.estate"))
    }

    "have A company as an option" in {
      view.getElementsByClass("govuk-radios__label").get(2).text() mustBe messages("relatesTo.company")
    }

    "have hint text for company option" in {
      view.select("div#value_2-item-hint").text() must include(messages("relatesTo.hint.company"))
    }

    "have A limited liability partnership as an option" in {
      view.getElementsByClass("govuk-radios__label").get(3).text() mustBe messages("relatesTo.llp")
    }

    "have hint text for llp option" in {
      view.select("div#value_3-item-hint").text() must include(messages("relatesTo.hint.llp"))
    }

    "have A trust as an option" in {
      view.getElementsByClass("govuk-radios__label").get(4).text() mustBe messages("relatesTo.trust")
    }

    "have hint text for trust option" in {
      view.select("div#value_4-item-hint").text() must include(messages("relatesTo.hint.trust"))
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

  }

}
