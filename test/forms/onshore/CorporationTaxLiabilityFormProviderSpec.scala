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

import java.time.{LocalDate, ZoneOffset}
import forms.behaviours.{PeriodEndBehaviours, IntFieldBehaviours, BigIntFieldBehaviours, StringFieldBehaviours}
import play.api.data.FormError

class CorporationTaxLiabilityFormProviderSpec extends PeriodEndBehaviours with IntFieldBehaviours with BigIntFieldBehaviours with StringFieldBehaviours {

  val form = new CorporationTaxLiabilityFormProvider()()

  ".periodEnd" - {

    val fieldName = "periodEnd"

    val data = Map(
      s"periodEnd.day" -> "1",
      s"periodEnd.month" -> "1",
      s"periodEnd.year" -> LocalDate.now(ZoneOffset.UTC).minusDays(1).getYear.toString,
      s"howMuchIncome" -> "100",
      s"howMuchUnpaid" -> "100",
      s"howMuchInterest" -> "100",
      s"penaltyRate" -> "5",
      s"penaltyRateReason" -> "Reason"
    )

    periodEndField(form, "corporationTaxLiability", data)

    periodEndFieldCheckingMaxDay(form, data, FormError(fieldName + ".day", "corporationTaxLiability.periodEnd.error.invalidDay"))

    periodEndFieldCheckingMaxMonth(form, data, FormError(fieldName + ".month", "corporationTaxLiability.periodEnd.error.invalidMonth"))

    periodEndFieldInFuture(form, data, FormError(fieldName, "corporationTaxLiability.periodEnd.error.invalidFutureDate"))

    periodEndFieldWithMin(form, data, FormError(fieldName, "corporationTaxLiability.periodEnd.error.invalidPastDate"))


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
