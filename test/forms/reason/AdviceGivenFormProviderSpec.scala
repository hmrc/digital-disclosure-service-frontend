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

import forms.behaviours.{IntFieldBehaviours, StringFieldBehaviours, OptionFieldBehaviours}
import play.api.data.FormError
import models.AdviceContactPreference

class AdviceGivenFormProviderSpec extends OptionFieldBehaviours with StringFieldBehaviours with IntFieldBehaviours {

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

  
  ".date.month" - {
    val minimum = 1
    val maximum = 12

    val fieldName = "date.month"
    val requiredKey = "adviceGiven.error.date.month.required"
    val formatKey = "adviceGiven.error.format"
    val rangeKey = "adviceGiven.error.date.month.max"


    val validDataGenerator = intsInRangeWithCommas(minimum, maximum)

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validDataGenerator
    )

    behave like intField(
      form,
      fieldName,
      nonNumericError  = FormError(fieldName, formatKey),
      wholeNumberError = FormError(fieldName, formatKey)
    )

    behave like intFieldWithRange(
      form,
      fieldName,
      minimum       = minimum,
      maximum       = maximum,
      expectedError = FormError(fieldName, rangeKey, Seq(minimum, maximum))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
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
