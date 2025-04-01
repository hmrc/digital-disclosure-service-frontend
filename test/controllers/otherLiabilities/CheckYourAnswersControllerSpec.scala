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
import models.OtherLiabilityIssues._
import models._
import org.scalacheck.Arbitrary.arbitrary
import pages._
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.RevealFullText
import viewmodels.checkAnswers._
import viewmodels.govuk.SummaryListFluency
import views.html.otherLiabilities.CheckYourAnswersView

class CheckYourAnswersControllerSpec extends SpecBase with SummaryListFluency {

  val revealFullText: RevealFullText = application.injector.instanceOf[RevealFullText]

  "Check Your Answers Controller" - {

    "must return OK and the correct view for a GET when userAnswers is empty" in {

      setupMockSessionResponse(Some(emptyUserAnswers))
      val ua = UserAnswers("id", "session-123")

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad.url)

      val result = route(application, request).value

      val view                 = application.injector.instanceOf[CheckYourAnswersView]
      val otherLiabilitiesList = SummaryListViewModel(Seq(OtherLiabilityIssuesSummary.row(ua)(messages)).flatten)
      val list                 = OtherLiabilitiesSummaryLists(otherLiabilitiesList)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(list)(request, messages).toString
    }

    def rowIsDisplayedWhenPageIsPopulated(
      ua: UserAnswers
    )(otherLiabilitiesSummaryLists: Messages => OtherLiabilitiesSummaryLists) = {

      setupMockSessionResponse(Some(ua))
      val list = otherLiabilitiesSummaryLists(messages)

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad.url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckYourAnswersView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(list)(request, messages).toString
    }

    "must return OK and the correct view for a GET when OtherLiabilityIssuesPage is populated" in {
      val answer: Set[OtherLiabilityIssues] = Set(VatIssues, InheritanceTaxIssues)
      val ua                                = UserAnswers("id", "session-123").set(OtherLiabilityIssuesPage, answer).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages =>
        OtherLiabilitiesSummaryLists(
          SummaryListViewModel(Seq(OtherLiabilityIssuesSummary.row(ua)(messages)).flatten)
        )
      )
    }

    "must return OK and the correct view for a GET when DescribeTheGiftPage is populated" in {
      val ua = UserAnswers("id", "session-123").set(DescribeTheGiftPage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages =>
        OtherLiabilitiesSummaryLists(
          SummaryListViewModel(Seq(DescribeTheGiftSummary.row(ua, revealFullText)(messages)).flatten)
        )
      )
    }

    "must return OK and the correct view for a GET when WhatOtherLiabilityIssuesPage is populated" in {
      val ua =
        UserAnswers("id", "session-123").set(WhatOtherLiabilityIssuesPage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages =>
        OtherLiabilitiesSummaryLists(
          SummaryListViewModel(Seq(WhatOtherLiabilityIssuesSummary.row(ua, revealFullText)(messages)).flatten)
        )
      )
    }

    "must return OK and the correct view for a GET when DidYouReceiveTaxCreditPage is populated" in {
      val ua =
        UserAnswers("id", "session-123").set(DidYouReceiveTaxCreditPage, arbitrary[Boolean].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages =>
        OtherLiabilitiesSummaryLists(
          SummaryListViewModel(Seq(DidYouReceiveTaxCreditSummary.row(ua)(messages)).flatten)
        )
      )
    }

  }

}
