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
import models.store.disclosure._
import models.store._
import models.store.notification._
import models.store.Metadata
import pages._
import models._
import org.scalatest.TryValues
import java.time.{LocalDate, LocalDateTime, Instant}
import config.Country

class DisclosureToUAServiceSpec extends AnyWordSpec with Matchers with TryValues {

  val notificationToUaService = new NotificationToUAServiceImpl
  val sut = new DisclosureToUAServiceImpl(notificationToUaService)

  val testNotification = Notification("userId", "submissionId", Instant.now(), Metadata(), PersonalDetails(Background(), AboutYou()))

  val emptyUA = UserAnswers("id", "session-123")

  "otherLiabilitiesToUa" should {

    "return no PageWithValues for an empty OtherLiabilities" in {
      val otherLiabilities = OtherLiabilities()
      val updatedUserAnswers = sut.otherLiabilitiesToUa(otherLiabilities, emptyUA).success.value
      updatedUserAnswers.get(OtherLiabilityIssuesPage)                           shouldEqual None
      updatedUserAnswers.get(DescribeTheGiftPage)                                shouldEqual None
      updatedUserAnswers.get(WhatOtherLiabilityIssuesPage)                       shouldEqual None
      updatedUserAnswers.get(DidYouReceiveTaxCreditPage)                         shouldEqual None
    }

    "return a PageWithValue for all that are set" in {
      val otherIssuesSet: Set[OtherLiabilityIssues] = Set(OtherLiabilityIssues.Other, OtherLiabilityIssues.InheritanceTaxIssues)
      val otherLiabilities = OtherLiabilities(
        Some(otherIssuesSet), 
        Some("Some gift"), 
        Some("Some issue"), 
        Some(false)
      )
      val updatedUserAnswers = sut.otherLiabilitiesToUa(otherLiabilities, emptyUA).success.value
      updatedUserAnswers.get(OtherLiabilityIssuesPage)                           shouldEqual Some(otherIssuesSet)
      updatedUserAnswers.get(DescribeTheGiftPage)                                shouldEqual Some("Some gift")
      updatedUserAnswers.get(WhatOtherLiabilityIssuesPage)                       shouldEqual Some("Some issue")
      updatedUserAnswers.get(DidYouReceiveTaxCreditPage)                         shouldEqual Some(false)
    }
  }

