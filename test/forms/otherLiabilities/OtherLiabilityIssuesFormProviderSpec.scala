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

import forms.behaviours.CheckboxFieldBehaviours
import models.OtherLiabilityIssues
import play.api.data.FormError

class OtherLiabilityIssuesFormProviderSpec extends CheckboxFieldBehaviours {

  val form = new OtherLiabilityIssuesFormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "otherLiabilityIssues.error.required"
    val validSelectionKey = "otherLiabilityIssues.error.validSelection"

    behave like checkboxField[OtherLiabilityIssues](
      form,
      fieldName,
      validValues  = OtherLiabilityIssues.values,
      invalidError = FormError(s"$fieldName[0]", "error.invalid")
    )

    behave like mandatoryCheckboxField(
      form,
      fieldName,
      requiredKey
    )

    behave like Seq(
      "vatIssues",
      "inheritanceTaxIssues",
      "class2National",
      "other"
    ).foreach { option => 
      s"fail to bind when the user selects both No excluded amount AND $option" in {
        val data = Map(
          "value[0]" -> "noExclusion",
          "value[1]" -> option
        )
        val expectedError = FormError("value", validSelectionKey)
        form.bind(data).errors must contain(expectedError)
      }  
    }
  }
}
