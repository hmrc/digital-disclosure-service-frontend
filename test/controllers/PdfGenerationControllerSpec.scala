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
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl
import views.html.{JourneyRecoveryContinueView, JourneyRecoveryStartAgainView}
import org.mockito.ArgumentMatchers.any
import play.api.inject.bind
import org.mockito.Mockito.{never, times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import akka.util.ByteString
import services.SubmissionPDFService
import scala.concurrent.Future
import models.UserAnswers

class PdfGenerationControllerSpec extends SpecBase with MockitoSugar {

  "PdfGenerationController" - {

    "call the generation service" - {

      val mockService = mock[SubmissionPDFService]
      when(mockService.generatePdf(any())(any(), any())) thenReturn Future.successful(ByteString("String"))

      "for generate" in {
        val application = applicationBuilder(userAnswers = Some(UserAnswers("id", "submissionId"))).overrides(
          bind[SubmissionPDFService].toInstance(mockService)
        ).build()
        running(application) {
          val request = FakeRequest(GET, routes.PdfGenerationController.generate.url)
          val result = route(application, request).value

          status(result) mustEqual OK
        }
      }

      "for generateForSubmissionId" in {
        val application = applicationBuilder(userAnswers = Some(UserAnswers("id", "submissionId"))).overrides(
          bind[SubmissionPDFService].toInstance(mockService)
        ).build()
        
        running(application) {
          val request = FakeRequest(GET, routes.PdfGenerationController.generateForSubmissionId("submissionId").url)
          val result = route(application, request).value

          status(result) mustEqual OK
        }
      }

    }
  }
}
