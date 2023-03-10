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
import controllers.letting.routes
import pages._
import models._

@Singleton
class LettingNavigator @Inject()() {

  private val normalRoutes: Page => Int => UserAnswers => Call = {
    case RentalAddressLookupPage => i => _ => routes.PropertyFirstLetOutController.onPageLoad(i, NormalMode)
    case PropertyFirstLetOutPage => i => _ =>  routes.PropertyStoppedBeingLetOutController.onPageLoad(i, NormalMode)

    case PropertyStoppedBeingLetOutPage => i => ua => ua.getBySeqIndex(LettingPropertyPage, i).flatMap(_.stoppedBeingLetOut) match {
      case Some(true) => routes.PropertyIsNoLongerBeingLetOutController.onPageLoad(i, NormalMode)
      case _ => routes.WasPropertyFurnishedController.onPageLoad(i, NormalMode)
    }

    case PropertyIsNoLongerBeingLetOutPage => i => _ => routes.WasPropertyFurnishedController.onPageLoad(i, NormalMode)

    case WasPropertyFurnishedPage => i => ua => ua.getBySeqIndex(LettingPropertyPage, i).flatMap(_.wasFurnished) match {
      case Some(true) => routes.FHLController.onPageLoad(i, NormalMode)
      case _ => routes.JointlyOwnedPropertyController.onPageLoad(i, NormalMode)
    }

    case FHLPage => i => _ => routes.JointlyOwnedPropertyController.onPageLoad(i, NormalMode)

    case JointlyOwnedPropertyPage => i => ua => ua.getBySeqIndex(LettingPropertyPage, i).flatMap(_.isJointOwnership) match {
      case Some(true) => routes.WhatWasThePercentageIncomeYouReceivedFromPropertyController.onPageLoad(i, NormalMode)
      case _ => routes.DidYouHaveAMortgageOnPropertyController.onPageLoad(i, NormalMode)
    }

    case WhatWasThePercentageIncomeYouReceivedFromPropertyPage => i => _ => routes.DidYouHaveAMortgageOnPropertyController.onPageLoad(i, NormalMode)

    case DidYouHaveAMortgageOnPropertyPage => i => ua => ua.getBySeqIndex(LettingPropertyPage, i).flatMap(_.isMortgageOnProperty) match {
      case Some(true) => routes.WhatTypeOfMortgageDidYouHaveController.onPageLoad(i, NormalMode)
      case _ => routes.WasALettingAgentUsedToManagePropertyController.onPageLoad(i, NormalMode)
    }

    case WhatTypeOfMortgageDidYouHavePage => i => ua => ua.getBySeqIndex(LettingPropertyPage, i).flatMap(_.typeOfMortgage) match {
      case Some(TypeOfMortgageDidYouHave.Other) => routes.WhatWasTheTypeOfMortgageController.onPageLoad(i, NormalMode)
      case _ => routes.WasALettingAgentUsedToManagePropertyController.onPageLoad(i, NormalMode)
    }

    case WhatWasTheTypeOfMortgagePage => i => _ => routes.WasALettingAgentUsedToManagePropertyController.onPageLoad(i, NormalMode)

    case WasALettingAgentUsedToManagePropertyPage => i => ua => ua.getBySeqIndex(LettingPropertyPage, i).flatMap(_.wasPropertyManagerByAgent) match {
      case Some(true) => routes.DidTheLettingAgentCollectRentOnYourBehalfController.onPageLoad(i, NormalMode)
      case _ => routes.CheckYourAnswersController.onPageLoad(i, NormalMode)
    }

    case DidTheLettingAgentCollectRentOnYourBehalfPage => i =>  _ => routes.CheckYourAnswersController.onPageLoad(i, NormalMode)

    case _ => _ => _ => controllers.routes.IndexController.onPageLoad
  }

  private val checkRouteMap: Page => Int => UserAnswers => Boolean => Call = {

    case JointlyOwnedPropertyPage => i => ua => hasChanged =>
      if(hasChanged) routes.WhatWasThePercentageIncomeYouReceivedFromPropertyController.onPageLoad(i, CheckMode)
      else routes.CheckYourAnswersController.onPageLoad(i, CheckMode)

    case WasPropertyFurnishedPage => i => ua => hasChanged =>
      if(hasChanged) routes.FHLController.onPageLoad(i, CheckMode)
      else routes.CheckYourAnswersController.onPageLoad(i, CheckMode) 

    case PropertyStoppedBeingLetOutPage => i => ua => hasChanged =>
      if(hasChanged) routes.PropertyIsNoLongerBeingLetOutController.onPageLoad(i, CheckMode)
      else routes.CheckYourAnswersController.onPageLoad(i, CheckMode)

    case _ => i => _ => _ => routes.CheckYourAnswersController.onPageLoad(i, CheckMode)
  }

  def nextPage(page: Page, i: Int, mode: Mode, userAnswers: UserAnswers, hasAnswerChanged: Boolean = true): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(i)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(i)(userAnswers)(hasAnswerChanged)
  }

}
