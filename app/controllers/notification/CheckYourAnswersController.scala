/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.notification

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.govuk.summarylist._
import views.html.notification.CheckYourAnswersView
import viewmodels.checkAnswers._
import pages._
import models._

class CheckYourAnswersController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            identify: IdentifierAction,
                                            getData: DataRetrievalAction,
                                            requireData: DataRequiredAction,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: CheckYourAnswersView
                                          ) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val ua = request.userAnswers

      val backgroundList = SummaryListViewModel(
        rows = Seq(
          ReceivedALetterSummary.row(ua),
          LetterReferenceSummary.row(ua),
          RelatesToSummary.row(ua),
          AreYouTheIndividualSummary.row(ua),
          OffshoreLiabilitiesSummary.row(ua),
          OnshoreLiabilitiesSummary.row(ua)
        ).flatten
      )

      val aboutTheIndividualList = request.userAnswers.get(AreYouTheIndividualPage) match {
        case Some(AreYouTheIndividual.No) => 
          Some(
            SummaryListViewModel(
              rows = Seq(
                WhatIsTheIndividualsFullNameSummary.row(ua),
                WhatIsTheIndividualDateOfBirthControllerSummary.row(ua),
                WhatIsTheIndividualOccupationSummary.row(ua),
                DoesTheIndividualHaveNationalInsuranceNumberSummary.row(ua),
                WhatIsIndividualsNationalInsuranceNumberSummary.row(ua),
                IsTheIndividualRegisteredForVATSummary.row(ua),
                WhatIsTheIndividualsVATRegistrationNumberSummary.row(ua),
                IsTheIndividualRegisteredForSelfAssessmentSummary.row(ua),
                WhatIsTheIndividualsUniqueTaxReferenceSummary.row(ua),
                IndividualAddressLookupSummary.row(ua)
              ).flatten
            )
          )
        case _ => None
      }

      Ok(view(backgroundList, aboutTheIndividualList))
  }
}
