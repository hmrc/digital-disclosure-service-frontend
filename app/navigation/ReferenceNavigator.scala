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

import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.{DoYouHaveACaseReferencePage, Page}
import play.api.mvc.Call
import controllers.reference.routes

import javax.inject.{Inject, Singleton}

@Singleton
class ReferenceNavigator @Inject()() {
  
  private val normalRoutes: Page => UserAnswers => Call = {

    case DoYouHaveACaseReferencePage => ua => ua.get(DoYouHaveACaseReferencePage) match {
      case Some(true) => routes.WhatIsTheCaseReferenceController.onPageLoad(NormalMode)
      case Some(false) => controllers.routes.TaskListController.onPageLoad
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
}
