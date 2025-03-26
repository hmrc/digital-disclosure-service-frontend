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

package views

import base.ViewSpecBase
import play.twirl.api.Html
import support.ViewMatchers
import views.html.NotificationSubmittedView

class NotificationSubmittedViewSpec extends ViewSpecBase with ViewMatchers {

  val page: NotificationSubmittedView = inject[NotificationSubmittedView]

  private def createView(dateString: String, ref: String): Html = page(dateString, ref)(request, messages)

  "view" should {

    val dateString = "24 March 2022"
    val ref        = "12345"
    val view       = createView(dateString, ref)

    "have title" in {
      view.select("title").text() must include(messages("notificationSubmitted.title", dateString))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("notificationSubmitted.heading", dateString)
    }

    "contain first-paragraph" in {
      view.getElementById("first-paragraph").text() mustBe messages("notificationSubmitted.paragraph1", ref)
    }

    "contain second-paragraph" in {
      view.getElementById("second-paragraph").text() mustBe messages("notificationSubmitted.paragraph2")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId("disclosure")
      view.getElementsByClass("govuk-button").text() mustBe messages("notificationSubmitted.button")
    }

  }

}
