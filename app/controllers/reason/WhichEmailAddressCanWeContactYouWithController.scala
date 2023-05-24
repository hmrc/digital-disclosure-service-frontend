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

package controllers.reason

import controllers.actions._
import forms.WhichEmailAddressCanWeContactYouWithFormProvider
import models.WhichEmailAddressCanWeContactYouWith.ExistingEmail

import javax.inject.Inject
import models.{Mode, UserAnswers, WhichEmailAddressCanWeContactYouWith}
import navigation.ReasonNavigator
import pages.{WhatEmailAddressCanWeContactYouWithPage, WhichEmailAddressCanWeContactYouWithPage, YourEmailAddressPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.reason.WhichEmailAddressCanWeContactYouWithView

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class WhichEmailAddressCanWeContactYouWithController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionService: SessionService,
                                       navigator: ReasonNavigator,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       formProvider: WhichEmailAddressCanWeContactYouWithFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: WhichEmailAddressCanWeContactYouWithView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(WhichEmailAddressCanWeContactYouWithPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      request.userAnswers.get(YourEmailAddressPage) match {
        case Some(email) => Ok(view(preparedForm, mode, email))
        case _ => Redirect(routes.WhatEmailAddressCanWeContactYouWithController.onPageLoad(mode))
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          request.userAnswers.get(YourEmailAddressPage) match {
            case Some(email) => Future.successful(BadRequest(view(formWithErrors, mode, email)))
            case _ => Future.successful(Redirect(routes.WhichEmailAddressCanWeContactYouWithController.onPageLoad(mode)))
          },

        value =>
          for {
            userAnswers    <- Future.fromTry(request.userAnswers.set(WhichEmailAddressCanWeContactYouWithPage, value))
            updatedAnswers <- setEmail(userAnswers, value)
            _              <- sessionService.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(WhichEmailAddressCanWeContactYouWithPage, mode, updatedAnswers))
      )
  }

  private def setEmail(userAnswers:UserAnswers, value:WhichEmailAddressCanWeContactYouWith): Future[UserAnswers] = {
    (value, userAnswers.get(YourEmailAddressPage)) match {
      case (ExistingEmail, Some(email)) => Future.fromTry(userAnswers.set(WhatEmailAddressCanWeContactYouWithPage, email))
      case _ => Future(userAnswers)
    }
  }
}
