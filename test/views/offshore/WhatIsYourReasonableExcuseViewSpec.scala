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

package views.offshore

import base.ViewSpecBase
import forms.WhatIsYourReasonableExcuseFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.offshore.WhatIsYourReasonableExcuseView
import models.NormalMode

class WhatIsYourReasonableExcuseViewSpec extends ViewSpecBase with ViewMatchers {

  val form = new WhatIsYourReasonableExcuseFormProvider()()
  val page: WhatIsYourReasonableExcuseView = inject[WhatIsYourReasonableExcuseView]

  private def createView: Html = page(form, NormalMode)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("whatIsYourReasonableExcuse.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("whatIsYourReasonableExcuse.heading")
    }

    "contain label" in {
      view.getElementsByClass("govuk-label").first().text() mustBe messages("whatIsYourReasonableExcuse.excuse")
      view.getElementsByClass("govuk-label").last().text() mustBe messages("whatIsYourReasonableExcuse.years")
    }

    "contain hints" in {
      view.getElementById("years-hint").text() mustBe messages("whatIsYourReasonableExcuse.years.hint")
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