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

package controllers.otherLiabilities

import controllers.actions._
import forms.DidYouReceiveTaxCreditFormProvider
import javax.inject.Inject
import models.{Mode, UserAnswers, RelatesTo}
import navigation.OtherLiabilitiesNavigator
import pages.{DidYouReceiveTaxCreditPage, AreYouTheIndividualPage, RelatesToPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.otherLiabilities.DidYouReceiveTaxCreditView

import scala.concurrent.{ExecutionContext, Future}

class DidYouReceiveTaxCreditController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionService: SessionService,
                                       navigator: OtherLiabilitiesNavigator,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       formProvider: DidYouReceiveTaxCreditFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: DidYouReceiveTaxCreditView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(DidYouReceiveTaxCreditPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val areTheyTheIndividual = isTheUserTheIndividual(request.userAnswers)
      val entity = request.userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      Ok(view(preparedForm, mode, areTheyTheIndividual, entity))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val areTheyTheIndividual = isTheUserTheIndividual(request.userAnswers)
      val entity = request.userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, areTheyTheIndividual, entity))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(DidYouReceiveTaxCreditPage, value))
            _              <- sessionService.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(DidYouReceiveTaxCreditPage, mode, updatedAnswers))
      )
  }

  def isTheUserTheIndividual(userAnswers: UserAnswers): Boolean = {
    userAnswers.get(AreYouTheIndividualPage) match {
      case Some(true) => true
      case _ => false
    }
  }
}
