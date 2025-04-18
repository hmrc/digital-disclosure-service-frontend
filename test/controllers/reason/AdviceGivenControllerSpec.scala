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

package controllers.reason

import base.SpecBase
import forms.AdviceGivenFormProvider
import models.{AdviceContactPreference, AdviceGiven, MonthYear, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.AdviceGivenPage
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.reason.AdviceGivenView

import scala.concurrent.Future

class AdviceGivenControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new AdviceGivenFormProvider()
  val form         = formProvider()

  lazy val adviceGivenRoute = routes.AdviceGivenController.onPageLoad(NormalMode).url

  val adviceGiven = AdviceGiven("Advice", MonthYear(12, 2021), AdviceContactPreference.Email)
  val userAnswers = UserAnswers(userAnswersId, "session-123").set(AdviceGivenPage, adviceGiven).success.value

  "AdviceGiven Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, adviceGivenRoute)

      val view = application.injector.instanceOf[AdviceGivenView]

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, adviceGivenRoute)

      val view = application.injector.instanceOf[AdviceGivenView]

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(adviceGiven), NormalMode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, adviceGivenRoute)
          .withFormUrlEncodedBody(
            ("adviceGiven", "value 1"),
            ("date.month", "12"),
            ("date.year", "2012"),
            ("contact", "email")
          )

      val result = route(applicationWithFakeReasonNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, adviceGivenRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[AdviceGivenView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, adviceGivenRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to Index for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, adviceGivenRoute)
          .withFormUrlEncodedBody(("adviceGiven", "value 1"), ("field2", "value 2"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }
  }
}
