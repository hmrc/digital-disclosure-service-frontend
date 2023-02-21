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

import java.time.{LocalDate, Month, ZoneOffset}
import forms.behaviours.{IntFieldBehaviours, BigIntFieldBehaviours, StringFieldBehaviours}
import play.api.data.FormError

class CorporationTaxLiabilityFormProviderSpec extends IntFieldBehaviours with BigIntFieldBehaviours with StringFieldBehaviours {

  val form = new CorporationTaxLiabilityFormProvider()()

  ".periodEnd" - {

  }

  Seq(
    "howMuchIncome",
    "howMuchUnpaid",
    "howMuchInterest"
  ).foreach { fieldName =>
    s"$fieldName" - {
      val minimum = BigInt(0)
      val maximum = BigInt("999999999999999999999999")

      val validDataGenerator = bigintsInRange(minimum, maximum)

      behave like fieldThatBindsValidData(
        form,
        fieldName,
        validDataGenerator
      )

      behave like bigintField(
        form,
        fieldName,
        nonNumericError  = FormError(fieldName, s"corporationTaxLiability.$fieldName.error.nonNumeric"),
        wholeNumberError = FormError(fieldName, s"corporationTaxLiability.$fieldName.error.wholeNumber")
      )

      behave like mandatoryField(
        form,
        fieldName,
        requiredError = FormError(fieldName, s"corporationTaxLiability.$fieldName.error.required")
      )
    }
  }

  ".penaltyRate" - {
    val minimum = 0
    val maximum = 200

    val fieldName = "penaltyRate"

    val validDataGenerator = intsInRangeWithCommas(minimum, maximum)

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validDataGenerator
    )

    behave like intField(
      form,
      fieldName,
      nonNumericError  = FormError(fieldName, s"corporationTaxLiability.penaltyRate.error.nonNumeric"),
      wholeNumberError = FormError(fieldName, s"corporationTaxLiability.penaltyRate.error.wholeNumber")
    )

    behave like intFieldWithRange(
      form,
      fieldName,
      minimum       = minimum,
      maximum       = maximum,
      expectedError = FormError(fieldName, s"corporationTaxLiability.penaltyRate.error.outOfRange", Seq(minimum, maximum))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, s"corporationTaxLiability.penaltyRate.error.required")
    )
  }

  ".penaltyRateReason" - {

    val fieldName = "penaltyRateReason"
    val maxLength = 5000

    val lengthKey = "corporationTaxLiability.penaltyRateReason.error.length"
    val requiredKey = "corporationTaxLiability.penaltyRateReason.error.required"

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
}
