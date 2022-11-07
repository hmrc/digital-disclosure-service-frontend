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
import forms.AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutFormProvider
import models.{NormalMode, AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAbout, UserAnswers}
import navigation.{FakeNotificationNavigator, NotificationNavigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.notification.AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutView

import scala.concurrent.Future

class AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val areYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutRoute = notification.routes.AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutController.onPageLoad(NormalMode).url

  val formProvider = new AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutFormProvider()
  val form = formProvider()

  "AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAbout Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, areYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage, AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAbout.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, areYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutRoute)

        val view = application.injector.instanceOf[AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAbout.values.head), NormalMode)(request, messages(application)).toString
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
          FakeRequest(POST, areYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutRoute)
            .withFormUrlEncodedBody(("value", AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAbout.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, areYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, areYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, areYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutRoute)
            .withFormUrlEncodedBody(("value", AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAbout.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
