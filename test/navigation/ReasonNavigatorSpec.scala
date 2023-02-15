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

      "if the user says yes must go from WhichTelephoneNumberCanWeContactYouWithPage to CheckYourAnswersController" in {
        val ua = UserAnswers("id").set(WhichTelephoneNumberCanWeContactYouWithPage, WhichTelephoneNumberCanWeContactYouWith.ExistingNumber).success.value
        navigator.nextPage(WhichTelephoneNumberCanWeContactYouWithPage, NormalMode, ua) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "if the user says no must go from WhichTelephoneNumberCanWeContactYouWithPage to WhichEmailAddressCanWeContactYouWithController" in {
        val ua = UserAnswers("id").set(WhichTelephoneNumberCanWeContactYouWithPage, WhichTelephoneNumberCanWeContactYouWith.DifferentNumber).success.value
        navigator.nextPage(WhichTelephoneNumberCanWeContactYouWithPage, NormalMode, ua) mustBe routes.WhatTelephoneNumberCanWeContactYouWithController.onPageLoad(NormalMode)
      }

      "if the user select existing email radio button must go from WhichEmailAddressCanWeContactYouWithPage to CheckYourAnswersController" in {
        val ua = UserAnswers("id").set(WhichEmailAddressCanWeContactYouWithPage, WhichEmailAddressCanWeContactYouWith.ExistingEmail).success.value
        navigator.nextPage(WhichEmailAddressCanWeContactYouWithPage, NormalMode, ua) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "if the user select different email radio button must go from WhichEmailAddressCanWeContactYouWithPage to WhatEmailAddressCanWeContactYouWithPage" in {
        val ua = UserAnswers("id").set(WhichEmailAddressCanWeContactYouWithPage, WhichEmailAddressCanWeContactYouWith.DifferentEmail).success.value
        navigator.nextPage(WhichEmailAddressCanWeContactYouWithPage, NormalMode, ua) mustBe routes.WhatEmailAddressCanWeContactYouWithController.onPageLoad(NormalMode)
      }

      "must go from WhatEmailAddressCanWeContactYouWithPage to CheckYourAnswersController" in {
        navigator.nextPage(WhatEmailAddressCanWeContactYouWithPage, NormalMode, UserAnswers("id")) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from WhatTelephoneNumberCanWeContactYouWithPage to CheckYourAnswersController" in {
        navigator.nextPage(WhatTelephoneNumberCanWeContactYouWithPage, NormalMode, UserAnswers("id")) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from AdviceGivenPage to WhichEmailAddressCanWeContactYouWithController if the user already entered an email" in {
        val adviceGiven = AdviceGiven("Advice",MonthYear(1,1960), AdviceContactPreference.Email)
        val adviceUa = UserAnswers("id").set(AdviceGivenPage, adviceGiven).success.value
        navigator.nextPage(AdviceGivenPage, NormalMode, adviceUa) mustBe routes.WhichEmailAddressCanWeContactYouWithController.onPageLoad(NormalMode)
      }

      "must go from AdviceGivenPage to WhichTelephoneNumberCanWeContactYouWithController if the user already entered a telephone number" in {
        val adviceGiven = AdviceGiven("Advice", MonthYear(1, 1960), AdviceContactPreference.Telephone)
        val adviceUa = UserAnswers("id").set(AdviceGivenPage, adviceGiven).success.value
        navigator.nextPage(AdviceGivenPage, NormalMode, adviceUa) mustBe routes.WhichTelephoneNumberCanWeContactYouWithController.onPageLoad(NormalMode)
      }

      "must go from AdviceGivenPage to CheckYourAnswersController if the user selected No" in {
        val adviceGiven = AdviceGiven("", MonthYear(1, 1960), AdviceContactPreference.No)
        val adviceUa = UserAnswers("id").set(AdviceGivenPage, adviceGiven).success.value
        navigator.nextPage(AdviceGivenPage, NormalMode, adviceUa) mustBe routes.CheckYourAnswersController.onPageLoad
      }
    }

    "in Check mode" - {

      "must go from the WhyAreYouMakingADisclosurePage to WhatIsTheReasonForMakingADisclosureNowController when user select 'Other'" in {
        val set: Set[WhyAreYouMakingADisclosure] = Set(Other)
        val userAnswers = UserAnswers("id").set(WhyAreYouMakingADisclosurePage, set).success.value
        navigator.nextPage(WhyAreYouMakingADisclosurePage, CheckMode, userAnswers, true) mustBe routes.WhatIsTheReasonForMakingADisclosureNowController.onPageLoad(CheckMode)
      }

      "must go from the WhyAreYouMakingADisclosurePage to CheckYourAnswers when user don't change anything" in {
        val set: Set[WhyAreYouMakingADisclosure] = Set(GovUkGuidance)
        val userAnswers = UserAnswers("id").set(WhyAreYouMakingADisclosurePage, set).success.value
        navigator.nextPage(WhyAreYouMakingADisclosurePage, CheckMode, userAnswers, true) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the DidSomeoneGiveYouAdviceNotDeclareTaxPage to CheckYourAnswers when the answer is No and has changed" in {
        val userAnswers = UserAnswers("id").set(DidSomeoneGiveYouAdviceNotDeclareTaxPage, false).success.value
        navigator.nextPage(DidSomeoneGiveYouAdviceNotDeclareTaxPage, CheckMode, userAnswers, true) mustBe routes.PersonWhoGaveAdviceController.onPageLoad(NormalMode)
      }

      "must go from the DidSomeoneGiveYouAdviceNotDeclareTaxPage to CheckYourAnswers when the answer is Yes and has not changed" in {
        val userAnswers = UserAnswers("id").set(DidSomeoneGiveYouAdviceNotDeclareTaxPage, true).success.value
        navigator.nextPage(DidSomeoneGiveYouAdviceNotDeclareTaxPage, CheckMode, userAnswers, false) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the DidSomeoneGiveYouAdviceNotDeclareTaxPage to CheckYourAnswers when the answer is No and has not changed" in {
        val userAnswers = UserAnswers("id").set(DidSomeoneGiveYouAdviceNotDeclareTaxPage, false).success.value
        navigator.nextPage(DidSomeoneGiveYouAdviceNotDeclareTaxPage, CheckMode, userAnswers, false) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, UserAnswers("id")) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the AdviceBusinessesOrOrgPage to AdviceBusinessNameController where the answer is Yes" in {
        val userAnswers = UserAnswers("id").set(AdviceBusinessesOrOrgPage, true).success.value
        navigator.nextPage(AdviceBusinessesOrOrgPage, CheckMode, userAnswers, true) mustBe routes.AdviceBusinessNameController.onPageLoad(CheckMode)
      }

      "must go from the AdviceBusinessesOrOrgPage to CheckYourAnswers where the answer is No" in {
        val userAnswers = UserAnswers("id").set(AdviceBusinessesOrOrgPage, false).success.value
        navigator.nextPage(AdviceBusinessesOrOrgPage, CheckMode, userAnswers, false) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the AdviceGivenPage to WhichEmailAddressCanWeContactYouWithController where the answer is Yes, by email" in {
        val adviceGiven = AdviceGiven("Advice", MonthYear(1,1960), AdviceContactPreference.Email)
        val userAnswers = UserAnswers("id").set(AdviceGivenPage, adviceGiven).success.value
        navigator.nextPage(AdviceGivenPage, CheckMode, userAnswers, true) mustBe routes.WhichEmailAddressCanWeContactYouWithController.onPageLoad(CheckMode)
      }

      "must go from the AdviceGivenPage to WhichTelephoneNumberCanWeContactYouWithController where the answer is Yes, by telephone" in {
        val adviceGiven = AdviceGiven("Advice", MonthYear(1,1960), AdviceContactPreference.Telephone)
        val userAnswers = UserAnswers("id").set(AdviceGivenPage, adviceGiven).success.value
        navigator.nextPage(AdviceGivenPage, CheckMode, userAnswers, true) mustBe routes.WhichTelephoneNumberCanWeContactYouWithController.onPageLoad(CheckMode)
      }

      "must go from the AdviceGivenPage to CheckYourAnswersController where the answer is No" in {
        val adviceGiven = AdviceGiven("Advice", MonthYear(1,1960), AdviceContactPreference.No)
        val userAnswers = UserAnswers("id").set(AdviceGivenPage, adviceGiven).success.value
        navigator.nextPage(AdviceGivenPage, CheckMode, userAnswers, true) mustBe routes.CheckYourAnswersController.onPageLoad
      }
    }
  }

}