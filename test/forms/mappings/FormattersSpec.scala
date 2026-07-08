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

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import play.api.data.FormError
import models._

class FormattersSpec extends AnyFreeSpec with Matchers with Formatters {

  private val fieldKey            = "value"
  private val requiredKey    = "error.required"
  private val wholeNumberKey = "error.wholeNumber"
  private val nonNumericKey  = "error.nonNumeric"
  private val invalidKey     = "error.invalid"

  "stringFormatter" - {
    val formatter = stringFormatter(requiredKey)

    "bind a valid string" in {
      formatter.bind(fieldKey, Map(fieldKey -> "hello")) shouldBe Right("hello")
    }
    "strip carriage returns" in {
      formatter.bind(fieldKey, Map(fieldKey -> "hel\r\nlo")) shouldBe Right("hel\nlo")
    }
    "fail when the fieldKey is missing" in {
      formatter.bind(fieldKey, Map.empty) shouldBe Left(Seq(FormError(fieldKey, requiredKey)))
    }
    "fail when the value is empty" in {
      formatter.bind(fieldKey, Map(fieldKey -> "")) shouldBe Left(Seq(FormError(fieldKey, requiredKey)))
    }
    "fail when the value is only whitespace" in {
      formatter.bind(fieldKey, Map(fieldKey -> "   ")) shouldBe Left(Seq(FormError(fieldKey, requiredKey)))
    }
    "unbind a value" in {
      formatter.unbind(fieldKey, "hello") shouldBe Map(fieldKey -> "hello")
    }
  }

  "booleanFormatter" - {
    val formatter = booleanFormatter(requiredKey, invalidKey)

    "bind true" in { formatter.bind(fieldKey, Map(fieldKey -> "true")) shouldBe Right(true) }
    "bind false" in { formatter.bind(fieldKey, Map(fieldKey -> "false")) shouldBe Right(false) }
    "fail on an invalid value" in {
      formatter.bind(fieldKey, Map(fieldKey -> "yes")) shouldBe Left(Seq(FormError(fieldKey, invalidKey)))
    }
    "fail when missing" in {
      formatter.bind(fieldKey, Map.empty) shouldBe Left(Seq(FormError(fieldKey, requiredKey)))
    }
    "unbind" in { formatter.unbind(fieldKey, true) shouldBe Map(fieldKey -> "true") }
  }

  "intFormatter" - {
    val formatter = intFormatter(requiredKey, wholeNumberKey, nonNumericKey)

    "bind a plain int" in { formatter.bind(fieldKey, Map(fieldKey -> "123")) shouldBe Right(123) }
    "strip commas, pounds, percent and spaces" in {
      formatter.bind(fieldKey, Map(fieldKey -> " £1,000% ")) shouldBe Right(1000)
    }
    "fail with wholeNumber on a decimal" in {
      formatter.bind(fieldKey, Map(fieldKey -> "1.5")) shouldBe Left(Seq(FormError(fieldKey, wholeNumberKey)))
    }
    "fail with nonNumeric on letters" in {
      formatter.bind(fieldKey, Map(fieldKey -> "abc")) shouldBe Left(Seq(FormError(fieldKey, nonNumericKey)))
    }
    "fail when missing" in {
      formatter.bind(fieldKey, Map.empty) shouldBe Left(Seq(FormError(fieldKey, requiredKey)))
    }
    "unbind" in { formatter.unbind(fieldKey, 123) shouldBe Map(fieldKey -> "123") }
  }

  "intFormatterWithPound" - {
    val formatter = intFormatterWithPound(requiredKey, wholeNumberKey, nonNumericKey)

    "strip pounds and commas" in { formatter.bind(fieldKey, Map(fieldKey -> "£1,000")) shouldBe Right(1000) }
    "fail nonNumeric when a percent remains" in {
      formatter.bind(fieldKey, Map(fieldKey -> "50%")) shouldBe Left(Seq(FormError(fieldKey, nonNumericKey)))
    }
    "fail wholeNumber on a decimal" in {
      formatter.bind(fieldKey, Map(fieldKey -> "1.5")) shouldBe Left(Seq(FormError(fieldKey, wholeNumberKey)))
    }
    "unbind" in { formatter.unbind(fieldKey, 5) shouldBe Map(fieldKey -> "5") }
  }

