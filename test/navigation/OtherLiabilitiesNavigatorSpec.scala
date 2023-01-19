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
import controllers.otherLiabilities.routes
import pages._
import models._

class OtherLiabilitiesNavigatorSpec extends SpecBase {

  val navigator = new OtherLiabilitiesNavigator

  "Other Liabilities Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, UserAnswers("id")) mustBe controllers.routes.IndexController.onPageLoad
      }
    }

    "in Check mode" - {

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, UserAnswers("id")) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the OtherLiabilityIssuesPage to CheckYourAnswers when the answer preference has not changed" in {
        val answer: Set[OtherLiabilityIssues] = Set(OtherLiabilityIssues.InheritanceTaxIssues)
        val userAnswers = UserAnswers("id").set(OtherLiabilityIssuesPage, answer).success.value
        navigator.nextPage(OtherLiabilityIssuesPage, CheckMode, userAnswers, false) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the OtherLiabilityIssuesPage to DescribeTheGiftPage when the preference answer has changed and InheritanceTaxIssues is selected and it's not already set" in {
        val answer: Set[OtherLiabilityIssues] = Set(OtherLiabilityIssues.InheritanceTaxIssues)
        val userAnswers = UserAnswers("id").set(OtherLiabilityIssuesPage, answer).success.value
        navigator.nextPage(OtherLiabilityIssuesPage, CheckMode, userAnswers) mustBe routes.DescribeTheGiftController.onPageLoad(CheckMode)
      }

      "must go from the OtherLiabilityIssuesPage to WhatOtherLiabilityIssuesPage when the preference answer has changed, Other is selected and it's not already set" in {
        val answer: Set[OtherLiabilityIssues] = Set(OtherLiabilityIssues.Other)
        val userAnswers = UserAnswers("id").set(OtherLiabilityIssuesPage, answer).success.value
        navigator.nextPage(OtherLiabilityIssuesPage, CheckMode, userAnswers) mustBe routes.WhatOtherLiabilityIssuesController.onPageLoad(CheckMode)
      }

      "must go from the DescribeTheGiftPage to WhatOtherLiabilityIssuesPage when the preference answers contains the Other and it's not already set" in {
        val answer: Set[OtherLiabilityIssues] = Set(OtherLiabilityIssues.Other)
        val userAnswers = UserAnswers("id").set(OtherLiabilityIssuesPage, answer).success.value
        navigator.nextPage(DescribeTheGiftPage, CheckMode, userAnswers) mustBe routes.WhatOtherLiabilityIssuesController.onPageLoad(CheckMode)
      }
    }
    
  }

}