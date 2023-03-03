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
import forms.AccountingPeriodDLAddedFormProvider
import javax.inject.Inject
import models.{Mode, UserAnswers, DirectorLoanAccountLiabilities}
import navigation.OnshoreNavigator
import pages.{AccountingPeriodDLAddedPage, DirectorLoanAccountLiabilitiesPage}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.onshore.DirectorLoanAccountLiabilityModel
import views.html.onshore.AccountingPeriodDLAddedView

import scala.concurrent.{ExecutionContext, Future}

class AccountingPeriodDLAddedController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         sessionService: SessionService,
                                         navigator: OnshoreNavigator,
                                         identify: IdentifierAction,
                                         getData: DataRetrievalAction,
                                         requireData: DataRequiredAction,
                                         formProvider: AccountingPeriodDLAddedFormProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         view: AccountingPeriodDLAddedView
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
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AccountingPeriodDLAddedPage, value))
            _              <- sessionService.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AccountingPeriodDLAddedPage, mode, updatedAnswers))
      )
  }

  def remove(i: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      for {
        updatedAnswers <- Future.fromTry(request.userAnswers.removeByIndex[DirectorLoanAccountLiabilities](DirectorLoanAccountLiabilitiesPage, i))
        _              <- sessionService.set(updatedAnswers)
      } yield {
        updatedAnswers.get(DirectorLoanAccountLiabilitiesPage) match {
          case Some(directorLoanAccountLiabilities) if directorLoanAccountLiabilities.nonEmpty => Redirect(routes.AccountingPeriodDLAddedController.onSubmit(mode).url)
          case _ =>  Redirect(routes.DirectorLoanAccountLiabilitiesController.onPageLoad(0, mode).url)
        }
      }
  }

  private def getPeriodEndDates(userAnswers: UserAnswers, mode: Mode)(implicit messages:Messages): Seq[SummaryListRow] = {
    userAnswers.get(DirectorLoanAccountLiabilitiesPage) match {
      case Some(directorLoanAccountLiabilities) => DirectorLoanAccountLiabilityModel.row(directorLoanAccountLiabilities, mode)
      case _ => Seq()
    }
  }

}