  "intFormatterWithPercentage" - {
    val formatter = intFormatterWithPercentage(requiredKey, wholeNumberKey, nonNumericKey)

    "strip percent and commas" in { formatter.bind(fieldKey, Map(fieldKey -> "1,000%")) shouldBe Right(1000) }
    "fail nonNumeric when a pound remains" in {
      formatter.bind(fieldKey, Map(fieldKey -> "£100")) shouldBe Left(Seq(FormError(fieldKey, nonNumericKey)))
    }
    "fail wholeNumber on a decimal" in {
      formatter.bind(fieldKey, Map(fieldKey -> "1.5")) shouldBe Left(Seq(FormError(fieldKey, wholeNumberKey)))
    }
    "unbind" in { formatter.unbind(fieldKey, 7) shouldBe Map(fieldKey -> "7") }
  }

  "bigintFormatter" - {
    val formatter = bigintFormatter(requiredKey, wholeNumberKey, nonNumericKey)

    "bind and strip symbols" in {
      formatter.bind(fieldKey, Map(fieldKey -> "£1,000%")) shouldBe Right(BigInt(1000))
    }
    "fail wholeNumber on a decimal" in {
      formatter.bind(fieldKey, Map(fieldKey -> "1.5")) shouldBe Left(Seq(FormError(fieldKey, wholeNumberKey)))
    }
    "fail nonNumeric on letters" in {
      formatter.bind(fieldKey, Map(fieldKey -> "abc")) shouldBe Left(Seq(FormError(fieldKey, nonNumericKey)))
    }
    "unbind" in { formatter.unbind(fieldKey, BigInt(1000)) shouldBe Map(fieldKey -> "1000") }
  }

  "bigintFormatterWithPound" - {
    val formatter = bigintFormatterWithPound(requiredKey, wholeNumberKey, nonNumericKey)

    "strip pounds and commas" in { formatter.bind(fieldKey, Map(fieldKey -> "£1,000")) shouldBe Right(BigInt(1000)) }
    "fail nonNumeric when a percent remains" in {
      formatter.bind(fieldKey, Map(fieldKey -> "50%")) shouldBe Left(Seq(FormError(fieldKey, nonNumericKey)))
    }
    "unbind" in { formatter.unbind(fieldKey, BigInt(5)) shouldBe Map(fieldKey -> "5") }
  }

  "bigintFormatterWithPercentage" - {
    val formatter = bigintFormatterWithPercentage(requiredKey, wholeNumberKey, nonNumericKey)

    "strip percent and commas" in { formatter.bind(fieldKey, Map(fieldKey -> "1,000%")) shouldBe Right(BigInt(1000)) }
    "fail nonNumeric when a pound remains" in {
      formatter.bind(fieldKey, Map(fieldKey -> "£100")) shouldBe Left(Seq(FormError(fieldKey, nonNumericKey)))
    }
    "unbind" in { formatter.unbind(fieldKey, BigInt(7)) shouldBe Map(fieldKey -> "7") }
  }

  "decimalFormatter" - {
    val formatter = decimalFormatter(requiredKey, nonNumericKey)

    "bind and strip symbols" in {
      formatter.bind(fieldKey, Map(fieldKey -> "£1,234.56%")) shouldBe Right(BigDecimal("1234.56"))
    }
    "fail nonNumeric on letters" in {
      formatter.bind(fieldKey, Map(fieldKey -> "abc")) shouldBe Left(Seq(FormError(fieldKey, nonNumericKey)))
    }
    "fail when missing" in {
      formatter.bind(fieldKey, Map.empty) shouldBe Left(Seq(FormError(fieldKey, requiredKey)))
    }
    "unbind" in { formatter.unbind(fieldKey, BigDecimal("1.5")) shouldBe Map(fieldKey -> "1.5") }
  }

