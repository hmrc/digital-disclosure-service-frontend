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
import forms.WhatIsYourVATRegistrationNumberFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.notification.WhatIsYourVATRegistrationNumberView
import models.NormalMode

class WhatIsYourVATRegistrationNumberViewSpec extends ViewSpecBase with ViewMatchers {

  val form = new WhatIsYourVATRegistrationNumberFormProvider()()
  val page: WhatIsYourVATRegistrationNumberView = inject[WhatIsYourVATRegistrationNumberView]

  private def createView: Html = page(form, NormalMode)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("whatIsYourVATRegistrationNumber.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("whatIsYourVATRegistrationNumber.heading")
    }

    "have a body" in {
      view.getElementById("label").text() mustBe messages("whatIsYourVATRegistrationNumber.label")
    }

    "have a label" in {
      view.getElementsByClass("govuk-label").text() mustBe messages("whatIsYourVATRegistrationNumber.heading")
    }

    "have a hint" in {
      view.getElementsByClass("govuk-hint").text() mustBe messages("whatIsYourVATRegistrationNumber.hint")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

  }

}