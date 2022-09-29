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

import forms.behaviours.EmailBehaviours
import forms.mappings.Validation
import play.api.data.FormError

class YourEmailAddressFormProviderSpec extends EmailBehaviours with Validation{

  val requiredKey = "yourEmailAddress.error.required"
  val lengthKey = "yourEmailAddress.error.length"
  val maxLength = 320

  val form = new YourEmailAddressFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like validEmailBindsValidData(
      form,
      fieldName
    )

    behave like maxLengthEmailBindsValidData(
      form,
      fieldName,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like emailBindsInvalidData(
      form,
      fieldName,
      validError = FormError(fieldName, requiredKey, Seq(emailRegex))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
