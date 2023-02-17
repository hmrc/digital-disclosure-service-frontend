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
import models.YourLegalInterpretation._
import uk.gov.hmrc.time.CurrentTaxYear
import config.Country

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

      "must go from WhyAreYouMakingThisOnshoreDisclosurePage to WhichYearsController when selected a deliberate behaviour, and also selected that they're an estate" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(
          WhyAreYouMakingThisOnshoreDisclosure.DeliberatelyDidNotNotify,
          WhyAreYouMakingThisOnshoreDisclosure.DeliberateInaccurateReturn,
          WhyAreYouMakingThisOnshoreDisclosure.DeliberatelyDidNotFile
        )
        val userAnswers = (for {
          ua <- UserAnswers("id").set(WhyAreYouMakingThisOnshoreDisclosurePage, set)
          finalUa <- ua.set(RelatesToPage, RelatesTo.AnEstate)
        } yield finalUa).success.value
        navigator.nextPage(WhyAreYouMakingThisOnshoreDisclosurePage, NormalMode, userAnswers) mustBe routes.WhichYearsController.onPageLoad(NormalMode)
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

      "must go from WhyAreYouMakingThisOnshoreDisclosurePage to ReasonableExcuseOnshoreForNotFilingReturnController when selected NotFileHasExcuse" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.NotFileHasExcuse)
        val userAnswers = UserAnswers("id").set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        navigator.nextPage(WhyAreYouMakingThisOnshoreDisclosurePage, NormalMode, userAnswers) mustBe routes.ReasonableExcuseOnshoreForNotFilingReturnController.onPageLoad(NormalMode)
      }

      "must go from WhyAreYouMakingThisOnshoreDisclosurePage to WhichYearsController when selected any other option(s)" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturnNoCare)
        val userAnswers = UserAnswers("id").set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        navigator.nextPage(WhyAreYouMakingThisOnshoreDisclosurePage, NormalMode, userAnswers) mustBe routes.WhichYearsController.onPageLoad(NormalMode)
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