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

package controllers.offshore

import base.SpecBase
import models.{AreYouTheEntity, Behaviour, RelatesTo, UserAnswers, WhyAreYouMakingThisDisclosure}
import pages.{AreYouTheEntityPage, RelatesToPage, WhyAreYouMakingThisDisclosurePage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.OffshoreWhichYearsService
import views.html.offshore.YouHaveNoOffshoreLiabilitiesView

class YouHaveNoOffshoreLiabilitiesControllerSpec extends SpecBase {

  "YouHaveNoOffshoreLiabilities Controller" - {

    "must return OK and the correct view for a GET for Individual and the user is the individual" in {

      val entityString                                = "iAmIndividual"
      val areTheyTheIndividual                        = AreYouTheEntity.YesIAm
      val entity                                      = RelatesTo.AnIndividual
      val reasons: Set[WhyAreYouMakingThisDisclosure] = Set(
        WhyAreYouMakingThisDisclosure.DeliberateInaccurateReturn,
        WhyAreYouMakingThisDisclosure.InaccurateReturnNoCare,
        WhyAreYouMakingThisDisclosure.InaccurateReturnWithCare
      )

      val userAnswers = (for {
        uaEntity     <- UserAnswers(userAnswersId, "session-123").set(RelatesToPage, entity)
        uaIndividual <- uaEntity.set(AreYouTheEntityPage, areTheyTheIndividual)
        updatedUa    <- uaIndividual.set(WhyAreYouMakingThisDisclosurePage, reasons)
      } yield updatedUa).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, routes.YouHaveNoOffshoreLiabilitiesController.onPageLoad.url)

      val result = route(application, request).value

      val service = application.injector.instanceOf[OffshoreWhichYearsService]
      val year    = service.getEarliestYearByBehaviour(Behaviour.Deliberate).toString

      val view = application.injector.instanceOf[YouHaveNoOffshoreLiabilitiesView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(entityString, entity, year)(request, messages).toString
    }

    "must return OK and the correct view for a GET for Individual and the user is not the individual" in {

      val entityString                                = "iAmNotIndividual"
      val areTheyTheIndividual                        = AreYouTheEntity.IAmAnAccountantOrTaxAgent
      val entity                                      = RelatesTo.AnIndividual
      val reasons: Set[WhyAreYouMakingThisDisclosure] = Set(
        WhyAreYouMakingThisDisclosure.DeliberateInaccurateReturn,
        WhyAreYouMakingThisDisclosure.InaccurateReturnNoCare,
        WhyAreYouMakingThisDisclosure.InaccurateReturnWithCare
      )

      val userAnswers = (for {
        uaEntity     <- UserAnswers(userAnswersId, "session-123").set(RelatesToPage, entity)
        uaIndividual <- uaEntity.set(AreYouTheEntityPage, areTheyTheIndividual)
        updatedUa    <- uaIndividual.set(WhyAreYouMakingThisDisclosurePage, reasons)
      } yield updatedUa).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, routes.YouHaveNoOffshoreLiabilitiesController.onPageLoad.url)

      val result = route(application, request).value

      val service = application.injector.instanceOf[OffshoreWhichYearsService]
      val year    = service.getEarliestYearByBehaviour(Behaviour.Deliberate).toString

      val view = application.injector.instanceOf[YouHaveNoOffshoreLiabilitiesView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(entityString, entity, year)(request, messages).toString
    }

    "must return OK and the correct view for a GET for an Entity different from individual" in {

      val entityString                                = "other"
      val areTheyTheIndividual                        = AreYouTheEntity.IAmAnAccountantOrTaxAgent
      val entity                                      = RelatesTo.ACompany
      val reasons: Set[WhyAreYouMakingThisDisclosure] = Set(
        WhyAreYouMakingThisDisclosure.DeliberateInaccurateReturn,
        WhyAreYouMakingThisDisclosure.InaccurateReturnNoCare,
        WhyAreYouMakingThisDisclosure.InaccurateReturnWithCare
      )

      val userAnswers = (for {
        uaEntity     <- UserAnswers(userAnswersId, "session-123").set(RelatesToPage, entity)
        uaIndividual <- uaEntity.set(AreYouTheEntityPage, areTheyTheIndividual)
        updatedUa    <- uaIndividual.set(WhyAreYouMakingThisDisclosurePage, reasons)
      } yield updatedUa).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, routes.YouHaveNoOffshoreLiabilitiesController.onPageLoad.url)

      val result = route(application, request).value

      val service = application.injector.instanceOf[OffshoreWhichYearsService]
      val year    = service.getEarliestYearByBehaviour(Behaviour.Deliberate).toString

      val view = application.injector.instanceOf[YouHaveNoOffshoreLiabilitiesView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(entityString, entity, year)(request, messages).toString
    }

    "must return OK and the correct view with 2022 year for a GET for an Entity different from individual" in {

      val entityString                                = "other"
      val areTheyTheIndividual                        = AreYouTheEntity.IAmAnAccountantOrTaxAgent
      val entity                                      = RelatesTo.ACompany
      val reasons: Set[WhyAreYouMakingThisDisclosure] = Set(
        WhyAreYouMakingThisDisclosure.InaccurateReturnNoCare,
        WhyAreYouMakingThisDisclosure.InaccurateReturnWithCare
      )

      val userAnswers = (for {
        uaEntity     <- UserAnswers(userAnswersId, "session-123").set(RelatesToPage, entity)
        uaIndividual <- uaEntity.set(AreYouTheEntityPage, areTheyTheIndividual)
        updatedUa    <- uaIndividual.set(WhyAreYouMakingThisDisclosurePage, reasons)
      } yield updatedUa).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, routes.YouHaveNoOffshoreLiabilitiesController.onPageLoad.url)

      val result = route(application, request).value

      val service = application.injector.instanceOf[OffshoreWhichYearsService]
      val year    = service.getEarliestYearByBehaviour(Behaviour.Careless).toString

      val view = application.injector.instanceOf[YouHaveNoOffshoreLiabilitiesView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(entityString, entity, year)(request, messages).toString
    }

    "must return OK and the correct view with 2021 year for a GET for an Entity different from individual" in {

      val entityString                                = "other"
      val areTheyTheIndividual                        = AreYouTheEntity.IAmAnAccountantOrTaxAgent
      val entity                                      = RelatesTo.ACompany
      val reasons: Set[WhyAreYouMakingThisDisclosure] = Set(WhyAreYouMakingThisDisclosure.InaccurateReturnWithCare)

      val userAnswers = (for {
        uaEntity     <- UserAnswers(userAnswersId, "session-123").set(RelatesToPage, entity)
        uaIndividual <- uaEntity.set(AreYouTheEntityPage, areTheyTheIndividual)
        updatedUa    <- uaIndividual.set(WhyAreYouMakingThisDisclosurePage, reasons)
      } yield updatedUa).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, routes.YouHaveNoOffshoreLiabilitiesController.onPageLoad.url)

      val result = route(application, request).value

      val service = application.injector.instanceOf[OffshoreWhichYearsService]
      val year    = service.getEarliestYearByBehaviour(Behaviour.ReasonableExcuse).toString

      val view = application.injector.instanceOf[YouHaveNoOffshoreLiabilitiesView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(entityString, entity, year)(request, messages).toString
    }
  }
}
