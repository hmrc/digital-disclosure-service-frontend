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
import models.{OnshoreYearStarting, OnshoreYears, PriorToFiveYears, PriorToNineteenYears, PriorToThreeYears}
import play.api.data.FormError
import java.time.LocalDate
import uk.gov.hmrc.time.CurrentTaxYear

class WhichOnshoreYearsFormProviderSpec extends CheckboxFieldBehaviours with CurrentTaxYear {

  def now = () => LocalDate.now()

  val form = new WhichOnshoreYearsFormProvider()()

  val currentYear   = current.startYear
  val nineteenYears =
    Range.inclusive(1, 19).map(i => OnshoreYearStarting(currentYear - i)).toSeq :+ PriorToNineteenYears
  val fiveYears     = Range.inclusive(1, 5).map(i => OnshoreYearStarting(currentYear - i)).toSeq :+ PriorToFiveYears
  val threeYears    = Range.inclusive(1, 3).map(i => OnshoreYearStarting(currentYear - i)).toSeq :+ PriorToThreeYears

  "nineteen years" - {

    val fieldName   = "value"
    val requiredKey = "whichOnshoreYears.error.required"

    behave like checkboxField[OnshoreYears](
      form,
      fieldName,
      validValues = nineteenYears,
      invalidError = FormError(s"$fieldName[0]", "error.invalid")
    )

    behave like mandatoryCheckboxField(
      form,
      fieldName,
      requiredKey
    )
  }

  "five years" - {

    val fieldName   = "value"
    val requiredKey = "whichOnshoreYears.error.required"

    behave like checkboxField[OnshoreYears](
      form,
      fieldName,
      validValues = fiveYears,
      invalidError = FormError(s"$fieldName[0]", "error.invalid")
    )

    behave like mandatoryCheckboxField(
      form,
      fieldName,
      requiredKey
    )
  }

  "three years" - {

    val fieldName   = "value"
    val requiredKey = "whichOnshoreYears.error.required"

    behave like checkboxField[OnshoreYears](
      form,
      fieldName,
      validValues = threeYears,
      invalidError = FormError(s"$fieldName[0]", "error.invalid")
    )

    behave like mandatoryCheckboxField(
      form,
      fieldName,
      requiredKey
    )
  }
}
