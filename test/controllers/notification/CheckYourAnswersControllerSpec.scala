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

import base.SpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.govuk.SummaryListFluency
import views.html.notification.CheckYourAnswersView
import viewmodels.checkAnswers._
import models._
import pages._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import play.api.i18n.Messages
import org.scalacheck.Arbitrary.arbitrary

class CheckYourAnswersControllerSpec extends SpecBase with SummaryListFluency {


  "Check Your Answers Controller" - {

    "must return OK and the correct view for a GET when userAnswers is empty" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckYourAnswersView]
        val list = SummaryListViewModel(Seq.empty)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(list)(request, messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad.url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    def rowIsDisplayedWhenPageIsPopulated(ua: UserAnswers)(summaryList: Messages => SummaryList) = {
      val application = applicationBuilder(userAnswers = Some(ua)).build()
      implicit val mess = messages(application)
      val list = summaryList(mess)

      running(application) {
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckYourAnswersView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(list)(request, messages(application)).toString
      }
    }

    "must return OK and the correct view for a GET when ReceivedALetterPage is populated" in {
      val ua = UserAnswers("id").set(ReceivedALetterPage, arbitrary[Boolean].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryListViewModel(Seq(ReceivedALetterSummary.row(ua)(messages)).flatten))
    }

    "must return OK and the correct view for a GET when LetterReferencePage is populated" in {
      val ua = UserAnswers("id").set(LetterReferencePage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryListViewModel(Seq(LetterReferenceSummary.row(ua)(messages)).flatten))
    }

    "must return OK and the correct view for a GET when RelatesTo is populated" in {
      val ua = UserAnswers("id").set(RelatesToPage, arbitrary[RelatesTo].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages)).flatten))
    }

    "must return OK and the correct view for a GET when AreYouTheIndividualPage is populated" in {
      val ua = UserAnswers("id").set(AreYouTheIndividualPage, arbitrary[AreYouTheIndividual].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryListViewModel(Seq(AreYouTheIndividualSummary.row(ua)(messages)).flatten))
    }

    "must return OK and the correct view for a GET when OffshoreLiabilitiesPage is populated" in {
      val ua = UserAnswers("id").set(OffshoreLiabilitiesPage,  OffshoreLiabilities.IWantTo).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryListViewModel(Seq(OffshoreLiabilitiesSummary.row(ua)(messages)).flatten))
    }

    "must return OK and the correct view for a GET when OnshoreLiabilitiesPage is populated" in {
      val ua = UserAnswers("id").set(OnshoreLiabilitiesPage, arbitrary[OnshoreLiabilities].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryListViewModel(Seq(OnshoreLiabilitiesSummary.row(ua)(messages)).flatten))
    }

  }
}
