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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalamock.scalatest.MockFactory
import models.store.notification._
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent.Future
import org.scalamock.handlers.{CallHandler1, CallHandler2, CallHandler3}
import uk.gov.hmrc.http.HeaderCarrier
import play.api.mvc.Result
import java.time.{LocalDateTime, Instant}
import models.{UserAnswers, SubmissionType}
import play.api.mvc.Results.Ok
import repositories.SessionRepository
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Try}

class SessionServiceSpec extends AnyWordSpec with Matchers 
    with MockFactory with ScalaFutures {

  implicit val hc = HeaderCarrier()
    
  val testNotification = Notification("123", "Individual", Instant.now(), Metadata(), Background(), AboutYou())
  val testSubmittedNotification = Notification("123", "Individual", Instant.now(), Metadata(submissionTime = Some(LocalDateTime.now)), Background(), AboutYou())

  val userAnswers = UserAnswers("123", "Individual", SubmissionType.Notification, lastUpdated = Instant.now())
  val submittedUserAnswers = UserAnswers("123", "Individual", SubmissionType.Notification, lastUpdated = Instant.now(), metadata = Metadata(submissionTime = Some(LocalDateTime.now)))
  val emptyUserAnswers = UserAnswers("123")

  "getSession" should {
    "return the same value as returned by the connector" in new Test {
      mockRepoGet("123")(Future.successful(Some(userAnswers)))
      sut.getSession("123").futureValue shouldEqual Some(userAnswers)
    }
  }

  "set" should {
    "update the repo and the store" in new Test {
      (repo.set(_: UserAnswers)).expects(userAnswers).returning(Future.successful(true))
      (storeService.setNotification(_: UserAnswers)(_: HeaderCarrier)).expects(userAnswers, *).returning(Future.successful(Ok))

      sut.set(userAnswers).futureValue shouldEqual true
    }
  }

  "clear" should {
    "return the same value as returned by the connector" in new Test {
      mockRepoClear("123")(Future.successful(true))
      sut.clear("123").futureValue shouldEqual true
    }
  }

  "keepAlive" should {
    "return the same value as returned by the connector" in new Test {
      mockRepoKeepAlive("123")(Future.successful(true))
      sut.keepAlive("123").futureValue shouldEqual true
    }
  }


  "newSession" should {

    "check the store and where it finds nothing, default and set that default in the session and store" in new Test {   
      mockGetNotification("123", UserAnswers.defaultNotificationId)(Future.successful(None))
      (repo.set(_: UserAnswers)).expects(*).returning(Future.successful(true))
      (storeService.setNotification(_: UserAnswers)(_: HeaderCarrier)).expects(*, *).returning(Future.successful(Ok))
      val result = sut.newSession("123")
      Thread.sleep(150)
      result.futureValue shouldEqual true
    }

    "check the store and where it finds something, set that value in both the session and store" in new Test {
      mockGetNotification("123", UserAnswers.defaultNotificationId)(Future.successful(Some(testNotification)))
      mockNotificationToUserAnswers(testNotification)(Success(userAnswers))
      (repo.set(_: UserAnswers)).expects(userAnswers).returning(Future.successful(true))
      (storeService.setNotification(_: UserAnswers)(_: HeaderCarrier)).expects(userAnswers, *).returning(Future.successful(Ok))

      sut.newSession("123").futureValue shouldEqual true
    }

    "check the store and where it finds something which has been submitted, default and set that default in the session and store" in new Test {
      mockGetNotification("123", UserAnswers.defaultNotificationId)(Future.successful(Some(testSubmittedNotification)))
      mockNotificationToUserAnswers(testSubmittedNotification)(Success(submittedUserAnswers))
      (repo.set(_: UserAnswers)).expects(*).returning(Future.successful(true))
      (storeService.setNotification(_: UserAnswers)(_: HeaderCarrier)).expects(*, *).returning(Future.successful(Ok))

      sut.newSession("123").futureValue shouldEqual true
    }
  }

  "getIndividualNotificationUserAnswers" should {
    "check the store and where it finds nothing return None" in new Test {
      mockGetNotification("123", UserAnswers.defaultNotificationId)(Future.successful(None))

      sut.getIndividualNotificationUserAnswers("123", "Individual").futureValue shouldEqual None
    }

    "check the store and where it finds something, convert it to a UserAnswers" in new Test {
      mockGetNotification("123", UserAnswers.defaultNotificationId)(Future.successful(Some(testNotification)))
      mockNotificationToUserAnswers(testNotification)(Success(userAnswers))

      sut.getIndividualNotificationUserAnswers("123", "Individual").futureValue shouldEqual Some(userAnswers)
    }
  }

  trait Test { 
  
    val repo = mock[SessionRepository]
    val storeService = mock[NotificationStoreService]
    val dataService = mock[NotificationDataService]

    val sut = new SessionServiceImpl(repo, storeService, dataService)

    def mockRepoGet(userId: String)(
      response: Future[Option[UserAnswers]]
    ): CallHandler1[String, Future[Option[UserAnswers]]] =
      (repo
        .get(_: String))
        .expects(userId)
        .returning(response)

    def mockRepoSet(answers: UserAnswers)(
      response: Future[Boolean]
    ): CallHandler1[UserAnswers, Future[Boolean]] =
      (repo
        .set(_: UserAnswers))
        .expects(answers)
        .returning(response)

    def mockRepoClear(userId: String)(
      response: Future[Boolean]
    ): CallHandler1[String, Future[Boolean]] =
      (repo
        .clear(_: String))
        .expects(userId)
        .returning(response)

    def mockRepoKeepAlive(userId: String)(
      response: Future[Boolean]
    ): CallHandler1[String, Future[Boolean]] =
      (repo
        .keepAlive(_: String))
        .expects(userId)
        .returning(response)

    def mockGetNotification(userId: String, notificationId: String)(
      response: Future[Option[Notification]]
    ): CallHandler3[String, String, HeaderCarrier, Future[Option[Notification]]] =
      (storeService
        .getNotification(_: String, _: String)(_: HeaderCarrier))
        .expects(userId, notificationId, *)
        .returning(response)
    
    def mockSetNotification(userAnswers: UserAnswers)(
      response: Future[Result]
    ): CallHandler2[UserAnswers, HeaderCarrier, Future[Result]] =
      (storeService
        .setNotification(_: UserAnswers)(_: HeaderCarrier))
        .expects(userAnswers, *)
        .returning(response)
    
    def mockNotificationToUserAnswers(notification: Notification)(
      response: Try[UserAnswers]
    ): CallHandler1[Notification, Try[UserAnswers]] =
      (dataService
        .notificationToUserAnswers(_: Notification))
        .expects(notification)
        .returning(response)

  }

}