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

package models

import models.address.Address
import java.time.LocalDate
import play.api.libs.json._

final case class LettingProperty(
  address: Option[Address] = None,
  dateFirstLetOut: Option[LocalDate] = None,
  stoppedBeingLetOut: Option[Boolean] = None,
  noLongerBeingLetOut: Option[NoLongerBeingLetOut] = None,
  fhl: Option[Boolean] = None,
  isJointOwnership: Option[Boolean] = None,
  isMortgageOnProperty: Option[Boolean] = None,
  percentageIncomeOnProperty: Option[Int] = None
)

object LettingProperty {
  implicit val format = Json.format[LettingProperty]
}

final case class NoLongerBeingLetOut (
  stopDate: LocalDate,
  whatHasHappenedToProperty: String
)

object NoLongerBeingLetOut {
  implicit val noLongerBeingLetOutFormat: OFormat[NoLongerBeingLetOut] = Json.format[NoLongerBeingLetOut]
}
