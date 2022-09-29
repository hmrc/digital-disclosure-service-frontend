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

package navigation

import base.SpecBase
import controllers.notification.routes
import pages._
import models._

import scala.util.{Success, Failure}

class NotificationNavigatorSpec extends SpecBase {

  val navigator = new NotificationNavigator

  "Notification Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, UserAnswers("id")) mustBe controllers.routes.IndexController.onPageLoad
      }

      "must go from the ReceivedALetter page to the RelatesTo controller when the user selects No" in {
        UserAnswers("id").set(ReceivedALetterPage, false) match {
          case Success(ua) => navigator.nextPage(ReceivedALetterPage, NormalMode, ua) mustBe routes.RelatesToController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the ReceivedALetter page to the LetterReference controller when the user selects Yes" in {
        UserAnswers("id").set(ReceivedALetterPage, true) match {
          case Success(ua) => navigator.nextPage(ReceivedALetterPage, NormalMode, ua) mustBe routes.LetterReferenceController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the LetterReferencePage page to the RelatesTo controller when the user enter any data" in {
        UserAnswers("id").set(LetterReferencePage, "test") match {
          case Success(ua) => navigator.nextPage(LetterReferencePage, NormalMode, ua) mustBe routes.RelatesToController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the RelatesTo page to the AreYouTheIndividual controller" in {
        navigator.nextPage(RelatesToPage, NormalMode, UserAnswers("id")) mustBe routes.AreYouTheIndividualController.onPageLoad(NormalMode)
      }
      "must go from the AreYouTheIndividual page to the OffshoreLiabilities controller when the user selects Yes" in {
        UserAnswers("id").set(AreYouTheIndividualPage, AreYouTheIndividual.Yes) match {
          case Success(ua) => navigator.nextPage(AreYouTheIndividualPage, NormalMode, ua) mustBe routes.OffshoreLiabilitiesController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the AreYouTheIndividual page to the OffshoreLiabilities controller when the user selects No" in {
        UserAnswers("id").set(AreYouTheIndividualPage, AreYouTheIndividual.No) match {
          case Success(ua) => navigator.nextPage(AreYouTheIndividualPage, NormalMode, ua) mustBe routes.OffshoreLiabilitiesController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the OffshoreLiabilities page to the OnshoreLiabilities controller when the user selects 'I want to disclose offshore liabilities'" in {
        UserAnswers("id").set(OffshoreLiabilitiesPage, OffshoreLiabilities.IWantTo) match {
          case Success(ua) => navigator.nextPage(OffshoreLiabilitiesPage, NormalMode, ua) mustBe routes.OnshoreLiabilitiesController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the OffshoreLiabilities page to the OnshoreLiabilities controller when the user selects 'I do not have offshore liabilities to disclose'" in {
        UserAnswers("id").set(OffshoreLiabilitiesPage, OffshoreLiabilities.IDoNotWantTo) match {
          case Success(ua) => navigator.nextPage(OffshoreLiabilitiesPage, NormalMode, ua) mustBe routes.OnlyOnshoreLiabilitiesController.onPageLoad
          case Failure(e) => throw e
        }
      }

      "must go from the OnshoreLiabilities page to the WhatIsYourFullName controller when the user selects Yes" in {
        UserAnswers("id").set(OnshoreLiabilitiesPage, OnshoreLiabilities.IWantTo) match {
          case Success(ua) => navigator.nextPage(OnshoreLiabilitiesPage, NormalMode, ua) mustBe routes.WhatIsYourFullNameController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the OnshoreLiabilities page to the WhatIsYourFullName controller when the user selects No" in {
        UserAnswers("id").set(OnshoreLiabilitiesPage, OnshoreLiabilities.IDoNotWantTo) match {
          case Success(ua) => navigator.nextPage(OnshoreLiabilitiesPage, NormalMode, ua) mustBe routes.WhatIsYourFullNameController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the WhatIsYourFullName page to the YourPhoneNumber controller when the user enter name" in {
        UserAnswers("id").set(WhatIsYourFullNamePage, "test") match {
          case Success(ua) => navigator.nextPage(WhatIsYourFullNamePage, NormalMode, ua) mustBe routes.YourPhoneNumberController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the YourPhoneNumber page to the YourEmailAddress controller when the user enter the phone number" in {
        UserAnswers("id").set(YourPhoneNumberPage, "test") match {
          case Success(ua) => navigator.nextPage(YourPhoneNumberPage, NormalMode, ua) mustBe routes.DoYouHaveAnEmailAddressController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the DoYouHaveAnEmailAddress page to the WhatIsYourDateOfBirth controller when the user selects No" in {
        UserAnswers("id").set(DoYouHaveAnEmailAddressPage, false) match {
          case Success(ua) => navigator.nextPage(DoYouHaveAnEmailAddressPage, NormalMode, ua) mustBe routes.WhatIsYourDateOfBirthController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the DoYouHaveAnEmailAddress page to the YourEmailAddress controller when the user selects Yes" in {
        UserAnswers("id").set(DoYouHaveAnEmailAddressPage, true) match {
          case Success(ua) => navigator.nextPage(DoYouHaveAnEmailAddressPage, NormalMode, ua) mustBe routes.YourEmailAddressController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the YourEmailAddressPage page to the WhatIsYourDateOfBirthController controller when the user enter an email" in {
        UserAnswers("id").set(YourEmailAddressPage, "test") match {
          case Success(ua) => navigator.nextPage(YourEmailAddressPage, NormalMode, ua) mustBe routes.WhatIsYourDateOfBirthController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the WhatIsYourDateOfBirth page to the WhatIsYourMainOccupation controller when the user enter date of birth" in {
        navigator.nextPage(WhatIsYourDateOfBirthPage, NormalMode, UserAnswers("id")) mustBe routes.WhatIsYourMainOccupationController.onPageLoad(NormalMode)
      }

      "must go from the DoYouHaveNationalInsuranceNumber page to the WhatIsYourNationalInsuranceNumber controller when the user selects Yes, and I know my National Insurance number" in {
        UserAnswers("id").set(DoYouHaveNationalInsuranceNumberPage, DoYouHaveNationalInsuranceNumber.YesIknow) match {
          case Success(ua) => navigator.nextPage(DoYouHaveNationalInsuranceNumberPage, NormalMode, ua) mustBe routes.WhatIsYourNationalInsuranceNumberController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the DoYouHaveNationalInsuranceNumber page to the WhatIsYourVATRegistrationNumber controller when the user selects Yes, but I do not know my National Insurance number" in {
        UserAnswers("id").set(DoYouHaveNationalInsuranceNumberPage, DoYouHaveNationalInsuranceNumber.YesButDontKnow) match {
          case Success(ua) => navigator.nextPage(DoYouHaveNationalInsuranceNumberPage, NormalMode, ua) mustBe routes.WhatIsYourVATRegistrationNumberController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the DoYouHaveNationalInsuranceNumber page to the WhatIsYourVATRegistrationNumber controller when the user selects No" in {
        UserAnswers("id").set(DoYouHaveNationalInsuranceNumberPage, DoYouHaveNationalInsuranceNumber.No) match {
          case Success(ua) => navigator.nextPage(DoYouHaveNationalInsuranceNumberPage, NormalMode, ua) mustBe routes.WhatIsYourVATRegistrationNumberController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the WhatIsYourMainOccupation page to the DoYouHaveNationalInsuranceNumber controller when the user enter the occupation" in {
        UserAnswers("id").set(WhatIsYourMainOccupationPage, "test" ) match {
          case Success(ua) => navigator.nextPage(WhatIsYourMainOccupationPage, NormalMode, ua) mustBe routes.DoYouHaveNationalInsuranceNumberController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the WhatIsYourNationalInsuranceNumberPage to the AreYouRegisteredForVATPage when the user enter national insurance number" in {
        navigator.nextPage(WhatIsYourNationalInsuranceNumberPage, NormalMode, UserAnswers("id")) mustBe routes.AreYouRegisteredForVATController.onPageLoad(NormalMode)
      }

    }

    "in Check mode" - {

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, UserAnswers("id")) mustBe controllers.routes.CheckYourAnswersController.onPageLoad
      }
    }
  }
}
