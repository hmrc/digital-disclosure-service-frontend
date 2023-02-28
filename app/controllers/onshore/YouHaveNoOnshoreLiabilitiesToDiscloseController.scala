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

package controllers.onshore

import controllers.actions._
import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.onshore.YouHaveNoOnshoreLiabilitiesToDiscloseView
import models.{UserAnswers, RelatesTo}
import models.WhyAreYouMakingThisOnshoreDisclosure._
import pages.{AreYouTheIndividualPage, RelatesToPage, WhyAreYouMakingThisOnshoreDisclosurePage}

class YouHaveNoOnshoreLiabilitiesToDiscloseController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: YouHaveNoOnshoreLiabilitiesToDiscloseView
                                     ) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      
      val areTheyTheIndividual = isTheUserTheIndividual(request.userAnswers)
      val entity = request.userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)
      val years = numberOfYears(request.userAnswers)

      Ok(view(areTheyTheIndividual, entity, years))
  }

  def numberOfYears(ua: UserAnswers): Int = {
    ua.get(WhyAreYouMakingThisOnshoreDisclosurePage) match {
      case Some(value) => 
        if (value.contains(DidNotNotifyNoExcuse) || value.contains(DeliberatelyDidNotNotify) || value.contains(DeliberateInaccurateReturn) || value.contains(DeliberatelyDidNotFile)) 20
        else if (value.contains(InaccurateReturnNoCare)) 6
        else 4
      case None => 4    
    }
  }

  def isTheUserTheIndividual(userAnswers: UserAnswers): Boolean = {
    userAnswers.get(AreYouTheIndividualPage) match {
      case Some(true) => true
      case _ => false
    }
  }
}
