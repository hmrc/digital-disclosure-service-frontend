package forms

import forms.behaviours.CheckboxFieldBehaviours
import models.WhichYearDoesThisOffshoreDisclosureRelateTo
import play.api.data.FormError

class WhichYearDoesThisOffshoreDisclosureRelateToFormProviderSpec extends CheckboxFieldBehaviours {

  val form = new WhichYearDoesThisOffshoreDisclosureRelateToFormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "whichYearDoesThisOffshoreDisclosureRelateTo.error.required"

    behave like checkboxField[WhichYearDoesThisOffshoreDisclosureRelateTo](
      form,
      fieldName,
      validValues  = WhichYearDoesThisOffshoreDisclosureRelateTo.values,
      invalidError = FormError(s"$fieldName[0]", "error.invalid")
    )

    behave like mandatoryCheckboxField(
      form,
      fieldName,
      requiredKey
    )
  }
}
