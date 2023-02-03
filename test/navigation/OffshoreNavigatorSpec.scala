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

import java.time.LocalDate

class OffshoreNavigatorSpec extends SpecBase with CurrentTaxYear {

  val navigator = new OffshoreNavigator

  def now = () => LocalDate.now()

  "Offshore Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, UserAnswers("id")) mustBe controllers.routes.IndexController.onPageLoad
      }

      "must go from WhyAreYouMakingThisDisclosurePage to ContractualDisclosureFacilityController when selected any or all (DeliberatelyDidNotNotify/DeliberateInaccurateReturn/DeliberatelyDidNotFile)" in {
        val set: Set[WhyAreYouMakingThisDisclosure] = Set(
          WhyAreYouMakingThisDisclosure.DeliberatelyDidNotNotify,
          WhyAreYouMakingThisDisclosure.DeliberateInaccurateReturn,
          WhyAreYouMakingThisDisclosure.DeliberatelyDidNotFile
        )
        val userAnswers = UserAnswers("id").set(WhyAreYouMakingThisDisclosurePage, set).success.value
        navigator.nextPage(WhyAreYouMakingThisDisclosurePage, NormalMode, userAnswers) mustBe routes.ContractualDisclosureFacilityController.onPageLoad(NormalMode)
      }

      "must go from WhyAreYouMakingThisDisclosurePage to WhatIsYourReasonableExcuseController when selected DidNotNotifyHasExcuse" in {
        val set: Set[WhyAreYouMakingThisDisclosure] = Set(WhyAreYouMakingThisDisclosure.DidNotNotifyHasExcuse)
        val userAnswers = UserAnswers("id").set(WhyAreYouMakingThisDisclosurePage, set).success.value
        navigator.nextPage(WhyAreYouMakingThisDisclosurePage, NormalMode, userAnswers) mustBe routes.WhatIsYourReasonableExcuseController.onPageLoad(NormalMode)
      }

      "must go from WhyAreYouMakingThisDisclosurePage to WhatReasonableCareDidYouTakeController when selected InaccurateReturnWithCare" in {
        val set: Set[WhyAreYouMakingThisDisclosure] = Set(WhyAreYouMakingThisDisclosure.InaccurateReturnWithCare)
        val userAnswers = UserAnswers("id").set(WhyAreYouMakingThisDisclosurePage, set).success.value
        navigator.nextPage(WhyAreYouMakingThisDisclosurePage, NormalMode, userAnswers) mustBe routes.WhatReasonableCareDidYouTakeController.onPageLoad(NormalMode)
      }

      "must go from WhyAreYouMakingThisDisclosurePage to WhatIsYourReasonableExcuseForNotFilingReturnController when selected NotFileHasExcuse" in {
        val set: Set[WhyAreYouMakingThisDisclosure] = Set(WhyAreYouMakingThisDisclosure.NotFileHasExcuse)
        val userAnswers = UserAnswers("id").set(WhyAreYouMakingThisDisclosurePage, set).success.value
        navigator.nextPage(WhyAreYouMakingThisDisclosurePage, NormalMode, userAnswers) mustBe routes.WhatIsYourReasonableExcuseForNotFilingReturnController.onPageLoad(NormalMode)
      }

      "must go from WhyAreYouMakingThisDisclosurePage to WhichYearsController when selected any other option(s)" in {
        val set: Set[WhyAreYouMakingThisDisclosure] = Set(WhyAreYouMakingThisDisclosure.InaccurateReturnNoCare)
        val userAnswers = UserAnswers("id").set(WhyAreYouMakingThisDisclosurePage, set).success.value
        navigator.nextPage(WhyAreYouMakingThisDisclosurePage, NormalMode, userAnswers) mustBe routes.WhichYearsController.onPageLoad(NormalMode)
      }  

      "must go from ContractualDisclosureFacilityPage to YouHaveLeftTheDDSController when selected any other option(s) & false" in {
        val userAnswers = UserAnswers("id").set(ContractualDisclosureFacilityPage, false).success.value
        navigator.nextPage(ContractualDisclosureFacilityPage, NormalMode, userAnswers) mustBe routes.YouHaveLeftTheDDSController.onPageLoad(NormalMode)
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

      "must go from ContractualDisclosureFacilityPage to WhichYearsController when selected any other option(s)" in {
        navigator.nextPage(ContractualDisclosureFacilityPage, NormalMode, UserAnswers("id")) mustBe routes.WhichYearsController.onPageLoad(NormalMode)
      }

      "must go from WhatIsYourReasonableExcusePage to WhatReasonableCareDidYouTakeController when selected InaccurateReturnWithCare" in {
        val set: Set[WhyAreYouMakingThisDisclosure] = Set(WhyAreYouMakingThisDisclosure.InaccurateReturnWithCare)
        val userAnswers = UserAnswers("id").set(WhyAreYouMakingThisDisclosurePage, set).success.value
        navigator.nextPage(WhatIsYourReasonableExcusePage, NormalMode, userAnswers) mustBe routes.WhatReasonableCareDidYouTakeController.onPageLoad(NormalMode)
      }

      "must go from WhatIsYourReasonableExcusePage to WhatIsYourReasonableExcuseForNotFilingReturnController when selected NotFileHasExcuse" in {
        val set: Set[WhyAreYouMakingThisDisclosure] = Set(WhyAreYouMakingThisDisclosure.NotFileHasExcuse)
        val userAnswers = UserAnswers("id").set(WhyAreYouMakingThisDisclosurePage, set).success.value
        navigator.nextPage(WhatIsYourReasonableExcusePage, NormalMode, userAnswers) mustBe routes.WhatIsYourReasonableExcuseForNotFilingReturnController.onPageLoad(NormalMode)
      }

      "must go from WhatIsYourReasonableExcusePage to WhichYearsController when selected any other option(s)" in {
        navigator.nextPage(WhatIsYourReasonableExcusePage, NormalMode, UserAnswers("id")) mustBe routes.WhichYearsController.onPageLoad(NormalMode)
      }

      "must go from WhatReasonableCareDidYouTakePage to WhatIsYourReasonableExcuseForNotFilingReturnController when selected NotFileHasExcuse" in {
        val set: Set[WhyAreYouMakingThisDisclosure] = Set(WhyAreYouMakingThisDisclosure.NotFileHasExcuse)
        val userAnswers = UserAnswers("id").set(WhyAreYouMakingThisDisclosurePage, set).success.value
        navigator.nextPage(WhatReasonableCareDidYouTakePage, NormalMode, userAnswers) mustBe routes.WhatIsYourReasonableExcuseForNotFilingReturnController.onPageLoad(NormalMode)
      }

      "must go from WhatReasonableCareDidYouTakePage to WhichYearsController when selected any other option(s)" in {
        navigator.nextPage(WhatReasonableCareDidYouTakePage, NormalMode, UserAnswers("id")) mustBe routes.WhichYearsController.onPageLoad(NormalMode)
      }

      "must go from WhatIsYourReasonableExcuseForNotFilingReturnPage to WhichYearsController when selected any other option(s)" in {
        navigator.nextPage(WhatIsYourReasonableExcuseForNotFilingReturnPage, NormalMode, UserAnswers("id")) mustBe routes.WhichYearsController.onPageLoad(NormalMode)
      }

      "must go from CountryOfYourOffshoreLiabilityPage to CountriesOrTerritoriesController" in {
        navigator.nextPage(CountryOfYourOffshoreLiabilityPage, NormalMode, UserAnswers("id")) mustBe routes.CountriesOrTerritoriesController.onPageLoad(NormalMode)
      }


      Seq(
        YourResidenceStatus,
        YourDomicileStatus,
        TheRemittanceBasis,
        HowIncomeArisingInATrust,
        TheTransferOfAssets,
        HowIncomeArisingInAnOffshore,
        InheritanceTaxIssues,
        WhetherIncomeShouldBeTaxed,
      ).foreach { option => 
        s"must go from YourLegalInterpretationPage to UnderWhatConsiderationController when selected option $option & anotherIssue" in {
          val set: Set[YourLegalInterpretation] = Set(option)
          val updatedSet: Set[YourLegalInterpretation] = Set(AnotherIssue)
          
          val userAnswers = for {
            answer          <- UserAnswers("id").set(YourLegalInterpretationPage, set)
            updatedAnswer 	<- answer.set(YourLegalInterpretationPage, updatedSet)
          } yield updatedAnswer
        
          navigator.nextPage(YourLegalInterpretationPage, NormalMode, userAnswers.success.value) mustBe routes.UnderWhatConsiderationController.onPageLoad(NormalMode)
        }
      }

      Seq(
        YourResidenceStatus,
        YourDomicileStatus,
        TheRemittanceBasis,
        HowIncomeArisingInATrust,
        TheTransferOfAssets,
        HowIncomeArisingInAnOffshore,
        InheritanceTaxIssues,
        WhetherIncomeShouldBeTaxed,
      ).foreach { option => 
        s"must go from YourLegalInterpretationPage to HowMuchTaxHasNotBeenIncludedController when selected option $option" in {
          val set: Set[YourLegalInterpretation] = Set(option)
          val userAnswers = UserAnswers("id").set(YourLegalInterpretationPage, set).success.value
          navigator.nextPage(YourLegalInterpretationPage, NormalMode, userAnswers) mustBe routes.HowMuchTaxHasNotBeenIncludedController.onPageLoad(NormalMode)
        }
      }

      "must go from YourLegalInterpretationPage to UnderWhatConsiderationController when selected option anotherIssue" in {
        val set: Set[YourLegalInterpretation] = Set(AnotherIssue)
        val userAnswers = UserAnswers("id").set(YourLegalInterpretationPage, set).success.value
        navigator.nextPage(YourLegalInterpretationPage, NormalMode, userAnswers) mustBe routes.UnderWhatConsiderationController.onPageLoad(NormalMode)
      }

      "must go from YourLegalInterpretationPage to TheMaximumValueOfAllAssetsController when selected option NoExclusion" in {
        val set: Set[YourLegalInterpretation] = Set(NoExclusion)
        val userAnswers = UserAnswers("id").set(YourLegalInterpretationPage, set).success.value
        navigator.nextPage(YourLegalInterpretationPage, NormalMode, userAnswers) mustBe routes.TheMaximumValueOfAllAssetsController.onPageLoad(NormalMode)
      }
      
      "must go from UnderWhatConsiderationPage to HowMuchTaxHasNotBeenIncludedController" in {
        navigator.nextPage(UnderWhatConsiderationPage, NormalMode, UserAnswers("id")) mustBe routes.HowMuchTaxHasNotBeenIncludedController.onPageLoad(NormalMode)
      }

      "must go from HowMuchTaxHasNotBeenIncludedPage to TheMaximumValueOfAllAssetsController" in {
        navigator.nextPage(HowMuchTaxHasNotBeenIncludedPage, NormalMode, UserAnswers("id")) mustBe routes.TheMaximumValueOfAllAssetsController.onPageLoad(NormalMode)
      }

      "must go from WhichYearsPage to TaxBeforeFiveYearsController when selected option PriorTo5Years" in {
        val set: Set[OffshoreYears] = Set(PriorTo5Years)
        val userAnswers = UserAnswers("id").set(WhichYearsPage, set).success.value
        navigator.nextPage(WhichYearsPage, NormalMode, userAnswers) mustBe routes.TaxBeforeFiveYearsController.onPageLoad(NormalMode)
      }

      "must go from WhichYearsPage to TaxBeforeSevenYearsController when selected option PriorTo5Years" in {
        val set: Set[OffshoreYears] = Set(PriorTo7Years)
        val userAnswers = UserAnswers("id").set(WhichYearsPage, set).success.value
        navigator.nextPage(WhichYearsPage, NormalMode, userAnswers) mustBe routes.TaxBeforeSevenYearsController.onPageLoad(NormalMode)
      }

      "must go from WhichYearsPage to CountryOfYourOffshoreLiabilityController when selected option PriorTo5Years" in {
        val year = current.back(1).startYear
        val set: Set[OffshoreYears] = Set(TaxYearStarting(year))
        val userAnswers = UserAnswers("id").set(WhichYearsPage, set).success.value
        navigator.nextPage(WhichYearsPage, NormalMode, userAnswers) mustBe routes.TaxYearLiabilitiesController.onPageLoad(0, NormalMode)
      }

      "must go from TaxBeforeFiveYearsPage to MakingNilDisclosureController when only selected option PriorTo5Years" in {
        val set: Set[OffshoreYears] = Set(PriorTo5Years)
        val userAnswers = UserAnswers("id").set(WhichYearsPage, set).success.value
        navigator.nextPage(TaxBeforeFiveYearsPage, NormalMode, userAnswers) mustBe controllers.routes.MakingNilDisclosureController.onPageLoad
      }

      "must go from TaxBeforeFiveYearsPage to CountryOfYourOffshoreLiabilityController when selected more than one TaxYear option including PriorTo5Years" in {
        val year = current.back(1).startYear
        val set: Set[OffshoreYears] = Set(PriorTo5Years, TaxYearStarting(year))
        val userAnswers = UserAnswers("id").set(WhichYearsPage, set).success.value
        navigator.nextPage(TaxBeforeFiveYearsPage, NormalMode, userAnswers) mustBe routes.CountryOfYourOffshoreLiabilityController.onPageLoad(None, NormalMode)
      }

      "must go from TaxBeforeSevenYearsPage to MakingNilDisclosureController when only selected option PriorTo7Years" in {
        val set: Set[OffshoreYears] = Set(PriorTo7Years)
        val userAnswers = UserAnswers("id").set(WhichYearsPage, set).success.value
        navigator.nextPage(TaxBeforeSevenYearsPage, NormalMode, userAnswers) mustBe controllers.routes.MakingNilDisclosureController.onPageLoad
      }

      "must go from TaxBeforeSevenYearsPage to CountryOfYourOffshoreLiabilityController when selected more than one TaxYear option including PriorTo7Years" in {
        val year = current.back(1).startYear
        val set: Set[OffshoreYears] = Set(PriorTo7Years, TaxYearStarting(year))
        val userAnswers = UserAnswers("id").set(WhichYearsPage, set).success.value
        navigator.nextPage(TaxBeforeSevenYearsPage, NormalMode, userAnswers) mustBe routes.CountryOfYourOffshoreLiabilityController.onPageLoad(None, NormalMode)
      }
    }

    "in Check mode" - {

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, UserAnswers("id")) mustBe routes.CheckYourAnswersController.onPageLoad
      }
    }

    "nextTaxYearLiabilitiesPage" - {

      "must take the user to the CYA page in check mode" in {
        val whichYears: Set[OffshoreYears] = Set(TaxYearStarting(2021), TaxYearStarting(2020), TaxYearStarting(2019))
        val userAnswersWithTaxYears = UserAnswers(userAnswersId).set(WhichYearsPage, whichYears).success.value

        navigator.nextTaxYearLiabilitiesPage(0, CheckMode, userAnswersWithTaxYears) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must increment the index and tax the user to the tax year liability page when there more years in the which years list" in {
        val whichYears: Set[OffshoreYears] = Set(TaxYearStarting(2021), TaxYearStarting(2020), TaxYearStarting(2019), TaxYearStarting(2018))
        val userAnswersWithTaxYears = UserAnswers(userAnswersId).set(WhichYearsPage, whichYears).success.value

        navigator.nextTaxYearLiabilitiesPage(0, NormalMode, userAnswersWithTaxYears) mustBe routes.TaxYearLiabilitiesController.onPageLoad(1, NormalMode)
        navigator.nextTaxYearLiabilitiesPage(1, NormalMode, userAnswersWithTaxYears) mustBe routes.TaxYearLiabilitiesController.onPageLoad(2, NormalMode)
        navigator.nextTaxYearLiabilitiesPage(2, NormalMode, userAnswersWithTaxYears) mustBe routes.TaxYearLiabilitiesController.onPageLoad(3, NormalMode)
      }

      "must take the user to the next page when there are no more years in the which years list" in {
        val whichYears: Set[OffshoreYears] = Set(TaxYearStarting(2021), TaxYearStarting(2020), TaxYearStarting(2019), TaxYearStarting(2018))
        val userAnswersWithTaxYears = UserAnswers(userAnswersId).set(WhichYearsPage, whichYears).success.value

        navigator.nextTaxYearLiabilitiesPage(3, NormalMode, userAnswersWithTaxYears) mustBe routes.YourLegalInterpretationController.onPageLoad(NormalMode)
      }

    }
  }

}