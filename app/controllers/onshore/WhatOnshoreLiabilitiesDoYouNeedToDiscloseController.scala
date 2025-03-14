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
import forms.WhatOnshoreLiabilitiesDoYouNeedToDiscloseFormProvider
import models.WhatOnshoreLiabilitiesDoYouNeedToDisclose._

import javax.inject.Inject
import models.{Mode, RelatesTo, UserAnswers, WhatOnshoreLiabilitiesDoYouNeedToDisclose}
import navigation.OnshoreNavigator
import pages.{CorporationTaxLiabilityPage, DirectorLoanAccountLiabilitiesPage, LettingPropertyPage, OnshoreTaxYearLiabilitiesPage, QuestionPage, RelatesToPage, TaxBeforeFiveYearsOnshorePage, TaxBeforeSevenYearsPage, WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, WhichOnshoreYearsPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.onshore.WhatOnshoreLiabilitiesDoYouNeedToDiscloseView

import scala.concurrent.{ExecutionContext, Future}

class WhatOnshoreLiabilitiesDoYouNeedToDiscloseController @Inject() (
  override val messagesApi: MessagesApi,
  sessionService: SessionService,
  navigator: OnshoreNavigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: WhatOnshoreLiabilitiesDoYouNeedToDiscloseFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: WhatOnshoreLiabilitiesDoYouNeedToDiscloseView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.get(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    val isUserCompany = isTheUserACompany(request.userAnswers)

    Ok(view(preparedForm, mode, isUserCompany))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val isUserCompany = isTheUserACompany(request.userAnswers)

      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode, isUserCompany))),
          value => {
            val (pagesToClear, hasValueChanged) = changedPages(request.userAnswers, value)
            for {
              updatedAnswers <-
                Future.fromTry(request.userAnswers.set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, value))
              clearedPages   <- Future.fromTry(updatedAnswers.remove(pagesToClear))
              _              <- sessionService.set(clearedPages)
            } yield Redirect(
              navigator.nextPage(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, mode, updatedAnswers, hasValueChanged)
            )
          }
        )
  }

  def isTheUserACompany(userAnswers: UserAnswers): Boolean =
    userAnswers.get(RelatesToPage) match {
      case Some(RelatesTo.ACompany) => true
      case _                        => false
    }

  def changedPages(
    answers: UserAnswers,
    value: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose]
  ): (List[QuestionPage[_]], Boolean) =
    answers.get(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage) match {
      case Some(liability) if liability != value => (getPages(value), true)
      case _                                     => (Nil, false)
    }

  private def getPages(liabilities: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose]): List[QuestionPage[_]] = {
    case class ClearingCondition(
      selections: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose],
      pagesToClear: List[QuestionPage[_]]
    ) {
      def isConditionMet(liabilities: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose]): Boolean =
        liabilities.intersect(selections).isEmpty
    }

    val nonCompany     = ClearingCondition(
      Set(BusinessIncome, Gains, NonBusinessIncome, LettingIncome),
      List(
        WhichOnshoreYearsPage,
        OnshoreTaxYearLiabilitiesPage,
        TaxBeforeFiveYearsOnshorePage,
        TaxBeforeSevenYearsPage,
        TaxBeforeFiveYearsOnshorePage
      )
    )
    val corporationTax = ClearingCondition(Set(CorporationTax), List(CorporationTaxLiabilityPage))
    val directorLoan   = ClearingCondition(Set(DirectorLoan), List(DirectorLoanAccountLiabilitiesPage))
    val lettingIncome  = ClearingCondition(Set(LettingIncome), List(LettingPropertyPage))

    val conditions = List(nonCompany, corporationTax, directorLoan, lettingIncome)

    conditions.foldLeft[List[QuestionPage[_]]](List()) { (cleared, condition) =>
      if (condition.isConditionMet(liabilities)) cleared ++ condition.pagesToClear else cleared
    }
  }
}
