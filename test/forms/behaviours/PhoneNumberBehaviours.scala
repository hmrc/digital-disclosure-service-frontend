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

trait PhoneNumberBehaviours extends FieldBehaviours {

  def ukPhoneNumberBindsValidData(form: Form[_],
                              fieldName: String): Unit = {

    "bind valid phone number" in {

      val validDataGenerator = ukPhoneNumber()

      forAll(validDataGenerator -> "validDataItem") {
        dataItem: String =>
          val result = form.bind(Map(fieldName -> dataItem)).apply(fieldName)
          result.value.value mustBe dataItem
          result.errors mustBe empty
      }
    }
  }

  def invalidPhoneNumberBindsInvalidData(form: Form[_],
                                         fieldName: String,
                                         invalidFormError: FormError): Unit = {

    "not bind invalid phone number" in {
      forAll(invalidPhoneNumber -> "invalidDataItem") {
        dataItem: String =>
          val result = form.bind(Map(fieldName -> dataItem)).apply(fieldName)
          result.errors must contain(invalidFormError)
      }
    }
  }

  def internationalPhoneNumberBindsValidData(form: Form[_],
                                            fieldName: String): Unit = {

    "bind international phone number" in {
      val validDataGenerator = internationalPhoneNumber(doubleZero = true)

      forAll(validDataGenerator -> "validDataItem") {
        dataItem: String =>
          val result = form.bind(Map(fieldName -> dataItem)).apply(fieldName)
          result.value.value mustBe dataItem
          result.errors mustBe empty
      }
    }

    "bind phone number with double zero prefix" in {
      val validDataGenerator = internationalPhoneNumber()

      forAll(validDataGenerator -> "validDataItem") {
        dataItem: String =>
          val result = form.bind(Map(fieldName -> dataItem)).apply(fieldName)
          result.value.value mustBe dataItem
          result.errors mustBe empty
      }
    }
  }
}
