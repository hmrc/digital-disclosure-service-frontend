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

import base.SpecBase
import forms.NotificationStartedFormProvider
import models.{NotificationStarted, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.NotificationStartedView

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDate, ZoneOffset}
import scala.concurrent.Future

class NotificationStartedControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute: Call = Call("GET", "/foo")

  lazy val notificationStartedRoute: String = routes.NotificationStartedController.onPageLoad.url

  val formProvider                    = new NotificationStartedFormProvider()
  val form: Form[NotificationStarted] = formProvider()

  val instant: Instant                 = Instant.now()
  val date: LocalDate                  = instant.atZone(ZoneOffset.UTC).toLocalDate
  val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
  val formattedDate: String            = date.format(dateFormatter)

  val userAnswers: UserAnswers      = emptyUserAnswers.copy(lastUpdated = instant)
  val view: NotificationStartedView = application.injector.instanceOf[NotificationStartedView]

  "NotificationStarted Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, notificationStartedRoute)

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, formattedDate)(request, messages).toString
    }

    "must redirect to the next page when Disclosure is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, notificationStartedRoute)
          .withFormUrlEncodedBody(("value", NotificationStarted.Disclosure.toString))

      val result = route(applicationWithFakeNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must redirect to the next page when Continue is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, notificationStartedRoute)
          .withFormUrlEncodedBody(("value", NotificationStarted.Continue.toString))

      val result = route(applicationWithFakeNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(userAnswers))

      val request =
        FakeRequest(POST, notificationStartedRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, formattedDate)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, notificationStartedRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, notificationStartedRoute)
          .withFormUrlEncodedBody(("value", NotificationStarted.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
    }
  }
}
