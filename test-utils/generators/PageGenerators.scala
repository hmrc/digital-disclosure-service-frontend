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

package generators

import org.scalacheck.Arbitrary
import pages._

trait PageGenerators {

  given arbitraryAreYouTheEntityPage: Arbitrary[AreYouTheEntityPage.type] =
    Arbitrary(AreYouTheEntityPage)

  given arbitraryAccountingPeriodDLAddedPage: Arbitrary[AccountingPeriodDLAddedPage.type] =
    Arbitrary(AccountingPeriodDLAddedPage)

  given arbitraryAccountingPeriodCTAddedPage: Arbitrary[AccountingPeriodCTAddedPage.type] =
    Arbitrary(AccountingPeriodCTAddedPage)

  given arbitraryPropertyAddedPage: Arbitrary[PropertyAddedPage.type] =
    Arbitrary(PropertyAddedPage)

  given arbitraryHowManyPropertiesDoYouCurrentlyLetOutPage: Arbitrary[HowManyPropertiesDoYouCurrentlyLetOutPage.type] =
    Arbitrary(HowManyPropertiesDoYouCurrentlyLetOutPage)

  given arbitraryAreYouAMemberOfAnyLandlordAssociationsPage
    : Arbitrary[AreYouAMemberOfAnyLandlordAssociationsPage.type] =
    Arbitrary(AreYouAMemberOfAnyLandlordAssociationsPage)

  given arbitraryDidTheLettingAgentCollectRentOnYourBehalfPage
    : Arbitrary[DidTheLettingAgentCollectRentOnYourBehalfPage.type] =
    Arbitrary(DidTheLettingAgentCollectRentOnYourBehalfPage)

  given arbitraryWasALettingAgentUsedToManagePropertyPage: Arbitrary[WasALettingAgentUsedToManagePropertyPage.type] =
    Arbitrary(WasALettingAgentUsedToManagePropertyPage)

  given arbitraryWhatWasTheTypeOfMortgagePage: Arbitrary[WhatWasTheTypeOfMortgagePage.type] =
    Arbitrary(WhatWasTheTypeOfMortgagePage)

  given arbitraryWhatTypeOfMortgageDidYouHavePage: Arbitrary[WhatTypeOfMortgageDidYouHavePage.type] =
    Arbitrary(WhatTypeOfMortgageDidYouHavePage)

  given arbitraryWasPropertyFurnishedPage: Arbitrary[WasPropertyFurnishedPage.type] =
    Arbitrary(WasPropertyFurnishedPage)

  given arbitraryWhatWasThePercentageIncomeYouReceivedFromPropertyPage
    : Arbitrary[WhatWasThePercentageIncomeYouReceivedFromPropertyPage.type] =
    Arbitrary(WhatWasThePercentageIncomeYouReceivedFromPropertyPage)

  given arbitraryDidYouHaveAMortgageOnPropertyPage: Arbitrary[DidYouHaveAMortgageOnPropertyPage.type] =
    Arbitrary(DidYouHaveAMortgageOnPropertyPage)

  given arbitraryJointlyOwnedPropertyPage: Arbitrary[JointlyOwnedPropertyPage.type] =
    Arbitrary(JointlyOwnedPropertyPage)

  given arbitraryFHLPage: Arbitrary[FHLPage.type] =
    Arbitrary(FHLPage)

  given arbitraryPropertyIsNoLongerBeingLetOutPage: Arbitrary[PropertyIsNoLongerBeingLetOutPage.type] =
    Arbitrary(PropertyIsNoLongerBeingLetOutPage)

  given arbitraryPropertyStoppedBeingLetOutPage: Arbitrary[PropertyStoppedBeingLetOutPage.type] =
    Arbitrary(PropertyStoppedBeingLetOutPage)

  given arbitraryPropertyFirstLetOutPage: Arbitrary[PropertyFirstLetOutPage.type] =
    Arbitrary(PropertyFirstLetOutPage)

  given arbitraryCorporationTaxLiabilityPage: Arbitrary[CorporationTaxLiabilityPage.type] =
    Arbitrary(CorporationTaxLiabilityPage)

  given arbitraryResidentialReductionPage: Arbitrary[ResidentialReductionPage.type] =
    Arbitrary(ResidentialReductionPage)

  given arbitraryDirectorLoanAccountLiabilitiesPage: Arbitrary[DirectorLoanAccountLiabilitiesPage.type] =
    Arbitrary(DirectorLoanAccountLiabilitiesPage)

