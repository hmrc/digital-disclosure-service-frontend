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

package views.offer

import base.ViewSpecBase
import forms.OfferLetterFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.OfferLetterView

class OfferLetterViewSpec extends ViewSpecBase with ViewMatchers {

  val form = new OfferLetterFormProvider()()
  val page: OfferLetterView = inject[OfferLetterView]

  private def createView(entity: String, agentName: String): Html = page(form, "some name", "some address line", 123, entity, agentName, areTheyTheIndividual = true)(request, messages)

  "view" should {

    val view = createView("individual", "agent name")

    "have title" in {
      view.select("title").text() must include(messages("offerLetter.individual.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("offerLetter.individual.heading")
    }

    "have an introduction paragraph" in {
      view.getElementById("introduction-paragraph").text() mustBe messages("offerLetter.introduction.individual", 123)
    }

    "have the letter subheading" in {
      view.getElementById("subheading-letter").text() mustBe messages("offerLetter.subheading.letter")
    }

    "have a first letter paragraph" in {
      view.getElementById("letter-paragraph1").text() mustBe messages("offerLetter.letter.paragraph1.individual")
    }

    "have a second letter paragraph" in {
      view.getElementById("letter-paragraph2").text() mustBe messages("offerLetter.letter.paragraph2.individual")
    }

    "have a third letter paragraph" in {
      view.getElementById("letter-paragraph3").text() mustBe messages("offerLetter.letter.paragraph3.individual", "some name")
    }

    "have address lines" in {
      view.getElementById("address").text() mustBe "some address line"
    }

    "have a fourth letter paragraph" in {
      view.getElementById("letter-paragraph4").text() mustBe messages("offerLetter.letter.paragraph4", "some name")
    }

    "have a fifth letter paragraph" in {
      view.getElementById("letter-paragraph5").text() mustBe messages("offerLetter.letter.paragraph5", "some name")
    }

    "have a sixth letter paragraph" in {
      view.getElementById("letter-paragraph6").text() mustBe messages("offerLetter.letter.paragraph6", "some name")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("send-disclosure")
      view.getElementsByClass("govuk-button").text() mustBe messages("offerLetter.button")
    }

  }

  "view for company" should {

    val view = createView("company", "agent name")

    "have title" in {
      view.select("title").text() must include(messages("offerLetter.company.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("offerLetter.company.heading")
    }

    "have an introduction paragraph" in {
      view.getElementById("introduction-paragraph").text() mustBe messages("offerLetter.introduction.company", 123)
    }

    "have a first letter paragraph" in {
      view.getElementById("letter-paragraph1").text() mustBe messages("offerLetter.letter.paragraph1.company")
    }

    "have a second letter paragraph" in {
      view.getElementById("letter-paragraph2").text() mustBe messages("offerLetter.letter.paragraph2.company")
    }

    "have a third letter paragraph" in {
      view.getElementById("letter-paragraph3").text() mustBe messages("offerLetter.letter.paragraph3.company", "some name")
    }

  }

  "view for estate" should {

    val view = createView("estate", "agent name")

    "have title" in {
      view.select("title").text() must include(messages("offerLetter.estate.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("offerLetter.estate.heading")
    }

    "have an introduction paragraph" in {
      view.getElementById("introduction-paragraph").text() mustBe messages("offerLetter.introduction.estate", 123)
    }

    "have a first letter paragraph" in {
      view.getElementById("letter-paragraph1").text() mustBe messages("offerLetter.letter.paragraph1.estate", "some name")
    }

    "have a second letter paragraph" in {
      view.getElementById("letter-paragraph2").text() mustBe messages("offerLetter.letter.paragraph2.estate")
    }

    "have a third letter paragraph" in {
      view.getElementById("letter-paragraph3").text() mustBe messages("offerLetter.letter.paragraph3.estate", "some name", "agent name")
    }

  }

  "view for trust" should {

    val view = createView("trust", "agent name")

    "have title" in {
      view.select("title").text() must include(messages("offerLetter.trust.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("offerLetter.trust.heading")
    }

    "have an introduction paragraph" in {
      view.getElementById("introduction-paragraph").text() mustBe messages("offerLetter.introduction.trust", 123)
    }

    "have a first letter paragraph" in {
      view.getElementById("letter-paragraph1").text() mustBe messages("offerLetter.letter.paragraph1.trust")
    }

    "have a second letter paragraph" in {
      view.getElementById("letter-paragraph2").text() mustBe messages("offerLetter.letter.paragraph2.trust")
    }

    "have a third letter paragraph" in {
      view.getElementById("letter-paragraph3").text() mustBe messages("offerLetter.letter.paragraph3.trust", "some name", "agent name")
    }

  }

}