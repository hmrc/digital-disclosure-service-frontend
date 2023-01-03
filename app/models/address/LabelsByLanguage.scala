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

final case class LabelsByLanguage(
  appLevelLabels: AppLevelLabels,
  countryPickerLabels: CountryPickerLabels,
  lookupPageLabels: LookupPageLabels,
  selectPageLabels: SelectPageLabels,
  editPageLabels: EditPageLabels,
  confirmPageLabels: ConfirmPageLabels,
  international: InternationalLabels
)

object LabelsByLanguage {
  implicit val format: OFormat[LabelsByLanguage] = Json.format[LabelsByLanguage]
}

final case class InternationalLabels(
  lookupPageLabels: LookupPageLabels,
  selectPageLabels: SelectPageLabels,
  editPageLabels: EditPageLabels,
  confirmPageLabels: ConfirmPageLabels
)

object InternationalLabels {
  implicit val format: OFormat[InternationalLabels] = Json.format[InternationalLabels]
}