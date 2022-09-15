/*
 * Copyright 2022 HM Revenue & Customs
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
import controllers.notification.routes
import pages._
import models._

@Singleton
class NotificationNavigator @Inject()() {

  private val normalRoutes: Page => UserAnswers => Call = {

    case ReceivedALetterPage => ua => ua.get(ReceivedALetterPage) match {
      case Some(true) => routes.LetterReferenceController.onPageLoad(NormalMode)
      case Some(false) => routes.RelatesToController.onPageLoad(NormalMode)
      case None => routes.ReceivedALetterController.onPageLoad(NormalMode)
    }

    case LetterReferencePage => _ => routes.RelatesToController.onPageLoad(NormalMode)

    case RelatesToPage => _ => routes.AreYouTheIndividualController.onPageLoad(NormalMode)

    case AreYouTheIndividualPage => _ => routes.OffshoreLiabilitiesController.onPageLoad(NormalMode)

    case OffshoreLiabilitiesPage => ua => ua.get(OffshoreLiabilitiesPage) match {
      case Some(OffshoreLiabilities.Option1) => routes.OnshoreLiabilitiesController.onPageLoad(NormalMode)
      case Some(OffshoreLiabilities.Option2) => routes.OnlyOnshoreLiabilitiesController.onPageLoad
      case None => routes.OffshoreLiabilitiesController.onPageLoad(NormalMode)
    }

    case _ => _ => controllers.routes.IndexController.onPageLoad
  }

  private val checkRouteMap: Page => UserAnswers => Call = {
    case _ => _ => controllers.routes.CheckYourAnswersController.onPageLoad
  }

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }
}
