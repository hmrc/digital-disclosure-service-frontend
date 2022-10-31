/*
 * Copyright 2022 HM Revenue & Customs
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
import forms.DoYouHaveAnEmailAddressFormProvider
import models._
import navigation.{FakeNotificationNavigator, NotificationNavigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages._
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.notification.DoYouHaveAnEmailAddressView

import scala.concurrent.Future

class DoYouHaveAnEmailAddressControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new DoYouHaveAnEmailAddressFormProvider()
  val form = formProvider()

  lazy val doYouHaveAnEmailAddressRoute = notification.routes.DoYouHaveAnEmailAddressController.onPageLoad(NormalMode).url
  lazy val doYouHaveAnEmailAddressRouteCheckMode = notification.routes.DoYouHaveAnEmailAddressController.onPageLoad(CheckMode).url

  "DoYouHaveAnEmailAddress Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, doYouHaveAnEmailAddressRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[DoYouHaveAnEmailAddressView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(DoYouHaveAnEmailAddressPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, doYouHaveAnEmailAddressRoute)

        val view = application.injector.instanceOf[DoYouHaveAnEmailAddressView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(true), NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to DoYouHaveAnEmailAddress screen and then YourEmailAddress page (change mode) if DoYouHaveAnEmailAddress page answer changes from No to Yes in check mode" in {

      val previousAnswer = false
      val newAnswer = true

      val userAnswers = arbitraryUserData.arbitrary.sample.get
        .set(DoYouHaveAnEmailAddressPage, previousAnswer).success.value

      val mockSessionRepository = mock[SessionRepository]
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      
      val expectedUserAnswers = userAnswers.set(DoYouHaveAnEmailAddressPage, newAnswer).get

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[SessionRepository].toInstance(mockSessionRepository)
        )
        .build()

      val yourEmailAddressRouteCheckMode = notification.routes.YourEmailAddressController.onPageLoad(CheckMode).url

      running(application) {
        val request =
          FakeRequest(POST, doYouHaveAnEmailAddressRouteCheckMode)
            .withFormUrlEncodedBody(("value", newAnswer.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual yourEmailAddressRouteCheckMode

        verify(mockSessionRepository, times(1)).set(expectedUserAnswers)
      }
    }

    "must redirect to DoYouHaveAnEmailAddress screen and clear YourEmailAddress page if DoYouHaveAnEmailAddress page answer changes from Yes to No in check mode" in {

      val previousAnswer = true
      val newAnswer = false

      val userAnswers = arbitraryUserData.arbitrary.sample.get
        .set(DoYouHaveAnEmailAddressPage, previousAnswer).success.value

      val mockSessionRepository = mock[SessionRepository]
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val expectedUserAnswers = userAnswers.remove(List(YourEmailAddressPage)).get
        .set(DoYouHaveAnEmailAddressPage, newAnswer).get

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[SessionRepository].toInstance(mockSessionRepository)
        )
        .build()

      val checkYourAnswersRoute = notification.routes.CheckYourAnswersController.onPageLoad.url

      running(application) {
        val request =
          FakeRequest(POST, doYouHaveAnEmailAddressRouteCheckMode)
            .withFormUrlEncodedBody(("value", newAnswer.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual checkYourAnswersRoute

        verify(mockSessionRepository, times(1)).set(expectedUserAnswers)
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[NotificationNavigator].toInstance(new FakeNotificationNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, doYouHaveAnEmailAddressRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, doYouHaveAnEmailAddressRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[DoYouHaveAnEmailAddressView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, doYouHaveAnEmailAddressRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, doYouHaveAnEmailAddressRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
