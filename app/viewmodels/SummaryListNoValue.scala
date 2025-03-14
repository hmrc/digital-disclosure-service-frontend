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

package viewmodels

import play.api.libs.json._
import play.api.libs.functional.syntax._
import uk.gov.hmrc.govukfrontend.views.viewmodels.CommonJsonFormats._
import viewmodels.SummaryListRowNoValue

final case class SummaryListNoValue(
  rows: Seq[SummaryListRowNoValue] = Nil,
  classes: String = "",
  attributes: Map[String, String] = Map.empty
)

object SummaryListNoValue {

  def defaultObject: SummaryListNoValue = SummaryListNoValue()

  implicit def jsonReads: Reads[SummaryListNoValue] = (
    (__ \ "rows").readWithDefault[Seq[SummaryListRowNoValue]](defaultObject.rows)(
      forgivingSeqReads[SummaryListRowNoValue]
    ) and
      (__ \ "classes").readWithDefault[String](defaultObject.classes) and
      (__ \ "attributes").readWithDefault[Map[String, String]](defaultObject.attributes)(attributesReads)
  )(SummaryListNoValue.apply _)

  implicit def jsonWrites: OWrites[SummaryListNoValue] = Json.writes[SummaryListNoValue]
}
