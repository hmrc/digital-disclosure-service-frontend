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

package forms.mappings

import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.data.{Form, FormError}
import models.Enumerable

object MappingsSpec {

  sealed trait Foo
  case object Bar extends Foo
  case object Baz extends Foo

  object Foo {

    val values: Set[Foo] = Set(Bar, Baz)

    implicit val fooEnumerable: Enumerable[Foo] =
      Enumerable(values.toSeq.map(v => v.toString -> v)*)
  }
}

class MappingsSpec extends AnyFreeSpec with Matchers with OptionValues with Mappings {

  import MappingsSpec._

  "text" - {

    val testForm: Form[String] =
      Form(
        "value" -> text()
      )

    "must bind a valid string" in {
      val result = testForm.bind(Map("value" -> "foobar"))
      result.get mustEqual "foobar"
    }

    "must not bind an empty string" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "must not bind a string of whitespace only" in {
      val result = testForm.bind(Map("value" -> " \t"))
      result.errors must contain(FormError("value", "error.required"))
    }

    "must not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "must return a custom error message" in {
      val form   = Form("value" -> text("custom.error"))
      val result = form.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "custom.error"))
    }

    "must unbind a valid value" in {
      val result = testForm.fill("foobar")
      result.apply("value").value.value mustEqual "foobar"
    }
  }

  "boolean" - {

    val testForm: Form[Boolean] =
      Form(
        "value" -> boolean()
      )

    "must bind true" in {
      val result = testForm.bind(Map("value" -> "true"))
      result.get mustEqual true
    }

    "must bind false" in {
      val result = testForm.bind(Map("value" -> "false"))
      result.get mustEqual false
    }

    "must not bind a non-boolean" in {
      val result = testForm.bind(Map("value" -> "not a boolean"))
      result.errors must contain(FormError("value", "error.boolean"))
    }

    "must not bind an empty value" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "must not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "must unbind" in {
      val result = testForm.fill(true)
      result.apply("value").value.value mustEqual "true"
    }
  }

  "int" - {

    val testForm: Form[Int] =
      Form(
        "value" -> int()
      )

    "must bind a valid integer" in {
      val result = testForm.bind(Map("value" -> "1"))
      result.get mustEqual 1
    }

    "must not bind an empty value" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "must not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "must unbind a valid value" in {
      val result = testForm.fill(123)
      result.apply("value").value.value mustEqual "123"
    }
  }

  "enumerable" - {

    val testForm = Form(
      "value" -> enumerable[Foo]()
    )

    "must bind a valid option" in {
      val result = testForm.bind(Map("value" -> "Bar"))
      result.get mustEqual Bar
    }

    "must not bind an invalid option" in {
      val result = testForm.bind(Map("value" -> "Not Bar"))
      result.errors must contain(FormError("value", "error.invalid"))
    }

    "must not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }
  }

  "intWithPound" - {
    val testForm = Form("value" -> intWithPound())
    "must bind stripping pound and commas" in {
      testForm.bind(Map("value" -> "£1,000")).get mustEqual 1000
    }
    "must not bind a decimal" in {
      testForm.bind(Map("value" -> "1.5")).errors must contain(FormError("value", "error.wholeNumber"))
    }
    "must unbind" in {
      testForm.fill(1000).apply("value").value.value mustEqual "1000"
    }
  }

  "intWithPercentage" - {
    val testForm = Form("value" -> intWithPercentage())
    "must bind stripping percent and commas" in {
      testForm.bind(Map("value" -> "1,000%")).get mustEqual 1000
    }
    "must not bind a decimal" in {
      testForm.bind(Map("value" -> "1.5")).errors must contain(FormError("value", "error.wholeNumber"))
    }
    "must unbind" in {
      testForm.fill(50).apply("value").value.value mustEqual "50"
    }
  }

  "bigint" - {
    val testForm = Form("value" -> bigint())
    "must bind a valid bigint" in {
      testForm.bind(Map("value" -> "1000")).get mustEqual BigInt(1000)
    }
    "must not bind a non-numeric" in {
      testForm.bind(Map("value" -> "abc")).errors must contain(FormError("value", "error.nonNumeric"))
    }
    "must unbind" in {
      testForm.fill(BigInt(1000)).apply("value").value.value mustEqual "1000"
    }
  }

  "bigintWithPound" - {
    val testForm = Form("value" -> bigintWithPound())
    "must bind stripping pound and commas" in {
      testForm.bind(Map("value" -> "£1,000")).get mustEqual BigInt(1000)
    }
    "must not bind a decimal" in {
      testForm.bind(Map("value" -> "1.5")).errors must contain(FormError("value", "error.wholeNumber"))
    }
    "must unbind" in {
      testForm.fill(BigInt(5)).apply("value").value.value mustEqual "5"
    }
  }

  "bigintWithPercentage" - {
    val testForm = Form("value" -> bigintWithPercentage())
    "must bind stripping percent and commas" in {
      testForm.bind(Map("value" -> "1,000%")).get mustEqual BigInt(1000)
    }
    "must not bind a decimal" in {
      testForm.bind(Map("value" -> "1.5")).errors must contain(FormError("value", "error.wholeNumber"))
    }
    "must unbind" in {
      testForm.fill(BigInt(7)).apply("value").value.value mustEqual "7"
    }
  }

  "decimal" - {
    val testForm = Form("value" -> decimal())
    "must bind a valid decimal" in {
      testForm.bind(Map("value" -> "£1,234.56")).get mustEqual BigDecimal("1234.56")
    }
    "must not bind a non-numeric" in {
      testForm.bind(Map("value" -> "abc")).errors must contain(FormError("value", "error.nonNumeric"))
    }
    "must not bind an empty map" in {
      testForm.bind(Map.empty[String, String]).errors must contain(FormError("value", "error.required"))
    }
    "must unbind" in {
      testForm.fill(BigDecimal("1.5")).apply("value").value.value mustEqual "1.5"
    }
  }

  "decimalWithPound" - {
    val testForm = Form("value" -> decimalWithPound())
    "must bind stripping pound" in {
      testForm.bind(Map("value" -> "£1,234.56")).get mustEqual BigDecimal("1234.56")
    }
    "must not bind a non-numeric" in {
      testForm.bind(Map("value" -> "abc")).errors must contain(FormError("value", "error.nonNumeric"))
    }
    "must unbind" in {
      testForm.fill(BigDecimal("2.5")).apply("value").value.value mustEqual "2.5"
    }
  }

  "decimalWithPercentage" - {
    val testForm = Form("value" -> decimalWithPercentage())
    "must bind stripping percent" in {
      testForm.bind(Map("value" -> "12.5%")).get mustEqual BigDecimal("12.5")
    }
    "must not bind a non-numeric" in {
      testForm.bind(Map("value" -> "abc")).errors must contain(FormError("value", "error.nonNumeric"))
    }
    "must unbind" in {
      testForm.fill(BigDecimal("3.5")).apply("value").value.value mustEqual "3.5"
    }
  }

  "offshoreYears" - {
    val testForm = Form("value" -> offshoreYears())
    "must bind a raw tax year" in {
      testForm.bind(Map("value" -> "2015")).get mustEqual models.TaxYearStarting(2015)
    }
    "must bind a keyword value" in {
      testForm.bind(Map("value" -> "carelessPriorTo")).get mustEqual models.CarelessPriorTo
    }
    "must not bind an invalid value" in {
      testForm.bind(Map("value" -> "nope")).errors must contain(FormError("value", "error.invalid"))
    }
    "must unbind" in {
      testForm.fill(models.TaxYearStarting(2015)).apply("value").value.value mustEqual "2015"
    }
  }

  "onshoreYears" - {
    val testForm = Form("value" -> onshoreYears())
    "must bind a raw onshore year" in {
      testForm.bind(Map("value" -> "2018")).get mustEqual models.OnshoreYearStarting(2018)
    }
    "must bind a keyword value" in {
      testForm.bind(Map("value" -> "priorToFiveYears")).get mustEqual models.PriorToFiveYears
    }
    "must not bind an invalid value" in {
      testForm.bind(Map("value" -> "nope")).errors must contain(FormError("value", "error.invalid"))
    }
    "must unbind" in {
      testForm.fill(models.OnshoreYearStarting(2018)).apply("value").value.value mustEqual "2018"
    }
  }
}
