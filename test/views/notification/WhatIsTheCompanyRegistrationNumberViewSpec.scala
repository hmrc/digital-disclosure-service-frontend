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
import forms.WhatIsTheCompanyRegistrationNumberFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.notification.WhatIsTheCompanyRegistrationNumberView
import models.NormalMode

class WhatIsTheCompanyRegistrationNumberViewSpec extends ViewSpecBase with ViewMatchers {

  val form                                         = new WhatIsTheCompanyRegistrationNumberFormProvider()()
  val page: WhatIsTheCompanyRegistrationNumberView = inject[WhatIsTheCompanyRegistrationNumberView]

  private def createView: Html = page(form, NormalMode, false)(using request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("whatIsTheCompanyRegistrationNumber.title"))
    }

    "have a heading" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("whatIsTheCompanyRegistrationNumber.heading")
    }

    "have a body text with the Companies House link" in {
      view.getElementById("body-text").text() mustBe
        s"${messages("whatIsTheCompanyRegistrationNumber.body")} ${messages("whatIsTheCompanyRegistrationNumber.link")}${messages("site.dot")}"
    }

    "have a Companies House link that opens in a new tab" in {
      val link = view.getElementById("companies-house-link")
      link.text() mustBe messages("whatIsTheCompanyRegistrationNumber.link")
      link.attr("href") mustBe "https://find-and-update.company-information.service.gov.uk/"
      link.attr("target") mustBe "_blank"
      link.attr("rel") must include("noopener")
    }

    "have a visible medium label" in {
      val label = view.getElementsByClass("govuk-label").first()
      label.text() mustBe messages("whatIsTheCompanyRegistrationNumber.label")
      label.hasClass("govuk-label--m") mustBe true
      label.hasClass("govuk-visually-hidden") mustBe false
    }

    "have a hint" in {
      view.getElementsByClass("govuk-hint").text() mustBe messages("whatIsTheCompanyRegistrationNumber.hint")
    }

    "display the continue button" in {
      view.getElementById("continue").text() mustBe messages("site.saveAndContinue")
    }

  }

}
