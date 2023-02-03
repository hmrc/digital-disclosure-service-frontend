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

package forms

import forms.behaviours.CheckboxFieldBehaviours
import models.{TaxYearStarting, OffshoreYears, PriorTo5Years, PriorTo7Years, PriorTo19Years}
import play.api.data.FormError
import java.time.LocalDate
import uk.gov.hmrc.time.CurrentTaxYear

class WhichYearsFormProviderSpec extends CheckboxFieldBehaviours with CurrentTaxYear {

  def now = () => LocalDate.now()

  val form = new WhichYearsFormProvider()()

  val currentYear = current.startYear
  val nineteenYears = Range.inclusive(1, 19).map(i => TaxYearStarting(currentYear-i)).toSeq :+ PriorTo19Years
  val sevenYears = Range.inclusive(1, 19).map(i => TaxYearStarting(currentYear-i)).toSeq :+ PriorTo7Years
  val fiveYears = Range.inclusive(1, 19).map(i => TaxYearStarting(currentYear-i)).toSeq :+ PriorTo5Years

  ".value for 19 years" - {

    val fieldName = "value"
    val requiredKey = "whichYears.error.required"

    behave like checkboxField[OffshoreYears](
      form,
      fieldName,
      validValues  = nineteenYears,
      invalidError = FormError(s"$fieldName[0]", "error.invalid")
    )

    behave like mandatoryCheckboxField(
      form,
      fieldName,
      requiredKey
    )
  }

  ".value for 7 years" - {

    val fieldName = "value"
    val requiredKey = "whichYears.error.required"

    behave like checkboxField[OffshoreYears](
      form,
      fieldName,
      validValues  = sevenYears,
      invalidError = FormError(s"$fieldName[0]", "error.invalid")
    )

    behave like mandatoryCheckboxField(
      form,
      fieldName,
      requiredKey
    )
  }

  ".value for 5 years" - {

    val fieldName = "value"
    val requiredKey = "whichYears.error.required"

    behave like checkboxField[OffshoreYears](
      form,
      fieldName,
      validValues  = fiveYears,
      invalidError = FormError(s"$fieldName[0]", "error.invalid")
    )

    behave like mandatoryCheckboxField(
      form,
      fieldName,
      requiredKey
    )
  }
}
