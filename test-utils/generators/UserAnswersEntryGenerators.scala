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
        value <- arbitrary[OffshoreLiabilities].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAreYouTheIndividualUserAnswersEntry: Arbitrary[(AreYouTheIndividualPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AreYouTheIndividualPage.type]
        value <- arbitrary[AreYouTheIndividual].map(Json.toJson(_))
      } yield (page, value)
    }
  
  implicit lazy val arbitraryOnshoreLiabilitiesUserAnswersEntry: Arbitrary[(OnshoreLiabilitiesPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[OnshoreLiabilitiesPage.type]
        value <- arbitrary[OnshoreLiabilities].map(Json.toJson(_))
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
