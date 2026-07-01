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

package pages.notification

import pages._

trait SectionPages {

  val aboutYouPages: List[QuestionPage[?]] = List(
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

  val aboutIndividualPages: List[QuestionPage[?]] = List(
    WhatIsTheIndividualsFullNamePage,
    WhatIsTheIndividualDateOfBirthPage,
    WhatIsTheIndividualOccupationPage,
    DoesTheIndividualHaveNationalInsuranceNumberPage,
    WhatIsIndividualsNationalInsuranceNumberPage,
    IsTheIndividualRegisteredForVATPage,
    WhatIsTheIndividualsVATRegistrationNumberPage,
    IsTheIndividualRegisteredForSelfAssessmentPage,
    WhatIsTheIndividualsUniqueTaxReferencePage,
    IndividualAddressLookupPage
  )

  val aboutCompanyPages: List[QuestionPage[?]] = List(
    WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage,
    WhatIsTheCompanyRegistrationNumberPage,
    CompanyAddressLookupPage
  )

  val aboutLLPPages: List[QuestionPage[?]] = List(
    WhatIsTheLLPNamePage,
    LLPAddressLookupPage
  )

  val aboutTrustPages: List[QuestionPage[?]] = List(
    WhatIsTheTrustNamePage,
    TrustAddressLookupPage
  )

  val aboutPersonWhoDiedPages: List[QuestionPage[?]] = List(
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

  val areYouTheOrganisationPages: List[QuestionPage[?]] = List(
    AreYouRepresentingAnOrganisationPage,
    WhatIsTheNameOfTheOrganisationYouRepresentPage
  )

  val areYouTheEntityPages: List[QuestionPage[?]] = List(
    AreYouTheEntityPage
  )

  val allEntityPages: List[QuestionPage[?]] = aboutIndividualPages :::
    aboutCompanyPages :::
    aboutLLPPages :::
    aboutTrustPages :::
    aboutPersonWhoDiedPages :::
    areYouTheOrganisationPages :::
    areYouTheEntityPages
}
