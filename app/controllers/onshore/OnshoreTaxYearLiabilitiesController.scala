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
import forms.OnshoreTaxYearLiabilitiesFormProvider

import javax.inject.Inject
import models.{Mode, NormalMode, OnshoreYearStarting, OnshoreTaxYearWithLiabilities, WhatOnshoreLiabilitiesDoYouNeedToDisclose}
import navigation.OnshoreNavigator
import pages.{WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, OnshoreTaxYearLiabilitiesPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.onshore.OnshoreTaxYearLiabilitiesView
import play.api.mvc.Result
import models.requests.DataRequest

import scala.concurrent.{ExecutionContext, Future}

class OnshoreTaxYearLiabilitiesController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionService: SessionService,
                                        navigator: OnshoreNavigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: OnshoreTaxYearLiabilitiesFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: OnshoreTaxYearLiabilitiesView
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def form(taxTypes: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose]) = formProvider(taxTypes)

  def onPageLoad(i: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

    withYear(i) { year => 
      val taxTypes = request.userAnswers.get(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage).getOrElse(Set())

      val preparedForm = request.userAnswers.getByKey(OnshoreTaxYearLiabilitiesPage, year.toString) match {
        case None => form(taxTypes)
        case Some(value) => form(taxTypes).fill(value.taxYearLiabilities)
      }

      Ok(view(preparedForm, mode, i, year, taxTypes))
    }

  }


  def onSubmit(i: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

    val taxTypes = request.userAnswers.get(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage).getOrElse(Set())

      withYearAsync(i) { year =>
        form(taxTypes).bindFromRequest().fold(
          formWithErrors => {
            Future.successful(BadRequest(view(formWithErrors, mode, i, year, taxTypes)))
          },
          value => {
            val taxYearWithLiabilities = OnshoreTaxYearWithLiabilities(OnshoreYearStarting(year), value)
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.setByKey(OnshoreTaxYearLiabilitiesPage, year.toString, taxYearWithLiabilities))
              _              <- sessionService.set(updatedAnswers)
            } yield Redirect(navigator.nextTaxYearLiabilitiesPage(i, value.residentialTaxReduction.getOrElse(false), mode, updatedAnswers, false))
          }
        )
      }
  }

  def withYearAsync(i: Int)(f: Int => Future[Result])(implicit request: DataRequest[_]): Future[Result] = {
    request.userAnswers.inverselySortedOnshoreTaxYears.flatMap(_.lift(i)) match {
      case Some(year: OnshoreYearStarting) => f(year.startYear)
      case _ => Future.successful(Redirect(routes.WhichOnshoreYearsController.onPageLoad(NormalMode).url))
    }
  }

  def withYear(i: Int)(f: Int => Result)(implicit request: DataRequest[_]): Result = {
    request.userAnswers.inverselySortedOnshoreTaxYears.flatMap(_.lift(i)) match {
      case Some(year: OnshoreYearStarting) => f(year.startYear)
      case _ => Redirect(routes.WhichOnshoreYearsController.onPageLoad(NormalMode).url)
    }
  }

  

}
