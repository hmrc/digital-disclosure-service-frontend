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

sealed trait YourLegalInterpretation

object YourLegalInterpretation extends Enumerable.Implicits {

  case object Yourresidencestatus extends WithName("yourResidenceStatus") with YourLegalInterpretation
  case object Yourdomicilestatus extends WithName("yourDomicileStatus") with YourLegalInterpretation
  case object TheRemittanceBasis extends WithName("theRemittanceBasis") with YourLegalInterpretation
  case object HowIncomeArisingInATrust extends WithName("howIncomeArisingInATrust") with YourLegalInterpretation
  case object TheTransferOfAssets extends WithName("theTransferOfAssets") with YourLegalInterpretation
  case object HowIncomeArisingInAnOffshore extends WithName("howIncomeArisingInAnOffshore") with YourLegalInterpretation
  case object InheritanceTaxIssues extends WithName("inheritanceTaxIssues") with YourLegalInterpretation
  case object WhetherIncomeShouldBeTaxed extends WithName("whetherIncomeShouldBeTaxed") with YourLegalInterpretation
  case object AnotherIssue extends WithName("anotherIssue") with YourLegalInterpretation
  case object NoExclusion extends WithName("noExclusion") with YourLegalInterpretation

  val values: Seq[YourLegalInterpretation] = Seq(
    Yourresidencestatus,
    Yourdomicilestatus,
    TheRemittanceBasis,
    HowIncomeArisingInATrust,
    TheTransferOfAssets,
    HowIncomeArisingInAnOffshore,
    InheritanceTaxIssues,
    WhetherIncomeShouldBeTaxed,
    AnotherIssue,
    NoExclusion
  )

  def checkboxItems(implicit messages: Messages): Seq[CheckboxItem] = {
    val checkboxes = values.zipWithIndex.map {
      case (value, index) =>
        CheckboxItemViewModel(
          content = Text(messages(s"yourLegalInterpretation.${value.toString}")),
          fieldId = "value",
          index   = index,
          value   = value.toString
        )
    }

    val divider = CheckboxItem(divider = Some("or"))   

    checkboxes.dropRight(1) :+ divider :+ checkboxes.last  
  }  

  implicit val enumerable: Enumerable[YourLegalInterpretation] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
