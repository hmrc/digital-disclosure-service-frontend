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
import forms.WhatIsTheIndividualDateOfBirthControllerFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.notification.WhatIsTheIndividualDateOfBirthControllerView
import models.NormalMode

class WhatIsTheIndividualDateOfBirthControllerViewSpec extends ViewSpecBase with ViewMatchers {

  val form = new WhatIsTheIndividualDateOfBirthControllerFormProvider()()
  val page: WhatIsTheIndividualDateOfBirthControllerView = inject[WhatIsTheIndividualDateOfBirthControllerView]

  private def createView: Html = page(form, NormalMode)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("whatIsTheIndividualDateOfBirthController.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-fieldset__heading").text() mustBe messages("whatIsTheIndividualDateOfBirthController.heading")
    }

    "contain hint" in {
      view.getElementsByClass("govuk-hint").text() mustBe messages("whatIsTheIndividualDateOfBirthController.hint")
    }

    "contain day, month & year" in {
      view.getElementsByClass("govuk-date-input__item").get(0).text() mustBe messages("date.day")
      view.getElementsByClass("govuk-date-input__item").get(1).text() mustBe messages("date.month")
      view.getElementsByClass("govuk-date-input__item").get(2).text() mustBe messages("date.year")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

  }

}