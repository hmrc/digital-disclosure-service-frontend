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
import models.WhatOnshoreLiabilitiesDoYouNeedToDisclose.{CorporationTax, DirectorLoan}
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
        navigator.nextPage(UnknownPage, NormalMode, UserAnswers("id")) mustBe controllers.routes.TaskListController.onPageLoad
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

      "must go from WhichOnshoreYearsPage to TaxBeforeThreeYearsOnshoreController when selected option PriorToThreeYears" in {
        val set: Set[OnshoreYears] = Set(PriorToThreeYears)
        val userAnswers = UserAnswers("id").set(WhichOnshoreYearsPage, set).success.value
        navigator.nextPage(WhichOnshoreYearsPage, NormalMode, userAnswers) mustBe routes.TaxBeforeThreeYearsOnshoreController.onPageLoad(NormalMode)
      }

      "must go from WhichOnshoreYearsPage to TaxBeforeFiveYearsOnshoreController when selected option PriorToFiveYears" in {
        val set: Set[OnshoreYears] = Set(PriorToFiveYears)
        val userAnswers = UserAnswers("id").set(WhichOnshoreYearsPage, set).success.value
        navigator.nextPage(WhichOnshoreYearsPage, NormalMode, userAnswers) mustBe routes.TaxBeforeFiveYearsOnshoreController.onPageLoad(NormalMode)
      }

      "must go from WhichOnshoreYearsPage to TaxBeforeNineteenYearsController when selected option PriorToNineteenYears" in {
        val set: Set[OnshoreYears] = Set(PriorToNineteenYears)
        val userAnswers = UserAnswers("id").set(WhichOnshoreYearsPage, set).success.value
        navigator.nextPage(WhichOnshoreYearsPage, NormalMode, userAnswers) mustBe routes.TaxBeforeNineteenYearsOnshoreController.onPageLoad(NormalMode)
      }

      "must go from WhichOnshoreYearsPage to NotIncludedSingleTaxYearController when not selected an entire interval" in {
        val year = current.back(1).startYear
        val year2 = current.back(3).startYear
        val set: Set[OnshoreYears] = Set(OnshoreYearStarting(year), OnshoreYearStarting(year2))
        val userAnswers = UserAnswers("id").set(WhichOnshoreYearsPage, set).success.value
        navigator.nextPage(WhichOnshoreYearsPage, NormalMode, userAnswers) mustBe routes.NotIncludedSingleTaxYearController.onPageLoad(NormalMode)
      }

      "must go from WhichOnshoreYearsPage to NotIncludedMultipleTaxYearsController when multiple intervals are missing" in {
        val year = current.back(1).startYear
        val year2 = current.back(3).startYear
        val year3 = current.back(5).startYear
        val set: Set[OnshoreYears] = Set(OnshoreYearStarting(year), OnshoreYearStarting(year2), OnshoreYearStarting(year3))
        val userAnswers = UserAnswers("id").set(WhichOnshoreYearsPage, set).success.value
        navigator.nextPage(WhichOnshoreYearsPage, NormalMode, userAnswers) mustBe routes.NotIncludedMultipleTaxYearsController.onPageLoad(NormalMode)
      }

      "must go from WhichOnshoreYearsPage to OnshoreTaxYearLiabilitiesController" in {
        val year = current.back(1).startYear
        val set: Set[OnshoreYears] = Set(OnshoreYearStarting(year))
        val userAnswers = UserAnswers("id").set(WhichOnshoreYearsPage, set).success.value
        navigator.nextPage(WhichOnshoreYearsPage, NormalMode, userAnswers) mustBe routes.OnshoreTaxYearLiabilitiesController.onPageLoad(0, NormalMode)
      }

      "must go from WhatOnshoreLiabilitiesDoYouNeedToDisclose to the CorporationTaxLiabilityController when CorporationTax is selected" in {
        val set: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] = Set(CorporationTax)
        val userAnswers = UserAnswers("id").set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, set).success.value
        navigator.nextPage(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, NormalMode, userAnswers) mustBe routes.CorporationTaxLiabilityController.onPageLoad(0, NormalMode)
      }

      "must go from WhatOnshoreLiabilitiesDoYouNeedToDisclose to DirectorLoanAccountLiabilitiesController when DirectorLoan is selected" in {
        val set: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] = Set(DirectorLoan)
        val userAnswers = UserAnswers("id").set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, set).success.value
        navigator.nextPage(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, NormalMode, userAnswers) mustBe routes.DirectorLoanAccountLiabilitiesController.onPageLoad(0, NormalMode)
      }

      "must go from WhatOnshoreLiabilitiesDoYouNeedToDisclose to WhichOnshoreYearsController when the user has not selected CorporationTax or DirectorLoan" in {
        val set: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] = Set()
        val userAnswers = UserAnswers("id").set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, set).success.value
        navigator.nextPage(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, NormalMode, userAnswers) mustBe routes.WhichOnshoreYearsController.onPageLoad(NormalMode)
      }

      "must go from IncomeOrGainSourcePage to OtherIncomeOrGainSourceController when the user has selected SomewhereElse" in {
        val set: Set[IncomeOrGainSource] = Set(IncomeOrGainSource.SomewhereElse)
        val userAnswers = UserAnswers("id").set(IncomeOrGainSourcePage, set).success.value
        navigator.nextPage(IncomeOrGainSourcePage, NormalMode, userAnswers) mustBe routes.OtherIncomeOrGainSourceController.onPageLoad(NormalMode)
      }

      "must go from IncomeOrGainSourcePage to OtherIncomeOrGainSourceController when the user has selected a combination of SomewhereElse with other answers" in {
        val set: Set[IncomeOrGainSource] = Set(IncomeOrGainSource.SomewhereElse, IncomeOrGainSource.PropertyIncome, IncomeOrGainSource.SelfEmploymentIncome)
        val userAnswers = UserAnswers("id").set(IncomeOrGainSourcePage, set).success.value
        navigator.nextPage(IncomeOrGainSourcePage, NormalMode, userAnswers) mustBe routes.OtherIncomeOrGainSourceController.onPageLoad(NormalMode)
      }

      "must go from IncomeOrGainSourcePage to OtherIncomeOrGainSourceController when the user has not selected SomewhereElse" in {
        val set: Set[IncomeOrGainSource] = Set(IncomeOrGainSource.PropertyIncome, IncomeOrGainSource.SelfEmploymentIncome)
        val userAnswers = UserAnswers("id").set(IncomeOrGainSourcePage, set).success.value
        navigator.nextPage(IncomeOrGainSourcePage, NormalMode, userAnswers) mustBe routes.CheckYourAnswersController.onPageLoad
      }
    }

    "nextTaxYearLiabilitiesPage" - {

      "must go to ResidentialReductionController with the current index when the residential reduction is set to true" in {
        val set: Set[OnshoreYears] = Set(OnshoreYearStarting(2012))
        val userAnswers = UserAnswers("id").set(WhichOnshoreYearsPage, set).success.value
        navigator.nextTaxYearLiabilitiesPage(0, true, NormalMode, userAnswers, false) mustBe routes.ResidentialReductionController.onPageLoad(0, NormalMode)
      }

      "must increment the index and take the user to the tax year liability page when there more years in the which years list" in {
        val whichYears: Set[OnshoreYears] = Set(OnshoreYearStarting(2021), OnshoreYearStarting(2020), OnshoreYearStarting(2019), OnshoreYearStarting(2018))
        val userAnswersWithTaxYears = UserAnswers(userAnswersId).set(WhichOnshoreYearsPage, whichYears).success.value

        navigator.nextTaxYearLiabilitiesPage(0, false, NormalMode, userAnswersWithTaxYears) mustBe routes.OnshoreTaxYearLiabilitiesController.onPageLoad(1, NormalMode)
        navigator.nextTaxYearLiabilitiesPage(1, false, NormalMode, userAnswersWithTaxYears) mustBe routes.OnshoreTaxYearLiabilitiesController.onPageLoad(2, NormalMode)
        navigator.nextTaxYearLiabilitiesPage(2, false, NormalMode, userAnswersWithTaxYears) mustBe routes.OnshoreTaxYearLiabilitiesController.onPageLoad(3, NormalMode)
      }

      "must take the user to the next page when there are no more years in the which years list" in {
        val whichYears: Set[OnshoreYears] = Set(OnshoreYearStarting(2021), OnshoreYearStarting(2020), OnshoreYearStarting(2019), OnshoreYearStarting(2018))
        val userAnswersWithTaxYears = UserAnswers(userAnswersId).set(WhichOnshoreYearsPage, whichYears).success.value

        navigator.nextTaxYearLiabilitiesPage(3, false, NormalMode, userAnswersWithTaxYears) mustBe routes.IncomeOrGainSourceController.onPageLoad(NormalMode)
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