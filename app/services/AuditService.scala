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

import com.google.inject.{Inject, Singleton, ImplementedBy}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.http.HeaderCarrier
import models.store.{FullDisclosure, Notification}
import scala.concurrent.ExecutionContext

@Singleton
class AuditServiceImpl @Inject()(
  connector: AuditConnector
)(implicit ec: ExecutionContext) extends AuditService {

  val NOTIFICATION_AUDIT_TYPE = "NotificationSubmission"
  val DISCLOSURE_AUDIT_TYPE = "DisclosureSubmission"

  def auditNotificationSubmission(notification: Notification)(implicit hc: HeaderCarrier): Unit = connector.sendExplicitAudit(NOTIFICATION_AUDIT_TYPE, notification)
  def auditDisclosureSubmission(disclosure: FullDisclosure)(implicit hc: HeaderCarrier): Unit = connector.sendExplicitAudit(DISCLOSURE_AUDIT_TYPE, disclosure)

}

@ImplementedBy(classOf[AuditServiceImpl])
trait AuditService {
  def auditNotificationSubmission(notification: Notification)(implicit hc: HeaderCarrier): Unit
  def auditDisclosureSubmission(disclosure: FullDisclosure)(implicit hc: HeaderCarrier): Unit
}