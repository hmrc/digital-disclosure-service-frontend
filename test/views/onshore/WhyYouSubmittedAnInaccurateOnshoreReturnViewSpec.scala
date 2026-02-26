/*
 * Copyright 2026 HM Revenue & Customs
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

package views.onshore

import base.ViewSpecBase
import forms.onshore.WhyYouSubmittedAnInaccurateOnshoreReturnFormProvider
import models.{NormalMode, RelatesTo}
import play.twirl.api.Html
import support.ViewMatchers
import views.html.onshore.WhyYouSubmittedAnInaccurateOnshoreReturnView

class WhyYouSubmittedAnInaccurateOnshoreReturnViewSpec extends ViewSpecBase with ViewMatchers {

  val form                                               = new WhyYouSubmittedAnInaccurateOnshoreReturnFormProvider()()
  val page: WhyYouSubmittedAnInaccurateOnshoreReturnView = inject[WhyYouSubmittedAnInaccurateOnshoreReturnView]

  private def createViewAsIndividual: Html = page(form, NormalMode, true, RelatesTo.AnIndividual)(request, messages)
  private def createViewAsCompany: Html    = page(form, NormalMode, false, RelatesTo.ACompany)(request, messages)

  "view as individual" should {

    val view = createViewAsIndividual

    "have title" in {
      view.select("title").text() must include(messages("WhyYouSubmittedAnInaccurateReturn.title.you"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("WhyYouSubmittedAnInaccurateReturn.title.you")
    }

    "contain second paragraph" in {
      view.getElementById("second-paragraph").text() mustBe messages(
        "WhyYouSubmittedAnInaccurateReturn.paragraph.second"
      )
    }

    "contain third paragraph" in {
      view.getElementById("third-paragraph").text() mustBe messages("WhyYouSubmittedAnInaccurateReturn.paragraph.third")
    }

    "display the continue button" in {
      view.getElementById("continue").text() mustBe messages("site.saveAndContinue")
    }

    "have a task list link" in {
      view.getElementById("task-list-link").attr("href") mustBe controllers.routes.TaskListController.onPageLoad.url
    }
  }

  "view as company" should {

    val view = createViewAsCompany

    "have title" in {
      view.select("title").text() must include(
        messages(s"WhyYouSubmittedAnInaccurateReturn.title.${RelatesTo.ACompany}")
      )
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages(
        s"WhyYouSubmittedAnInaccurateReturn.title.${RelatesTo.ACompany}"
      )
    }

    "display the continue button" in {
      view.getElementById("continue").text() mustBe messages("site.saveAndContinue")
    }
  }
}
