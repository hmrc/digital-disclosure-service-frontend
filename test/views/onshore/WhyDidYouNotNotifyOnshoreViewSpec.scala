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
import forms.onshore.WhyDidYouNotNotifyOnshoreFormProvider
import models.{NormalMode, RelatesTo}
import play.twirl.api.Html
import support.ViewMatchers
import views.html.onshore.WhyDidYouNotNotifyOnshoreView

class WhyDidYouNotNotifyOnshoreViewSpec extends ViewSpecBase with ViewMatchers {

  val form                                = new WhyDidYouNotNotifyOnshoreFormProvider()(true, RelatesTo.AnIndividual)
  val page: WhyDidYouNotNotifyOnshoreView = inject[WhyDidYouNotNotifyOnshoreView]

  private def createViewAsIndividual: Html =
    page(form, NormalMode, true, RelatesTo.AnIndividual)(using request, messages)
  private def createViewAsCompany: Html    = page(form, NormalMode, false, RelatesTo.ACompany)(using request, messages)

  "view as individual" should {

    val view = createViewAsIndividual

    "have title" in {
      view.select("title").text() must include(messages("whyDidYouNotNotify.title.you"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("whyDidYouNotNotify.title.you")
    }

    "contain second paragraph" in {
      view.getElementById("second-paragraph").text() mustBe messages("whyDidYouNotNotify.paragraph.second")
    }

    "contain third paragraph" in {
      view.getElementById("third-paragraph").text() mustBe messages("whyDidYouNotNotify.paragraph.third")
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
      view.select("title").text() must include(messages(s"whyDidYouNotNotify.title.${RelatesTo.ACompany}"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages(
        s"whyDidYouNotNotify.title.${RelatesTo.ACompany}"
      )
    }

    "display the continue button" in {
      view.getElementById("continue").text() mustBe messages("site.saveAndContinue")
    }
  }
}
