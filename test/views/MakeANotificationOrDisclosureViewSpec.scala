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
import forms.MakeANotificationOrDisclosureFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.MakeANotificationOrDisclosureView

class MakeANotificationOrDisclosureViewSpec extends ViewSpecBase with ViewMatchers {

  val form = new MakeANotificationOrDisclosureFormProvider()()
  val page: MakeANotificationOrDisclosureView = inject[MakeANotificationOrDisclosureView]

  private def createView: Html = page(form)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("makeANotificationOrDisclosure.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("makeANotificationOrDisclosure.header")
    }

    "contain first paragraph" in {
      view.getElementById("first-paragraph").text() mustBe messages("makeANotificationOrDisclosure.paragraph.first")
    }

    "contain second paragraph" in {
      view.getElementById("second-paragraph").text() mustBe messages("makeANotificationOrDisclosure.paragraph.second")
    }

    "have all the elements in the bullet-list" in {
      view.getElementsByClass("dashed-list-item").get(0).text() mustBe messages("makeANotificationOrDisclosure.bulletList.first")
      view.getElementsByClass("dashed-list-item").get(1).text() mustBe messages("makeANotificationOrDisclosure.bulletList.second")
    }

    "have a guidance link address for first bullet point" in {
      view.getElementById("bullet-first-link").attr("href") mustBe "https://www.gov.uk/government/publications/hmrc-your-guide-to-making-a-disclosure/your-guide-to-making-a-disclosure#general-information"
    }

    "contain third paragraph" in {
      view.getElementById("third-paragraph").text() mustBe messages("makeANotificationOrDisclosure.paragraph.third")
    }

    "contain label" in {
      view.getElementsByClass("govuk-fieldset__legend--m").text() mustBe messages("makeANotificationOrDisclosure.label")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.continue")
    }

  }

}