package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class WhatWasTheTypeOfMortgageFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "whatWasTheTypeOfMortgage.error.required"
  val lengthKey = "whatWasTheTypeOfMortgage.error.length"
  val maxLength = 500

  val form = new WhatWasTheTypeOfMortgageFormProvider()()

  ".value" - {

    val fieldName = "value"

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
