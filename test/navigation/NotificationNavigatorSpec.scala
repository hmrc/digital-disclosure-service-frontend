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

      "must go from the OnshoreLiabilities page to the WhatIsYourFullName/WhatIsTheIndividualsFullName controller when the user selects Yes" in {
        val userAnswersForIWantToWithYes = for {
          uaWithOnshore <- UserAnswers("id").set(OnshoreLiabilitiesPage, OnshoreLiabilities.IWantTo)
            ua 	<- uaWithOnshore.set(AreYouTheIndividualPage, AreYouTheIndividual.Yes)
          } yield ua
          navigator.nextPage(OnshoreLiabilitiesPage, NormalMode, userAnswersForIWantToWithYes.success.value) mustBe routes.WhatIsYourFullNameController.onPageLoad(NormalMode)

        val userAnswersForIWantToWithNo = for {
            uaWithOnshore <- UserAnswers("id").set(OnshoreLiabilitiesPage, OnshoreLiabilities.IWantTo)
            ua 	<- uaWithOnshore.set(AreYouTheIndividualPage, AreYouTheIndividual.No)
          } yield ua
          navigator.nextPage(OnshoreLiabilitiesPage, NormalMode, userAnswersForIWantToWithNo.success.value) mustBe routes.WhatIsTheIndividualsFullNameController.onPageLoad(NormalMode) 
      }

      "must go from the OnshoreLiabilities page to the WhatIsYourFullName/WhatIsTheIndividualsFullName controller when the user selects No" in {
        val userAnswersForIDoNotWantToWithYes = for {
          uaWithOnshore <- UserAnswers("id").set(OnshoreLiabilitiesPage, OnshoreLiabilities.IDoNotWantTo)
            ua 	<- uaWithOnshore.set(AreYouTheIndividualPage, AreYouTheIndividual.Yes)
          } yield ua
          navigator.nextPage(OnshoreLiabilitiesPage, NormalMode, userAnswersForIDoNotWantToWithYes.success.value) mustBe routes.WhatIsYourFullNameController.onPageLoad(NormalMode)

        val userAnswersForIDoNotWantToWithNo = for {
            uaWithOnshore <- UserAnswers("id").set(OnshoreLiabilitiesPage, OnshoreLiabilities.IDoNotWantTo)
            ua 	<- uaWithOnshore.set(AreYouTheIndividualPage, AreYouTheIndividual.No)
          } yield ua
          navigator.nextPage(OnshoreLiabilitiesPage, NormalMode, userAnswersForIDoNotWantToWithNo.success.value) mustBe routes.WhatIsTheIndividualsFullNameController.onPageLoad(NormalMode) 
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

      "must go from the DoYouHaveNationalInsuranceNumber page to the AreYouRegisteredForVATController when the user selects Yes, but I do not know my National Insurance number" in {
        UserAnswers("id").set(DoYouHaveNationalInsuranceNumberPage, DoYouHaveNationalInsuranceNumber.YesButDontKnow) match {
          case Success(ua) => navigator.nextPage(DoYouHaveNationalInsuranceNumberPage, NormalMode, ua) mustBe routes.AreYouRegisteredForVATController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the DoYouHaveNationalInsuranceNumber page to the AreYouRegisteredForVATController when the user selects No" in {
        UserAnswers("id").set(DoYouHaveNationalInsuranceNumberPage, DoYouHaveNationalInsuranceNumber.No) match {
          case Success(ua) => navigator.nextPage(DoYouHaveNationalInsuranceNumberPage, NormalMode, ua) mustBe routes.AreYouRegisteredForVATController.onPageLoad(NormalMode)
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

      "must go from the AreYouRegisteredForVAT page to the WhatIsYourVATRegistrationNumber controller when the user selects Yes, and I know my VAT registration number" in {
        UserAnswers("id").set(AreYouRegisteredForVATPage, AreYouRegisteredForVAT.YesIKnow) match {
          case Success(ua) => navigator.nextPage(AreYouRegisteredForVATPage, NormalMode, ua) mustBe routes.WhatIsYourVATRegistrationNumberController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the AreYouRegisteredForVAT page to the AreYouRegisteredForSelfAssessmentController when the user selects Yes, but I do not know my VAT registration number" in {
        UserAnswers("id").set(AreYouRegisteredForVATPage, AreYouRegisteredForVAT.YesButDontKnow) match {
          case Success(ua) => navigator.nextPage(AreYouRegisteredForVATPage, NormalMode, ua) mustBe routes.AreYouRegisteredForSelfAssessmentController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the AreYouRegisteredForVAT page to the AreYouRegisteredForSelfAssessmentController when the user selects No" in {
        UserAnswers("id").set(AreYouRegisteredForVATPage, AreYouRegisteredForVAT.No) match {
          case Success(ua) => navigator.nextPage(AreYouRegisteredForVATPage, NormalMode, ua) mustBe routes.AreYouRegisteredForSelfAssessmentController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the AreYouRegisteredForSelfAssessment page to the WhatIsYourUniqueTaxReference controller when the user selects Yes, and I know my UTR" in {
        UserAnswers("id").set(AreYouRegisteredForSelfAssessmentPage, AreYouRegisteredForSelfAssessment.YesIKnowMyUTR) match {
          case Success(ua) => navigator.nextPage(AreYouRegisteredForSelfAssessmentPage, NormalMode, ua) mustBe routes.WhatIsYourUniqueTaxReferenceController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the AreYouRegisteredForSelfAssessment page to the YourAddressLookup controller when the user selects Yes, but I do not know my UTR" in {
        UserAnswers("id").set(AreYouRegisteredForSelfAssessmentPage, AreYouRegisteredForSelfAssessment.YesIDontKnowMyUTR) match {
          case Success(ua) => navigator.nextPage(AreYouRegisteredForSelfAssessmentPage, NormalMode, ua) mustBe routes.YourAddressLookupController.lookupAddress(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the AreYouRegisteredForSelfAssessment page to the YourAddressLookup controller when the user selects No" in {
        UserAnswers("id").set(AreYouRegisteredForSelfAssessmentPage, AreYouRegisteredForSelfAssessment.No) match {
          case Success(ua) => navigator.nextPage(AreYouRegisteredForSelfAssessmentPage, NormalMode, ua) mustBe routes.YourAddressLookupController.lookupAddress(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the WhatIsYourVATRegistrationNumberPage to the AreYouRegisteredForSelfAssessmentPage when the user enter VAT registration number" in {
        navigator.nextPage(WhatIsYourVATRegistrationNumberPage, NormalMode, UserAnswers("id")) mustBe routes.AreYouRegisteredForSelfAssessmentController.onPageLoad(NormalMode)
      }

      "must go from the WhatIsYourUniqueTaxReferencePage to the YourAddressLookupController when the user enter UTR reference number" in {
        navigator.nextPage(WhatIsYourUniqueTaxReferencePage, NormalMode, UserAnswers("id")) mustBe routes.YourAddressLookupController.lookupAddress(NormalMode)
      }

      "must go from the WhatIsTheIndividualsFullNamePage to the WhatIsTheIndividualDateOfBirthControllerController" in {
        navigator.nextPage(WhatIsTheIndividualsFullNamePage, NormalMode, UserAnswers("id")) mustBe routes.WhatIsTheIndividualDateOfBirthControllerController.onPageLoad(NormalMode)
      }

      "must go from the WhatIsTheIndividualOccupationPage to the DoesTheIndividualHaveNationalInsuranceNumberController" in {
        navigator.nextPage(WhatIsTheIndividualOccupationPage, NormalMode, UserAnswers("id")) mustBe routes.DoesTheIndividualHaveNationalInsuranceNumberController.onPageLoad(NormalMode)
      }

      "must go from the DoesTheIndividualHaveNationalInsuranceNumberPage to the WhatIsIndividualsNationalInsuranceNumberController when the user selects Yes, and I know their NINO"in {
        UserAnswers("id").set(DoesTheIndividualHaveNationalInsuranceNumberPage, DoesTheIndividualHaveNationalInsuranceNumber.YesIknow) match {
          case Success(ua) => navigator.nextPage(DoesTheIndividualHaveNationalInsuranceNumberPage, NormalMode, ua) mustBe routes.WhatIsIndividualsNationalInsuranceNumberController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the DoesTheIndividualHaveNationalInsuranceNumberPage to the YourAddressLookup controller when the user selects Yes, but I do not know their UTR" in {
        UserAnswers("id").set(DoesTheIndividualHaveNationalInsuranceNumberPage, DoesTheIndividualHaveNationalInsuranceNumber.YesButDontKnow) match {
          case Success(ua) => navigator.nextPage(DoesTheIndividualHaveNationalInsuranceNumberPage, NormalMode, ua) mustBe routes.IsTheIndividualRegisteredForVATController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the DoesTheIndividualHaveNationalInsuranceNumberPage to the YourAddressLookup controller when the user selects No" in {
        UserAnswers("id").set(DoesTheIndividualHaveNationalInsuranceNumberPage, DoesTheIndividualHaveNationalInsuranceNumber.No) match {
          case Success(ua) => navigator.nextPage(DoesTheIndividualHaveNationalInsuranceNumberPage, NormalMode, ua) mustBe routes.IsTheIndividualRegisteredForVATController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the WhatIsIndividualsNationalInsuranceNumberPage to the IsTheIndividualRegisteredForVATController" in {
        navigator.nextPage(WhatIsIndividualsNationalInsuranceNumberPage, NormalMode, UserAnswers("id")) mustBe routes.IsTheIndividualRegisteredForVATController.onPageLoad(NormalMode)
      }

      "must go from the WhatIsTheIndividualDateOfBirthControllerPage to the WhatIsTheIndividualOccupationController" in {
        navigator.nextPage(WhatIsTheIndividualDateOfBirthControllerPage, NormalMode, UserAnswers("id")) mustBe routes.WhatIsTheIndividualOccupationController.onPageLoad(NormalMode)
      }

      "must go from the IsTheIndividualRegisteredForVATPage to the WhatIsIndividualsNationalInsuranceNumberController when the user selects Yes, and I know their VAT registration number"in {
        UserAnswers("id").set(IsTheIndividualRegisteredForVATPage, IsTheIndividualRegisteredForVAT.YesIKnow) match {
          case Success(ua) => navigator.nextPage(IsTheIndividualRegisteredForVATPage, NormalMode, ua) mustBe routes.WhatIsTheIndividualsVATRegistrationNumberController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the IsTheIndividualRegisteredForVATPage to the IsTheIndividualRegisteredForSelfAssessmentController when the user selects Yes, but I do not know their VAT registration number" in {
        UserAnswers("id").set(IsTheIndividualRegisteredForVATPage, IsTheIndividualRegisteredForVAT.YesButDontKnow) match {
          case Success(ua) => navigator.nextPage(IsTheIndividualRegisteredForVATPage, NormalMode, ua) mustBe routes.IsTheIndividualRegisteredForSelfAssessmentController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the IsTheIndividualRegisteredForVATPage to the IsTheIndividualRegisteredForSelfAssessmentController when the user selects No" in {
        UserAnswers("id").set(IsTheIndividualRegisteredForVATPage, IsTheIndividualRegisteredForVAT.No) match {
          case Success(ua) => navigator.nextPage(IsTheIndividualRegisteredForVATPage, NormalMode, ua) mustBe routes.IsTheIndividualRegisteredForSelfAssessmentController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the WhatIsTheIndividualsVATRegistrationNumberPage to the IsTheIndividualRegisteredForSelfAssessmentController" in {
        navigator.nextPage(WhatIsTheIndividualsVATRegistrationNumberPage, NormalMode, UserAnswers("id")) mustBe routes.IsTheIndividualRegisteredForSelfAssessmentController.onPageLoad(NormalMode)
      }

      "must go from the WhatIsTheIndividualsUniqueTaxReferencePage to the IndividualAddressLookupController" in {
        navigator.nextPage(WhatIsTheIndividualsUniqueTaxReferencePage, NormalMode, UserAnswers("id")) mustBe routes.IndividualAddressLookupController.lookupAddress(NormalMode)
      }

      "must go from the IsTheIndividualRegisteredForSelfAssessmentPage to the WhatIsTheIndividualsUniqueTaxReferenceController when the user selects Yes, and I know their UTR number"in {
        UserAnswers("id").set(IsTheIndividualRegisteredForSelfAssessmentPage, IsTheIndividualRegisteredForSelfAssessment.YesIKnow) match {
          case Success(ua) => navigator.nextPage(IsTheIndividualRegisteredForSelfAssessmentPage, NormalMode, ua) mustBe routes.WhatIsTheIndividualsUniqueTaxReferenceController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the IsTheIndividualRegisteredForSelfAssessmentPage to the IndividualAddressLookupController when the user selects Yes, but I do not know their UTR number" in {
        UserAnswers("id").set(IsTheIndividualRegisteredForSelfAssessmentPage, IsTheIndividualRegisteredForSelfAssessment.YesButDontKnow) match {
          case Success(ua) => navigator.nextPage(IsTheIndividualRegisteredForSelfAssessmentPage, NormalMode, ua) mustBe routes.IndividualAddressLookupController.lookupAddress(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the IsTheIndividualRegisteredForSelfAssessmentPage to the IndividualAddressLookupController when the user selects No" in {
        UserAnswers("id").set(IsTheIndividualRegisteredForSelfAssessmentPage, IsTheIndividualRegisteredForSelfAssessment.No) match {
          case Success(ua) => navigator.nextPage(IsTheIndividualRegisteredForSelfAssessmentPage, NormalMode, ua) mustBe routes.IndividualAddressLookupController.lookupAddress(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the IndividualAddressLookupPage to the WhatIsYourFullNameController" in {
        navigator.nextPage(IndividualAddressLookupPage, NormalMode, UserAnswers("id")) mustBe routes.WhatIsYourFullNameController.onPageLoad(NormalMode)
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
