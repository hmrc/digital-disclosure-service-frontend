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

final case class AboutYou (
  fullName: Option[String] = None,
  telephoneNumber: Option[String] = None,
  emailAddress: Option[String] = None,
  dateOfBirth: Option[LocalDate] = None,
  mainOccupation: Option[String] = None,
  contactPreference: Option[ContactPreferences] = None,
  doYouHaveANino: Option[YesNoOrUnsure] = None,
  nino: Option[String] = None,
  registeredForVAT: Option[YesNoOrUnsure] = None,
  vatRegNumber: Option[String] = None,
  registeredForSA: Option[YesNoOrUnsure] = None,
  sautr: Option[String] = None,
  address: Option[Address] = None
) {
  def isComplete(isTheIndividual: Boolean) = (isTheIndividual, this) match {
    case (true, AboutYou(Some(_), _, _, Some(_), Some(_), Some(ContactPreferences(contactPrefs)), Some(hasNino), nino, Some(hasVAT), vatRegNumber, Some(hasSA), sautr, Some(_))) => 
      val ninoQuestionsPopulated = (hasNino != Yes || nino.isDefined)
      val vatQuestionsPopulated = (hasVAT != Yes || vatRegNumber.isDefined)
      val saQuestionsPopulated = (hasSA != Yes || sautr.isDefined)
      val contactQuestionsPopulated = (!contactPrefs.contains(Email) || emailAddress.isDefined) && (!contactPrefs.contains(Telephone) || telephoneNumber.isDefined)
      ( ninoQuestionsPopulated && vatQuestionsPopulated && saQuestionsPopulated && contactQuestionsPopulated )
    case (false, AboutYou(Some(_), _, _, _, _, Some(ContactPreferences(contactPrefs)), _, _, _, _, _, _, Some(_))) => 
      (!contactPrefs.contains(Email) || emailAddress.isDefined) && (!contactPrefs.contains(Telephone) || telephoneNumber.isDefined)
    case _ => false
  }
}

object AboutYou {
  implicit val format: OFormat[AboutYou] = Json.format[AboutYou]
}