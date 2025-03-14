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

sealed trait IncomeOrGainSource

object IncomeOrGainSource extends Enumerable.Implicits {

  case object Dividends extends WithName("dividends") with IncomeOrGainSource
  case object Interest extends WithName("interest") with IncomeOrGainSource
  case object PropertyIncome extends WithName("propertyIncome") with IncomeOrGainSource
  case object ResidentialPropertyGain extends WithName("residentialPropertyGain") with IncomeOrGainSource
  case object SelfEmploymentIncome extends WithName("selfEmploymentIncome") with IncomeOrGainSource
  case object OtherGains extends WithName("otherGains") with IncomeOrGainSource
  case object SomewhereElse extends WithName("somewhereElse") with IncomeOrGainSource

  val values: Seq[IncomeOrGainSource] = Seq(
    Dividends,
    Interest,
    PropertyIncome,
    ResidentialPropertyGain,
    SelfEmploymentIncome,
    OtherGains,
    SomewhereElse
  )

  def checkboxItems(implicit messages: Messages): Seq[CheckboxItem] =
    values.zipWithIndex.map { case (value, index) =>
      CheckboxItemViewModel(
        content = Text(messages(s"whereDidTheUndeclaredIncomeOrGainIncluded.${value.toString}")),
        fieldId = "value",
        index = index,
        value = value.toString
      )
    }

  implicit val enumerable: Enumerable[IncomeOrGainSource] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
