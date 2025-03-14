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

package controllers.letting

import controllers.actions._
import forms.PropertyStoppedBeingLetOutFormProvider

import javax.inject.Inject
import models.{LettingProperty, Mode, UserAnswers}
import navigation.LettingNavigator
import pages.{LettingPropertyPage, PropertyStoppedBeingLetOutPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.letting.PropertyStoppedBeingLetOutView

import scala.concurrent.{ExecutionContext, Future}

class PropertyStoppedBeingLetOutController @Inject() (
  override val messagesApi: MessagesApi,
  sessionService: SessionService,
  navigator: LettingNavigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: PropertyStoppedBeingLetOutFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: PropertyStoppedBeingLetOutView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(i: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.getBySeqIndex(LettingPropertyPage, i).flatMap(_.stoppedBeingLetOut) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, i, mode))
  }

  def onSubmit(i: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, i, mode))),
          { value =>
            val lettingProperty = request.userAnswers
              .getBySeqIndex(LettingPropertyPage, i)
              .getOrElse(LettingProperty())
              .copy(stoppedBeingLetOut = Some(value))

            val (updatedLettingProperty, hasValueChanged) =
              updateLettingProperty(lettingProperty, request.userAnswers, value, i)

            for {
              updatedAnswers <-
                Future.fromTry(request.userAnswers.setBySeqIndex(LettingPropertyPage, i, updatedLettingProperty))
              _              <- sessionService.set(updatedAnswers)
            } yield Redirect(
              navigator.nextPage(PropertyStoppedBeingLetOutPage, i, mode, updatedAnswers, hasValueChanged)
            )
          }
        )
  }

  def updateLettingProperty(
    lettingProperty: LettingProperty,
    userAnswers: UserAnswers,
    value: Boolean,
    index: Int
  ): (LettingProperty, Boolean) =
    userAnswers.getBySeqIndex(LettingPropertyPage, index).flatMap(_.stoppedBeingLetOut) match {
      case Some(true) if value != true   => (lettingProperty.copy(noLongerBeingLetOut = None), false)
      case Some(false) if value != false => (lettingProperty, true)
      case _                             => (lettingProperty, false)
    }
}
