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

package views.offshore

import base.ViewSpecBase
import forms.TaxYearLiabilitiesFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.offshore.TaxYearLiabilitiesView
import models.NormalMode

class TaxYearLiabilitiesViewSpec extends ViewSpecBase with ViewMatchers {

  val form = new TaxYearLiabilitiesFormProvider()()
  val page: TaxYearLiabilitiesView = inject[TaxYearLiabilitiesView]

  private def createView: Html = page(form, NormalMode, 0, 2021)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("taxYearLiabilities.title", "2021", "2022"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("taxYearLiabilities.heading", "2021", "2022")
    }

    "contain a body" in {
      view.getElementById("guidance").text() mustBe s"${messages("taxYearLiabilities.body")} ${messages("taxYearLiabilities.link")}"
    }

    "contain the income question" in {
      view.getElementsByClass("govuk-label--m").get(0).text() mustBe messages("taxYearLiabilities.income.question", "2022")
    }

    "contain the chargeableTransfers question" in {
      view.getElementsByClass("govuk-label--m").get(1).text() mustBe messages("taxYearLiabilities.chargeableTransfers.question", "2022")
    }

    "contain the capitalGains question" in {
      view.getElementsByClass("govuk-label--m").get(2).text() mustBe messages("taxYearLiabilities.capitalGains.question", "2022")
    }

    "contain the unpaidTax question" in {
      view.getElementsByClass("govuk-label--m").get(3).text() mustBe messages("taxYearLiabilities.unpaidTax.question", "2022")
    }

    "contain the interest question" in {
      view.getElementsByClass("govuk-label--m").get(4).text() mustBe messages("taxYearLiabilities.interest.question", "2022")
    }

    "contain the penaltyRate question" in {
      view.getElementsByClass("govuk-label--m").get(5).text() mustBe messages("taxYearLiabilities.penaltyRate.question", "2022")
    }

    "contain the foreignTaxCredit question" in {
      view.getElementsByClass("govuk-fieldset__legend--m").text() mustBe messages("taxYearLiabilities.foreignTaxCredit.question", "2022")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

}

}