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

package services

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.TryValues
import models.store.FullDisclosure
import models.store.notification.{Background, PersonalDetails, AboutYou}
import models.store.disclosure._
import pages._
import models._

class UAToDisclosureServiceSpec extends AnyWordSpec with Matchers with TryValues {

  val emptyUA = UserAnswers("id")

  val notificationService = new UAToNotificationServiceImpl
  val sut = new UAToDisclosureServiceImpl(notificationService)

  "uaToOtherLiabilities" should {

    "populate OtherLiabilities with nothing if nothing is set" in {
      sut.uaToOtherLiabilities(emptyUA) shouldEqual OtherLiabilities()
    }

    "populate OtherLiabilities with everything that is set" in {
      val otherIssuesSet: Set[OtherLiabilityIssues] = Set(OtherLiabilityIssues.Other, OtherLiabilityIssues.InheritanceTaxIssues)
      val pages = List(
        PageWithValue(OtherLiabilityIssuesPage, otherIssuesSet),
        PageWithValue(DescribeTheGiftPage, "Some gift"),
        PageWithValue(WhatOtherLiabilityIssuesPage, "Some issue"),
        PageWithValue(DidYouReceiveTaxCreditPage, false)
      )
      val userAnswers = PageWithValue.pagesToUserAnswers(pages, emptyUA).success.value
      val expected = OtherLiabilities(
        Some(otherIssuesSet), 
        Some("Some gift"), 
        Some("Some issue"), 
        Some(false)
      )
      sut.uaToOtherLiabilities(userAnswers) shouldEqual expected
    }

  }

  "uaToOffshoreLiabilities" should {

    "populate OffshoreLiabilities with nothing if nothing is set" in {
      sut.uaToOffshoreLiabilities(emptyUA) shouldEqual OffshoreLiabilities()
    }

    "populate OffshoreLiabilities with everything that is set" in {
      val liabilities = TaxYearLiabilities(
        income = BigInt(2000),
        chargeableTransfers = BigInt(2000),
        capitalGains = BigInt(2000),
        unpaidTax = BigInt(2000),
        interest = BigInt(2000),
        penaltyRate = 12,
        penaltyRateReason = "Reason",
        foreignTaxCredit = false
      )
      val whySet: Set[WhyAreYouMakingThisDisclosure] = Set(WhyAreYouMakingThisDisclosure.DidNotNotifyHasExcuse)
      val yearsSet: Set[OffshoreYears] = Set(TaxYearStarting(2012))
      val interpretationSet: Set[YourLegalInterpretation] = Set(YourLegalInterpretation.AnotherIssue)
      val pages = List(
        PageWithValue(WhyAreYouMakingThisDisclosurePage, whySet),
        PageWithValue(WhatIsYourReasonableExcusePage, WhatIsYourReasonableExcuse("Some excuse", "Some years")),
        PageWithValue(WhatReasonableCareDidYouTakePage, WhatReasonableCareDidYouTake("Some excuse", "Some years")),
        PageWithValue(WhatIsYourReasonableExcuseForNotFilingReturnPage, WhatIsYourReasonableExcuseForNotFilingReturn("Some excuse", "Some years")),
        PageWithValue(WhichYearsPage, yearsSet),
        PageWithValue(YouHaveNotIncludedTheTaxYearPage, "Some Value"),
        PageWithValue(YouHaveNotSelectedCertainTaxYearPage, "Some Value"),
        PageWithValue(TaxBeforeFiveYearsPage, "Some liabilities"),
        PageWithValue(TaxBeforeSevenYearsPage, "Some liabilities"),
        PageWithValue(TaxYearLiabilitiesPage, Map("2012" -> TaxYearWithLiabilities(TaxYearStarting(2012), liabilities))),
        PageWithValue(YourLegalInterpretationPage, interpretationSet),
        PageWithValue(UnderWhatConsiderationPage, "Some interpretation"),
        PageWithValue(HowMuchTaxHasNotBeenIncludedPage, HowMuchTaxHasNotBeenIncluded.TenThousandOrLess),
        PageWithValue(TheMaximumValueOfAllAssetsPage, TheMaximumValueOfAllAssets.TenThousandOrLess)
      )
      val userAnswers = PageWithValue.pagesToUserAnswers(pages, emptyUA).success.value
      val expected = OffshoreLiabilities(
        Some(whySet), 
        Some(WhatIsYourReasonableExcuse("Some excuse", "Some years")), 
        Some(WhatReasonableCareDidYouTake("Some excuse", "Some years")), 
        Some(WhatIsYourReasonableExcuseForNotFilingReturn("Some excuse", "Some years")), 
        Some(yearsSet),
        Some("Some Value"),
        Some("Some Value"), 
        Some("Some liabilities"),
        Some("Some liabilities"),
        Some(Map("2012" -> TaxYearWithLiabilities(TaxYearStarting(2012), liabilities))),
        Some(interpretationSet),
        Some("Some interpretation"),
        Some(HowMuchTaxHasNotBeenIncluded.TenThousandOrLess),
        Some(TheMaximumValueOfAllAssets.TenThousandOrLess)
      )
      sut.uaToOffshoreLiabilities(userAnswers) shouldEqual expected
    }

  }

