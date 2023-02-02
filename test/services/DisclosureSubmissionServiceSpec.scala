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
import java.time.{Instant, LocalDateTime}
import org.mockito.Mockito.{verify, when}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.TryValues
import org.scalatest.concurrent.ScalaFutures
import connectors.DigitalDisclosureServiceConnector
import models.UserAnswers
import models.store.notification._
import models.store.disclosure._
import models.store._
import scala.concurrent.Future
import uk.gov.hmrc.http.HeaderCarrier
import scala.concurrent.ExecutionContext.Implicits.global
import pages.LetterReferencePage

class DisclosureSubmissionServiceSpec extends AnyWordSpec with ScalaFutures
    with TryValues with Matchers with MockitoSugar  {

  "submitDisclosure" should {

    "call the reference generator if a letter reference isn't populated" in new Test {

      val metadata = Metadata(reference = Some("123456"), submissionTime = Some(time))
      val updatedUserAnswers = emptyUA.copy(metadata = metadata)

      when(uaToDisclosureService.uaToFullDisclosure(updatedUserAnswers)) thenReturn testDisclosure
      when(connector.submitDisclosure(testDisclosure)(hc)) thenReturn Future.successful("id")
      when(sessionService.set(updatedUserAnswers)(hc)) thenReturn Future.successful(true)
      
      sut.submitDisclosure(emptyUA).futureValue shouldEqual "123456"
      verify(auditService).auditDisclosureSubmission(testDisclosure)(hc)
    }

    "use the letter reference for the ref if a letter reference is populated" in new Test {

      val initialUserAnswers = emptyUA.set(LetterReferencePage, "ABCDE").success.value
      val metadata = Metadata(reference = Some("ABCDE"), submissionTime = Some(time))
      val updatedUserAnswers = initialUserAnswers.copy(metadata = metadata)

      when(uaToDisclosureService.uaToFullDisclosure(updatedUserAnswers)) thenReturn testDisclosure
      when(connector.submitDisclosure(testDisclosure)(hc)) thenReturn Future.successful("id")
      when(sessionService.set(updatedUserAnswers)(hc)) thenReturn Future.successful(true)
      
      sut.submitDisclosure(initialUserAnswers).futureValue shouldEqual "ABCDE"
      verify(auditService).auditDisclosureSubmission(testDisclosure)(hc)
    }

    trait Test {
      val connector: DigitalDisclosureServiceConnector = mock[DigitalDisclosureServiceConnector]
      val uaToDisclosureService: UAToDisclosureService = mock[UAToDisclosureService]
      val sessionService: SessionService = mock[SessionService]
      val auditService: AuditService = mock[AuditService]

      val reference = "123456"
      object FakeReferenceService extends ReferenceService {
        def generateReference: String = reference
      }

      val time = LocalDateTime.now
      object FakeTimeService extends TimeService {
        def now: LocalDateTime = time
      } 

      val sut = new DisclosureSubmissionServiceImpl(connector, uaToDisclosureService, FakeReferenceService, sessionService, FakeTimeService, auditService)

      val emptyUA = UserAnswers("id")
      val testDisclosure = FullDisclosure("123", "123", Instant.now(), Metadata(), CaseReference(), PersonalDetails(Background(), AboutYou()), OffshoreLiabilities(), OtherLiabilities(), ReasonForDisclosingNow())
      implicit val hc = HeaderCarrier()
    }


  }

}