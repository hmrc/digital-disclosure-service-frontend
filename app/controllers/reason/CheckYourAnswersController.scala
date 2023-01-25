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

package controllers.reason

import controllers.actions._
import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.reason.CheckYourAnswersView
import viewmodels.reason.CheckYourAnswersViewModel
import viewmodels.govuk.summarylist._
import viewmodels.checkAnswers._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import models.UserAnswers
import pages.DidSomeoneGiveYouAdviceNotDeclareTaxPage
import play.api.i18n.Messages

class CheckYourAnswersController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: CheckYourAnswersView
                                     ) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val ua = request.userAnswers

      val reasonList = SummaryListViewModel(
        rows = Seq(
          WhyAreYouMakingADisclosureSummary.row(ua),
          WhatIsTheReasonForMakingADisclosureNowSummary.row(ua),
          WhyNotBeforeNowSummary.row(ua),
          DidSomeoneGiveYouAdviceNotDeclareTaxSummary.row(ua)
        ).flatten
      )

      val adviceList: Option[SummaryList] = ua.get(DidSomeoneGiveYouAdviceNotDeclareTaxPage) match {
        case Some(true) => Some(getAdviceList(ua))
        case _ => None
      }

      val viewModel = CheckYourAnswersViewModel(
        reasonList,
        adviceList
      )
      
      Ok(view(viewModel))

  }

  def getAdviceList(ua: UserAnswers)(implicit messages: Messages): SummaryList = {

    val pageRows = Seq(
      PersonWhoGaveAdviceSummary.row(ua),
      AdviceBusinessesOrOrgSummary.row(ua),
      AdviceBusinessNameSummary.row(ua),
      AdviceProfessionSummary.row(ua),
    ).flatten

    val contactPageRows = Seq(
      WhatEmailAddressCanWeContactYouWithSummary.row(ua),
      WhatTelephoneNumberCanWeContactYouWithSummary.row(ua)
    ).flatten

    val rows = pageRows ++ AdviceGivenRowsSummary.rows(ua) ++ contactPageRows

    SummaryListViewModel(rows = rows)
  }
}
