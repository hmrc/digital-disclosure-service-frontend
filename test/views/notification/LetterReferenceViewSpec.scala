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
import forms.LetterReferenceFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.notification.LetterReferenceView
import models.NormalMode

class LetterReferenceViewSpec extends ViewSpecBase with ViewMatchers {

  val form = new LetterReferenceFormProvider()()
  val page: LetterReferenceView = inject[LetterReferenceView]

  private def createView: Html = page(form, NormalMode, false)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("letterReference.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("letterReference.heading")
    }

    "contain label" in {
      view.getElementsByClass("govuk-label").text() mustBe messages("letterReference.heading")
    }

    "contain body" in {
      view.getElementById("body").text() mustBe messages("letterReference.body")
    }

    "contain hint" in {
      view.getElementsByClass("govuk-hint").text() mustBe messages("letterReference.hint")
    }


    "have a text input" in {
      view.getElementsByClass("govuk-input").first must haveClass("govuk-input--width-10")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

  }

}