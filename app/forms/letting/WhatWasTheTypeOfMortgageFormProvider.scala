package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class WhatWasTheTypeOfMortgageFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("whatWasTheTypeOfMortgage.error.required")
        .verifying(maxLength(500, "whatWasTheTypeOfMortgage.error.length"))
    )
}
