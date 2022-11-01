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
import forms.DoYouHaveNationalInsuranceNumberFormProvider

import javax.inject.Inject
import models.{AreYouTheIndividual, DoYouHaveNationalInsuranceNumber, Mode, UserAnswers}
import navigation.NotificationNavigator
import pages.{AreYouTheIndividualPage, DoYouHaveNationalInsuranceNumberPage, QuestionPage, WhatIsYourNationalInsuranceNumberPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.notification.DoYouHaveNationalInsuranceNumberView

import scala.concurrent.{ExecutionContext, Future}

class DoYouHaveNationalInsuranceNumberController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionRepository: SessionRepository,
                                       navigator: NotificationNavigator,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       formProvider: DoYouHaveNationalInsuranceNumberFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: DoYouHaveNationalInsuranceNumberView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(DoYouHaveNationalInsuranceNumberPage) match {
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
          val (userAnswers, hasChanged) = userChanges(request.userAnswers, value)

          for {
            updatedAnswers <- Future.fromTry(userAnswers.set(DoYouHaveNationalInsuranceNumberPage, value))
            _ <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(DoYouHaveNationalInsuranceNumberPage, mode, updatedAnswers, hasChanged))
        }
      )
  }

  def userChanges(answers: UserAnswers, newAnswer: DoYouHaveNationalInsuranceNumber): (UserAnswers, Boolean) =
    answers.get(DoYouHaveNationalInsuranceNumberPage) match {
      case Some(DoYouHaveNationalInsuranceNumber.YesIknow) if DoYouHaveNationalInsuranceNumber.YesIknow != newAnswer =>
        (answers.remove(WhatIsYourNationalInsuranceNumberPage).get, true)
      case Some(_) if DoYouHaveNationalInsuranceNumber.YesIknow == newAnswer => (answers, true)
      case _ => (answers, false)
    }
}
