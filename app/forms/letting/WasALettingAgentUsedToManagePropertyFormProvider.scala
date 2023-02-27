package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class WasALettingAgentUsedToManagePropertyFormProvider @Inject() extends Mappings {

  def apply(): Form[Boolean] =
    Form(
      "value" -> boolean("wasALettingAgentUsedToManageProperty.error.required")
    )
}
