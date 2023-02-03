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
import scala.concurrent.{ExecutionContext, Future}
import models.store.Metadata
import connectors.DigitalDisclosureServiceConnector
import com.google.inject.{Inject, Singleton, ImplementedBy}
import models.UserAnswers
import pages.LetterReferencePage

@Singleton
class DisclosureSubmissionServiceImpl @Inject()(
  connector: DigitalDisclosureServiceConnector,
  uaToDisclosureService: UAToDisclosureService,
  referenceService: ReferenceService,
  sessionService: SessionService,
  timeService: TimeService,
  auditService: AuditService
) extends DisclosureSubmissionService {

  def submitDisclosure(userAnswers: UserAnswers)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[String] = {
        
    val reference = userAnswers.get(LetterReferencePage).getOrElse(referenceService.generateReference.toString)
    val metadata = Metadata(reference = Some(reference), submissionTime = Some(timeService.now))

    val updatedUserAnswers = userAnswers.copy(metadata = metadata)
    val fullDisclosure = uaToDisclosureService.uaToFullDisclosure(updatedUserAnswers)

    for {
      _ <- connector.submitDisclosure(fullDisclosure)
      _ = auditService.auditDisclosureSubmission(fullDisclosure)
      _ <- sessionService.set(updatedUserAnswers)
    } yield reference

  }

}

@ImplementedBy(classOf[DisclosureSubmissionServiceImpl])
trait DisclosureSubmissionService {
  def submitDisclosure(userAnswers: UserAnswers)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[String]
}