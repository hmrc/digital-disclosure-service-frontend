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

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Arbitrary
import pages._
import models._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryWhatWasTheNameOfThePersonWhoDiedUserAnswersEntry: Arbitrary[(WhatWasTheNameOfThePersonWhoDiedPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatWasTheNameOfThePersonWhoDiedPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsTheTrustNameUserAnswersEntry: Arbitrary[(WhatIsTheTrustNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsTheTrustNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutUserAnswersEntry: Arbitrary[(AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsTheLLPNameUserAnswersEntry: Arbitrary[(WhatIsTheLLPNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsTheLLPNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutUserAnswersEntry: Arbitrary[(AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsTheCompanyRegistrationNumberUserAnswersEntry: Arbitrary[(WhatIsTheCompanyRegistrationNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsTheCompanyRegistrationNumberPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutUserAnswersEntry: Arbitrary[(WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsTheNameOfTheOrganisationYouRepresentUserAnswersEntry: Arbitrary[(WhatIsTheNameOfTheOrganisationYouRepresentPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsTheNameOfTheOrganisationYouRepresentPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAreYouRepresentingAnOrganisationUserAnswersEntry: Arbitrary[(AreYouRepresentingAnOrganisationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AreYouRepresentingAnOrganisationPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutUserAnswersEntry: Arbitrary[(AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsTheIndividualsUniqueTaxReferenceUserAnswersEntry: Arbitrary[(WhatIsTheIndividualsUniqueTaxReferencePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsTheIndividualsUniqueTaxReferencePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsTheIndividualsVATRegistrationNumberUserAnswersEntry: Arbitrary[(WhatIsTheIndividualsVATRegistrationNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsTheIndividualsVATRegistrationNumberPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsTheIndividualRegisteredForSelfAssessmentUserAnswersEntry: Arbitrary[(IsTheIndividualRegisteredForSelfAssessmentPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsTheIndividualRegisteredForSelfAssessmentPage.type]
        value <- arbitrary[IsTheIndividualRegisteredForSelfAssessment].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsTheIndividualRegisteredForVATUserAnswersEntry: Arbitrary[(IsTheIndividualRegisteredForVATPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsTheIndividualRegisteredForVATPage.type]
        value <- arbitrary[IsTheIndividualRegisteredForVAT].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsIndividualsNationalInsuranceNumberUserAnswersEntry: Arbitrary[(WhatIsIndividualsNationalInsuranceNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsIndividualsNationalInsuranceNumberPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDoesTheIndividualHaveNationalInsuranceNumberUserAnswersEntry: Arbitrary[(DoesTheIndividualHaveNationalInsuranceNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DoesTheIndividualHaveNationalInsuranceNumberPage.type]
        value <- arbitrary[DoesTheIndividualHaveNationalInsuranceNumber].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsTheIndividualOccupationUserAnswersEntry: Arbitrary[(WhatIsTheIndividualOccupationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsTheIndividualOccupationPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsTheIndividualDateOfBirthControllerUserAnswersEntry: Arbitrary[(WhatIsTheIndividualDateOfBirthControllerPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsTheIndividualDateOfBirthControllerPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsTheIndividualsFullNameUserAnswersEntry: Arbitrary[(WhatIsTheIndividualsFullNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsTheIndividualsFullNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsYourUniqueTaxReferenceUserAnswersEntry: Arbitrary[(WhatIsYourUniqueTaxReferencePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsYourUniqueTaxReferencePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAreYouRegisteredForSelfAssessmentUserAnswersEntry: Arbitrary[(AreYouRegisteredForSelfAssessmentPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AreYouRegisteredForSelfAssessmentPage.type]
        value <- arbitrary[AreYouRegisteredForSelfAssessment].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAreYouRegisteredForVATUserAnswersEntry: Arbitrary[(AreYouRegisteredForVATPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AreYouRegisteredForVATPage.type]
        value <- arbitrary[AreYouRegisteredForVAT].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsYourVATRegistrationNumberUserAnswersEntry: Arbitrary[(WhatIsYourVATRegistrationNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsYourVATRegistrationNumberPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsYourNationalInsuranceNumberUserAnswersEntry: Arbitrary[(WhatIsYourNationalInsuranceNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsYourNationalInsuranceNumberPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDoYouHaveNationalInsuranceNumberUserAnswersEntry: Arbitrary[(DoYouHaveNationalInsuranceNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DoYouHaveNationalInsuranceNumberPage.type]
        value <- arbitrary[DoYouHaveNationalInsuranceNumber].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsYourMainOccupationUserAnswersEntry: Arbitrary[(WhatIsYourMainOccupationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsYourMainOccupationPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsYourDateOfBirthUserAnswersEntry: Arbitrary[(WhatIsYourDateOfBirthPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsYourDateOfBirthPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsYourFullNameUserAnswersEntry: Arbitrary[(WhatIsYourFullNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsYourFullNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }
    
  implicit lazy val arbitraryYourEmailAddressUserAnswersEntry: Arbitrary[(YourEmailAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[YourEmailAddressPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryYourPhoneNumberUserAnswersEntry: Arbitrary[(YourPhoneNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[YourPhoneNumberPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDoYouHaveAnEmailAddressUserAnswersEntry: Arbitrary[(DoYouHaveAnEmailAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DoYouHaveAnEmailAddressPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryLetterReferenceUserAnswersEntry: Arbitrary[(LetterReferencePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[LetterReferencePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryOffshoreLiabilitiesUserAnswersEntry: Arbitrary[(OffshoreLiabilitiesPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[OffshoreLiabilitiesPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAreYouTheIndividualUserAnswersEntry: Arbitrary[(AreYouTheIndividualPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AreYouTheIndividualPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }
  
  implicit lazy val arbitraryOnshoreLiabilitiesUserAnswersEntry: Arbitrary[(OnshoreLiabilitiesPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[OnshoreLiabilitiesPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryrelatesToUserAnswersEntry: Arbitrary[(RelatesToPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[RelatesToPage.type]
        value <- arbitrary[RelatesTo].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryReceivedALetterUserAnswersEntry: Arbitrary[(ReceivedALetterPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ReceivedALetterPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }
}
