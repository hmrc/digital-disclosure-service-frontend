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
import forms.ReceivedALetterFormProvider
import models._
import navigation.{FakeNotificationNavigator, NotificationNavigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages._
import pages.notification.IndividualPages
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.notification.ReceivedALetterView

import scala.concurrent.Future

class ReceivedALetterControllerSpec extends SpecBase with MockitoSugar with IndividualPages {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new ReceivedALetterFormProvider()
  val form = formProvider()

  lazy val receivedALetterRoute = notification.routes.ReceivedALetterController.onPageLoad(NormalMode).url
  lazy val receivedALetterRouteCheckMode = notification.routes.ReceivedALetterController.onPageLoad(CheckMode).url

  "ReceivedALetter Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, receivedALetterRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ReceivedALetterView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(ReceivedALetterPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, receivedALetterRoute)

        val view = application.injector.instanceOf[ReceivedALetterView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(true), NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to received a letter screen and then letter reference page (change mode) if received a letter page answer changes from No to Yes in check mode" in {

      val previousAnswer = false
      val newAnswer = true

      val userAnswers = arbitraryUserData.arbitrary.sample.get
        .set(ReceivedALetterPage, previousAnswer).success.value

      val mockSessionRepository = mock[SessionRepository]
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      
      val expectedUserAnswers = userAnswers.set(ReceivedALetterPage, newAnswer).get

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[SessionRepository].toInstance(mockSessionRepository)
        )
        .build()

      val letterReferenceRouteCheckMode = notification.routes.LetterReferenceController.onPageLoad(CheckMode).url

      running(application) {
        val request =
          FakeRequest(POST, receivedALetterRouteCheckMode)
            .withFormUrlEncodedBody(("value", newAnswer.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual letterReferenceRouteCheckMode

        verify(mockSessionRepository, times(1)).set(expectedUserAnswers)
      }
    }

    "must redirect to received a letter screen and clear letter reference page if received a letter page answer changes from Yes to No in check mode" in {

      val previousAnswer = true
      val newAnswer = false

      val userAnswers = arbitraryUserData.arbitrary.sample.get
        .set(ReceivedALetterPage, previousAnswer).success.value

      val mockSessionRepository = mock[SessionRepository]
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val expectedUserAnswers = userAnswers.remove(removeLetterReferencePages).get
        .set(ReceivedALetterPage, newAnswer).get

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[SessionRepository].toInstance(mockSessionRepository)
        )
        .build()

      val checkYourAnswersRoute = notification.routes.CheckYourAnswersController.onPageLoad.url

      running(application) {
        val request =
          FakeRequest(POST, receivedALetterRouteCheckMode)
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
          FakeRequest(POST, receivedALetterRoute)
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
          FakeRequest(POST, receivedALetterRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[ReceivedALetterView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, receivedALetterRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, receivedALetterRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
