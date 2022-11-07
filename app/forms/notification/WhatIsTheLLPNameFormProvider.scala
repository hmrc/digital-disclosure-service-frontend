package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class WhatIsTheLLPNameFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("whatIsTheLLPName.error.required")
        .verifying(maxLength(50, "whatIsTheLLPName.error.length"))
    )
}
