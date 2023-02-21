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

package controllers.onshore

import controllers.actions._
import forms.TaxBeforeThreeYearsOnshoreFormProvider
import javax.inject.Inject
import models.{Behaviour, Mode}
import navigation.OnshoreNavigator
import pages.TaxBeforeThreeYearsOnshorePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{OnshoreWhichYearsService, SessionService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.onshore.TaxBeforeThreeYearsOnshoreView

import scala.concurrent.{ExecutionContext, Future}

class TaxBeforeThreeYearsOnshoreController @Inject()(
                                      override val messagesApi: MessagesApi,
                                      sessionService: SessionService,
                                      navigator: OnshoreNavigator,
                                      identify: IdentifierAction,
                                      getData: DataRetrievalAction,
                                      requireData: DataRequiredAction,
                                      formProvider: TaxBeforeThreeYearsOnshoreFormProvider,
                                      val controllerComponents: MessagesControllerComponents,
                                      view: TaxBeforeThreeYearsOnshoreView,
                                      onshoreWhichYearsService: OnshoreWhichYearsService
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val year = onshoreWhichYearsService.getEarliestYearByBehaviour(Behaviour.ReasonableExcuse).toString
  
  val form = formProvider(year)

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(TaxBeforeThreeYearsOnshorePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, year))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, year))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(TaxBeforeThreeYearsOnshorePage, value))
            _              <- sessionService.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(TaxBeforeThreeYearsOnshorePage, mode, updatedAnswers))
      )
  }
}
