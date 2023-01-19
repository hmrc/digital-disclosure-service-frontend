package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class CountryOfYourOffshoreLiabilityFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "countryOfYourOffshoreLiability.error.required"
  val lengthKey = "countryOfYourOffshoreLiability.error.length"
  val maxLength = 100

  val form = new CountryOfYourOffshoreLiabilityFormProvider()()

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
