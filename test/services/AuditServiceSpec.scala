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
import org.mockito.ArgumentMatchers.{any, refEq}
import org.mockito.Mockito.verify
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import scala.concurrent.ExecutionContext.Implicits.global
import models.store.notification._
import java.time.Instant
import uk.gov.hmrc.http.HeaderCarrier

class AuditServiceSpec extends AnyWordSpec with Matchers with MockitoSugar {

  val mockConnector = mock[AuditConnector]
  val sut = new AuditServiceImpl(mockConnector)

  "auditSubmission" should {

    "call connector passing the notification with audit type NotificationSubmission" in {
      implicit val hc = HeaderCarrier()
      val notification = Notification("This user Id", "Some notification Id", Instant.now(), Metadata(), Background(), AboutYou())
      sut.auditSubmission(notification)
      verify(mockConnector).sendExplicitAudit(refEq("NotificationSubmission"), refEq(notification))(any(), any(), any())
    }

  }

}