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
import uk.gov.hmrc.time.{CurrentTaxYear, TaxYear}
import java.time.LocalDate

object WhichYears extends CurrentTaxYear {

  sealed trait Behaviour {
    def numberOfYears: Int
  }

  case object ReasonableExcuse extends Behaviour {
    val numberOfYears = 6
  }
  case object Careless extends Behaviour {
    val numberOfYears = 8
  }
  case object Deliberate extends Behaviour {
    val numberOfYears = 19
  }

  def now = () => LocalDate.now()

  def checkboxItems(behaviour: Behaviour)(implicit messages: Messages): Seq[CheckboxItem] = { 

    val checkboxItem = behaviour match {
      case ReasonableExcuse => Seq(createReasonableExcusePriorToCheckbox(behaviour.numberOfYears, current)(messages))
      case Careless         => Seq(createCarelessPriorToCheckbox(behaviour.numberOfYears, current)(messages))
      case Deliberate       => Seq(createDeliberatePriorToCheckbox(behaviour.numberOfYears, current)(messages))
    }

    createYearCheckboxes(behaviour.numberOfYears, current)(messages) ++ checkboxItem
  }

  def createYearCheckboxes(numberOfYears: Int, currentTaxYear: TaxYear)(implicit messages: Messages): Seq[CheckboxItem] = {
    Range.inclusive(0, numberOfYears-1).toList.map {i =>
      val taxYear = currentTaxYear.back(i+1)
      CheckboxItemViewModel(
        content = Text(messages(s"whichYears.checkbox", s"${taxYear.startYear}", s"${taxYear.finishYear}")),
        fieldId = "value",
        index   = i,
        value   = taxYear.startYear.toString
      )
    }
  }

  def createReasonableExcusePriorToCheckbox(numberOfYears: Int, currentTaxYear: TaxYear)(implicit messages: Messages): CheckboxItem = {
    val taxYear = currentTaxYear.back(6)
    CheckboxItemViewModel(
      content = Text(messages(s"whichYears.checkbox.any", s"${taxYear.startYear}")),
      fieldId = "value",
      index   = numberOfYears,
      value   = "reasonableExcusePriorTo"
    )
  }

  def createCarelessPriorToCheckbox(numberOfYears: Int, currentTaxYear: TaxYear)(implicit messages: Messages): CheckboxItem = {
    val taxYear = currentTaxYear.back(numberOfYears)
    CheckboxItemViewModel(
      content = Text(messages(s"whichYears.checkbox.any", s"${taxYear.startYear}")),
      fieldId = "value",
      index   = numberOfYears,
      value   = "carelessPriorTo"
    )
  }

  def createDeliberatePriorToCheckbox(numberOfYears: Int, currentTaxYear: TaxYear)(implicit messages: Messages): CheckboxItem = {
    val taxYear = currentTaxYear.back(numberOfYears)
    CheckboxItemViewModel(
      content = Text(messages(s"whichYears.checkbox.any", s"${taxYear.startYear}")),
      fieldId = "value",
      index = numberOfYears,
      value = "deliberatePriorTo"
    )
  }
    
}