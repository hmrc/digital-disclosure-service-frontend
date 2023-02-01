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

import uk.gov.hmrc.http.HeaderCarrier
import scala.concurrent.Future
import models.store.Submission
import play.api.mvc.Result
import connectors.SubmissionStoreConnector
import com.google.inject.{Inject, Singleton, ImplementedBy}
import models.UserAnswers

@Singleton
class SubmissionStoreServiceImpl @Inject()(
  connector: SubmissionStoreConnector,
  uaToSubmissionService: UAToSubmissionService
) extends SubmissionStoreService {

  def setSubmission(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Result] = {
    val submission = uaToSubmissionService.uaToSubmission(userAnswers)
    setSubmission(submission)
  }

  def setSubmission(submission: Submission)(implicit hc: HeaderCarrier): Future[Result] = {
    connector.setSubmission(submission)
  }

  def getSubmission(userId: String, submissionId: String)(implicit hc: HeaderCarrier): Future[Option[Submission]] = {
    connector.getSubmission(userId, submissionId)
  }

  def getAllSubmissions(userId: String)(implicit hc: HeaderCarrier): Future[Seq[Submission]] = {
    connector.getAllSubmissions(userId)
  }

  def deleteSubmission(userId: String, submissionId: String)(implicit hc: HeaderCarrier): Future[Result] = {
    connector.deleteSubmission(userId, submissionId)
  }
}

@ImplementedBy(classOf[SubmissionStoreServiceImpl])
trait SubmissionStoreService {
  def setSubmission(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Result]
  def getSubmission(userId: String, submissionId: String)(implicit hc: HeaderCarrier): Future[Option[Submission]]
  def deleteSubmission(userId: String, submissionId: String)(implicit hc: HeaderCarrier): Future[Result]
}