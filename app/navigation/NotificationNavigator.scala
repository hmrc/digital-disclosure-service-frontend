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

    case RelatesToPage => _ => routes.AreYouTheIndividualController.onPageLoad(NormalMode)

    case AreYouTheIndividualPage => _ => routes.OffshoreLiabilitiesController.onPageLoad(NormalMode)

    case OnshoreLiabilitiesPage => ua => ua.get(OnshoreLiabilitiesPage) match {
      case Some(OnshoreLiabilities.IWantTo) => routes.WhatIsYourFullNameController.onPageLoad(NormalMode)
      case Some(OnshoreLiabilities.IDoNotWantTo) => routes.WhatIsYourFullNameController.onPageLoad(NormalMode)
      case None => routes.OnshoreLiabilitiesController.onPageLoad(NormalMode)
    }
    
    case OffshoreLiabilitiesPage => ua => ua.get(OffshoreLiabilitiesPage) match {
      case Some(OffshoreLiabilities.IWantTo) => routes.OnshoreLiabilitiesController.onPageLoad(NormalMode)
      case Some(OffshoreLiabilities.IDoNotWantTo) => routes.OnlyOnshoreLiabilitiesController.onPageLoad
      case None => routes.OffshoreLiabilitiesController.onPageLoad(NormalMode)
    }

    case WhatIsYourFullNamePage => _ => routes.YourPhoneNumberController.onPageLoad(NormalMode)

    case YourPhoneNumberPage => _ => routes.DoYouHaveAnEmailAddressController.onPageLoad(NormalMode)

    case DoYouHaveAnEmailAddressPage => ua => ua.get(DoYouHaveAnEmailAddressPage) match {
      case Some(true) => routes.YourEmailAddressController.onPageLoad(NormalMode)
      case Some(false) => routes.WhatIsYourDateOfBirthController.onPageLoad(NormalMode)
      case None => routes.DoYouHaveAnEmailAddressController.onPageLoad(NormalMode)
    }

    case WhatIsYourDateOfBirthPage => _ => routes.WhatIsYourMainOccupationController.onPageLoad(NormalMode)

    case WhatIsYourMainOccupationPage => _ => routes.DoYouHaveNationalInsuranceNumberController.onPageLoad(NormalMode)

    case DoYouHaveNationalInsuranceNumberPage => ua => ua.get(DoYouHaveNationalInsuranceNumberPage) match {
      case Some(DoYouHaveNationalInsuranceNumber.YesIknow) => routes.WhatIsYourNationalInsuranceNumberController.onPageLoad(NormalMode)
      case Some(DoYouHaveNationalInsuranceNumber.YesButDontKnow) => routes.AreYouRegisteredForVATController.onPageLoad(NormalMode)
      case Some(DoYouHaveNationalInsuranceNumber.No) => routes.AreYouRegisteredForVATController.onPageLoad(NormalMode)
      case None => routes.DoYouHaveNationalInsuranceNumberController.onPageLoad(NormalMode)
    }

    case YourEmailAddressPage => _ => routes.WhatIsYourDateOfBirthController.onPageLoad(NormalMode)

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

    case _ => _ => controllers.routes.IndexController.onPageLoad
  }

  private val checkRouteMap: Page => UserAnswers => Call = {
    case _ => _ => controllers.routes.CheckYourAnswersController.onPageLoad
  }

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }
}
