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
import forms.OffshoreLiabilitiesFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.notification.OffshoreLiabilitiesView
import models.NormalMode

class OffshoreLiabilitiesViewSpec extends ViewSpecBase with ViewMatchers {

  val form = new OffshoreLiabilitiesFormProvider()()
  val page: OffshoreLiabilitiesView = inject[OffshoreLiabilitiesView]

  private def createView: Html = page(form, NormalMode)(request, messages)

  "view" should {

    val view = createView


    "have title" in {
      view.select("title").text() must include(messages("offshoreLiabilities.title"))
    }

    "have a heading component" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("offshoreLiabilities.heading")
    }

    "have a first paragraph" in {
      view.getElementById("first-paragraph").text() mustBe messages("offshoreLiabilities.paragraph.first")
    }

    "have a second paragraph" in {
      view.getElementById("second-paragraph").text() mustBe messages("offshoreLiabilities.paragraph.second")
    }

    "have a guidance link address" in {
      view.getElementById("guidance-link").attr("href") mustBe "https://www.gov.uk/guidance/worldwide-disclosure-facility-make-a-disclosure"
    }

    "have a guidance link" in {
      view.getElementById("guidance-link").text() mustBe messages("offshoreLiabilities.guidance.link")
    }

    "have a hidden header for the RadioModelView component" in {
      view.getElementsByClass("govuk-fieldset__legend").text() mustBe messages("offshoreLiabilities.heading")
    }

    "have all the elements in the bullet-list" in {
      view.getElementsByClass("dashed-list-item").get(0).text() mustBe messages("offshoreLiabilities.bulletList.first")
      view.getElementsByClass("dashed-list-item").get(1).text() mustBe messages("offshoreLiabilities.bulletList.second")
      view.getElementsByClass("dashed-list-item").get(2).text() mustBe messages("offshoreLiabilities.bulletList.third")
      view.getElementsByClass("dashed-list-item").get(3).text() mustBe messages("offshoreLiabilities.bulletList.forth")
    }

    "have I want to disclose offshore liabilities option" in {
      view.getElementsByClass("govuk-radios__label").first().text() mustBe messages("offshoreLiabilities.option1")
    }

    "have I do not have offshore liabilities to disclose option" in {
      view.getElementsByClass("govuk-radios__label").last().text() mustBe messages("offshoreLiabilities.option2")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

  }

}