package forms

import java.time.LocalDate

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class PropertyIsNoLongerBeingLetOutFormProvider @Inject() extends Mappings {

  def apply(): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey     = "propertyIsNoLongerBeingLetOut.error.invalid",
        allRequiredKey = "propertyIsNoLongerBeingLetOut.error.required.all",
        twoRequiredKey = "propertyIsNoLongerBeingLetOut.error.required.two",
        requiredKey    = "propertyIsNoLongerBeingLetOut.error.required"
      )
    )
}
