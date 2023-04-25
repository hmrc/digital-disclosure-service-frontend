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
import config.Country
import java.time.LocalDate

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
        PageWithValue(YouHaveNotIncludedTheTaxYearPage, "Some value"),
        PageWithValue(YouHaveNotSelectedCertainTaxYearPage, "Some value"),
        PageWithValue(TaxBeforeFiveYearsPage, "Some liabilities"),
        PageWithValue(TaxBeforeSevenYearsPage, "Some liabilities"),
        PageWithValue(TaxBeforeNineteenYearsPage, "Some liabilities"),
        PageWithValue(ContractualDisclosureFacilityPage, true),
        PageWithValue(TaxYearLiabilitiesPage, Map("2012" -> TaxYearWithLiabilities(TaxYearStarting(2012), liabilities))),
        PageWithValue(ForeignTaxCreditPage, Map("2012" -> BigInt(123))),
        PageWithValue(CountryOfYourOffshoreLiabilityPage, Map("GBR" -> Country("GBR", "United Kingdom"))),
        PageWithValue(YourLegalInterpretationPage, interpretationSet),
        PageWithValue(UnderWhatConsiderationPage, "Some interpretation"),
        PageWithValue(HowMuchTaxHasNotBeenIncludedPage, HowMuchTaxHasNotBeenIncluded.TenThousandOrLess),
        PageWithValue(TheMaximumValueOfAllAssetsPage, TheMaximumValueOfAllAssets.Below500k)
      )
      val userAnswers = PageWithValue.pagesToUserAnswers(pages, emptyUA).success.value
      val expected = OffshoreLiabilities(
        behaviour = Some(Set(WhyAreYouMakingThisDisclosure.DidNotNotifyHasExcuse)), 
        excuseForNotNotifying = Some(WhatIsYourReasonableExcuse("Some excuse", "Some years")), 
        reasonableCare = Some(WhatReasonableCareDidYouTake("Some excuse", "Some years")), 
        excuseForNotFiling = Some(WhatIsYourReasonableExcuseForNotFilingReturn("Some excuse", "Some years")), 
        whichYears = Some(yearsSet), 
        youHaveNotIncludedTheTaxYear = Some("Some value"),
        youHaveNotSelectedCertainTaxYears = Some("Some value"),
        taxBeforeFiveYears = Some("Some liabilities"),
        taxBeforeSevenYears = Some("Some liabilities"),
        taxBeforeNineteenYears = Some("Some liabilities"),
        disregardedCDF = Some(true),
        taxYearLiabilities = Some(Map("2012" -> TaxYearWithLiabilities(TaxYearStarting(2012), liabilities))),
        taxYearForeignTaxDeductions = Some(Map("2012" -> BigInt(123))),
        countryOfYourOffshoreLiability = Some(Map("GBR" -> Country("GBR", "United Kingdom"))),
        legalInterpretation = Some(interpretationSet),
        otherInterpretation = Some("Some interpretation"),
        notIncludedDueToInterpretation = Some(HowMuchTaxHasNotBeenIncluded.TenThousandOrLess),
        maximumValueOfAssets = Some(TheMaximumValueOfAllAssets.Below500k)
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
        PageWithValue(WhichTelephoneNumberCanWeContactYouWithPage, WhichTelephoneNumberCanWeContactYouWith.values.head),
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
        Some(WhichTelephoneNumberCanWeContactYouWith.values.head),
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

  "uaToOnshoreLiabilities" should {

    "populate OnshoreLiabilities with nothing if nothing is set" in {
      sut.uaToOnshoreLiabilities(emptyUA) shouldEqual Some(OnshoreLiabilities())
    }

    "populate OnshoreLiabilities with everything that is set" in {
      val date = LocalDate.now
      val liabilities = OnshoreTaxYearLiabilities(
        lettingIncome = Some(BigInt(2000)),
        gains = Some(BigInt(2000)),
        unpaidTax = BigInt(2000),
        niContributions = BigInt(2000),
        interest = BigInt(2000),
        penaltyRate = 12,
        penaltyRateReason = "Reason",
        undeclaredIncomeOrGain = Some("Income or gain"),
        residentialTaxReduction = Some(false)
      )
      val whySet: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHasExcuse)
      val yearsSet: Set[OnshoreYears] = Set(OnshoreYearStarting(2012))
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
      val whichLiabilitiesSet: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] = Set(WhatOnshoreLiabilitiesDoYouNeedToDisclose.BusinessIncome)
      val pages = List(
        PageWithValue(WhyAreYouMakingThisOnshoreDisclosurePage, whySet),
        PageWithValue(ReasonableExcuseOnshorePage, ReasonableExcuseOnshore("Some excuse", "Some years")),
        PageWithValue(ReasonableCareOnshorePage, ReasonableCareOnshore("Some excuse", "Some years")),
        PageWithValue(ReasonableExcuseForNotFilingOnshorePage, ReasonableExcuseForNotFilingOnshore("Some excuse", "Some years")),
        PageWithValue(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, whichLiabilitiesSet),
        PageWithValue(WhichOnshoreYearsPage, yearsSet),
        PageWithValue(NotIncludedSingleTaxYearPage, "Not included year"),
        PageWithValue(NotIncludedMultipleTaxYearsPage, "Not included years"),
        PageWithValue(TaxBeforeThreeYearsOnshorePage, "Some liabilities 1"),
        PageWithValue(TaxBeforeFiveYearsOnshorePage, "Some liabilities 2"),
        PageWithValue(TaxBeforeNineteenYearsOnshorePage, "Some liabilities 3"),
        PageWithValue(CDFOnshorePage, true),
        PageWithValue(OnshoreTaxYearLiabilitiesPage, Map("2012" -> OnshoreTaxYearWithLiabilities(OnshoreYearStarting(2012), liabilities))),
        PageWithValue(ResidentialReductionPage, Map("2012" -> BigInt(123))),
        PageWithValue(LettingPropertyPage, lettingProperty),
        PageWithValue(AreYouAMemberOfAnyLandlordAssociationsPage, true),
        PageWithValue(WhichLandlordAssociationsAreYouAMemberOfPage, "Some associations"),
        PageWithValue(HowManyPropertiesDoYouCurrentlyLetOutPage, "Some properties"),
        PageWithValue(CorporationTaxLiabilityPage, corporationTax),
        PageWithValue(DirectorLoanAccountLiabilitiesPage, directorLoan),
      )
      val userAnswers = PageWithValue.pagesToUserAnswers(pages, emptyUA).success.value
      val expected = OnshoreLiabilities(
        behaviour = Some(whySet), 
        excuseForNotNotifying = Some(ReasonableExcuseOnshore("Some excuse", "Some years")), 
        reasonableCare = Some(ReasonableCareOnshore("Some excuse", "Some years")), 
        excuseForNotFiling = Some(ReasonableExcuseForNotFilingOnshore("Some excuse", "Some years")), 
        whatLiabilities = Some(whichLiabilitiesSet),
        whichYears = Some(yearsSet), 
        youHaveNotIncludedTheTaxYear = Some("Not included year"),
        youHaveNotSelectedCertainTaxYears = Some("Not included years"),
        taxBeforeThreeYears = Some("Some liabilities 1"),
        taxBeforeFiveYears = Some("Some liabilities 2"),
        taxBeforeNineteenYears = Some("Some liabilities 3"),
        disregardedCDF = Some(true),
        taxYearLiabilities = Some(Map("2012" -> OnshoreTaxYearWithLiabilities(OnshoreYearStarting(2012), liabilities))),
        lettingDeductions = Some(Map("2012" -> BigInt(123))),
        lettingProperties = Some(lettingProperty),
        memberOfLandlordAssociations = Some(true),
        landlordAssociations = Some("Some associations"),
        howManyProperties = Some("Some properties"),
        corporationTaxLiabilities = Some(corporationTax),
        directorLoanAccountLiabilities = Some(directorLoan)
      )
      sut.uaToOnshoreLiabilities(userAnswers) shouldEqual Some(expected)
    }

  }

  "uaToFullDisclosure" should {

    "populate FullDisclosure" in {
      val result = sut.uaToFullDisclosure(emptyUA) 
      val expectedResult = FullDisclosure (
        userId = emptyUA.id,
        submissionId = emptyUA.submissionId,
        lastUpdated = emptyUA.lastUpdated,
        metadata = emptyUA.metadata,
        caseReference = CaseReference(),
        personalDetails = PersonalDetails(Background(), AboutYou()),
        onshoreLiabilities = Some(OnshoreLiabilities()),
        offshoreLiabilities = OffshoreLiabilities(),
        otherLiabilities = OtherLiabilities(),
        reasonForDisclosingNow = ReasonForDisclosingNow(),
        offerAmount = None,
        created = result.created
      )
      
      result shouldEqual expectedResult
    }

  }

}