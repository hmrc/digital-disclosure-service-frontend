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

import base.ControllerSpecBase
import forms.WasThePersonRegisteredForVATFormProvider
import models._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages._
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.notification.WasThePersonRegisteredForVATView

import scala.concurrent.Future

class WasThePersonRegisteredForVATControllerSpec extends ControllerSpecBase {

  def onwardRoute = Call("GET", "/foo")

  lazy val wasThePersonRegisteredForVATRoute = routes.WasThePersonRegisteredForVATController.onPageLoad(NormalMode).url

  val formProvider = new WasThePersonRegisteredForVATFormProvider()
  val form         = formProvider()

  "WasThePersonRegisteredForVAT Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, wasThePersonRegisteredForVATRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[WasThePersonRegisteredForVATView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode, false)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId, "session-123")
        .set(WasThePersonRegisteredForVATPage, WasThePersonRegisteredForVAT.values.head)
        .success
        .value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, wasThePersonRegisteredForVATRoute)

      val view = application.injector.instanceOf[WasThePersonRegisteredForVATView]

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(WasThePersonRegisteredForVAT.values.head), NormalMode, false)(
        request,
        messages
      ).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, wasThePersonRegisteredForVATRoute)
          .withFormUrlEncodedBody(("value", WasThePersonRegisteredForVAT.values.head.toString))

      val result = route(applicationWithFakeNotificationNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must redirect to WhatWasThePersonVATRegistrationNumber screen in check mode if WasThePersonRegisteredForVAT page answer was change from No or YesButDontKnow to YesIKnow" in {
      val previousAnswers = Seq(WasThePersonRegisteredForVAT.No, WasThePersonRegisteredForVAT.YesButIDontKnow)
      val newAnswer       = WasThePersonRegisteredForVAT.YesIKnow

      val urlToTest        = routes.WasThePersonRegisteredForVATController.onPageLoad(CheckMode).url
      val destinationRoute = routes.WhatWasThePersonVATRegistrationNumberController.onPageLoad(CheckMode).url

      previousAnswers.foreach(
        testChangeAnswerRouting(_, newAnswer, WasThePersonRegisteredForVATPage, urlToTest, destinationRoute)
      )
    }

    "must redirect to CheckYourAnswers screen if the if WasThePersonRegisteredForVAT page answer was change from YesIKnow to No or YesButDontKnow" in {
      val previousAnswer = WasThePersonRegisteredForVAT.YesIKnow
      val newAnswers     = Seq(WasThePersonRegisteredForVAT.No, WasThePersonRegisteredForVAT.YesButIDontKnow)

      val urlToTest        = routes.WasThePersonRegisteredForVATController.onPageLoad(CheckMode).url
      val destinationRoute = routes.CheckYourAnswersController.onPageLoad.url

      val pageToClean = List(WhatWasThePersonVATRegistrationNumberPage)

      newAnswers.foreach(
        testChangeAnswerRouting(
          previousAnswer,
          _,
          WasThePersonRegisteredForVATPage,
          urlToTest,
          destinationRoute,
          pageToClean
        )
      )
    }

    "must redirect to CheckYourAnswer screen if there are no changes in the user answer" in {
      val urlToTest        = routes.WasThePersonRegisteredForVATController.onPageLoad(CheckMode).url
      val destinationRoute = routes.CheckYourAnswersController.onPageLoad.url

      WasThePersonRegisteredForVAT.values.foreach(value =>
        testChangeAnswerRouting(value, value, WasThePersonRegisteredForVATPage, urlToTest, destinationRoute)
      )
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, wasThePersonRegisteredForVATRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[WasThePersonRegisteredForVATView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, false)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, wasThePersonRegisteredForVATRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, wasThePersonRegisteredForVATRoute)
          .withFormUrlEncodedBody(("value", WasThePersonRegisteredForVAT.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }
  }
}
