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
import forms.YouHaveNotIncludedTheTaxYearFormProvider
import javax.inject.Inject
import models.{Mode, NormalMode, UserAnswers, TaxYearStarting}
import models.requests.DataRequest
import play.api.mvc.Result
import navigation.OffshoreNavigator
import pages.YouHaveNotIncludedTheTaxYearPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.offshore.YouHaveNotIncludedTheTaxYearView

import scala.concurrent.{ExecutionContext, Future}

class YouHaveNotIncludedTheTaxYearController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionService: SessionService,
                                        navigator: OffshoreNavigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: YouHaveNotIncludedTheTaxYearFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: YouHaveNotIncludedTheTaxYearView
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      onMissingYearData(request.userAnswers, mode){
        (userAnswers, mode, head, firstYear, lastYear) => displayPage(userAnswers, mode, head, firstYear, lastYear)
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      onMissingYearData(request.userAnswers, mode){
        (userAnswers, mode, head, firstYear, lastYear) => submitData(userAnswers, mode, head, firstYear, lastYear)
      }
  }

  private def onMissingYearData(userAnswers: UserAnswers, mode: Mode)(f: (UserAnswers, Mode, TaxYearStarting, TaxYearStarting, TaxYearStarting) => Future[Result]): Future[Result] = {
    val result = for {
      years <- userAnswers.inverselySortedOffshoreTaxYears
      firstYear <- years.toList.lastOption
      lastYear <- years.toList.headOption
    } yield {
      val missingYears = TaxYearStarting.findMissingYears(years.toList)
      missingYears match {
        case head :: Nil => f(userAnswers, mode, head, firstYear, lastYear) 
        case head :: tail => Future.successful(Redirect(controllers.offshore.routes.YouHaveNotIncludedTheTaxYearController.onPageLoad(NormalMode).url)) //TODO: Multiple missing years page
        case Nil => Future.successful(Redirect(controllers.offshore.routes.CountryOfYourOffshoreLiabilityController.onPageLoad(None, NormalMode).url))
      }
    } 
    result.getOrElse(Future.successful(Redirect(controllers.offshore.routes.WhichYearsController.onPageLoad(NormalMode).url))) 
  }

  def displayPage(userAnswers: UserAnswers, mode: Mode, missingYear: TaxYearStarting, firstYear: TaxYearStarting, lastYear: TaxYearStarting)(implicit request: DataRequest[_]): Future[Result] = {
    val preparedForm = userAnswers.get(YouHaveNotIncludedTheTaxYearPage) match {
      case None => formProvider(missingYear.toString)
      case Some(value) => formProvider(missingYear.toString).fill(value)
    }
    Future.successful(Ok(view(preparedForm, mode, missingYear.toString, firstYear.toString, lastYear.toString)))
  }

  def submitData(userAnswers: UserAnswers, mode: Mode, missingYear: TaxYearStarting, firstYear: TaxYearStarting, lastYear: TaxYearStarting)(implicit request: DataRequest[_]): Future[Result] = {
    formProvider(missingYear.toString).bindFromRequest().fold(  
      formWithErrors => 
        Future.successful(BadRequest(view(formWithErrors, mode, missingYear.toString, firstYear.toString, lastYear.toString))),

      value =>
        for {
          updatedAnswers <- Future.fromTry(userAnswers.set(YouHaveNotIncludedTheTaxYearPage, value))
          _              <- sessionService.set(updatedAnswers)
        } yield Redirect(navigator.nextPage(YouHaveNotIncludedTheTaxYearPage, mode, updatedAnswers))
    )
  }

}