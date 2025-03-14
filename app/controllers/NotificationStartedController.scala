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

package controllers

import controllers.actions._
import forms.NotificationStartedFormProvider
import javax.inject.Inject
import navigation.Navigator
import pages.NotificationStartedPage
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.NotificationStartedView
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.ZoneOffset
import models.{NotificationStarted, SubmissionType}

import scala.concurrent.{ExecutionContext, Future}

class NotificationStartedController @Inject() (
  override val messagesApi: MessagesApi,
  sessionService: SessionService,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: NotificationStartedFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: NotificationStartedView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val messages: Messages = messagesApi.preferred(request)
    val date               = request.userAnswers.lastUpdated.atZone(ZoneOffset.UTC).toLocalDate()
    val dateFormatter      = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale(messages.lang.code))
    val formattedDate      = date.format(dateFormatter)
    Ok(view(form, formattedDate))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        formWithErrors => {
          val date               = request.userAnswers.lastUpdated.atZone(ZoneOffset.UTC).toLocalDate()
          val messages: Messages = messagesApi.preferred(request)
          val dateFormatter      = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale(messages.lang.code))
          val formattedDate      = date.format(dateFormatter)
          Future.successful(BadRequest(view(formWithErrors, formattedDate)))
        },
        value => {
          val submissionType = value match {
            case NotificationStarted.Continue   => SubmissionType.Notification
            case NotificationStarted.Disclosure => SubmissionType.Disclosure
          }
          val updatedAnswers = request.userAnswers.copy(submissionType = submissionType)
          for {
            _ <- sessionService.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(NotificationStartedPage, updatedAnswers))
        }
      )
  }

}
