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

package models.store.disclosure

import play.api.libs.json.{Json, OFormat}
import java.time.Instant
import play.api.Logging 
import models.store.{CustomerId, Metadata}
import models.store.notification.PersonalDetails

final case class FullDisclosure (
  userId: String,
  disclosureId: String,
  lastUpdated: Instant,
  metadata: Metadata,
  caseReference: CaseReference,
  personalDetails: PersonalDetails,
  offshoreLiabilities: OffshoreLiabilities,
  otherLiabilities: OtherLiabilities,
  reasonForDisclosingNow: ReasonForDisclosingNow,
  customerId: Option[CustomerId] = None
) extends Logging {

  lazy val disclosingAboutThemselves: Boolean = personalDetails.disclosingAboutThemselves

  lazy val isSubmitted: Boolean = metadata.submissionTime.isDefined

  lazy val disclosingOffshoreLiabilities: Boolean = personalDetails.background.offshoreLiabilities.getOrElse(false)

  lazy val isComplete: Boolean = 
    caseReference.isComplete && 
    personalDetails.isComplete && 
    otherLiabilities.isComplete(personalDetails.isAnIndividual) && 
    reasonForDisclosingNow.isComplete && 
    (!disclosingOffshoreLiabilities || offshoreLiabilities.isComplete)
}

object FullDisclosure {
  implicit val format: OFormat[FullDisclosure] = Json.format[FullDisclosure]
}