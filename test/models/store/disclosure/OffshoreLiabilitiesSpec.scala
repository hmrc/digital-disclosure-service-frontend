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

package models.store.disclosure

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import models._

class OffshoreLiabilitiesSpec extends AnyFreeSpec with Matchers with OptionValues {

  "isComplete" - {

    "must return true where they have answered necessary questions" in {
      val liabilities         = TaxYearLiabilities(
        income = BigInt(2000),
        chargeableTransfers = BigInt(2000),
        capitalGains = BigInt(2000),
        unpaidTax = BigInt(2000),
        interest = BigInt(2000),
        penaltyRate = 12,
        penaltyRateReason = "Reason",
        undeclaredIncomeOrGain = Some("Income or gain"),
        foreignTaxCredit = false
      )
      val offshoreLiabilities = OffshoreLiabilities(
        behaviour = Some(Set(WhyAreYouMakingThisDisclosure.DidNotNotifyHasExcuse)),
        whichYears = Some(Set(TaxYearStarting(2012))),
        taxYearLiabilities = Some(Map("2012" -> TaxYearWithLiabilities(TaxYearStarting(2012), liabilities))),
        countryOfYourOffshoreLiability = Some(Map()),
        legalInterpretation = Some(Set(YourLegalInterpretation.NoExclusion)),
        maximumValueOfAssets = Some(TheMaximumValueOfAllAssets.Below500k)
      )

      offshoreLiabilities.isComplete mustBe true
    }

    "must return true where they have answered all questions" in {
      val liabilities         = TaxYearLiabilities(
        income = BigInt(2000),
        chargeableTransfers = BigInt(2000),
        capitalGains = BigInt(2000),
        unpaidTax = BigInt(2000),
        interest = BigInt(2000),
        penaltyRate = 12,
        penaltyRateReason = "Reason",
        foreignTaxCredit = false
      )
      val offshoreLiabilities = OffshoreLiabilities(
        behaviour = Some(Set(WhyAreYouMakingThisDisclosure.DidNotNotifyHasExcuse)),
        excuseForNotNotifying = Some(WhatIsYourReasonableExcuse("Some excuse", "Some years")),
        reasonableCare = Some(WhatReasonableCareDidYouTake("Some excuse", "Some years")),
        excuseForNotFiling = Some(WhatIsYourReasonableExcuseForNotFilingReturn("Some excuse", "Some years")),
        whichYears = Some(Set(TaxYearStarting(2012))),
        youHaveNotIncludedTheTaxYear = Some("You have not included the tax year"),
        youHaveNotSelectedCertainTaxYears = Some("You have not selected certain tax years"),
        taxBeforeFiveYears = Some("Some liabilities"),
        taxBeforeSevenYears = Some("Some liabilities"),
        taxBeforeNineteenYears = Some("Some liabilities"),
        taxYearLiabilities = Some(Map("2012" -> TaxYearWithLiabilities(TaxYearStarting(2012), liabilities))),
        countryOfYourOffshoreLiability = Some(Map()),
        legalInterpretation = Some(Set(YourLegalInterpretation.AnotherIssue)),
        otherInterpretation = Some("Some interpretation"),
        notIncludedDueToInterpretation = Some(HowMuchTaxHasNotBeenIncluded.TenThousandOrLess),
        maximumValueOfAssets = Some(TheMaximumValueOfAllAssets.Below500k)
      )
      offshoreLiabilities.isComplete mustBe true
    }

    "must return false where they have not answered all necessary questions" in {
      val offshoreLiabilities = OffshoreLiabilities()
      offshoreLiabilities.isComplete mustBe false
    }

  }
}
