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

import config.Country
import controllers.actions._
import forms.CountriesOrTerritoriesFormProvider

import javax.inject.Inject
import models.{Mode, NormalMode, UserAnswers}
import navigation.OffshoreNavigator
import pages.{CountriesOrTerritoriesPage, CountryOfYourOffshoreLiabilityPage}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.offshore.CountryModels
import views.html.offshore.CountriesOrTerritoriesView

import scala.concurrent.{ExecutionContext, Future}

class CountriesOrTerritoriesController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         sessionService: SessionService,
                                         navigator: OffshoreNavigator,
                                         identify: IdentifierAction,
                                         getData: DataRetrievalAction,
                                         requireData: DataRequiredAction,
                                         formProvider: CountriesOrTerritoriesFormProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         view: CountriesOrTerritoriesView
                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>


      val countries = getCountries(request.userAnswers)

      val preparedForm = request.userAnswers.get(CountriesOrTerritoriesPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }


      Ok(view(preparedForm, countries, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val countries = getCountries(request.userAnswers)

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, countries, mode))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(CountriesOrTerritoriesPage, value))
            _              <- sessionService.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(CountriesOrTerritoriesPage, mode, updatedAnswers))
      )
  }

  def remove(countryCode:String): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      for {
        updatedAnswers <- Future.fromTry(request.userAnswers.removeByKey[Country](CountryOfYourOffshoreLiabilityPage, countryCode))
        _ <- sessionService.set(updatedAnswers)
      } yield {
        updatedAnswers.get(CountryOfYourOffshoreLiabilityPage) match {
          case Some(countries) if countries.nonEmpty => Redirect(routes.CountriesOrTerritoriesController.onSubmit(NormalMode).url)
          case _ =>  Redirect(routes.CountryOfYourOffshoreLiabilityController.onSubmit(NormalMode).url)
        }
      }

  }

  private def getCountries(userAnswers:UserAnswers)(implicit messages:Messages): Seq[SummaryListRow] =
    userAnswers.get(CountryOfYourOffshoreLiabilityPage) match {
      case Some(countryMap) => CountryModels.row(countryMap.values.toSet)
      case None => Seq()
    }
}
