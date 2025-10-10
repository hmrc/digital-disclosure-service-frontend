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
import forms.OnshoreTaxYearLiabilitiesFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.onshore.OnshoreTaxYearLiabilitiesView
import models.{NormalMode, WhatOnshoreLiabilitiesDoYouNeedToDisclose}

class OnshoreTaxYearLiabilitiesViewSpec extends ViewSpecBase with ViewMatchers {

  def form(taxTypes: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose]) = new OnshoreTaxYearLiabilitiesFormProvider()(
    taxTypes
  )
  val page: OnshoreTaxYearLiabilitiesView                            = inject[OnshoreTaxYearLiabilitiesView]

  private def createView(taxTypes: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose]): Html =
    page(form(taxTypes), NormalMode, 0, 2021, taxTypes)(request, messages)

  "view with no types selected" should {

    val view = createView(Set())

    "have title" in {
      view.select("title").text() must include(messages("onshoreTaxYearLiabilities.title", "2021", "2022"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages(
        "onshoreTaxYearLiabilities.heading",
        "2021",
        "2022"
      )
    }

    "contain a body" in {
      view
        .getElementById("guidance")
        .text() mustBe s"${messages("onshoreTaxYearLiabilities.body")} ${messages("onshoreTaxYearLiabilities.link")}${messages("site.dot")}"
    }

    "contain the unpaidTax question" in {
      view.getElementsByClass("govuk-label--m").get(0).text() mustBe messages(
        "onshoreTaxYearLiabilities.unpaidTax.question",
        "2022"
      )
    }

    "contain the niContributions question" in {
      view.getElementsByClass("govuk-label--m").get(1).text() mustBe messages(
        "onshoreTaxYearLiabilities.niContributions.question",
        "2022"
      )
    }

    "contain the interest question" in {
      view.getElementsByClass("govuk-label--m").get(2).text() mustBe messages(
        "onshoreTaxYearLiabilities.interest.question",
        "2022"
      )
    }

    "contain the penaltyRate question" in {
      view.getElementsByClass("govuk-label--m").get(3).text() mustBe messages(
        "onshoreTaxYearLiabilities.penaltyRate.question",
        "2022"
      )
    }

    "contain the undeclaredIncomeOrGain question" in {
      view.getElementsByClass("govuk-label--m").get(5).text() mustBe messages(
        "onshoreTaxYearLiabilities.undeclaredIncomeOrGain.question"
      )
    }

    "contain the undeclaredIncomeOrGain hint" in {
      view.getElementById("undeclaredIncomeOrGain-hint").text() mustBe messages(
        "onshoreTaxYearLiabilities.undeclaredIncomeOrGain.hint"
      )
    }

    "display the continue button" in {
      view.getElementById("continue").text() mustBe messages("site.saveAndContinue")
    }

  }

  "view with all types selected" should {

    val view = createView(WhatOnshoreLiabilitiesDoYouNeedToDisclose.values.toSet)

    "have title" in {
      view.select("title").text() must include(messages("onshoreTaxYearLiabilities.title", "2021", "2022"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages(
        "onshoreTaxYearLiabilities.heading",
        "2021",
        "2022"
      )
    }

    "contain a body" in {
      view
        .getElementById("guidance")
        .text() mustBe s"${messages("onshoreTaxYearLiabilities.body")} ${messages("onshoreTaxYearLiabilities.link")}${messages("site.dot")}"
    }

    "contain the nonBusinessIncome question" in {
      view.getElementsByClass("govuk-label--m").get(0).text() mustBe messages(
        "onshoreTaxYearLiabilities.nonBusinessIncome.question",
        "2022"
      )
    }

    "contain the businessIncome question" in {
      view.getElementsByClass("govuk-label--m").get(1).text() mustBe messages(
        "onshoreTaxYearLiabilities.businessIncome.question",
        "2022"
      )
    }

    "contain the lettingIncome question" in {
      view.getElementsByClass("govuk-label--m").get(2).text() mustBe messages(
        "onshoreTaxYearLiabilities.lettingIncome.question",
        "2022"
      )
    }

    "contain the gains question" in {
      view.getElementsByClass("govuk-label--m").get(3).text() mustBe messages(
        "onshoreTaxYearLiabilities.gains.question",
        "2022"
      )
    }

    "contain the unpaidTax question" in {
      view.getElementsByClass("govuk-label--m").get(4).text() mustBe messages(
        "onshoreTaxYearLiabilities.unpaidTax.question",
        "2022"
      )
    }

    "contain the niContributions question" in {
      view.getElementsByClass("govuk-label--m").get(5).text() mustBe messages(
        "onshoreTaxYearLiabilities.niContributions.question",
        "2022"
      )
    }

    "contain the interest question" in {
      view.getElementsByClass("govuk-label--m").get(6).text() mustBe messages(
        "onshoreTaxYearLiabilities.interest.question",
        "2022"
      )
    }

    "contain the penaltyRate question" in {
      view.getElementsByClass("govuk-label--m").get(7).text() mustBe messages(
        "onshoreTaxYearLiabilities.penaltyRate.question",
        "2022"
      )
    }

    "display the continue button" in {
      view.getElementById("continue").text() mustBe messages("site.saveAndContinue")
    }

  }

}
