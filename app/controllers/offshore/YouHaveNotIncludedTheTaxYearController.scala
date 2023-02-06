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
import models.{Mode, UserAnswers, TaxYearStarting}
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

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val (firstYear, missingYear, lastYear) = getMissingYearData(request.userAnswers)

      val preparedForm = request.userAnswers.get(YouHaveNotIncludedTheTaxYearPage) match {
        case None => formProvider(missingYear)
        case Some(value) => formProvider(missingYear).fill(value)
      }

      Ok(view(preparedForm, mode, firstYear, missingYear, lastYear))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val (firstYear, missingYear, lastYear) = getMissingYearData(request.userAnswers)

      formProvider(missingYear).bindFromRequest().fold(  
        formWithErrors => 
          Future.successful(BadRequest(view(formWithErrors, mode, firstYear, missingYear, lastYear))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(YouHaveNotIncludedTheTaxYearPage, value))
            _              <- sessionService.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(YouHaveNotIncludedTheTaxYearPage, mode, updatedAnswers))
      )
  }

  private def getMissingYearData(userAnswers: UserAnswers): (String, String, String) = {
    val taxYearStartingList = userAnswers.inverselySortedOffshoreTaxYears.toList.flatten
    val missingYears = TaxYearStarting.findMissingYears(taxYearStartingList)
    val missingYear = if (missingYears.nonEmpty) missingYears(0).toString else "0"
    val firstYear = (missingYear.toInt - 1).toString
    val lastYear = (missingYear.toInt + 1).toString 
    (firstYear, missingYear, lastYear)
  }

}