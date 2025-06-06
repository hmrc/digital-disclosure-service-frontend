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
import forms.HowMuchTaxHasNotBeenIncludedFormProvider
import models.{HowMuchTaxHasNotBeenIncluded, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.HowMuchTaxHasNotBeenIncludedPage
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.offshore.HowMuchTaxHasNotBeenIncludedView

import scala.concurrent.Future

class HowMuchTaxHasNotBeenIncludedControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val howMuchTaxHasNotBeenIncludedRoute = routes.HowMuchTaxHasNotBeenIncludedController.onPageLoad(NormalMode).url

  val formProvider = new HowMuchTaxHasNotBeenIncludedFormProvider()
  val form         = formProvider()

  "HowMuchTaxHasNotBeenIncluded Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, howMuchTaxHasNotBeenIncludedRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[HowMuchTaxHasNotBeenIncludedView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId, "session-123")
        .set(HowMuchTaxHasNotBeenIncludedPage, HowMuchTaxHasNotBeenIncluded.values.head)
        .success
        .value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, howMuchTaxHasNotBeenIncludedRoute)

      val view = application.injector.instanceOf[HowMuchTaxHasNotBeenIncludedView]

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(HowMuchTaxHasNotBeenIncluded.values.head), NormalMode)(
        request,
        messages
      ).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, howMuchTaxHasNotBeenIncludedRoute)
          .withFormUrlEncodedBody(("value", HowMuchTaxHasNotBeenIncluded.values.head.toString))

      val result = route(applicationWithFakeOffshoreNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, howMuchTaxHasNotBeenIncludedRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[HowMuchTaxHasNotBeenIncludedView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, howMuchTaxHasNotBeenIncludedRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, howMuchTaxHasNotBeenIncludedRoute)
          .withFormUrlEncodedBody(("value", HowMuchTaxHasNotBeenIncluded.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }
  }
}
