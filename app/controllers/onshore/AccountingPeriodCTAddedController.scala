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
import forms.AccountingPeriodCTAddedFormProvider
import javax.inject.Inject
import models.{Mode, UserAnswers, CorporationTaxLiability}
import navigation.OnshoreNavigator
import pages.{AccountingPeriodCTAddedPage, CorporationTaxLiabilityPage}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.onshore.CorporationTaxLiabilityModel
import viewmodels.SummaryListRowNoValue
import views.html.onshore.AccountingPeriodCTAddedView

import scala.concurrent.{ExecutionContext, Future}

class AccountingPeriodCTAddedController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         sessionService: SessionService,
                                         navigator: OnshoreNavigator,
                                         identify: IdentifierAction,
                                         getData: DataRetrievalAction,
                                         requireData: DataRequiredAction,
                                         formProvider: AccountingPeriodCTAddedFormProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         view: AccountingPeriodCTAddedView
                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val periodEndDates = getPeriodEndDates(request.userAnswers, mode)

      Ok(view(form, periodEndDates, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val periodEndDates = getPeriodEndDates(request.userAnswers, mode)

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, periodEndDates, mode))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AccountingPeriodCTAddedPage, value))
            _              <- sessionService.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AccountingPeriodCTAddedPage, mode, updatedAnswers))
      )
  }

  def remove(i: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      for {
        updatedAnswers <- Future.fromTry(request.userAnswers.removeBySeqIndex[CorporationTaxLiability](CorporationTaxLiabilityPage, i))
        _              <- sessionService.set(updatedAnswers)
      } yield {
        updatedAnswers.get(CorporationTaxLiabilityPage) match {
          case Some(corporationTaxLiabilities) if corporationTaxLiabilities.nonEmpty => Redirect(routes.AccountingPeriodCTAddedController.onSubmit(mode).url)
          case _ =>  Redirect(routes.CorporationTaxLiabilityController.onPageLoad(0, mode).url)
        }
      }
  }

  private def getPeriodEndDates(userAnswers: UserAnswers, mode: Mode)(implicit messages:Messages): Seq[SummaryListRowNoValue] = {
    userAnswers.get(CorporationTaxLiabilityPage) match {
      case Some(corporationTaxLiabilities) => CorporationTaxLiabilityModel.row(corporationTaxLiabilities, mode)
      case _ => Seq()
    }
  }

}
