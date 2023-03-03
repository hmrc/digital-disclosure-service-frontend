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

import base.SpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.govuk.SummaryListFluency
import views.html.letting.CheckYourAnswersView
import viewmodels.checkAnswers._
import models.{LettingProperty, NoLongerBeingLetOut, UserAnswers, LettingSummaryLists, TypeOfMortgageDidYouHave, NormalMode}
import models.address.Address
import pages.LettingPropertyPage
import play.api.i18n.Messages
import org.scalacheck.Arbitrary.arbitrary

import java.time.LocalDate

class CheckYourAnswersControllerSpec extends SpecBase with SummaryListFluency {

  "Check Your Answers Controller" - {

    "must return OK and the correct view for a GET when userAnswers is empty" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      implicit val mess = messages(application)

      running(application) {
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(0, NormalMode).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckYourAnswersView]
        val lettingList = SummaryListViewModel(Seq.empty)
        val list = LettingSummaryLists(lettingList)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(list, 0, NormalMode)(request, mess).toString
      }
    }

    "must redirect to Index for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(0, NormalMode).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
      }
    }

    def rowIsDisplayedWhenPageIsPopulated(ua: UserAnswers)(summaryList: Messages => LettingSummaryLists) = {
      val application = applicationBuilder(userAnswers = Some(ua)).build()
      implicit val mess = messages(application)
      val list = summaryList(mess)

      running(application) {
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(0, NormalMode).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckYourAnswersView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(list, 0, NormalMode)(request, messages(application)).toString
      }
    }

    "must return OK and the correct view for a GET when RentalAddressLookupSummary is populated" in {
      val lettingProperty = LettingProperty(address = Some(arbitrary[Address].sample.value))
      val userAnswers = UserAnswers("id").setBySeqIndex(LettingPropertyPage, 0, lettingProperty).success.value
      rowIsDisplayedWhenPageIsPopulated(userAnswers)(messages => LettingSummaryLists(
        SummaryListViewModel(Seq(RentalAddressLookupSummary.row(0, lettingProperty)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when PropertyFirstLetOutSummary is populated" in {
      val lettingProperty = LettingProperty(dateFirstLetOut = Some(arbitrary[LocalDate].sample.value))
      val userAnswers = UserAnswers("id").setBySeqIndex(LettingPropertyPage, 0, lettingProperty).success.value
      rowIsDisplayedWhenPageIsPopulated(userAnswers)(messages => LettingSummaryLists(
        SummaryListViewModel(Seq(PropertyFirstLetOutSummary.row(0, lettingProperty)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when PropertyStoppedBeingLetOutSummary is populated" in {
      val lettingProperty = LettingProperty(stoppedBeingLetOut = Some(arbitrary[Boolean].sample.value))
      val userAnswers = UserAnswers("id").setBySeqIndex(LettingPropertyPage, 0, lettingProperty).success.value
      rowIsDisplayedWhenPageIsPopulated(userAnswers)(messages => LettingSummaryLists(
        SummaryListViewModel(Seq(PropertyStoppedBeingLetOutSummary.row(0, lettingProperty)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when PropertyIsNoLongerBeingLetOutSummary is populated" in {
      val noLongerBeingLetOut = NoLongerBeingLetOut(stopDate = arbitrary[LocalDate].sample.value, whatHasHappenedToProperty = arbitrary[String].sample.value)
      val lettingProperty = LettingProperty(noLongerBeingLetOut = Some(noLongerBeingLetOut))
      val userAnswers = UserAnswers("id").setBySeqIndex(LettingPropertyPage, 0, lettingProperty).success.value
      rowIsDisplayedWhenPageIsPopulated(userAnswers)(messages => LettingSummaryLists(
        SummaryListViewModel(Seq(
          PropertyIsNoLongerBeingLetOutSummary.row(0, lettingProperty, "stopDate")(messages),
          PropertyIsNoLongerBeingLetOutSummary.row(0, lettingProperty, "whatHasHappenedToProperty")(messages)
        ).flatten)
      ))
    }

    "must return OK and the correct view for a GET when WasPropertyFurnishedSummary is populated" in {
      val lettingProperty = LettingProperty(wasFurnished = Some(arbitrary[Boolean].sample.value))
      val userAnswers = UserAnswers("id").setBySeqIndex(LettingPropertyPage, 0, lettingProperty).success.value
      rowIsDisplayedWhenPageIsPopulated(userAnswers)(messages => LettingSummaryLists(
        SummaryListViewModel(Seq(WasPropertyFurnishedSummary.row(0, lettingProperty)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when FHLSummary is populated" in {
      val lettingProperty = LettingProperty(fhl = Some(arbitrary[Boolean].sample.value))
      val userAnswers = UserAnswers("id").setBySeqIndex(LettingPropertyPage, 0, lettingProperty).success.value
      rowIsDisplayedWhenPageIsPopulated(userAnswers)(messages => LettingSummaryLists(
        SummaryListViewModel(Seq(FHLSummary.row(0, lettingProperty)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when JointlyOwnedPropertySummary is populated" in {
      val lettingProperty = LettingProperty(isJointOwnership = Some(arbitrary[Boolean].sample.value))
      val userAnswers = UserAnswers("id").setBySeqIndex(LettingPropertyPage, 0, lettingProperty).success.value
      rowIsDisplayedWhenPageIsPopulated(userAnswers)(messages => LettingSummaryLists(
        SummaryListViewModel(Seq(JointlyOwnedPropertySummary.row(0, lettingProperty)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when DidYouHaveAMortgageOnPropertySummary is populated" in {
      val lettingProperty = LettingProperty(isMortgageOnProperty = Some(arbitrary[Boolean].sample.value))
      val userAnswers = UserAnswers("id").setBySeqIndex(LettingPropertyPage, 0, lettingProperty).success.value
      rowIsDisplayedWhenPageIsPopulated(userAnswers)(messages => LettingSummaryLists(
        SummaryListViewModel(Seq(DidYouHaveAMortgageOnPropertySummary.row(0, lettingProperty)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when WasALettingAgentUsedToManagePropertySummary is populated" in {
      val lettingProperty = LettingProperty(wasPropertyManagerByAgent = Some(arbitrary[Boolean].sample.value))
      val userAnswers = UserAnswers("id").setBySeqIndex(LettingPropertyPage, 0, lettingProperty).success.value
      rowIsDisplayedWhenPageIsPopulated(userAnswers)(messages => LettingSummaryLists(
        SummaryListViewModel(Seq(WasALettingAgentUsedToManagePropertySummary.row(0, lettingProperty)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when DidTheLettingAgentCollectRentOnYourBehalfSummary is populated" in {
      val lettingProperty = LettingProperty(didTheLettingAgentCollectRentOnYourBehalf = Some(arbitrary[Boolean].sample.value))
      val userAnswers = UserAnswers("id").setBySeqIndex(LettingPropertyPage, 0, lettingProperty).success.value
      rowIsDisplayedWhenPageIsPopulated(userAnswers)(messages => LettingSummaryLists(
        SummaryListViewModel(Seq(DidTheLettingAgentCollectRentOnYourBehalfSummary.row(0, lettingProperty)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when WhatWasThePercentageIncomeYouReceivedFromPropertySummary is populated" in {
      val lettingProperty = LettingProperty(percentageIncomeOnProperty = Some(arbitrary[Int].sample.value))
      val userAnswers = UserAnswers("id").setBySeqIndex(LettingPropertyPage, 0, lettingProperty).success.value
      rowIsDisplayedWhenPageIsPopulated(userAnswers)(messages => LettingSummaryLists(
        SummaryListViewModel(Seq(WhatWasThePercentageIncomeYouReceivedFromPropertySummary.row(0, lettingProperty)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when WhatTypeOfMortgageDidYouHaveSummary is populated" in {
      val lettingProperty = LettingProperty(typeOfMortgage = Some(TypeOfMortgageDidYouHave.CapitalRepayment))
      val userAnswers = UserAnswers("id").setBySeqIndex(LettingPropertyPage, 0, lettingProperty).success.value
      rowIsDisplayedWhenPageIsPopulated(userAnswers)(messages => LettingSummaryLists(
        SummaryListViewModel(Seq(WhatTypeOfMortgageDidYouHaveSummary.row(0, lettingProperty)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when WhatWasTheTypeOfMortgageSummary is populated" in {
      val lettingProperty = LettingProperty(otherTypeOfMortgage = Some(arbitrary[String].sample.value))
      val userAnswers = UserAnswers("id").setBySeqIndex(LettingPropertyPage, 0, lettingProperty).success.value
      rowIsDisplayedWhenPageIsPopulated(userAnswers)(messages => LettingSummaryLists(
        SummaryListViewModel(Seq(WhatWasTheTypeOfMortgageSummary.row(0, lettingProperty)(messages)).flatten)
      ))
    }

  }
}
