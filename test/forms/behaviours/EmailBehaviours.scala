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

package forms.behaviours

import play.api.data.{Form, FormError}

trait EmailBehaviours extends FieldBehaviours {

  def validEmailBindsValidData(form: Form[_], fieldName: String): Unit = {
    "bind valid data" in {

      val validDataGenerator = email()

      forAll(validDataGenerator -> "validDataItem") {
        dataItem: String =>
          val result = form.bind(Map(fieldName -> dataItem)).apply(fieldName)
          result.value.value mustBe dataItem
          result.errors mustBe empty
      }
    }
  }

  def emailBindsInvalidData(form: Form[_], fieldName: String, validError: FormError): Unit = {

    "not bind invalid email" in {
      forAll(invalidEmail() -> "longString") {
        string =>
          val result = form.bind(Map(fieldName -> string)).apply(fieldName)
          result.errors must contain only validError
      }
    }

    "not bind email with invalid domain" in {
      forAll(invalidEmailDomain() -> "longString") {
        string =>
          val result = form.bind(Map(fieldName -> string)).apply(fieldName)
          result.errors must contain only validError
      }
    }
  }

  def maxLengthEmailBindsValidData(form: Form[_], fieldName: String, lengthError: FormError): Unit = {

    "not bind strings longer than 320 characters" in {
      forAll(invalidLengthEmail() -> "longString") {
        string =>
          val result = form.bind(Map(fieldName -> string)).apply(fieldName)
          result.errors must contain only lengthError
      }
    }
  }
}
