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

package controllers.offshore

import controllers.actions._
import models.WhyAreYouMakingThisDisclosure._
import models.{RelatesTo, UserAnswers}
import pages.{AreYouTheIndividualPage, RelatesToPage, WhyAreYouMakingThisDisclosurePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.offshore.YouHaveNoOffshoreLiabilitiesView

import javax.inject.Inject

class YouHaveNoOffshoreLiabilitiesController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: YouHaveNoOffshoreLiabilitiesView
                                     ) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request => {

      val entity = request.userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)
      val isIndividual = request.userAnswers.get(AreYouTheIndividualPage).getOrElse(false)
      val entityString = (entity, isIndividual) match {
        case (RelatesTo.AnIndividual, true) => "iAmIndividual"
        case (RelatesTo.AnIndividual, false) => "iAmNotIndividual"
        case (RelatesTo.ALimitedLiabilityPartnership, _ ) => "llp"
        case _ => "other"
      }
      val numberOfYears = getNumberOfYears(request.userAnswers)

      Ok(view(entityString, entity, numberOfYears))
    }
  }

  def getNumberOfYears(userAnswers: UserAnswers): Int = {
    userAnswers.get(WhyAreYouMakingThisDisclosurePage) match {
      case Some(value) if value.intersect(Set(
        DidNotNotifyHasExcuse,
        DeliberatelyDidNotNotify,
        DeliberateInaccurateReturn,
        DeliberatelyDidNotFile)).nonEmpty => 20
      case Some(value) if value.contains(InaccurateReturnNoCare) => 8
      case _ => 6
    }
  }
}
