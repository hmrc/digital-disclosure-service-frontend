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

import javax.inject.{Inject, Singleton}

import play.api.mvc.Call
import controllers.notification.routes
import pages._
import models._

@Singleton
class NotificationNavigator @Inject()() {

  private val normalRoutes: Page => UserAnswers => Call = {

    case ReceivedALetterPage => ua => ua.get(ReceivedALetterPage) match {
      case Some(true) => routes.LetterReferenceController.onPageLoad(NormalMode)
      case Some(false) => routes.RelatesToController.onPageLoad(NormalMode)
      case None => routes.ReceivedALetterController.onPageLoad(NormalMode)
    }

    case LetterReferencePage => _ => routes.RelatesToController.onPageLoad(NormalMode)

    case RelatesToPage => ua => ua.get(RelatesToPage) match {
      case Some(RelatesTo.AnIndividual) => routes.AreYouTheIndividualController.onPageLoad(NormalMode)
      case Some(RelatesTo.ACompany) => routes.AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutController.onPageLoad(NormalMode)
      case _ => routes.RelatesToController.onPageLoad(NormalMode)
    }

    case AreYouTheIndividualPage => _ => routes.OffshoreLiabilitiesController.onPageLoad(NormalMode)

    case OnshoreLiabilitiesPage => ua => ua.get(AreYouTheIndividualPage) match {
      case Some(AreYouTheIndividual.Yes) => routes.WhatIsYourFullNameController.onPageLoad(NormalMode)
      case Some(AreYouTheIndividual.No) => routes.WhatIsTheIndividualsFullNameController.onPageLoad(NormalMode)
      case None => routes.OnshoreLiabilitiesController.onPageLoad(NormalMode)
    }
    
    case OffshoreLiabilitiesPage => ua => ua.get(OffshoreLiabilitiesPage) match {
      case Some(OffshoreLiabilities.IWantTo) => routes.OnshoreLiabilitiesController.onPageLoad(NormalMode)
      case Some(OffshoreLiabilities.IDoNotWantTo) => routes.OnlyOnshoreLiabilitiesController.onPageLoad
      case None => routes.OffshoreLiabilitiesController.onPageLoad(NormalMode)
    }

    case WhatIsYourFullNamePage => _ => routes.YourPhoneNumberController.onPageLoad(NormalMode)

    case YourPhoneNumberPage => _ => routes.DoYouHaveAnEmailAddressController.onPageLoad(NormalMode)

    case DoYouHaveAnEmailAddressPage => ua => (ua.get(DoYouHaveAnEmailAddressPage), ua.get(AreYouTheIndividualPage)) match {
      case (Some(true), Some(AreYouTheIndividual.Yes)) => routes.YourEmailAddressController.onPageLoad(NormalMode)
      case (Some(false), Some(AreYouTheIndividual.Yes)) => routes.WhatIsYourDateOfBirthController.onPageLoad(NormalMode)
      case (Some(true), Some(AreYouTheIndividual.No)) => routes.YourEmailAddressController.onPageLoad(NormalMode)
      case (Some(false), Some(AreYouTheIndividual.No)) => routes.YourAddressLookupController.lookupAddress(NormalMode)
      case (_, _) => routes.DoYouHaveAnEmailAddressController.onPageLoad(NormalMode)
    }

    case WhatIsYourDateOfBirthPage => _ => routes.WhatIsYourMainOccupationController.onPageLoad(NormalMode)

    case WhatIsYourMainOccupationPage => _ => routes.DoYouHaveNationalInsuranceNumberController.onPageLoad(NormalMode)

    case DoYouHaveNationalInsuranceNumberPage => ua => ua.get(DoYouHaveNationalInsuranceNumberPage) match {
      case Some(DoYouHaveNationalInsuranceNumber.YesIKnow) => routes.WhatIsYourNationalInsuranceNumberController.onPageLoad(NormalMode)
      case Some(DoYouHaveNationalInsuranceNumber.YesButDontKnow) => routes.AreYouRegisteredForVATController.onPageLoad(NormalMode)
      case Some(DoYouHaveNationalInsuranceNumber.No) => routes.AreYouRegisteredForVATController.onPageLoad(NormalMode)
      case None => routes.DoYouHaveNationalInsuranceNumberController.onPageLoad(NormalMode)
    }

    case YourEmailAddressPage => ua => ua.get(AreYouTheIndividualPage) match {
      case Some(AreYouTheIndividual.Yes) => routes.WhatIsYourDateOfBirthController.onPageLoad(NormalMode)
      case Some(AreYouTheIndividual.No) => routes.YourAddressLookupController.lookupAddress(NormalMode)
      case _ => routes.YourEmailAddressController.onPageLoad(NormalMode)
    }

    case WhatIsYourNationalInsuranceNumberPage => _ => routes.AreYouRegisteredForVATController.onPageLoad(NormalMode)

    case AreYouRegisteredForVATPage => ua => ua.get(AreYouRegisteredForVATPage) match {
      case Some(AreYouRegisteredForVAT.YesIKnow) => routes.WhatIsYourVATRegistrationNumberController.onPageLoad(NormalMode)
      case Some(AreYouRegisteredForVAT.YesButDontKnow) => routes.AreYouRegisteredForSelfAssessmentController.onPageLoad(NormalMode)
      case Some(AreYouRegisteredForVAT.No) => routes.AreYouRegisteredForSelfAssessmentController.onPageLoad(NormalMode)
      case None => routes.AreYouRegisteredForVATController.onPageLoad(NormalMode)
    }

    case WhatIsYourVATRegistrationNumberPage => _ => routes.AreYouRegisteredForSelfAssessmentController.onPageLoad(NormalMode)

    case AreYouRegisteredForSelfAssessmentPage => ua => ua.get(AreYouRegisteredForSelfAssessmentPage) match {
      case Some(AreYouRegisteredForSelfAssessment.YesIKnowMyUTR) => routes.WhatIsYourUniqueTaxReferenceController.onPageLoad(NormalMode)
      case Some(AreYouRegisteredForSelfAssessment.YesIDontKnowMyUTR) => routes.YourAddressLookupController.lookupAddress(NormalMode)
      case Some(AreYouRegisteredForSelfAssessment.No) => routes.YourAddressLookupController.lookupAddress(NormalMode)
      case None => routes.AreYouRegisteredForSelfAssessmentController.onPageLoad(NormalMode)
    }

    case WhatIsYourUniqueTaxReferencePage => _ => routes.YourAddressLookupController.lookupAddress(NormalMode)

    case YourAddressLookupPage => _ => routes.CheckYourAnswersController.onPageLoad

    case WhatIsTheIndividualsFullNamePage => _ => routes.WhatIsTheIndividualDateOfBirthControllerController.onPageLoad(NormalMode)

    case WhatIsTheIndividualOccupationPage => _ => routes.DoesTheIndividualHaveNationalInsuranceNumberController.onPageLoad(NormalMode)

    case DoesTheIndividualHaveNationalInsuranceNumberPage => ua => ua.get(DoesTheIndividualHaveNationalInsuranceNumberPage) match {
      case Some(DoesTheIndividualHaveNationalInsuranceNumber.YesIKnow) => routes.WhatIsIndividualsNationalInsuranceNumberController.onPageLoad(NormalMode)
      case Some(DoesTheIndividualHaveNationalInsuranceNumber.YesButDontKnow) => routes.IsTheIndividualRegisteredForVATController.onPageLoad(NormalMode)
      case Some(DoesTheIndividualHaveNationalInsuranceNumber.No) => routes.IsTheIndividualRegisteredForVATController.onPageLoad(NormalMode)
      case None => routes.DoesTheIndividualHaveNationalInsuranceNumberController.onPageLoad(NormalMode)
    }

    case WhatIsIndividualsNationalInsuranceNumberPage => _ => routes.IsTheIndividualRegisteredForVATController.onPageLoad(NormalMode)

    case WhatIsTheIndividualDateOfBirthControllerPage => _ => routes.WhatIsTheIndividualOccupationController.onPageLoad(NormalMode)

    case IsTheIndividualRegisteredForVATPage => ua => ua.get(IsTheIndividualRegisteredForVATPage) match {
      case Some(IsTheIndividualRegisteredForVAT.YesIKnow) => routes.WhatIsTheIndividualsVATRegistrationNumberController.onPageLoad(NormalMode)
      case Some(IsTheIndividualRegisteredForVAT.YesButDontKnow) => routes.IsTheIndividualRegisteredForSelfAssessmentController.onPageLoad(NormalMode)
      case Some(IsTheIndividualRegisteredForVAT.No) => routes.IsTheIndividualRegisteredForSelfAssessmentController.onPageLoad(NormalMode)
      case None => routes.IsTheIndividualRegisteredForVATController.onPageLoad(NormalMode)
    }

    case WhatIsTheIndividualsVATRegistrationNumberPage => _ => routes.IsTheIndividualRegisteredForSelfAssessmentController.onPageLoad(NormalMode)

    case WhatIsTheIndividualsUniqueTaxReferencePage => _ => routes.IndividualAddressLookupController.lookupAddress(NormalMode)

    case IsTheIndividualRegisteredForSelfAssessmentPage => ua => ua.get(IsTheIndividualRegisteredForSelfAssessmentPage) match {
      case Some(IsTheIndividualRegisteredForSelfAssessment.YesIKnow) => routes.WhatIsTheIndividualsUniqueTaxReferenceController.onPageLoad(NormalMode)
      case Some(IsTheIndividualRegisteredForSelfAssessment.YesButDontKnow) => routes.IndividualAddressLookupController.lookupAddress(NormalMode)
      case Some(IsTheIndividualRegisteredForSelfAssessment.No) => routes.IndividualAddressLookupController.lookupAddress(NormalMode)
      case None => routes.IsTheIndividualRegisteredForSelfAssessmentController.onPageLoad(NormalMode)
    }
    
    case IndividualAddressLookupPage => _ => routes.WhatIsYourFullNameController.onPageLoad(NormalMode)

    case AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage => ua => ua.get(AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage) match {
      case Some(AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAbout.Yes) => routes.OffshoreLiabilitiesController.onPageLoad(NormalMode)
      case Some(AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAbout.No) => routes.AreYouRepresentingAnOrganisationController.onPageLoad(NormalMode)
      case None => routes.AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutController.onPageLoad(NormalMode)
    }

    case AreYouRepresentingAnOrganisationPage => ua => ua.get(AreYouRepresentingAnOrganisationPage) match {
      case Some(true) => routes.WhatIsTheNameOfTheOrganisationYouRepresentController.onPageLoad(NormalMode)
      case Some(false) => routes.OffshoreLiabilitiesController.onPageLoad(NormalMode)
      case None => routes.AreYouRepresentingAnOrganisationController.onPageLoad(NormalMode)
    }

    case WhatIsTheNameOfTheOrganisationYouRepresentPage => _ => routes.OffshoreLiabilitiesController.onPageLoad(NormalMode)

    case _ => _ => controllers.routes.IndexController.onPageLoad
  }

