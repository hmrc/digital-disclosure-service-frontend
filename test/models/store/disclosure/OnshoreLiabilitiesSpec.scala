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
import java.time.LocalDate

class OnshoreLiabilitiesSpec extends AnyFreeSpec with Matchers with OptionValues {

  val date = LocalDate.now
  val liabilities = OnshoreTaxYearLiabilities(
    lettingIncome = Some(BigInt(2000)),
    gains = Some(BigInt(2000)),
    unpaidTax = BigInt(2000),
    niContributions = BigInt(2000),
    interest = BigInt(2000),
    penaltyRate = 12,
    penaltyRateReason = "Reason",
    undeclaredIncomeOrGain = "Income or gain",
    residentialTaxReduction = Some(false)
  )
  val whySet: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHasExcuse)
  val corporationTax = Seq(CorporationTaxLiability (
    periodEnd = date,
    howMuchIncome = BigInt(2000),
    howMuchUnpaid = BigInt(2000),
    howMuchInterest = BigInt(2000),
    penaltyRate = 123,
    penaltyRateReason = "Some reason"
  ))
  val directorLoan = Seq(DirectorLoanAccountLiabilities (
    name = "Name",
    periodEnd = date,
    overdrawn = BigInt(2000),
    unpaidTax = BigInt(2000),
    interest = BigInt(2000),
    penaltyRate = 123,
    penaltyRateReason = "Some reason"
  ))
  val lettingProperty = Seq(LettingProperty(
    address = None,
    dateFirstLetOut = Some(date),
    stoppedBeingLetOut = Some(true),
    noLongerBeingLetOut = None,
    fhl = Some(false),
    isJointOwnership = Some(true),
    isMortgageOnProperty = Some(false),
    percentageIncomeOnProperty = Some(123),
    wasFurnished = Some(false),
    typeOfMortgage = None,
    otherTypeOfMortgage = Some("Some mortgage"),
    wasPropertyManagerByAgent = Some(true),
    didTheLettingAgentCollectRentOnYourBehalf = Some(false)
  ))

  "isComplete" - {

    "must return true where they have selected all types of tax" in {
      val onshoreLiabilities = OnshoreLiabilities(
        behaviour = Some(whySet),
        whatLiabilities = Some(WhatOnshoreLiabilitiesDoYouNeedToDisclose.values.toSet),
        whichYears = Some(Set(OnshoreYearStarting(2012))), 
        taxYearLiabilities = Some(Map("2012" -> OnshoreTaxYearWithLiabilities(OnshoreYearStarting(2012), liabilities))),
        lettingProperties = Some(lettingProperty),
        memberOfLandlordAssociations = Some(true),
        landlordAssociations = Some("Some associations"),
        howManyProperties = Some("Some properties"),
        corporationTaxLiabilities = Some(corporationTax),
        directorLoanAccountLiabilities = Some(directorLoan)
      )

      onshoreLiabilities.isComplete mustBe true
    }

    "must return true where they have only selected corporation tax" in {
      val onshoreLiabilities = OnshoreLiabilities(
        behaviour = Some(whySet),
        whatLiabilities = Some(Set(WhatOnshoreLiabilitiesDoYouNeedToDisclose.CorporationTax)),
        whichYears = None, 
        taxYearLiabilities = None,
        lettingProperties = None,
        memberOfLandlordAssociations = None,
        landlordAssociations = None,
        howManyProperties = None,
        corporationTaxLiabilities = Some(corporationTax),
        directorLoanAccountLiabilities = None
      )
      onshoreLiabilities.isComplete mustBe true
    }

    "must return true where they have only selected director loan" in {
      val onshoreLiabilities = OnshoreLiabilities(
        behaviour = Some(whySet),
        whatLiabilities = Some(Set(WhatOnshoreLiabilitiesDoYouNeedToDisclose.DirectorLoan)),
        whichYears = None, 
        taxYearLiabilities = None,
        lettingProperties = None,
        memberOfLandlordAssociations = None,
        landlordAssociations = None,
        howManyProperties = None,
        corporationTaxLiabilities = None,
        directorLoanAccountLiabilities = Some(directorLoan)
      )
      onshoreLiabilities.isComplete mustBe true
    }

    "must return true where they have only selected lettings" in {
      val onshoreLiabilities = OnshoreLiabilities(
        behaviour = Some(whySet),
        whatLiabilities = Some(Set(WhatOnshoreLiabilitiesDoYouNeedToDisclose.LettingIncome)),
        whichYears = Some(Set(OnshoreYearStarting(2012))), 
        taxYearLiabilities = Some(Map("2012" -> OnshoreTaxYearWithLiabilities(OnshoreYearStarting(2012), liabilities))),
        lettingProperties = Some(lettingProperty),
        memberOfLandlordAssociations = Some(true),
        landlordAssociations = Some("Some associations"),
        howManyProperties = Some("Some number of properties"),
        corporationTaxLiabilities = None,
        directorLoanAccountLiabilities = None
      )
      onshoreLiabilities.isComplete mustBe true
    }

    "must return true where they have only selected lettings but it's a nil disclosure" in {
      val onshoreLiabilities = OnshoreLiabilities(
        behaviour = Some(whySet),
        whatLiabilities = Some(Set(WhatOnshoreLiabilitiesDoYouNeedToDisclose.LettingIncome)),
        whichYears = Some(Set(PriorToThreeYears)), 
        taxYearLiabilities = None,
        lettingProperties = None,
        memberOfLandlordAssociations = Some(true),
        landlordAssociations = Some("Some associations"),
        howManyProperties = Some("Some number of properties"),
        corporationTaxLiabilities = None,
        directorLoanAccountLiabilities = None
      )
      onshoreLiabilities.isComplete mustBe true
    }

    "must return true where they have only selected business income" in {
      val onshoreLiabilities = OnshoreLiabilities(
        behaviour = Some(whySet),
        whatLiabilities = Some(Set(WhatOnshoreLiabilitiesDoYouNeedToDisclose.BusinessIncome)),
        whichYears = Some(Set(OnshoreYearStarting(2012))), 
        taxYearLiabilities = Some(Map("2012" -> OnshoreTaxYearWithLiabilities(OnshoreYearStarting(2012), liabilities)))
      )
      onshoreLiabilities.isComplete mustBe true
    }

    "must return false where they have not answered all necessary questions" in {
      val onshoreLiabilities = OnshoreLiabilities()
      onshoreLiabilities.isComplete mustBe false
    }

  }

  "taxYearQuestionsAnswered" - {

    "must return true where the actual tax years selected matches the tax years entered" in {
      val onshoreLiabilities = OnshoreLiabilities(
        behaviour = Some(whySet),
        whatLiabilities = Some(Set(WhatOnshoreLiabilitiesDoYouNeedToDisclose.LettingIncome)),
        whichYears = Some(Set(OnshoreYearStarting(2012))), 
        taxYearLiabilities = Some(Map("2012" -> OnshoreTaxYearWithLiabilities(OnshoreYearStarting(2012), liabilities))),
        lettingProperties = Some(lettingProperty),
        memberOfLandlordAssociations = Some(true),
        landlordAssociations = Some("Some associations"),
        howManyProperties = Some("Some number of properties"),
        corporationTaxLiabilities = None,
        directorLoanAccountLiabilities = None
      )
      onshoreLiabilities.taxYearQuestionsAnswered mustBe true
    }

    "must return true where the actual tax years selected matches the tax years entered and the user selected a before x year value" in {
      val onshoreLiabilities = OnshoreLiabilities(
        behaviour = Some(whySet),
        whatLiabilities = Some(Set(WhatOnshoreLiabilitiesDoYouNeedToDisclose.LettingIncome)),
        whichYears = Some(Set(OnshoreYearStarting(2012), PriorToThreeYears)), 
        taxYearLiabilities = Some(Map("2012" -> OnshoreTaxYearWithLiabilities(OnshoreYearStarting(2012), liabilities))),
        lettingProperties = Some(lettingProperty),
        memberOfLandlordAssociations = Some(true),
        landlordAssociations = Some("Some associations"),
        howManyProperties = Some("Some number of properties"),
        corporationTaxLiabilities = None,
        directorLoanAccountLiabilities = None
      )
      onshoreLiabilities.taxYearQuestionsAnswered mustBe true
    }

    "must return false where the number of years selected matches the number of liabilities years entered, but the years don't match" in {
      val onshoreLiabilities = OnshoreLiabilities(
        behaviour = Some(whySet),
        whatLiabilities = Some(Set(WhatOnshoreLiabilitiesDoYouNeedToDisclose.LettingIncome)),
        whichYears = Some(Set(OnshoreYearStarting(2012))), 
        taxYearLiabilities = Some(Map("2013" -> OnshoreTaxYearWithLiabilities(OnshoreYearStarting(2013), liabilities))),
        lettingProperties = Some(lettingProperty),
        memberOfLandlordAssociations = Some(true),
        landlordAssociations = Some("Some associations"),
        howManyProperties = Some("Some number of properties"),
        corporationTaxLiabilities = None,
        directorLoanAccountLiabilities = None
      )
      onshoreLiabilities.taxYearQuestionsAnswered mustBe false
    }

    "must return false where the years selected doesn't match the liabilities years entered" in {
      val onshoreLiabilities = OnshoreLiabilities(
        behaviour = Some(whySet),
        whatLiabilities = Some(Set(WhatOnshoreLiabilitiesDoYouNeedToDisclose.LettingIncome)),
        whichYears = Some(Set(OnshoreYearStarting(2012), OnshoreYearStarting(2013))), 
        taxYearLiabilities = Some(Map("2012" -> OnshoreTaxYearWithLiabilities(OnshoreYearStarting(2012), liabilities))),
        lettingProperties = Some(lettingProperty),
        memberOfLandlordAssociations = Some(true),
        landlordAssociations = Some("Some associations"),
        howManyProperties = Some("Some number of properties"),
        corporationTaxLiabilities = None,
        directorLoanAccountLiabilities = None
      )
      onshoreLiabilities.taxYearQuestionsAnswered mustBe false
    }

  }


}