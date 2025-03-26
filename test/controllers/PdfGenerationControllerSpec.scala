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
import org.mockito.ArgumentMatchers.any
import play.api.inject.bind
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import org.apache.pekko.util.ByteString
import services.SubmissionPDFService
import scala.concurrent.Future
import models.UserAnswers
import uk.gov.hmrc.http.HeaderCarrier
import models.store.notification._
import models.store._
import services.SubmissionStoreService
import java.time.Instant
import play.api.mvc.Result
import play.api.mvc.Results.NoContent

class PdfGenerationControllerSpec extends SpecBase with MockitoSugar {

  "PdfGenerationController" - {

    "call the generation service" - {

      val mockService = mock[SubmissionPDFService]
      when(mockService.generatePdf(any())(any(), any())) thenReturn Future.successful(ByteString("String"))

      "for generate" in {
        setupMockSessionResponse(Some(UserAnswers("id", "submissionId")))
        val applicationWithMockPDFService = applicationBuilder
          .overrides(
            bind[SubmissionPDFService].toInstance(mockService)
          )
          .build()

        val request = FakeRequest(GET, routes.PdfGenerationController.generate.url)
        val result  = route(applicationWithMockPDFService, request).value

        status(result) mustEqual OK
      }

      "for generateForSubmissionId" in {

        val submission =
          Notification("userId", "submissionId", Instant.now, Metadata(), PersonalDetails(Background(), AboutYou()))
        object TestStoreService extends SubmissionStoreService {
          def setSubmission(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Result]                =
            Future.successful(NoContent)
          def getSubmission(userId: String, submissionId: String)(implicit
            hc: HeaderCarrier
          ): Future[Option[Submission]] =
            Future.successful(Some(submission))
          def getAllSubmissions(userId: String)(implicit hc: HeaderCarrier): Future[Seq[Submission]]             =
            Future.successful(Seq(submission))
          def deleteSubmission(userId: String, submissionId: String)(implicit hc: HeaderCarrier): Future[Result] =
            Future.successful(NoContent)
        }

        setupMockSessionResponse(Some(UserAnswers("id", "submissionId")))
        val applicationWithFakePDFService = applicationBuilder
          .overrides(
            bind[SubmissionStoreService].toInstance(TestStoreService),
            bind[SubmissionPDFService].toInstance(mockService)
          )
          .build()

        val request = FakeRequest(GET, routes.PdfGenerationController.generateForSubmissionId("submissionId").url)
        val result  = route(applicationWithFakePDFService, request).value

        status(result) mustEqual OK
      }

    }
  }
}
