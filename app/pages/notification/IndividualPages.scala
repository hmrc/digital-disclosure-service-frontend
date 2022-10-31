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

trait IndividualPages {

  val aboutYouPages: List[QuestionPage[_]] = List(
    OnshoreLiabilitiesPage,
    OffshoreLiabilitiesPage,
    WhatIsYourFullNamePage,
    YourPhoneNumberPage,
    DoYouHaveAnEmailAddressPage,
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

  val removeLetterReferencePages: List[QuestionPage[_]] = List(
    LetterReferencePage
  )

}
