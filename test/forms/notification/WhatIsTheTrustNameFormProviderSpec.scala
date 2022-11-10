package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class WhatIsTheTrustNameFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "whatIsTheTrustName.error.required"
  val lengthKey = "whatIsTheTrustName.error.length"
  val maxLength = 50

  val form = new WhatIsTheTrustNameFormProvider()()

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
