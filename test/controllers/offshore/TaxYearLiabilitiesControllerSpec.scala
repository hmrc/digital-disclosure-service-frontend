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
import forms.TaxYearLiabilitiesFormProvider
import models.{NormalMode, OffshoreYears, TaxYearLiabilities, TaxYearStarting, TaxYearWithLiabilities, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{TaxYearLiabilitiesPage, WhichYearsPage}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.offshore.TaxYearLiabilitiesView

import scala.concurrent.Future

class TaxYearLiabilitiesControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider                   = new TaxYearLiabilitiesFormProvider()
  val form                           = formProvider(true)
  val whichYears: Set[OffshoreYears] = Set(TaxYearStarting(2021))
  val userAnswersWithTaxYears        = UserAnswers(userAnswersId, "session-123").set(WhichYearsPage, whichYears).success.value

  lazy val taxYearLiabilitiesRoute = routes.TaxYearLiabilitiesController.onPageLoad(0, NormalMode).url

  "TaxYearLiabilities Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(userAnswersWithTaxYears))

      val request = FakeRequest(GET, taxYearLiabilitiesRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[TaxYearLiabilitiesView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode, 0, 2021, true)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val answer = TaxYearLiabilities(
        income = BigInt(1000),
        chargeableTransfers = BigInt(100),
        capitalGains = BigInt(1000),
        unpaidTax = BigInt(200),
        interest = BigInt(20),
        penaltyRate = 30,
        penaltyRateReason = "Reason",
        undeclaredIncomeOrGain = Some("Income or gain"),
        foreignTaxCredit = true
      )

      val userAnswers = userAnswersWithTaxYears
        .set(TaxYearLiabilitiesPage, Map("2021" -> TaxYearWithLiabilities(TaxYearStarting(2021), answer)))
        .success
        .value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, taxYearLiabilitiesRoute)

      val view = application.injector.instanceOf[TaxYearLiabilitiesView]

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(answer), NormalMode, 0, 2021, true)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(userAnswersWithTaxYears))

      val request =
        FakeRequest(POST, taxYearLiabilitiesRoute)
          .withFormUrlEncodedBody(
            ("income", "2000"),
            ("chargeableTransfers", "2000"),
            ("capitalGains", "2000"),
            ("unpaidTax", "2000"),
            ("interest", "2000"),
            ("penaltyRate", "100"),
            ("penaltyRateReason", "Reason"),
            ("undeclaredIncomeOrGain", "Undeclared Income or Gain"),
            ("foreignTaxCredit", "true")
          )

      val result = route(applicationWithFakeOffshoreNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(userAnswersWithTaxYears))

      val request =
        FakeRequest(POST, taxYearLiabilitiesRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[TaxYearLiabilitiesView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, 0, 2021, true)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, taxYearLiabilitiesRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to Index for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, taxYearLiabilitiesRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }
  }
  "changedPages logic" - {

    "must remove ForeignTaxCreditPage when foreignTaxCredit changes from true to false" in {
      val controller = application.injector.instanceOf[TaxYearLiabilitiesController]
      val existingAnswer = TaxYearLiabilities(
        income = BigInt(1000), chargeableTransfers = BigInt(100), capitalGains = BigInt(1000),
        unpaidTax = BigInt(200), interest = BigInt(20), penaltyRate = 30,
        penaltyRateReason = "Reason", undeclaredIncomeOrGain = Some("gain"), foreignTaxCredit = true
      )
      val userAnswers = userAnswersWithTaxYears
        .set(TaxYearLiabilitiesPage, Map("2021" -> TaxYearWithLiabilities(TaxYearStarting(2021), existingAnswer)))
        .success.value

      val newAnswer = existingAnswer.copy(foreignTaxCredit = false)
      val (result, changed) = controller.changedPages(userAnswers, "2021", newAnswer)
      result.isSuccess mustBe true
      changed mustBe false
    }

    "must return true when foreignTaxCredit changes from false to true" in {
      val controller = application.injector.instanceOf[TaxYearLiabilitiesController]
      val existingAnswer = TaxYearLiabilities(
        income = BigInt(1000), chargeableTransfers = BigInt(100), capitalGains = BigInt(1000),
        unpaidTax = BigInt(200), interest = BigInt(20), penaltyRate = 30,
        penaltyRateReason = "Reason", undeclaredIncomeOrGain = Some("gain"), foreignTaxCredit = false
      )
      val userAnswers = userAnswersWithTaxYears
        .set(TaxYearLiabilitiesPage, Map("2021" -> TaxYearWithLiabilities(TaxYearStarting(2021), existingAnswer)))
        .success.value

      val newAnswer = existingAnswer.copy(foreignTaxCredit = true)
      val (result, changed) = controller.changedPages(userAnswers, "2021", newAnswer)
      result.isSuccess mustBe true
      changed mustBe true
    }

    "must return false when no existing answer" in {
      val controller = application.injector.instanceOf[TaxYearLiabilitiesController]
      val newAnswer = TaxYearLiabilities(
        income = BigInt(1000), chargeableTransfers = BigInt(100), capitalGains = BigInt(1000),
        unpaidTax = BigInt(200), interest = BigInt(20), penaltyRate = 30,
        penaltyRateReason = "Reason", undeclaredIncomeOrGain = Some("gain"), foreignTaxCredit = false
      )
      val (result, changed) = controller.changedPages(userAnswersWithTaxYears, "2021", newAnswer)
      result.isSuccess mustBe true
      changed mustBe false
    }

    "must redirect to WhichYearsController when index is out of bounds on GET" in {
      setupMockSessionResponse(Some(userAnswersWithTaxYears))
      val request = FakeRequest(GET, routes.TaxYearLiabilitiesController.onPageLoad(99, NormalMode).url)
      val result  = route(application, request).value
      status(result) mustEqual SEE_OTHER
    }

    "must redirect to WhichYearsController when index is out of bounds on POST" in {
      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(userAnswersWithTaxYears))
      val request = FakeRequest(POST, routes.TaxYearLiabilitiesController.onPageLoad(99, NormalMode).url)
        .withFormUrlEncodedBody(("income", "2000"))
      val result  = route(application, request).value
      status(result) mustEqual SEE_OTHER
    }
  }
}