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

package controllers

import base.SpecBase
import models.{RelatesTo, UserAnswers, WhyAreYouMakingThisDisclosure}
import pages.{AreYouTheIndividualPage, RelatesToPage, WhyAreYouMakingThisDisclosurePage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.offshore.YouHaveNoOffshoreLiabilitiesView

class YouHaveNoOffshoreLiabilitiesControllerSpec extends SpecBase {

  "YouHaveNoOffshoreLiabilities Controller" - {

    "must return OK and the correct view for a GET for Individual and the user is the individual" in {

      val entityString = "iAmIndividual"
      val areTheyTheIndividual = true
      val entity = RelatesTo.AnIndividual
      val years = 20
      val reasons: Set[WhyAreYouMakingThisDisclosure] = Set(
        WhyAreYouMakingThisDisclosure.DeliberateInaccurateReturn,
        WhyAreYouMakingThisDisclosure.InaccurateReturnNoCare,
        WhyAreYouMakingThisDisclosure.InaccurateReturnWithCare
      )

      val userAnswers = (for {
        uaEntity <- UserAnswers(userAnswersId).set(RelatesToPage, entity)
        uaIndividual <- uaEntity.set(AreYouTheIndividualPage, areTheyTheIndividual)
        updatedUa <- uaIndividual.set(WhyAreYouMakingThisDisclosurePage, reasons)
      } yield updatedUa).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, offshore.routes.YouHaveNoOffshoreLiabilitiesController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[YouHaveNoOffshoreLiabilitiesView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(entityString, entity, years)(request, messages(application)).toString
      }
    }

    "must return OK and the correct view for a GET for Individual and the user is not the individual" in {

      val entityString = "iAmNotIndividual"
      val areTheyTheIndividual = false
      val entity = RelatesTo.AnIndividual
      val years = 20
      val reasons: Set[WhyAreYouMakingThisDisclosure] = Set(
        WhyAreYouMakingThisDisclosure.DeliberateInaccurateReturn,
        WhyAreYouMakingThisDisclosure.InaccurateReturnNoCare,
        WhyAreYouMakingThisDisclosure.InaccurateReturnWithCare
      )

      val userAnswers = (for {
        uaEntity <- UserAnswers(userAnswersId).set(RelatesToPage, entity)
        uaIndividual <- uaEntity.set(AreYouTheIndividualPage, areTheyTheIndividual)
        updatedUa <- uaIndividual.set(WhyAreYouMakingThisDisclosurePage, reasons)
      } yield updatedUa).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, offshore.routes.YouHaveNoOffshoreLiabilitiesController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[YouHaveNoOffshoreLiabilitiesView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(entityString, entity, years)(request, messages(application)).toString
      }
    }


    "must return OK and the correct view for a GET for an Entity different from individual" in {

      val entityString = "other"
      val areTheyTheIndividual = false
      val entity = RelatesTo.ACompany
      val years = 20
      val reasons: Set[WhyAreYouMakingThisDisclosure] = Set(
        WhyAreYouMakingThisDisclosure.DeliberateInaccurateReturn,
        WhyAreYouMakingThisDisclosure.InaccurateReturnNoCare,
        WhyAreYouMakingThisDisclosure.InaccurateReturnWithCare
      )

      val userAnswers = (for {
        uaEntity <- UserAnswers(userAnswersId).set(RelatesToPage, entity)
        uaIndividual <- uaEntity.set(AreYouTheIndividualPage, areTheyTheIndividual)
        updatedUa <- uaIndividual.set(WhyAreYouMakingThisDisclosurePage, reasons)
      } yield updatedUa).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, offshore.routes.YouHaveNoOffshoreLiabilitiesController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[YouHaveNoOffshoreLiabilitiesView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(entityString, entity, years)(request, messages(application)).toString
      }
    }


    "must return OK and the correct view with 8 years for a GET for an Entity different from individual" in {

      val entityString = "other"
      val areTheyTheIndividual = false
      val entity = RelatesTo.ACompany
      val years = 8
      val reasons: Set[WhyAreYouMakingThisDisclosure] = Set(
        WhyAreYouMakingThisDisclosure.InaccurateReturnNoCare,
        WhyAreYouMakingThisDisclosure.InaccurateReturnWithCare
      )

      val userAnswers = (for {
        uaEntity <- UserAnswers(userAnswersId).set(RelatesToPage, entity)
        uaIndividual <- uaEntity.set(AreYouTheIndividualPage, areTheyTheIndividual)
        updatedUa <- uaIndividual.set(WhyAreYouMakingThisDisclosurePage, reasons)
      } yield updatedUa).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, offshore.routes.YouHaveNoOffshoreLiabilitiesController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[YouHaveNoOffshoreLiabilitiesView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(entityString, entity, years)(request, messages(application)).toString
      }
    }

    "must return OK and the correct view with 6 years for a GET for an Entity different from individual" in {

      val entityString = "other"
      val areTheyTheIndividual = false
      val entity = RelatesTo.ACompany
      val years = 6
      val reasons: Set[WhyAreYouMakingThisDisclosure] = Set(WhyAreYouMakingThisDisclosure.InaccurateReturnWithCare)

      val userAnswers = (for {
        uaEntity <- UserAnswers(userAnswersId).set(RelatesToPage, entity)
        uaIndividual <- uaEntity.set(AreYouTheIndividualPage, areTheyTheIndividual)
        updatedUa <- uaIndividual.set(WhyAreYouMakingThisDisclosurePage, reasons)
      } yield updatedUa).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, offshore.routes.YouHaveNoOffshoreLiabilitiesController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[YouHaveNoOffshoreLiabilitiesView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(entityString, entity, years)(request, messages(application)).toString
      }
    }
  }
}
