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

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class WhatReasonableCareDidYouTakeFormProviderSpec extends StringFieldBehaviours {

  ".reasonableCare when entity" - {

    val form = new WhatReasonableCareDidYouTakeFormProvider()(true)
    val fieldName = "reasonableCare"
    val requiredKey = "whatReasonableCareDidYouTake.error.reasonableCare.required"
    val entityLengthKey = "whatReasonableCareDidYouTake.entity.error.reasonableCare.length"
    val maxLength = 5000

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, entityLengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithValidUnicodeChars(
      form,
      fieldName
    )
  }

  ".reasonableCare when agent" - {

    val fieldName = "reasonableCare"
    val agentLengthKey = "whatReasonableCareDidYouTake.agent.error.reasonableCare.length"
    val maxLength = 5000

    behave like fieldWithMaxLength(
      new WhatReasonableCareDidYouTakeFormProvider()(false),
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, agentLengthKey, Seq(maxLength))
    )
  }

  ".yearsThisAppliesTo" - {

    val form = new WhatReasonableCareDidYouTakeFormProvider()(true)
    val fieldName = "yearsThisAppliesTo"
    val requiredKey = "whatReasonableCareDidYouTake.error.yearsThisAppliesTo.required"
    val lengthKey = "whatReasonableCareDidYouTake.error.yearsThisAppliesTo.length"
    val maxLength = 500

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithValidUnicodeChars(
      form,
      fieldName
    )
  }
}
