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
import forms.WhatIsTheCaseReferenceFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.reference.WhatIsTheCaseReferenceView
import models.NormalMode

class WhatIsTheCaseReferenceViewSpec extends ViewSpecBase with ViewMatchers {

  val form = new WhatIsTheCaseReferenceFormProvider()()
  val page: WhatIsTheCaseReferenceView = inject[WhatIsTheCaseReferenceView]

  private def createView: Html = page(form, NormalMode)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("whatIsTheCaseReference.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("whatIsTheCaseReference.heading")
    }

    "contain body" in {
      view.getElementsByClass("govuk-label").text() mustBe messages("whatIsTheCaseReference.body")
    }

    "contain hint" in {
      view.getElementsByClass("govuk-hint").text() mustBe messages("whatIsTheCaseReference.hint")
    }

    "have a text input" in {
      view.getElementsByClass("govuk-input").first must haveClass("govuk-!-width-full")
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