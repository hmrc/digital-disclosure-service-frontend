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
import pages._
import models._
import controllers.otherLiabilities.routes

import org.scalacheck.Arbitrary.arbitrary

class OtherLiabilitiesNavigatorSpec extends SpecBase {

  val navigator = new OtherLiabilitiesNavigator

  "Other Liabilities Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {
        case object UnknownPage extends Page
        navigator.nextPage(
          UnknownPage,
          NormalMode,
          UserAnswers("id", "session-123")
        ) mustBe controllers.routes.IndexController.onPageLoad
      }

      "must go from the OtherLiabilityIssuesPage to the DescribeTheGiftController when InheritanceTaxIssues selected" in {
        val answer: Set[OtherLiabilityIssues] = Set(OtherLiabilityIssues.InheritanceTaxIssues)
        val userAnswers                       = for {
          ua        <- UserAnswers("id", "session-123").set(OtherLiabilityIssuesPage, answer)
          updatedUa <- ua.set(RelatesToPage, RelatesTo.AnIndividual)
        } yield updatedUa

        navigator.nextPage(
          OtherLiabilityIssuesPage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.DescribeTheGiftController.onPageLoad(NormalMode)
      }

      "must go from the OtherLiabilityIssuesPage to the WhatOtherLiabilityIssuesController when Other selected" in {
        val answer: Set[OtherLiabilityIssues] = Set(OtherLiabilityIssues.Other)
        val userAnswers                       = for {
          ua        <- UserAnswers("id", "session-123").set(OtherLiabilityIssuesPage, answer)
          updatedUa <- ua.set(RelatesToPage, RelatesTo.AnIndividual)
        } yield updatedUa

        navigator.nextPage(
          OtherLiabilityIssuesPage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.WhatOtherLiabilityIssuesController.onPageLoad(NormalMode)
      }

      "must go from the OtherLiabilityIssuesPage to the DidYouReceiveTaxCreditController when user an individual" in {
        val answer: Set[OtherLiabilityIssues] = Set(OtherLiabilityIssues.VatIssues)
        val userAnswers                       = for {
          ua        <- UserAnswers("id", "session-123").set(OtherLiabilityIssuesPage, answer)
          updatedUa <- ua.set(RelatesToPage, RelatesTo.AnIndividual)
        } yield updatedUa

        navigator.nextPage(
          OtherLiabilityIssuesPage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.DidYouReceiveTaxCreditController.onPageLoad(NormalMode)
      }

      "must go from the OtherLiabilityIssuesPage to the DidYouReceiveTaxCreditController when user an estate" in {
        val answer: Set[OtherLiabilityIssues] = Set(OtherLiabilityIssues.VatIssues)
        val userAnswers                       = for {
          ua        <- UserAnswers("id", "session-123").set(OtherLiabilityIssuesPage, answer)
          updatedUa <- ua.set(RelatesToPage, RelatesTo.AnEstate)
        } yield updatedUa

        navigator.nextPage(
          OtherLiabilityIssuesPage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.DidYouReceiveTaxCreditController.onPageLoad(NormalMode)
      }

      "must go from the OtherLiabilityIssuesPage to the CheckYourAnswersController when user is company/trust/llp" in {
        val answer: Set[OtherLiabilityIssues] = Set(OtherLiabilityIssues.VatIssues)
        val userAnswers                       = for {
          ua        <- UserAnswers("id", "session-123").set(OtherLiabilityIssuesPage, answer)
          updatedUa <- ua.set(RelatesToPage, RelatesTo.ACompany)
        } yield updatedUa

        navigator.nextPage(
          OtherLiabilityIssuesPage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the DescribeTheGiftPage to the DidYouReceiveTaxCreditController when user an individual" in {
        val answer: Set[OtherLiabilityIssues] = Set(OtherLiabilityIssues.VatIssues)
        val userAnswers                       = for {
          ua        <- UserAnswers("id", "session-123").set(OtherLiabilityIssuesPage, answer)
          updatedUa <- ua.set(RelatesToPage, RelatesTo.AnIndividual)
        } yield updatedUa

        navigator.nextPage(
          DescribeTheGiftPage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.DidYouReceiveTaxCreditController.onPageLoad(NormalMode)
      }

      "must go from the DescribeTheGiftPage to the DidYouReceiveTaxCreditController when user an estate" in {
        val answer: Set[OtherLiabilityIssues] = Set(OtherLiabilityIssues.VatIssues)
        val userAnswers                       = for {
          ua        <- UserAnswers("id", "session-123").set(OtherLiabilityIssuesPage, answer)
          updatedUa <- ua.set(RelatesToPage, RelatesTo.AnEstate)
        } yield updatedUa

        navigator.nextPage(
          DescribeTheGiftPage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.DidYouReceiveTaxCreditController.onPageLoad(NormalMode)
      }

      "must go from the DescribeTheGiftPage to the WhatOtherLiabilityIssuesController when selected Other" in {
        val answer: Set[OtherLiabilityIssues] = Set(OtherLiabilityIssues.Other)
        val userAnswers                       = for {
          ua        <- UserAnswers("id", "session-123").set(OtherLiabilityIssuesPage, answer)
          updatedUa <- ua.set(RelatesToPage, RelatesTo.ACompany)
        } yield updatedUa

        navigator.nextPage(
          DescribeTheGiftPage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.WhatOtherLiabilityIssuesController.onPageLoad(NormalMode)
      }

      "must go from the DescribeTheGiftPage to the CheckYourAnswersController when user is company/trust/llp" in {
        val answer: Set[OtherLiabilityIssues] = Set(OtherLiabilityIssues.VatIssues)
        val userAnswers                       = for {
          ua        <- UserAnswers("id", "session-123").set(OtherLiabilityIssuesPage, answer)
          updatedUa <- ua.set(RelatesToPage, RelatesTo.ACompany)
        } yield updatedUa

        navigator.nextPage(
          DescribeTheGiftPage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the WhatOtherLiabilityIssuesPage to the DidYouReceiveTaxCreditController when user an individual" in {
        val userAnswers = for {
          ua        <- UserAnswers("id", "session-123").set(WhatOtherLiabilityIssuesPage, "answer")
          updatedUa <- ua.set(RelatesToPage, RelatesTo.AnIndividual)
        } yield updatedUa

        navigator.nextPage(
          WhatOtherLiabilityIssuesPage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.DidYouReceiveTaxCreditController.onPageLoad(NormalMode)
      }

      "must go from the WhatOtherLiabilityIssuesPage to the DidYouReceiveTaxCreditController when user an estate" in {
        val userAnswers = for {
          ua        <- UserAnswers("id", "session-123").set(WhatOtherLiabilityIssuesPage, "answer")
          updatedUa <- ua.set(RelatesToPage, RelatesTo.AnEstate)
        } yield updatedUa

        navigator.nextPage(
          WhatOtherLiabilityIssuesPage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.DidYouReceiveTaxCreditController.onPageLoad(NormalMode)
      }

      "must go from the WhatOtherLiabilityIssuesPage to the CheckYourAnswersController when user is company/trust/llp" in {
        val userAnswers = for {
          ua        <- UserAnswers("id", "session-123").set(WhatOtherLiabilityIssuesPage, "answer")
          updatedUa <- ua.set(RelatesToPage, RelatesTo.ACompany)
        } yield updatedUa

        navigator.nextPage(
          WhatOtherLiabilityIssuesPage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the DidYouReceiveTaxCreditPage to the CheckYourAnswersController when selected Yes/No" in {
        val ua = UserAnswers("id", "session-123")
          .set(DidYouReceiveTaxCreditPage, arbitrary[Boolean].sample.value)
          .success
          .value
        navigator.nextPage(
          DidYouReceiveTaxCreditPage,
          NormalMode,
          ua
        ) mustBe routes.CheckYourAnswersController.onPageLoad
      }
    }

    "in Check mode" - {

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {
        case object UnknownPage extends Page
        navigator.nextPage(
          UnknownPage,
          CheckMode,
          UserAnswers("id", "session-123")
        ) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the OtherLiabilityIssuesPage to CheckYourAnswers when the answer preference has not changed" in {
        val answer: Set[OtherLiabilityIssues] = Set(OtherLiabilityIssues.InheritanceTaxIssues)
        val userAnswers                       = UserAnswers("id", "session-123").set(OtherLiabilityIssuesPage, answer).success.value
        navigator.nextPage(
          OtherLiabilityIssuesPage,
          CheckMode,
          userAnswers,
          false
        ) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the OtherLiabilityIssuesPage to DescribeTheGiftPage when the preference answer has changed and InheritanceTaxIssues is selected and it's not already set" in {
        val answer: Set[OtherLiabilityIssues] = Set(OtherLiabilityIssues.InheritanceTaxIssues)
        val userAnswers                       = UserAnswers("id", "session-123").set(OtherLiabilityIssuesPage, answer).success.value
        navigator.nextPage(OtherLiabilityIssuesPage, CheckMode, userAnswers) mustBe routes.DescribeTheGiftController
          .onPageLoad(CheckMode)
      }

      "must go from the OtherLiabilityIssuesPage to WhatOtherLiabilityIssuesPage when the preference answer has changed, Other is selected and it's not already set" in {
        val answer: Set[OtherLiabilityIssues] = Set(OtherLiabilityIssues.Other)
        val userAnswers                       = UserAnswers("id", "session-123").set(OtherLiabilityIssuesPage, answer).success.value
        navigator.nextPage(
          OtherLiabilityIssuesPage,
          CheckMode,
          userAnswers
        ) mustBe routes.WhatOtherLiabilityIssuesController.onPageLoad(CheckMode)
      }

      "must go from the DescribeTheGiftPage to WhatOtherLiabilityIssuesPage when the preference answers contains the Other and it's not already set" in {
        val answer: Set[OtherLiabilityIssues] = Set(OtherLiabilityIssues.Other)
        val userAnswers                       = UserAnswers("id", "session-123").set(OtherLiabilityIssuesPage, answer).success.value
        navigator.nextPage(DescribeTheGiftPage, CheckMode, userAnswers) mustBe routes.WhatOtherLiabilityIssuesController
          .onPageLoad(CheckMode)
      }
    }

  }

}
