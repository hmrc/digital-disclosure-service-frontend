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

import models.AreYouTheEntity
import play.api.libs.json._

sealed trait Entity
object Entity {
  implicit val reads: Reads[Entity] = Reads {
    case JsString("Individual") => JsSuccess(Individual)
    case JsString("Estate") => JsSuccess(Estate)
    case JsString("Company") => JsSuccess(Company)
    case JsString("LLP") => JsSuccess(LLP)
    case JsString("Trust") => JsSuccess(Trust)
    case _ => JsError("error.invalid")
  }

  implicit val writes: Writes[Entity] = Writes[Entity] {
    case Individual => Json.toJson("Individual")
    case Estate => Json.toJson("Estate")
    case Company => Json.toJson("Company")
    case LLP => Json.toJson("LLP")
    case Trust => Json.toJson("Trust")
  }

  implicit val format: Format[Entity] =  Format(reads, writes)
}

case object Individual extends Entity
case object Estate extends Entity
case object Company extends Entity
case object LLP extends Entity
case object Trust extends Entity

final case class DisclosureEntity(entity: Entity, areYouTheEntity: Option[AreYouTheEntity] = None)

object DisclosureEntity {
  implicit val format: OFormat[DisclosureEntity] = Json.format[DisclosureEntity]
}