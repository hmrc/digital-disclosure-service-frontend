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
import forms.DirectorLoanAccountLiabilitiesFormProvider

import javax.inject.Inject
import models.Mode
import navigation.OnshoreNavigator
import pages.DirectorLoanAccountLiabilitiesPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.onshore.ReasonableExcuseHelper
import views.html.onshore.DirectorLoanAccountLiabilitiesView

import scala.concurrent.{ExecutionContext, Future}

class DirectorLoanAccountLiabilitiesController @Inject() (
  override val messagesApi: MessagesApi,
  sessionService: SessionService,
  navigator: OnshoreNavigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: DirectorLoanAccountLiabilitiesFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: DirectorLoanAccountLiabilitiesView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def form(hidePenaltySection: Boolean) = formProvider(hidePenaltySection)

  def onPageLoad(i: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      val hidePenaltySection = ReasonableExcuseHelper.hidePenaltyWhenReasonableExcuse(request.userAnswers)

      val preparedForm = request.userAnswers.getBySeqIndex(DirectorLoanAccountLiabilitiesPage, i) match {
        case None        => form(hidePenaltySection)
        case Some(value) => form(hidePenaltySection).fill(value)
      }

      Ok(view(preparedForm, mode, i, hidePenaltySection))
  }

  def onSubmit(i: Int, mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val hidePenaltySection = ReasonableExcuseHelper.hidePenaltyWhenReasonableExcuse(request.userAnswers)

      form(hidePenaltySection)
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode, i, hidePenaltySection))),
          value => {
            val adjustedValue =
              if (hidePenaltySection) {
                value.copy(penaltyRate = BigDecimal(0), penaltyRateReason = "")
              }
              else value
            for {
              updatedAnswers <-
                Future.fromTry(request.userAnswers.setBySeqIndex(DirectorLoanAccountLiabilitiesPage, i, adjustedValue))
              _              <- sessionService.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(DirectorLoanAccountLiabilitiesPage, mode, updatedAnswers))
          }
        )
  }
}
