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

import play.api.libs.json.{Format, JsError, JsString, JsSuccess, Json, OFormat, Reads, Writes}

sealed trait Preference
object Preference {
  implicit val reads: Reads[Preference] = Reads {
    case JsString("Email")     => JsSuccess(Email)
    case JsString("Telephone") => JsSuccess(Telephone)
    case _                     => JsError("error.invalid")
  }

  implicit val writes: Writes[Preference] = Writes[Preference] {
    case Email     => Json.toJson("Email")
    case Telephone => Json.toJson("Telephone")
  }

  implicit val format: Format[Preference] = Format(reads, writes)
}

case object Email extends Preference
case object Telephone extends Preference

final case class ContactPreferences(preferences: Set[Preference])

object ContactPreferences {
  implicit val format: OFormat[ContactPreferences] = Json.format[ContactPreferences]
}
