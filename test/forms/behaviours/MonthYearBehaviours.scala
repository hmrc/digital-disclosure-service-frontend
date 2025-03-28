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

import java.time.LocalDate
import org.scalacheck.Gen
import play.api.data.{Form, FormError}
import models.MonthYear

trait MonthYearBehaviours extends FieldBehaviours {

  def monthYearField(form: Form[_], key: String, validData: Gen[MonthYear]): Unit = {

    "bind valid data" in {

      forAll(validData -> "valid month and year") { monthYear =>
        val data = Map(
          s"$key.month" -> monthYear.month.toString,
          s"$key.year"  -> monthYear.year.toString
        )

        val result = form.bind(data)

        result.errors.filter(_.key == key) mustBe empty
      }
    }

    "bind valid data with leading/trailing blanks" in {

      forAll(validData -> "valid date") { monthYear =>
        val data = Map(
          s"$key.month" -> s" ${monthYear.month.toString} ",
          s"$key.year"  -> s" ${monthYear.year.toString} "
        )

        val result = form.bind(data)

        result.errors.filter(_.key == key) mustBe empty
      }
    }
  }

  def monthYearFieldCheckingMaxMonth(
    form: Form[_],
    key: String,
    validData: Gen[MonthYear],
    monthError: FormError
  ): Unit =
    "check maximum month value" in {

      forAll(validData -> "valid date", intsAboveValue(12) -> "invalid month") { (monthYear, month) =>
        val data = Map(
          s"$key.month" -> month.toString,
          s"$key.year"  -> monthYear.year.toString
        )

        val result = form.bind(data)

        result.errors must contain(monthError)
      }

    }

  def monthYearFieldInFuture(form: Form[_], key: String, formError: FormError): Unit =
    s"fail to bind a year in the future" in {

      val yearGenerator = intsAboveValue(LocalDate.now().getYear())

      forAll(yearGenerator -> "invalid dates") { year =>
        val data = Map(
          s"$key.month" -> "1",
          s"$key.year"  -> year.toString
        )

        val result = form.bind(data)

        result.errors must contain(formError)
      }
    }

  def monthYearFieldWithMin(form: Form[_], key: String, formError: FormError): Unit =
    s"fail to bind a monthYear earlier than 1850" in {

      val yearGenerator = intsBelowValue(1850)

      forAll(yearGenerator -> "invalid monthYears") { year =>
        val data = Map(
          s"$key.month" -> "1",
          s"$key.year"  -> year.toString
        )

        val result = form.bind(data)

        result.errors must contain(formError)
      }
    }

  def mandatoryMonthYearField(
    form: Form[_],
    key: String,
    requiredAllKey: String,
    errorArgs: Seq[String] = Seq.empty
  ): Unit =
    "fail to bind an empty monthYear" in {

      val result = form.bind(Map.empty[String, String])

      result.errors must contain(FormError(key, requiredAllKey, errorArgs))
    }
}