  given arbitraryWhichOnshoreYearsPage: Arbitrary[WhichOnshoreYearsPage.type] =
    Arbitrary(WhichOnshoreYearsPage)

  given arbitraryWhatOnshoreLiabilitiesDoYouNeedToDisclosePage
    : Arbitrary[WhatOnshoreLiabilitiesDoYouNeedToDisclosePage.type] =
    Arbitrary(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage)

  given arbitraryOfferLetterPage: Arbitrary[OfferLetterPage.type] =
    Arbitrary(OfferLetterPage)

  given arbitraryWhichTelephoneNumberCanWeContactYouWithPage
    : Arbitrary[WhichTelephoneNumberCanWeContactYouWithPage.type] =
    Arbitrary(WhichTelephoneNumberCanWeContactYouWithPage)

  given arbitraryForeignTaxCreditPage: Arbitrary[ForeignTaxCreditPage.type] =
    Arbitrary(ForeignTaxCreditPage)

  given arbitraryYouHaveNotSelectedCertainTaxYearPage: Arbitrary[YouHaveNotSelectedCertainTaxYearPage.type] =
    Arbitrary(YouHaveNotSelectedCertainTaxYearPage)

  given arbitraryTaxBeforeNineteenYearsPage: Arbitrary[TaxBeforeNineteenYearsPage.type] =
    Arbitrary(TaxBeforeNineteenYearsPage)

  given arbitraryTaxBeforeNineteenYearsOnshorePage: Arbitrary[TaxBeforeNineteenYearsOnshorePage.type] =
    Arbitrary(TaxBeforeNineteenYearsOnshorePage)

  given arbitraryNotificationStartedPage: Arbitrary[NotificationStartedPage.type] =
    Arbitrary(NotificationStartedPage)

  given arbitraryYouHaveNotIncludedTheTaxYearPage: Arbitrary[YouHaveNotIncludedTheTaxYearPage.type] =
    Arbitrary(YouHaveNotIncludedTheTaxYearPage)

  given arbitraryCountriesOrTerritoriesPage: Arbitrary[CountriesOrTerritoriesPage.type] =
    Arbitrary(CountriesOrTerritoriesPage)

  given arbitraryWhatEmailAddressCanWeContactYouWithPage: Arbitrary[WhatEmailAddressCanWeContactYouWithPage.type] =
    Arbitrary(WhatEmailAddressCanWeContactYouWithPage)

  given arbitraryWhichEmailAddressCanWeContactYouWithPage: Arbitrary[WhichEmailAddressCanWeContactYouWithPage.type] =
    Arbitrary(WhichEmailAddressCanWeContactYouWithPage)

  given arbitraryWhatIsTheCaseReferencePage: Arbitrary[WhatIsTheCaseReferencePage.type] =
    Arbitrary(WhatIsTheCaseReferencePage)

  given arbitraryDoYouHaveACaseReferencePage: Arbitrary[DoYouHaveACaseReferencePage.type] =
    Arbitrary(DoYouHaveACaseReferencePage)

  given arbitraryWhatTelephoneNumberCanWeContactYouWithPage
    : Arbitrary[WhatTelephoneNumberCanWeContactYouWithPage.type] =
    Arbitrary(WhatTelephoneNumberCanWeContactYouWithPage)

  given arbitraryAdviceGivenPage: Arbitrary[AdviceGivenPage.type] =
    Arbitrary(AdviceGivenPage)

  given arbitraryAdviceProfessionPage: Arbitrary[AdviceProfessionPage.type] =
    Arbitrary(AdviceProfessionPage)

  given arbitraryAdviceBusinessNamePage: Arbitrary[AdviceBusinessNamePage.type] =
    Arbitrary(AdviceBusinessNamePage)

  given arbitraryAdviceBusinessesOrOrgPage: Arbitrary[AdviceBusinessesOrOrgPage.type] =
    Arbitrary(AdviceBusinessesOrOrgPage)

  given arbitraryPersonWhoGaveAdvicePage: Arbitrary[PersonWhoGaveAdvicePage.type] =
    Arbitrary(PersonWhoGaveAdvicePage)

  given arbitraryDidSomeoneGiveYouAdviceNotDeclareTaxPage: Arbitrary[DidSomeoneGiveYouAdviceNotDeclareTaxPage.type] =
    Arbitrary(DidSomeoneGiveYouAdviceNotDeclareTaxPage)

