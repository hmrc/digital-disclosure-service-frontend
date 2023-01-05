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

package controllers

import base.ControllerSpecBase
import forms.DidThePersonHaveNINOFormProvider
import models.{CheckMode, DidThePersonHaveNINO, NormalMode, UserAnswers}
import navigation.{FakeNotificationNavigator, NotificationNavigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.{DidThePersonHaveNINOPage, WhatWasThePersonNINOPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SessionService
import views.html.notification.DidThePersonHaveNINOView

import scala.concurrent.Future

class DidThePersonHaveNINOControllerSpec extends ControllerSpecBase  {

  def onwardRoute = Call("GET", "/foo")

  lazy val didThePersonHaveNINORoute = notification.routes.DidThePersonHaveNINOController.onPageLoad(NormalMode).url

  val formProvider = new DidThePersonHaveNINOFormProvider()
  val form = formProvider()

  "DidThePersonHaveNINO Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, didThePersonHaveNINORoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[DidThePersonHaveNINOView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(DidThePersonHaveNINOPage, DidThePersonHaveNINO.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, didThePersonHaveNINORoute)

        val view = application.injector.instanceOf[DidThePersonHaveNINOView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(DidThePersonHaveNINO.values.head), NormalMode)(request, messages(application)).toString
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
          FakeRequest(POST, didThePersonHaveNINORoute)
            .withFormUrlEncodedBody(("value", DidThePersonHaveNINO.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must redirect to WhatWasThePersonNINOController screen in check mode if DidThePersonHaveNINO page answer was change from No or YesButDontKnow to YesIKnow" in {
      val previousAnswers = Seq(DidThePersonHaveNINO.No, DidThePersonHaveNINO.YesButDontKnow)
      val newAnswer = DidThePersonHaveNINO.YesIKnow

      val urlToTest = notification.routes.DidThePersonHaveNINOController.onPageLoad(CheckMode).url
      val destinationRoute = notification.routes.WhatWasThePersonNINOController.onPageLoad(CheckMode).url

      previousAnswers.foreach(
        testChangeAnswerRouting(_, newAnswer, DidThePersonHaveNINOPage, urlToTest, destinationRoute)
      )
    }

    "must redirect to CheckYourAnswers screen if the if DidThePersonHaveNINO page answer was change from YesIKnow to No or YesButDontKnow" in {
      val previousAnswer = DidThePersonHaveNINO.YesIKnow
      val newAnswers = Seq(DidThePersonHaveNINO.No, DidThePersonHaveNINO.YesButDontKnow)

      val urlToTest = notification.routes.DidThePersonHaveNINOController.onPageLoad(CheckMode).url
      val destinationRoute = notification.routes.CheckYourAnswersController.onPageLoad.url

      val pageToClean = List(WhatWasThePersonNINOPage)

      newAnswers.foreach(testChangeAnswerRouting(
        previousAnswer, _, DidThePersonHaveNINOPage, urlToTest, destinationRoute, pageToClean)
      )
    }

    "must redirect to CheckYourAnswer screen if there are no changes in the user answer" in {
      val urlToTest = notification.routes.DidThePersonHaveNINOController.onPageLoad(CheckMode).url
      val destinationRoute = notification.routes.CheckYourAnswersController.onPageLoad.url

      DidThePersonHaveNINO.values.foreach(value =>
        testChangeAnswerRouting(value, value, DidThePersonHaveNINOPage, urlToTest, destinationRoute)
      )
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, didThePersonHaveNINORoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[DidThePersonHaveNINOView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, didThePersonHaveNINORoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, didThePersonHaveNINORoute)
            .withFormUrlEncodedBody(("value", DidThePersonHaveNINO.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }
  }
}
