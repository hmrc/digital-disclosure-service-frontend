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

package controllers.offshore

import controllers.actions._
import forms.ForeignTaxCreditFormProvider
import javax.inject.Inject
import models.{Mode, NormalMode, TaxYearStarting}
import navigation.OffshoreNavigator
import pages.ForeignTaxCreditPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.offshore.ForeignTaxCreditView
import play.api.mvc.Result
import models.requests.DataRequest

import scala.concurrent.{ExecutionContext, Future}

class ForeignTaxCreditController @Inject() (
  override val messagesApi: MessagesApi,
  sessionService: SessionService,
  navigator: OffshoreNavigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: ForeignTaxCreditFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ForeignTaxCreditView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(i: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      withYear(i) { year =>
        val preparedForm = request.userAnswers.getByKey(ForeignTaxCreditPage, year.toString) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm, i, (year + 1).toString, mode))
      }
  }

  def onSubmit(i: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      withYearAsync(i) { year =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, i, (year + 1).toString, mode))),
            value =>
              for {
                updatedAnswers <-
                  Future.fromTry(request.userAnswers.setByKey(ForeignTaxCreditPage, year.toString, value))
                _              <- sessionService.set(updatedAnswers)
              } yield Redirect(navigator.nextTaxYearLiabilitiesPage(i, false, mode, updatedAnswers))
          )
      }
  }

  def withYearAsync(i: Int)(f: Int => Future[Result])(implicit request: DataRequest[_]): Future[Result] =
    request.userAnswers.inverselySortedOffshoreTaxYears.flatMap(_.lift(i)) match {
      case Some(year: TaxYearStarting) => f(year.startYear)
      case _                           => Future.successful(Redirect(routes.WhichYearsController.onPageLoad(NormalMode).url))
    }

  def withYear(i: Int)(f: Int => Result)(implicit request: DataRequest[_]): Result =
    request.userAnswers.inverselySortedOffshoreTaxYears.flatMap(_.lift(i)) match {
      case Some(year: TaxYearStarting) => f(year.startYear)
      case _                           => Redirect(routes.WhichYearsController.onPageLoad(NormalMode).url)
    }
}
