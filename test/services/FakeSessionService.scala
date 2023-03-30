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

import models.{UserAnswers, SubmissionType, CustomerId}
import scala.concurrent.Future
import uk.gov.hmrc.http.HeaderCarrier

class FakeSessionService extends SessionService {
  def getSession(userId: String)(implicit hc: HeaderCarrier): Future[Option[UserAnswers]] = Future.successful(None)
  def newSession(userId: String, submissionId: String, submissionType: SubmissionType, customerId: Option[CustomerId])(implicit hc: HeaderCarrier): Future[UserAnswers] = Future.successful(UserAnswers(userId))
  def set(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean] = Future.successful(true)
  def clear(id: String): Future[Boolean] = Future.successful(true)
  def keepAlive(id: String): Future[Boolean] = Future.successful(true)
  def clearAndRestartSessionAndDraft(id: String, submissionId: String, submissionType: SubmissionType)(implicit hc: HeaderCarrier): Future[UserAnswers] = Future.successful(UserAnswers(id))
  def getIndividualUserAnswers(userId: String, submissionId: String)(implicit hc: HeaderCarrier): Future[Option[UserAnswers]] = Future.successful(Some(UserAnswers(userId)))
}