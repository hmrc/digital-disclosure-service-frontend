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

package controllers.letting

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import models.{LettingSummaryLists, LettingProperty}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.govuk.summarylist._
import views.html.letting.CheckYourAnswersView
import viewmodels.checkAnswers._
import pages.LettingPropertyPage

import scala.concurrent.ExecutionContext

class CheckYourAnswersController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            identify: IdentifierAction,
                                            getData: DataRetrievalAction,
                                            requireData: DataRequiredAction,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: CheckYourAnswersView,
                                          )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(i: Int): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val ua = request.userAnswers

      val lettingProperty = ua.getBySeqIndex(LettingPropertyPage, i).getOrElse(LettingProperty())

      val lettingList = SummaryListViewModel(
        rows = Seq(
          RentalAddressLookupSummary.row(i, lettingProperty),
          PropertyFirstLetOutSummary.row(i, lettingProperty),
          PropertyStoppedBeingLetOutSummary.row(i, lettingProperty),
          PropertyIsNoLongerBeingLetOutSummary.row(i, lettingProperty, "stopDate"),
          PropertyIsNoLongerBeingLetOutSummary.row(i, lettingProperty, "whatHasHappenedToProperty"),
          WasPropertyFurnishedSummary.row(i, lettingProperty),
          FHLSummary.row(i, lettingProperty),
          JointlyOwnedPropertySummary.row(i, lettingProperty),
          WhatWasThePercentageIncomeYouReceivedFromPropertySummary.row(i, lettingProperty),
          DidYouHaveAMortgageOnPropertySummary.row(i, lettingProperty),
          WhatTypeOfMortgageDidYouHaveSummary.row(i, lettingProperty),
          WhatWasTheTypeOfMortgageSummary.row(i, lettingProperty),
          WasALettingAgentUsedToManagePropertySummary.row(i, lettingProperty),
          DidTheLettingAgentCollectRentOnYourBehalfSummary.row(i, lettingProperty)
        ).flatten
      )

      val list = LettingSummaryLists(
        lettingList
      )
      
      Ok(view(list, i))
  }
}
