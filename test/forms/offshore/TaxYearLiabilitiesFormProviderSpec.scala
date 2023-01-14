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

import forms.behaviours.{BigIntFieldBehaviours, IntFieldBehaviours}
import play.api.data.FormError

class TaxYearLiabilitiesFormProviderSpec extends IntFieldBehaviours with BigIntFieldBehaviours {

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

      val validDataGenerator = bigintsInRangeWithCommas(minimum, maximum)

      behave like fieldThatBindsValidData(
        form,
        fieldName,
        validDataGenerator
      )

      behave like bigintField(
        form,
        fieldName,
        nonNumericError  = FormError(fieldName, s"taxYearLiabilities.$fieldName.error.nonNumeric"),
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
        nonNumericError  = FormError(fieldName, s"taxYearLiabilities.penaltyRate.error.nonNumeric"),
        wholeNumberError = FormError(fieldName, s"taxYearLiabilities.penaltyRate.error.wholeNumber")
      )

      behave like intFieldWithRange(
        form,
        fieldName,
        minimum       = minimum,
        maximum       = maximum,
        expectedError = FormError(fieldName, s"taxYearLiabilities.penaltyRate.error.outOfRange", Seq(minimum, maximum))
      )

      behave like mandatoryField(
        form,
        fieldName,
        requiredError = FormError(fieldName, s"taxYearLiabilities.penaltyRate.error.required")
      )
    }

  ".foreignTaxCredit" - {

    val fieldName = "foreignTaxCredit"
    val requiredKey = "taxYearLiabilities.foreignTaxCredit.error.required"

    "bind true" in {
      val result = form.bind(Map(
        (fieldName, "true"),
        ("income", "2000"),
        ("chargeableTransfers", "2000"),
        ("capitalGains", "2000"),
        ("unpaidTax", "2000"),
        ("interest", "2000"),
        ("penaltyRate", "100")
      ))
      result.value.value.foreignTaxCredit mustBe true
      result.errors mustBe empty
    }

    "bind false" in {
      val result = form.bind(Map(
        (fieldName, "false"),
        ("income", "2000"),
        ("chargeableTransfers", "2000"),
        ("capitalGains", "2000"),
        ("unpaidTax", "2000"),
        ("interest", "2000"),
        ("penaltyRate", "100")
      ))
      result.value.value.foreignTaxCredit mustBe false
      result.errors mustBe empty
    }

    "not bind non-booleans" in {

      forAll(nonBooleans -> "nonBoolean") {
        nonBoolean =>
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

}
