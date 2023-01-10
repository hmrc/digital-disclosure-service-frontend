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
import forms.WhatIsYourReasonableExcuseFormProvider
import javax.inject.Inject
import models.{Mode, UserAnswers, RelatesTo}
import navigation.OffshoreNavigator
import pages._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.offshore.WhatIsYourReasonableExcuseView

import scala.concurrent.{ExecutionContext, Future}

class WhatIsYourReasonableExcuseController @Inject()(
                                      override val messagesApi: MessagesApi,
                                      sessionService: SessionService,
                                      navigator: OffshoreNavigator,
                                      identify: IdentifierAction,
                                      getData: DataRetrievalAction,
                                      requireData: DataRequiredAction,
                                      formProvider: WhatIsYourReasonableExcuseFormProvider,
                                      val controllerComponents: MessagesControllerComponents,
                                      view: WhatIsYourReasonableExcuseView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val areTheyTheIndividual = isTheUserTheIndividual(request.userAnswers)
      val entity = request.userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      val preparedForm = request.userAnswers.get(WhatIsYourReasonableExcusePage) match {
        case None => form(areTheyTheIndividual)
        case Some(value) => form(areTheyTheIndividual).fill(value)
      }

      Ok(view(preparedForm, mode, areTheyTheIndividual, entity))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val areTheyTheIndividual = isTheUserTheIndividual(request.userAnswers)
      val entity = request.userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      form(areTheyTheIndividual).bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, areTheyTheIndividual, entity))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(WhatIsYourReasonableExcusePage, value))
            _              <- sessionService.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(WhatIsYourReasonableExcusePage, mode, updatedAnswers))
      )
  }

  def isTheUserTheIndividual(userAnswers: UserAnswers): Boolean = {
    userAnswers.get(AreYouTheIndividualPage) match {
      case Some(true) => true
      case _ => false
    }
  }

  def form(areTheyTheIndividual: Boolean) = formProvider(areTheyTheIndividual)
}
