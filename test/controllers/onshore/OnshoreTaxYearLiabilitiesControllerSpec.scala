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
import forms.OnshoreTaxYearLiabilitiesFormProvider
import models.{NormalMode, OnshoreTaxYearLiabilities, OnshoreTaxYearWithLiabilities, OnshoreYearStarting, OnshoreYears, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{OnshoreTaxYearLiabilitiesPage, WhichOnshoreYearsPage}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.onshore.OnshoreTaxYearLiabilitiesView

import scala.concurrent.Future

class OnshoreTaxYearLiabilitiesControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider                  = new OnshoreTaxYearLiabilitiesFormProvider()
  val form                          = formProvider(Set())
  val whichYears: Set[OnshoreYears] = Set(OnshoreYearStarting(2021))
  val userAnswersWithTaxYears       =
    UserAnswers(userAnswersId, "session-123").set(WhichOnshoreYearsPage, whichYears).success.value

  lazy val taxYearLiabilitiesRoute = routes.OnshoreTaxYearLiabilitiesController.onPageLoad(0, NormalMode).url

  "TaxYearLiabilities Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(userAnswersWithTaxYears))

      val request = FakeRequest(GET, taxYearLiabilitiesRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[OnshoreTaxYearLiabilitiesView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode, 0, 2021, Set())(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val answer = OnshoreTaxYearLiabilities(
        nonBusinessIncome = None,
        businessIncome = None,
        lettingIncome = None,
        gains = None,
        unpaidTax = BigInt(200),
        niContributions = BigInt(200),
        interest = BigInt(20),
        penaltyRate = 30,
        penaltyRateReason = "Reason",
        undeclaredIncomeOrGain = Some("Income or gain"),
        residentialTaxReduction = None
      )

      val userAnswers = userAnswersWithTaxYears
        .set(
          OnshoreTaxYearLiabilitiesPage,
          Map("2021" -> OnshoreTaxYearWithLiabilities(OnshoreYearStarting(2021), answer))
        )
        .success
        .value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, taxYearLiabilitiesRoute)

      val view = application.injector.instanceOf[OnshoreTaxYearLiabilitiesView]

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(answer), NormalMode, 0, 2021, Set())(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(userAnswersWithTaxYears))

      val request =
        FakeRequest(POST, taxYearLiabilitiesRoute)
          .withFormUrlEncodedBody(
            ("unpaidTax", "2000"),
            ("interest", "2000"),
            ("niContributions", "2000"),
            ("penaltyRate", "100"),
            ("penaltyRateReason", "Reason"),
            ("undeclaredIncomeOrGain", "Undeclared Income or Gain")
          )

      val result = route(applicationWithFakeOnshoreNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(userAnswersWithTaxYears))

      val request =
        FakeRequest(POST, taxYearLiabilitiesRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[OnshoreTaxYearLiabilitiesView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, 0, 2021, Set())(request, messages).toString
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
}