  given arbitraryWhyNotBeforeNowPage: Arbitrary[WhyNotBeforeNowPage.type] =
    Arbitrary(WhyNotBeforeNowPage)

  given arbitraryWhatIsTheReasonForMakingADisclosureNowPage
    : Arbitrary[WhatIsTheReasonForMakingADisclosureNowPage.type] =
    Arbitrary(WhatIsTheReasonForMakingADisclosureNowPage)

  given arbitraryWhyAreYouMakingADisclosurePage: Arbitrary[WhyAreYouMakingADisclosurePage.type] =
    Arbitrary(WhyAreYouMakingADisclosurePage)

  given arbitraryDidYouReceiveTaxCreditPage: Arbitrary[DidYouReceiveTaxCreditPage.type] =
    Arbitrary(DidYouReceiveTaxCreditPage)

  given arbitraryWhatOtherLiabilityIssuesPage: Arbitrary[WhatOtherLiabilityIssuesPage.type] =
    Arbitrary(WhatOtherLiabilityIssuesPage)

  given arbitraryDescribeTheGiftPage: Arbitrary[DescribeTheGiftPage.type] =
    Arbitrary(DescribeTheGiftPage)

  given arbitraryOtherLiabilityIssuesPage: Arbitrary[OtherLiabilityIssuesPage.type] =
    Arbitrary(OtherLiabilityIssuesPage)

  given arbitraryTheMaximumValueOfAllAssetsPage: Arbitrary[TheMaximumValueOfAllAssetsPage.type] =
    Arbitrary(TheMaximumValueOfAllAssetsPage)

  given arbitraryHowMuchTaxHasNotBeenIncludedPage: Arbitrary[HowMuchTaxHasNotBeenIncludedPage.type] =
    Arbitrary(HowMuchTaxHasNotBeenIncludedPage)

  given arbitraryUnderWhatConsiderationPage: Arbitrary[UnderWhatConsiderationPage.type] =
    Arbitrary(UnderWhatConsiderationPage)

  given arbitraryTaxBeforeSevenYearsPage: Arbitrary[TaxBeforeSevenYearsPage.type] =
    Arbitrary(TaxBeforeSevenYearsPage)

  given arbitraryTaxBeforeThreeYearsOnshorePage: Arbitrary[TaxBeforeThreeYearsOnshorePage.type] =
    Arbitrary(TaxBeforeThreeYearsOnshorePage)

  given arbitraryTaxBeforeFiveYearsPage: Arbitrary[TaxBeforeFiveYearsPage.type] =
    Arbitrary(TaxBeforeFiveYearsPage)

  given arbitraryTaxBeforeFiveYearsOnshorePage: Arbitrary[TaxBeforeFiveYearsOnshorePage.type] =
    Arbitrary(TaxBeforeFiveYearsOnshorePage)

  given arbitraryTaxYearLiabilitiesPage: Arbitrary[TaxYearLiabilitiesPage.type] =
    Arbitrary(TaxYearLiabilitiesPage)

  given arbitraryYourLegalInterpretationPage: Arbitrary[YourLegalInterpretationPage.type] =
    Arbitrary(YourLegalInterpretationPage)

  given arbitraryCountryOfYourOffshoreLiabilityPage: Arbitrary[CountryOfYourOffshoreLiabilityPage.type] =
    Arbitrary(CountryOfYourOffshoreLiabilityPage)

  given arbitraryWhichYearsPage: Arbitrary[WhichYearsPage.type] =
    Arbitrary(WhichYearsPage)

  given arbitraryWhatIsYourReasonableExcusePage: Arbitrary[WhatIsYourReasonableExcusePage.type] =
    Arbitrary(WhatIsYourReasonableExcusePage)

  given arbitraryWhatIsYourReasonableExcuseForNotFilingReturnPage
    : Arbitrary[WhatIsYourReasonableExcuseForNotFilingReturnPage.type] =
    Arbitrary(WhatIsYourReasonableExcuseForNotFilingReturnPage)

  given arbitraryWhatReasonableCareDidYouTakePage: Arbitrary[WhatReasonableCareDidYouTakePage.type] =
    Arbitrary(WhatReasonableCareDidYouTakePage)

  given arbitraryYouHaveLeftTheDDSPage: Arbitrary[YouHaveLeftTheDDSPage.type] =
    Arbitrary(YouHaveLeftTheDDSPage)

