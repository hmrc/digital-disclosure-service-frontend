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
import views.html.DeclarationView

class DeclarationViewSpec extends ViewSpecBase with ViewMatchers {

  val page: DeclarationView = inject[DeclarationView]

  private def createView(isAgent: Boolean): Html = page(isAgent)(request, messages)

  "view for an agent" should {

    val view = createView(true)

    "have title" in {
      view.select("title").text() must include(messages("declaration.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("declaration.heading")
    }

    "contain an inset body" in {
      view.getElementById("inset-body").text() mustBe messages("declaration.inset")
    }

    "contain paragraph" in {
      view.getElementById("paragraph").text() mustBe messages("declaration.paragraph")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId("confirm")
      view.getElementsByClass("govuk-button").text() mustBe messages("declaration.button")
    }

  }

  "view other than for an agent" should {

    val view = createView(false)

    "have title" in {
      view.select("title").text() must include(messages("declaration.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("declaration.heading")
    }

    "contain paragraph" in {
      view.getElementById("paragraph").text() mustBe messages("declaration.paragraph")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId("confirm")
      view.getElementsByClass("govuk-button").text() mustBe messages("declaration.button")
    }

  }

}
