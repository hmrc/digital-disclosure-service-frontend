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
import java.time.Instant
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.TryValues
import org.scalatest.concurrent.ScalaFutures
import connectors.DigitalDisclosureServiceConnector
import models.UserAnswers
import models.store.notification._
import scala.concurrent.Future
import uk.gov.hmrc.http.HeaderCarrier
import scala.concurrent.ExecutionContext.Implicits.global
import akka.util.ByteString

class NotificationPDFServiceSpec extends AnyWordSpec with ScalaFutures
    with TryValues with Matchers with MockitoSugar  {

  val connector: DigitalDisclosureServiceConnector = mock[DigitalDisclosureServiceConnector]
  val storeDataService: StoreDataService = mock[StoreDataService]

  val sut = new NotificationPDFServiceImpl(connector, storeDataService)

  val emptyUA = UserAnswers("id")
  val testNotification = Notification("123", "456", Instant.now(), Metadata(), Background(), AboutYou())
  implicit val hc = HeaderCarrier()

  "generatePdf" should {

    "call userAnswersToNotification followed by generateNotificationPDF" in {
      when(storeDataService.userAnswersToNotification(emptyUA)) thenReturn testNotification
      when(connector.generateNotificationPDF(testNotification)(hc)) thenReturn Future.successful(ByteString("1234"))
      
      sut.generatePdf(emptyUA).futureValue shouldEqual ByteString("1234")
    }

  }

}