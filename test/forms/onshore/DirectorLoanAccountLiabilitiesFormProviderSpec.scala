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

import forms.behaviours.{BigIntFieldBehaviours, IntFieldBehaviours, PeriodEndBehaviours, BigDecimalFieldBehaviours, StringFieldBehaviours}
import play.api.data.FormError

import java.time.{LocalDate, ZoneOffset}

class DirectorLoanAccountLiabilitiesFormProviderSpec extends PeriodEndBehaviours with IntFieldBehaviours with BigIntFieldBehaviours with BigDecimalFieldBehaviours with StringFieldBehaviours {

  val form = new DirectorLoanAccountLiabilitiesFormProvider()()

  ".name" - {

    val fieldName = "name"
    val requiredKey = "directorLoanAccountLiabilities.name.required"
    val lengthKey = "directorLoanAccountLiabilities.name.invalid"
    val maxLength = 30

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

   Seq(
      "overdrawn",
      "unpaidTax",
      "interest"
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
          nonNumericError  = FormError(fieldName, s"directorLoanAccountLiabilities.$fieldName.error.nonNumeric"),
          wholeNumberError = FormError(fieldName, s"directorLoanAccountLiabilities.$fieldName.error.wholeNumber")
        )

        behave like mandatoryField(
          form,
          fieldName,
          requiredError = FormError(fieldName, s"directorLoanAccountLiabilities.$fieldName.error.required")
        )
      }
    }

  ".penaltyRate" - {
    val minimum = BigDecimal(0)
    val maximum = BigDecimal(200)

    val fieldName = "penaltyRate"

    val validDataGenerator = decimalsInRangeWithCommas(minimum, maximum)

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validDataGenerator
    )

    behave like decimalField(
      form,
      fieldName,
      nonNumericError = FormError(fieldName, s"directorLoanAccountLiabilities.penaltyRate.error.nonNumeric")
    )

    behave like bigdecimalFieldWithRange(
      form,
      fieldName,
      minimum = minimum,
      maximum = maximum,
      expectedError = FormError(fieldName, s"directorLoanAccountLiabilities.penaltyRate.error.outOfRange", Seq(minimum, maximum))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, s"directorLoanAccountLiabilities.penaltyRate.error.required")
    )
  }

  ".penaltyRateReason" - {

    val fieldName = "penaltyRateReason"
    val maxLength = 5000

    val lengthKey = "directorLoanAccountLiabilities.penaltyRateReason.error.length"
    val requiredKey = "directorLoanAccountLiabilities.penaltyRateReason.error.required"

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

  ".periodEnd" - {

    val fieldName = "periodEnd"

    val data = Map(
      s"name" -> "name",
      s"periodEnd.day" -> "1",
      s"periodEnd.month" -> "1",
      s"periodEnd.year" -> LocalDate.now(ZoneOffset.UTC).minusDays(1).getYear.toString,
      s"overdrawn" -> "100",
      s"unpaidTax" -> "100",
      s"interest" -> "100",
      s"penaltyRate" -> "100",
      s"penaltyRateReason" -> "reason"
    )

    periodEndField(form, "directorLoanAccountLiabilities", data)

    periodEndFieldCheckingMaxDay(form, fieldName, data, FormError(fieldName + ".day", "directorLoanAccountLiabilities.periodEnd.error.invalidDay"))

    periodEndFieldCheckingMaxMonth(form, fieldName, data, FormError(fieldName + ".month", "directorLoanAccountLiabilities.periodEnd.error.invalidMonth"))

    periodEndFieldInFuture(form, fieldName, data, FormError(fieldName, "directorLoanAccountLiabilities.periodEnd.error.invalidFuture"))

    periodEndFieldWithMin(form, fieldName, data, FormError(fieldName, "directorLoanAccountLiabilities.periodEnd.error.invalidPastDate"))


  }
}
