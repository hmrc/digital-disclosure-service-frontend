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

package models.store.notification

import java.time.LocalDate
import play.api.libs.json.{Json, OFormat}
import models.store.YesNoOrUnsure
import models.store.YesNoOrUnsure._
import models.address.Address

final case class AboutTheEstate (
  fullName: Option[String] = None,
  dateOfBirth: Option[LocalDate] = None,
  mainOccupation: Option[String] = None,
  doTheyHaveANino: Option[YesNoOrUnsure] = None,
  nino: Option[String] = None,
  registeredForVAT: Option[YesNoOrUnsure] = None,
  vatRegNumber: Option[String] = None,
  registeredForSA: Option[YesNoOrUnsure] = None,
  sautr: Option[String] = None,
  address: Option[Address] = None
) {
  def isComplete = this match {
    case AboutTheEstate(Some(_), Some(_), Some(_), Some(hasNino), nino, Some(hasVAT), vatRegNumber, Some(hasSA), sautr, Some(_)) => 
      ( (hasNino != Yes || nino.isDefined) && (hasVAT != Yes || vatRegNumber.isDefined) && (hasSA != Yes || sautr.isDefined) )
    case _ => false
  }
}

object AboutTheEstate {
  implicit val format: OFormat[AboutTheEstate] = Json.format[AboutTheEstate]
}