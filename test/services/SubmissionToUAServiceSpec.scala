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
import models.store.disclosure._
import models.store._
import models.store.notification._
import models.store.Metadata
import models._
import org.scalatest.TryValues
import java.time.Instant
import scala.util.{Success, Try}

class SubmissionToUAServiceSpec extends AnyWordSpec with Matchers with TryValues {

  val notificationUa = UserAnswers("notification")
  val disclosureUa = UserAnswers("disclosure")

  object TestNotificationService extends NotificationToUAService {
    def notificationToUserAnswers(notification: Notification): Try[UserAnswers] = Success(notificationUa)
    def personalDetailsToUserAnswers(personalDetails: PersonalDetails, userAnswers: UserAnswers): Try[UserAnswers] = Success(notificationUa)
  }

  object TestDisclosureService extends DisclosureToUAService {
    def fullDisclosureToUa(fullDisclosure: FullDisclosure): Try[UserAnswers] = Success(disclosureUa)
  }

  val sut = new SubmissionToUAServiceImpl(TestNotificationService, TestDisclosureService)

  val emptyUA = UserAnswers("id")

  "submissionToUa" should {
    "call the notification service if a notification is passed in" in {
      val notification = Notification("id", "submissionId", Instant.now, Metadata(), PersonalDetails(Background(), AboutYou(), None, None, None, None, None), None)
      sut.submissionToUa(notification).success.value shouldEqual notificationUa
    }

    "call the disclosure service if a notification is passed in" in {
      val fullDisclosure = FullDisclosure("userId", "submissionId", Instant.now, Metadata(), CaseReference(), PersonalDetails(Background(), AboutYou()), OffshoreLiabilities(), OtherLiabilities(), ReasonForDisclosingNow())
      sut.submissionToUa(fullDisclosure).success.value shouldEqual disclosureUa
    }
  }



}