/*
 * Copyright 2022 HM Revenue & Customs
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
import models.store.notification._
import play.api.mvc.Result
import connectors.NotificationStoreConnector
import com.google.inject.{Inject, Singleton, ImplementedBy}

@Singleton
class NotificationStoreServiceImpl @Inject()(
  connector: NotificationStoreConnector
) {

  // def getIndividualNotification(userId: String)(implicit hc: HeaderCarrier): Future[UserAnswers] = 
  //   getAllNotifications(userId).map(
  //     _.match {
  //       case Nil => UserAnswers(userId)
  //       case head :: _ => notificationDataService.notificationToUserAnswers(head)
  //     }
  //   )

  def getNotification(userId: String, notificationId: String)(implicit hc: HeaderCarrier): Future[Option[Notification]] = {
    connector.getNotification(userId, notificationId)
  }

  def getAllNotifications(userId: String)(implicit hc: HeaderCarrier): Future[Seq[Notification]] = {
    connector.getAllNotifications(userId)
  }

  def setNotification(notification: Notification)(implicit hc: HeaderCarrier): Future[Result] = {
    connector.setNotification(notification)
  }

  def deleteNotification(userId: String, notificationId: String)(implicit hc: HeaderCarrier): Future[Result] = {
    connector.deleteNotification(userId, notificationId)
  }
}

@ImplementedBy(classOf[NotificationStoreServiceImpl])
trait NotificationStoreService {
  //def getIndividualNotification(userId: String)(implicit hc: HeaderCarrier): Future[UserAnswers]
  def getNotification(userId: String, notificationId: String)(implicit hc: HeaderCarrier): Future[Option[Notification]]
  def getAllNotifications(userId: String)(implicit hc: HeaderCarrier): Future[Seq[Notification]]
  def setNotification(notification: Notification)(implicit hc: HeaderCarrier): Future[Result]
  def deleteNotification(userId: String, notificationId: String)(implicit hc: HeaderCarrier): Future[Result]
}