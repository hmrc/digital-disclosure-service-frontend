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

package viewmodels.onshore

import base.SpecBase
import models._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalacheck.Arbitrary.arbitrary
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.Key
import viewmodels.govuk.summarylist._
import viewmodels.RevealFullText

import java.time.LocalDate

class CorporationTaxLiabilitiesSummaryViewModelSpec extends SpecBase with ScalaCheckPropertyChecks {

  lazy val app                = application
  implicit val mess: Messages = messages
  val revealFullText          = app.injector.instanceOf[RevealFullText]

  "penaltyAmount" - {

    "apply the penalty rate to the amount of unpaid tax" in {
      forAll(arbitrary[CorporationTaxLiability]) { corporationTaxLiability =>
        val penaltyRate    = corporationTaxLiability.penaltyRate
        val unpaidTax      = corporationTaxLiability.howMuchUnpaid
        val expectedAmount = (penaltyRate * BigDecimal(unpaidTax)) / 100
        new CorporationTaxLiabilitiesSummaryViewModelCreation(revealFullText).penaltyAmount(
          corporationTaxLiability
        ) mustEqual expectedAmount
      }
    }
  }

  "CorporationTaxLiabilitiesSummaryViewModel" - {

    "return an empty Seq where the director loan account pages isn't populated" in {
      val ua        = UserAnswers("id", "session-123")
      val viewModel = new CorporationTaxLiabilitiesSummaryViewModelCreation(revealFullText).create(ua)
      viewModel.corporationTaxLiabilitiesList mustEqual Nil
    }

    "return the corporation tax liability section if user have added one or more accounting details" in {
      val corporationTaxLiability = CorporationTaxLiability(
        periodEnd = LocalDate.now(),
        howMuchIncome = BigInt(0),
        howMuchUnpaid = BigInt(0),
        howMuchInterest = BigInt(0),
        penaltyRate = 0,
        penaltyRateReason = "reason"
      )

      val summaryList = new CorporationTaxLiabilitiesSummaryViewModelCreation(revealFullText)
        .corporationTaxLiabilityToSummaryList(0, corporationTaxLiability, revealFullText)

      summaryList.rows(1).key mustEqual Key(Text(mess("corporationTaxLiability.howMuchIncome.checkYourAnswersLabel")))
      summaryList.rows(1).value mustEqual ValueViewModel(HtmlContent(s"&pound;0"))

      summaryList.rows(2).key mustEqual Key(Text(mess("corporationTaxLiability.howMuchUnpaid.checkYourAnswersLabel")))
      summaryList.rows(2).value mustEqual ValueViewModel(HtmlContent(s"&pound;0"))

      summaryList.rows(3).key mustEqual Key(Text(mess("corporationTaxLiability.howMuchInterest.checkYourAnswersLabel")))
      summaryList.rows(3).value mustEqual ValueViewModel(HtmlContent(s"&pound;0"))

      summaryList.rows(4).key mustEqual Key(Text(mess("corporationTaxLiability.penaltyRate.checkYourAnswersLabel")))
      summaryList.rows(4).value mustEqual ValueViewModel(Text(s"0.00%"))

      summaryList.rows(5).key mustEqual Key(Text(mess("checkYourAnswers.ct.total.penaltyAmount")))
      summaryList.rows(5).value mustEqual ValueViewModel(HtmlContent(s"&pound;0.00"))

      summaryList.rows(6).key mustEqual Key(
        Text(mess("corporationTaxLiability.penaltyRateReason.checkYourAnswersLabel"))
      )
      summaryList.rows(6).value mustEqual ValueViewModel(Text("reason"))

    }

    "return an empty total section where the director loan account pages isn't populated" in {
      val ua        = UserAnswers("id", "session-123")
      val viewModel = new CorporationTaxLiabilitiesSummaryViewModelCreation(revealFullText).create(ua)

      viewModel.totalAmountsList.rows(0).key mustEqual Key(Text(mess("checkYourAnswers.ct.total.taxDue")))
      viewModel.totalAmountsList.rows(0).value mustEqual ValueViewModel(HtmlContent(s"&pound;0"))

      viewModel.totalAmountsList.rows(1).key mustEqual Key(Text(mess("checkYourAnswers.ct.total.interestDue")))
      viewModel.totalAmountsList.rows(1).value mustEqual ValueViewModel(HtmlContent(s"&pound;0"))

      viewModel.totalAmountsList.rows(2).key mustEqual Key(Text(mess("checkYourAnswers.ct.total.penaltyAmount")))
      viewModel.totalAmountsList.rows(2).value mustEqual ValueViewModel(HtmlContent(s"&pound;0.00"))

      viewModel.totalAmountsList.rows(3).key mustEqual Key(Text(mess("checkYourAnswers.ct.total.totalAmountDue")))
      viewModel.totalAmountsList.rows(3).value mustEqual ValueViewModel(HtmlContent(s"&pound;0.00"))
    }
  }
}
