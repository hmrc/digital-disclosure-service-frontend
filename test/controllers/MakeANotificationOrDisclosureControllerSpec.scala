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
import forms.MakeANotificationOrDisclosureFormProvider
import models.audit._
import models.{MakeANotificationOrDisclosure, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.{any, refEq}
import org.mockito.Mockito.{verify, when}
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AuditService
import views.html.MakeANotificationOrDisclosureView

import scala.concurrent.Future

class MakeANotificationOrDisclosureControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute: Call = Call("GET", "/foo")

  lazy val makeANotificationOrDisclosureRoute: String = routes.MakeANotificationOrDisclosureController.onPageLoad.url

  val formProvider = new MakeANotificationOrDisclosureFormProvider()
  val form: Form[MakeANotificationOrDisclosure] = formProvider()
  val view: MakeANotificationOrDisclosureView = application.injector.instanceOf[MakeANotificationOrDisclosureView]

  "MakeANotificationOrDisclosure Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, makeANotificationOrDisclosureRoute)

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, makeANotificationOrDisclosureRoute)
          .withFormUrlEncodedBody(("value", MakeANotificationOrDisclosure.values.head.toString))

      val result = route(applicationWithFakeNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must send a NotificationStart audit event when a notification is started" in {

      val mockAuditService = mock[AuditService]

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val expectedAuditEvent = NotificationStart (
        userId = "id",
        submissionId = UserAnswers.defaultSubmissionId,
        isAgent = false,
        agentReference = None
      )

      val applicationWithFakeNavigator = applicationBuilder
        .overrides(
          bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
          bind[AuditService].toInstance(mockAuditService)
        ).build()

      val request =
        FakeRequest(POST, makeANotificationOrDisclosureRoute)
          .withFormUrlEncodedBody(("value", MakeANotificationOrDisclosure.MakeANotification.toString))

      val result = route(applicationWithFakeNavigator, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
      verify(mockAuditService).auditNotificationStart(refEq(expectedAuditEvent))(any())
    }

    "must send a DisclosureStart audit event when a disclosure is started" in {

      val mockAuditService = mock[AuditService]

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val expectedAuditEvent = DisclosureStart (
        userId = "id",
        submissionId = UserAnswers.defaultSubmissionId,
        isAgent = false,
        agentReference = None,
        notificationSubmitted = false
      )

      val applicationWithFakeNavigator = applicationBuilder
        .overrides(
          bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
          bind[AuditService].toInstance(mockAuditService)
        ).build()

      val request =
        FakeRequest(POST, makeANotificationOrDisclosureRoute)
          .withFormUrlEncodedBody(("value", MakeANotificationOrDisclosure.MakeADisclosure.toString))

      val result = route(applicationWithFakeNavigator, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
      verify(mockAuditService).auditDisclosureStart(refEq(expectedAuditEvent))(any())
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, makeANotificationOrDisclosureRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm)(request, messages).toString
    }
  }
}
