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
import forms.AccountingPeriodCTAddedFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.onshore.AccountingPeriodCTAddedView
import models.{CorporationTaxLiability, NormalMode}
import viewmodels.onshore.CorporationTaxLiabilityModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AccountingPeriodCTAddedViewSpec extends ViewSpecBase with ViewMatchers {

  val form = new AccountingPeriodCTAddedFormProvider()()
  val page: AccountingPeriodCTAddedView = inject[AccountingPeriodCTAddedView]

  val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

  val corporationTaxLiability: CorporationTaxLiability = CorporationTaxLiability(
    periodEnd = LocalDate.now(),
    howMuchIncome = BigInt(1),
    howMuchUnpaid = BigInt(1),
    howMuchInterest = BigInt(1),
    penaltyRate = 1,
    penaltyRateReason = "Some reason"
  )

  val corporationTaxLiability2: CorporationTaxLiability = CorporationTaxLiability(
    periodEnd = LocalDate.now(),
    howMuchIncome = BigInt(2),
    howMuchUnpaid = BigInt(2),
    howMuchInterest = BigInt(2),
    penaltyRate = 2,
    penaltyRateReason = "Some reason"
  )

  "view for a single corporation tax liability" should {

    val corporationTaxLiabilitiesSummaries = CorporationTaxLiabilityModel.row(Set(corporationTaxLiability), NormalMode)

    def createView: Html = page(form, corporationTaxLiabilitiesSummaries, NormalMode)(request, messages)
    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("accountingPeriodCTAdded.title.single"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("accountingPeriodCTAdded.heading.single")
    }

    "contain summary row" in {
      view.getElementsByClass("govuk-summary-list__only_key").text() mustBe s"Ending ${corporationTaxLiability.periodEnd.format(dateFormatter)}"
    }

    "contain remove link" in {
      view.getElementsByClass("summary-list-remove-link").first().text() must include(messages("site.remove"))
      view.getElementsByClass("summary-list-remove-link").last().text() must include(messages("site.remove"))
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

  }

  "view for a multiple corporation tax liability" should {

    val corporationTaxLiabilitiesSummaries = CorporationTaxLiabilityModel.row(Set(corporationTaxLiability, corporationTaxLiability2), NormalMode)

    def createView: Html = page(form, corporationTaxLiabilitiesSummaries, NormalMode)(request, messages)

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("accountingPeriodCTAdded.title.multi", 2))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("accountingPeriodCTAdded.title.multi", 2)
    }

    "contain summary row" in {
      view.getElementsByClass("govuk-summary-list__only_key").first().text() mustBe s"Ending ${corporationTaxLiability.periodEnd.format(dateFormatter)}"
      view.getElementsByClass("govuk-summary-list__only_key").last().text() mustBe s"Ending ${corporationTaxLiability2.periodEnd.format(dateFormatter)}"
    }

    "contain remove link" in {
      view.getElementsByClass("summary-list-remove-link").first().text() must include(messages("site.remove"))
      view.getElementsByClass("summary-list-remove-link").first().attr("href") mustBe routes.AccountingPeriodCTAddedController.remove(0, NormalMode).url
      view.getElementsByClass("summary-list-remove-link").last().text() must include(messages("site.remove"))
      view.getElementsByClass("summary-list-remove-link").last().attr("href") mustBe routes.AccountingPeriodCTAddedController.remove(1, NormalMode).url
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

  }

}