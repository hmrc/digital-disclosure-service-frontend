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

import forms.behaviours.{BigIntFieldBehaviours, IntFieldBehaviours, StringFieldBehaviours}
import play.api.data.FormError
import models.WhatOnshoreLiabilitiesDoYouNeedToDisclose

class OnshoreTaxYearLiabilitiesFormProviderSpec extends IntFieldBehaviours with BigIntFieldBehaviours with StringFieldBehaviours {

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

      val validDataGenerator = bigintsInRange(minimum, maximum)

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
      val minimum = 0
      val maximum = 200

      val fieldName = "penaltyRate"

      val validDataGenerator = intsInRangeWithCommas(minimum, maximum)

      behave like fieldThatBindsValidData(
        formWithNoSelections,
        fieldName,
        validDataGenerator
      )

      behave like intField(
        formWithNoSelections,
        fieldName,
        nonNumericError  = FormError(fieldName, s"onshoreTaxYearLiabilities.penaltyRate.error.nonNumeric"),
        wholeNumberError = FormError(fieldName, s"onshoreTaxYearLiabilities.penaltyRate.error.wholeNumber")
      )

      behave like intFieldWithRange(
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

      val validDataGenerator = bigintsInRange(minimum, maximum)

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

  "check all required field errors are in order" in {
    val form = new OnshoreTaxYearLiabilitiesFormProvider()(Set(
      WhatOnshoreLiabilitiesDoYouNeedToDisclose.NonBusinessIncome,
      WhatOnshoreLiabilitiesDoYouNeedToDisclose.BusinessIncome,
      WhatOnshoreLiabilitiesDoYouNeedToDisclose.LettingIncome,
      WhatOnshoreLiabilitiesDoYouNeedToDisclose.Gains  
    ))
    
    val result = form.bind(
      Map(
        ("nonBusinessIncome", ""),
        ("businessIncome", ""),
        ("lettingIncome", ""),
        ("gains", ""),
        ("unpaidTax", ""),
        ("niContributions", ""),
        ("interest", ""),
        ("penaltyRate", ""),
        ("penaltyRateReason", ""),
        ("residentialTaxReduction", "")
      )
    )

    result.errors(0).message mustBe "onshoreTaxYearLiabilities.nonBusinessIncome.error.required"
    result.errors(1).message mustBe "onshoreTaxYearLiabilities.businessIncome.error.required"
    result.errors(2).message mustBe "onshoreTaxYearLiabilities.lettingIncome.error.required"
    result.errors(3).message mustBe "onshoreTaxYearLiabilities.gains.error.required"
    result.errors(4).message mustBe "onshoreTaxYearLiabilities.unpaidTax.error.required"
    result.errors(5).message mustBe "onshoreTaxYearLiabilities.niContributions.error.required"
    result.errors(6).message mustBe "onshoreTaxYearLiabilities.interest.error.required"
    result.errors(7).message mustBe "onshoreTaxYearLiabilities.penaltyRate.error.required"
    result.errors(8).message mustBe "onshoreTaxYearLiabilities.penaltyRateReason.error.required"
    result.errors(9).message mustBe "onshoreTaxYearLiabilities.residentialTaxReduction.error.required"
  }

}
