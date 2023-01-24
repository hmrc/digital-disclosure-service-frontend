package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class WhatIsTheReasonForMakingADisclosureNowFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("whatIsTheReasonForMakingADisclosureNow.error.required")
        .verifying(maxLength(5000, "whatIsTheReasonForMakingADisclosureNow.error.length"))
    )
}
