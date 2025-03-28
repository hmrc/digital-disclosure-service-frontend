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
import models.requests.DataRequest
import forms.OffshoreLiabilitiesFormProvider
import javax.inject.Inject
import models.{Mode, UserAnswers}
import models.store.disclosure._
import models.store.{FullDisclosure, Notification}

import navigation.NotificationNavigator
import pages.OffshoreLiabilitiesPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{DisclosureToUAService, SessionService, UAToSubmissionService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.notification.OffshoreLiabilitiesView
import scala.util.{Success, Try}

import scala.concurrent.{ExecutionContext, Future}

class OffshoreLiabilitiesController @Inject() (
  override val messagesApi: MessagesApi,
  sessionService: SessionService,
  navigator: NotificationNavigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: OffshoreLiabilitiesFormProvider,
  uaToSubmissionService: UAToSubmissionService,
  disclosureService: DisclosureToUAService,
  val controllerComponents: MessagesControllerComponents,
  view: OffshoreLiabilitiesView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.get(OffshoreLiabilitiesPage) match {
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
          value =>
            for {
              clearedAnswers <- Future.fromTry(clearOffshoreLiabilities(request.userAnswers, value))
              updatedAnswers <- Future.fromTry(clearedAnswers.set(OffshoreLiabilitiesPage, value))
              _              <- sessionService.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(OffshoreLiabilitiesPage, mode, updatedAnswers))
        )
  }

  def clearOffshoreLiabilities(userAnswers: UserAnswers, value: Boolean)(implicit
    request: DataRequest[_]
  ): Try[UserAnswers] =
    userAnswers.get(OffshoreLiabilitiesPage) match {
      case Some(true) if value == false =>
        val submission = uaToSubmissionService.uaToSubmission(userAnswers)
        submission match {
          case disclosure: FullDisclosure =>
            val updatedDisclosure = disclosure.copy(offshoreLiabilities = OffshoreLiabilities())
            disclosureService.fullDisclosureToUa(request.sessionId, updatedDisclosure)
          case _: Notification            =>
            Success(userAnswers)
        }
      case _                            => Success(userAnswers)
    }

}
