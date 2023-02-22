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
import controllers.onshore.routes
import pages._
import models._
import models.WhyAreYouMakingThisOnshoreDisclosure._
import models.WhereDidTheUndeclaredIncomeOrGainIncluded._

@Singleton
class OnshoreNavigator @Inject()() {

  private val normalRoutes: Page => UserAnswers => Call = {

    case WhyAreYouMakingThisOnshoreDisclosurePage => ua => (ua.get(WhyAreYouMakingThisOnshoreDisclosurePage), ua.get(RelatesToPage)) match {
      case (Some(value), Some(entity)) if ( (entity != RelatesTo.AnEstate) && (
        value.contains(DeliberatelyDidNotNotify) ||
        value.contains(DeliberateInaccurateReturn) ||
        value.contains(DeliberatelyDidNotFile))
        ) => routes.CDFOnshoreController.onPageLoad(NormalMode)
      case (Some(value), _) if (value.contains(DidNotNotifyHasExcuse)) => routes.ReasonableExcuseOnshoreController.onPageLoad(NormalMode)
      case (Some(value), _) if (value.contains(InaccurateReturnWithCare)) => routes.ReasonableCareOnshoreController.onPageLoad(NormalMode)
      case (Some(value), _) if (value.contains(NotFileHasExcuse)) => routes.ReasonableExcuseForNotFilingOnshoreController.onPageLoad(NormalMode)
      case _ => routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
    }

    case CDFOnshorePage => ua => (ua.get(WhyAreYouMakingThisOnshoreDisclosurePage), ua.get(CDFOnshorePage)) match {
      case (_, Some(false)) => routes.YouHaveLeftTheDDSOnshoreController.onPageLoad(NormalMode)
      case (Some(value), Some(true)) if (value.contains(DidNotNotifyHasExcuse)) => routes.ReasonableExcuseOnshoreController.onPageLoad(NormalMode)
      case (Some(value), Some(true)) if (value.contains(InaccurateReturnWithCare)) => routes.ReasonableCareOnshoreController.onPageLoad(NormalMode)
      case (Some(value), Some(true)) if (value.contains(NotFileHasExcuse)) => routes.ReasonableExcuseForNotFilingOnshoreController.onPageLoad(NormalMode)
      case _ => routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
    }

    case ReasonableExcuseOnshorePage => ua => ua.get(WhyAreYouMakingThisOnshoreDisclosurePage) match {
      case Some(value) if (value.contains(InaccurateReturnWithCare)) => routes.ReasonableCareOnshoreController.onPageLoad(NormalMode)
      case Some(value) if (value.contains(NotFileHasExcuse)) => routes.ReasonableExcuseForNotFilingOnshoreController.onPageLoad(NormalMode)
      case _ => routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
    }

    case ReasonableCareOnshorePage => ua => ua.get(WhyAreYouMakingThisOnshoreDisclosurePage) match {
      case Some(value) if (value.contains(NotFileHasExcuse)) => routes.ReasonableExcuseForNotFilingOnshoreController.onPageLoad(NormalMode)
      case _ => routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
    }

    case ReasonableExcuseForNotFilingOnshorePage => _ => routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)

    case WhatOnshoreLiabilitiesDoYouNeedToDisclosePage => _ => routes.WhichOnshoreYearsController.onPageLoad(NormalMode)

    case WhichOnshoreYearsPage => ua => {
      val missingYearsCount = ua.inverselySortedOnshoreTaxYears.map(ty => OnshoreYearStarting.findMissingYears(ty.toList).size).getOrElse(0)
      (ua.get(WhichOnshoreYearsPage), missingYearsCount) match {
        case (Some(years), 0) if years.contains(PriorToThreeYears) => routes.TaxBeforeThreeYearsOnshoreController.onPageLoad(NormalMode)
        case (Some(years), 0) if years.contains(PriorToFiveYears) => routes.TaxBeforeFiveYearsOnshoreController.onPageLoad(NormalMode)
        case (Some(years), 0) if years.contains(PriorToNineteenYears) => routes.TaxBeforeNineteenYearsOnshoreController.onPageLoad(NormalMode)
        case (Some(_), 0) => routes.OnshoreTaxYearLiabilitiesController.onPageLoad(0, NormalMode)
        case (Some(_), 1) => routes.NotIncludedSingleTaxYearController.onPageLoad(NormalMode)
        case (Some(_), _) => routes.NotIncludedMultipleTaxYearsController.onPageLoad(NormalMode)
        case (_, _) => routes.OnshoreTaxYearLiabilitiesController.onPageLoad(0, NormalMode)
      }
    }

    case _ => _ => controllers.routes.IndexController.onPageLoad
  }

  private val checkRouteMap: Page => UserAnswers => Boolean => Call = {
    case _ => _ => _ => controllers.routes.IndexController.onPageLoad
  }

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers, hasAnswerChanged: Boolean = true): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)(hasAnswerChanged)
  }

  def nextTaxYearLiabilitiesPage(currentIndex: Int, deduction: Boolean, mode: Mode, userAnswers: UserAnswers, hasAnswerChanged: Boolean = false): Call =
    (mode, userAnswers.inverselySortedOnshoreTaxYears) match {
    case (NormalMode, _) if (deduction) => routes.ResidentialReductionController.onPageLoad(currentIndex, NormalMode)
    case (NormalMode, Some(years)) if ((years.size - 1) > currentIndex) => routes.OnshoreTaxYearLiabilitiesController.onPageLoad(currentIndex + 1, NormalMode)
    case (_, _) => routes.IncomeOrGainSourceController.onPageLoad(mode)
  }

}
