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
import services.SubmissionStoreService
import scala.concurrent.Future
import models.store.notification._
import models.store._
import uk.gov.hmrc.http.HeaderCarrier
import play.api.mvc.Result
import play.api.mvc.Results.NoContent
import models.UserAnswers
import java.time.Instant

class CaseManagementControllerSpec extends SpecBase {

  "onPageLoad" - {

    "must return OK and the correct view where we have cases to display" in {

      val submission = Notification("userId", "submissionId", Instant.now, Metadata(), PersonalDetails(Background(), AboutYou()))

      object TestStoreService extends SubmissionStoreService {
        def setSubmission(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Result] = Future.successful(NoContent)
        def getSubmission(userId: String, submissionId: String)(implicit hc: HeaderCarrier): Future[Option[Submission]] = 
          Future.successful(Some(submission))
        def getAllSubmissions(userId: String)(implicit hc: HeaderCarrier): Future[Seq[Submission]] = 
          Future.successful(Seq(submission))
        def deleteSubmission(userId: String, submissionId: String)(implicit hc: HeaderCarrier): Future[Result] = Future.successful(NoContent)
      }

      val application = applicationBuilderWithStoreService(userAnswers = Some(emptyUserAnswers), TestStoreService).build()

      running(application) {
        val request = FakeRequest(GET, routes.CaseManagementController.onPageLoad.url)

        val result = route(application, request).value

        status(result) mustEqual OK
      }
    }

    "must redirect where there are no cases to display" in {

      object TestStoreService extends SubmissionStoreService {
        def setSubmission(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Result] = Future.successful(NoContent)
        def getSubmission(userId: String, submissionId: String)(implicit hc: HeaderCarrier): Future[Option[Submission]] = 
          Future.successful(None)
        def getAllSubmissions(userId: String)(implicit hc: HeaderCarrier): Future[Seq[Submission]] = 
          Future.successful(Nil)
        def deleteSubmission(userId: String, submissionId: String)(implicit hc: HeaderCarrier): Future[Result] = Future.successful(NoContent)
      }

      val application = applicationBuilderWithStoreService(userAnswers = Some(emptyUserAnswers), TestStoreService).build()

      running(application) {
        val request = FakeRequest(GET, routes.CaseManagementController.onPageLoad.url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.CaseManagementController.newCase.url
      }
    }
  }

  "newCase" - {

    "must redirect where there are no cases to display" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.CaseManagementController.newCase.url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.MakeANotificationOrDisclosureController.onPageLoad.url
      }
    }

  }

}
