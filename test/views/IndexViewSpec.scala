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

package views.notification

import base.ViewSpecBase
import play.twirl.api.Html
import support.ViewMatchers
import views.html.IndexView

class IndexViewSpec extends ViewSpecBase with ViewMatchers {

  val page: IndexView = inject[IndexView]

  private def createView: Html = page()(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("index.title"))
    }

    "contain heading" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("index.heading")
    }

    "display the start now button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("start")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.continue")
    }

    "have a first paragraph" in {
      view.getElementById("first-paragraph").text() mustBe messages("index.guidance.paragraph.first")
    }

    "have a second paragraph" in {
      view.getElementById("second-paragraph").text() mustBe messages("index.guidance.paragraph.second")
    }

    "have a third paragraph" in {
      view.getElementById("third-paragraph").text() mustBe messages("index.guidance.paragraph.third") + messages("index.guidance.paragraph.third.link")
    }

    "have a third paragraph guidance link" in {
      view.getElementById("third-paragraph-link").attr("href") mustBe "https://www.gov.uk/government/publications/hmrc-your-guide-to-making-a-disclosure/your-guide-to-making-a-disclosure#general-information"
    }

    "contain before you start heading" in {
      view.getElementsByClass("govuk-heading-m").text() mustBe messages("index.heading.second")
    }

    "have a forth paragraph" in {
      view.getElementById("forth-paragraph").text() mustBe messages("index.guidance.paragraph.forth.link") + messages("index.guidance.paragraph.forth")
    }

    "have a forth paragraph guidance link" in {
      view.getElementById("forth-paragraph-link").attr("href") mustBe "https://www.gov.uk/government/publications/hmrc-your-guide-to-making-a-disclosure/your-guide-to-making-a-disclosure"
    }

    "have a fifth paragraph" in {
      view.getElementById("fifth-paragraph").text() mustBe 
        messages("index.guidance.paragraph.fifth") + 
        messages("index.guidance.paragraph.fifth.link") +
        messages("index.guidance.paragraph.sixth")
    }

    "have a fifth paragraph guidance link" in {
      view.getElementById("fifth-paragraph-link").attr("href") mustBe "https://www.gov.uk/appoint-tax-agent"
    }

    "contain what you will need to complete your disclosure heading" in {
      view.getElementsByClass("govuk-heading-s").text() mustBe messages("index.heading.third")
    }

    "have a seventh paragraph" in {
      view.getElementById("seventh-paragraph").text() mustBe messages("index.guidance.paragraph.seventh")
    }

    "have all the elements in the bullet-list" in {
      view.getElementsByClass("dashed-list-item").get(0).text() mustBe messages("index.bulletList.first") + messages("index.bulletList.first.link")
      view.getElementsByClass("dashed-list-item").get(1).text() mustBe messages("index.bulletList.second")
      view.getElementsByClass("dashed-list-item").get(2).text() mustBe messages("index.bulletList.third")
      view.getElementsByClass("dashed-list-item").get(3).text() mustBe messages("index.bulletList.forth")
    }

    "have a first bullet point guidance link" in {
      view.getElementById("first-bullet-link").attr("href") mustBe "https://www.gov.uk/government/publications/hmrc-your-guide-to-making-a-disclosure/your-guide-to-making-a-disclosure"
    }

  }

}