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

package views.reference

import base.ViewSpecBase
import forms.DoYouHaveACaseReferenceFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.reference.DoYouHaveACaseReferenceView
import models.NormalMode

class DoYouHaveACaseReferenceViewSpec extends ViewSpecBase with ViewMatchers {

  val form = new DoYouHaveACaseReferenceFormProvider()()
  val page: DoYouHaveACaseReferenceView = inject[DoYouHaveACaseReferenceView]

  private def createView: Html = page(form, NormalMode)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("doYouHaveACaseReference.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("doYouHaveACaseReference.heading")
    }

    "contain body text" in {
      view.getElementById("body").text() mustBe messages("doYouHaveACaseReference.label")
    }

    "have yes" in {
      view.getElementsByClass("govuk-radios__label").first().text() mustBe messages("doYouHaveACaseReference.yes")
    }

    "have no" in {
      view.getElementsByClass("govuk-radios__label").last().text() mustBe messages("doYouHaveACaseReference.no")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

  }

}