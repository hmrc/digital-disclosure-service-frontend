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

sealed trait OtherLiabilityIssues

object OtherLiabilityIssues extends Enumerable.Implicits {

  case object EmployerLiabilities extends WithName("employerLiabilities") with OtherLiabilityIssues 
  case object VatIssues extends WithName("vatIssues") with OtherLiabilityIssues
  case object InheritanceTaxIssues extends WithName("inheritanceTaxIssues") with OtherLiabilityIssues
  case object Class2National extends WithName("class2National") with OtherLiabilityIssues
  case object Other extends WithName("other") with OtherLiabilityIssues
  case object NoExclusion extends WithName("noExclusion") with OtherLiabilityIssues

  val values: Seq[OtherLiabilityIssues] = Seq(
    EmployerLiabilities,
    Class2National,
    InheritanceTaxIssues,
    VatIssues,
    Other,
    NoExclusion
  )

  def checkboxItems(implicit messages: Messages): Seq[CheckboxItem] = {
    val checkboxes = values.zipWithIndex.map {
      case (value, index) =>
        CheckboxItemViewModel(
          content = Text(messages(s"otherLiabilityIssues.${value.toString}")),
          fieldId = "value",
          index   = index,
          value   = value.toString
        )
    }
    val divider = CheckboxItem(divider = Some(messages("site.or")))   
    checkboxes.dropRight(1) :+ divider :+ checkboxes.last 
  }

  implicit val enumerable: Enumerable[OtherLiabilityIssues] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
