package forms

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class PropertyAddedFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "propertyAdded.error.required"
  val invalidKey = "error.boolean"

  val form = new PropertyAddedFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like booleanField(
      form,
      fieldName,
      invalidError = FormError(fieldName, invalidKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
