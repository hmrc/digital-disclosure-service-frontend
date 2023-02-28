package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class WhichLandlordAssociationsAreYouAMemberOfFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "whichLandlordAssociationsAreYouAMemberOf.error.required"
  val lengthKey = "whichLandlordAssociationsAreYouAMemberOf.error.length"
  val maxLength = 500

  val form = new WhichLandlordAssociationsAreYouAMemberOfFormProvider()()

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
