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

package views.notification

import base.ViewSpecBase
import forms.AreYouRegisteredForVATFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.notification.AreYouRegisteredForVATView
import models.NormalMode

class AreYouRegisteredForVATViewSpec extends ViewSpecBase with ViewMatchers {

  val form = new AreYouRegisteredForVATFormProvider()()
  val page: AreYouRegisteredForVATView = inject[AreYouRegisteredForVATView]

  private def createView: Html = page(form, NormalMode)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("areYouRegisteredForVAT.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-fieldset__heading").text() mustBe messages("areYouRegisteredForVAT.heading")
    }

    "have Yes, and I know my VAT number" in {
      view.getElementsByClass("govuk-radios__label").get(0).text() mustBe messages("areYouRegisteredForVAT.yesIKnow")
    }

    "have Yes, but I do not know my VAT number" in {
      view.getElementsByClass("govuk-radios__label").get(1).text() mustBe messages("areYouRegisteredForVAT.yesButDontKnow")
    }

    "have No" in {
      view.getElementsByClass("govuk-radios__label").get(2).text() mustBe messages("areYouRegisteredForVAT.no")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

  }

}