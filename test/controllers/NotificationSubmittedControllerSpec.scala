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
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.NotificationSubmittedView
import models.{SubmissionType, UserAnswers}
import models.store.Metadata
import models.audit.DisclosureStart

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import pages._
import play.api.mvc.Call
import navigation.{FakeNavigator, Navigator}
import play.api.inject.bind
import org.mockito.Mockito.{verify, when}
import org.mockito.ArgumentMatchers.{any, refEq}
import org.scalatestplus.mockito.MockitoSugar
import services.AuditService

import scala.concurrent.Future

class NotificationSubmittedControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute: Call = Call("GET", "/foo")

  "onPageLoad" - {

    "must return OK and the correct view for a GET when a notification has been submitted" in {

      val reference = "1234"
      val time = LocalDateTime.now()
      val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
      val formattedDate = time.format(dateFormatter)

      val userAnswers = UserAnswers(
        id = "id",
        sessionId = "session-123",
        submissionId = "id2",
        submissionType = SubmissionType.Notification,
        metadata = Metadata(Some(reference), Some(time))
      )

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, routes.NotificationSubmittedController.onPageLoad.url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[NotificationSubmittedView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(formattedDate, reference)(request, messages).toString
    }

    "must redirect to the index page where the stored submission is not a submitted notification" in {

      val reference = "1234"
      val time = LocalDateTime.now()

      val userAnswers = UserAnswers(
        id = "id",
        sessionId = "session-123",
        submissionId = "id2",
        submissionType = SubmissionType.Disclosure,
        metadata = Metadata(Some(reference), Some(time))
      )

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, routes.NotificationSubmittedController.onPageLoad.url)

      val result = route(applicationWithFakeNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }
  }

  "onSubmit" - {
    "must redirect to the next page" in {

      val mockAuditService = mock[AuditService]

      val reference = "1234"
      val time = LocalDateTime.now()

      val userAnswers = UserAnswers(
        id = "id",
        sessionId = "session-123",
        submissionId = "id2",
        submissionType = SubmissionType.Notification,
        metadata = Metadata(Some(reference), Some(time))
      )

      val expectedAuditEvent = DisclosureStart(
        userId = "id",
        submissionId = "id2",
        isAgent = false,
        agentReference = None,
        notificationSubmitted = true
      )

      setupMockSessionResponse(Some(userAnswers))
      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)

      val applicationWithFakeNavigator = applicationBuilder.overrides(
        bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
        bind[AuditService].toInstance(mockAuditService)
      ).build()

      val request = FakeRequest(POST, routes.NotificationSubmittedController.onSubmit.url)

      val result = route(applicationWithFakeNavigator, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
      verify(mockAuditService).auditDisclosureStart(refEq(expectedAuditEvent))(any())
    }
  }

  "convertToDisclosure" - {
    "must convert a notification to a disclosure, clearing metadata" in {
      val reference = "1234"
      val time = LocalDateTime.now()
      val userAnswers = UserAnswers(id = "id",
        sessionId = "session-123",
        submissionId = "id2",
        submissionType = SubmissionType.Notification,
        metadata = Metadata(Some(reference), Some(time))
      )
      val controller = application.injector.instanceOf[NotificationSubmittedController]

      val resultUA = controller.convertToDisclosure(userAnswers).success.value
      resultUA.metadata mustEqual Metadata()
      resultUA.submissionType mustEqual SubmissionType.Disclosure
    }

    "must convert a notification to a disclosure, clearing metadata and populating the case reference section where letterReferencePage is populated" in {
      val reference = "1234"
      val time = LocalDateTime.now()
      val userAnswers = UserAnswers(id = "id",
        sessionId = "session-123",
        submissionId = "id2",
        submissionType = SubmissionType.Notification,
        metadata = Metadata(Some(reference), Some(time))
      )
      val uaWithLetterRef = userAnswers.set(LetterReferencePage, "Some ref").success.value
      val controller = application.injector.instanceOf[NotificationSubmittedController]

      val resultUA = controller.convertToDisclosure(uaWithLetterRef).success.value
      resultUA.metadata mustEqual Metadata()
      resultUA.submissionType mustEqual SubmissionType.Disclosure
      resultUA.get(DoYouHaveACaseReferencePage) mustEqual Some(true)
      resultUA.get(WhatIsTheCaseReferencePage) mustEqual Some("Some ref")
    }
  }
}
