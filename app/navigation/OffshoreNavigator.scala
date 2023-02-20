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
import controllers.offshore.routes
import pages._
import models.{CarelessPriorTo, CheckMode, DeliberatePriorTo, Mode, NormalMode, ReasonableExcusePriorTo, RelatesTo, TaxYearStarting, UserAnswers}
import models.WhyAreYouMakingThisDisclosure._
import models.YourLegalInterpretation._
import models.WhereDidTheUndeclaredIncomeOrGainIncluded._

@Singleton
class OffshoreNavigator @Inject()() {

  private val normalRoutes: Page => UserAnswers => Call = {

    case WhyAreYouMakingThisDisclosurePage => ua => (ua.get(WhyAreYouMakingThisDisclosurePage), ua.get(RelatesToPage)) match {
      case (Some(value), Some(entity)) if ( (entity != RelatesTo.AnEstate) && (
        value.contains(DeliberatelyDidNotNotify) ||
        value.contains(DeliberateInaccurateReturn) ||
        value.contains(DeliberatelyDidNotFile))
        ) => routes.ContractualDisclosureFacilityController.onPageLoad(NormalMode)
      case (Some(value), _) if (value.contains(DidNotNotifyHasExcuse)) => routes.WhatIsYourReasonableExcuseController.onPageLoad(NormalMode)
      case (Some(value), _) if (value.contains(InaccurateReturnWithCare)) => routes.WhatReasonableCareDidYouTakeController.onPageLoad(NormalMode)
      case (Some(value), _) if (value.contains(NotFileHasExcuse)) => routes.WhatIsYourReasonableExcuseForNotFilingReturnController.onPageLoad(NormalMode)
      case _ => routes.WhichYearsController.onPageLoad(NormalMode)
    }

    case ContractualDisclosureFacilityPage => ua => (ua.get(WhyAreYouMakingThisDisclosurePage), ua.get(ContractualDisclosureFacilityPage)) match {
      case (_, Some(false)) => routes.YouHaveLeftTheDDSController.onPageLoad(NormalMode)
      case (Some(value), Some(true)) if (value.contains(DidNotNotifyHasExcuse)) => routes.WhatIsYourReasonableExcuseController.onPageLoad(NormalMode)
      case (Some(value), Some(true)) if (value.contains(InaccurateReturnWithCare)) => routes.WhatReasonableCareDidYouTakeController.onPageLoad(NormalMode)
      case (Some(value), Some(true)) if (value.contains(NotFileHasExcuse)) => routes.WhatIsYourReasonableExcuseForNotFilingReturnController.onPageLoad(NormalMode)
      case _ => routes.WhichYearsController.onPageLoad(NormalMode)
    }

    case WhatIsYourReasonableExcusePage => ua => ua.get(WhyAreYouMakingThisDisclosurePage) match {
      case Some(value) if (value.contains(InaccurateReturnWithCare)) => routes.WhatReasonableCareDidYouTakeController.onPageLoad(NormalMode)
      case Some(value) if (value.contains(NotFileHasExcuse)) => routes.WhatIsYourReasonableExcuseForNotFilingReturnController.onPageLoad(NormalMode)
      case _ => routes.WhichYearsController.onPageLoad(NormalMode)
    }

    case WhatReasonableCareDidYouTakePage => ua => ua.get(WhyAreYouMakingThisDisclosurePage) match {
      case Some(value) if (value.contains(NotFileHasExcuse)) => routes.WhatIsYourReasonableExcuseForNotFilingReturnController.onPageLoad(NormalMode)
      case _ => routes.WhichYearsController.onPageLoad(NormalMode)
    }

    case WhatIsYourReasonableExcuseForNotFilingReturnPage => _ => routes.WhichYearsController.onPageLoad(NormalMode)

    case WhichYearsPage => ua => {
      val missingYearsCount = ua.inverselySortedOffshoreTaxYears.map(ty => TaxYearStarting.findMissingYears(ty.toList).size)
      (ua.get(WhichYearsPage), missingYearsCount, ua.get(CountryOfYourOffshoreLiabilityPage)) match {
        case (Some(years), Some(0), Some(countryMap)) if years.contains(ReasonableExcusePriorTo) => routes.TaxBeforeFiveYearsController.onPageLoad(NormalMode)
        case (Some(years), Some(0), Some(countryMap)) if years.contains(CarelessPriorTo) => routes.TaxBeforeSevenYearsController.onPageLoad(NormalMode)
        case (Some(years), Some(0), Some(countryMap)) if years.contains(DeliberatePriorTo) => routes.CanYouTellUsMoreAboutTaxBeforeNineteenYearController.onPageLoad(NormalMode)
        case (Some(_), Some(0), Some(countryMap)) if !countryMap.isEmpty => routes.CountriesOrTerritoriesController.onPageLoad(NormalMode)
        case (Some(_), Some(1), Some(countryMap)) => routes.YouHaveNotIncludedTheTaxYearController.onPageLoad(NormalMode)
        case (Some(_), Some(_), Some(countryMap)) => routes.YouHaveNotSelectedCertainTaxYearController.onPageLoad(NormalMode)
        case (_, _, _) => routes.CountryOfYourOffshoreLiabilityController.onPageLoad(None, NormalMode)
      }
    }

    case YourLegalInterpretationPage => ua => ua.get(YourLegalInterpretationPage) match {
      case Some(value) if(value.contains(AnotherIssue)) => routes.UnderWhatConsiderationController.onPageLoad(NormalMode)
      case Some(value) if(value == Set(NoExclusion)) => routes.TheMaximumValueOfAllAssetsController.onPageLoad(NormalMode)
      case _ => routes.HowMuchTaxHasNotBeenIncludedController.onPageLoad(NormalMode)
    }

    case UnderWhatConsiderationPage => _ => routes.HowMuchTaxHasNotBeenIncludedController.onPageLoad(NormalMode)

    case HowMuchTaxHasNotBeenIncludedPage => _ => routes.TheMaximumValueOfAllAssetsController.onPageLoad(NormalMode)

    case TheMaximumValueOfAllAssetsPage => _ => routes.CheckYourAnswersController.onPageLoad

    case TaxBeforeFiveYearsPage => ua => (ua.get(WhichYearsPage), ua.get(CountryOfYourOffshoreLiabilityPage)) match {
      case (Some(whichYear), _) if whichYear.contains(ReasonableExcusePriorTo) && whichYear.size == 1 => controllers.routes.MakingNilDisclosureController.onPageLoad
      case (Some(_), Some(countryMap)) if !countryMap.isEmpty => routes.CountriesOrTerritoriesController.onPageLoad(NormalMode)
      case (_, _) => routes.CountryOfYourOffshoreLiabilityController.onPageLoad(None, NormalMode)
    }

    case TaxBeforeSevenYearsPage => ua => (ua.get(WhichYearsPage), ua.get(CountryOfYourOffshoreLiabilityPage)) match {
      case (Some(whichYear), _) if whichYear.contains(CarelessPriorTo) && whichYear.size == 1 => controllers.routes.MakingNilDisclosureController.onPageLoad
      case (Some(_), Some(countryMap)) if !countryMap.isEmpty => routes.CountriesOrTerritoriesController.onPageLoad(NormalMode)
      case (_, _) => routes.CountryOfYourOffshoreLiabilityController.onPageLoad(None, NormalMode)
    }

    case CanYouTellUsMoreAboutTaxBeforeNineteenYearPage => ua => (ua.get(WhichYearsPage), ua.get(CountryOfYourOffshoreLiabilityPage)) match {
      case (Some(whichYear), _) if whichYear.contains(DeliberatePriorTo) && whichYear.size == 1 => controllers.routes.MakingNilDisclosureController.onPageLoad
      case (Some(_), Some(countryMap)) if !countryMap.isEmpty => routes.CountriesOrTerritoriesController.onPageLoad(NormalMode)
      case (_, _) => routes.CountryOfYourOffshoreLiabilityController.onPageLoad(None, NormalMode)
    }

    case CountryOfYourOffshoreLiabilityPage => _ => routes.CountriesOrTerritoriesController.onPageLoad(NormalMode)

    case CountriesOrTerritoriesPage => ua => ua.get(CountriesOrTerritoriesPage) match {
      case Some(true) => routes.CountryOfYourOffshoreLiabilityController.onPageLoad(None, NormalMode)
      case Some(false) => routes.TaxYearLiabilitiesController.onPageLoad(0, NormalMode)
      case _ => routes.CountriesOrTerritoriesController.onPageLoad(NormalMode)
    }

    case YouHaveNotIncludedTheTaxYearPage => ua => ua.get(CountryOfYourOffshoreLiabilityPage) match {
      case Some(countryMap) if !countryMap.isEmpty => routes.CountriesOrTerritoriesController.onPageLoad(NormalMode)
      case _ => routes.CountryOfYourOffshoreLiabilityController.onPageLoad(None, NormalMode)
    }

    case YouHaveNotSelectedCertainTaxYearPage => ua => ua.get(CountryOfYourOffshoreLiabilityPage) match {
      case Some(countryMap) if !countryMap.isEmpty => routes.CountriesOrTerritoriesController.onPageLoad(NormalMode)
      case _ => routes.CountryOfYourOffshoreLiabilityController.onPageLoad(None, NormalMode)
    }

    case WhereDidTheUndeclaredIncomeOrGainIncludedPage => ua => ua.get(WhereDidTheUndeclaredIncomeOrGainIncludedPage) match {
      case Some(value) if(value.contains(SomewhereElse)) => routes.WhereDidTheUndeclaredIncomeOrGainController.onPageLoad(NormalMode)
      case _ => routes.YourLegalInterpretationController.onPageLoad(NormalMode)
    }

    case WhereDidTheUndeclaredIncomeOrGainPage => _ => routes.YourLegalInterpretationController.onPageLoad(NormalMode)

    case _ => _ => controllers.routes.IndexController.onPageLoad
  }

