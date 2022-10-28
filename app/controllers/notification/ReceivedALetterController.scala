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

package controllers.notification

import controllers.actions._
import forms.ReceivedALetterFormProvider
import javax.inject.Inject
import models._
import navigation.NotificationNavigator
import pages.{ReceivedALetterPage, QuestionPage, LetterReferencePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.notification.ReceivedALetterView

import scala.concurrent.{ExecutionContext, Future}

class ReceivedALetterController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: NotificationNavigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: ReceivedALetterFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ReceivedALetterView
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(ReceivedALetterPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),

        value => { 

          var changedPages: List[QuestionPage[_]] = List()
          var hasChanged: Boolean = true

          request.userAnswers.get(ReceivedALetterPage) match {
            case Some(false) if value == true =>
              changedPages = removePages 
              hasChanged = true
            case Some(false) if value == false =>
              changedPages = Nil 
              hasChanged = false
            case Some(true) if value == true =>
              changedPages = Nil 
              hasChanged = false 
            case Some(true) if value == false =>
              changedPages = removePages 
              hasChanged = false 
            case _ =>
              changedPages = Nil 
              hasChanged = false 
          }

          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(ReceivedALetterPage, value))
            clearedAnswers <- Future.fromTry(updatedAnswers.remove(changedPages))
            _              <- sessionRepository.set(clearedAnswers)
          } yield Redirect(navigator.nextPage(ReceivedALetterPage, mode, clearedAnswers, hasChanged))
        }
      )
  }
    
  val removePages: List[QuestionPage[_]] = List(
    LetterReferencePage
  )

}
