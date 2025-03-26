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

import forms.behaviours.{BigDecimalFieldBehaviours, BigIntFieldBehaviours, IntFieldBehaviours, StringFieldBehaviours}
import play.api.data.FormError

class TaxYearLiabilitiesFormProviderSpec
    extends IntFieldBehaviours
    with BigIntFieldBehaviours
    with BigDecimalFieldBehaviours
    with StringFieldBehaviours {

  val form = new TaxYearLiabilitiesFormProvider()()

  Seq(
    "chargeableTransfers",
    "capitalGains",
    "unpaidTax",
    "interest"
  ).foreach { fieldName =>
    s"$fieldName" - {
      val minimum = BigInt(0)
      val maximum = BigInt("999999999999999999999999")

      val validDataGenerator = bigintsInRangeWithPound(minimum, maximum)

      behave like fieldThatBindsValidData(
        form,
        fieldName,
        validDataGenerator
      )

      behave like bigintField(
        form,
        fieldName,
        nonNumericError = FormError(fieldName, s"taxYearLiabilities.$fieldName.error.nonNumeric"),
        wholeNumberError = FormError(fieldName, s"taxYearLiabilities.$fieldName.error.wholeNumber")
      )

      behave like mandatoryField(
        form,
        fieldName,
        requiredError = FormError(fieldName, s"taxYearLiabilities.$fieldName.error.required")
      )
    }
  }

  ".penaltyRate" - {
    val minimum = BigDecimal(0)
    val maximum = BigDecimal(200)

    val fieldName = "penaltyRate"

    val validDataGenerator = decimalsInRangeWithCommasWithPercentage(minimum, maximum)

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validDataGenerator
    )

    behave like decimalField(
      form,
      fieldName,
      nonNumericError = FormError(fieldName, s"taxYearLiabilities.penaltyRate.error.nonNumeric")
    )

    behave like bigdecimalFieldWithRange(
      form,
      fieldName,
      minimum = minimum,
      maximum = maximum,
      expectedError = FormError(fieldName, s"taxYearLiabilities.penaltyRate.error.outOfRange", Seq(minimum, maximum))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, s"taxYearLiabilities.penaltyRate.error.required")
    )
  }

  ".foreignTaxCredit" - {

    val fieldName   = "foreignTaxCredit"
    val requiredKey = "taxYearLiabilities.foreignTaxCredit.error.required"

    "bind true" in {
      val result = form.bind(
        Map(
          (fieldName, "true"),
          ("income", "2000"),
          ("chargeableTransfers", "2000"),
          ("capitalGains", "2000"),
          ("unpaidTax", "2000"),
          ("interest", "2000"),
          ("penaltyRateReason", "Reason"),
          ("undeclaredIncomeOrGain", "Undeclared Income or Gain"),
          ("penaltyRate", "100")
        )
      )
      result.value.value.foreignTaxCredit mustBe true
      result.errors mustBe empty
    }

    "bind false" in {
      val result = form.bind(
        Map(
          (fieldName, "false"),
          ("income", "2000"),
          ("chargeableTransfers", "2000"),
          ("capitalGains", "2000"),
          ("unpaidTax", "2000"),
          ("interest", "2000"),
          ("penaltyRateReason", "Reason"),
          ("undeclaredIncomeOrGain", "Undeclared Income or Gain"),
          ("penaltyRate", "100")
        )
      )
      result.value.value.foreignTaxCredit mustBe false
      result.errors mustBe empty
    }

    "not bind non-booleans" in {

      forAll(nonBooleans -> "nonBoolean") { nonBoolean =>
        val result = form.bind(Map(fieldName -> nonBoolean)).apply(fieldName)
        result.errors mustBe Seq(FormError("foreignTaxCredit", List("error.boolean")))
      }
    }

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  ".penaltyRateReason" - {

    val fieldName = "penaltyRateReason"
    val maxLength = 5000

    val lengthKey   = "taxYearLiabilities.penaltyRateReason.error.length"
    val requiredKey = "taxYearLiabilities.penaltyRateReason.error.required"

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

    behave like fieldWithValidUnicodeChars(
      form,
      fieldName
    )
  }

  ".undeclaredIncomeOrGain" - {

    val fieldName = "undeclaredIncomeOrGain"
    val maxLength = 5000

    val lengthKey   = "taxYearLiabilities.undeclaredIncomeOrGain.error.length"
    val requiredKey = "taxYearLiabilities.undeclaredIncomeOrGain.error.required"

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

    behave like fieldWithValidUnicodeChars(
      form,
      fieldName
    )
  }
}
