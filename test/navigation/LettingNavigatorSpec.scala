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
import models.{LettingProperty, NoLongerBeingLetOut, NormalMode, CheckMode, TypeOfMortgageDidYouHave, UserAnswers}
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

      "must go from DidYouHaveAMortgageOnPropertyPage to WhatTypeOfMortgageDidYouHaveController when Yes is selected" in {
        val index = 0
        val lettingProperty = LettingProperty(isMortgageOnProperty = Some(true))
        val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
        navigator.nextPage(DidYouHaveAMortgageOnPropertyPage, index, NormalMode, ua) mustBe routes.WhatTypeOfMortgageDidYouHaveController.onPageLoad(index, NormalMode)
      }

      "must go from DidYouHaveAMortgageOnPropertyPage to WasALettingAgentUsedToManagePropertyController when No is selected" in {
        val index = 0
        val lettingProperty = LettingProperty(isMortgageOnProperty = Some(false))
        val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
        navigator.nextPage(DidYouHaveAMortgageOnPropertyPage, index, NormalMode, ua) mustBe routes.WasALettingAgentUsedToManagePropertyController.onPageLoad(index, NormalMode)
      }

      "must go from WhatTypeOfMortgageDidYouHavePage to WasALettingAgentUsedToManagePropertyController when CapitalRepayment is selected" in {
        val index = 0
        val lettingProperty = LettingProperty(typeOfMortgage = Some(TypeOfMortgageDidYouHave.CapitalRepayment))
        val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
        navigator.nextPage(WhatTypeOfMortgageDidYouHavePage, index, NormalMode, ua) mustBe routes.WasALettingAgentUsedToManagePropertyController.onPageLoad(index, NormalMode)
      }

      "must go from WhatTypeOfMortgageDidYouHavePage to DidTheLettingAgentCollectRentOnYourBehalfController when InterestOnly is selected" in {
        val index = 0
        val lettingProperty = LettingProperty(typeOfMortgage = Some(TypeOfMortgageDidYouHave.InterestOnly))
        val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
        navigator.nextPage(WhatTypeOfMortgageDidYouHavePage, index, NormalMode, ua) mustBe routes.WasALettingAgentUsedToManagePropertyController.onPageLoad(index, NormalMode)
      }

      "must go from WhatTypeOfMortgageDidYouHavePage to WhatWasTheTypeOfMortgageController when Other is selected" in {
        val index = 0
        val lettingProperty = LettingProperty(typeOfMortgage = Some(TypeOfMortgageDidYouHave.Other))
        val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
        navigator.nextPage(WhatTypeOfMortgageDidYouHavePage, index, NormalMode, ua) mustBe routes.WhatWasTheTypeOfMortgageController.onPageLoad(index, NormalMode)
      }

      "must go from WhatWasTheTypeOfMortgagePage to DidTheLettingAgentCollectRentOnYourBehalfController" in {
        val index = 0
        val lettingProperty = LettingProperty(otherTypeOfMortgage = Some("type"))
        val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
        navigator.nextPage(WhatWasTheTypeOfMortgagePage, index, NormalMode, ua) mustBe routes.WasALettingAgentUsedToManagePropertyController.onPageLoad(index, NormalMode)
      }

      "must go from WasALettingAgentUsedToManagePropertyPage to CheckYourAnswersController if selected yes" in {
        val index = 0
        val lettingProperty = LettingProperty(wasPropertyManagerByAgent = Some(true))
        val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
        navigator.nextPage(WasALettingAgentUsedToManagePropertyPage, index, NormalMode, ua) mustBe routes.DidTheLettingAgentCollectRentOnYourBehalfController.onPageLoad(index, NormalMode)
      }

      "must go from WasALettingAgentUsedToManagePropertyPage to CheckYourAnswersController if selected no" in {
        val index = 0
        val lettingProperty = LettingProperty(wasPropertyManagerByAgent = Some(false))
        val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
        navigator.nextPage(WasALettingAgentUsedToManagePropertyPage, index, NormalMode, ua) mustBe routes.CheckYourAnswersController.onPageLoad(index, NormalMode)
      }

      "must go from DidTheLettingAgentCollectRentOnYourBehalfPage to CheckYourAnswersController if selected yes" in {
        val index = 0
        val lettingProperty = LettingProperty(didTheLettingAgentCollectRentOnYourBehalf = Some(true))
        val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
        navigator.nextPage(DidTheLettingAgentCollectRentOnYourBehalfPage, index, NormalMode, ua) mustBe routes.CheckYourAnswersController.onPageLoad(index, NormalMode)
      }

      "must go from DidTheLettingAgentCollectRentOnYourBehalfPage to CheckYourAnswersController if selected no" in {
        val index = 0
        val lettingProperty = LettingProperty(didTheLettingAgentCollectRentOnYourBehalf = Some(false))
        val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
        navigator.nextPage(DidTheLettingAgentCollectRentOnYourBehalfPage, index, NormalMode, ua) mustBe routes.CheckYourAnswersController.onPageLoad(index, NormalMode)
      }

    }
  }

  "in Check mode" - {

    "must go from JointlyOwnedPropertyPage to WhatWasThePercentageIncomeYouReceivedFromPropertyController if answer changed to Yes" in {
      val index = 0
      val lettingProperty = LettingProperty(isJointOwnership = Some(true))
      val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
      navigator.nextPage(JointlyOwnedPropertyPage, index, CheckMode, ua, true) mustBe routes.WhatWasThePercentageIncomeYouReceivedFromPropertyController.onPageLoad(index, CheckMode)
    }

    "must go from JointlyOwnedPropertyPage to CheckYourAnswersController if answer changed to No" in {
      val index = 0
      val lettingProperty = LettingProperty(isJointOwnership = Some(false))
      val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
      navigator.nextPage(JointlyOwnedPropertyPage, index, CheckMode, ua, false) mustBe routes.CheckYourAnswersController.onPageLoad(index, CheckMode)
    }

    "must go from WasPropertyFurnishedPage to FHLController if answer changed to Yes" in {
      val index = 0
      val lettingProperty = LettingProperty(wasFurnished = Some(true))
      val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
      navigator.nextPage(WasPropertyFurnishedPage, index, CheckMode, ua, true) mustBe routes.FHLController.onPageLoad(index, CheckMode)
    }

    "must go from WasPropertyFurnishedPage to CheckYourAnswersController if answer changed to No" in {
      val index = 0
      val lettingProperty = LettingProperty(wasFurnished = Some(false))
      val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
      navigator.nextPage(WasPropertyFurnishedPage, index, CheckMode, ua, false) mustBe routes.CheckYourAnswersController.onPageLoad(index, CheckMode)
    }

    "must go from PropertyStoppedBeingLetOutPage to PropertyIsNoLongerBeingLetOutController if answer changed to Yes" in {
      val index = 0
      val lettingProperty = LettingProperty(stoppedBeingLetOut = Some(true))
      val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
      navigator.nextPage(PropertyStoppedBeingLetOutPage, index, CheckMode, ua, true) mustBe routes.PropertyIsNoLongerBeingLetOutController.onPageLoad(index, CheckMode)
    }

    "must go from PropertyStoppedBeingLetOutPage to CheckYourAnswersController if answer changed to No" in {
      val index = 0
      val lettingProperty = LettingProperty(stoppedBeingLetOut = Some(false))
      val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
      navigator.nextPage(PropertyStoppedBeingLetOutPage, index, CheckMode, ua, false) mustBe routes.CheckYourAnswersController.onPageLoad(index, CheckMode)
    }

    "must go from DidYouHaveAMortgageOnPropertyPage to WhatTypeOfMortgageDidYouHaveController if answer changed to Yes" in {
      val index = 0
      val lettingProperty = LettingProperty(isMortgageOnProperty = Some(true))
      val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
      navigator.nextPage(DidYouHaveAMortgageOnPropertyPage, index, CheckMode, ua, true) mustBe routes.WhatTypeOfMortgageDidYouHaveController.onPageLoad(index, CheckMode)
    }

    "must go from DidYouHaveAMortgageOnPropertyPage to CheckYourAnswersController if answer changed to No" in {
      val index = 0
      val lettingProperty = LettingProperty(isMortgageOnProperty = Some(false))
      val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
      navigator.nextPage(DidYouHaveAMortgageOnPropertyPage, index, CheckMode, ua, false) mustBe routes.CheckYourAnswersController.onPageLoad(index, CheckMode)
    }

    "must go from WhatTypeOfMortgageDidYouHavePage to WhatWasTheTypeOfMortgageController if answer changed to Yes" in {
      val index = 0
      val lettingProperty = LettingProperty(typeOfMortgage = Some(TypeOfMortgageDidYouHave.Other))
      val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
      navigator.nextPage(WhatTypeOfMortgageDidYouHavePage, index, CheckMode, ua, true) mustBe routes.WhatWasTheTypeOfMortgageController.onPageLoad(index, CheckMode)
    }

    "must go from WhatTypeOfMortgageDidYouHavePage to CheckYourAnswersController if answer changed to No" in {
      val index = 0
      val lettingProperty = LettingProperty(typeOfMortgage = Some(TypeOfMortgageDidYouHave.CapitalRepayment))
      val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
      navigator.nextPage(WhatTypeOfMortgageDidYouHavePage, index, CheckMode, ua, false) mustBe routes.CheckYourAnswersController.onPageLoad(index, CheckMode)
    }

    "must go from WasALettingAgentUsedToManagePropertyPage to DidTheLettingAgentCollectRentOnYourBehalfController if answer changed to Yes" in {
      val index = 0
      val lettingProperty = LettingProperty(wasPropertyManagerByAgent = Some(true))
      val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
      navigator.nextPage(WasALettingAgentUsedToManagePropertyPage, index, CheckMode, ua, true) mustBe routes.DidTheLettingAgentCollectRentOnYourBehalfController.onPageLoad(index, CheckMode)
    }

    "must go from WasALettingAgentUsedToManagePropertyPage to CheckYourAnswersController if answer changed to No" in {
      val index = 0
      val lettingProperty = LettingProperty(wasPropertyManagerByAgent = Some(false))
      val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value
      navigator.nextPage(WasALettingAgentUsedToManagePropertyPage, index, CheckMode, ua, false) mustBe routes.CheckYourAnswersController.onPageLoad(index, CheckMode)
    }

  }

}
