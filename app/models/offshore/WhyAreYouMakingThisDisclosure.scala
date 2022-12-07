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
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.checkbox._

sealed trait WhyAreYouMakingThisDisclosure

object WhyAreYouMakingThisDisclosure extends Enumerable.Implicits {

  case object Checkbox1 extends WithName("checkbox1") with WhyAreYouMakingThisDisclosure
  case object Checkbox2 extends WithName("checkbox2") with WhyAreYouMakingThisDisclosure
  case object Checkbox3 extends WithName("checkbox3") with WhyAreYouMakingThisDisclosure
  case object Checkbox4 extends WithName("checkbox4") with WhyAreYouMakingThisDisclosure
  case object Checkbox5 extends WithName("checkbox5") with WhyAreYouMakingThisDisclosure
  case object Checkbox6 extends WithName("checkbox6") with WhyAreYouMakingThisDisclosure
  case object Checkbox7 extends WithName("checkbox7") with WhyAreYouMakingThisDisclosure
  case object Checkbox8 extends WithName("checkbox8") with WhyAreYouMakingThisDisclosure

  val values: Seq[WhyAreYouMakingThisDisclosure] = Seq(
    Checkbox1,
    Checkbox2,
    Checkbox3,
    Checkbox4,
    Checkbox5,
    Checkbox6,
    Checkbox7,
    Checkbox8
  )

  def checkboxItems(implicit messages: Messages): Seq[CheckboxItem] =
    values.zipWithIndex.map {
      case (value, index) =>
        CheckboxItemViewModel(
          content = Text(messages(s"whyAreYouMakingThisDisclosure.${value.toString}")),
          fieldId = "value",
          index   = index,
          value   = value.toString
        )
    }

  implicit val enumerable: Enumerable[WhyAreYouMakingThisDisclosure] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
