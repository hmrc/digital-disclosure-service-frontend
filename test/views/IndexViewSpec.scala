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
import views.html.IndexView
import models.NormalMode

class IndexViewSpec extends ViewSpecBase with ViewMatchers {

  val page: IndexView = inject[IndexView]

  "view" should {

    def createView: Html =
      page(controllers.routes.MakeANotificationOrDisclosureController.onPageLoad.url, false)(request, messages)

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("index.title"))
    }

    "contain heading" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("index.heading")
    }

    "display the start now button when full disclosure journey enabled is true" in {
      view.getElementById("start").text() mustBe messages("site.continue")
      view
        .getElementById("start")
        .attr("href") mustBe controllers.routes.MakeANotificationOrDisclosureController.onPageLoad.url
    }

    "have a first paragraph" in {
      view.getElementById("first-paragraph").text() mustBe messages("index.guidance.paragraph.first")
    }

    "have a second paragraph" in {
      view.getElementById("second-paragraph").text() mustBe messages("index.guidance.paragraph.second")
    }

    "have a third paragraph" in {
      view.getElementById("third-paragraph").text() mustBe messages("index.guidance.paragraph.third")
    }

    "have all the elements in the bullet-list" in {
      view.getElementsByClass("dashed-list-item").get(0).text() mustBe messages("index.bulletList.first.link")
      view.getElementsByClass("dashed-list-item").get(1).text() mustBe messages("index.bulletList.second")
    }

    "have a bullet point guidance link" in {
      view
        .getElementById("first-bullet-link")
        .attr(
          "href"
        ) mustBe "https://www.gov.uk/government/publications/hmrc-your-guide-to-making-a-disclosure/your-guide-to-making-a-disclosure#general-information"
    }

    "have a forth paragraph" in {
      view.getElementById("forth-paragraph").text() mustBe messages("index.guidance.paragraph.forth")
    }

    "contain before you start heading" in {
      view.getElementsByClass("govuk-heading-m").text() mustBe messages("index.heading.second")
    }

    "have a fifth paragraph" in {
      view.getElementById("fifth-paragraph").text() mustBe messages("index.guidance.paragraph.forth.link") + messages(
        "index.guidance.paragraph.fifth"
      )
    }

    "have a forth paragraph guidance link" in {
      view
        .getElementById("forth-paragraph-link")
        .attr(
          "href"
        ) mustBe "https://www.gov.uk/government/publications/hmrc-your-guide-to-making-a-disclosure/your-guide-to-making-a-disclosure"
    }

    "have a sixth paragraph" in {
      view.getElementById("sixth-paragraph").text() mustBe messages("index.guidance.paragraph.sixth") + messages(
        "index.guidance.paragraph.sixth.link"
      ) + messages("site.dot")
    }

    "have a sixth paragraph guidance link" in {
      view
        .getElementById("sixth-paragraph-link")
        .attr("href") mustBe "https://www.gov.uk/guidance/admitting-tax-fraud-the-contractual-disclosure-facility-cdf"
    }

    "have a seventh paragraph" in {
      view.getElementById("seventh-paragraph").text() mustBe messages("index.guidance.paragraph.seventh") + messages(
        "index.guidance.paragraph.seventh.link"
      ) + messages("index.guidance.paragraph.eighth")
    }

    "have a seventh paragraph guidance link" in {
      view.getElementById("seventh-paragraph-link").attr("href") mustBe "https://www.gov.uk/appoint-tax-agent"
    }

    "contain what you will need to complete your disclosure heading" in {
      view.getElementById("index-heading").text() mustBe messages("index.heading.third")
    }

    "have a ninth paragraph" in {
      view.getElementById("ninth-paragraph").text() mustBe messages("index.guidance.paragraph.ninth")
    }

    "have the elements in the bullet-list" in {
      view.getElementsByClass("dashed-list-item").get(2).text() mustBe messages("index.bulletList.third") + messages(
        "index.bulletList.third.link"
      )
      view.getElementsByClass("dashed-list-item").get(3).text() mustBe messages("index.bulletList.forth")
      view.getElementsByClass("dashed-list-item").get(4).text() mustBe messages("index.bulletList.fifth")
    }

    "have a bullet guidance link" in {
      view
        .getElementById("bullet-link")
        .attr(
          "href"
        ) mustBe "https://www.gov.uk/government/publications/hmrc-your-guide-to-making-a-disclosure/your-guide-to-making-a-disclosure"
    }

  }

  "view" should {

    def createView: Html = page(
      controllers.notification.routes.ReceivedALetterController.onPageLoad(NormalMode).url,
      false
    )(request, messages)

    val view = createView

    "display the start now button when full disclosure journey enabled is false" in {
      view.getElementById("start").attr("href") mustBe controllers.notification.routes.ReceivedALetterController
        .onPageLoad(NormalMode)
        .url
    }
  }

  "view" should {

    def createView: Html = page(
      controllers.notification.routes.ReceivedALetterController.onPageLoad(NormalMode).url,
      true
    )(request, messages)

    val view = createView

    "display the right messages if the user is an agent" in {
      view.getElementsByClass("dashed-list-item").get(2).text() mustBe messages(
        "index.bulletList.agent.first"
      ) + messages("index.bulletList.agent.first.link")
      view.getElementsByClass("seventh-paragraph") mustBe empty
      view.getElementById("first-paragraph").text() mustBe messages("index.guidance.paragraph.agent.first")
    }
  }

}
