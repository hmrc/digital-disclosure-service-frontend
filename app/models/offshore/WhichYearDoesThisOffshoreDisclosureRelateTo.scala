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
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.checkbox._

sealed trait WhichYearDoesThisOffshoreDisclosureRelateTo

object WhichYearDoesThisOffshoreDisclosureRelateTo extends Enumerable.Implicits {

  case object Checkbox1 extends WithName("checkbox1") with WhichYearDoesThisOffshoreDisclosureRelateTo
  case object Checkbox2 extends WithName("checkbox2") with WhichYearDoesThisOffshoreDisclosureRelateTo
  case object Checkbox3 extends WithName("checkbox3") with WhichYearDoesThisOffshoreDisclosureRelateTo
  case object Checkbox4 extends WithName("checkbox4") with WhichYearDoesThisOffshoreDisclosureRelateTo
  case object Checkbox5 extends WithName("checkbox5") with WhichYearDoesThisOffshoreDisclosureRelateTo
  case object Checkbox6 extends WithName("checkbox6") with WhichYearDoesThisOffshoreDisclosureRelateTo
  case object Checkbox7 extends WithName("checkbox7") with WhichYearDoesThisOffshoreDisclosureRelateTo
  case object Checkbox8 extends WithName("checkbox8") with WhichYearDoesThisOffshoreDisclosureRelateTo
  case object Checkbox9 extends WithName("checkbox9") with WhichYearDoesThisOffshoreDisclosureRelateTo
  case object Checkbox10 extends WithName("checkbox10") with WhichYearDoesThisOffshoreDisclosureRelateTo
  case object Checkbox11 extends WithName("checkbox11") with WhichYearDoesThisOffshoreDisclosureRelateTo
  case object Checkbox12 extends WithName("checkbox12") with WhichYearDoesThisOffshoreDisclosureRelateTo
  case object Checkbox13 extends WithName("checkbox13") with WhichYearDoesThisOffshoreDisclosureRelateTo
  case object Checkbox14 extends WithName("checkbox14") with WhichYearDoesThisOffshoreDisclosureRelateTo
  case object Checkbox15 extends WithName("checkbox15") with WhichYearDoesThisOffshoreDisclosureRelateTo
  case object Checkbox16 extends WithName("checkbox16") with WhichYearDoesThisOffshoreDisclosureRelateTo
  case object Checkbox17 extends WithName("checkbox17") with WhichYearDoesThisOffshoreDisclosureRelateTo
  case object Checkbox18 extends WithName("checkbox18") with WhichYearDoesThisOffshoreDisclosureRelateTo
  case object Checkbox19 extends WithName("checkbox19") with WhichYearDoesThisOffshoreDisclosureRelateTo
  case object Checkbox20 extends WithName("checkbox20") with WhichYearDoesThisOffshoreDisclosureRelateTo

  val values: Seq[WhichYearDoesThisOffshoreDisclosureRelateTo] = Seq(
    Checkbox1,
    Checkbox2,
    Checkbox3,
    Checkbox4,
    Checkbox5,
    Checkbox6,
    Checkbox7,
    Checkbox8,
    Checkbox9,
    Checkbox10,
    Checkbox11,
    Checkbox12,
    Checkbox13,
    Checkbox14,
    Checkbox15,
    Checkbox16,
    Checkbox17,
    Checkbox18,
    Checkbox19,
    Checkbox20
  )

  def checkboxItems(implicit messages: Messages): Seq[CheckboxItem] =
    values.zipWithIndex.map {
      case (value, index) =>
        CheckboxItemViewModel(
          content = Text(messages(s"whichYearDoesThisOffshoreDisclosureRelateTo.${value.toString}")),
          fieldId = "value",
          index   = index,
          value   = value.toString
        )
    }

  implicit val enumerable: Enumerable[WhichYearDoesThisOffshoreDisclosureRelateTo] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
