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

import org.scalacheck.Gen
import play.api.data.{Form, FormError}

import java.time.LocalDate

trait PeriodEndBehaviours extends FieldBehaviours {

  def localDateGen: Gen[LocalDate] = {
    val rangeStart = LocalDate.of(1850, 1, 1).toEpochDay
    val rangeEnd = LocalDate.now().minusDays(1).toEpochDay
    Gen.choose(rangeStart, rangeEnd).map(i => LocalDate.ofEpochDay(i))
  }

    def periodEndField(form: Form[_], key: String, validData: Map[String, String]): Unit = {

      "bind valid data" in {

        forAll(localDateGen -> "valid period end") {
          validPeriodEnd =>

            val periodEnd = Map(
              s"$key.day" -> validPeriodEnd.getDayOfMonth.toString,
              s"$key.month" -> validPeriodEnd.getMonthValue.toString,
              s"$key.year" -> validPeriodEnd.getYear.toString
            )

            val data = validData ++ periodEnd

            val result = form.bind(data)
            result.errors.filter(_.key == key) mustBe empty
        }
      }

      "bind valid data with leading/trailing blanks" in {

        forAll(localDateGen -> "valid period end") {
          validPeriodEnd =>

            val validPeriodEndWithSpaces = Map(
              s"$key.day" -> s" ${validPeriodEnd.getDayOfMonth.toString} ",
              s"$key.month" -> s" ${validPeriodEnd.getMonthValue.toString} ",
              s"$key.year" -> s" ${validPeriodEnd.getYear.toString} "
            )

            val data = validData ++ validPeriodEndWithSpaces

            val result = form.bind(data)
            result.errors.filter(_.key == key) mustBe empty
        }
      }
  }

  def periodEndFieldCheckingMaxDay(form: Form[_], key: String, validData: Map[String, String], formError: FormError): Unit = {

    "check maximum day value" in {

      forAll(localDateGen -> "valid period end", intsAboveValue(31) -> "invalid day") {
        (validPeriodEnd, day) =>

          val periodEnd = Map(
            s"$key.day" -> day.toString,
            s"$key.month" -> validPeriodEnd.getMonthValue.toString,
            s"$key.year" -> validPeriodEnd.getYear.toString
          )

          val data = validData ++ periodEnd

          val result = form.bind(data)

          result.errors must contain(formError)
      }
    }
  }

  def periodEndFieldCheckingMaxMonth(form: Form[_], key: String, validData: Map[String, String], formError: FormError): Unit = {

    "check maximum month value" in {

      forAll(localDateGen -> "valid period end", intsAboveValue(13) -> "invalid month") {
        (validPeriodEnd, invalidMonth) =>

          val periodEnd = Map(
            s"$key.day" -> validPeriodEnd.getDayOfMonth.toString,
            s"$key.month" -> invalidMonth.toString,
            s"$key.year" -> validPeriodEnd.getYear.toString
          )

          val data = validData ++ periodEnd

          val result = form.bind(data)

          result.errors must contain(formError)
      }
    }
  }

  def periodEndFieldInFuture(form: Form[_], key: String, validData: Map[String, String], formError: FormError): Unit = {

    "check maximum year value" in {

      val yearGenerator = Gen.choose(LocalDate.now().getYear, LocalDate.MAX.getYear)

      forAll(yearGenerator -> "invalid year in the future") {
        yearInFuture =>

          val data = validData + (
            s"$key.year" -> yearInFuture.toString
            )

          val result = form.bind(data)

          result.errors must contain(formError)
      }
    }
  }

  def periodEndFieldWithMin(form: Form[_], key: String, validData: Map[String, String], formError: FormError): Unit = {

    "check minimum year value" in {

      val yearGenerator = Gen.choose(LocalDate.MIN.getYear, 1849)

      forAll(yearGenerator -> "invalid year") {
        year =>

          val data = validData + (
            s"$key.year" -> year.toString
          )

          val result = form.bind(data)

          result.errors must contain(formError)
      }
    }
  }
}