  given arbitraryContractualDisclosureFacilityPage: Arbitrary[ContractualDisclosureFacilityPage.type] =
    Arbitrary(ContractualDisclosureFacilityPage)

  given arbitraryWhyAreYouMakingThisDisclosurePage: Arbitrary[WhyAreYouMakingThisDisclosurePage.type] =
    Arbitrary(WhyAreYouMakingThisDisclosurePage)

  given arbitraryMakeANotificationOrDisclosurePage: Arbitrary[MakeANotificationOrDisclosurePage.type] =
    Arbitrary(MakeANotificationOrDisclosurePage)

  given arbitraryHowWouldYouPreferToBeContactedPage: Arbitrary[HowWouldYouPreferToBeContactedPage.type] =
    Arbitrary(HowWouldYouPreferToBeContactedPage)

  given arbitraryWhatWasThePersonUTRPage: Arbitrary[WhatWasThePersonUTRPage.type] =
    Arbitrary(WhatWasThePersonUTRPage)

  given arbitraryWasThePersonRegisteredForSAPage: Arbitrary[WasThePersonRegisteredForSAPage.type] =
    Arbitrary(WasThePersonRegisteredForSAPage)

  given arbitraryWhatWasThePersonVATRegistrationNumberPage: Arbitrary[WhatWasThePersonVATRegistrationNumberPage.type] =
    Arbitrary(WhatWasThePersonVATRegistrationNumberPage)

  given arbitraryWasThePersonRegisteredForVATPage: Arbitrary[WasThePersonRegisteredForVATPage.type] =
    Arbitrary(WasThePersonRegisteredForVATPage)

  given arbitraryWhatWasThePersonNINOPage: Arbitrary[WhatWasThePersonNINOPage.type] =
    Arbitrary(WhatWasThePersonNINOPage)

  given arbitraryDidThePersonHaveNINOPage: Arbitrary[DidThePersonHaveNINOPage.type] =
    Arbitrary(DidThePersonHaveNINOPage)

  given arbitraryWhatWasThePersonDateOfBirthPage: Arbitrary[WhatWasThePersonDateOfBirthPage.type] =
    Arbitrary(WhatWasThePersonDateOfBirthPage)

  given arbitraryWhatWasThePersonOccupationPage: Arbitrary[WhatWasThePersonOccupationPage.type] =
    Arbitrary(WhatWasThePersonOccupationPage)

  given arbitraryWhatWasTheNameOfThePersonWhoDiedPage: Arbitrary[WhatWasTheNameOfThePersonWhoDiedPage.type] =
    Arbitrary(WhatWasTheNameOfThePersonWhoDiedPage)

  given arbitraryWhatIsTheTrustNamePage: Arbitrary[WhatIsTheTrustNamePage.type] =
    Arbitrary(WhatIsTheTrustNamePage)

  given arbitraryWhatIsTheLLPNamePage: Arbitrary[WhatIsTheLLPNamePage.type] =
    Arbitrary(WhatIsTheLLPNamePage)

  given arbitraryWhatIsTheCompanyRegistrationNumberPage: Arbitrary[WhatIsTheCompanyRegistrationNumberPage.type] =
    Arbitrary(WhatIsTheCompanyRegistrationNumberPage)

  given arbitraryWhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage
    : Arbitrary[WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage.type] =
    Arbitrary(WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage)

  given arbitraryWhatIsTheNameOfTheOrganisationYouRepresentPage
    : Arbitrary[WhatIsTheNameOfTheOrganisationYouRepresentPage.type] =
    Arbitrary(WhatIsTheNameOfTheOrganisationYouRepresentPage)

  given arbitraryAreYouRepresentingAnOrganisationPage: Arbitrary[AreYouRepresentingAnOrganisationPage.type] =
    Arbitrary(AreYouRepresentingAnOrganisationPage)

  given arbitraryWhatIsTheIndividualsUniqueTaxReferencePage
    : Arbitrary[WhatIsTheIndividualsUniqueTaxReferencePage.type] =
    Arbitrary(WhatIsTheIndividualsUniqueTaxReferencePage)

  given arbitraryWhatIsTheIndividualsVATRegistrationNumberPage
    : Arbitrary[WhatIsTheIndividualsVATRegistrationNumberPage.type] =
    Arbitrary(WhatIsTheIndividualsVATRegistrationNumberPage)

  given arbitraryIsTheIndividualRegisteredForSelfAssessmentPage
    : Arbitrary[IsTheIndividualRegisteredForSelfAssessmentPage.type] =
    Arbitrary(IsTheIndividualRegisteredForSelfAssessmentPage)

