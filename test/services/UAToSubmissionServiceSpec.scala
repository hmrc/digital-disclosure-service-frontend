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
import org.scalatest.TryValues
import models.store._
import models.store.notification.{AboutYou, Background, PersonalDetails}
import models.store.disclosure._
import models._
import java.time.Instant

class UAToSubmissionServiceSpec extends AnyWordSpec with Matchers with TryValues {

  val emptyUA = UserAnswers("id", "session-123")

  val fullDisclosure = FullDisclosure(
    "userId",
    "submissionId",
    Instant.now,
    Metadata(),
    CaseReference(),
    PersonalDetails(Background(), AboutYou()),
    None,
    OffshoreLiabilities(),
    OtherLiabilities(),
    ReasonForDisclosingNow()
  )
  val notification   = Notification(
    "id",
    "submissionId",
    Instant.now,
    Metadata(),
    PersonalDetails(Background(), AboutYou(), None, None, None, None, None),
    None
  )

  object TestNotificationService extends UAToNotificationService {
    def userAnswersToNotification(userAnswers: UserAnswers): Notification       = notification
    def userAnswersToPersonalDetails(userAnswers: UserAnswers): PersonalDetails =
      PersonalDetails(Background(), AboutYou())
  }

  object TestDisclosureService extends UAToDisclosureService {
    def uaToFullDisclosure(userAnswers: UserAnswers): FullDisclosure                 = fullDisclosure
    def uaToOtherLiabilities(userAnswers: UserAnswers): OtherLiabilities             = OtherLiabilities()
    def uaToOffshoreLiabilities(userAnswers: UserAnswers): OffshoreLiabilities       = OffshoreLiabilities()
    def uaToReasonForDisclosingNow(userAnswers: UserAnswers): ReasonForDisclosingNow = ReasonForDisclosingNow()
    def uaToCaseReference(userAnswers: UserAnswers): CaseReference                   = CaseReference()
  }

  val sut = new UAToSubmissionServiceImpl(TestNotificationService, TestDisclosureService)

  "uaToSubmission" should {

    "call UAToNotificationService for a notification" in {
      val ua = UserAnswers(id = "id", sessionId = "session-123", submissionType = SubmissionType.Notification)
      sut.uaToSubmission(ua) shouldEqual notification
    }

    "call UAToDisclosureService for a disclosure" in {
      val ua = UserAnswers(id = "id", sessionId = "session-123", submissionType = SubmissionType.Disclosure)
      sut.uaToSubmission(ua) shouldEqual fullDisclosure
    }

  }

}
