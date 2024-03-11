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

package controllers.onshore

import base.SpecBase
import controllers.onshore.WhichOnshoreYearsController
import forms.WhichOnshoreYearsFormProvider
import models.{Behaviour, CheckMode, NormalMode, OnshoreTaxYearLiabilities, OnshoreTaxYearWithLiabilities, OnshoreYearStarting, OnshoreYears, UserAnswers, WhyAreYouMakingThisOnshoreDisclosure}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{OnshoreTaxYearLiabilitiesPage, WhichOnshoreYearsPage, WhyAreYouMakingThisOnshoreDisclosurePage}
import play.api.i18n.Messages
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.OnshoreWhichYearsService
import views.html.onshore.WhichOnshoreYearsView

import scala.concurrent.Future

class WhichOnshoreYearsControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val whichOnshoreYearsRoute = routes.WhichOnshoreYearsController.onPageLoad(NormalMode).url
  lazy val whichOnshoreYearsCheckModeRoute = routes.WhichOnshoreYearsController.onPageLoad(CheckMode).url

  val formProvider = new WhichOnshoreYearsFormProvider()
  val form = formProvider()

  "determineBehaviour" - {

    val controller = application.injector.instanceOf[WhichOnshoreYearsController]
    implicit val mess: Messages = messages
    val service = application.injector.instanceOf[OnshoreWhichYearsService]

    "return 19" - {

      "when a deliberate behaviour is selected alongside other values" in {
        val userAnswers = UserAnswers(userAnswersId, "session-123").set(WhyAreYouMakingThisOnshoreDisclosurePage, WhyAreYouMakingThisOnshoreDisclosure.values.toSet).success.value
        controller.populateChecklist(userAnswers) mustEqual service.checkboxItems(Behaviour.Deliberate)
      }

      "when only DidNotNotifyNoExcuse is selected" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyNoExcuse)
        val userAnswers = UserAnswers(userAnswersId, "session-123").set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        controller.populateChecklist(userAnswers) mustEqual service.checkboxItems(Behaviour.Deliberate)
      }

      "when only DeliberatelyDidNotNotify is selected" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.DeliberatelyDidNotNotify)
        val userAnswers = UserAnswers(userAnswersId, "session-123").set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        controller.populateChecklist(userAnswers) mustEqual service.checkboxItems(Behaviour.Deliberate)
      }

      "when only DeliberateInaccurateReturn is selected" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.DeliberateInaccurateReturn)
        val userAnswers = UserAnswers(userAnswersId, "session-123").set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        controller.populateChecklist(userAnswers) mustEqual service.checkboxItems(Behaviour.Deliberate)
      }

      "when only DeliberatelyDidNotFile is selected" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.DeliberatelyDidNotFile)
        val userAnswers = UserAnswers(userAnswersId, "session-123").set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        controller.populateChecklist(userAnswers) mustEqual service.checkboxItems(Behaviour.Deliberate)
      }

    }

    "return 5" - {

      "when InaccurateReturnNoCare is selected alongside other values" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(
          WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturnNoCare,
          WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHasExcuse,
          WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturnWithCare,
          WhyAreYouMakingThisOnshoreDisclosure.NotFileHasExcuse
        )
        val userAnswers = UserAnswers(userAnswersId, "session-123").set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        controller.populateChecklist(userAnswers) mustEqual service.checkboxItems(Behaviour.Careless)
      }

      "when only InaccurateReturnNoCare is selected" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturnNoCare)
        val userAnswers = UserAnswers(userAnswersId, "session-123").set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        controller.populateChecklist(userAnswers) mustEqual service.checkboxItems(Behaviour.Careless)
      }

    }

    "return 3" - {

      "when all other values are selected" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(
          WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHasExcuse,
          WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturnWithCare,
          WhyAreYouMakingThisOnshoreDisclosure.NotFileHasExcuse
        )
        val userAnswers = UserAnswers(userAnswersId, "session-123").set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        controller.populateChecklist(userAnswers) mustEqual service.checkboxItems(Behaviour.ReasonableExcuse)
      }

      "when only DidNotNotifyHasExcuse is selected" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHasExcuse)
        val userAnswers = UserAnswers(userAnswersId, "session-123").set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        controller.populateChecklist(userAnswers) mustEqual service.checkboxItems(Behaviour.ReasonableExcuse)
      }

      "when only InaccurateReturnWithCare is selected" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturnWithCare)
        val userAnswers = UserAnswers(userAnswersId, "session-123").set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        controller.populateChecklist(userAnswers) mustEqual service.checkboxItems(Behaviour.ReasonableExcuse)
      }

      "when only NotFileHasExcuse is selected" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.NotFileHasExcuse)
        val userAnswers = UserAnswers(userAnswersId, "session-123").set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        controller.populateChecklist(userAnswers) mustEqual service.checkboxItems(Behaviour.ReasonableExcuse)
      }

    }

  }

  "WhichOnshoreYears Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, whichOnshoreYearsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[WhichOnshoreYearsView]
      val service = application.injector.instanceOf[OnshoreWhichYearsService]
      implicit val mess: Messages = messages

      status(result) mustEqual OK

      contentAsString(result) mustEqual view(form, NormalMode, service.checkboxItems(Behaviour.ReasonableExcuse), false, false)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val previousValue: Set[OnshoreYears] = Set(OnshoreYearStarting(2022))
      val userAnswers = UserAnswers(userAnswersId, "session-123").set(WhichOnshoreYearsPage, previousValue).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, whichOnshoreYearsRoute)

      val view = application.injector.instanceOf[WhichOnshoreYearsView]
      val service = application.injector.instanceOf[OnshoreWhichYearsService]
      implicit val mess: Messages = messages

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(previousValue), NormalMode, service.checkboxItems(Behaviour.ReasonableExcuse), false, false)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, whichOnshoreYearsRoute)
          .withFormUrlEncodedBody(("value[0]", "2022"))

      val result = route(applicationWithFakeOnshoreNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, whichOnshoreYearsRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[WhichOnshoreYearsView]
      val service = application.injector.instanceOf[OnshoreWhichYearsService]
      implicit val mess: Messages = messages

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, service.checkboxItems(Behaviour.ReasonableExcuse), false, false)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, whichOnshoreYearsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to Index for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, whichOnshoreYearsRoute)
          .withFormUrlEncodedBody(("value[0]", "2022"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }
  }

  "WhichOnshoreYears CheckMode Controller" - {
    "must redirect to CheckYourAnswer page if no changes are made" in {
      val year = 2021
      val yearStarting = OnshoreYearStarting(year)
      val previousValue: Set[OnshoreYears] = Set(yearStarting)

      val onshoreTaxYearLiabilities = OnshoreTaxYearLiabilities(
        unpaidTax = BigInt(0),
        niContributions = BigInt(0),
        interest = BigInt(0),
        penaltyRate = 0,
        penaltyRateReason = "reason",
        undeclaredIncomeOrGain = Some("Income or gain"),
        residentialTaxReduction = Some(false)
      )
      val onshoreTaxYearWithLiabilities = OnshoreTaxYearWithLiabilities(taxYear = yearStarting, taxYearLiabilities = onshoreTaxYearLiabilities)
      val userAnswers = UserAnswers(userAnswersId, "session-123").set(WhichOnshoreYearsPage, previousValue).success.value
        .set(OnshoreTaxYearLiabilitiesPage, Map(year.toString -> onshoreTaxYearWithLiabilities)).success.value

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(userAnswers))

      val request =
        FakeRequest(POST, whichOnshoreYearsCheckModeRoute)
          .withFormUrlEncodedBody(("value[0]", year.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.CheckYourAnswersController.onPageLoad.url
    }

    "must redirect to OnshoreTaxYearLiabilitiesController if changes are made and there are no missing year or no 'before certain year' selected" in {
      val previousYear = 2021
      val newYear = 2022

      val yearStarting = OnshoreYearStarting(previousYear)
      val previousValue: Set[OnshoreYears] = Set(yearStarting)

      val onshoreTaxYearLiabilities = OnshoreTaxYearLiabilities(
        unpaidTax = BigInt(0),
        niContributions = BigInt(0),
        interest = BigInt(0),
        penaltyRate = 0,
        penaltyRateReason = "reason",
        undeclaredIncomeOrGain = Some("Income or gain"),
        residentialTaxReduction = Some(false)
      )
      val onshoreTaxYearWithLiabilities = OnshoreTaxYearWithLiabilities(taxYear = yearStarting, taxYearLiabilities = onshoreTaxYearLiabilities)
      val userAnswers = UserAnswers(userAnswersId, "session-123").set(WhichOnshoreYearsPage, previousValue).success.value
        .set(OnshoreTaxYearLiabilitiesPage, Map(previousYear.toString -> onshoreTaxYearWithLiabilities)).success.value

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(userAnswers))

      val request =
        FakeRequest(POST, whichOnshoreYearsCheckModeRoute)
          .withFormUrlEncodedBody(("value[0]", newYear.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.OnshoreTaxYearLiabilitiesController.onPageLoad(0, NormalMode).url
     }

    "must redirect to NotIncludedSingleTaxYearController if changes are made and there is one missing year and no 'before certain year' selected" in {
      val previousYears1 = 2022
      val previousYears2 = 2021
      val newYear2 = 2020

      val yearStarting1 = OnshoreYearStarting(previousYears1)
      val yearStarting2 = OnshoreYearStarting(previousYears2)
      val previousValue: Set[OnshoreYears] = Set(yearStarting1, yearStarting2)

      val onshoreTaxYearLiabilities = OnshoreTaxYearLiabilities(
        unpaidTax = BigInt(0),
        niContributions = BigInt(0),
        interest = BigInt(0),
        penaltyRate = 0,
        penaltyRateReason = "reason",
        undeclaredIncomeOrGain = Some("Income or gain"),
        residentialTaxReduction = Some(false)
      )

      val onshoreTaxYearWithLiabilities1 = OnshoreTaxYearWithLiabilities(taxYear = yearStarting1, taxYearLiabilities = onshoreTaxYearLiabilities)
      val onshoreTaxYearWithLiabilities2 = OnshoreTaxYearWithLiabilities(taxYear = yearStarting2, taxYearLiabilities = onshoreTaxYearLiabilities)
      val userAnswers = UserAnswers(userAnswersId, "session-123").set(WhichOnshoreYearsPage, previousValue).success.value
        .set(OnshoreTaxYearLiabilitiesPage, Map(
          previousYears1.toString -> onshoreTaxYearWithLiabilities1,
          previousYears2.toString -> onshoreTaxYearWithLiabilities2
        )).success.value

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(userAnswers))

      val request =
        FakeRequest(POST, whichOnshoreYearsCheckModeRoute)
          .withFormUrlEncodedBody(("value[0]", previousYears1.toString), ("value[1]", newYear2.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.NotIncludedSingleTaxYearController.onPageLoad(NormalMode).url
    }


    "must redirect to TaxBeforeThreeYearsOnshoreController if changes are made and 'before three year' is selected" in {
      val previousYears1 = 2022
      val previousYears2 = 2021
      val newYear2 = "priorToThreeYears"

      val yearStarting1 = OnshoreYearStarting(previousYears1)
      val yearStarting2 = OnshoreYearStarting(previousYears2)
      val previousValue: Set[OnshoreYears] = Set(yearStarting1, yearStarting2)

      val onshoreTaxYearLiabilities = OnshoreTaxYearLiabilities(
        unpaidTax = BigInt(0),
        niContributions = BigInt(0),
        interest = BigInt(0),
        penaltyRate = 0,
        penaltyRateReason = "reason",
        undeclaredIncomeOrGain = Some("Income or gain"),
        residentialTaxReduction = Some(false)
      )

      val onshoreTaxYearWithLiabilities1 = OnshoreTaxYearWithLiabilities(taxYear = yearStarting1, taxYearLiabilities = onshoreTaxYearLiabilities)
      val onshoreTaxYearWithLiabilities2 = OnshoreTaxYearWithLiabilities(taxYear = yearStarting2, taxYearLiabilities = onshoreTaxYearLiabilities)
      val userAnswers = UserAnswers(userAnswersId, "session-123").set(WhichOnshoreYearsPage, previousValue).success.value
        .set(OnshoreTaxYearLiabilitiesPage, Map(
          previousYears1.toString -> onshoreTaxYearWithLiabilities1,
          previousYears2.toString -> onshoreTaxYearWithLiabilities2
        )).success.value

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(userAnswers))

      val request =
        FakeRequest(POST, whichOnshoreYearsCheckModeRoute)
          .withFormUrlEncodedBody(
            ("value[0]", previousYears1.toString),
            ("value[1]", newYear2)
      )

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.TaxBeforeThreeYearsOnshoreController.onPageLoad(NormalMode).url
    }

    "must redirect to TaxBeforeFiveYearsOnshoreController if changes are made and 'before five year' is selected" in {
      val previousYears1 = 2022
      val previousYears2 = 2021
      val newYear2 = "priorToFiveYears"

      val yearStarting1 = OnshoreYearStarting(previousYears1)
      val yearStarting2 = OnshoreYearStarting(previousYears2)
      val previousValue: Set[OnshoreYears] = Set(yearStarting1, yearStarting2)

      val onshoreTaxYearLiabilities = OnshoreTaxYearLiabilities(
        unpaidTax = BigInt(0),
        niContributions = BigInt(0),
        interest = BigInt(0),
        penaltyRate = 0,
        penaltyRateReason = "reason",
        undeclaredIncomeOrGain = Some("Income or gain"),
        residentialTaxReduction = Some(false)
      )

      val onshoreTaxYearWithLiabilities1 = OnshoreTaxYearWithLiabilities(taxYear = yearStarting1, taxYearLiabilities = onshoreTaxYearLiabilities)
      val onshoreTaxYearWithLiabilities2 = OnshoreTaxYearWithLiabilities(taxYear = yearStarting2, taxYearLiabilities = onshoreTaxYearLiabilities)
      val userAnswers = UserAnswers(userAnswersId, "session-123").set(WhichOnshoreYearsPage, previousValue).success.value
        .set(OnshoreTaxYearLiabilitiesPage, Map(
          previousYears1.toString -> onshoreTaxYearWithLiabilities1,
          previousYears2.toString -> onshoreTaxYearWithLiabilities2
        )).success.value

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(userAnswers))

      val request =
        FakeRequest(POST, whichOnshoreYearsCheckModeRoute)
          .withFormUrlEncodedBody(
            ("value[0]", previousYears1.toString),
            ("value[1]", newYear2)
          )

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.TaxBeforeFiveYearsOnshoreController.onPageLoad(NormalMode).url
    }

    "must redirect to TaxBeforeNineteenYearsOnshoreController if changes are made and 'before nineteen year' is selected" in {
      val previousYears1 = 2022
      val previousYears2 = 2021
      val newYear2 = "priorToNineteenYears"

      val yearStarting1 = OnshoreYearStarting(previousYears1)
      val yearStarting2 = OnshoreYearStarting(previousYears2)
      val previousValue: Set[OnshoreYears] = Set(yearStarting1, yearStarting2)

      val onshoreTaxYearLiabilities = OnshoreTaxYearLiabilities(
        unpaidTax = BigInt(0),
        niContributions = BigInt(0),
        interest = BigInt(0),
        penaltyRate = 0,
        penaltyRateReason = "reason",
        undeclaredIncomeOrGain = Some("Income or gain"),
        residentialTaxReduction = Some(false)
      )

      val onshoreTaxYearWithLiabilities1 = OnshoreTaxYearWithLiabilities(taxYear = yearStarting1, taxYearLiabilities = onshoreTaxYearLiabilities)
      val onshoreTaxYearWithLiabilities2 = OnshoreTaxYearWithLiabilities(taxYear = yearStarting2, taxYearLiabilities = onshoreTaxYearLiabilities)
      val userAnswers = UserAnswers(userAnswersId, "session-123").set(WhichOnshoreYearsPage, previousValue).success.value
        .set(OnshoreTaxYearLiabilitiesPage, Map(
          previousYears1.toString -> onshoreTaxYearWithLiabilities1,
          previousYears2.toString -> onshoreTaxYearWithLiabilities2
        )).success.value

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(userAnswers))

      val request =
        FakeRequest(POST, whichOnshoreYearsCheckModeRoute)
          .withFormUrlEncodedBody(
            ("value[0]", previousYears1.toString),
            ("value[1]", newYear2)
          )

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.TaxBeforeNineteenYearsOnshoreController.onPageLoad(NormalMode).url
    }
  }
}
