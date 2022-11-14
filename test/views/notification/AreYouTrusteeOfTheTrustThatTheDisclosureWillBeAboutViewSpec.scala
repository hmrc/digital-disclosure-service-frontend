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
import forms.AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.notification.AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutView
import models.NormalMode

class AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutViewSpec extends ViewSpecBase with ViewMatchers {

  val form = new AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutFormProvider()()
  val page: AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutView = inject[AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutView]

  private def createView: Html = page(form, NormalMode)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("areYouTrusteeOfTheTrustThatTheDisclosureWillBeAbout.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("areYouTrusteeOfTheTrustThatTheDisclosureWillBeAbout.heading")
    }

    "contain body text" in {
      view.getElementById("body").text() mustBe messages("areYouTrusteeOfTheTrustThatTheDisclosureWillBeAbout.body")
    }

    "have yes" in {
      view.getElementsByClass("govuk-radios__label").first().text() mustBe messages("areYouTrusteeOfTheTrustThatTheDisclosureWillBeAbout.yes")
    }

    "have no" in {
      view.getElementsByClass("govuk-radios__label").last().text() mustBe messages("areYouTrusteeOfTheTrustThatTheDisclosureWillBeAbout.no")
    }

    "have hint text for no option" in {
      view.select("div#value-no-item-hint").text() must include(messages("areYouTrusteeOfTheTrustThatTheDisclosureWillBeAbout.no.hint"))
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

  }

}