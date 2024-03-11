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

package controllers.notification

import base.SpecBase
import forms.HowWouldYouPreferToBeContactedFormProvider
import models.{CheckMode, HowWouldYouPreferToBeContacted, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.HowWouldYouPreferToBeContactedPage
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.notification.HowWouldYouPreferToBeContactedView

import scala.concurrent.Future

class HowWouldYouPreferToBeContactedControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val howWouldYouPreferToBeContactedRoute = routes.HowWouldYouPreferToBeContactedController.onPageLoad(NormalMode).url

  val formProvider = new HowWouldYouPreferToBeContactedFormProvider()
  val form = formProvider()

  "HowWouldYouPreferToBeContacted Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, howWouldYouPreferToBeContactedRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[HowWouldYouPreferToBeContactedView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual view(form, NormalMode, false)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId, "session-123").set(HowWouldYouPreferToBeContactedPage, HowWouldYouPreferToBeContacted.values.toSet).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, howWouldYouPreferToBeContactedRoute)

      val view = application.injector.instanceOf[HowWouldYouPreferToBeContactedView]

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(HowWouldYouPreferToBeContacted.values.toSet), NormalMode, false)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, howWouldYouPreferToBeContactedRoute)
          .withFormUrlEncodedBody(("value[0]", HowWouldYouPreferToBeContacted.values.head.toString))

      val result = route(applicationWithFakeNotificationNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, howWouldYouPreferToBeContactedRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[HowWouldYouPreferToBeContactedView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, false)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, howWouldYouPreferToBeContactedRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to Index for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, howWouldYouPreferToBeContactedRoute)
          .withFormUrlEncodedBody(("value[0]", HowWouldYouPreferToBeContacted.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to YourEmailAddressPage screen in check mode if Email change to selected" in {
      val previousPreferences: Set[HowWouldYouPreferToBeContacted] = Set(HowWouldYouPreferToBeContacted.Telephone)
      val previousAnswers = UserAnswers("id", "session-123").set(HowWouldYouPreferToBeContactedPage, previousPreferences).success.value
      val newAnswer = HowWouldYouPreferToBeContacted.Email

      val urlToTest = routes.HowWouldYouPreferToBeContactedController.onPageLoad(CheckMode).url
      val destinationRoute = routes.YourEmailAddressController.onPageLoad(CheckMode).url
      setupMockSessionResponse(Some(previousAnswers))
      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, urlToTest)
          .withFormUrlEncodedBody(("value[0]", newAnswer.toString))

      val result = route(application, request).value
      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual destinationRoute
    }

    "must redirect to YourPhoneNumber screen in check mode if Telephone change to selected" in {
      val previousPreferences: Set[HowWouldYouPreferToBeContacted] = Set(HowWouldYouPreferToBeContacted.Email)
      val previousAnswers = UserAnswers("id", "session-123").set(HowWouldYouPreferToBeContactedPage, previousPreferences).success.value
      val newAnswer = HowWouldYouPreferToBeContacted.Telephone

      val urlToTest = routes.HowWouldYouPreferToBeContactedController.onPageLoad(CheckMode).url
      val destinationRoute = routes.YourPhoneNumberController.onPageLoad(CheckMode).url
      setupMockSessionResponse(Some(previousAnswers))
      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, urlToTest)
          .withFormUrlEncodedBody(("value[0]", newAnswer.toString))

      val result = route(application, request).value
      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual destinationRoute
    }

    "must redirect to CheckYourAnswer screen if there are no changes in the user answer" in {
      val previousPreferences: Set[HowWouldYouPreferToBeContacted] = Set(HowWouldYouPreferToBeContacted.Email, HowWouldYouPreferToBeContacted.Telephone)
      val previousAnswers = UserAnswers("id", "session-123").set(HowWouldYouPreferToBeContactedPage, previousPreferences).success.value

      val newAnswerEmail: HowWouldYouPreferToBeContacted = HowWouldYouPreferToBeContacted.Email
      val newAnswerTelephone: HowWouldYouPreferToBeContacted = HowWouldYouPreferToBeContacted.Telephone

      val urlToTest = routes.HowWouldYouPreferToBeContactedController.onPageLoad(CheckMode).url
      val destinationRoute = routes.CheckYourAnswersController.onPageLoad.url
      setupMockSessionResponse(Some(previousAnswers))
      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, urlToTest)
          .withFormUrlEncodedBody(("value[0]", newAnswerEmail.toString), ("value[1]", newAnswerTelephone.toString))

      val result = route(application, request).value
      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual destinationRoute
    }
  }
}
