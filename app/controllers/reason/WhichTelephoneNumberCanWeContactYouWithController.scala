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
import forms.WhichTelephoneNumberCanWeContactYouWithFormProvider

import javax.inject.Inject
import models.{Mode, UserAnswers, WhichTelephoneNumberCanWeContactYouWith}
import models.WhichTelephoneNumberCanWeContactYouWith.ExistingNumber
import navigation.ReasonNavigator
import pages.{WhatTelephoneNumberCanWeContactYouWithPage, WhichTelephoneNumberCanWeContactYouWithPage, YourPhoneNumberPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.reason.WhichTelephoneNumberCanWeContactYouWithView

import scala.concurrent.{ExecutionContext, Future}

class WhichTelephoneNumberCanWeContactYouWithController @Inject() (
  override val messagesApi: MessagesApi,
  sessionService: SessionService,
  navigator: ReasonNavigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: WhichTelephoneNumberCanWeContactYouWithFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: WhichTelephoneNumberCanWeContactYouWithView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.get(WhichTelephoneNumberCanWeContactYouWithPage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    request.userAnswers.get(YourPhoneNumberPage) match {
      case Some(telephoneNumber) => Ok(view(preparedForm, mode, telephoneNumber))
      case _                     => Redirect(routes.WhatTelephoneNumberCanWeContactYouWithController.onPageLoad(mode))
    }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors =>
            request.userAnswers.get(YourPhoneNumberPage) match {
              case Some(telephoneNumber) => Future.successful(BadRequest(view(formWithErrors, mode, telephoneNumber)))
              case _                     =>
                Future.successful(Redirect(routes.WhatTelephoneNumberCanWeContactYouWithController.onPageLoad(mode)))
            },
          value =>
            for {
              userAnswers    <- Future.fromTry(request.userAnswers.set(WhichTelephoneNumberCanWeContactYouWithPage, value))
              updatedAnswers <- setTelephone(userAnswers, value)
              _              <- sessionService.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(WhichTelephoneNumberCanWeContactYouWithPage, mode, updatedAnswers))
        )
  }

  private def setTelephone(
    userAnswers: UserAnswers,
    value: WhichTelephoneNumberCanWeContactYouWith
  ): Future[UserAnswers] =
    (value, userAnswers.get(YourPhoneNumberPage)) match {
      case (ExistingNumber, Some(email)) =>
        Future.fromTry(userAnswers.set(WhatTelephoneNumberCanWeContactYouWithPage, email))
      case _                             => Future(userAnswers)
    }
}
