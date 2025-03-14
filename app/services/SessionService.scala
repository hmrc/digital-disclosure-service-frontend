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

import models._
import uk.gov.hmrc.http.HeaderCarrier
import scala.concurrent.{ExecutionContext, Future}
import com.google.inject.{ImplementedBy, Inject, Singleton}
import repositories.SessionRepository
import cats.implicits._
import play.api.Logging
import java.time.Instant

@Singleton
class SessionServiceImpl @Inject() (
  val sessionRepository: SessionRepository,
  val submissionStoreService: SubmissionStoreService,
  val submissionToUAService: SubmissionToUAService
)(implicit ec: ExecutionContext)
    extends SessionService
    with Logging {

  def getSession(userId: String, sessionId: String)(implicit hc: HeaderCarrier): Future[Option[UserAnswers]] =
    sessionRepository.get(userId, sessionId)

  def newSession(
    userId: String,
    sessionId: String,
    submissionId: String,
    submissionType: SubmissionType,
    customerId: Option[CustomerId]
  )(implicit hc: HeaderCarrier): Future[UserAnswers] =
    for {
      uaOpt  <- getIndividualUserAnswers(userId, sessionId, submissionId)
      ua      = uaOpt.getOrElse(
                  UserAnswers(
                    id = userId,
                    sessionId = sessionId,
                    submissionId = submissionId,
                    submissionType = submissionType,
                    created = Instant.now,
                    customerId = customerId
                  )
                )
      result <- set(ua)
    } yield ua

  def getIndividualUserAnswers(userId: String, sessionId: String, submissionId: String)(implicit
    hc: HeaderCarrier
  ): Future[Option[UserAnswers]] =
    for {
      submissionOpt <- submissionStoreService.getSubmission(userId, submissionId)
      uaOpt         <- Future.fromTry(submissionOpt.map(s => submissionToUAService.submissionToUa(sessionId, s)).sequence)
    } yield uaOpt

  def set(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean] =
    for {
      result <- sessionRepository.set(userAnswers)
      _      <- submissionStoreService.setSubmission(userAnswers)
    } yield result

  def clearAndRestartSessionAndDraft(
    userId: String,
    sessionId: String,
    submissionId: String,
    submissionType: SubmissionType
  )(implicit hc: HeaderCarrier): Future[UserAnswers] =
    for {
      _ <- submissionStoreService.deleteSubmission(userId, submissionId)
      _ <- sessionRepository.clear(userId, sessionId)
      ua = UserAnswers(userId, sessionId, submissionId, submissionType, created = Instant.now)
      _ <- set(ua)
    } yield ua

  def clear(userId: String, sessionId: String): Future[Boolean] = sessionRepository.clear(userId, sessionId)

  def keepAlive(userId: String, sessionId: String): Future[Boolean] = sessionRepository.keepAlive(userId, sessionId)
}

@ImplementedBy(classOf[SessionServiceImpl])
trait SessionService {
  def getSession(userId: String, sessionId: String)(implicit hc: HeaderCarrier): Future[Option[UserAnswers]]
  def newSession(
    userId: String,
    sessionId: String,
    submissionId: String,
    submissionType: SubmissionType,
    customerId: Option[CustomerId]
  )(implicit hc: HeaderCarrier): Future[UserAnswers]
  def set(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean]
  def clear(userId: String, sessionId: String): Future[Boolean]
  def keepAlive(userId: String, sessionId: String): Future[Boolean]
  def clearAndRestartSessionAndDraft(
    userId: String,
    sessionId: String,
    submissionId: String,
    submissionType: SubmissionType
  )(implicit hc: HeaderCarrier): Future[UserAnswers]
  def getIndividualUserAnswers(userId: String, sessionId: String, submissionId: String)(implicit
    hc: HeaderCarrier
  ): Future[Option[UserAnswers]]
}
