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
import forms.WhatIsTheTrustNameFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.notification.WhatIsTheTrustNameView
import models.NormalMode

class WhatIsTheTrustNameViewSpec extends ViewSpecBase with ViewMatchers {

  val form = new WhatIsTheTrustNameFormProvider()()
  val page: WhatIsTheTrustNameView = inject[WhatIsTheTrustNameView]

  private def createView: Html = page(form, NormalMode, false)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("whatIsTheTrustName.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-label--xl").text() mustBe messages("whatIsTheTrustName.heading")
    }

    "have a text input" in {
      view.getElementsByClass("govuk-input").first must haveClass("govuk-input--width-50")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

  }

}