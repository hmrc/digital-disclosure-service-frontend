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

import com.google.inject.{Inject, Singleton, ImplementedBy}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.http.HeaderCarrier
import models.store.notification.Notification
import scala.concurrent.ExecutionContext

@Singleton
class AuditServiceImpl @Inject()(
  connector: AuditConnector,
)(implicit ec: ExecutionContext) extends AuditService {

  val AUDIT_TYPE = "NotificationSubmission"

  def auditSubmission(notification: Notification)(implicit hc: HeaderCarrier): Unit = connector.sendExplicitAudit(AUDIT_TYPE, notification)

}

@ImplementedBy(classOf[AuditServiceImpl])
trait AuditService {
  def auditSubmission(notification: Notification)(implicit hc: HeaderCarrier): Unit
}