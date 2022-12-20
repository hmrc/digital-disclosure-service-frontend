/*
 * Copyright 2022 HM Revenue & Customs
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

  implicit lazy val arbitraryWhatIsTheIndividualDateOfBirthControllerPage: Arbitrary[WhatIsTheIndividualDateOfBirthControllerPage.type] =
    Arbitrary(WhatIsTheIndividualDateOfBirthControllerPage)

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
