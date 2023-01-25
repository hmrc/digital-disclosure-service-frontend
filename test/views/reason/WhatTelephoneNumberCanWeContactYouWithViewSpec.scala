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

package views.reason

import base.ViewSpecBase
import forms.WhatTelephoneNumberCanWeContactYouWithFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.reason.WhatTelephoneNumberCanWeContactYouWithView
import models.NormalMode

class WhatTelephoneNumberCanWeContactYouWithViewSpec extends ViewSpecBase with ViewMatchers {

  val form = new WhatTelephoneNumberCanWeContactYouWithFormProvider()()
  val page: WhatTelephoneNumberCanWeContactYouWithView = inject[WhatTelephoneNumberCanWeContactYouWithView]

  private def createView: Html = page(form, NormalMode)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("whatTelephoneNumberCanWeContactYouWith.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("whatTelephoneNumberCanWeContactYouWith.heading")
    }

    "contain body" in {
      view.getElementById("body").text() mustBe messages("whatTelephoneNumberCanWeContactYouWith.body")
    }

    "contain hint" in {
      view.getElementsByClass("govuk-hint").text() mustBe messages("whatTelephoneNumberCanWeContactYouWith.hint")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

    "have a task list link" in {
      view.getElementById("task-list-link").attr("href") mustBe controllers.routes.TaskListController.onPageLoad.url
    }

  }

}