  private val checkRouteMap: Page => UserAnswers => Boolean => Call = {

    case CountryOfYourOffshoreLiabilityPage => _ => _ => routes.CountriesOrTerritoriesController.onPageLoad(CheckMode)

    case CountriesOrTerritoriesPage => ua => _ => ua.get(CountriesOrTerritoriesPage) match {
        case Some(true) => routes.CountryOfYourOffshoreLiabilityController.onPageLoad(None, CheckMode)
        case Some(false) => routes.CheckYourAnswersController.onPageLoad
        case _ => routes.CountriesOrTerritoriesController.onPageLoad(CheckMode)
      }

    case WhyAreYouMakingThisDisclosurePage => ua => hasChanged =>
        if (hasChanged) normalRoutes(WhyAreYouMakingThisDisclosurePage)(ua)
        else routes.CheckYourAnswersController.onPageLoad

    case YourLegalInterpretationPage => ua => {
      case true => nextPage(YourLegalInterpretationPage, NormalMode, ua)
      case false => routes.CheckYourAnswersController.onPageLoad
    }
    
    case WhichYearsPage => ua => hasAnswerChanged => 
      if(hasAnswerChanged) nextPage(WhichYearsPage, NormalMode, ua)
      else routes.CheckYourAnswersController.onPageLoad

    case YourLegalInterpretationPage => ua => hasAnswerChanged => 
      if(hasAnswerChanged) nextPage(YourLegalInterpretationPage, NormalMode, ua)
      else routes.CheckYourAnswersController.onPageLoad

    case _ => _ => _ => routes.CheckYourAnswersController.onPageLoad
  }

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers, hasAnswerChanged: Boolean = true): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)(hasAnswerChanged)
  }

  def nextTaxYearLiabilitiesPage(currentIndex: Int, foreignTaxCreditReduction: Boolean, mode: Mode, userAnswers: UserAnswers, hasAnswerChanged:Boolean = false): Call =
    (mode, foreignTaxCreditReduction, userAnswers.inverselySortedOffshoreTaxYears, hasAnswerChanged) match {
    case (NormalMode, true, _, _) => routes.ForeignTaxCreditController.onPageLoad(currentIndex, NormalMode)
    case (NormalMode, _, Some(years), _) if ((years.size - 1) > currentIndex) => routes.TaxYearLiabilitiesController.onPageLoad(currentIndex + 1, NormalMode)
    case (NormalMode, _, _, _) => routes.WhereDidTheUndeclaredIncomeOrGainIncludedController.onPageLoad(NormalMode)
    case (CheckMode, _, _, true) => routes.ForeignTaxCreditController.onPageLoad(currentIndex, CheckMode)
    case (CheckMode, _, _, _) => checkRouteMap(TaxYearLiabilitiesPage)(userAnswers)(hasAnswerChanged)
  }

}
