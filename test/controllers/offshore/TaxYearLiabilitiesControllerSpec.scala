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
import forms.TaxYearLiabilitiesFormProvider
import models.{TaxYearWithLiabilities, OffshoreYears, TaxYearLiabilities, TaxYearStarting, NormalMode, UserAnswers}
import navigation.{FakeOffshoreNavigator, OffshoreNavigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{WhichYearsPage, TaxYearLiabilitiesPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SessionService
import views.html.offshore.TaxYearLiabilitiesView

import scala.concurrent.Future

class TaxYearLiabilitiesControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new TaxYearLiabilitiesFormProvider()
  val form = formProvider()
  val whichYears: Set[OffshoreYears] = Set(TaxYearStarting(2021))
  val userAnswersWithTaxYears = UserAnswers(userAnswersId).set(WhichYearsPage, whichYears).success.value

  lazy val taxYearLiabilitiesRoute = offshore.routes.TaxYearLiabilitiesController.onPageLoad(0, NormalMode).url

  "TaxYearLiabilities Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(userAnswersWithTaxYears)).build()

      running(application) {
        val request = FakeRequest(GET, taxYearLiabilitiesRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TaxYearLiabilitiesView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, 0, 2021)(request, messages(application)).toString
      }
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
        foreignTaxCredit = true
      )

      val userAnswers = userAnswersWithTaxYears.set(TaxYearLiabilitiesPage, Map("2021" -> TaxYearWithLiabilities(TaxYearStarting(2021), answer))).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, taxYearLiabilitiesRoute)

        val view = application.injector.instanceOf[TaxYearLiabilitiesView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(answer), NormalMode, 0, 2021)(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionService = mock[SessionService]

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilderWithSessionService(userAnswers = Some(userAnswersWithTaxYears), mockSessionService)
          .overrides(
            bind[OffshoreNavigator].toInstance(new FakeOffshoreNavigator(onwardRoute))
          )
          .build()

      running(application) {
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
              ("foreignTaxCredit", "true")
            )

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(userAnswersWithTaxYears)).build()

      running(application) {
        val request =
          FakeRequest(POST, taxYearLiabilitiesRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[TaxYearLiabilitiesView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, 0, 2021)(request, messages(application)).toString
      }
    }

    "must redirect to Index for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, taxYearLiabilitiesRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

    "must redirect to Index for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, taxYearLiabilitiesRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }
  }
}
