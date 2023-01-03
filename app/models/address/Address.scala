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

import play.api.i18n.Messages
import play.api.libs.json.{Json, OFormat}

final case class Address(
  line1: String,
  line2: Option[String],
  line3: Option[String],
  line4: Option[String],
  postcode: Option[String],
  country: Country
)

object Address {

  implicit class AddressOps(private val a: Address) extends AnyVal {

    def getAddressLines(implicit messages: Messages): List[String] = {
      val lines =
        List(
          Some(a.line1),
          a.line2,
          a.line3,
          a.line4,
          a.postcode,
          messages.translate(s"country.${a.country.code}", Seq.empty)
        )

      lines.collect { case Some(s) => s }
    }
  }

  implicit val addressFormat: OFormat[Address] = Json.format[Address]
}