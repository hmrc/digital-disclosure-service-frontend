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
import models.{UserAnswers, Mode, NormalMode, CheckMode}
import models.WhyAreYouMakingThisDisclosure._
import models.YourLegalInterpretation._

@Singleton
class OffshoreNavigator @Inject()() {

  private val normalRoutes: Page => UserAnswers => Call = {

    case WhyAreYouMakingThisDisclosurePage => ua => ua.get(WhyAreYouMakingThisDisclosurePage) match {
      case Some(value) if (
        value.contains(DeliberatelyDidNotNotify) ||
        value.contains(DeliberateInaccurateReturn) ||
        value.contains(DeliberatelyDidNotFile)
        ) => routes.ContractualDisclosureFacilityController.onPageLoad(NormalMode)  
      case Some(value) if (value.contains(DidNotNotifyHasExcuse)) => routes.WhatIsYourReasonableExcuseController.onPageLoad(NormalMode)
      case Some(value) if (value.contains(InaccurateReturnWithCare)) => routes.WhatReasonableCareDidYouTakeController.onPageLoad(NormalMode)
      case Some(value) if (value.contains(NotFileHasExcuse)) => routes.WhatIsYourReasonableExcuseForNotFilingReturnController.onPageLoad(NormalMode)
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

    case WhichYearsPage => _ => routes.TaxYearLiabilitiesController.onPageLoad(0, NormalMode)

    case YourLegalInterpretationPage => ua => ua.get(YourLegalInterpretationPage) match {
      case Some(value) if ((
          value.contains(YourResidenceStatus) ||
          value.contains(YourDomicileStatus) ||
          value.contains(TheRemittanceBasis) ||
          value.contains(HowIncomeArisingInATrust) ||
          value.contains(TheTransferOfAssets) ||
          value.contains(HowIncomeArisingInAnOffshore) ||
          value.contains(InheritanceTaxIssues) ||
          value.contains(WhetherIncomeShouldBeTaxed)) && 
          value.contains(AnotherIssue)
        ) => routes.UnderWhatConsiderationController.onPageLoad(NormalMode)
      case Some(value) if (
          value.contains(YourResidenceStatus) ||
          value.contains(YourDomicileStatus) ||
          value.contains(TheRemittanceBasis) ||
          value.contains(HowIncomeArisingInATrust) ||
          value.contains(TheTransferOfAssets) ||
          value.contains(HowIncomeArisingInAnOffshore) ||
          value.contains(InheritanceTaxIssues) ||
          value.contains(WhetherIncomeShouldBeTaxed)
        ) => routes.HowMuchTaxHasNotBeenIncludedController.onPageLoad(NormalMode)
      case Some(value) if(value.contains(AnotherIssue)) => routes.UnderWhatConsiderationController.onPageLoad(NormalMode)
      case Some(value) if(value.contains(NoExclusion)) => routes.TheMaximumValueOfAllAssetsController.onPageLoad(NormalMode)
    }  

    case UnderWhatConsiderationPage => _ => routes.HowMuchTaxHasNotBeenIncludedController.onPageLoad(NormalMode)

    case HowMuchTaxHasNotBeenIncludedPage => _ => routes.TheMaximumValueOfAllAssetsController.onPageLoad(NormalMode)

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

  def nextTaxYearLiabilitiesPage(currentIndex: Int, mode: Mode, userAnswers: UserAnswers): Call = (mode, userAnswers.inverselySortedOffshoreTaxYears) match {
    case (NormalMode, Some(years)) if ((years.size - 1) > currentIndex) => routes.TaxYearLiabilitiesController.onPageLoad(currentIndex + 1, NormalMode)
    case (NormalMode, _) => routes.YourLegalInterpretationController.onPageLoad(NormalMode)
    case (CheckMode, _) => checkRouteMap(TaxYearLiabilitiesPage)(userAnswers)(true)
  }

}
