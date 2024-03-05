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

package services

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalamock.scalatest.MockFactory
import models.store.notification._
import models.store._
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent.Future
import org.scalamock.handlers.{CallHandler1, CallHandler2, CallHandler3}
import uk.gov.hmrc.http.HeaderCarrier
import connectors.SubmissionStoreConnector
import play.api.mvc.Result
import java.time.Instant
import models.UserAnswers
import play.api.mvc.Results.Ok

class SubmissionStoreServiceSpec extends AnyWordSpec with Matchers 
    with MockFactory with ScalaFutures {

  private val connector = mock[SubmissionStoreConnector]
  private val service = mock[UAToSubmissionService]
  val sut = new SubmissionStoreServiceImpl(connector, service)
  implicit val hc: HeaderCarrier = HeaderCarrier()

  def mockGetSubmission(userId: String, submissionId: String)(
    response: Future[Option[Submission]]
  ): CallHandler3[String, String, HeaderCarrier, Future[Option[Submission]]] =
    (connector
      .getSubmission(_: String, _: String)(_: HeaderCarrier))
      .expects(userId, submissionId, *)
      .returning(response)
  
  def mockGetAllSubmissions(userId: String)(
    response: Future[Seq[Submission]]
  ): CallHandler2[String, HeaderCarrier, Future[Seq[Submission]]] =
    (connector
      .getAllSubmissions(_: String)(_: HeaderCarrier))
      .expects(userId, *)
      .returning(response)

  def mockSetSubmission(notification: Submission)(
    response: Future[Result]
  ): CallHandler2[Submission, HeaderCarrier, Future[Result]] =
    (connector
      .setSubmission(_: Submission)(_: HeaderCarrier))
      .expects(notification, *)
      .returning(response)
  
  def mockDeleteSubmission(userId: String, submissionId: String)(
    response: Future[Result]
  ): CallHandler3[String, String, HeaderCarrier, Future[Result]] =
    (connector
      .deleteSubmission(_: String, _: String)(_: HeaderCarrier))
      .expects(userId, submissionId, *)
      .returning(response)

  def mockUserAnswersToNotification(userAnswers: UserAnswers)(
    response: Submission
  ): CallHandler1[UserAnswers, Submission] =
    (service
      .uaToSubmission(_: UserAnswers))
      .expects(userAnswers)
      .returning(response)

  val testSubmission: Submission = Notification("123", "456", Instant.now(), Metadata(), PersonalDetails(Background(), AboutYou()))

  "getSubmission" should {
    "return the same value as returned by the connector" in {
      mockGetSubmission("123", "456")(Future.successful(Some(testSubmission)))
      sut.getSubmission("123", "456").futureValue shouldEqual Some(testSubmission)
    }
  }

  "setSubmission" should {
    "pass the userAnswers to the dataService and return the converted value to the connector" in {
      val userAnswers = UserAnswers("id", "session-123")
      val convertedSubmission = Notification("id", "submissionId", Instant.now(), Metadata(), PersonalDetails(Background(), AboutYou()))
      mockUserAnswersToNotification(userAnswers)(convertedSubmission)
      mockSetSubmission(convertedSubmission)(Future.successful(Ok("Done")))

      sut.setSubmission(userAnswers).futureValue shouldEqual Ok("Done")
    }
  }

}
