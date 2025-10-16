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
import forms.DoesTheIndividualHaveNationalInsuranceNumberFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.notification.DoesTheIndividualHaveNationalInsuranceNumberView
import models.NormalMode

class DoesTheIndividualHaveNationalInsuranceNumberViewSpec extends ViewSpecBase with ViewMatchers {

  val form                                                   = new DoesTheIndividualHaveNationalInsuranceNumberFormProvider()()
  val page: DoesTheIndividualHaveNationalInsuranceNumberView = inject[DoesTheIndividualHaveNationalInsuranceNumberView]

  private def createView: Html = page(form, NormalMode, false)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("doesTheIndividualHaveNINO.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-fieldset__heading").text() mustBe messages("doesTheIndividualHaveNINO.heading")
    }

    "have Yes, and I know their National Insurance number" in {
      view.getElementsByClass("govuk-radios__label").get(0).text() mustBe messages("doesTheIndividualHaveNINO.yesIKnow")
    }

    "have Yes, but I do not know their National Insurance number" in {
      view.getElementsByClass("govuk-radios__label").get(1).text() mustBe messages(
        "doesTheIndividualHaveNINO.yesButDontKnow"
      )
    }

    "have No" in {
      view.getElementsByClass("govuk-radios__label").get(2).text() mustBe messages("doesTheIndividualHaveNINO.no")
    }

    "display the continue button" in {
      view.getElementById("continue").text() mustBe messages("site.saveAndContinue")
    }

  }

}
