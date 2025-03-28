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
import forms.OnshoreLiabilitiesFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.notification.OnshoreLiabilitiesView
import models.NormalMode

class OnshoreLiabilitiesViewSpec extends ViewSpecBase with ViewMatchers {

  val form                         = new OnshoreLiabilitiesFormProvider()()
  val page: OnshoreLiabilitiesView = inject[OnshoreLiabilitiesView]

  private def createView: Html = page(form, NormalMode, false)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("onshoreLiabilities.title"))
    }

    "contain heading" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("onshoreLiabilities.heading")
    }

    "have a first paragraph" in {
      view.getElementById("first-paragraph").text() mustBe messages("onshoreLiabilities.paragraph.first") + messages(
        "onshoreLiabilities.paragraph.second"
      ) + messages("onshoreLiabilities.paragraph.third")
    }

    "have a guidance link" in {
      view.getElementById("guidance-link").attr("href") mustBe "https://www.gov.uk/tax-foreign-income/residence"
    }

    "have a forth paragraph" in {
      view.getElementById("forth-paragraph").text() mustBe messages("onshoreLiabilities.paragraph.forth")
    }

    "have a header for the RadioModelView component" in {
      view.getElementsByClass("govuk-fieldset__legend--m").text() mustBe messages("onshoreLiabilities.label")
    }

    "have all the elements in the bullet-list" in {
      view.getElementsByClass("dashed-list-item").get(0).text() mustBe messages("onshoreLiabilities.bulletList.first")
      view.getElementsByClass("dashed-list-item").get(1).text() mustBe messages("onshoreLiabilities.bulletList.second")
      view.getElementsByClass("dashed-list-item").get(2).text() mustBe messages("onshoreLiabilities.bulletList.third")
      view.getElementsByClass("dashed-list-item").get(3).text() mustBe messages("onshoreLiabilities.bulletList.forth")
    }

    "have yes option" in {
      view.getElementsByClass("govuk-radios__label").first().text() mustBe messages("onshoreLiabilities.yes")
    }

    "have no option" in {
      view.getElementsByClass("govuk-radios__label").last().text() mustBe messages("onshoreLiabilities.no")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

  }

}
