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

package models.store.notification

import java.time.LocalDate
import play.api.libs.json.{Json, OFormat}
import models.store.YesNoOrUnsure
import models.address.Address

final case class AboutYou (
  fullName: Option[String] = None,
  telephoneNumber: Option[String] = None,
  doYouHaveAEmailAddress: Option[Boolean] = None,
  emailAddress: Option[String] = None,
  dateOfBirth: Option[LocalDate] = None,
  mainOccupation: Option[String] = None,
  doYouHaveANino: Option[YesNoOrUnsure] = None,
  nino: Option[String] = None,
  registeredForVAT: Option[YesNoOrUnsure] = None,
  vatRegNumber: Option[String] = None,
  registeredForSA: Option[YesNoOrUnsure] = None,
  sautr: Option[String] = None,
  address: Option[Address] = None
)

object AboutYou {
  implicit val format: OFormat[AboutYou] = Json.format[AboutYou]
}