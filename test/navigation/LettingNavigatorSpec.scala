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
import models.{LettingProperty, NoLongerBeingLetOut, NormalMode, UserAnswers}
import pages._
import controllers.letting.routes
import org.scalacheck.Arbitrary.arbitrary
import java.time.LocalDate

class LettingNavigatorSpec extends SpecBase {

  val navigator = new LettingNavigator

  "LettingNavitator" - {

    "In normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, 0, NormalMode, UserAnswers("id")) mustBe controllers.routes.IndexController.onPageLoad
      }

      "must go from RentalAddressLookupPage to PropertyFirstLetOutController" in {
        val index = 0
        val lettingProperty = LettingProperty(dateFirstLetOut = Some(LocalDate.now().minusDays(1)))
        val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value

        navigator.nextPage(RentalAddressLookupPage, index, NormalMode,ua) mustBe routes.PropertyFirstLetOutController.onPageLoad(index, NormalMode)
      }

      "must go from PropertyFirstLetOutPage to PropertyStoppedBeingLetOutController" in {
        val index = 0
        val lettingProperty = LettingProperty(stoppedBeingLetOut = Some(true))
        val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value

        navigator.nextPage(PropertyFirstLetOutPage, index, NormalMode, ua) mustBe routes.PropertyStoppedBeingLetOutController.onPageLoad(index, NormalMode)
      }

      "must go from PropertyStoppedBeingLetOutPage to PropertyIsNoLongerBeingLetOutController" in {
        val index = 0
        val lettingProperty = LettingProperty(stoppedBeingLetOut = Some(true))
        val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
        navigator.nextPage(PropertyStoppedBeingLetOutPage, index, NormalMode, ua) mustBe routes.PropertyIsNoLongerBeingLetOutController.onPageLoad(index, NormalMode)
      }

      "must go from PropertyStoppedBeingLetOutPage to WasPropertyFurnishedController" in {
        val index = 0
        val lettingProperty = LettingProperty(stoppedBeingLetOut = Some(false))
        val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
        navigator.nextPage(PropertyStoppedBeingLetOutPage, index, NormalMode, ua) mustBe routes.WasPropertyFurnishedController.onPageLoad(index, NormalMode)
      }

      "must go from PropertyIsNoLongerBeingLetOutPage to WasPropertyFurnishedController" in {
        val index = 0
        val lettingProperty = LettingProperty(noLongerBeingLetOut = Some(NoLongerBeingLetOut(LocalDate.now().minusDays(1), "some value")))
        val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
        navigator.nextPage(PropertyIsNoLongerBeingLetOutPage, index, NormalMode, ua) mustBe routes.WasPropertyFurnishedController.onPageLoad(index, NormalMode)
      }

      "must go from WasPropertyFurnishedPage to FHLController" in {
        val index = 0
        val lettingProperty = LettingProperty(wasFurnished = Some(true))
        val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
        navigator.nextPage(WasPropertyFurnishedPage, index, NormalMode, ua) mustBe routes.FHLController.onPageLoad(index, NormalMode)
      }

      "must go from WasPropertyFurnishedPage to JointlyOwnedPropertyController" in {
        val index = 0
        val lettingProperty = LettingProperty(wasFurnished = Some(false))
        val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
        navigator.nextPage(WasPropertyFurnishedPage, index, NormalMode, ua) mustBe routes.JointlyOwnedPropertyController.onPageLoad(index, NormalMode)
      }

      "must go from FHLPage to JointlyOwnedPropertyController" in {
        val index = 0
        val lettingProperty = LettingProperty(fhl = Some(arbitrary[Boolean].sample.value))
        val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
        navigator.nextPage(FHLPage, index, NormalMode, ua) mustBe routes.JointlyOwnedPropertyController.onPageLoad(index, NormalMode)
      }

      "must go from JointlyOwnedPropertyPage to WhatWasThePercentageIncomeYouReceivedFromPropertyController" in {
        val index = 0
        val lettingProperty = LettingProperty(isJointOwnership = Some(true))
        val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
        navigator.nextPage(JointlyOwnedPropertyPage, index, NormalMode, ua) mustBe routes.WhatWasThePercentageIncomeYouReceivedFromPropertyController.onPageLoad(index, NormalMode)
      }

      "must go from JointlyOwnedPropertyPage to DidYouHaveAMortgageOnPropertyController" in {
        val index = 0
        val lettingProperty = LettingProperty(isJointOwnership = Some(false))
        val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
        navigator.nextPage(JointlyOwnedPropertyPage, index, NormalMode, ua) mustBe routes.DidYouHaveAMortgageOnPropertyController.onPageLoad(index, NormalMode)
      }

      "must go from WhatWasThePercentageIncomeYouReceivedFromPropertyPage to DidYouHaveAMortgageOnPropertyController" in {
        val index = 0
        val lettingProperty = LettingProperty(percentageIncomeOnProperty = Some(5))
        val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
        navigator.nextPage(WhatWasThePercentageIncomeYouReceivedFromPropertyPage, index, NormalMode, ua) mustBe routes.DidYouHaveAMortgageOnPropertyController.onPageLoad(index, NormalMode)
      }

    }
  }

}
