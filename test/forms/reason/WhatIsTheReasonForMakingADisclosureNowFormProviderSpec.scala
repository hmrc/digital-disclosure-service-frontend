package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class WhatIsTheReasonForMakingADisclosureNowFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "whatIsTheReasonForMakingADisclosureNow.error.required"
  val lengthKey = "whatIsTheReasonForMakingADisclosureNow.error.length"
  val maxLength = 5000

  val form = new WhatIsTheReasonForMakingADisclosureNowFormProvider()()

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
