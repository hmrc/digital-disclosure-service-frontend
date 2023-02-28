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

package models.address

import play.api.libs.json.Json
import play.api.libs.json.OFormat

final case class EditPageLabels(
  title: String,
  heading: String,
  line1Label: String = "Address line 1",
  line2Label: String = "Address line 2 (optional)",
  line3Label: String = "Address line 3 (optional)",
  townLabel: String = "Town or city",
  postcodeLabel: String = "Postcode (optional)",
  countryLabel: String = "Country"
)

object EditPageLabels {
  implicit val format: OFormat[EditPageLabels] = Json.format[EditPageLabels]
}