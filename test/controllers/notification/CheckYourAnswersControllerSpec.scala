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
import models.address.Address
import pages._
import play.api.i18n.Messages
import org.scalacheck.Arbitrary.arbitrary

import java.time.LocalDate

class CheckYourAnswersControllerSpec extends SpecBase with SummaryListFluency {


  "Check Your Answers Controller" - {

    "must return OK and the correct view for a GET when userAnswers is empty" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckYourAnswersView]
        val backgroundList = SummaryListViewModel(Seq.empty)
        val aboutYouList = SummaryListViewModel(Seq.empty)
        val list = SummaryLists(backgroundList, aboutYouList)

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

    def rowIsDisplayedWhenPageIsPopulated(ua: UserAnswers)(summaryList: Messages => SummaryLists) = {
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
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(SummaryListViewModel(Seq(ReceivedALetterSummary.row(ua)(messages)).flatten), SummaryListViewModel(Seq.empty)))
    }

    "must return OK and the correct view for a GET when LetterReferencePage is populated" in {
      val ua = UserAnswers("id").set(LetterReferencePage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(SummaryListViewModel(Seq(LetterReferenceSummary.row(ua)(messages)).flatten), SummaryListViewModel(Seq.empty)))
    }

    "must return OK and the correct view for a GET when RelatesTo is populated" in {
      val ua = UserAnswers("id").set(RelatesToPage, arbitrary[RelatesTo].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages)).flatten), SummaryListViewModel(Seq.empty)))
    }

    "must return OK and the correct view for a GET when AreYouTheIndividualPage is populated" in {
      val ua = UserAnswers("id").set(AreYouTheIndividualPage, arbitrary[AreYouTheIndividual].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(SummaryListViewModel(Seq(AreYouTheIndividualSummary.row(ua)(messages)).flatten), SummaryListViewModel(Seq.empty)))
    }

    "must return OK and the correct view for a GET when OffshoreLiabilitiesPage is populated" in {
      val ua = UserAnswers("id").set(OffshoreLiabilitiesPage,  OffshoreLiabilities.IWantTo).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(SummaryListViewModel(Seq(OffshoreLiabilitiesSummary.row(ua)(messages)).flatten), SummaryListViewModel(Seq.empty)))
    }

    "must return OK and the correct view for a GET when OnshoreLiabilitiesPage is populated" in {
      val ua = UserAnswers("id").set(OnshoreLiabilitiesPage, arbitrary[OnshoreLiabilities].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(SummaryListViewModel(Seq(OnshoreLiabilitiesSummary.row(ua)(messages)).flatten), SummaryListViewModel(Seq.empty)))
    }

    "must return OK and the correct view for a GET when YourPhoneNumberPage is populated" in {
      val ua = UserAnswers("id").set(YourPhoneNumberPage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq.empty),
        aboutYou = SummaryListViewModel(Seq(YourPhoneNumberSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when DoYouHaveAnEmailAddressPage is populated" in {
      val ua = UserAnswers("id").set(DoYouHaveAnEmailAddressPage, arbitrary[Boolean].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq.empty),
        aboutYou = SummaryListViewModel(Seq(DoYouHaveAnEmailAddressSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when YourEmailAddressPage is populated" in {
      val ua = UserAnswers("id").set(YourEmailAddressPage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq.empty),
        aboutYou = SummaryListViewModel(Seq(YourEmailAddressSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when YourAddressLookupPage is populated" in {
      val ua = UserAnswers("id").set(YourAddressLookupPage, arbitrary[Address].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq.empty),
        aboutYou = SummaryListViewModel(Seq(YourAddressLookupSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when WhatIsYourDateOfBirthPage is populated" in {
      val ua = UserAnswers("id").set(WhatIsYourDateOfBirthPage, arbitrary[LocalDate].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq.empty),
        aboutYou = SummaryListViewModel(Seq(WhatIsYourDateOfBirthSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when WhatIsYourMainOccupationPage is populated" in {
      val ua = UserAnswers("id").set( WhatIsYourMainOccupationPage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq.empty),
        aboutYou = SummaryListViewModel(Seq(WhatIsYourMainOccupationSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when DoYouHaveNationalInsuranceNumberPage is populated" in {
      val ua = UserAnswers("id").set( DoYouHaveNationalInsuranceNumberPage, arbitrary[DoYouHaveNationalInsuranceNumber].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq.empty),
        aboutYou = SummaryListViewModel(Seq(DoYouHaveNationalInsuranceNumberSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when WhatIsYourNationalInsuranceNumberPage is populated" in {
      val ua = UserAnswers("id").set( WhatIsYourNationalInsuranceNumberPage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq.empty),
        aboutYou = SummaryListViewModel(Seq(WhatIsYourNationalInsuranceNumberSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when AreYouRegisteredForVATPage is populated" in {
      val ua = UserAnswers("id").set( AreYouRegisteredForVATPage, arbitrary[AreYouRegisteredForVAT].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq.empty),
        aboutYou = SummaryListViewModel(Seq(AreYouRegisteredForVATSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when WhatIsYourVATRegistrationNumberPage is populated" in {
      val ua = UserAnswers("id").set( WhatIsYourVATRegistrationNumberPage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq.empty),
        aboutYou = SummaryListViewModel(Seq(WhatIsYourVATRegistrationNumberSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when AreYouRegisteredForSelfAssessmentPage is populated" in {
      val ua = UserAnswers("id").set( AreYouRegisteredForSelfAssessmentPage, arbitrary[AreYouRegisteredForSelfAssessment].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq.empty),
        aboutYou = SummaryListViewModel(Seq(AreYouRegisteredForSelfAssessmentSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when WhatIsYourUniqueTaxReferencePage is populated" in {
      val ua = UserAnswers("id").set( WhatIsYourUniqueTaxReferencePage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq.empty),
        aboutYou = SummaryListViewModel(Seq(WhatIsYourUniqueTaxReferenceSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when WhatIsYourFullNamePage is populated" in {
      val ua = UserAnswers("id").set( WhatIsYourFullNamePage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq.empty),
        aboutYou = SummaryListViewModel(Seq(WhatIsYourFullNameSummary.row(ua)(messages)).flatten)
      ))
    }
  }
}
