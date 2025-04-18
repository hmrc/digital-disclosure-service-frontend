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

package views.notification

import base.ViewSpecBase
import play.twirl.api.Html
import support.ViewMatchers
import views.html.notification.IndividualSentYourNotificationView

class IndividualSentYourNotificationViewSpec extends ViewSpecBase with ViewMatchers {

  val page: IndividualSentYourNotificationView = inject[IndividualSentYourNotificationView]

  private def createView: Html = page(false)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("individualSentYourNotification.title"))
    }

    "contain green box heading" in {
      view.getElementsByClass("govuk-panel__title").text() mustBe messages("individualSentYourNotification.heading")
    }

    "contain green box body text" in {
      view.getElementsByClass("govuk-panel__body").text() mustBe messages("individualSentYourNotification.body")
    }

    "have a first paragraph" in {
      view.getElementById("first-paragraph").text() mustBe messages("individualSentYourNotification.paragraph.first")
    }

    "have a second paragraph" in {
      view.getElementById("second-paragraph").text() mustBe messages("individualSentYourNotification.paragraph.second")
    }

    "have a second heading" in {
      view.getElementsByClass("govuk-heading-m").text() mustBe messages(
        "individualSentYourNotification.paragraph.header"
      )
    }

  }

}
