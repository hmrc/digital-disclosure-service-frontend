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
import forms.RelatesToFormProvider
import javax.inject.Inject
import models._
import models.SubmissionType._
import navigation.NotificationNavigator
import pages._
import pages.notification.SectionPages
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.notification.RelatesToView

import scala.concurrent.{ExecutionContext, Future}

class RelatesToController @Inject()(
  override val messagesApi: MessagesApi,
  sessionService: SessionService,
  navigator: NotificationNavigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: RelatesToFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: RelatesToView
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with SectionPages {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(RelatesToPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, isDisclosure(request.userAnswers)))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, isDisclosure(request.userAnswers)))),

        value => {

          val changedPages = whatHasChanged(request.userAnswers, value)
          val hasChanged = changedPages.nonEmpty

          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(RelatesToPage, value))
            clearedAnswers <- Future.fromTry(updatedAnswers.remove(changedPages))
            _              <- sessionService.set(clearedAnswers)
          } yield Redirect(navigator.nextPage(RelatesToPage, mode, clearedAnswers, hasChanged))
        }
      )
  }

  def whatHasChanged(userAnswers: UserAnswers, value: RelatesTo): List[QuestionPage[_]] =
  userAnswers.get(RelatesToPage) match {
    case None => Nil
    case Some(relatesTo) if(value == relatesTo) => Nil
    case Some(_) if(value == RelatesTo.AnIndividual) => allEntityPages ::: aboutYouPages
    case Some(RelatesTo.AnIndividual) => allEntityPages ::: aboutYouPages
    case Some(relatesTo) => allEntityPages
  }

  def isDisclosure(userAnswers: UserAnswers): Boolean = userAnswers.submissionType == Disclosure
}
