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
import java.time.Instant
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.TryValues
import org.scalatest.concurrent.ScalaFutures
import connectors.DigitalDisclosureServiceConnector
import models.UserAnswers
import models.store._
import models.store.notification._
import models.store.disclosure._
import scala.concurrent.Future
import uk.gov.hmrc.http.HeaderCarrier
import scala.concurrent.ExecutionContext.Implicits.global
import akka.util.ByteString

class SubmissionPDFServiceSpec extends AnyWordSpec with ScalaFutures
    with TryValues with Matchers with MockitoSugar  {

  val connector: DigitalDisclosureServiceConnector = mock[DigitalDisclosureServiceConnector]
  val uaToSubmissionService: UAToSubmissionService = mock[UAToSubmissionService]

  val sut = new SubmissionPDFServiceImpl(connector, uaToSubmissionService)

  val emptyUA = UserAnswers("id")
  val testNotification = Notification("123", "456", Instant.now(), Metadata(), PersonalDetails(Background(), AboutYou()))
  val testDisclosure = FullDisclosure("123", "123", Instant.now(), Metadata(), CaseReference(), PersonalDetails(Background(), AboutYou()), OffshoreLiabilities(), OtherLiabilities(), ReasonForDisclosingNow())
  implicit val hc = HeaderCarrier()

  "generatePdf" should {

    "call userAnswersToSubmission followed by generateNotificationPDF for a notification" in {
      when(uaToSubmissionService.uaToSubmission(emptyUA)) thenReturn testNotification
      when(connector.generateNotificationPDF(testNotification)(hc)) thenReturn Future.successful(ByteString("1234"))
      
      sut.generatePdf(emptyUA).futureValue shouldEqual ByteString("1234")
    }

    "call userAnswersToSubmission followed by generateSubmissionPDF for a disclosure" in {
      when(uaToSubmissionService.uaToSubmission(emptyUA)) thenReturn testDisclosure
      when(connector.generateDisclosurePDF(testDisclosure)(hc)) thenReturn Future.successful(ByteString("1234"))
      
      sut.generatePdf(emptyUA).futureValue shouldEqual ByteString("1234")
    }

  }

}