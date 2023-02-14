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

package views.submit

import base.ViewSpecBase
import play.twirl.api.Html
import support.ViewMatchers
import views.html.SubmittedView

class SubmittedViewSpec extends ViewSpecBase with ViewMatchers {

  val page: SubmittedView = inject[SubmittedView]

  private def createView: Html = page(false, "reference")(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("submitted.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-panel__title").text() mustBe messages("submitted.heading")
    }

    "have a first paragraph" in {
      view.getElementById("paragraph1").text() mustBe messages("submitted.paragraph1") + " " + messages("submitted.paragraph1.link") + messages("site.dot")
    }

    "have a second paragraph" in {
      view.getElementById("paragraph2").text() mustBe messages("submitted.paragraph2") + " " + messages("submitted.paragraph2.link") + messages("site.dot")
    }

    "have a third paragraph" in {
      view.getElementById("paragraph3").text() mustBe messages("submitted.paragraph3")
    }

    "have a fourth paragraph" in {
      view.getElementById("paragraph4").text() mustBe messages("submitted.paragraph4") + " " + messages("submitted.paragraph4.link") + messages("site.dot")
    }

  }

}