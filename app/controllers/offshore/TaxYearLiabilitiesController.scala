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
import forms.TaxYearLiabilitiesFormProvider
import javax.inject.Inject
import models.{Mode, TaxYearStarting, TaxYearWithLiabilities, NormalMode}
import navigation.OffshoreNavigator
import pages.TaxYearLiabilitiesPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.offshore.TaxYearLiabilitiesView
import play.api.mvc.Result
import models.requests.DataRequest

import scala.concurrent.{ExecutionContext, Future}

class TaxYearLiabilitiesController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionService: SessionService,
                                        navigator: OffshoreNavigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: TaxYearLiabilitiesFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: TaxYearLiabilitiesView
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(i: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

    withYear(i) { year => 
      val preparedForm = request.userAnswers.getByKey(TaxYearLiabilitiesPage, year.toString) match {
        case None => form
        case Some(value) => form.fill(value.taxYearLiabilities)
      }

      Ok(view(preparedForm, mode, i, year))
    }

  }


  def onSubmit(i: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      withYearAsync(i) { year =>
        form.bindFromRequest().fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors, mode, i, year))),

          value => {
            val taxYearWithLiabilities = TaxYearWithLiabilities(TaxYearStarting(year), value)
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.setByKey(TaxYearLiabilitiesPage, year.toString, taxYearWithLiabilities))
              _              <- sessionService.set(updatedAnswers)
            } yield Redirect(navigator.nextTaxYearLiabilitiesPage(i, mode, updatedAnswers))
          }
        )
      }
  }

  def withYearAsync(i: Int)(f: Int => Future[Result])(implicit request: DataRequest[_]): Future[Result] = {
    request.userAnswers.inverselySortedOffshoreTaxYears.flatMap(_.lift(i)) match {
      case Some(year: TaxYearStarting) => f(year.startYear)
      case _ => Future.successful(Redirect(routes.WhichYearsController.onPageLoad(NormalMode).url))
    }
  }

  def withYear(i: Int)(f: Int => Result)(implicit request: DataRequest[_]): Result = {
    request.userAnswers.inverselySortedOffshoreTaxYears.flatMap(_.lift(i)) match {
      case Some(year: TaxYearStarting) => f(year.startYear)
      case _ => Redirect(routes.WhichYearsController.onPageLoad(NormalMode).url)
    }
  }
}
