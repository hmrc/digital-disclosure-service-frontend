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
import uk.gov.hmrc.govukfrontend.views.viewmodels.hint.Hint
import viewmodels.govuk.checkbox._
import uk.gov.hmrc.time.{CurrentTaxYear, TaxYear}
import java.time.LocalDate

object WhichYears extends CurrentTaxYear {

  def now = () => LocalDate.now()

  def checkboxItems(numberOfYears: Int)(implicit messages: Messages): Seq[CheckboxItem] = { 
    
    val checkboxItem = numberOfYears match {
      case 5  => Seq(createPriorTo5YearsCheckbox(numberOfYears, current)(messages))
      case 7  => Seq(createPriorTo7YearsCheckbox(numberOfYears, current)(messages))
      case 19 => Seq(createNoneOfTheseCheckbox(numberOfYears)(messages))
      case _  => Nil
    }

    createYearCheckboxes(numberOfYears, current)(messages) ++ checkboxItem
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

  def createPriorTo5YearsCheckbox(numberOfYears: Int, currentTaxYear: TaxYear)(implicit messages: Messages): CheckboxItem = {
    val taxYear = currentTaxYear.back(numberOfYears)
    CheckboxItemViewModel(
      content = Text(messages(s"whichYears.checkbox.any", s"${taxYear.startYear}")),
      fieldId = "value",
      index   = numberOfYears,
      value   = "priorTo5Years"
    )
  }

  def createPriorTo7YearsCheckbox(numberOfYears: Int, currentTaxYear: TaxYear)(implicit messages: Messages): CheckboxItem = {
    val taxYear = currentTaxYear.back(numberOfYears)
    CheckboxItemViewModel(
      content = Text(messages(s"whichYears.checkbox.any", s"${taxYear.startYear}")),
      fieldId = "value",
      index   = numberOfYears,
      value   = "priorTo7Years"
    )
  }

  def createNoneOfTheseCheckbox(numberOfYears: Int)(implicit messages: Messages): CheckboxItem = {
    CheckboxItemViewModel(
      content = Text(messages(s"whichYears.checkbox.none")),
      fieldId = "value",
      index   = numberOfYears,
      value   = "noneOfTheseYears"
    ).withHint(Hint(content = Text(messages("whichYears.checkbox.none.hint"))))
  }
    
}