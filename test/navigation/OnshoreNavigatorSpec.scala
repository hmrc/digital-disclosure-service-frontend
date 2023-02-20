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
import controllers.onshore.routes
import pages._
import models._
import models.YourLegalInterpretation._
import uk.gov.hmrc.time.CurrentTaxYear

import java.time.LocalDate

class OnshoreNavigatorSpec extends SpecBase with CurrentTaxYear {

  val navigator = new OnshoreNavigator

  def now = () => LocalDate.now()

  "Onshore Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, UserAnswers("id")) mustBe controllers.routes.IndexController.onPageLoad
      }

      "must go from WhyAreYouMakingThisOnshoreDisclosurePage to CDFOnshoreController when selected any deliberate behaviour, and they're not an estate" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(
          WhyAreYouMakingThisOnshoreDisclosure.DeliberatelyDidNotNotify,
          WhyAreYouMakingThisOnshoreDisclosure.DeliberateInaccurateReturn,
          WhyAreYouMakingThisOnshoreDisclosure.DeliberatelyDidNotFile
        )
        val userAnswers = (for {
          ua <- UserAnswers("id").set(WhyAreYouMakingThisOnshoreDisclosurePage, set)
          finalUa <- ua.set(RelatesToPage, RelatesTo.AnIndividual)
        } yield finalUa).success.value
        navigator.nextPage(WhyAreYouMakingThisOnshoreDisclosurePage, NormalMode, userAnswers) mustBe routes.CDFOnshoreController.onPageLoad(NormalMode)
      }

      "must go from WhyAreYouMakingThisOnshoreDisclosurePage to WhatOnshoreLiabilitiesDoYouNeedToDiscloseController when selected a deliberate behaviour, and also selected that they're an estate" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(
          WhyAreYouMakingThisOnshoreDisclosure.DeliberatelyDidNotNotify,
          WhyAreYouMakingThisOnshoreDisclosure.DeliberateInaccurateReturn,
          WhyAreYouMakingThisOnshoreDisclosure.DeliberatelyDidNotFile
        )
        val userAnswers = (for {
          ua <- UserAnswers("id").set(WhyAreYouMakingThisOnshoreDisclosurePage, set)
          finalUa <- ua.set(RelatesToPage, RelatesTo.AnEstate)
        } yield finalUa).success.value
        navigator.nextPage(WhyAreYouMakingThisOnshoreDisclosurePage, NormalMode, userAnswers) mustBe routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
      }

      "must go from WhyAreYouMakingThisOnshoreDisclosurePage to ReasonableExcuseOnshoreController when selected DidNotNotifyHasExcuse" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHasExcuse)
        val userAnswers = UserAnswers("id").set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        navigator.nextPage(WhyAreYouMakingThisOnshoreDisclosurePage, NormalMode, userAnswers) mustBe routes.ReasonableExcuseOnshoreController.onPageLoad(NormalMode)
      }

      "must go from WhyAreYouMakingThisOnshoreDisclosurePage to ReasonableCareOnshoreController when selected InaccurateReturnWithCare" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturnWithCare)
        val userAnswers = UserAnswers("id").set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        navigator.nextPage(WhyAreYouMakingThisOnshoreDisclosurePage, NormalMode, userAnswers) mustBe routes.ReasonableCareOnshoreController.onPageLoad(NormalMode)
      }

      "must go from WhyAreYouMakingThisOnshoreDisclosurePage to ReasonableExcuseForNotFilingOnshoreController when selected NotFileHasExcuse" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.NotFileHasExcuse)
        val userAnswers = UserAnswers("id").set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        navigator.nextPage(WhyAreYouMakingThisOnshoreDisclosurePage, NormalMode, userAnswers) mustBe routes.ReasonableExcuseForNotFilingOnshoreController.onPageLoad(NormalMode)
      }

      "must go from WhyAreYouMakingThisOnshoreDisclosurePage to WhatOnshoreLiabilitiesDoYouNeedToDiscloseController when selected any other option(s)" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturnNoCare)
        val userAnswers = UserAnswers("id").set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        navigator.nextPage(WhyAreYouMakingThisOnshoreDisclosurePage, NormalMode, userAnswers) mustBe routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
      }  

      "must go from CDFOnshorePage to YouHaveLeftTheDDSOnshoreControllerController when selected any other option(s) & false" in {
        val userAnswers = UserAnswers("id").set(CDFOnshorePage, false).success.value
        navigator.nextPage(CDFOnshorePage, NormalMode, userAnswers) mustBe routes.YouHaveLeftTheDDSOnshoreController.onPageLoad(NormalMode)
      }

      "must go from CDFOnshorePage to ReasonableExcuseOnshoreController when selected DidNotNotifyHasExcuse & true" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHasExcuse)
        val userAnswers = for {
          answer          <- UserAnswers("id").set(WhyAreYouMakingThisOnshoreDisclosurePage, set)
          updatedAnswer 	<- answer.set(CDFOnshorePage, true)
          } yield updatedAnswer
        navigator.nextPage(CDFOnshorePage, NormalMode, userAnswers.success.value) mustBe routes.ReasonableExcuseOnshoreController.onPageLoad(NormalMode)
      }

      "must go from CDFOnshorePage to ReasonableCareOnshoreController when selected InaccurateReturnWithCare & true" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturnWithCare)
        val userAnswers = for {
          answer          <- UserAnswers("id").set(WhyAreYouMakingThisOnshoreDisclosurePage, set)
          updatedAnswer 	<- answer.set(CDFOnshorePage, true)
          } yield updatedAnswer
        navigator.nextPage(CDFOnshorePage, NormalMode, userAnswers.success.value) mustBe routes.ReasonableCareOnshoreController.onPageLoad(NormalMode)
      }

      "must go from CDFOnshorePage to ReasonableExcuseForNotFilingOnshoreController when selected NotFileHasExcuse & true" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.NotFileHasExcuse)
        val userAnswers = for {
          answer          <- UserAnswers("id").set(WhyAreYouMakingThisOnshoreDisclosurePage, set)
          updatedAnswer 	<- answer.set(CDFOnshorePage, true)
          } yield updatedAnswer
        navigator.nextPage(CDFOnshorePage, NormalMode, userAnswers.success.value) mustBe routes.ReasonableExcuseForNotFilingOnshoreController.onPageLoad(NormalMode)
      }

      "must go from CDFOnshorePage to WhatOnshoreLiabilitiesDoYouNeedToDiscloseController when selected any other option(s)" in {
        navigator.nextPage(CDFOnshorePage, NormalMode, UserAnswers("id")) mustBe routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
      }

      "must go from ReasonableExcuseOnshorePage to ReasonableCareOnshoreController when selected InaccurateReturnWithCare" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturnWithCare)
        val userAnswers = UserAnswers("id").set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        navigator.nextPage(ReasonableExcuseOnshorePage, NormalMode, userAnswers) mustBe routes.ReasonableCareOnshoreController.onPageLoad(NormalMode)
      }

      "must go from ReasonableExcuseOnshorePage to ReasonableExcuseForNotFilingOnshoreController when selected NotFileHasExcuse" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.NotFileHasExcuse)
        val userAnswers = UserAnswers("id").set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        navigator.nextPage(ReasonableExcuseOnshorePage, NormalMode, userAnswers) mustBe routes.ReasonableExcuseForNotFilingOnshoreController.onPageLoad(NormalMode)
      }

      "must go from ReasonableExcuseOnshorePage to WhatOnshoreLiabilitiesDoYouNeedToDiscloseController when selected any other option(s)" in {
        navigator.nextPage(ReasonableExcuseOnshorePage, NormalMode, UserAnswers("id")) mustBe routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
      }

      "must go from ReasonableCareOnshorePage to ReasonableExcuseForNotFilingOnshoreController when selected NotFileHasExcuse" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.NotFileHasExcuse)
        val userAnswers = UserAnswers("id").set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        navigator.nextPage(ReasonableCareOnshorePage, NormalMode, userAnswers) mustBe routes.ReasonableExcuseForNotFilingOnshoreController.onPageLoad(NormalMode)
      }

      "must go from ReasonableCareOnshorePage to WhatOnshoreLiabilitiesDoYouNeedToDiscloseController when selected any other option(s)" in {
        navigator.nextPage(ReasonableCareOnshorePage, NormalMode, UserAnswers("id")) mustBe routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
      }

      "must go from ReasonableExcuseForNotFilingOnshorePage to WhatOnshoreLiabilitiesDoYouNeedToDiscloseController when selected any other option(s)" in {
        navigator.nextPage(ReasonableExcuseForNotFilingOnshorePage, NormalMode, UserAnswers("id")) mustBe routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
      }

      "must go from WhatOnshoreLiabilitiesDoYouNeedToDisclose to WhichOnshoreYearsController when selected any other option(s)" in {
        navigator.nextPage(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, NormalMode, UserAnswers("id")) mustBe routes.WhichOnshoreYearsController.onPageLoad(NormalMode)
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