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

import forms.behaviours.FieldBehaviours
import play.api.data.FormError

class HowManyPropertiesDoYouCurrentlyLetOutFormProviderSpec extends FieldBehaviours {

  val requiredKey = "howManyProperties.error.required"
  val nonNumericKey = "howManyProperties.error.nonNumeric"

  val form = new HowManyPropertiesDoYouCurrentlyLetOutFormProvider()()

  ".value" - {

    val fieldName = "value"

    "bind valid data" in {

      forAll(nonNumerics -> "validDataItem") {
        dataItem: String =>
          val result = form.bind(Map(fieldName -> dataItem)).apply(fieldName)
          result.value.value mustBe dataItem
          result.errors mustBe Seq(FormError(fieldName, nonNumericKey))
      }
    }

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
