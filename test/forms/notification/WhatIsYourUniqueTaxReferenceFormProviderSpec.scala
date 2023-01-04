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

import forms.behaviours.UTRBehaviours
import play.api.data.FormError

class WhatIsYourUniqueTaxReferenceFormProviderSpec extends UTRBehaviours {

  val requiredKey = "whatIsYourUniqueTaxReference.error.required"
  val invalidKey = "whatIsYourUniqueTaxReference.error.invalid"

  val form = new WhatIsYourUniqueTaxReferenceFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName
    )

    behave like fieldThatInvalidLengthData(
      form,
      fieldName,
      invalidKey
    )

    behave like fieldThatInvalidCharData(
      form,
      fieldName,
      invalidKey
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
