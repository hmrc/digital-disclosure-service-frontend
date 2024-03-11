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
import models.UserAnswers
import models.store._
import models.store.notification._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.inject.bind
import play.api.mvc.Result
import play.api.mvc.Results.NoContent
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SubmissionStoreService
import uk.gov.hmrc.http.HeaderCarrier

import java.time.Instant
import scala.concurrent.Future

class CaseManagementControllerSpec extends SpecBase {

  "onPageLoad" - {

    "must return OK and the correct view where we have cases to display" in {

      val submission = Notification("userId", "submissionId", Instant.now, Metadata(), PersonalDetails(Background(), AboutYou()))

      object TestStoreService extends SubmissionStoreService {
        def setSubmission(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Result] =
          Future.successful(NoContent)
        def getSubmission(userId: String, submissionId: String)(implicit hc: HeaderCarrier): Future[Option[Submission]] =
          Future.successful(Some(submission))
        def getAllSubmissions(userId: String)(implicit hc: HeaderCarrier): Future[Seq[Submission]] =
          Future.successful(Seq(submission))
        def deleteSubmission(userId: String, submissionId: String)(implicit hc: HeaderCarrier): Future[Result] =
          Future.successful(NoContent)
      }

      setupMockSessionResponse(Some(emptyUserAnswers))

      val applicationWithFakeStoreService = applicationBuilder.overrides(
        bind[SubmissionStoreService].toInstance(TestStoreService)
      ).build()

      val request = FakeRequest(GET, routes.CaseManagementController.onPageLoad(1).url)

      val result = route(applicationWithFakeStoreService, request).value

      status(result) mustEqual OK
    }

    "must redirect where there are no cases to display" in {

      object TestStoreService extends SubmissionStoreService {
        def setSubmission(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Result] =
          Future.successful(NoContent)
        def getSubmission(userId: String, submissionId: String)(implicit hc: HeaderCarrier): Future[Option[Submission]] =
          Future.successful(None)
        def getAllSubmissions(userId: String)(implicit hc: HeaderCarrier): Future[Seq[Submission]] =
          Future.successful(Nil)
        def deleteSubmission(userId: String, submissionId: String)(implicit hc: HeaderCarrier): Future[Result] =
          Future.successful(NoContent)
      }

      setupMockSessionResponse(Some(emptyUserAnswers))

      val applicationWithFakeStoreService = applicationBuilder.overrides(
        bind[SubmissionStoreService].toInstance(TestStoreService)
      ).build()

      val request = FakeRequest(GET, routes.CaseManagementController.onPageLoad(1).url)

      val result = route(applicationWithFakeStoreService, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.CaseManagementController.newCase.url
    }
  }

  "newCase" - {

    "must redirect where there are no cases to display" in {

      setupMockSessionResponse(Some(emptyUserAnswers))
      when(mockSessionService.newSession(any(), any(), any(), any(), any())(any()))
        .thenReturn(Future.successful(emptyUserAnswers))

      val request = FakeRequest(GET, routes.CaseManagementController.newCase.url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.MakeANotificationOrDisclosureController.onPageLoad.url
    }
  }
}
