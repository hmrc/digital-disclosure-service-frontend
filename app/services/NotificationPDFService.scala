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

import uk.gov.hmrc.http.HeaderCarrier
import connectors.DigitalDisclosureServiceConnector
import com.google.inject.{Inject, Singleton, ImplementedBy}
import scala.concurrent.{ExecutionContext, Future}
import models.UserAnswers
import akka.util.ByteString

@Singleton
class NotificationPDFServiceImpl @Inject()(
  connector: DigitalDisclosureServiceConnector,
  storeDataService: StoreDataService
) extends NotificationPDFService {

  def generatePdf(userAnswers: UserAnswers)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ByteString] = {
    val notification = storeDataService.userAnswersToNotification(userAnswers)
    connector.generateNotificationPDF(notification)
  }

}

@ImplementedBy(classOf[NotificationPDFServiceImpl])
trait NotificationPDFService {
  def generatePdf(userAnswers: UserAnswers)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ByteString]
}