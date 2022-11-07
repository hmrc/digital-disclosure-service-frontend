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

import base.ControllerSpecBase
import forms.AreYouRepresentingAnOrganisationFormProvider
import models.{CheckMode, NormalMode, UserAnswers}
import navigation.{FakeNotificationNavigator, NotificationNavigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.{AreYouRepresentingAnOrganisationPage, WhatIsTheNameOfTheOrganisationYouRepresentPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.notification.AreYouRepresentingAnOrganisationView

import scala.concurrent.Future

class AreYouRepresentingAnOrganisationControllerSpec extends ControllerSpecBase {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new AreYouRepresentingAnOrganisationFormProvider()
  val form = formProvider()

  lazy val areYouRepresentingAnOrganisationRoute = notification.routes.AreYouRepresentingAnOrganisationController.onPageLoad(NormalMode).url

  "AreYouRepresentingAnOrganisation Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, areYouRepresentingAnOrganisationRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AreYouRepresentingAnOrganisationView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(AreYouRepresentingAnOrganisationPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, areYouRepresentingAnOrganisationRoute)

        val view = application.injector.instanceOf[AreYouRepresentingAnOrganisationView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(true), NormalMode)(request, messages(application)).toString
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
          FakeRequest(POST, areYouRepresentingAnOrganisationRoute)
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
          FakeRequest(POST, areYouRepresentingAnOrganisationRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[AreYouRepresentingAnOrganisationView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, areYouRepresentingAnOrganisationRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, areYouRepresentingAnOrganisationRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to WhatIsTheNameOfTheOrganisationYouRepresent page (change mode) if page answer changes from No to Yes in check mode" in {
      val previousAnswer = false
      val newAnswer = true

      val urlToTest = notification.routes.AreYouRepresentingAnOrganisationController.onPageLoad(CheckMode).url
      val destinationRoute = notification.routes.WhatIsTheNameOfTheOrganisationYouRepresentController.onPageLoad(CheckMode).url

      testChangeAnswerRouting(previousAnswer, newAnswer, AreYouRepresentingAnOrganisationPage, urlToTest, destinationRoute, Nil)
    }

    "must redirect to CheckYourAnswers page (change mode) and clean WhatIsTheNameOfTheOrganisationYouRepresentPage if page answer changes from Yes to No in check mode" in {
      val previousAnswer = true
      val newAnswer = false

      val urlToTest = notification.routes.AreYouRepresentingAnOrganisationController.onPageLoad(CheckMode).url
      val destinationRoute = notification.routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, AreYouRepresentingAnOrganisationPage, urlToTest, destinationRoute, List(WhatIsTheNameOfTheOrganisationYouRepresentPage))
    }

    "must redirect to CheckYourAnswers page (change mode) if page is true and doesn't change" in {
      val previousAnswer = true
      val newAnswer = true

      val urlToTest = notification.routes.AreYouRepresentingAnOrganisationController.onPageLoad(CheckMode).url
      val destinationRoute = notification.routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, AreYouRepresentingAnOrganisationPage, urlToTest, destinationRoute, Nil)
    }

    "must redirect to CheckYourAnswers page (change mode) if page is false and doesn't change" in {
      val previousAnswer = false
      val newAnswer = false

      val urlToTest = notification.routes.AreYouRepresentingAnOrganisationController.onPageLoad(CheckMode).url
      val destinationRoute = notification.routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, AreYouRepresentingAnOrganisationPage, urlToTest, destinationRoute, Nil)
    }
  }
}
