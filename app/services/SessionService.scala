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
import com.google.inject.{Inject, Singleton, ImplementedBy}
import repositories.SessionRepository
import cats.implicits._
import play.api.Logging

@Singleton
class SessionServiceImpl @Inject()(
  val sessionRepository: SessionRepository,
  val notificationStoreService: NotificationStoreService,
  val notificationDataService: NotificationDataService
)(implicit ec: ExecutionContext) extends SessionService with Logging {

  def getSession(userId: String)(implicit hc: HeaderCarrier): Future[Option[UserAnswers]] =
    sessionRepository.get(userId)

  def newSession(userId: String)(implicit hc: HeaderCarrier): Future[Boolean] =
    for {
      uaOpt  <- getIndividualNotificationUserAnswers(userId, UserAnswers.defaultNotificationId)
      ua     = extractOrDefaultUserAnswers(userId, uaOpt)
      result <- set(ua)
    } yield result

  def extractOrDefaultUserAnswers(userId: String, uaOpt: Option[UserAnswers]) =
    uaOpt match {
      case Some(ua) if ua.metadata.submissionTime.isEmpty => ua
      case _ => UserAnswers(userId)
    }

  def getIndividualNotificationUserAnswers(userId: String, notificationId: String)(implicit hc: HeaderCarrier): Future[Option[UserAnswers]] = 
    for {
      notificationOpt <- notificationStoreService.getNotification(userId, notificationId)
      uaOpt <- Future.fromTry(notificationOpt.map(notificationDataService.notificationToUserAnswers).sequence)
    } yield uaOpt

  def set(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean] =
    userAnswers.submissionType match {
      case SubmissionType.Notification => setNotification(userAnswers)
      case SubmissionType.Disclosure => sessionRepository.set(userAnswers)
    }

  def setNotification(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean] =
    for {
      result <- sessionRepository.set(userAnswers)
      _      <- notificationStoreService.setNotification(userAnswers)
    } yield result
    
  def clear(id: String): Future[Boolean] = sessionRepository.clear(id)

  def keepAlive(id: String): Future[Boolean] = sessionRepository.keepAlive(id)
}

@ImplementedBy(classOf[SessionServiceImpl])
trait SessionService {
  def getSession(userId: String)(implicit hc: HeaderCarrier): Future[Option[UserAnswers]] 
  def newSession(userId: String)(implicit hc: HeaderCarrier): Future[Boolean]
  def set(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean]
  def clear(id: String): Future[Boolean]
  def keepAlive(id: String): Future[Boolean]
}