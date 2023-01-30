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

import forms.behaviours.{MonthYearBehaviours, StringFieldBehaviours, OptionFieldBehaviours}
import play.api.data.FormError
import models.{MonthYear, AdviceContactPreference}
import org.scalacheck.Arbitrary.arbitrary

class AdviceGivenFormProviderSpec extends OptionFieldBehaviours with StringFieldBehaviours with MonthYearBehaviours {

  val form = new AdviceGivenFormProvider()()

  ".adviceGiven" - {

    val fieldName = "adviceGiven"
    val requiredKey = "adviceGiven.error.adviceGiven.required"
    val lengthKey = "adviceGiven.error.adviceGiven.length"
    val maxLength = 5000

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  
  ".date" - {
    val validData = arbitrary[MonthYear]

    behave like monthYearField(form, "date", validData)

    behave like monthYearFieldInFuture(form, "date", FormError("date", "adviceGiven.date.error.invalidFutureDate"))

    behave like monthYearFieldWithMin(form, "date", FormError("date", "adviceGiven.date.error.invalidPastDate"))

    behave like mandatoryMonthYearField(form, "date", "adviceGiven.date.error.required.all")

    behave like monthYearFieldCheckingMaxMonth(form, "date", validData, FormError("date.month", "adviceGiven.date.error.invalidMonth"))
  }

  ".contact" - {

    val fieldName = "contact"
    val requiredKey = "adviceGiven.error.contact.required"

    behave like optionsField[AdviceContactPreference](
      form,
      fieldName,
      validValues  = AdviceContactPreference.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
