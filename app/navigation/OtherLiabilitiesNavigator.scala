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
import pages._
import models.{UserAnswers, Mode, NormalMode, CheckMode, RelatesTo}
import models.OtherLiabilityIssues._

@Singleton
class OtherLiabilitiesNavigator @Inject()() {

  private val normalRoutes: Page => UserAnswers => Call = {  

    case OtherLiabilityIssuesPage => ua => (ua.get(OtherLiabilityIssuesPage), ua.get(RelatesToPage)) match {
      case (Some(value), _) if (value.contains(InheritanceTaxIssues)) => routes.DescribeTheGiftController.onPageLoad(NormalMode)
      case (Some(value), _) if (value.contains(Other)) => routes.WhatOtherLiabilityIssuesController.onPageLoad(NormalMode) 
      case (Some(_), relatesTo) if(relatesTo.contains(RelatesTo.AnIndividual) || relatesTo.contains(RelatesTo.AnEstate)) => routes.DidYouReceiveTaxCreditController.onPageLoad(NormalMode)
      case (Some(_), _) => routes.CheckYourAnswersController.onPageLoad
      case _ => routes.OtherLiabilityIssuesController.onPageLoad(NormalMode)
    }

    case DescribeTheGiftPage => ua => (ua.get(OtherLiabilityIssuesPage), ua.get(RelatesToPage)) match {
      case (Some(value), relatesTo) if(!value.contains(Other) && (relatesTo.contains(RelatesTo.AnIndividual) || relatesTo.contains(RelatesTo.AnEstate))) => routes.DidYouReceiveTaxCreditController.onPageLoad(NormalMode)
      case (Some(value), _) if (value.contains(Other)) => routes.WhatOtherLiabilityIssuesController.onPageLoad(NormalMode)
      case (Some(_), _) => routes.CheckYourAnswersController.onPageLoad
      case _ => routes.DescribeTheGiftController.onPageLoad(NormalMode)
    }

    case WhatOtherLiabilityIssuesPage => ua => (ua.get(WhatOtherLiabilityIssuesPage),  ua.get(RelatesToPage)) match {
      case (Some(value), relatesTo) if(relatesTo.contains(RelatesTo.AnIndividual) || relatesTo.contains(RelatesTo.AnEstate)) => routes.DidYouReceiveTaxCreditController.onPageLoad(NormalMode)
      case (Some(value), _) => routes.CheckYourAnswersController.onPageLoad
      case _ => routes.WhatOtherLiabilityIssuesController.onPageLoad(NormalMode)
    }

    case DidYouReceiveTaxCreditPage => _ => routes.CheckYourAnswersController.onPageLoad

    case _ => _ => controllers.routes.IndexController.onPageLoad
  }

  private val checkRouteMap: Page => UserAnswers => Boolean => Call = {

    case OtherLiabilityIssuesPage => ua => hasValueChanged => ua.get(OtherLiabilityIssuesPage) match {
      case Some(value) if hasValueChanged && value.contains(InheritanceTaxIssues) => routes.DescribeTheGiftController.onPageLoad(CheckMode)
      case Some(value) if hasValueChanged && value.contains(Other) => routes.WhatOtherLiabilityIssuesController.onPageLoad(CheckMode)
      case _ => routes.CheckYourAnswersController.onPageLoad  
    }

    case DescribeTheGiftPage => ua => hasValueChanged => ua.get(OtherLiabilityIssuesPage) match {
      case Some(value) if hasValueChanged && value.contains(Other) => routes.WhatOtherLiabilityIssuesController.onPageLoad(CheckMode)
      case _ => routes.CheckYourAnswersController.onPageLoad  
    }

    case _ => _ => _ => routes.CheckYourAnswersController.onPageLoad
  }

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers, hasAnswerChanged: Boolean = true): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)(hasAnswerChanged)
  }

}
