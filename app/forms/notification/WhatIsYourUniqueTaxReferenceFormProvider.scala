/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class WhatIsYourUniqueTaxReferenceFormProvider @Inject() extends Mappings {

  val length = 10

  def apply(): Form[String] =
    Form(
      "value" -> text("whatIsYourUniqueTaxReference.error.length")
        .verifying(validUTR(
          length,
          lengthKey = "whatIsYourUniqueTaxReference.error.length",
          invalidCharErrorKey = "whatIsYourUniqueTaxReference.error.invalidChar"
        ))
    )
}
