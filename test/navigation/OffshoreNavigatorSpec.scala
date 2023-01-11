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
import controllers.offshore.routes
import pages._
import models._

class OffshoreNavigatorSpec extends SpecBase {

  val navigator = new OffshoreNavigator

  "Offshore Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, UserAnswers("id")) mustBe controllers.routes.IndexController.onPageLoad
      }

      "must go from ContractualDisclosureFacilityPage to WhatIsYourReasonableExcuseController when selected DidNotNotifyHasExcuse & true" in {
        val set: Set[WhyAreYouMakingThisDisclosure] = Set(WhyAreYouMakingThisDisclosure.DidNotNotifyHasExcuse)
        val userAnswers = for {
          answer          <- UserAnswers("id").set(WhyAreYouMakingThisDisclosurePage, set)
          updatedAnswer 	<- answer.set(ContractualDisclosureFacilityPage, true)
          } yield updatedAnswer
        navigator.nextPage(ContractualDisclosureFacilityPage, NormalMode, userAnswers.success.value) mustBe routes.WhatIsYourReasonableExcuseController.onPageLoad(NormalMode)
      }

      "must go from ContractualDisclosureFacilityPage to WhatReasonableCareDidYouTakeController when selected InaccurateReturnWithCare & true" in {
        val set: Set[WhyAreYouMakingThisDisclosure] = Set(WhyAreYouMakingThisDisclosure.InaccurateReturnWithCare)
        val userAnswers = for {
          answer          <- UserAnswers("id").set(WhyAreYouMakingThisDisclosurePage, set)
          updatedAnswer 	<- answer.set(ContractualDisclosureFacilityPage, true)
          } yield updatedAnswer
        navigator.nextPage(ContractualDisclosureFacilityPage, NormalMode, userAnswers.success.value) mustBe routes.WhatReasonableCareDidYouTakeController.onPageLoad(NormalMode)
      }

      "must go from ContractualDisclosureFacilityPage to WhatIsYourReasonableExcuseForNotFilingReturnController when selected NotFileHasExcuse & true" in {
        val set: Set[WhyAreYouMakingThisDisclosure] = Set(WhyAreYouMakingThisDisclosure.NotFileHasExcuse)
        val userAnswers = for {
          answer          <- UserAnswers("id").set(WhyAreYouMakingThisDisclosurePage, set)
          updatedAnswer 	<- answer.set(ContractualDisclosureFacilityPage, true)
          } yield updatedAnswer
        navigator.nextPage(ContractualDisclosureFacilityPage, NormalMode, userAnswers.success.value) mustBe routes.WhatIsYourReasonableExcuseForNotFilingReturnController.onPageLoad(NormalMode)
      }

      "must go from ContractualDisclosureFacilityPage to WhichYearsController when selected any checkbox & false" in {
        val set: Set[WhyAreYouMakingThisDisclosure] = Set(
          WhyAreYouMakingThisDisclosure.InaccurateReturnNoCare,
          WhyAreYouMakingThisDisclosure.DidNotNotifyNoExcuse,
          WhyAreYouMakingThisDisclosure.DeliberatelyDidNotNotify,
          WhyAreYouMakingThisDisclosure.DeliberateInaccurateReturn,
          WhyAreYouMakingThisDisclosure.DeliberatelyDidNotFile
        )
        val userAnswers = for {
          answer          <- UserAnswers("id").set(WhyAreYouMakingThisDisclosurePage, set)
          updatedAnswer 	<- answer.set(ContractualDisclosureFacilityPage, true)
          } yield updatedAnswer
        navigator.nextPage(ContractualDisclosureFacilityPage, NormalMode, userAnswers.success.value) mustBe routes.WhichYearsController.onPageLoad(NormalMode)
      }

      "must go from ContractualDisclosureFacilityPage to YouHaveLeftTheDDSController when selected any checkbox & false" in {
        val set: Set[WhyAreYouMakingThisDisclosure] = Set(
          WhyAreYouMakingThisDisclosure.InaccurateReturnNoCare,
          WhyAreYouMakingThisDisclosure.DidNotNotifyNoExcuse,
          WhyAreYouMakingThisDisclosure.DeliberatelyDidNotNotify,
          WhyAreYouMakingThisDisclosure.DeliberateInaccurateReturn,
          WhyAreYouMakingThisDisclosure.DeliberatelyDidNotFile
        )
        val userAnswers = for {
          answer          <- UserAnswers("id").set(WhyAreYouMakingThisDisclosurePage, set)
          updatedAnswer 	<- answer.set(ContractualDisclosureFacilityPage, false)
          } yield updatedAnswer
        navigator.nextPage(ContractualDisclosureFacilityPage, NormalMode, userAnswers.success.value) mustBe routes.YouHaveLeftTheDDSController.onPageLoad(NormalMode)
      }
    }

    "in Check mode" - {

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, UserAnswers("id")) mustBe controllers.routes.IndexController.onPageLoad
      }
    }
  }

}