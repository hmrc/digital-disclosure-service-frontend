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
import forms.ForeignTaxCreditFormProvider
import models.{NormalMode, OffshoreYears, TaxYearStarting, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{ForeignTaxCreditPage, WhichYearsPage}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.offshore.ForeignTaxCreditView

import scala.concurrent.Future

class ForeignTaxCreditControllerSpec extends SpecBase with MockitoSugar {

  val formProvider = new ForeignTaxCreditFormProvider()
  val form = formProvider()

  def onwardRoute = Call("GET", "/foo")

  val validAnswer = BigInt(0)

  val whichYears: Set[OffshoreYears] = Set(TaxYearStarting(2021))
  val userAnswersWithTaxYears = UserAnswers(userAnswersId, "session-123").set(WhichYearsPage, whichYears).success.value

  lazy val foreignTaxCreditRoute = routes.ForeignTaxCreditController.onPageLoad(0, NormalMode).url

  "ForeignTaxCredit Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(userAnswersWithTaxYears))

      val request = FakeRequest(GET, foreignTaxCreditRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ForeignTaxCreditView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, 0, "2022", NormalMode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = userAnswersWithTaxYears.set(ForeignTaxCreditPage, Map("2021" -> validAnswer)).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, foreignTaxCreditRoute)

      val view = application.injector.instanceOf[ForeignTaxCreditView]

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(validAnswer), 0, "2022", NormalMode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(userAnswersWithTaxYears))

      val request =
        FakeRequest(POST, foreignTaxCreditRoute)
          .withFormUrlEncodedBody(("value", validAnswer.toString))

      val result = route(applicationWithFakeOffshoreNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(userAnswersWithTaxYears))

      val request =
        FakeRequest(POST, foreignTaxCreditRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[ForeignTaxCreditView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm,  0, "2022", NormalMode)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, foreignTaxCreditRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to Index for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, foreignTaxCreditRoute)
          .withFormUrlEncodedBody(("value", validAnswer.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }
  }
}
