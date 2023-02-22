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

  implicit lazy val arbitraryCorporationTaxLiabilityPage: Arbitrary[CorporationTaxLiabilityPage.type] =
    Arbitrary(CorporationTaxLiabilityPage)
    
  implicit lazy val arbitraryResidentialReductionPage: Arbitrary[ResidentialReductionPage.type] =
    Arbitrary(ResidentialReductionPage)

  implicit lazy val arbitraryDirectorLoanAccountLiabilitiesPage: Arbitrary[DirectorLoanAccountLiabilitiesPage.type] =
    Arbitrary(DirectorLoanAccountLiabilitiesPage)

  implicit lazy val arbitraryWhichOnshoreYearsPage: Arbitrary[WhichOnshoreYearsPage.type] =
    Arbitrary(WhichOnshoreYearsPage)

  implicit lazy val arbitraryWhatOnshoreLiabilitiesDoYouNeedToDisclosePage: Arbitrary[WhatOnshoreLiabilitiesDoYouNeedToDisclosePage.type] =
    Arbitrary(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage)

  implicit lazy val arbitraryWhereDidTheUndeclaredIncomeOrGainIncludedPage: Arbitrary[WhereDidTheUndeclaredIncomeOrGainIncludedPage.type] =
    Arbitrary(WhereDidTheUndeclaredIncomeOrGainIncludedPage)
    
  implicit lazy val arbitraryOfferLetterPage: Arbitrary[OfferLetterPage.type] =
    Arbitrary(OfferLetterPage)

  implicit lazy val arbitraryWhichTelephoneNumberCanWeContactYouWithPage: Arbitrary[WhichTelephoneNumberCanWeContactYouWithPage.type] =
    Arbitrary(WhichTelephoneNumberCanWeContactYouWithPage)

  implicit lazy val arbitraryWhereDidTheUndeclaredIncomeOrGainPage: Arbitrary[WhereDidTheUndeclaredIncomeOrGainPage.type] =
    Arbitrary(WhereDidTheUndeclaredIncomeOrGainPage)

  implicit lazy val arbitraryForeignTaxCreditPage: Arbitrary[ForeignTaxCreditPage.type] =
    Arbitrary(ForeignTaxCreditPage)

  implicit lazy val arbitraryYouHaveNotSelectedCertainTaxYearPage: Arbitrary[YouHaveNotSelectedCertainTaxYearPage.type] =
    Arbitrary(YouHaveNotSelectedCertainTaxYearPage)

  implicit lazy val arbitraryTaxBeforeNineteenYearsPage: Arbitrary[TaxBeforeNineteenYearsPage.type] =
    Arbitrary(TaxBeforeNineteenYearsPage)

  implicit lazy val arbitraryTaxBeforeNineteenYearsOnshorePage: Arbitrary[TaxBeforeNineteenYearsOnshorePage.type] =
    Arbitrary(TaxBeforeNineteenYearsOnshorePage)

  implicit lazy val arbitraryNotificationStartedPage: Arbitrary[NotificationStartedPage.type] =
    Arbitrary(NotificationStartedPage)
    
  implicit lazy val arbitraryYouHaveNotIncludedTheTaxYearPage: Arbitrary[YouHaveNotIncludedTheTaxYearPage.type] =
    Arbitrary(YouHaveNotIncludedTheTaxYearPage)

  implicit lazy val arbitraryCountriesOrTerritoriesPage: Arbitrary[CountriesOrTerritoriesPage.type] =
    Arbitrary(CountriesOrTerritoriesPage)

  implicit lazy val arbitraryWhatEmailAddressCanWeContactYouWithPage: Arbitrary[WhatEmailAddressCanWeContactYouWithPage.type] =
    Arbitrary(WhatEmailAddressCanWeContactYouWithPage)

  implicit lazy val arbitraryWhichEmailAddressCanWeContactYouWithPage: Arbitrary[WhichEmailAddressCanWeContactYouWithPage.type] =
    Arbitrary(WhichEmailAddressCanWeContactYouWithPage)

  implicit lazy val arbitraryWhatIsTheCaseReferencePage: Arbitrary[WhatIsTheCaseReferencePage.type] =
    Arbitrary(WhatIsTheCaseReferencePage)

  implicit lazy val arbitraryDoYouHaveACaseReferencePage: Arbitrary[DoYouHaveACaseReferencePage.type] =
    Arbitrary(DoYouHaveACaseReferencePage)

  implicit lazy val arbitraryWhatTelephoneNumberCanWeContactYouWithPage: Arbitrary[WhatTelephoneNumberCanWeContactYouWithPage.type] =
    Arbitrary(WhatTelephoneNumberCanWeContactYouWithPage)

  implicit lazy val arbitraryAdviceGivenPage: Arbitrary[AdviceGivenPage.type] =
    Arbitrary(AdviceGivenPage)
    
  implicit lazy val arbitraryAdviceProfessionPage: Arbitrary[AdviceProfessionPage.type] =
    Arbitrary(AdviceProfessionPage)

  implicit lazy val arbitraryAdviceBusinessNamePage: Arbitrary[AdviceBusinessNamePage.type] =
    Arbitrary(AdviceBusinessNamePage)

  implicit lazy val arbitraryAdviceBusinessesOrOrgPage: Arbitrary[AdviceBusinessesOrOrgPage.type] =
    Arbitrary(AdviceBusinessesOrOrgPage)

  implicit lazy val arbitraryPersonWhoGaveAdvicePage: Arbitrary[PersonWhoGaveAdvicePage.type] =
    Arbitrary(PersonWhoGaveAdvicePage)

  implicit lazy val arbitraryDidSomeoneGiveYouAdviceNotDeclareTaxPage: Arbitrary[DidSomeoneGiveYouAdviceNotDeclareTaxPage.type] =
    Arbitrary(DidSomeoneGiveYouAdviceNotDeclareTaxPage)

  implicit lazy val arbitraryWhyNotBeforeNowPage: Arbitrary[WhyNotBeforeNowPage.type] =
    Arbitrary(WhyNotBeforeNowPage)
    
  implicit lazy val arbitraryWhatIsTheReasonForMakingADisclosureNowPage: Arbitrary[WhatIsTheReasonForMakingADisclosureNowPage.type] =
    Arbitrary(WhatIsTheReasonForMakingADisclosureNowPage)

  implicit lazy val arbitraryWhyAreYouMakingADisclosurePage: Arbitrary[WhyAreYouMakingADisclosurePage.type] =
    Arbitrary(WhyAreYouMakingADisclosurePage)

  implicit lazy val arbitraryDidYouReceiveTaxCreditPage: Arbitrary[DidYouReceiveTaxCreditPage.type] =
    Arbitrary(DidYouReceiveTaxCreditPage)

  implicit lazy val arbitraryWhatOtherLiabilityIssuesPage: Arbitrary[WhatOtherLiabilityIssuesPage.type] =
    Arbitrary(WhatOtherLiabilityIssuesPage)

  implicit lazy val arbitraryDescribeTheGiftPage: Arbitrary[DescribeTheGiftPage.type] =
    Arbitrary(DescribeTheGiftPage)

  implicit lazy val arbitraryOtherLiabilityIssuesPage: Arbitrary[OtherLiabilityIssuesPage.type] =
    Arbitrary(OtherLiabilityIssuesPage)

  implicit lazy val arbitraryTheMaximumValueOfAllAssetsPage: Arbitrary[TheMaximumValueOfAllAssetsPage.type] =
    Arbitrary(TheMaximumValueOfAllAssetsPage)

  implicit lazy val arbitraryHowMuchTaxHasNotBeenIncludedPage: Arbitrary[HowMuchTaxHasNotBeenIncludedPage.type] =
    Arbitrary(HowMuchTaxHasNotBeenIncludedPage)

  implicit lazy val arbitraryUnderWhatConsiderationPage: Arbitrary[UnderWhatConsiderationPage.type] =
    Arbitrary(UnderWhatConsiderationPage)

  implicit lazy val arbitraryTaxBeforeSevenYearsPage: Arbitrary[TaxBeforeSevenYearsPage.type] =
    Arbitrary(TaxBeforeSevenYearsPage)

  implicit lazy val arbitraryTaxBeforeThreeYearsOnshorePage: Arbitrary[TaxBeforeThreeYearsOnshorePage.type] =
    Arbitrary(TaxBeforeThreeYearsOnshorePage)

  implicit lazy val arbitraryTaxBeforeFiveYearsPage: Arbitrary[TaxBeforeFiveYearsPage.type] =
    Arbitrary(TaxBeforeFiveYearsPage)

  implicit lazy val arbitraryTaxBeforeFiveYearsOnshorePage: Arbitrary[TaxBeforeFiveYearsOnshorePage.type] =
    Arbitrary(TaxBeforeFiveYearsOnshorePage)

  implicit lazy val arbitraryTaxYearLiabilitiesPage: Arbitrary[TaxYearLiabilitiesPage.type] =
    Arbitrary(TaxYearLiabilitiesPage)

  implicit lazy val arbitraryYourLegalInterpretationPage: Arbitrary[YourLegalInterpretationPage.type] =
    Arbitrary(YourLegalInterpretationPage)

  implicit lazy val arbitraryCountryOfYourOffshoreLiabilityPage: Arbitrary[CountryOfYourOffshoreLiabilityPage.type] =
    Arbitrary(CountryOfYourOffshoreLiabilityPage)

  implicit lazy val arbitraryWhichYearsPage: Arbitrary[WhichYearsPage.type] =
    Arbitrary(WhichYearsPage)

  implicit lazy val arbitraryWhatIsYourReasonableExcusePage: Arbitrary[WhatIsYourReasonableExcusePage.type] =
    Arbitrary(WhatIsYourReasonableExcusePage)
    
  implicit lazy val arbitraryWhatIsYourReasonableExcuseForNotFilingReturnPage: Arbitrary[WhatIsYourReasonableExcuseForNotFilingReturnPage.type] =
    Arbitrary(WhatIsYourReasonableExcuseForNotFilingReturnPage)

  implicit lazy val arbitraryWhatReasonableCareDidYouTakePage: Arbitrary[WhatReasonableCareDidYouTakePage.type] =
    Arbitrary(WhatReasonableCareDidYouTakePage)
    
  implicit lazy val arbitraryYouHaveLeftTheDDSPage: Arbitrary[YouHaveLeftTheDDSPage.type] =
    Arbitrary(YouHaveLeftTheDDSPage)

  implicit lazy val arbitraryContractualDisclosureFacilityPage: Arbitrary[ContractualDisclosureFacilityPage.type] =
    Arbitrary(ContractualDisclosureFacilityPage)

  implicit lazy val arbitraryWhyAreYouMakingThisDisclosurePage: Arbitrary[WhyAreYouMakingThisDisclosurePage.type] =
    Arbitrary(WhyAreYouMakingThisDisclosurePage)

  implicit lazy val arbitraryMakeANotificationOrDisclosurePage: Arbitrary[MakeANotificationOrDisclosurePage.type] =
    Arbitrary(MakeANotificationOrDisclosurePage)

  implicit lazy val arbitraryHowWouldYouPreferToBeContactedPage: Arbitrary[HowWouldYouPreferToBeContactedPage.type] =
    Arbitrary(HowWouldYouPreferToBeContactedPage)

  implicit lazy val arbitraryWhatWasThePersonUTRPage: Arbitrary[WhatWasThePersonUTRPage.type] =
    Arbitrary(WhatWasThePersonUTRPage)

  implicit lazy val arbitraryWasThePersonRegisteredForSAPage: Arbitrary[WasThePersonRegisteredForSAPage.type] =
    Arbitrary(WasThePersonRegisteredForSAPage)

  implicit lazy val arbitraryWhatWasThePersonVATRegistrationNumberPage: Arbitrary[WhatWasThePersonVATRegistrationNumberPage.type] =
    Arbitrary(WhatWasThePersonVATRegistrationNumberPage)

  implicit lazy val arbitraryWasThePersonRegisteredForVATPage: Arbitrary[WasThePersonRegisteredForVATPage.type] =
    Arbitrary(WasThePersonRegisteredForVATPage)

  implicit lazy val arbitraryWhatWasThePersonNINOPage: Arbitrary[WhatWasThePersonNINOPage.type] =
    Arbitrary(WhatWasThePersonNINOPage)

  implicit lazy val arbitraryDidThePersonHaveNINOPage: Arbitrary[DidThePersonHaveNINOPage.type] =
    Arbitrary(DidThePersonHaveNINOPage)

  implicit lazy val arbitraryWhatWasThePersonDateOfBirthPage: Arbitrary[WhatWasThePersonDateOfBirthPage.type] =
    Arbitrary(WhatWasThePersonDateOfBirthPage)

  implicit lazy val arbitraryWhatWasThePersonOccupationPage: Arbitrary[WhatWasThePersonOccupationPage.type] =
    Arbitrary(WhatWasThePersonOccupationPage)

  implicit lazy val arbitraryAreYouTheExecutorOfTheEstatePage: Arbitrary[AreYouTheExecutorOfTheEstatePage.type] =
    Arbitrary(AreYouTheExecutorOfTheEstatePage)

  implicit lazy val arbitraryWhatWasTheNameOfThePersonWhoDiedPage: Arbitrary[WhatWasTheNameOfThePersonWhoDiedPage.type] =
    Arbitrary(WhatWasTheNameOfThePersonWhoDiedPage)

  implicit lazy val arbitraryWhatIsTheTrustNamePage: Arbitrary[WhatIsTheTrustNamePage.type] =
    Arbitrary(WhatIsTheTrustNamePage)

  implicit lazy val arbitraryAreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage: Arbitrary[AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage.type] =
    Arbitrary(AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage)

  implicit lazy val arbitraryWhatIsTheLLPNamePage: Arbitrary[WhatIsTheLLPNamePage.type] =
    Arbitrary(WhatIsTheLLPNamePage)

  implicit lazy val arbitraryAreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage: Arbitrary[AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage.type] =
    Arbitrary(AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage)

  implicit lazy val arbitraryWhatIsTheCompanyRegistrationNumberPage: Arbitrary[WhatIsTheCompanyRegistrationNumberPage.type] =
    Arbitrary(WhatIsTheCompanyRegistrationNumberPage)

  implicit lazy val arbitraryWhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage: Arbitrary[WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage.type] =
    Arbitrary(WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage)

  implicit lazy val arbitraryWhatIsTheNameOfTheOrganisationYouRepresentPage: Arbitrary[WhatIsTheNameOfTheOrganisationYouRepresentPage.type] =
    Arbitrary(WhatIsTheNameOfTheOrganisationYouRepresentPage)

  implicit lazy val arbitraryAreYouRepresentingAnOrganisationPage: Arbitrary[AreYouRepresentingAnOrganisationPage.type] =
    Arbitrary(AreYouRepresentingAnOrganisationPage)

  implicit lazy val arbitraryAreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage: Arbitrary[AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage.type] =
    Arbitrary(AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage)

  implicit lazy val arbitraryWhatIsTheIndividualsUniqueTaxReferencePage: Arbitrary[WhatIsTheIndividualsUniqueTaxReferencePage.type] =
    Arbitrary(WhatIsTheIndividualsUniqueTaxReferencePage)

  implicit lazy val arbitraryWhatIsTheIndividualsVATRegistrationNumberPage: Arbitrary[WhatIsTheIndividualsVATRegistrationNumberPage.type] =
    Arbitrary(WhatIsTheIndividualsVATRegistrationNumberPage)

  implicit lazy val arbitraryIsTheIndividualRegisteredForSelfAssessmentPage: Arbitrary[IsTheIndividualRegisteredForSelfAssessmentPage.type] =
    Arbitrary(IsTheIndividualRegisteredForSelfAssessmentPage)

  implicit lazy val arbitraryIsTheIndividualRegisteredForVATPage: Arbitrary[IsTheIndividualRegisteredForVATPage.type] =
    Arbitrary(IsTheIndividualRegisteredForVATPage)

  implicit lazy val arbitraryWhatIsIndividualsNationalInsuranceNumberPage: Arbitrary[WhatIsIndividualsNationalInsuranceNumberPage.type] =
    Arbitrary(WhatIsIndividualsNationalInsuranceNumberPage)

  implicit lazy val arbitraryDoesTheIndividualHaveNationalInsuranceNumberPage: Arbitrary[DoesTheIndividualHaveNationalInsuranceNumberPage.type] =
    Arbitrary(DoesTheIndividualHaveNationalInsuranceNumberPage)

  implicit lazy val arbitraryWhatIsTheIndividualOccupationPage: Arbitrary[WhatIsTheIndividualOccupationPage.type] =
    Arbitrary(WhatIsTheIndividualOccupationPage)

  implicit lazy val arbitraryWhatIsTheIndividualDateOfBirthPage: Arbitrary[WhatIsTheIndividualDateOfBirthPage.type] =
    Arbitrary(WhatIsTheIndividualDateOfBirthPage)

  implicit lazy val arbitraryWhatIsTheIndividualsFullNamePage: Arbitrary[WhatIsTheIndividualsFullNamePage.type] =
    Arbitrary(WhatIsTheIndividualsFullNamePage)

  implicit lazy val arbitraryWhatIsYourUniqueTaxReferencePage: Arbitrary[WhatIsYourUniqueTaxReferencePage.type] =
    Arbitrary(WhatIsYourUniqueTaxReferencePage)

  implicit lazy val arbitraryAreYouRegisteredForSelfAssessmentPage: Arbitrary[AreYouRegisteredForSelfAssessmentPage.type] =
    Arbitrary(AreYouRegisteredForSelfAssessmentPage)

  implicit lazy val arbitraryAreYouRegisteredForVATPage: Arbitrary[AreYouRegisteredForVATPage.type] =
    Arbitrary(AreYouRegisteredForVATPage)

  implicit lazy val arbitraryWhatIsYourVATRegistrationNumberPage: Arbitrary[WhatIsYourVATRegistrationNumberPage.type] =
    Arbitrary(WhatIsYourVATRegistrationNumberPage)

  implicit lazy val arbitraryWhatIsYourNationalInsuranceNumberPage: Arbitrary[WhatIsYourNationalInsuranceNumberPage.type] =
    Arbitrary(WhatIsYourNationalInsuranceNumberPage)

  implicit lazy val arbitraryDoYouHaveNationalInsuranceNumberPage: Arbitrary[DoYouHaveNationalInsuranceNumberPage.type] =
    Arbitrary(DoYouHaveNationalInsuranceNumberPage)

  implicit lazy val arbitraryWhatIsYourMainOccupationPage: Arbitrary[WhatIsYourMainOccupationPage.type] =
    Arbitrary(WhatIsYourMainOccupationPage)

  implicit lazy val arbitraryWhatIsYourDateOfBirthPage: Arbitrary[WhatIsYourDateOfBirthPage.type] =
    Arbitrary(WhatIsYourDateOfBirthPage)

  implicit lazy val arbitraryWhatIsYourFullNamePage: Arbitrary[WhatIsYourFullNamePage.type] =
    Arbitrary(WhatIsYourFullNamePage)
    
  implicit lazy val arbitraryYourEmailAddressPage: Arbitrary[YourEmailAddressPage.type] =
    Arbitrary(YourEmailAddressPage)
    
  implicit lazy val arbitraryYourPhoneNumberPage: Arbitrary[YourPhoneNumberPage.type] =
    Arbitrary(YourPhoneNumberPage)

  implicit lazy val arbitraryLetterReferencePage: Arbitrary[LetterReferencePage.type] =
    Arbitrary(LetterReferencePage)

  implicit lazy val arbitraryOffshoreLiabilitiesPage: Arbitrary[OffshoreLiabilitiesPage.type] =
    Arbitrary(OffshoreLiabilitiesPage)

  implicit lazy val arbitraryOnshoreLiabilitiesPage: Arbitrary[OnshoreLiabilitiesPage.type] =
    Arbitrary(OnshoreLiabilitiesPage)

  implicit lazy val arbitraryAreYouTheIndividualPage: Arbitrary[AreYouTheIndividualPage.type] =
    Arbitrary(AreYouTheIndividualPage)

  implicit lazy val arbitraryrelatesToPage: Arbitrary[RelatesToPage.type] =
    Arbitrary(RelatesToPage)
 
  implicit lazy val arbitraryReceivedALetterPage: Arbitrary[ReceivedALetterPage.type] =
    Arbitrary(ReceivedALetterPage)

}
