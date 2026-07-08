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

package controllers.actions

import base.SpecBase
import controllers.routes
import models.{SubmissionType, UserAnswers}
import models.requests.{DataRequest, OptionalDataRequest}
import models.store.Metadata
import play.api.mvc.Result
import play.api.mvc.Results.Redirect
import play.api.test.FakeRequest

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DataRequiredActionSpec extends SpecBase {

  class Harness extends DataRequiredActionImpl {
    def callRefine[A](request: OptionalDataRequest[A]): Future[Either[Result, DataRequest[A]]] =
      refine(request)
  }

  class EvenSubmittedHarness extends DataRequiredActionEvenSubmittedImpl {
    def callRefine[A](request: OptionalDataRequest[A]): Future[Either[Result, DataRequest[A]]] =
      refine(request)
  }

  private val submittedTime = LocalDateTime.of(2024, 1, 1, 12, 0)

  private def optionalRequest(
    userAnswers: Option[UserAnswers],
    isAgent: Boolean = false
  ): OptionalDataRequest[?] =
    OptionalDataRequest(FakeRequest(), userAnswersId, sessionId, userAnswers, isAgent, None)

  "DataRequiredAction" - {

    "must redirect to the Index page when there are no user answers" in {
      val result = new Harness().callRefine(optionalRequest(None)).futureValue
      result mustBe Left(Redirect(routes.IndexController.onPageLoad))
    }

    "for a submitted Disclosure" - {

      def submittedDisclosure: UserAnswers =
        emptyUserAnswers.copy(
          submissionType = SubmissionType.Disclosure,
          metadata = Metadata(reference = Some("CFSS-123"), submissionTime = Some(submittedTime))
        )

      "must redirect an agent to Case Management" in {
        val result = new Harness().callRefine(optionalRequest(Some(submittedDisclosure), isAgent = true)).futureValue
        result mustBe Left(Redirect(routes.CaseManagementController.onPageLoad(1)))
      }

      "must redirect a non-agent to the Submitted page" in {
        val result = new Harness().callRefine(optionalRequest(Some(submittedDisclosure))).futureValue
        result mustBe Left(Redirect(routes.SubmittedController.onPageLoad("CFSS-123")))
      }

      "must redirect a non-agent to the Submitted page with a fallback reference" in {
        val noRef = submittedDisclosure.copy(metadata = Metadata(reference = None, submissionTime = Some(submittedTime)))
        val result = new Harness().callRefine(optionalRequest(Some(noRef))).futureValue
        result mustBe Left(Redirect(routes.SubmittedController.onPageLoad("-")))
      }
    }

    "for a submitted Notification" - {

      def submittedNotification: UserAnswers =
        emptyUserAnswers.copy(
          submissionType = SubmissionType.Notification,
          metadata = Metadata(reference = Some("CFSS-456"), submissionTime = Some(submittedTime))
        )

      "must redirect an agent to Case Management" in {
        val result = new Harness().callRefine(optionalRequest(Some(submittedNotification), isAgent = true)).futureValue
        result mustBe Left(Redirect(routes.CaseManagementController.onPageLoad(1)))
      }

      "must redirect a non-agent to the Index page" in {
        val result = new Harness().callRefine(optionalRequest(Some(submittedNotification))).futureValue
        result mustBe Left(Redirect(routes.IndexController.onPageLoad))
      }
    }

    "must return a DataRequest when answers exist and nothing has been submitted" in {
      val answers = emptyUserAnswers
      val result  = new Harness().callRefine(optionalRequest(Some(answers))).futureValue
      result.isRight mustBe true
      result.toOption.value.userAnswers mustBe answers
    }
  }

  "DataRequiredActionEvenSubmitted" - {

    "must redirect to the Index page when there are no user answers" in {
      val result = new EvenSubmittedHarness().callRefine(optionalRequest(None)).futureValue
      result mustBe Left(Redirect(routes.IndexController.onPageLoad))
    }

    "must return a DataRequest even for a submitted disclosure" in {
      val submitted = emptyUserAnswers.copy(
        submissionType = SubmissionType.Disclosure,
        metadata = Metadata(reference = Some("CFSS-789"), submissionTime = Some(submittedTime))
      )
      val result = new EvenSubmittedHarness().callRefine(optionalRequest(Some(submitted))).futureValue
      result.isRight mustBe true
      result.toOption.value.userAnswers mustBe submitted
    }
  }
}
