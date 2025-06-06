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

import forms.behaviours.BigIntFieldBehaviours
import play.api.data.FormError

class ForeignTaxCreditFormProviderSpec extends BigIntFieldBehaviours {

  val form = new ForeignTaxCreditFormProvider()()

  ".value" - {

    val fieldName = "value"

    val minimum = BigInt(0)
    val maximum = BigInt("999999999999999999999999")

    val validDataGenerator = bigintsInRangeWithPound(minimum, maximum)

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validDataGenerator
    )

    behave like bigintField(
      form,
      fieldName,
      nonNumericError = FormError(fieldName, "foreignTaxCredit.error.nonNumeric"),
      wholeNumberError = FormError(fieldName, "foreignTaxCredit.error.wholeNumber")
    )

    behave like bigintFieldWithRange(
      form,
      fieldName,
      minimum = minimum,
      maximum = maximum,
      expectedError = FormError(fieldName, "foreignTaxCredit.error.outOfRange", Seq(minimum, maximum))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, "foreignTaxCredit.error.required")
    )
  }
}
