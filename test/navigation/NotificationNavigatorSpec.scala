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

      "must go from the RelatesTo page to the AreYouTheIndividual controller when select an individual" in {
        UserAnswers("id").set(RelatesToPage, RelatesTo.AnIndividual) match {
          case Success(ua) => navigator.nextPage(RelatesToPage, NormalMode, ua) mustBe routes.AreYouTheIndividualController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the RelatesTo page to the AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAbout controller when select a company" in {
        UserAnswers("id").set(RelatesToPage, RelatesTo.ACompany) match {
          case Success(ua) => navigator.nextPage(RelatesToPage, NormalMode, ua) mustBe routes.AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the RelatesTo page to the AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutController controller when select an LLP" in {
        UserAnswers("id").set(RelatesToPage, RelatesTo.ALimitedLiabilityPartnership) match {
          case Success(ua) => navigator.nextPage(RelatesToPage, NormalMode, ua) mustBe routes.AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the RelatesTo page to the AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutController when select a Trust" in {
        UserAnswers("id").set(RelatesToPage, RelatesTo.ATrust) match {
          case Success(ua) => navigator.nextPage(RelatesToPage, NormalMode, ua) mustBe routes.AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the RelatesTo page to the AreYouTheExecutorOfTheEstateController when select a Trust" in {
        UserAnswers("id").set(RelatesToPage, RelatesTo.AnEstate) match {
          case Success(ua) => navigator.nextPage(RelatesToPage, NormalMode, ua) mustBe routes.AreYouTheExecutorOfTheEstateController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the AreYouTheIndividual page to the OffshoreLiabilities controller when the user selects Yes" in {
        UserAnswers("id").set(AreYouTheIndividualPage, true) match {
          case Success(ua) => navigator.nextPage(AreYouTheIndividualPage, NormalMode, ua) mustBe routes.OffshoreLiabilitiesController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the AreYouTheIndividual page to the AreYouRepresentingAnOrganisationController controller when the user selects No" in {
        UserAnswers("id").set(AreYouTheIndividualPage, false) match {
          case Success(ua) => navigator.nextPage(AreYouTheIndividualPage, NormalMode, ua) mustBe routes.AreYouRepresentingAnOrganisationController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the OffshoreLiabilities page to the OnshoreLiabilities controller when the user selects 'I want to disclose offshore liabilities'" in {
        UserAnswers("id").set(OffshoreLiabilitiesPage, true) match {
          case Success(ua) => navigator.nextPage(OffshoreLiabilitiesPage, NormalMode, ua) mustBe routes.OnshoreLiabilitiesController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the OffshoreLiabilities page to the OnshoreLiabilities controller when the user selects 'I do not have offshore liabilities to disclose'" in {
        UserAnswers("id").set(OffshoreLiabilitiesPage, false) match {
          case Success(ua) => navigator.nextPage(OffshoreLiabilitiesPage, NormalMode, ua) mustBe routes.OnlyOnshoreLiabilitiesController.onPageLoad
          case Failure(e) => throw e
        }
      }

      testOnshoreLiabilitiesRouting(OnshoreLiabilitiesPage)

      testOnshoreLiabilitiesRouting(OnlyOnshoreLiabilitiesPage)

      "must go from the WhatIsYourFullName page to the YourPhoneNumber controller when the user enter name" in {
        val ua = UserAnswers("id").set(WhatIsYourFullNamePage, "test").success.value
        navigator.nextPage(WhatIsYourFullNamePage, NormalMode, ua) mustBe routes.HowWouldYouPreferToBeContactedController.onPageLoad(NormalMode)
      }

      "must go from the YourEmailAddressPage page to the WhatIsYourDateOfBirthController controller when the user enter an email and is an individual" in {
        val userAnswers = for {
          uaWithOnshore <- UserAnswers("id").set(YourEmailAddressPage, "test")
            ua 	<- uaWithOnshore.set(AreYouTheIndividualPage, true)
          } yield ua
        navigator.nextPage(YourEmailAddressPage, NormalMode, userAnswers.success.value) mustBe routes.WhatIsYourDateOfBirthController.onPageLoad(NormalMode)
      }

      "must go from the YourEmailAddressPage page to the YourAddressLookupController controller when the user enter an email and on behalf of individual" in {
        val userAnswers = for {
          uaWithOnshore <- UserAnswers("id").set(YourEmailAddressPage, "test")
            ua 	<- uaWithOnshore.set(AreYouTheIndividualPage, false)
          } yield ua
        navigator.nextPage(YourEmailAddressPage, NormalMode, userAnswers.success.value) mustBe routes.YourAddressLookupController.lookupAddress(NormalMode)
      }

      "must go from the YourEmailAddressPage page to the YourAddressLookupController controller when the user enter an email and Yes, I am an officer of the company" in {
        val userAnswers = for {
          uaWithOnshore <- UserAnswers("id").set(YourEmailAddressPage, "test")
          ua 	<- uaWithOnshore.set(AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage, true)
        } yield ua
        navigator.nextPage(YourEmailAddressPage, NormalMode, userAnswers.success.value) mustBe routes.YourAddressLookupController.lookupAddress(NormalMode)
      }

      "must go from the YourEmailAddressPage page to the YourAddressLookupController controller when the user enter an email and No, I will be making a disclosure on behalf of an officerl" in {
        val userAnswers = for {
          uaWithOnshore <- UserAnswers("id").set(YourEmailAddressPage, "test")
          ua 	<- uaWithOnshore.set(AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage, false)
        } yield ua
        navigator.nextPage(YourEmailAddressPage, NormalMode, userAnswers.success.value) mustBe routes.YourAddressLookupController.lookupAddress(NormalMode)
      }

      "must go from the WhatIsYourDateOfBirth page to the WhatIsYourMainOccupation controller when the user enter date of birth" in {
        navigator.nextPage(WhatIsYourDateOfBirthPage, NormalMode, UserAnswers("id")) mustBe routes.WhatIsYourMainOccupationController.onPageLoad(NormalMode)
      }

      "must go from the DoYouHaveNationalInsuranceNumber page to the WhatIsYourNationalInsuranceNumber controller when the user selects Yes, and I know my National Insurance number" in {
        UserAnswers("id").set(DoYouHaveNationalInsuranceNumberPage, DoYouHaveNationalInsuranceNumber.YesIKnow) match {
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
        UserAnswers("id").set(DoesTheIndividualHaveNationalInsuranceNumberPage, DoesTheIndividualHaveNationalInsuranceNumber.YesIKnow) match {
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

      "must go from the CompanyAddressLookupPage to the WhatIsYourFullNameController" in {
        navigator.nextPage(CompanyAddressLookupPage, NormalMode, UserAnswers("id")) mustBe routes.WhatIsYourFullNameController.onPageLoad(NormalMode)
      }

      "must go from the EstateAddressLookupPage to the WhatIsYourFullNameController" in {
        navigator.nextPage(EstateAddressLookupPage, NormalMode, UserAnswers("id")) mustBe routes.WhatIsYourFullNameController.onPageLoad(NormalMode)
      }

      "must go from the Your Address Lookup page to the CheckYourAnswersController" in {
        navigator.nextPage(YourAddressLookupPage, NormalMode, UserAnswers("id")) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage to the OffshoreLiabilitiesController when the user selects Yes, I am an officer of the company" in {
        UserAnswers("id").set(AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage, true) match {
          case Success(ua) => navigator.nextPage(AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage, NormalMode, ua) mustBe routes.OffshoreLiabilitiesController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage to the AreYouRepresentingAnOrganisationController when the user selects No, I will be making a disclosure on behalf of an officer" in {
        UserAnswers("id").set(AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage, false) match {
          case Success(ua) => navigator.nextPage(AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage, NormalMode, ua) mustBe routes.AreYouRepresentingAnOrganisationController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the AreYouRepresentingAnOrganisationPage to the WhatIsTheNameOfTheOrganisationYouRepresentController when the user selects Yes" in {
        UserAnswers("id").set(AreYouRepresentingAnOrganisationPage, true) match {
          case Success(ua) => navigator.nextPage(AreYouRepresentingAnOrganisationPage, NormalMode, ua) mustBe routes.WhatIsTheNameOfTheOrganisationYouRepresentController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the AreYouRepresentingAnOrganisationPage to the OffshoreLiabilitiesController when the user selects No" in {
        UserAnswers("id").set(AreYouRepresentingAnOrganisationPage, false) match {
          case Success(ua) => navigator.nextPage(AreYouRepresentingAnOrganisationPage, NormalMode, ua) mustBe routes.OffshoreLiabilitiesController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage to the AreYouRepresentingAnOrganisationController when the user selects Yes" in {
        UserAnswers("id").set(AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage, false) match {
          case Success(ua) => navigator.nextPage(AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage, NormalMode, ua) mustBe routes.AreYouRepresentingAnOrganisationController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage to the OffshoreLiabilitiesController when the user selects Yes" in {
        UserAnswers("id").set(AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage, true) match {
          case Success(ua) => navigator.nextPage(AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage, NormalMode, ua) mustBe routes.OffshoreLiabilitiesController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the WhatIsTheNameOfTheOrganisationYouRepresentPage to the OffshoreLiabilitiesController when the user enter organisation name" in {
        navigator.nextPage(WhatIsTheNameOfTheOrganisationYouRepresentPage, NormalMode, UserAnswers("id")) mustBe routes.OffshoreLiabilitiesController.onPageLoad(NormalMode)
      }

      "must go from the WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage to the WhatIsTheCompanyRegistrationNumberController when the user enter company name" in {
        navigator.nextPage(WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage, NormalMode, UserAnswers("id")) mustBe routes.WhatIsTheCompanyRegistrationNumberController.onPageLoad(NormalMode)
      }

      "must go from the WhatIsTheCompanyRegistrationNumberPage to the CompanyAddressLookupController" in {
        navigator.nextPage(WhatIsTheCompanyRegistrationNumberPage, NormalMode, UserAnswers("id")) mustBe routes.CompanyAddressLookupController.lookupAddress(NormalMode)
      }

      "must go from the WhatIsTheLLPNamePage to the LLPAddressLookupController when the user enter llp name" in {
        navigator.nextPage(WhatIsTheLLPNamePage, NormalMode, UserAnswers("id")) mustBe routes.LLPAddressLookupController.lookupAddress(NormalMode)
      }

      "must go from the LLPAddressLookupPage to the WhatIsYourFullNameController when the user enter llp name" in {
        navigator.nextPage(LLPAddressLookupPage, NormalMode, UserAnswers("id")) mustBe routes.WhatIsYourFullNameController.onPageLoad(NormalMode)
      }

      "must go from the AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage to the AreYouRepresentingAnOrganisationController when the user selects Yes" in {
        UserAnswers("id").set(AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage, false) match {
          case Success(ua) => navigator.nextPage(AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage, NormalMode, ua) mustBe routes.AreYouRepresentingAnOrganisationController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage to the OffshoreLiabilitiesController when the user selects Yes" in {
        UserAnswers("id").set(AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage, true) match {
          case Success(ua) => navigator.nextPage(AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage, NormalMode, ua) mustBe routes.OffshoreLiabilitiesController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the WhatIsTheTrustNamePage to the TrustAddressLookupController when the user enter trust name" in {
        navigator.nextPage(WhatIsTheTrustNamePage, NormalMode, UserAnswers("id")) mustBe routes.TrustAddressLookupController.lookupAddress(NormalMode)
      }

      "must go from the TrustAddressLookupPage to the WhatIsYourFullNameController when the user enter trust address" in {
        navigator.nextPage(TrustAddressLookupPage, NormalMode, UserAnswers("id")) mustBe routes.WhatIsYourFullNameController.onPageLoad(NormalMode)
      }

      "must go from the AreYouTheExecutorOfTheEstatePage to the AreYouRepresentingAnOrganisationController when the user selects Yes" in {
        UserAnswers("id").set(AreYouTheExecutorOfTheEstatePage, false) match {
          case Success(ua) => navigator.nextPage(AreYouTheExecutorOfTheEstatePage, NormalMode, ua) mustBe routes.AreYouRepresentingAnOrganisationController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the AreYouTheExecutorOfTheEstatePage to the OffshoreLiabilitiesController when the user selects Yes" in {
        UserAnswers("id").set(AreYouTheExecutorOfTheEstatePage, true) match {
          case Success(ua) => navigator.nextPage(AreYouTheExecutorOfTheEstatePage, NormalMode, ua) mustBe routes.OffshoreLiabilitiesController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the WhatWasTheNameOfThePersonWhoDiedPage to the WhatWasThePersonDateOfBirthController when the user enter a valid name" in {
        navigator.nextPage(WhatWasTheNameOfThePersonWhoDiedPage, NormalMode, UserAnswers("id")) mustBe routes.WhatWasThePersonDateOfBirthController.onPageLoad(NormalMode)
      }

      "must go from the WhatWasThePersonDateOfBirthPage to the WhatWasThePersonOccupationController when the user enter a valid name" in {
        navigator.nextPage(WhatWasThePersonDateOfBirthPage, NormalMode, UserAnswers("id")) mustBe routes.WhatWasThePersonOccupationController.onPageLoad(NormalMode)
      }

      "must go from the WhatWasThePersonOccupationPage to the DidThePersonHaveNINOController when the user enter a valid name" in {
        navigator.nextPage(WhatWasThePersonOccupationPage, NormalMode, UserAnswers("id")) mustBe routes.DidThePersonHaveNINOController.onPageLoad(NormalMode)
      }

      "must go from the DidThePersonHaveNINOPage to the WhatWasThePersonNINOController when the user selects Yes, and I know their NINO" in {
        UserAnswers("id").set(DidThePersonHaveNINOPage, DidThePersonHaveNINO.YesIKnow) match {
          case Success(ua) => navigator.nextPage(DidThePersonHaveNINOPage, NormalMode, ua) mustBe routes.WhatWasThePersonNINOController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the DidThePersonHaveNINOPage to the WasThePersonRegisteredForVATController when the user selects Yes, but I do not know their NINO" in {
        UserAnswers("id").set(DidThePersonHaveNINOPage, DidThePersonHaveNINO.YesButDontKnow) match {
          case Success(ua) => navigator.nextPage(DidThePersonHaveNINOPage, NormalMode, ua) mustBe routes.WasThePersonRegisteredForVATController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the IsTheIndividualRegisteredForSelfAssessmentPage to the WasThePersonRegisteredForVATController when the user selects No" in {
        UserAnswers("id").set(DidThePersonHaveNINOPage, DidThePersonHaveNINO.No) match {
          case Success(ua) => navigator.nextPage(DidThePersonHaveNINOPage, NormalMode, ua) mustBe routes.WasThePersonRegisteredForVATController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the WhatWasThePersonNINOPage to the WasThePersonRegisteredForVATController when the user enter a valid NINO" in {
        navigator.nextPage(WhatWasThePersonNINOPage, NormalMode, UserAnswers("id")) mustBe routes.WasThePersonRegisteredForVATController.onPageLoad(NormalMode)
      }

      "must go from the WasThePersonRegisteredForVATPage to the WhatWasThePersonVATRegistrationNumberController when the user selects Yes, and I know their VAT" in {
        UserAnswers("id").set(WasThePersonRegisteredForVATPage, WasThePersonRegisteredForVAT.YesIKnow) match {
          case Success(ua) => navigator.nextPage(WasThePersonRegisteredForVATPage, NormalMode, ua) mustBe routes.WhatWasThePersonVATRegistrationNumberController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the WasThePersonRegisteredForVATPage to the WasThePersonRegisteredForSAController when the user selects Yes, but I do not know their VAT" in {
        UserAnswers("id").set(WasThePersonRegisteredForVATPage, WasThePersonRegisteredForVAT.YesButIDontKnow) match {
          case Success(ua) => navigator.nextPage(WasThePersonRegisteredForVATPage, NormalMode, ua) mustBe routes.WasThePersonRegisteredForSAController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the IsTheIndividualRegisteredForSelfAssessmentPage to the WasThePersonRegisteredForSAController when the user selects No" in {
        UserAnswers("id").set(WasThePersonRegisteredForVATPage, WasThePersonRegisteredForVAT.No) match {
          case Success(ua) => navigator.nextPage(WasThePersonRegisteredForVATPage, NormalMode, ua) mustBe routes.WasThePersonRegisteredForSAController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the WhatWasThePersonVATRegistrationNumberPage to the WasThePersonRegisteredForSAController when the user enter a valid VAT" in {
        navigator.nextPage(WhatWasThePersonVATRegistrationNumberPage, NormalMode, UserAnswers("id")) mustBe routes.WasThePersonRegisteredForSAController.onPageLoad(NormalMode)
      }

      "must go from the WasThePersonRegisteredForSAPage to the WhatWasThePersonUTRController when the user selects Yes, and I know their UTR" in {
        UserAnswers("id").set(WasThePersonRegisteredForSAPage, WasThePersonRegisteredForSA.YesIKnow) match {
          case Success(ua) => navigator.nextPage(WasThePersonRegisteredForSAPage, NormalMode, ua) mustBe routes.WhatWasThePersonUTRController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the WasThePersonRegisteredForSAPage to the EstateAddressLookupController when the user selects Yes, but I do not know their UTR" in {
        UserAnswers("id").set(WasThePersonRegisteredForSAPage, WasThePersonRegisteredForSA.YesButIDontKnow) match {
          case Success(ua) => navigator.nextPage(WasThePersonRegisteredForSAPage, NormalMode, ua) mustBe routes.EstateAddressLookupController.lookupAddress(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the IsTheIndividualRegisteredForSelfAssessmentPage to the EstateAddressLookupController when the user selects No" in {
        UserAnswers("id").set(WasThePersonRegisteredForSAPage, WasThePersonRegisteredForSA.No) match {
          case Success(ua) => navigator.nextPage(WasThePersonRegisteredForSAPage, NormalMode, ua) mustBe routes.EstateAddressLookupController.lookupAddress(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the WhatWasThePersonUTRPage to the EstateAddressLookupController when the user enter a valid UTR" in {
        navigator.nextPage(WhatWasThePersonUTRPage, NormalMode, UserAnswers("id")) mustBe routes.EstateAddressLookupController.lookupAddress(NormalMode)
      }

      "must go from the MakeANotificationOrDisclosure page to the ReceivedALetter controller when select an MakeANotification" in {
        UserAnswers("id").set(MakeANotificationOrDisclosurePage, MakeANotificationOrDisclosure.MakeANotification) match {
          case Success(ua) => navigator.nextPage(MakeANotificationOrDisclosurePage, NormalMode, ua) mustBe routes.ReceivedALetterController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the HowWouldYouPreferToBeContactedPage to the YourEmailAddressController when the user selects only Email as preferred method" in {
        val answer: Set[HowWouldYouPreferToBeContacted] = Set(HowWouldYouPreferToBeContacted.Email)
        val ua = UserAnswers("id").set(HowWouldYouPreferToBeContactedPage, answer).success.value
        navigator.nextPage(HowWouldYouPreferToBeContactedPage, NormalMode, ua) mustBe routes.YourEmailAddressController.onPageLoad(NormalMode)
      }

      "must go from the HowWouldYouPreferToBeContactedPage to the YourPhoneNumberController when the user selects only Telephone as preferred method" in {
        val answer: Set[HowWouldYouPreferToBeContacted] = Set(HowWouldYouPreferToBeContacted.Telephone)
        val ua = UserAnswers("id").set(HowWouldYouPreferToBeContactedPage, answer).success.value
        navigator.nextPage(HowWouldYouPreferToBeContactedPage, NormalMode, ua) mustBe routes.YourPhoneNumberController.onPageLoad(NormalMode)
      }

      "must go from the HowWouldYouPreferToBeContactedPage to the YourEmailAddressController when the user selects Email and Telephone as preferred method" in {
        val answer: Set[HowWouldYouPreferToBeContacted] = Set(HowWouldYouPreferToBeContacted.Email, HowWouldYouPreferToBeContacted.Telephone)
        val ua = UserAnswers("id").set(HowWouldYouPreferToBeContactedPage, answer).success.value
        navigator.nextPage(HowWouldYouPreferToBeContactedPage, NormalMode, ua) mustBe routes.YourEmailAddressController.onPageLoad(NormalMode)
      }

    }

    "in Check mode" - {

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, UserAnswers("id")) mustBe routes.CheckYourAnswersController.onPageLoad
      }

     "must go from the AreYouRegisteredForVATPage to WhatIsYourVATRegistrationNumberController where the answer is YesIKnow and has changed" in {
        val userAnswers = UserAnswers("id").set(AreYouRegisteredForVATPage, AreYouRegisteredForVAT.YesIKnow).success.value
        navigator.nextPage(AreYouRegisteredForVATPage, CheckMode, userAnswers, true) mustBe routes.WhatIsYourVATRegistrationNumberController.onPageLoad(CheckMode)
      }

      "must go from the AreYouRegisteredForVATPage to CheckYourAnswers where the answer is No and has changed" in {
        val userAnswers = UserAnswers("id").set(AreYouRegisteredForVATPage, AreYouRegisteredForVAT.No).success.value
        navigator.nextPage(AreYouRegisteredForVATPage, CheckMode, userAnswers, true) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the AreYouRegisteredForVATPage to CheckYourAnswers where the answer is YesButDontKnow and has changed" in {
        val userAnswers = UserAnswers("id").set(AreYouRegisteredForVATPage, AreYouRegisteredForVAT.YesButDontKnow).success.value
        navigator.nextPage(AreYouRegisteredForVATPage, CheckMode, userAnswers, true) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the AreYouRegisteredForVATPage to CheckYourAnswers where the answer is YesIKnow but has NOT changed" in {
        val userAnswers = UserAnswers("id").set(AreYouRegisteredForVATPage, AreYouRegisteredForVAT.YesIKnow).success.value
        navigator.nextPage(AreYouRegisteredForVATPage, CheckMode, userAnswers, false) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the AreYouRegisteredForSelfAssessmentPage to WhatIsYourUniqueTaxReferenceController where the answer is YesIKnowMyUTR and has changed" in {
        val userAnswers = UserAnswers("id").set(AreYouRegisteredForSelfAssessmentPage, AreYouRegisteredForSelfAssessment.YesIKnowMyUTR).success.value
        navigator.nextPage(AreYouRegisteredForSelfAssessmentPage, CheckMode, userAnswers, true) mustBe routes.WhatIsYourUniqueTaxReferenceController.onPageLoad(CheckMode)
      }

      "must go from the AreYouRegisteredForSelfAssessmentPage to CheckYourAnswers where the answer is No and has changed" in {
        val userAnswers = UserAnswers("id").set(AreYouRegisteredForSelfAssessmentPage, AreYouRegisteredForSelfAssessment.No).success.value
        navigator.nextPage(AreYouRegisteredForSelfAssessmentPage, CheckMode, userAnswers, false) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the AreYouRegisteredForSelfAssessmentPage to CheckYourAnswers where the answer is YesIDontKnowMyUTR and has changed" in {
        val userAnswers = UserAnswers("id").set(AreYouRegisteredForSelfAssessmentPage, AreYouRegisteredForSelfAssessment.YesIDontKnowMyUTR).success.value
        navigator.nextPage(AreYouRegisteredForSelfAssessmentPage, CheckMode, userAnswers, false) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the AreYouRegisteredForSelfAssessmentPage to CheckYourAnswers where the answer is YesIKnowMyUTR but has NOT changed" in {
        val userAnswers = UserAnswers("id").set(AreYouRegisteredForSelfAssessmentPage, AreYouRegisteredForSelfAssessment.YesIKnowMyUTR).success.value
        navigator.nextPage(AreYouRegisteredForSelfAssessmentPage, CheckMode, userAnswers, false) mustBe routes.CheckYourAnswersController.onPageLoad
      }
      
      "must go from the IsTheIndividualRegisteredForVATPage to WhatIsTheIndividualsVATRegistrationNumberPage where the answer is YesIKnow and has changed" in {
        val userAnswers = UserAnswers("id").set(IsTheIndividualRegisteredForVATPage, IsTheIndividualRegisteredForVAT.YesIKnow).success.value
        navigator.nextPage(IsTheIndividualRegisteredForVATPage, CheckMode, userAnswers, true) mustBe routes.WhatIsTheIndividualsVATRegistrationNumberController.onPageLoad(CheckMode)
      }

      "must go from the IsTheIndividualRegisteredForVATPage to CheckYourAnswers where the answer is No and has changed" in {
        val userAnswers = UserAnswers("id").set(IsTheIndividualRegisteredForVATPage, IsTheIndividualRegisteredForVAT.No).success.value
        navigator.nextPage(IsTheIndividualRegisteredForVATPage, CheckMode, userAnswers, true) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the IsTheIndividualRegisteredForVATPage to CheckYourAnswers where the answer is YesButDontKnow and has changed" in {
        val userAnswers = UserAnswers("id").set(IsTheIndividualRegisteredForVATPage, IsTheIndividualRegisteredForVAT.YesButDontKnow).success.value
        navigator.nextPage(IsTheIndividualRegisteredForVATPage, CheckMode, userAnswers, true) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the IsTheIndividualRegisteredForVATPage to CheckYourAnswers where the answer is YesIKnow but has NOT changed" in {
        val userAnswers = UserAnswers("id").set(IsTheIndividualRegisteredForVATPage, IsTheIndividualRegisteredForVAT.YesIKnow).success.value
        navigator.nextPage(IsTheIndividualRegisteredForVATPage, CheckMode, userAnswers, false) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the IsTheIndividualRegisteredForSelfAssessmentPage to WhatIsTheIndividualsVATRegistrationNumberPage where the answer is YesIKnow and has changed" in {
        val userAnswers = UserAnswers("id").set(IsTheIndividualRegisteredForSelfAssessmentPage, IsTheIndividualRegisteredForSelfAssessment.YesIKnow).success.value
        navigator.nextPage(IsTheIndividualRegisteredForSelfAssessmentPage, CheckMode, userAnswers, true) mustBe routes.WhatIsTheIndividualsUniqueTaxReferenceController.onPageLoad(CheckMode)
      }

      "must go from the IsTheIndividualRegisteredForSelfAssessmentPage to CheckYourAnswers where the answer is No and has changed" in {
        val userAnswers = UserAnswers("id").set(IsTheIndividualRegisteredForSelfAssessmentPage, IsTheIndividualRegisteredForSelfAssessment.No).success.value
        navigator.nextPage(IsTheIndividualRegisteredForSelfAssessmentPage, CheckMode, userAnswers, true) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the IsTheIndividualRegisteredForSelfAssessmentPage to CheckYourAnswers where the answer is YesButDontKnow and has changed" in {
        val userAnswers = UserAnswers("id").set(IsTheIndividualRegisteredForSelfAssessmentPage, IsTheIndividualRegisteredForSelfAssessment.YesButDontKnow).success.value
        navigator.nextPage(IsTheIndividualRegisteredForSelfAssessmentPage, CheckMode, userAnswers, true) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the IsTheIndividualRegisteredForSelfAssessmentPage to CheckYourAnswers where the answer is YesIKnow but has NOT changed" in {
        val userAnswers = UserAnswers("id").set(IsTheIndividualRegisteredForSelfAssessmentPage, IsTheIndividualRegisteredForSelfAssessment.YesIKnow).success.value
        navigator.nextPage(IsTheIndividualRegisteredForSelfAssessmentPage, CheckMode, userAnswers, false) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the DoesTheIndividualHaveNationalInsuranceNumberPage to WhatIsIndividualsNationalInsuranceNumberPage where the answer is YesIKnow and has changed" in {
        val userAnswers = UserAnswers("id").set(DoesTheIndividualHaveNationalInsuranceNumberPage, DoesTheIndividualHaveNationalInsuranceNumber.YesIKnow).success.value
        navigator.nextPage(DoesTheIndividualHaveNationalInsuranceNumberPage, CheckMode, userAnswers, true) mustBe routes.WhatIsIndividualsNationalInsuranceNumberController.onPageLoad(CheckMode)
      }

      "must go from the DoesTheIndividualHaveNationalInsuranceNumberPage to CheckYourAnswers where the answer is No and has changed" in {
        val userAnswers = UserAnswers("id").set(DoesTheIndividualHaveNationalInsuranceNumberPage, DoesTheIndividualHaveNationalInsuranceNumber.No).success.value
        navigator.nextPage(DoesTheIndividualHaveNationalInsuranceNumberPage, CheckMode, userAnswers, true) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the DoesTheIndividualHaveNationalInsuranceNumberPage to CheckYourAnswers where the answer is YesButDontKnow and has changed" in {
        val userAnswers = UserAnswers("id").set(DoesTheIndividualHaveNationalInsuranceNumberPage, DoesTheIndividualHaveNationalInsuranceNumber.YesButDontKnow).success.value
        navigator.nextPage(DoesTheIndividualHaveNationalInsuranceNumberPage, CheckMode, userAnswers, true) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the DoesTheIndividualHaveNationalInsuranceNumberPage to CheckYourAnswers where the answer is YesIKnow but has NOT changed" in {
        val userAnswers = UserAnswers("id").set(DoesTheIndividualHaveNationalInsuranceNumberPage, DoesTheIndividualHaveNationalInsuranceNumber.YesIKnow).success.value
        navigator.nextPage(DoesTheIndividualHaveNationalInsuranceNumberPage, CheckMode, userAnswers, false) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage to CheckYourAnswers where the answer is No and has not changed" in {
        val userAnswers = UserAnswers("id").set(AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage, false).success.value
        navigator.nextPage(AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage, CheckMode, userAnswers, false) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage to CheckYourAnswers where the answer is Yes and has not changed" in {
        val userAnswers = UserAnswers("id").set(AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage, true).success.value
        navigator.nextPage(AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage, CheckMode, userAnswers, false) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage to AreYouRepresentingAnOrganisationController where the answer is Yes and has changed to No" in {
        val userAnswers = UserAnswers("id").set(AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage, false).success.value
        navigator.nextPage(AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage, CheckMode, userAnswers, true) mustBe routes.AreYouRepresentingAnOrganisationController.onPageLoad(CheckMode)
      }

      "must go from the AreYouRepresentingAnOrganisationPage to WhatIsTheNameOfTheOrganisationYouRepresentController where the answer is Yes" in {
        val userAnswers = UserAnswers("id").set(AreYouRepresentingAnOrganisationPage, true).success.value
        navigator.nextPage(AreYouRepresentingAnOrganisationPage, CheckMode, userAnswers, true) mustBe routes.WhatIsTheNameOfTheOrganisationYouRepresentController.onPageLoad(CheckMode)
      }

      "must go from the AreYouRepresentingAnOrganisationPage to CheckYourAnswers where the answer is No" in {
        val userAnswers = UserAnswers("id").set(AreYouRepresentingAnOrganisationPage, false).success.value
        navigator.nextPage(AreYouRepresentingAnOrganisationPage, CheckMode, userAnswers, false) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage to CheckYourAnswers where the answer is No" in {
        val userAnswers = UserAnswers("id").set(AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage, false).success.value
        navigator.nextPage(AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage, CheckMode, userAnswers) mustBe routes.AreYouRepresentingAnOrganisationController.onPageLoad(CheckMode)
      }

      "must go from the AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage to CheckYourAnswers where the answer is Yes and has not changed" in {
        val userAnswers = UserAnswers("id").set(AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage, true).success.value
        navigator.nextPage(AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage, CheckMode, userAnswers, false) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage to CheckYourAnswers where the answer is No" in {
        val userAnswers = UserAnswers("id").set(AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage, false).success.value
        navigator.nextPage(AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage, CheckMode, userAnswers) mustBe routes.AreYouRepresentingAnOrganisationController.onPageLoad(CheckMode)
      }

      "must go from the AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage to CheckYourAnswers where the answer is Yes and has not changed" in {
        val userAnswers = UserAnswers("id").set(AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage, true).success.value
        navigator.nextPage(AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage, CheckMode, userAnswers, false) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the DidThePersonHaveNINOPage to WhatIsIndividualsNationalInsuranceNumberPage where the answer is YesIKnow and has changed" in {
        val userAnswers = UserAnswers("id").set(DidThePersonHaveNINOPage, DidThePersonHaveNINO.YesIKnow).success.value
        navigator.nextPage(DidThePersonHaveNINOPage, CheckMode, userAnswers, true) mustBe routes.WhatWasThePersonNINOController.onPageLoad(CheckMode)
      }

      "must go from the DidThePersonHaveNINOPage to CheckYourAnswers where the answer is No and has changed" in {
        val userAnswers = UserAnswers("id").set(DidThePersonHaveNINOPage, DidThePersonHaveNINO.No).success.value
        navigator.nextPage(DidThePersonHaveNINOPage, CheckMode, userAnswers, true) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the DidThePersonHaveNINOPage to CheckYourAnswers where the answer is YesButDontKnow and has changed" in {
        val userAnswers = UserAnswers("id").set(DidThePersonHaveNINOPage, DidThePersonHaveNINO.YesButDontKnow).success.value
        navigator.nextPage(DidThePersonHaveNINOPage, CheckMode, userAnswers, true) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the DidThePersonHaveNINOPage to CheckYourAnswers where the answer is YesIKnow but has NOT changed" in {
        val userAnswers = UserAnswers("id").set(DidThePersonHaveNINOPage, DidThePersonHaveNINO.YesIKnow).success.value
        navigator.nextPage(DidThePersonHaveNINOPage, CheckMode, userAnswers, false) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the WasThePersonRegisteredForSAPage to WhatWasThePersonUTRC page where the answer is YesIKnow and has changed" in {
        val userAnswers = UserAnswers("id").set(WasThePersonRegisteredForSAPage, WasThePersonRegisteredForSA.YesIKnow).success.value
        navigator.nextPage(WasThePersonRegisteredForSAPage, CheckMode, userAnswers, true) mustBe routes.WhatWasThePersonUTRController.onPageLoad(CheckMode)
      }
      "must go from the WasThePersonRegisteredForSAPage to CheckYourAnswers where the answer is No and has changed" in {
        val userAnswers = UserAnswers("id").set(WasThePersonRegisteredForSAPage, WasThePersonRegisteredForSA.No).success.value
        navigator.nextPage(WasThePersonRegisteredForSAPage, CheckMode, userAnswers, true) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the WasThePersonRegisteredForSAPage to CheckYourAnswers where the answer is YesButDontKnow and has changed" in {
        val userAnswers = UserAnswers("id").set(WasThePersonRegisteredForSAPage, WasThePersonRegisteredForSA.YesButIDontKnow).success.value
        navigator.nextPage(WasThePersonRegisteredForSAPage, CheckMode, userAnswers, true) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the WasThePersonRegisteredForSAPage to CheckYourAnswers where the answer is YesIKnow but has NOT changed" in {
        val userAnswers = UserAnswers("id").set(WasThePersonRegisteredForSAPage, WasThePersonRegisteredForSA.YesIKnow).success.value
        navigator.nextPage(WasThePersonRegisteredForSAPage, CheckMode, userAnswers, false) mustBe routes.CheckYourAnswersController.onPageLoad
      }
      
      "must go from the WasThePersonRegisteredForVATPage to WhatWasThePersonVATRegistrationNumber page where the answer is YesIKnow and has changed" in {
        val userAnswers = UserAnswers("id").set(WasThePersonRegisteredForVATPage, WasThePersonRegisteredForVAT.YesIKnow).success.value
        navigator.nextPage(WasThePersonRegisteredForVATPage, CheckMode, userAnswers, true) mustBe routes.WhatWasThePersonVATRegistrationNumberController.onPageLoad(CheckMode)
      }
      "must go from the WasThePersonRegisteredForVATPage to CheckYourAnswers where the answer is No and has changed" in {
        val userAnswers = UserAnswers("id").set(WasThePersonRegisteredForVATPage, WasThePersonRegisteredForVAT.No).success.value
        navigator.nextPage(WasThePersonRegisteredForVATPage, CheckMode, userAnswers, true) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the WasThePersonRegisteredForVATPage to CheckYourAnswers where the answer is YesButDontKnow and has changed" in {
        val userAnswers = UserAnswers("id").set(WasThePersonRegisteredForVATPage, WasThePersonRegisteredForVAT.YesButIDontKnow).success.value
        navigator.nextPage(WasThePersonRegisteredForVATPage, CheckMode, userAnswers, true) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the WasThePersonRegisteredForVATPage to CheckYourAnswers where the answer is YesIKnow but has NOT changed" in {
        val userAnswers = UserAnswers("id").set(WasThePersonRegisteredForVATPage, WasThePersonRegisteredForVAT.YesIKnow).success.value
        navigator.nextPage(WasThePersonRegisteredForVATPage, CheckMode, userAnswers, false) mustBe routes.CheckYourAnswersController.onPageLoad
      }
    }
  }

  def testOnshoreLiabilitiesRouting(page: Page) = {
    s"must go from $page to the WhatIsYourFullName page when the user is the individual" in {
      val userAnswers = for {
        uaWithOnshore <- UserAnswers("id").set(RelatesToPage, RelatesTo.AnIndividual)
        ua 	<- uaWithOnshore.set(AreYouTheIndividualPage, true)
      } yield ua
      navigator.nextPage(page, NormalMode, userAnswers.success.value) mustBe routes.WhatIsYourFullNameController.onPageLoad(NormalMode)
    }

    s"must go from $page to the WhatIsTheIndividualsFullName page when the user is the individual and disclosing on behalf of the individual" in {
      val userAnswers = for {
        uaWithOnshore <- UserAnswers("id").set(RelatesToPage, RelatesTo.AnIndividual)
        ua 	<- uaWithOnshore.set(AreYouTheIndividualPage, false)
      } yield ua
      navigator.nextPage(page, NormalMode, userAnswers.success.value) mustBe routes.WhatIsTheIndividualsFullNameController.onPageLoad(NormalMode) 
    }
    
    s"must go from $page to the WhatIsTheNameOfTheCompanyTheDisclosureWillBeAbout page when the user is a company" in {
      val userAnswers = UserAnswers("id").set(RelatesToPage, RelatesTo.ACompany)
      navigator.nextPage(page, NormalMode, userAnswers.success.value) mustBe routes.WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutController.onPageLoad(NormalMode)
    }

    s"must go from $page to the WhatIsTheLLPName page when the user is a company" in {
      val userAnswers = UserAnswers("id").set(RelatesToPage, RelatesTo.ALimitedLiabilityPartnership)
      navigator.nextPage(page, NormalMode, userAnswers.success.value) mustBe routes.WhatIsTheLLPNameController.onPageLoad(NormalMode)
    }

    s"must go from $page to the WhatIsTheTrustName page when the user is a company" in {
      val userAnswers = UserAnswers("id").set(RelatesToPage, RelatesTo.ATrust)
      navigator.nextPage(page, NormalMode, userAnswers.success.value) mustBe routes.WhatIsTheTrustNameController.onPageLoad(NormalMode)
    }

    s"must go from $page to the WhatWasTheNameOfThePersonWhoDied page when the user is a company" in {
      val userAnswers = UserAnswers("id").set(RelatesToPage, RelatesTo.AnEstate)
      navigator.nextPage(page, NormalMode, userAnswers.success.value) mustBe routes.WhatWasTheNameOfThePersonWhoDiedController.onPageLoad(NormalMode)
    }
  }

}