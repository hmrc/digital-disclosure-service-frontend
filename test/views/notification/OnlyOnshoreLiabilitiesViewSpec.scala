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
import models.NormalMode
import play.twirl.api.Html
import support.ViewMatchers
import views.html.notification.OnlyOnshoreLiabilitiesView

class OnlyOnshoreLiabilitiesViewSpec extends ViewSpecBase with ViewMatchers {

  val page: OnlyOnshoreLiabilitiesView = inject[OnlyOnshoreLiabilitiesView]

  private def createView: Html = page()(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("onlyOnshoreLiabilities.title"))
    }

    "contain heading" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("onlyOnshoreLiabilities.heading")
    }

    "have a first paragraph" in {
      view.getElementById("first-paragraph").text() mustBe messages("onlyOnshoreLiabilities.paragraph.first") + messages("onlyOnshoreLiabilities.paragraph.link") + messages("onlyOnshoreLiabilities.paragraph.second")
    }

    "have a guidance link" in {
      view.getElementById("guidance-link").attr("href") mustBe "https://www.gov.uk/tax-foreign-income/residence"
    }

    "have a third paragraph" in {
      view.getElementById("third-paragraph").text() mustBe messages("onlyOnshoreLiabilities.paragraph.third")
    }

    "have all the elements in the bullet-list" in {
      view.getElementsByClass("dashed-list-item").get(0).text() mustBe messages("onlyOnshoreLiabilities.bulletList.first")
      view.getElementsByClass("dashed-list-item").get(1).text() mustBe messages("onlyOnshoreLiabilities.bulletList.second")
      view.getElementsByClass("dashed-list-item").get(2).text() mustBe messages("onlyOnshoreLiabilities.bulletList.third")
      view.getElementsByClass("dashed-list-item").get(3).text() mustBe messages("onlyOnshoreLiabilities.bulletList.forth")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.confirmAndContinue")
      view.getElementsByClass("govuk-button").attr("href") mustBe controllers.notification.routes.WhatIsYourFullNameController.onPageLoad(NormalMode).url
    }

  }

}