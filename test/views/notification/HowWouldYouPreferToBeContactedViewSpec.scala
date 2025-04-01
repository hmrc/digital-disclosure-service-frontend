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
import forms.HowWouldYouPreferToBeContactedFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.notification.HowWouldYouPreferToBeContactedView
import models.NormalMode

class HowWouldYouPreferToBeContactedViewSpec extends ViewSpecBase with ViewMatchers {

  val form                                     = new HowWouldYouPreferToBeContactedFormProvider()()
  val page: HowWouldYouPreferToBeContactedView = inject[HowWouldYouPreferToBeContactedView]

  private def createView: Html = page(form, NormalMode, false)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("howWouldYouPreferToBeContacted.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("howWouldYouPreferToBeContacted.heading")
    }

    "have a label" in {
      view.getElementsByClass("govuk-fieldset__legend").text() mustBe messages("howWouldYouPreferToBeContacted.heading")
    }

    "have a body" in {
      view.getElementById("body").text() mustBe messages("howWouldYouPreferToBeContacted.body")
    }

    "contain hint" in {
      view.getElementsByClass("govuk-hint").first().text() mustBe messages("howWouldYouPreferToBeContacted.hint")
    }

    "contain the email option" in {
      view.getElementsByClass("govuk-checkboxes__label").first().text() mustBe messages(
        "howWouldYouPreferToBeContacted.email"
      )
    }

    "contain the telephone option" in {
      view.getElementsByClass("govuk-checkboxes__label").last().text() mustBe messages(
        "howWouldYouPreferToBeContacted.telephone"
      )
    }

    "contain the telephone option hint" in {
      view.getElementsByClass("govuk-checkboxes__hint").last().text() mustBe messages(
        "howWouldYouPreferToBeContacted.telephone.hint"
      )
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

  }

}
