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
import forms.WhichTelephoneNumberCanWeContactYouWithFormProvider
import models.{NormalMode, UserAnswers, WhichTelephoneNumberCanWeContactYouWith}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{WhichTelephoneNumberCanWeContactYouWithPage, YourPhoneNumberPage}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.reason.WhichTelephoneNumberCanWeContactYouWithView

import scala.concurrent.Future

class WhichTelephoneNumberCanWeContactYouWithControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val whichTelephoneNumberCanWeContactYouWithRoute = routes.WhichTelephoneNumberCanWeContactYouWithController.onPageLoad(NormalMode).url

  val formProvider = new WhichTelephoneNumberCanWeContactYouWithFormProvider()
  val form = formProvider()

  "WhichTelephoneNumberCanWeContactYouWith Controller" - {

    "must return OK and the correct view for a GET" in {

      val telephoneNumber = "07777 777777"
      val userAnswers = UserAnswers("id", "session-123").set(YourPhoneNumberPage, telephoneNumber).success.value
      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, whichTelephoneNumberCanWeContactYouWithRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[WhichTelephoneNumberCanWeContactYouWithView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode, telephoneNumber)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val telephoneNumber = "07777 777777"
      val userAnswers = (for {
        ua <- UserAnswers("id", "session-123").set(WhichTelephoneNumberCanWeContactYouWithPage, WhichTelephoneNumberCanWeContactYouWith.values.head)
        updatedUa <- ua.set(YourPhoneNumberPage, telephoneNumber)  
      } yield updatedUa).success.value  

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, whichTelephoneNumberCanWeContactYouWithRoute)

      val view = application.injector.instanceOf[WhichTelephoneNumberCanWeContactYouWithView]

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(WhichTelephoneNumberCanWeContactYouWith.values.head), NormalMode, telephoneNumber)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, whichTelephoneNumberCanWeContactYouWithRoute)
          .withFormUrlEncodedBody(("value", WhichTelephoneNumberCanWeContactYouWith.values.head.toString))

      val result = route(applicationWithFakeReasonNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val telephoneNumber = "07777 777777"
      val userAnswers = (for {
        ua <- UserAnswers("id", "session-123").set(WhichTelephoneNumberCanWeContactYouWithPage, WhichTelephoneNumberCanWeContactYouWith.values.head)
        updatedUa <- ua.set(YourPhoneNumberPage, telephoneNumber)  
      } yield updatedUa).success.value 

      setupMockSessionResponse(Some(userAnswers))

      val request =
        FakeRequest(POST, whichTelephoneNumberCanWeContactYouWithRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[WhichTelephoneNumberCanWeContactYouWithView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, telephoneNumber)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, whichTelephoneNumberCanWeContactYouWithRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, whichTelephoneNumberCanWeContactYouWithRoute)
          .withFormUrlEncodedBody(("value", WhichTelephoneNumberCanWeContactYouWith.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }
  }
}
