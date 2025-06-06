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
import forms.DoYouHaveNationalInsuranceNumberFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.notification.DoYouHaveNationalInsuranceNumberView
import models.NormalMode

class DoYouHaveNationalInsuranceNumberViewSpec extends ViewSpecBase with ViewMatchers {

  val form                                       = new DoYouHaveNationalInsuranceNumberFormProvider()()
  val page: DoYouHaveNationalInsuranceNumberView = inject[DoYouHaveNationalInsuranceNumberView]

  private def createView: Html = page(form, NormalMode, false)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("doYouHaveNationalInsuranceNumber.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-fieldset__heading").text() mustBe messages(
        "doYouHaveNationalInsuranceNumber.heading"
      )
    }

    "have Yes, and I know my National Insurance number" in {
      view.getElementsByClass("govuk-radios__label").get(0).text() mustBe messages(
        "doYouHaveNationalInsuranceNumber.yesIKnow"
      )
    }

    "have Yes, but I do not know my National Insurance number" in {
      view.getElementsByClass("govuk-radios__label").get(1).text() mustBe messages(
        "doYouHaveNationalInsuranceNumber.yesButDontKnow"
      )
    }

    "have No" in {
      view.getElementsByClass("govuk-radios__label").get(2).text() mustBe messages(
        "doYouHaveNationalInsuranceNumber.no"
      )
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

  }

}
