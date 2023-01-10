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
      val checkbox = WhichYears.createNoneOfTheseCheckbox(0)
      checkbox.content mustEqual Text(mess(s"whichYears.checkbox.none"))
      checkbox.id mustEqual Some("value_0")
      checkbox.name mustEqual Some("value[0]")
      checkbox.value mustEqual "noneOfTheseYears"
    }

    "must create a checkbox for any prior to 7 years row" in {
      val checkbox = WhichYears.createPriorTo7YearsCheckbox(7, currentTaxYear)
      checkbox.content mustEqual Text(mess(s"whichYears.checkbox.any", s"${currentTaxYear.back(6).startYear}"))
      checkbox.id mustEqual Some("value_7")
      checkbox.name mustEqual Some("value[7]")
      checkbox.value mustEqual "priorTo7Years"
    }

    "must create a checkbox for any prior to 5 years row" in {
      val checkbox = WhichYears.createPriorTo5YearsCheckbox(5, currentTaxYear)
      checkbox.content mustEqual Text(mess(s"whichYears.checkbox.any", s"${currentTaxYear.back(4).startYear}"))
      checkbox.id mustEqual Some("value_5")
      checkbox.name mustEqual Some("value[5]")
      checkbox.value mustEqual "priorTo5Years"
    }

    "must create checkbox items for 19 years" in {
      val checkboxItems = WhichYears.createYearCheckboxes(19, currentTaxYear)
      checkboxItems.zipWithIndex.map{ 
        case (value, index) =>
          value.content mustEqual Text(mess(s"whichYears.checkbox", s"${currentTaxYear.startYear - (index)}", s"${currentTaxYear.finishYear - (index)}"))
          value.id mustEqual Some(s"value_${index}")
          value.name mustEqual Some(s"value[${index}]")
          value.value mustEqual (currentTaxYear.startYear - (index)).toString
      } 
    }

    "must create checkbox items for 5 years" in {
      val checkboxItems = WhichYears.createYearCheckboxes(5, currentTaxYear)
      checkboxItems.zipWithIndex.map{ 
        case (value, index) =>
          value.content mustEqual Text(mess(s"whichYears.checkbox", s"${currentTaxYear.startYear - (index)}", s"${currentTaxYear.finishYear - (index)}"))
          value.id mustEqual Some(s"value_${index}")
          value.name mustEqual Some(s"value[${index}]")
          value.value mustEqual (currentTaxYear.startYear - (index)).toString
      } 
    }

    "must create checkbox items for 7 years" in {
      val checkboxItems = WhichYears.createYearCheckboxes(7, currentTaxYear)
      checkboxItems.zipWithIndex.map{ 
        case (value, index) =>
          value.content mustEqual Text(mess(s"whichYears.checkbox", s"${currentTaxYear.startYear - (index)}", s"${currentTaxYear.finishYear - (index)}"))
          value.id mustEqual Some(s"value_${index}")
          value.name mustEqual Some(s"value[${index}]")
          value.value mustEqual (currentTaxYear.startYear - (index)).toString
      } 
    }

    "must create checkbox items for 19 years with none of these years row" in {
      val checkboxItems = WhichYears.checkboxItems(19)
      val first19Elements = checkboxItems.slice(0,18)

      first19Elements.zipWithIndex.map{ 
        case (value, index) => value.value mustEqual (currentTaxYear.startYear - (index)).toString
      } 
      checkboxItems.last.value mustEqual "noneOfTheseYears"
    }

    "must create checkbox items for 7 years with prior to 7 years row" in {
      val checkboxItems = WhichYears.checkboxItems(7)
      val first7Elements = checkboxItems.slice(0,6)

      first7Elements.zipWithIndex.map{ 
        case (value, index) => value.value mustEqual (currentTaxYear.startYear - (index)).toString
      } 
      checkboxItems.last.value mustEqual "priorTo7Years"

    }

    "must create checkbox items for 5 years with prior to 5 years row" in {
      val checkboxItems = WhichYears.checkboxItems(5)
      val first5Elements = checkboxItems.slice(0,4)

      first5Elements.zipWithIndex.map{ 
        case (value, index) => value.value mustEqual (currentTaxYear.startYear - (index)).toString
      } 
      checkboxItems.last.value mustEqual "priorTo5Years"
    }

  }
}
