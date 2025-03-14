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

package controllers.reference

import controllers.actions._
import forms.DoYouHaveACaseReferenceFormProvider
import javax.inject.Inject
import models.{Mode, UserAnswers}
import navigation.ReferenceNavigator
import pages.{DoYouHaveACaseReferencePage, QuestionPage, WhatIsTheCaseReferencePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.reference.DoYouHaveACaseReferenceView

import scala.concurrent.{ExecutionContext, Future}

class DoYouHaveACaseReferenceController @Inject() (
  override val messagesApi: MessagesApi,
  sessionService: SessionService,
  navigator: ReferenceNavigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: DoYouHaveACaseReferenceFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: DoYouHaveACaseReferenceView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.get(DoYouHaveACaseReferencePage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode))),
          value => {

            val pagesToBeClear = userAnswerChanged(request.userAnswers, value)

            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(DoYouHaveACaseReferencePage, value))
              clearedAnswers <- Future.fromTry(updatedAnswers.remove(pagesToBeClear))
              _              <- sessionService.set(clearedAnswers)
            } yield Redirect(navigator.nextPage(DoYouHaveACaseReferencePage, mode, clearedAnswers))
          }
        )
  }

  def userAnswerChanged(userAnswers: UserAnswers, newValue: Boolean): List[QuestionPage[_]] = {
    val caseReference = userAnswers.get(WhatIsTheCaseReferencePage)
    (newValue, caseReference) match {
      case (false, Some(_)) => List(WhatIsTheCaseReferencePage)
      case _                => Nil
    }
  }
}
