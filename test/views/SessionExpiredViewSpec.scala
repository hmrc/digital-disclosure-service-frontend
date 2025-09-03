/*
 * Copyright 2025 HM Revenue & Customs
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

package views

import base.ViewSpecBase
import play.twirl.api.Html
import support.ViewMatchers
import views.html.SessionExpiredView

class SessionExpiredViewSpec extends ViewSpecBase with ViewMatchers {

  private val page: SessionExpiredView = inject[SessionExpiredView]

  "SessionExpiredView" should {

    def createView: Html = page()(messages, request)

    val view = createView

    "have the correct title" in {
      view.select("title").text() must include(messages("sessionExpired.title"))
    }

    "render the h1 heading" in {
      view.getElementsByClass("govuk-heading-l").text() mustBe messages("sessionExpired.heading")
    }

    "show the guidance paragraph" in {
      view.getElementsByClass("govuk-body").first().text() mustBe messages("sessionExpired.guidance.one")
    }

    "show a Sign in button linking to the start page" in {
      val button = view.getElementsByClass("govuk-button").first()
      button.text() mustBe messages("site.signIn")
      button.attr("href") mustBe controllers.routes.IndexController.onPageLoad.url
    }
  }
}
