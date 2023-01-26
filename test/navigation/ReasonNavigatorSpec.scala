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

import base.SpecBase
import controllers.reason.routes
import pages._
import models._
import models.WhyAreYouMakingADisclosure._

class ReasonNavigatorSpec extends SpecBase {

  val navigator = new ReasonNavigator

  "Reason Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, UserAnswers("id")) mustBe controllers.routes.IndexController.onPageLoad
      }

      "must go from WhyAreYouMakingADisclosurePage to WhatIsTheReasonForMakingADisclosureNowController when selected Other" in {
        val set: Set[WhyAreYouMakingADisclosure] = Set(Other)
        val userAnswers = UserAnswers("id").set(WhyAreYouMakingADisclosurePage, set).success.value
        navigator.nextPage(WhyAreYouMakingADisclosurePage, NormalMode, userAnswers) mustBe routes.WhatIsTheReasonForMakingADisclosureNowController.onPageLoad(NormalMode)
      }

      Seq(
        GovUkGuidance,
        LetterFromHMRC,
        Employer,
        News,
        Publication,
        Accountant,
        ROE
      ).foreach { option => 
        s"must go from WhyAreYouMakingADisclosurePage to WhyNotBeforeNowController when selected option $option" in {
          val set: Set[WhyAreYouMakingADisclosure] = Set(option)
          val userAnswers = UserAnswers("id").set(WhyAreYouMakingADisclosurePage, set).success.value
          navigator.nextPage(WhyAreYouMakingADisclosurePage, NormalMode, userAnswers) mustBe routes.WhyNotBeforeNowController.onPageLoad(NormalMode)
        }
      }  
      "must go from WhatIsTheReasonForMakingADisclosureNowPage to WhyNotBeforeNowController" in {
        navigator.nextPage(WhatIsTheReasonForMakingADisclosureNowPage, NormalMode, UserAnswers("id")) mustBe routes.WhyNotBeforeNowController.onPageLoad(NormalMode)
      }

      "must go from WhyNotBeforeNowPage to DidSomeoneGiveYouAdviceNotDeclareTaxController" in {
        navigator.nextPage(WhyNotBeforeNowPage, NormalMode, UserAnswers("id")) mustBe routes.DidSomeoneGiveYouAdviceNotDeclareTaxController.onPageLoad(NormalMode)
      }

      "if the user says yes must go from DidSomeoneGiveYouAdviceNotDeclareTaxPage to PersonWhoGaveAdviceController" in {
        val ua = UserAnswers("id").set(DidSomeoneGiveYouAdviceNotDeclareTaxPage, true).success.value
        navigator.nextPage(DidSomeoneGiveYouAdviceNotDeclareTaxPage, NormalMode, ua) mustBe routes.PersonWhoGaveAdviceController.onPageLoad(NormalMode)
      }

      "if the user says no must go from DidSomeoneGiveYouAdviceNotDeclareTaxPage to Summary page" in {
        val ua = UserAnswers("id").set(DidSomeoneGiveYouAdviceNotDeclareTaxPage, false).success.value
        navigator.nextPage(DidSomeoneGiveYouAdviceNotDeclareTaxPage, NormalMode, ua) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from PersonWhoGaveAdvicePage to AdviceBusinessesOrOrgController" in {
        navigator.nextPage(PersonWhoGaveAdvicePage, NormalMode, UserAnswers("id")) mustBe routes.AdviceBusinessesOrOrgController.onPageLoad(NormalMode)
      }
      
      "if the user says yes must go from AdviceBusinessesOrOrgPage to AdviceBusinessNameController" in {
        val ua = UserAnswers("id").set(AdviceBusinessesOrOrgPage, true).success.value
        navigator.nextPage(AdviceBusinessesOrOrgPage, NormalMode, ua) mustBe routes.AdviceBusinessNameController.onPageLoad(NormalMode)
      }

      "if the user says no must go from AdviceBusinessesOrOrgPage to AdviceProfessionController" in {
        val ua = UserAnswers("id").set(AdviceBusinessesOrOrgPage, false).success.value
        navigator.nextPage(AdviceBusinessesOrOrgPage, NormalMode, ua) mustBe routes.AdviceProfessionController.onPageLoad(NormalMode)
      }

      "must go from AdviceBusinessNamePage to AdviceProfessionController" in {
        navigator.nextPage(AdviceBusinessNamePage, NormalMode, UserAnswers("id")) mustBe routes.AdviceProfessionController.onPageLoad(NormalMode)
      }

      "must go from AdviceProfessionPage to AdviceGivenController" in {
        navigator.nextPage(AdviceProfessionPage, NormalMode, UserAnswers("id")) mustBe routes.AdviceGivenController.onPageLoad(NormalMode)
      }

      "if the user says yes must go from CanWeUseEmailAddressToContactYouPage to CheckYourAnswersController" in {
        val ua = UserAnswers("id").set(CanWeUseEmailAddressToContactYouPage, true).success.value
        navigator.nextPage(CanWeUseEmailAddressToContactYouPage, NormalMode, ua) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "if the user says no must go from CanWeUseEmailAddressToContactYouPage to WhatEmailAddressCanWeContactYouWithController" in {
        val ua = UserAnswers("id").set(CanWeUseEmailAddressToContactYouPage, false).success.value
        navigator.nextPage(CanWeUseEmailAddressToContactYouPage, NormalMode, ua) mustBe routes.WhatEmailAddressCanWeContactYouWithController.onPageLoad(NormalMode)
      }

      "if the user says yes must go from CanWeUseTelephoneNumberToContactYouPage to CheckYourAnswersController" in {
        val ua = UserAnswers("id").set(CanWeUseTelephoneNumberToContactYouPage, true).success.value
        navigator.nextPage(CanWeUseTelephoneNumberToContactYouPage, NormalMode, ua) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "if the user says no must go from CanWeUseTelephoneNumberToContactYouPage to WhatEmailAddressCanWeContactYouWithController" in {
        val ua = UserAnswers("id").set(CanWeUseTelephoneNumberToContactYouPage, false).success.value
        navigator.nextPage(CanWeUseTelephoneNumberToContactYouPage, NormalMode, ua) mustBe routes.WhatTelephoneNumberCanWeContactYouWithController.onPageLoad(NormalMode)
      }

      "must go from WhatEmailAddressCanWeContactYouWithPage to CheckYourAnswersController" in {
        navigator.nextPage(WhatEmailAddressCanWeContactYouWithPage, NormalMode, UserAnswers("id")) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from WhatTelephoneNumberCanWeContactYouWithPage to CheckYourAnswersController" in {
        navigator.nextPage(WhatTelephoneNumberCanWeContactYouWithPage, NormalMode, UserAnswers("id")) mustBe routes.CheckYourAnswersController.onPageLoad
      }

    }

    "in Check mode" - {

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, UserAnswers("id")) mustBe routes.CheckYourAnswersController.onPageLoad
      }
    }
  }

}