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

package controllers.otherLiabilities

import base.SpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.govuk.SummaryListFluency
import views.html.otherLiabilities.CheckYourAnswersView
import play.api.i18n.Messages
import org.scalacheck.Arbitrary.arbitrary
import viewmodels.checkAnswers._
import models.OtherLiabilityIssues._
import models._
import pages._
import viewmodels.RevealFullText

class CheckYourAnswersControllerSpec extends SpecBase with SummaryListFluency {

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
  val revealFullText = application.injector.instanceOf[RevealFullText]
  
  "Check Your Answers Controller" - {

      "must return OK and the correct view for a GET when userAnswers is empty" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
        implicit val mess = messages(application)
        val ua = UserAnswers("id")

        running(application) {
          val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad.url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[CheckYourAnswersView]
          val otherLiabilitiesList = SummaryListViewModel(Seq(OtherLiabilityIssuesSummary.row(ua)(mess)).flatten)
          val list = OtherLiabilitiesSummaryLists(otherLiabilitiesList)

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(list)(request, mess).toString
        }
      }

      def rowIsDisplayedWhenPageIsPopulated(ua: UserAnswers)(otherLiabilitiesSummaryLists: Messages => OtherLiabilitiesSummaryLists) = {
        val application = applicationBuilder(userAnswers = Some(ua)).build()
        implicit val mess = messages(application)
        val list = otherLiabilitiesSummaryLists(mess)

        running(application) {
          val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad.url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[CheckYourAnswersView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(list)(request, messages(application)).toString
        }
      }

      "must return OK and the correct view for a GET when OtherLiabilityIssuesPage is populated" in {
        val answer: Set[OtherLiabilityIssues] = Set(VatIssues, InheritanceTaxIssues)
        val ua = UserAnswers("id").set(OtherLiabilityIssuesPage, answer).success.value
        rowIsDisplayedWhenPageIsPopulated(ua)(messages => OtherLiabilitiesSummaryLists(
          SummaryListViewModel(Seq(OtherLiabilityIssuesSummary.row(ua)(messages)).flatten) 
        ))
      }

      "must return OK and the correct view for a GET when DescribeTheGiftPage is populated" in {
        val ua = UserAnswers("id").set(DescribeTheGiftPage, arbitrary[String].sample.value).success.value
        rowIsDisplayedWhenPageIsPopulated(ua)(messages => OtherLiabilitiesSummaryLists(
          SummaryListViewModel(Seq(DescribeTheGiftSummary.row(ua, revealFullText)(messages)).flatten) 
        ))
      }

      "must return OK and the correct view for a GET when WhatOtherLiabilityIssuesPage is populated" in {
        val ua = UserAnswers("id").set(WhatOtherLiabilityIssuesPage, arbitrary[String].sample.value).success.value
        rowIsDisplayedWhenPageIsPopulated(ua)(messages => OtherLiabilitiesSummaryLists(
          SummaryListViewModel(Seq(WhatOtherLiabilityIssuesSummary.row(ua, revealFullText)(messages)).flatten) 
        ))
      }

      "must return OK and the correct view for a GET when DidYouReceiveTaxCreditPage is populated" in {
        val ua = UserAnswers("id").set(DidYouReceiveTaxCreditPage, arbitrary[Boolean].sample.value).success.value
        rowIsDisplayedWhenPageIsPopulated(ua)(messages => OtherLiabilitiesSummaryLists(
          SummaryListViewModel(Seq(DidYouReceiveTaxCreditSummary.row(ua)(messages)).flatten) 
        ))
      }

  } 

}