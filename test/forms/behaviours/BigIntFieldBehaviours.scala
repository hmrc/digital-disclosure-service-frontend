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

trait BigIntFieldBehaviours extends FieldBehaviours {

  def bigintField(form: Form[_], fieldName: String, nonNumericError: FormError, wholeNumberError: FormError): Unit = {

    "not bind non-numeric numbers" in {

      forAll(nonNumerics -> "nonNumeric") { nonNumeric =>
        val result = form.bind(Map(fieldName -> nonNumeric)).apply(fieldName)
        result.errors must contain only nonNumericError
      }
    }

    "not bind decimals" in {

      forAll(decimals -> "decimal") { decimal =>
        val result = form.bind(Map(fieldName -> decimal)).apply(fieldName)
        result.errors must contain only wholeNumberError
      }
    }

  }

  def bigintFieldWithMinimumZero(form: Form[_], fieldName: String, expectedError: FormError): Unit =
    s"not bind integers below zero" in {

      forAll(bigintsBelowZero -> "intBelowMin") { number: BigInt =>
        val result = form.bind(Map(fieldName -> number.toString)).apply(fieldName)
        result.errors must contain only expectedError
      }
    }

  def bigintFieldWithMinimum(form: Form[_], fieldName: String, minimum: BigInt, expectedError: FormError): Unit =
    s"not bind integers below $minimum" in {

      forAll(bigintsBelowValue(minimum) -> "intBelowMin") { number: BigInt =>
        val result = form.bind(Map(fieldName -> number.toString)).apply(fieldName)
        result.errors must contain only expectedError
      }
    }

  def bigintFieldWithMaximum(form: Form[_], fieldName: String, maximum: BigInt, expectedError: FormError): Unit =
    s"not bind integers above $maximum" in {

      forAll(bigintsAboveValue(maximum) -> "intAboveMax") { number: BigInt =>
        val result = form.bind(Map(fieldName -> number.toString)).apply(fieldName)
        result.errors must contain only expectedError
      }
    }

  def bigintFieldWithRange(
    form: Form[_],
    fieldName: String,
    minimum: BigInt,
    maximum: BigInt,
    expectedError: FormError
  ): Unit =
    s"not bind integers outside the range $minimum to $maximum" in {

      forAll(bigintsOutsideRange(minimum, maximum) -> "bigintOutsideRange") { number =>
        val result = form.bind(Map(fieldName -> number.toString)).apply(fieldName)
        result.errors must contain only expectedError
      }
    }
}
