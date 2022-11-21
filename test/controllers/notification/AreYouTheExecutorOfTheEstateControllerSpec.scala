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
import forms.AreYouTheExecutorOfTheEstateFormProvider
import models._
import navigation.{FakeNotificationNavigator, NotificationNavigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages._
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SessionService
import views.html.notification.AreYouTheExecutorOfTheEstateView

import scala.concurrent.Future

class AreYouTheExecutorOfTheEstateControllerSpec extends ControllerSpecBase {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new AreYouTheExecutorOfTheEstateFormProvider()
  val form = formProvider()

  lazy val areYouTheExecutorOfTheEstateRoute = notification.routes.AreYouTheExecutorOfTheEstateController.onPageLoad(NormalMode).url

  "AreYouTheExecutorOfTheEstate Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, areYouTheExecutorOfTheEstateRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AreYouTheExecutorOfTheEstateView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(AreYouTheExecutorOfTheEstatePage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, areYouTheExecutorOfTheEstateRoute)

        val view = application.injector.instanceOf[AreYouTheExecutorOfTheEstateView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(true), NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionService = mock[SessionService]

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilderWithSessionService(userAnswers = Some(emptyUserAnswers), mockSessionService)
          .overrides(
            bind[NotificationNavigator].toInstance(new FakeNotificationNavigator(onwardRoute))
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, areYouTheExecutorOfTheEstateRoute)
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
          FakeRequest(POST, areYouTheExecutorOfTheEstateRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[AreYouTheExecutorOfTheEstateView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to Index for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, areYouTheExecutorOfTheEstateRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

    "must redirect to Index for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, areYouTheExecutorOfTheEstateRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

    "must redirect to AreYouRepresentingAnOrganisation screen (change mode) if AreYouTheExecutorOfTheEstate page answer changes from Yes to No" in {

      val previousAnswer = true
      val newAnswer = false

      val urlToTest = notification.routes.AreYouTheExecutorOfTheEstateController.onPageLoad(CheckMode).url
      val destinationRoute = notification.routes.AreYouRepresentingAnOrganisationController.onPageLoad(CheckMode).url

      testChangeAnswerRouting(previousAnswer, newAnswer, AreYouTheExecutorOfTheEstatePage, urlToTest, destinationRoute)
    }

    "must redirect to CheckYourAnswers page if AreYouTheExecutorOfTheEstate page answer changes from No to Yes in check mode" in {

      val previousAnswer = false
      val newAnswer = true

      val urlToTest = notification.routes.AreYouTheExecutorOfTheEstateController.onPageLoad(CheckMode).url
      val destinationRoute = notification.routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, AreYouTheExecutorOfTheEstatePage, urlToTest, destinationRoute, List(AreYouRepresentingAnOrganisationPage, WhatIsTheNameOfTheOrganisationYouRepresentPage))
    }

    "must redirect to CheckYourAnswers page (change mode) if page is true and doesn't change" in {
      val previousAnswer = true
      val newAnswer = true

      val urlToTest = notification.routes.AreYouTheExecutorOfTheEstateController.onPageLoad(CheckMode).url
      val destinationRoute = notification.routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, AreYouTheExecutorOfTheEstatePage, urlToTest, destinationRoute, Nil)
    }

    "must redirect to CheckYourAnswers page (change mode) if page is false and doesn't change" in {
      val previousAnswer = false
      val newAnswer = false

      val urlToTest = notification.routes.AreYouTheExecutorOfTheEstateController.onPageLoad(CheckMode).url
      val destinationRoute = notification.routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, AreYouTheExecutorOfTheEstatePage, urlToTest, destinationRoute, Nil)
    }
  }
}
