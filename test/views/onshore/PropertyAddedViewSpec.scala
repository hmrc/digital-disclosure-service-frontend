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

package views.onshore

import base.ViewSpecBase
import controllers.onshore.routes
import forms.PropertyAddedFormProvider
import models.address.{Address, Country}
import play.twirl.api.Html
import support.ViewMatchers
import views.html.onshore.PropertyAddedView
import models.{LettingProperty, NormalMode}
import viewmodels.onshore.LettingPropertyModel

class PropertyAddedViewSpec extends ViewSpecBase with ViewMatchers {

  val form                    = new PropertyAddedFormProvider()()
  val page: PropertyAddedView = inject[PropertyAddedView]

  val postcode1         = "AA111AA"
  val address1: Address = Address(
    line1 = "Line 1",
    postcode = Some(postcode1),
    line2 = None,
    line3 = None,
    line4 = None,
    country = Country("AA")
  )
  val property1         = LettingProperty(address = Some(address1))

  val postcode2         = "BB222BB"
  val address2: Address = Address(
    line1 = "Line 2",
    postcode = Some(postcode2),
    line2 = None,
    line3 = None,
    line4 = None,
    country = Country("AA")
  )
  val property2         = LettingProperty(address = Some(address2))

  "view for a single property" should {

    val propertiesSummaries = LettingPropertyModel.row(Seq(property1), NormalMode)

    def createView: Html = page(form, propertiesSummaries, NormalMode)(request, messages)
    val view             = createView

    "have title" in {
      view.select("title").text() must include(messages("propertyAdded.title.single"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("propertyAdded.heading.single")
    }

    "contain summary row" in {
      view.getElementsByClass("hmrc-summary-list__key").text() mustBe s"${address1.line1}, $postcode1"
    }

    "contain remove link" in {
      view.getElementsByClass("summary-list-remove-link").first().text() must include(messages("site.remove"))
      view.getElementsByClass("summary-list-remove-link").last().text()  must include(messages("site.remove"))
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

  }

  "view for a multiple properties" should {

    val propertiesSummaries = LettingPropertyModel.row(Seq(property1, property2), NormalMode)

    def createView: Html = page(form, propertiesSummaries, NormalMode)(request, messages)

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("propertyAdded.title.multi", 2))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("propertyAdded.title.multi", 2)
    }

    "contain summary row" in {
      view.getElementsByClass("hmrc-summary-list__key").first().text() mustBe s"${address1.line1}, $postcode1"
      view.getElementsByClass("hmrc-summary-list__key").last().text() mustBe s"${address2.line1}, $postcode2"
    }

    "contain remove link" in {
      view.getElementsByClass("summary-list-remove-link").first().text() must include(messages("site.remove"))
      view.getElementsByClass("summary-list-remove-link").first().attr("href") mustBe routes.PropertyAddedController
        .remove(0, NormalMode)
        .url
      view.getElementsByClass("summary-list-remove-link").last().text()  must include(messages("site.remove"))
      view.getElementsByClass("summary-list-remove-link").last().attr("href") mustBe routes.PropertyAddedController
        .remove(1, NormalMode)
        .url
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

  }

}
