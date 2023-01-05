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

package forms.behaviours

import play.api.data.{Form, FormError}

trait RegistrationNumberBehaviours extends FieldBehaviours {

  def fieldThatBindsValidData(form: Form[_], fieldName: String): Unit = {
    "bind valid AlphaNumeric Registration Number" in {
      val validDataGenerator = generateAlphaNumericRegistrationNumber()

      forAll(validDataGenerator -> "validDataItem") {
        dataItem: String =>
          val result = form.bind(Map(fieldName -> dataItem)).apply(fieldName)
          result.value.value mustBe dataItem
          result.errors mustBe empty
      }
    }

    "bind valid Numeric Registration Number" in {
      val validDataGenerator = generateNumericRegistrationNumber()

      forAll(validDataGenerator -> "validDataItem") {
        dataItem: String =>
          val result = form.bind(Map(fieldName -> dataItem)).apply(fieldName)
          result.value.value mustBe dataItem
          result.errors mustBe empty
      }
    }
  }

  def fieldThatBindInvalidRegistrationNumber(form: Form[_], fieldName: String, error: FormError): Unit = {
    "not bind invalid registration number" in {
      val invalidRegistrationNumberGenerator = generateInvalidRegistrationNumber()

      forAll(invalidRegistrationNumberGenerator -> "validDataItem") {
        string: String =>
          val result = form.bind(Map(fieldName -> string)).apply(fieldName)
          result.errors must contain only error
      }
    }
  }
}
