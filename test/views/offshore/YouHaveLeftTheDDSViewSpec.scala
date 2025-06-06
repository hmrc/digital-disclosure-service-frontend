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

package views.offshore

import base.ViewSpecBase
import config.FrontendAppConfig
import forms.YouHaveLeftTheDDSFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.offshore.YouHaveLeftTheDDSView
import models.NormalMode

class YouHaveLeftTheDDSViewSpec extends ViewSpecBase with ViewMatchers {

  val form                        = new YouHaveLeftTheDDSFormProvider()()
  val page: YouHaveLeftTheDDSView = inject[YouHaveLeftTheDDSView]
  val config: FrontendAppConfig   = inject[FrontendAppConfig]

  private def createView: Html = page(form, NormalMode)(request, messages, config)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("youHaveLeftTheDDS.title"))
    }

    "contain green box heading" in {
      view.getElementsByClass("govuk-panel__body").text() mustBe messages("youHaveLeftTheDDS.heading")
    }

    "contain first paragraph" in {
      view.getElementById("body").text() mustBe
        messages("youHaveLeftTheDDS.body.first") +
        messages("youHaveLeftTheDDS.link") +
        messages("youHaveLeftTheDDS.body.second")
    }

    "have an exit survey paragraph" in {
      view.getElementById("exit-survey").text mustBe
        messages("exitSurvey.heading") + " " + messages("exitSurvey.p1") + " " + messages(
          "exitSurvey.link"
        ) + " " + messages("exitSurvey.p2")
    }

    "have an exit survey link" in {
      view.getElementsByClass("govuk-link").attr("href") mustBe controllers.auth.routes.AuthController.signOut.url
    }

    "have a guidance link" in {
      view
        .getElementById("guidance-link-first")
        .attr("href") mustBe "https://www.gov.uk/guidance/admitting-tax-fraud-the-contractual-disclosure-facility-cdf"
    }
  }

}