  given arbitraryIsTheIndividualRegisteredForVATPage: Arbitrary[IsTheIndividualRegisteredForVATPage.type] =
    Arbitrary(IsTheIndividualRegisteredForVATPage)

  given arbitraryWhatIsIndividualsNationalInsuranceNumberPage
    : Arbitrary[WhatIsIndividualsNationalInsuranceNumberPage.type] =
    Arbitrary(WhatIsIndividualsNationalInsuranceNumberPage)

  given arbitraryDoesTheIndividualHaveNationalInsuranceNumberPage
    : Arbitrary[DoesTheIndividualHaveNationalInsuranceNumberPage.type] =
    Arbitrary(DoesTheIndividualHaveNationalInsuranceNumberPage)

  given arbitraryWhatIsTheIndividualOccupationPage: Arbitrary[WhatIsTheIndividualOccupationPage.type] =
    Arbitrary(WhatIsTheIndividualOccupationPage)

  given arbitraryWhatIsTheIndividualDateOfBirthPage: Arbitrary[WhatIsTheIndividualDateOfBirthPage.type] =
    Arbitrary(WhatIsTheIndividualDateOfBirthPage)

  given arbitraryWhatIsTheIndividualsFullNamePage: Arbitrary[WhatIsTheIndividualsFullNamePage.type] =
    Arbitrary(WhatIsTheIndividualsFullNamePage)

  given arbitraryWhatIsYourUniqueTaxReferencePage: Arbitrary[WhatIsYourUniqueTaxReferencePage.type] =
    Arbitrary(WhatIsYourUniqueTaxReferencePage)

  given arbitraryAreYouRegisteredForSelfAssessmentPage: Arbitrary[AreYouRegisteredForSelfAssessmentPage.type] =
    Arbitrary(AreYouRegisteredForSelfAssessmentPage)

  given arbitraryAreYouRegisteredForVATPage: Arbitrary[AreYouRegisteredForVATPage.type] =
    Arbitrary(AreYouRegisteredForVATPage)

  given arbitraryWhatIsYourVATRegistrationNumberPage: Arbitrary[WhatIsYourVATRegistrationNumberPage.type] =
    Arbitrary(WhatIsYourVATRegistrationNumberPage)

  given arbitraryWhatIsYourNationalInsuranceNumberPage: Arbitrary[WhatIsYourNationalInsuranceNumberPage.type] =
    Arbitrary(WhatIsYourNationalInsuranceNumberPage)

  given arbitraryDoYouHaveNationalInsuranceNumberPage: Arbitrary[DoYouHaveNationalInsuranceNumberPage.type] =
    Arbitrary(DoYouHaveNationalInsuranceNumberPage)

  given arbitraryWhatIsYourMainOccupationPage: Arbitrary[WhatIsYourMainOccupationPage.type] =
    Arbitrary(WhatIsYourMainOccupationPage)

  given arbitraryWhatIsYourDateOfBirthPage: Arbitrary[WhatIsYourDateOfBirthPage.type] =
    Arbitrary(WhatIsYourDateOfBirthPage)

  given arbitraryWhatIsYourFullNamePage: Arbitrary[WhatIsYourFullNamePage.type] =
    Arbitrary(WhatIsYourFullNamePage)

  given arbitraryYourEmailAddressPage: Arbitrary[YourEmailAddressPage.type] =
    Arbitrary(YourEmailAddressPage)

  given arbitraryYourPhoneNumberPage: Arbitrary[YourPhoneNumberPage.type] =
    Arbitrary(YourPhoneNumberPage)

  given arbitraryLetterReferencePage: Arbitrary[LetterReferencePage.type] =
    Arbitrary(LetterReferencePage)

  given arbitraryOffshoreLiabilitiesPage: Arbitrary[OffshoreLiabilitiesPage.type] =
    Arbitrary(OffshoreLiabilitiesPage)

  given arbitraryOnshoreLiabilitiesPage: Arbitrary[OnshoreLiabilitiesPage.type] =
    Arbitrary(OnshoreLiabilitiesPage)

  given arbitraryrelatesToPage: Arbitrary[RelatesToPage.type] =
    Arbitrary(RelatesToPage)

  given arbitraryReceivedALetterPage: Arbitrary[ReceivedALetterPage.type] =
    Arbitrary(ReceivedALetterPage)

}
