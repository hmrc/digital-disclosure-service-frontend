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
import forms.DidTheLettingAgentCollectRentOnYourBehalfFormProvider
import javax.inject.Inject
import models.{Mode, LettingProperty}
import navigation.LettingNavigator
import pages.{DidTheLettingAgentCollectRentOnYourBehalfPage, LettingPropertyPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.letting.DidTheLettingAgentCollectRentOnYourBehalfView

import scala.concurrent.{ExecutionContext, Future}

class DidTheLettingAgentCollectRentOnYourBehalfController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         sessionService: SessionService,
                                         navigator: LettingNavigator,
                                         identify: IdentifierAction,
                                         getData: DataRetrievalAction,
                                         requireData: DataRequiredAction,
                                         formProvider: DidTheLettingAgentCollectRentOnYourBehalfFormProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         view: DidTheLettingAgentCollectRentOnYourBehalfView
                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(i:Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.getBySeqIndex(LettingPropertyPage, i).flatMap(_.didTheLettingAgentCollectRentOnYourBehalf) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, i, mode))
  }

  def onSubmit(i:Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, i, mode))),

        { value =>
          val updatedLettingProperty = request.userAnswers.getBySeqIndex(LettingPropertyPage, i)
            .getOrElse(LettingProperty())
            .copy(didTheLettingAgentCollectRentOnYourBehalf = Some(value))

          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.setBySeqIndex(LettingPropertyPage, i, updatedLettingProperty))
            _ <- sessionService.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(DidTheLettingAgentCollectRentOnYourBehalfPage, i, mode, updatedAnswers))
        }
      )
  }
}
