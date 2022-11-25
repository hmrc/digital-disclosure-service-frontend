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

import play.api.libs.json.{Json, OFormat}

sealed trait CustomerId {
  def id: String
}

final case class NINO(id: String) extends CustomerId
object NINO {
  implicit val format: OFormat[NINO] = Json.format[NINO]
}

final case class CAUTR(id: String) extends CustomerId
object CAUTR {
  implicit val format: OFormat[CAUTR] = Json.format[CAUTR]
}
final case class SAUTR(id: String) extends CustomerId
object SAUTR {
  implicit val format: OFormat[SAUTR] = Json.format[SAUTR]
}

final case class ARN(id: String) extends CustomerId
object ARN {
  implicit val format: OFormat[ARN] = Json.format[ARN]
}

object CustomerId {
  implicit val format: OFormat[CustomerId] = Json.format[CustomerId]
}