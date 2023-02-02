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

package navigation

import javax.inject.{Inject, Singleton}
import play.api.mvc.Call
import pages._
import models.{AdviceContactPreference, CheckMode, Mode, NormalMode, UserAnswers}
import models.WhyAreYouMakingADisclosure._
import models.WhichEmailAddressCanWeContactYouWith._
import controllers.reason.routes

@Singleton
class ReasonNavigator @Inject()() {

  private val normalRoutes: Page => UserAnswers => Call = {  

    case WhyAreYouMakingADisclosurePage => ua => ua.get(WhyAreYouMakingADisclosurePage) match {
      case Some(value) if(value.contains(Other)) => routes.WhatIsTheReasonForMakingADisclosureNowController.onPageLoad(NormalMode)
      case _ => routes.WhyNotBeforeNowController.onPageLoad(NormalMode)
    }

    case WhatIsTheReasonForMakingADisclosureNowPage => _ => routes.WhyNotBeforeNowController.onPageLoad(NormalMode)
    
    case WhyNotBeforeNowPage => _ => routes.DidSomeoneGiveYouAdviceNotDeclareTaxController.onPageLoad(NormalMode)
    
    case DidSomeoneGiveYouAdviceNotDeclareTaxPage => ua => ua.get(DidSomeoneGiveYouAdviceNotDeclareTaxPage) match {
      case Some(true) => routes.PersonWhoGaveAdviceController.onPageLoad(NormalMode)
      case _ => routes.CheckYourAnswersController.onPageLoad
    }
    
    case PersonWhoGaveAdvicePage => _ => routes.AdviceBusinessesOrOrgController.onPageLoad(NormalMode)
    
    case AdviceBusinessesOrOrgPage => ua => ua.get(AdviceBusinessesOrOrgPage) match {
      case Some(true) => routes.AdviceBusinessNameController.onPageLoad(NormalMode)
      case _ => routes.AdviceProfessionController.onPageLoad(NormalMode)
    }
    
    case AdviceBusinessNamePage => _ => routes.AdviceProfessionController.onPageLoad(NormalMode)
    
    case AdviceProfessionPage => _ => routes.AdviceGivenController.onPageLoad(NormalMode)

    case AdviceGivenPage => ua => ua.get(AdviceGivenPage) match {
      case (Some(value)) if value.contactPreference == AdviceContactPreference.Email => routes.WhichEmailAddressCanWeContactYouWithController.onPageLoad(NormalMode)
      case (Some(value)) if value.contactPreference == AdviceContactPreference.Telephone => routes.CanWeUseTelephoneNumberToContactYouController.onPageLoad(NormalMode)
      case _ => routes.CheckYourAnswersController.onPageLoad
    }

    case WhichEmailAddressCanWeContactYouWithPage => ua => ua.get(WhichEmailAddressCanWeContactYouWithPage) match {
      case Some(DifferentEmail) => routes.WhatEmailAddressCanWeContactYouWithController.onPageLoad(NormalMode)
      case Some(ExistingEmail) => routes.CheckYourAnswersController.onPageLoad
      case _ => routes.WhichEmailAddressCanWeContactYouWithController.onPageLoad(NormalMode)
    }

    case WhatEmailAddressCanWeContactYouWithPage => _ => routes.CheckYourAnswersController.onPageLoad
    
    case CanWeUseTelephoneNumberToContactYouPage => ua => ua.get(CanWeUseTelephoneNumberToContactYouPage) match {
      case Some(true) => routes.CheckYourAnswersController.onPageLoad
      case _ => routes.WhatTelephoneNumberCanWeContactYouWithController.onPageLoad(NormalMode)
    }

    case WhatTelephoneNumberCanWeContactYouWithPage => _ => routes.CheckYourAnswersController.onPageLoad  

    case _ => _ => controllers.routes.IndexController.onPageLoad
  }

  private val checkRouteMap: Page => UserAnswers => Boolean => Call = {
    case _ => _ => _ => routes.CheckYourAnswersController.onPageLoad
  }

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers, hasAnswerChanged: Boolean = true): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)(hasAnswerChanged)
  }

}
