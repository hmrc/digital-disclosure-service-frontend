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

package models

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait DoYouHaveNationalInsuranceNumber

object DoYouHaveNationalInsuranceNumber extends Enumerable.Implicits {

  case object YesIknow extends WithName("yesIKnow") with DoYouHaveNationalInsuranceNumber
  case object YesButDontKnow extends WithName("yesButDontKnow") with DoYouHaveNationalInsuranceNumber
  case object No extends WithName("no") with DoYouHaveNationalInsuranceNumber

  val values: Seq[DoYouHaveNationalInsuranceNumber] = Seq(
    YesIknow, YesButDontKnow, No
  )

  def options(implicit messages: Messages): Seq[RadioItem] = values.zipWithIndex.map {
    case (value, index) =>
      RadioItem(
        content = Text(messages(s"doYouHaveNationalInsuranceNumber.${value.toString}")),
        value   = Some(value.toString),
        id      = Some(s"value_$index")
      )
  }

  implicit val enumerable: Enumerable[DoYouHaveNationalInsuranceNumber] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
