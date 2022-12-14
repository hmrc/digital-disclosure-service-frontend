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

package pages.notification

import pages._

trait SectionPages {

  val aboutYouPages: List[QuestionPage[_]] = List(
    OnshoreLiabilitiesPage,
    OffshoreLiabilitiesPage,
    WhatIsYourFullNamePage,
    HowWouldYouPreferToBeContactedPage,
    YourPhoneNumberPage,
    YourEmailAddressPage,
    YourAddressLookupPage,
    WhatIsYourDateOfBirthPage,
    WhatIsYourMainOccupationPage,
    DoYouHaveNationalInsuranceNumberPage,
    WhatIsYourNationalInsuranceNumberPage,
    AreYouRegisteredForVATPage,
    WhatIsYourVATRegistrationNumberPage,
    AreYouRegisteredForSelfAssessmentPage,
    WhatIsYourUniqueTaxReferencePage
  )

  val aboutIndividualPages: List[QuestionPage[_]] = List(
    WhatIsTheIndividualsFullNamePage,
    WhatIsTheIndividualDateOfBirthControllerPage,
    WhatIsTheIndividualOccupationPage,
    DoesTheIndividualHaveNationalInsuranceNumberPage,
    WhatIsIndividualsNationalInsuranceNumberPage,
    IsTheIndividualRegisteredForVATPage,
    WhatIsTheIndividualsVATRegistrationNumberPage,
    IsTheIndividualRegisteredForSelfAssessmentPage,
    WhatIsTheIndividualsUniqueTaxReferencePage,
    IndividualAddressLookupPage
  )

  val aboutCompanyPages: List[QuestionPage[_]] = List(
    WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage,
    WhatIsTheCompanyRegistrationNumberPage,
    CompanyAddressLookupPage
  )

  val aboutLLPPages: List[QuestionPage[_]] = List(
    WhatIsTheLLPNamePage,
    LLPAddressLookupPage
  )

  val aboutTrustPages: List[QuestionPage[_]] = List(
    WhatIsTheTrustNamePage,
    TrustAddressLookupPage
  )

  val aboutPersonWhoDiedPages: List[QuestionPage[_]] = List(
    WhatWasTheNameOfThePersonWhoDiedPage,
    WhatWasThePersonDateOfBirthPage,
    WhatWasThePersonOccupationPage,
    DidThePersonHaveNINOPage,
    WhatWasThePersonNINOPage,
    WasThePersonRegisteredForVATPage,
    WhatWasThePersonVATRegistrationNumberPage,
    WasThePersonRegisteredForSAPage,
    WhatWasThePersonUTRPage,
    EstateAddressLookupPage
  )

  val areYouTheOrganisationPages: List[QuestionPage[_]] = List(
      AreYouRepresentingAnOrganisationPage,
      WhatIsTheNameOfTheOrganisationYouRepresentPage
  )

  val areYouTheEntityPages: List[QuestionPage[_]] = List(
    AreYouTheIndividualPage,
    AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage,
    AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage,
    AreYouTheExecutorOfTheEstatePage,
    AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage
  ) 

  val allEntityPages: List[QuestionPage[_]] = aboutIndividualPages ::: 
                                              aboutCompanyPages ::: 
                                              aboutLLPPages ::: 
                                              aboutTrustPages ::: 
                                              aboutPersonWhoDiedPages :::
                                              areYouTheOrganisationPages :::
                                              areYouTheEntityPages
}
