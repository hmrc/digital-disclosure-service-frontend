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

package controllers.notification

import controllers.actions._
import forms.ReceivedALetterFormProvider
import javax.inject.Inject
import models._

import navigation.NotificationNavigator
import pages._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.notification.ReceivedALetterView

import scala.concurrent.{ExecutionContext, Future}

class ReceivedALetterController @Inject() (
  override val messagesApi: MessagesApi,
  sessionService: SessionService,
  navigator: NotificationNavigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: ReceivedALetterFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ReceivedALetterView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.get(ReceivedALetterPage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, mode, request.userAnswers.isDisclosure))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode, request.userAnswers.isDisclosure))),
          value => {

            val (pagesToClear, hasValueChanged) = changedPages(request.userAnswers, value)

            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(ReceivedALetterPage, value))
              clearedAnswers <- Future.fromTry(updatedAnswers.remove(pagesToClear))
              _              <- sessionService.set(clearedAnswers)
            } yield Redirect(navigator.nextPage(ReceivedALetterPage, mode, clearedAnswers, hasValueChanged))
          }
        )
  }

  def changedPages(userAnswers: UserAnswers, value: Boolean): (List[QuestionPage[_]], Boolean) =
    userAnswers.get(ReceivedALetterPage) match {
      case Some(false) if value == true =>
        (Nil, true)
      case Some(true) if value == false =>
        (List(LetterReferencePage), false)
      case _                            =>
        (Nil, false)
    }

}
