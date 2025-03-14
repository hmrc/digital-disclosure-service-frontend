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

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import viewmodels.govuk.HintFluency

sealed trait RelatesTo

object RelatesTo extends Enumerable.Implicits with HintFluency {

  case object AnIndividual extends WithName("individual") with RelatesTo
  case object AnEstate extends WithName("estate") with RelatesTo
  case object ACompany extends WithName("company") with RelatesTo
  case object ALimitedLiabilityPartnership extends WithName("llp") with RelatesTo
  case object ATrust extends WithName("trust") with RelatesTo

  val values: Seq[RelatesTo] = Seq(
    AnIndividual,
    AnEstate,
    ACompany,
    ALimitedLiabilityPartnership,
    ATrust
  )

  def options(implicit messages: Messages): Seq[RadioItem] = values.zipWithIndex.map { case (value, index) =>
    RadioItem(
      content = Text(messages(s"relatesTo.${value.toString}")),
      value = Some(value.toString),
      id = Some(s"value_$index"),
      hint = Some(HintViewModel(Text(messages(s"relatesTo.hint.${value.toString}"))))
    )
  }

  implicit val enumerable: Enumerable[RelatesTo] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
