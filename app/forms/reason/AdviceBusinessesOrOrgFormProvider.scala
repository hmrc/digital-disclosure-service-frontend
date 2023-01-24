package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class AdviceBusinessesOrOrgFormProvider @Inject() extends Mappings {

  def apply(): Form[Boolean] =
    Form(
      "value" -> boolean("adviceBusinessesOrOrg.error.required")
    )
}
