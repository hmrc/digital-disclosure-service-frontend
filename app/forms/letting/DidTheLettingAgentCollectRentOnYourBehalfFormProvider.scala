package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class DidTheLettingAgentCollectRentOnYourBehalfFormProvider @Inject() extends Mappings {

  def apply(): Form[Boolean] =
    Form(
      "value" -> boolean("didTheLettingAgentCollectRentOnYourBehalf.error.required")
    )
}
