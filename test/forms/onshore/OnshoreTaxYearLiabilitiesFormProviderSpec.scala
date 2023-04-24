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

import forms.behaviours.{BigIntFieldBehaviours, IntFieldBehaviours, StringFieldBehaviours, BigDecimalFieldBehaviours}
import play.api.data.FormError
import models.WhatOnshoreLiabilitiesDoYouNeedToDisclose

class OnshoreTaxYearLiabilitiesFormProviderSpec extends IntFieldBehaviours with BigIntFieldBehaviours with BigDecimalFieldBehaviours with StringFieldBehaviours {

  val formWithNoSelections = new OnshoreTaxYearLiabilitiesFormProvider()(Set())
  val formWithNonBusinessSelected = new OnshoreTaxYearLiabilitiesFormProvider()(Set(WhatOnshoreLiabilitiesDoYouNeedToDisclose.NonBusinessIncome))
  val formWithBusinessSelected = new OnshoreTaxYearLiabilitiesFormProvider()(Set(WhatOnshoreLiabilitiesDoYouNeedToDisclose.BusinessIncome))
  val formWithLettingSelected = new OnshoreTaxYearLiabilitiesFormProvider()(Set(WhatOnshoreLiabilitiesDoYouNeedToDisclose.LettingIncome))

  Seq(
    "niContributions",
    "unpaidTax",
    "interest"
  ).foreach { fieldName =>
    s"$fieldName" - {
      val minimum = BigInt(0)
      val maximum = BigInt("999999999999999999999999")

      val validDataGenerator = bigintsInRangeWithPound(minimum, maximum)

      behave like fieldThatBindsValidData(
        formWithNoSelections,
        fieldName,
        validDataGenerator
      )

      behave like bigintField(
        formWithNoSelections,
        fieldName,
        nonNumericError  = FormError(fieldName, s"onshoreTaxYearLiabilities.$fieldName.error.nonNumeric"),
        wholeNumberError = FormError(fieldName, s"onshoreTaxYearLiabilities.$fieldName.error.wholeNumber")
      )

      behave like mandatoryField(
        formWithNoSelections,
        fieldName,
        requiredError = FormError(fieldName, s"onshoreTaxYearLiabilities.$fieldName.error.required")
      )
    }
  }

  ".penaltyRate" - {
      val minimum = BigDecimal(0)
      val maximum = BigDecimal(200)

      val fieldName = "penaltyRate"

      val validDataGenerator = decimalsInRangeWithCommasWithPercentage(minimum, maximum)

      behave like fieldThatBindsValidData(
        formWithNoSelections,
        fieldName,
        validDataGenerator
      )

      behave like decimalField(
        formWithNoSelections,
        fieldName,
        nonNumericError  = FormError(fieldName, s"onshoreTaxYearLiabilities.penaltyRate.error.nonNumeric")
      )

      behave like bigdecimalFieldWithRange(
        formWithNoSelections,
        fieldName,
        minimum       = minimum,
        maximum       = maximum,
        expectedError = FormError(fieldName, s"onshoreTaxYearLiabilities.penaltyRate.error.outOfRange", Seq(minimum, maximum))
      )

      behave like mandatoryField(
        formWithNoSelections,
        fieldName,
        requiredError = FormError(fieldName, s"onshoreTaxYearLiabilities.penaltyRate.error.required")
      )
    }

  ".residentialTaxReduction" - {

    val fieldName = "residentialTaxReduction"
    val requiredKey = "onshoreTaxYearLiabilities.residentialTaxReduction.error.required"

    "bind true" in {
      val result = formWithLettingSelected.bind(Map(
        ("lettingIncome", "2000"),
        ("unpaidTax", "2000"),
        ("niContributions", "2000"),
        ("interest", "2000"),
        ("penaltyRate", "100"),
        ("penaltyRateReason", "Reason"),
        ("undeclaredIncomeOrGain", "Undeclared Income or Gain"),
        ("residentialTaxReduction", "true")
      ))
      result.value.value.residentialTaxReduction.value mustBe true
      result.errors mustBe empty
    }

    "bind false" in {
      val result = formWithLettingSelected.bind(Map(
        ("lettingIncome", "2000"),
        ("unpaidTax", "2000"),
        ("niContributions", "2000"),
        ("interest", "2000"),
        ("penaltyRate", "100"),
        ("penaltyRateReason", "Reason"),
        ("undeclaredIncomeOrGain", "Undeclared Income or Gain"),
        ("residentialTaxReduction", "false")
      ))
      result.value.value.residentialTaxReduction.value mustBe false
      result.errors mustBe empty
    }

    "not bind non-booleans" in {

      forAll(nonBooleans -> "nonBoolean") {
        nonBoolean =>
          val result = formWithLettingSelected.bind(Map(fieldName -> nonBoolean)).apply(fieldName)
          result.errors mustBe Seq(FormError("residentialTaxReduction", List("error.boolean")))
      }
    }

    behave like mandatoryField(
      formWithLettingSelected,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  ".penaltyRateReason" - {

    val fieldName = "penaltyRateReason"
    val maxLength = 5000

    val lengthKey = "onshoreTaxYearLiabilities.penaltyRateReason.error.length"
    val requiredKey = "onshoreTaxYearLiabilities.penaltyRateReason.error.required"

    behave like fieldThatBindsValidData(
      formWithNoSelections,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      formWithNoSelections,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      formWithNoSelections,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  Seq(
    "nonBusinessIncome" -> formWithNonBusinessSelected,
    "businessIncome" -> formWithBusinessSelected,
    "lettingIncome" -> formWithLettingSelected
  ).foreach { case (fieldName, form) =>
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
        nonNumericError  = FormError(fieldName, s"onshoreTaxYearLiabilities.$fieldName.error.nonNumeric"),
        wholeNumberError = FormError(fieldName, s"onshoreTaxYearLiabilities.$fieldName.error.wholeNumber")
      )

      behave like mandatoryField(
        form,
        fieldName,
        requiredError = FormError(fieldName, s"onshoreTaxYearLiabilities.$fieldName.error.required")
      )
    }
  }

}