  "decimalFormatterWithPound" - {
    val formatter = decimalFormatterWithPound(requiredKey, nonNumericKey)

    "bind and strip pounds" in {
      formatter.bind(fieldKey, Map(fieldKey -> "£1,234.56")) shouldBe Right(BigDecimal("1234.56"))
    }
    "fail nonNumeric when a percent remains" in {
      formatter.bind(fieldKey, Map(fieldKey -> "50%")) shouldBe Left(Seq(FormError(fieldKey, nonNumericKey)))
    }
    "unbind" in { formatter.unbind(fieldKey, BigDecimal("2.5")) shouldBe Map(fieldKey -> "2.5") }
  }

  "decimalFormatterWithPercentage" - {
    val formatter = decimalFormatterWithPercentage(requiredKey, nonNumericKey)

    "bind and strip percent" in {
      formatter.bind(fieldKey, Map(fieldKey -> "12.5%")) shouldBe Right(BigDecimal("12.5"))
    }
    "fail nonNumeric when a pound remains" in {
      formatter.bind(fieldKey, Map(fieldKey -> "£10")) shouldBe Left(Seq(FormError(fieldKey, nonNumericKey)))
    }
    "unbind" in { formatter.unbind(fieldKey, BigDecimal("3.5")) shouldBe Map(fieldKey -> "3.5") }
  }

  "offshoreYearsFormatter" - {
    val formatter = offshoreYearsFormatter(requiredKey, invalidKey)

    "bind reasonableExcusePriorTo" in {
      formatter.bind(fieldKey, Map(fieldKey -> "reasonableExcusePriorTo")) shouldBe Right(ReasonableExcusePriorTo)
    }
    "bind carelessPriorTo" in {
      formatter.bind(fieldKey, Map(fieldKey -> "carelessPriorTo")) shouldBe Right(CarelessPriorTo)
    }
    "bind deliberatePriorTo" in {
      formatter.bind(fieldKey, Map(fieldKey -> "deliberatePriorTo")) shouldBe Right(DeliberatePriorTo)
    }
    "bind a raw tax year" in {
      formatter.bind(fieldKey, Map(fieldKey -> "2015")) shouldBe Right(TaxYearStarting(2015))
    }
    "fail on an unrecognised value" in {
      formatter.bind(fieldKey, Map(fieldKey -> "nope")) shouldBe Left(Seq(FormError(fieldKey, invalidKey)))
    }
    "fail when missing" in {
      formatter.bind(fieldKey, Map.empty) shouldBe Left(Seq(FormError(fieldKey, requiredKey)))
    }
    "unbind a value" in {
      formatter.unbind(fieldKey, TaxYearStarting(2015)) shouldBe Map(fieldKey -> "2015")
    }
  }

  "onshoreYearsFormatter" - {
    val formatter = onshoreYearsFormatter(requiredKey, invalidKey)

    "bind priorToThreeYears" in {
      formatter.bind(fieldKey, Map(fieldKey -> "priorToThreeYears")) shouldBe Right(PriorToThreeYears)
    }
    "bind priorToFiveYears" in {
      formatter.bind(fieldKey, Map(fieldKey -> "priorToFiveYears")) shouldBe Right(PriorToFiveYears)
    }
    "bind priorToNineteenYears" in {
      formatter.bind(fieldKey, Map(fieldKey -> "priorToNineteenYears")) shouldBe Right(PriorToNineteenYears)
    }
    "bind a raw onshore year" in {
      formatter.bind(fieldKey, Map(fieldKey -> "2018")) shouldBe Right(OnshoreYearStarting(2018))
    }
    "fail on an unrecognised value" in {
      formatter.bind(fieldKey, Map(fieldKey -> "nope")) shouldBe Left(Seq(FormError(fieldKey, invalidKey)))
    }
    "fail when missing" in {
      formatter.bind(fieldKey, Map.empty) shouldBe Left(Seq(FormError(fieldKey, requiredKey)))
    }
    "unbind a value" in {
      formatter.unbind(fieldKey, OnshoreYearStarting(2018)) shouldBe Map(fieldKey -> "2018")
    }
  }
}