  "offshoreLiabilitiesToUa" should {

    "return no PageWithValues for an empty OffshoreLiabilities" in {
      val otherLiabilities = OffshoreLiabilities()
      val updatedUserAnswers = sut.offshoreLiabilitiesToUa(otherLiabilities, emptyUA).success.value
      updatedUserAnswers.get(OtherLiabilityIssuesPage)                           shouldEqual None
      updatedUserAnswers.get(DescribeTheGiftPage)                                shouldEqual None
      updatedUserAnswers.get(WhatOtherLiabilityIssuesPage)                       shouldEqual None
      updatedUserAnswers.get(DidYouReceiveTaxCreditPage)                         shouldEqual None
    }

    "return a PageWithValue for all that are set" in {
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
      val offshoreLiabilities = OffshoreLiabilities(
        behaviour = Some(whySet), 
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
      val updatedUserAnswers = sut.offshoreLiabilitiesToUa(offshoreLiabilities, emptyUA).success.value
      updatedUserAnswers.get(WhyAreYouMakingThisDisclosurePage)                      shouldEqual Some(whySet)
      updatedUserAnswers.get(WhatIsYourReasonableExcusePage)                         shouldEqual Some(WhatIsYourReasonableExcuse("Some excuse", "Some years"))
      updatedUserAnswers.get(WhatReasonableCareDidYouTakePage)                       shouldEqual Some(WhatReasonableCareDidYouTake("Some excuse", "Some years"))
      updatedUserAnswers.get(WhatIsYourReasonableExcuseForNotFilingReturnPage)       shouldEqual Some(WhatIsYourReasonableExcuseForNotFilingReturn("Some excuse", "Some years"))
      updatedUserAnswers.get(WhichYearsPage)                                         shouldEqual Some(yearsSet)
      updatedUserAnswers.get(YouHaveNotIncludedTheTaxYearPage)                       shouldEqual Some("Some value")
      updatedUserAnswers.get(YouHaveNotSelectedCertainTaxYearPage)                   shouldEqual Some("Some value")
      updatedUserAnswers.get(TaxBeforeFiveYearsPage)                                 shouldEqual Some("Some liabilities")
      updatedUserAnswers.get(TaxBeforeSevenYearsPage)                                shouldEqual Some("Some liabilities")
      updatedUserAnswers.get(TaxBeforeNineteenYearsPage)                             shouldEqual Some("Some liabilities") 
      updatedUserAnswers.get(ContractualDisclosureFacilityPage)                      shouldEqual Some(true)
      updatedUserAnswers.get(TaxYearLiabilitiesPage)                                 shouldEqual Some(Map("2012" -> TaxYearWithLiabilities(TaxYearStarting(2012), liabilities)))
      updatedUserAnswers.get(ForeignTaxCreditPage)                                   shouldEqual Some(Map("2012" -> BigInt(123)))
      updatedUserAnswers.get(CountryOfYourOffshoreLiabilityPage)                     shouldEqual Some(Map("GBR" -> Country("GBR", "United Kingdom")))
      updatedUserAnswers.get(YourLegalInterpretationPage)                            shouldEqual Some(interpretationSet)
      updatedUserAnswers.get(UnderWhatConsiderationPage)                             shouldEqual Some("Some interpretation")
      updatedUserAnswers.get(HowMuchTaxHasNotBeenIncludedPage)                       shouldEqual Some(HowMuchTaxHasNotBeenIncluded.TenThousandOrLess)
      updatedUserAnswers.get(TheMaximumValueOfAllAssetsPage)                         shouldEqual Some(TheMaximumValueOfAllAssets.Below500k)
    }
  }

  "reasonForDisclosingNowToUa" should {

    "return no PageWithValues for an empty ReasonForDisclosingNow" in {
      val reasonForDisclosingNow = ReasonForDisclosingNow()
      val updatedUserAnswers = sut.reasonForDisclosingNowToUa(reasonForDisclosingNow, emptyUA).success.value
      updatedUserAnswers.get(OtherLiabilityIssuesPage)                           shouldEqual None
      updatedUserAnswers.get(DescribeTheGiftPage)                                shouldEqual None
      updatedUserAnswers.get(WhatOtherLiabilityIssuesPage)                       shouldEqual None
      updatedUserAnswers.get(DidYouReceiveTaxCreditPage)                         shouldEqual None
    }

    "return a PageWithValue for all that are set" in {
      val reasonSet: Set[WhyAreYouMakingADisclosure] = Set(WhyAreYouMakingADisclosure.GovUkGuidance)
      val reasonForDisclosingNow = ReasonForDisclosingNow(
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
      val updatedUserAnswers = sut.reasonForDisclosingNowToUa(reasonForDisclosingNow, emptyUA).success.value
      updatedUserAnswers.get(WhyAreYouMakingADisclosurePage)                       shouldEqual Some(reasonSet)
      updatedUserAnswers.get(WhatIsTheReasonForMakingADisclosureNowPage)           shouldEqual Some("Some other")
      updatedUserAnswers.get(WhyNotBeforeNowPage)                                  shouldEqual Some("Some reason")
      updatedUserAnswers.get(DidSomeoneGiveYouAdviceNotDeclareTaxPage)             shouldEqual Some(true)
      updatedUserAnswers.get(PersonWhoGaveAdvicePage)                              shouldEqual Some("Some guy")
      updatedUserAnswers.get(AdviceBusinessesOrOrgPage)                            shouldEqual Some(true)
      updatedUserAnswers.get(AdviceBusinessNamePage)                               shouldEqual Some("Some business")
      updatedUserAnswers.get(AdviceProfessionPage)                                 shouldEqual Some("Some profession")
      updatedUserAnswers.get(AdviceGivenPage)                                      shouldEqual Some(AdviceGiven("Some advice", MonthYear(12, 2012), AdviceContactPreference.No))
      updatedUserAnswers.get(WhichEmailAddressCanWeContactYouWithPage)             shouldEqual Some(WhichEmailAddressCanWeContactYouWith.values.head)
      updatedUserAnswers.get(WhichTelephoneNumberCanWeContactYouWithPage)          shouldEqual Some(WhichTelephoneNumberCanWeContactYouWith.values.head)
      updatedUserAnswers.get(WhatEmailAddressCanWeContactYouWithPage)              shouldEqual Some("Email")
      updatedUserAnswers.get(WhatTelephoneNumberCanWeContactYouWithPage)           shouldEqual Some("Telephone")
    }
  }

  "caseReferenceToUa" should {

    "return no PageWithValues for an empty ReasonForDisclosingNow" in {
      val model = CaseReference()
      val updatedUserAnswers = sut.caseReferenceToUa(model, emptyUA).success.value
      updatedUserAnswers.get(DoYouHaveACaseReferencePage)                           shouldEqual None
      updatedUserAnswers.get(WhatIsTheCaseReferencePage)                            shouldEqual None

    }

    "return a PageWithValue for all that are set" in {
      val model = CaseReference(Some(true), Some("Some reference"))
      val updatedUserAnswers = sut.caseReferenceToUa(model, emptyUA).success.value
      updatedUserAnswers.get(DoYouHaveACaseReferencePage)                           shouldEqual Some(true)
      updatedUserAnswers.get(WhatIsTheCaseReferencePage)                            shouldEqual Some("Some reference")
    }
  }

  "initialiseUserAnswers" should {
    "retrieve the userId, submissionId and lastUpdated from the notification" in {
      val instant = Instant.now()
      val metadata = Metadata(reference = Some("123"), submissionTime = Some(LocalDateTime.now))
      val disclosure = FullDisclosure("This user Id", "Some notification Id", instant, metadata, CaseReference(), PersonalDetails(Background(), AboutYou()), None, OffshoreLiabilities(), OtherLiabilities(), ReasonForDisclosingNow())
      
      val result = sut.initialiseUserAnswers("session-123", disclosure)
      val expectedResult = UserAnswers(id = "This user Id", sessionId = "session-123", submissionId = "Some notification Id", submissionType = SubmissionType.Disclosure, lastUpdated = instant, metadata = metadata, created = result.created)

      result shouldEqual expectedResult
    }
  }

  "fullDisclosureToUa" should {
    "populate UA with everything within the FullDisclosure object" in {
      val instant = Instant.now()
      val metadata = Metadata(reference = Some("123"), submissionTime = Some(LocalDateTime.now))

      val caseReference = CaseReference(Some(true), Some("Some reference"))
      val background = Background(relatesTo = Some(RelatesTo.AnIndividual))
      val whySet: Set[WhyAreYouMakingThisDisclosure] = Set(WhyAreYouMakingThisDisclosure.DidNotNotifyHasExcuse)
      val offshoreLiabilities = OffshoreLiabilities(Some(whySet))
      val whyOnlineSet: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHasExcuse)
      val onshoreLiabilities = Some(OnshoreLiabilities(Some(whyOnlineSet)))
      val otherLiabilities = OtherLiabilities(None, Some("Some gift"), None, None)
      val reasonSet: Set[WhyAreYouMakingADisclosure] = Set(WhyAreYouMakingADisclosure.GovUkGuidance)
      val reasonForDisclosingNow = ReasonForDisclosingNow(Some(reasonSet))
      val disclosure = FullDisclosure("This user Id", "Some notification Id", instant, metadata, caseReference, PersonalDetails(background, AboutYou()), onshoreLiabilities, offshoreLiabilities, otherLiabilities, reasonForDisclosingNow)

      val updatedUserAnswers = sut.fullDisclosureToUa("session-123", disclosure).success.value

      updatedUserAnswers.id shouldEqual "This user Id"
      updatedUserAnswers.submissionId shouldEqual "Some notification Id"
      updatedUserAnswers.submissionType shouldEqual SubmissionType.Disclosure
      updatedUserAnswers.lastUpdated shouldEqual instant
      updatedUserAnswers.metadata shouldEqual metadata

      updatedUserAnswers.get(DescribeTheGiftPage)                                   shouldEqual Some("Some gift")
      updatedUserAnswers.get(DoYouHaveACaseReferencePage)                           shouldEqual Some(true)
      updatedUserAnswers.get(WhyAreYouMakingADisclosurePage)                        shouldEqual Some(reasonSet)
      updatedUserAnswers.get(WhyAreYouMakingThisDisclosurePage)                     shouldEqual Some(whySet)
    }
  }

  "onshoreLiabilitiesToUa" should {

    "return no PageWithValues for an empty OffshoreLiabilities" in {
      val onshoreLiabilities = Some(OnshoreLiabilities())
      val updatedUserAnswers = sut.onshoreLiabilitiesToUa(onshoreLiabilities, emptyUA).success.value

      updatedUserAnswers.get(WhyAreYouMakingThisOnshoreDisclosurePage)                       shouldEqual None
      updatedUserAnswers.get(ReasonableExcuseOnshorePage)                                    shouldEqual None
      updatedUserAnswers.get(ReasonableCareOnshorePage)                                      shouldEqual None
      updatedUserAnswers.get(ReasonableExcuseForNotFilingOnshorePage)                        shouldEqual None
      updatedUserAnswers.get(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage)                  shouldEqual None
      updatedUserAnswers.get(WhichOnshoreYearsPage)                                          shouldEqual None
      updatedUserAnswers.get(NotIncludedSingleTaxYearPage)                                   shouldEqual None
      updatedUserAnswers.get(NotIncludedMultipleTaxYearsPage)                                shouldEqual None
      updatedUserAnswers.get(TaxBeforeThreeYearsOnshorePage)                                 shouldEqual None
      updatedUserAnswers.get(TaxBeforeFiveYearsOnshorePage)                                  shouldEqual None
      updatedUserAnswers.get(TaxBeforeNineteenYearsOnshorePage)                              shouldEqual None
      updatedUserAnswers.get(CDFOnshorePage)                                                 shouldEqual None
      updatedUserAnswers.get(OnshoreTaxYearLiabilitiesPage)                                  shouldEqual None
      updatedUserAnswers.get(ResidentialReductionPage)                                       shouldEqual None
      updatedUserAnswers.get(LettingPropertyPage)                                            shouldEqual None
      updatedUserAnswers.get(AreYouAMemberOfAnyLandlordAssociationsPage)                     shouldEqual None
      updatedUserAnswers.get(WhichLandlordAssociationsAreYouAMemberOfPage)                   shouldEqual None
      updatedUserAnswers.get(HowManyPropertiesDoYouCurrentlyLetOutPage)                      shouldEqual None
      updatedUserAnswers.get(CorporationTaxLiabilityPage)                                    shouldEqual None
      updatedUserAnswers.get(DirectorLoanAccountLiabilitiesPage)                             shouldEqual None
    }

    "return a PageWithValue for all that are set" in {
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
      val onshoreLiabilities = OnshoreLiabilities(
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
      val updatedUserAnswers = sut.onshoreLiabilitiesToUa(Some(onshoreLiabilities), emptyUA).success.value

      updatedUserAnswers.get(WhyAreYouMakingThisOnshoreDisclosurePage)                       shouldEqual Some(whySet)
      updatedUserAnswers.get(ReasonableExcuseOnshorePage)                                    shouldEqual Some(ReasonableExcuseOnshore("Some excuse", "Some years"))
      updatedUserAnswers.get(ReasonableCareOnshorePage)                                      shouldEqual Some(ReasonableCareOnshore("Some excuse", "Some years"))
      updatedUserAnswers.get(ReasonableExcuseForNotFilingOnshorePage)                        shouldEqual Some(ReasonableExcuseForNotFilingOnshore("Some excuse", "Some years"))
      updatedUserAnswers.get(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage)                  shouldEqual Some(whichLiabilitiesSet)
      updatedUserAnswers.get(WhichOnshoreYearsPage)                                          shouldEqual Some(yearsSet)
      updatedUserAnswers.get(NotIncludedSingleTaxYearPage)                                   shouldEqual Some("Not included year")
      updatedUserAnswers.get(NotIncludedMultipleTaxYearsPage)                                shouldEqual Some("Not included years")
      updatedUserAnswers.get(TaxBeforeThreeYearsOnshorePage)                                 shouldEqual Some("Some liabilities 1")
      updatedUserAnswers.get(TaxBeforeFiveYearsOnshorePage)                                  shouldEqual Some("Some liabilities 2")
      updatedUserAnswers.get(TaxBeforeNineteenYearsOnshorePage)                              shouldEqual Some("Some liabilities 3")
      updatedUserAnswers.get(CDFOnshorePage)                                                 shouldEqual Some(true)
      updatedUserAnswers.get(OnshoreTaxYearLiabilitiesPage)                                  shouldEqual Some(Map("2012" -> OnshoreTaxYearWithLiabilities(OnshoreYearStarting(2012), liabilities)))
      updatedUserAnswers.get(ResidentialReductionPage)                                       shouldEqual Some(Map("2012" -> BigInt(123)))
      updatedUserAnswers.get(LettingPropertyPage)                                            shouldEqual Some(lettingProperty)
      updatedUserAnswers.get(AreYouAMemberOfAnyLandlordAssociationsPage)                     shouldEqual Some(true)
      updatedUserAnswers.get(WhichLandlordAssociationsAreYouAMemberOfPage)                   shouldEqual Some("Some associations")
      updatedUserAnswers.get(HowManyPropertiesDoYouCurrentlyLetOutPage)                      shouldEqual Some("Some properties")
      updatedUserAnswers.get(CorporationTaxLiabilityPage)                                    shouldEqual Some(corporationTax)
      updatedUserAnswers.get(DirectorLoanAccountLiabilitiesPage)                             shouldEqual Some(directorLoan)

    }
  }

}