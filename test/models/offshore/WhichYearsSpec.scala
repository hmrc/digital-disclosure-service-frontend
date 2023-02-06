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

import base.SpecBase
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.time.CurrentTaxYear
import java.time.LocalDate

class WhichYearsSpec extends SpecBase with CurrentTaxYear {

  def now = () => LocalDate.now()
  val currentTaxYear = current

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

  implicit val mess = messages(application)

  "WhichYearsSpec" - {

    "must create a checkbox for none of these years row" in {
      val checkbox = WhichYears.createDeliberatePriorToCheckbox(19, currentTaxYear)
      checkbox.content mustEqual Text(mess(s"whichYears.checkbox.any", s"${currentTaxYear.back(19).startYear}"))
      checkbox.id mustEqual Some("value_19")
      checkbox.name mustEqual Some("value[19]")
      checkbox.value mustEqual "deliberatePriorTo"
    }

    "must create a checkbox for any prior to 8 years row" in {
      val checkbox = WhichYears.createCarelessPriorToCheckbox(8, currentTaxYear)
      checkbox.content mustEqual Text(mess(s"whichYears.checkbox.any", s"${currentTaxYear.back(8).startYear}"))
      checkbox.id mustEqual Some("value_8")
      checkbox.name mustEqual Some("value[8]")
      checkbox.value mustEqual "carelessPriorTo"
    }

    "must create a checkbox for any prior to 6 years row" in {
      val checkbox = WhichYears.createReasonableExcusePriorToCheckbox(6, currentTaxYear)
      checkbox.content mustEqual Text(mess(s"whichYears.checkbox.any", s"${currentTaxYear.back(6).startYear}"))
      checkbox.id mustEqual Some("value_6")
      checkbox.name mustEqual Some("value[6]")
      checkbox.value mustEqual "reasonableExcusePriorTo"
    }

    "must create checkbox items for 19 years" in {
      val checkboxItems = WhichYears.createYearCheckboxes(19, currentTaxYear)
      checkboxItems.zipWithIndex.map{ 
        case (value, index) =>
          value.content mustEqual Text(mess(s"whichYears.checkbox", s"${currentTaxYear.startYear - (index+1)}", s"${currentTaxYear.finishYear - (index+1)}"))
          value.id mustEqual Some(s"value_${index}")
          value.name mustEqual Some(s"value[${index}]")
          value.value mustEqual (currentTaxYear.startYear - (index+1)).toString
      } 
    }

    "must create checkbox items for 6 years" in {
      val checkboxItems = WhichYears.createYearCheckboxes(6, currentTaxYear)
      checkboxItems.zipWithIndex.map{ 
        case (value, index) =>
          value.content mustEqual Text(mess(s"whichYears.checkbox", s"${currentTaxYear.startYear - (index+1)}", s"${currentTaxYear.finishYear - (index+1)}"))
          value.id mustEqual Some(s"value_${index}")
          value.name mustEqual Some(s"value[${index}]")
          value.value mustEqual (currentTaxYear.startYear - (index+1)).toString
      } 
    }

    "must create checkbox items for 8 years" in {
      val checkboxItems = WhichYears.createYearCheckboxes(8, currentTaxYear)
      checkboxItems.zipWithIndex.map{ 
        case (value, index) =>
          value.content mustEqual Text(mess(s"whichYears.checkbox", s"${currentTaxYear.startYear - (index+1)}", s"${currentTaxYear.finishYear - (index+1)}"))
          value.id mustEqual Some(s"value_${index}")
          value.name mustEqual Some(s"value[${index}]")
          value.value mustEqual (currentTaxYear.startYear - (index+1)).toString
      } 
    }

    "must create checkbox items for 19 years with none of these years row" in {
      val checkboxItems = WhichYears.checkboxItems(WhichYears.Deliberate)
      val first19Elements = checkboxItems.slice(0,18)

      first19Elements.zipWithIndex.map{ 
        case (value, index) => value.value mustEqual (currentTaxYear.startYear - (index+1)).toString
      } 
      checkboxItems.last.value mustEqual "deliberatePriorTo"
    }

    "must create checkbox items for 8 years with prior to 8 years row" in {
      val checkboxItems = WhichYears.checkboxItems(WhichYears.Careless)
      val first8Elements = checkboxItems.slice(0,7)

      first8Elements.zipWithIndex.map{ 
        case (value, index) => value.value mustEqual (currentTaxYear.startYear - (index+1)).toString
      } 
      checkboxItems.last.value mustEqual "carelessPriorTo"

    }

    "must create checkbox items for 6 years with prior to 6 years row" in {
      val checkboxItems = WhichYears.checkboxItems(WhichYears.ReasonableExcuse)
      val first6Elements = checkboxItems.slice(0,5)

      first6Elements.zipWithIndex.map{ 
        case (value, index) => value.value mustEqual (currentTaxYear.startYear - (index+1)).toString
      } 
      checkboxItems.last.value mustEqual "reasonableExcusePriorTo"
    }

  }
}
