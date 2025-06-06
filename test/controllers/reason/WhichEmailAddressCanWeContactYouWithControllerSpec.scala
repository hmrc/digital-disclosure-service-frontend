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
import forms.WhichEmailAddressCanWeContactYouWithFormProvider
import models.{NormalMode, UserAnswers, WhichEmailAddressCanWeContactYouWith}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{WhichEmailAddressCanWeContactYouWithPage, YourEmailAddressPage}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.reason.WhichEmailAddressCanWeContactYouWithView

import scala.concurrent.Future

class WhichEmailAddressCanWeContactYouWithControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val whichEmailAddressCanWeContactYouWithRoute =
    routes.WhichEmailAddressCanWeContactYouWithController.onPageLoad(NormalMode).url

  val formProvider = new WhichEmailAddressCanWeContactYouWithFormProvider()
  val form         = formProvider()

  "WhichEmailAddressCanWeContactYouWith Controller" - {

    "must return OK and the correct view for a GET" in {

      val email       = "test@test.com"
      val userAnswers = UserAnswers("id", "session-123").set(YourEmailAddressPage, email).success.value
      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, whichEmailAddressCanWeContactYouWithRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[WhichEmailAddressCanWeContactYouWithView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode, email)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val email       = "test@test.com"
      val userAnswers = (for {
        ua        <-
          UserAnswers("id", "session-123")
            .set(WhichEmailAddressCanWeContactYouWithPage, WhichEmailAddressCanWeContactYouWith.values.head)
        updatedUa <- ua.set(YourEmailAddressPage, email)
      } yield updatedUa).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, whichEmailAddressCanWeContactYouWithRoute)

      val view = application.injector.instanceOf[WhichEmailAddressCanWeContactYouWithView]

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form.fill(WhichEmailAddressCanWeContactYouWith.values.head),
        NormalMode,
        email
      )(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, whichEmailAddressCanWeContactYouWithRoute)
          .withFormUrlEncodedBody(("value", WhichEmailAddressCanWeContactYouWith.values.head.toString))

      val result = route(applicationWithFakeReasonNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val email       = "test@test.com"
      val userAnswers = (for {
        ua        <-
          UserAnswers("id", "session-123")
            .set(WhichEmailAddressCanWeContactYouWithPage, WhichEmailAddressCanWeContactYouWith.values.head)
        updatedUa <- ua.set(YourEmailAddressPage, email)
      } yield updatedUa).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request =
        FakeRequest(POST, whichEmailAddressCanWeContactYouWithRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[WhichEmailAddressCanWeContactYouWithView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, email)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, whichEmailAddressCanWeContactYouWithRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, whichEmailAddressCanWeContactYouWithRoute)
          .withFormUrlEncodedBody(("value", WhichEmailAddressCanWeContactYouWith.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }
  }
}