  private val checkRouteMap: Page => UserAnswers => Boolean => Call = {
    case AreYouTheIndividualPage => ua => hasAnswerChanged =>
      if(hasAnswerChanged) normalRoutes(AreYouTheIndividualPage)(ua)
      else routes.CheckYourAnswersController.onPageLoad

    case ReceivedALetterPage => ua => hasAnswerChanged =>
      if(hasAnswerChanged) routes.LetterReferenceController.onPageLoad(CheckMode)
      else routes.CheckYourAnswersController.onPageLoad

    case DoYouHaveAnEmailAddressPage => ua => hasAnswerChanged =>
      if(hasAnswerChanged) routes.YourEmailAddressController.onPageLoad(CheckMode)
      else routes.CheckYourAnswersController.onPageLoad

    case DoYouHaveNationalInsuranceNumberPage => ua => hasAnswerChanged => ua.get(DoYouHaveNationalInsuranceNumberPage) match {
      case Some(DoYouHaveNationalInsuranceNumber.YesIKnow) if hasAnswerChanged => routes.WhatIsYourNationalInsuranceNumberController.onPageLoad(CheckMode)
      case _ => routes.CheckYourAnswersController.onPageLoad
    }

    case AreYouRegisteredForVATPage => ua => hasAnswerChanged => ua.get(AreYouRegisteredForVATPage) match {
      case Some(AreYouRegisteredForVAT.YesIKnow) if hasAnswerChanged => routes.WhatIsYourVATRegistrationNumberController.onPageLoad(CheckMode)
      case _ => routes.CheckYourAnswersController.onPageLoad
    }
    
    case AreYouRegisteredForSelfAssessmentPage => _ => hasAnswerChanged =>
      if(hasAnswerChanged) routes.WhatIsYourUniqueTaxReferenceController.onPageLoad(CheckMode)
      else routes.CheckYourAnswersController.onPageLoad

    case IsTheIndividualRegisteredForVATPage => ua => hasAnswerChanged => ua.get(IsTheIndividualRegisteredForVATPage) match {
      case Some(IsTheIndividualRegisteredForVAT.YesIKnow) if hasAnswerChanged => routes.WhatIsTheIndividualsVATRegistrationNumberController.onPageLoad(CheckMode)
      case _ => routes.CheckYourAnswersController.onPageLoad
    }

    case IsTheIndividualRegisteredForSelfAssessmentPage => ua => hasAnswerChanged => ua.get(IsTheIndividualRegisteredForSelfAssessmentPage) match {
      case Some(IsTheIndividualRegisteredForSelfAssessment.YesIKnow) if hasAnswerChanged => routes.WhatIsTheIndividualsUniqueTaxReferenceController.onPageLoad(CheckMode)
      case _ => routes.CheckYourAnswersController.onPageLoad
    }

    case DoesTheIndividualHaveNationalInsuranceNumberPage => ua => hasAnswerChanged => ua.get(DoesTheIndividualHaveNationalInsuranceNumberPage) match {
      case Some(DoesTheIndividualHaveNationalInsuranceNumber.YesIKnow) if hasAnswerChanged => routes.WhatIsIndividualsNationalInsuranceNumberController.onPageLoad(CheckMode)
      case _ => routes.CheckYourAnswersController.onPageLoad
    }

    case _ => _ => _ => routes.CheckYourAnswersController.onPageLoad
  }

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers, hasAnswerChanged: Boolean = true): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)(hasAnswerChanged)
  }
}
