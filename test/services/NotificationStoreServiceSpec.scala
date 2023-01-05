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
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent.Future
import org.scalamock.handlers.{CallHandler1, CallHandler2, CallHandler3}
import uk.gov.hmrc.http.HeaderCarrier
import connectors.NotificationStoreConnector
import play.api.mvc.Result
import java.time.Instant
import models.UserAnswers
import play.api.mvc.Results.Ok

class NotificationStoreServiceSpec extends AnyWordSpec with Matchers 
    with MockFactory with ScalaFutures {

  private val connector = mock[NotificationStoreConnector]
  private val service = mock[StoreDataService]
  val sut = new NotificationStoreServiceImpl(connector, service)
  implicit val hc = HeaderCarrier()

  def mockGetNotification(userId: String, notificationId: String)(
    response: Future[Option[Notification]]
  ): CallHandler3[String, String, HeaderCarrier, Future[Option[Notification]]] =
    (connector
      .getNotification(_: String, _: String)(_: HeaderCarrier))
      .expects(userId, notificationId, *)
      .returning(response)
  
  def mockGetAllNotifications(userId: String)(
    response: Future[Seq[Notification]]
  ): CallHandler2[String, HeaderCarrier, Future[Seq[Notification]]] =
    (connector
      .getAllNotifications(_: String)(_: HeaderCarrier))
      .expects(userId, *)
      .returning(response)

  def mockSetNotification(notification: Notification)(
    response: Future[Result]
  ): CallHandler2[Notification, HeaderCarrier, Future[Result]] =
    (connector
      .setNotification(_: Notification)(_: HeaderCarrier))
      .expects(notification, *)
      .returning(response)
  
  def mockDeleteNotification(userId: String, notificationId: String)(
    response: Future[Result]
  ): CallHandler3[String, String, HeaderCarrier, Future[Result]] =
    (connector
      .deleteNotification(_: String, _: String)(_: HeaderCarrier))
      .expects(userId, notificationId, *)
      .returning(response)

  def mockUserAnswersToNotification(userAnswers: UserAnswers)(
    response: Notification
  ): CallHandler1[UserAnswers, Notification] =
    (service
      .userAnswersToNotification(_: UserAnswers))
      .expects(userAnswers)
      .returning(response)

  val testNotification = Notification("123", "456", Instant.now(), Metadata(), Background(), AboutYou())

  "getNotification" should {
    "return the same value as returned by the connector" in {
      mockGetNotification("123", "456")(Future.successful(Some(testNotification)))
      sut.getNotification("123", "456").futureValue shouldEqual Some(testNotification)
    }
  }

  "setNotification" should {
    "pass the userAnswers to the dataService and return the converted value to the connector" in {
      val userAnswers = UserAnswers("id")
      val convertedNotification = Notification("id", "notificationId", Instant.now(), Metadata(), Background(), AboutYou())
      mockUserAnswersToNotification(userAnswers)(convertedNotification)
      mockSetNotification(convertedNotification)(Future.successful(Ok("Done")))

      sut.setNotification(userAnswers).futureValue shouldEqual Ok("Done")
    }
  }

}