  "uaToReasonForDisclosingNow" should {

    "populate ReasonForDisclosingNow with nothing if nothing is set" in {
      sut.uaToReasonForDisclosingNow(emptyUA) shouldEqual ReasonForDisclosingNow()
    }

    "populate ReasonForDisclosingNow with everything that is set" in {
      val reasonSet: Set[WhyAreYouMakingADisclosure] = Set(WhyAreYouMakingADisclosure.GovUkGuidance)
      val pages = List(
        PageWithValue(WhyAreYouMakingADisclosurePage, reasonSet),
        PageWithValue(WhatIsTheReasonForMakingADisclosureNowPage, "Some other"),
        PageWithValue(WhyNotBeforeNowPage, "Some reason"),
        PageWithValue(DidSomeoneGiveYouAdviceNotDeclareTaxPage, true),
        PageWithValue(PersonWhoGaveAdvicePage, "Some guy"),
        PageWithValue(AdviceBusinessesOrOrgPage, true),
        PageWithValue(AdviceBusinessNamePage, "Some business"),
        PageWithValue(AdviceProfessionPage, "Some profession"),
        PageWithValue(AdviceGivenPage, AdviceGiven("Some advice", MonthYear(12, 2012), AdviceContactPreference.No)),
        PageWithValue(WhichEmailAddressCanWeContactYouWithPage, WhichEmailAddressCanWeContactYouWith.values.head),
        PageWithValue(CanWeUseTelephoneNumberToContactYouPage, false),
        PageWithValue(WhatEmailAddressCanWeContactYouWithPage, "Email"),
        PageWithValue(WhatTelephoneNumberCanWeContactYouWithPage, "Telephone"),
      )
      val userAnswers = PageWithValue.pagesToUserAnswers(pages, emptyUA).success.value
      val expected = ReasonForDisclosingNow(
        Some(reasonSet), 
        Some("Some other"), 
        Some("Some reason"), 
        Some(true), 
        Some("Some guy"), 
        Some(true), 
        Some("Some business"), 
        Some("Some profession"),
        Some(AdviceGiven("Some advice", MonthYear(12, 2012), AdviceContactPreference.No)),
        Some(WhichEmailAddressCanWeContactYouWith.values.head),
        Some(false),
        Some("Email"),
        Some("Telephone")
      )
      sut.uaToReasonForDisclosingNow(userAnswers) shouldEqual expected
    }

  }

  "uaToCaseReference" should {

    "populate CaseReference with nothing if nothing is set" in {
      sut.uaToCaseReference(emptyUA) shouldEqual CaseReference()
    }

    "populate CaseReference with everything that is set" in {
      val pages = List(
        PageWithValue(DoYouHaveACaseReferencePage, true),
        PageWithValue(WhatIsTheCaseReferencePage, "CSFF-1234567")
      )
      val userAnswers = PageWithValue.pagesToUserAnswers(pages, emptyUA).success.value
      val expected = CaseReference(
        Some(true), 
        Some("CSFF-1234567")
      )
      sut.uaToCaseReference(userAnswers) shouldEqual expected
    }

  }

  "uaToFullDisclosure" should {

    "populate FullDisclosure" in {
      sut.uaToFullDisclosure(emptyUA) shouldEqual FullDisclosure (
        userId = emptyUA.id,
        submissionId = emptyUA.submissionId,
        lastUpdated = emptyUA.lastUpdated,
        metadata = emptyUA.metadata,
        caseReference = CaseReference(),
        personalDetails = PersonalDetails(Background(), AboutYou()),
        offshoreLiabilities = OffshoreLiabilities(),
        otherLiabilities = OtherLiabilities(),
        reasonForDisclosingNow = ReasonForDisclosingNow()
      )
    }

  }

}