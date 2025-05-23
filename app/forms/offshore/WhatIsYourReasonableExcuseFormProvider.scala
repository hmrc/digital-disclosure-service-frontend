/*
 * Copyright 2023 HM Revenue & Customs
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
import play.api.data.Forms._
import models.WhatIsYourReasonableExcuse

class WhatIsYourReasonableExcuseFormProvider @Inject() extends Mappings {

  def apply(areTheyTheIndividual: Boolean): Form[WhatIsYourReasonableExcuse] = Form(
    mapping(
      "excuse" -> text("whatIsYourReasonableExcuse.error.excuse.required")
        .verifying(
          maxLength(
            5000,
            if (areTheyTheIndividual) {
              "whatIsYourReasonableExcuse.entity.error.excuse.length"
            } else {
              "whatIsYourReasonableExcuse.agent.error.excuse.length"
            }
          )
        )
        .verifying(validUnicodeCharacters),
      "years"  -> text("whatIsYourReasonableExcuse.error.years.required")
        .verifying(maxLength(500, "whatIsYourReasonableExcuse.error.years.length"))
        .verifying(validUnicodeCharacters)
    )(WhatIsYourReasonableExcuse.apply)(WhatIsYourReasonableExcuse.unapply)
  )
}
