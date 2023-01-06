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
import java.time.Year

final case class TaxYear(finalYear: Int) {

  val startYear = finalYear - 1
  
  def minusYears(numberOfYears: Int): TaxYear = {
    val newFinalYear = finalYear - numberOfYears
    TaxYear(newFinalYear)
  }
}

object WhichYearDoesThisOffshoreDisclosureRelateTo {

  def checkboxItems(currentYear: TaxYear, numberOfYears: Int)(implicit messages: Messages): Seq[CheckboxItem] = {
    createCheckboxItems(currentYear, numberOfYears)(messages) :+ createLastCheckbox(currentYear, numberOfYears)(messages)
  }

  def createCheckboxItems(currentYear: TaxYear, numberOfYears: Int)(implicit messages: Messages): Seq[CheckboxItem] = {
    Range.inclusive(1,numberOfYears).toList.map {i =>
      val taxYear = currentYear.minusYears(i)
      CheckboxItemViewModel(
        content = Text(messages(s"whichYearDoesThisOffshoreDisclosureRelateTo.checkbox.first", s"${taxYear.startYear}", s"${taxYear.finalYear}")),
        fieldId = "value",
        index   = i,
        value   = taxYear.finalYear.toString
      )
    }
  }

  def createLastCheckbox(currentYear: TaxYear, numberOfYears: Int)(implicit messages: Messages): CheckboxItem = {
    CheckboxItemViewModel(
      content = Text(messages(s"whichYearDoesThisOffshoreDisclosureRelateTo.checkbox.last", s"${currentYear.finalYear}")),
      fieldId = "value",
      index   = numberOfYears+1,
      value   = (numberOfYears+1).toString
    )
  }

}
