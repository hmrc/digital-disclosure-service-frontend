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
import forms.NotIncludedSingleTaxYearFormProvider
import javax.inject.Inject
import models.{Mode, NormalMode, UserAnswers, OnshoreYearStarting}
import models.requests.DataRequest
import play.api.mvc.Result
import navigation.OnshoreNavigator
import pages.NotIncludedSingleTaxYearPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.onshore.NotIncludedSingleTaxYearView

import scala.concurrent.{ExecutionContext, Future}

class NotIncludedSingleTaxYearController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionService: SessionService,
                                        navigator: OnshoreNavigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: NotIncludedSingleTaxYearFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: NotIncludedSingleTaxYearView
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

  private def onMissingYearData(userAnswers: UserAnswers, mode: Mode)(f: (UserAnswers, Mode, OnshoreYearStarting, OnshoreYearStarting, OnshoreYearStarting) => Future[Result]): Future[Result] = {
    val result = for {
      years <- userAnswers.inverselySortedOnshoreTaxYears
      firstYear <- years.toList.lastOption
      lastYear <- years.toList.headOption
    } yield {
      val missingYears = OnshoreYearStarting.findMissingYears(years.toList)
      missingYears match {
        case head :: Nil => f(userAnswers, mode, head, firstYear, lastYear) 
        case head :: tail => Future.successful(Redirect(controllers.onshore.routes.NotIncludedMultipleTaxYearsController.onPageLoad(NormalMode).url))
        //TODO this needs to go the next page in the journey
        case Nil => Future.successful(Redirect(controllers.routes.IndexController.onPageLoad.url))
      }
    } 
    //TODO this needs to go the which years page
    result.getOrElse(Future.successful(Redirect(controllers.routes.IndexController.onPageLoad.url))) 
  }

  def displayPage(userAnswers: UserAnswers, mode: Mode, missingYear: OnshoreYearStarting, firstYear: OnshoreYearStarting, lastYear: OnshoreYearStarting)(implicit request: DataRequest[_]): Future[Result] = {
    val missingYr: String = (missingYear.toString.toInt + 1).toString
    val firstYr: String = (firstYear.toString.toInt + 1).toString
    val lastYr: String = (lastYear.toString.toInt + 1).toString
    
    val preparedForm = userAnswers.get(NotIncludedSingleTaxYearPage) match {
      case None => formProvider(missingYr)
      case Some(value) => formProvider(missingYr).fill(value)
    }
    Future.successful(Ok(view(preparedForm, mode, missingYr, firstYr, lastYr)))
  }

  def submitData(userAnswers: UserAnswers, mode: Mode, missingYear: OnshoreYearStarting, firstYear: OnshoreYearStarting, lastYear: OnshoreYearStarting)(implicit request: DataRequest[_]): Future[Result] = {
    val missingYr: String = (missingYear.toString.toInt + 1).toString
    val firstYr: String = (firstYear.toString.toInt + 1).toString
    val lastYr: String = (lastYear.toString.toInt + 1).toString
    
    formProvider(missingYr).bindFromRequest().fold(  
      formWithErrors => 
        Future.successful(BadRequest(view(formWithErrors, mode, missingYr, firstYr, lastYr))),

      value =>
        for {
          updatedAnswers <- Future.fromTry(userAnswers.set(NotIncludedSingleTaxYearPage, value))
          _              <- sessionService.set(updatedAnswers)
        } yield Redirect(navigator.nextPage(NotIncludedSingleTaxYearPage, mode, updatedAnswers))
    )
  }

}