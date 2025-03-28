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

import java.time.LocalDate
import play.api.data.FieldMapping
import play.api.data.Forms.of
import models.Enumerable
import models.{MonthYear, OffshoreYears, OnshoreYears}

trait Mappings extends Formatters with Constraints {

  protected def text(errorKey: String = "error.required", args: Seq[String] = Seq.empty): FieldMapping[String] =
    of(stringFormatter(errorKey, args))

  protected def int(
    requiredKey: String = "error.required",
    wholeNumberKey: String = "error.wholeNumber",
    nonNumericKey: String = "error.nonNumeric",
    args: Seq[String] = Seq.empty
  ): FieldMapping[Int] =
    of(intFormatter(requiredKey, wholeNumberKey, nonNumericKey, args))

  protected def intWithPound(
    requiredKey: String = "error.required",
    wholeNumberKey: String = "error.wholeNumber",
    nonNumericKey: String = "error.nonNumeric",
    args: Seq[String] = Seq.empty
  ): FieldMapping[Int] =
    of(intFormatterWithPound(requiredKey, wholeNumberKey, nonNumericKey, args))

  protected def intWithPercentage(
    requiredKey: String = "error.required",
    wholeNumberKey: String = "error.wholeNumber",
    nonNumericKey: String = "error.nonNumeric",
    args: Seq[String] = Seq.empty
  ): FieldMapping[Int] =
    of(intFormatterWithPercentage(requiredKey, wholeNumberKey, nonNumericKey, args))

  protected def bigint(
    requiredKey: String = "error.required",
    wholeNumberKey: String = "error.wholeNumber",
    nonNumericKey: String = "error.nonNumeric",
    args: Seq[String] = Seq.empty
  ): FieldMapping[BigInt] =
    of(bigintFormatter(requiredKey, wholeNumberKey, nonNumericKey, args))

  protected def bigintWithPound(
    requiredKey: String = "error.required",
    wholeNumberKey: String = "error.wholeNumber",
    nonNumericKey: String = "error.nonNumeric",
    args: Seq[String] = Seq.empty
  ): FieldMapping[BigInt] =
    of(bigintFormatterWithPound(requiredKey, wholeNumberKey, nonNumericKey, args))

  protected def bigintWithPercentage(
    requiredKey: String = "error.required",
    wholeNumberKey: String = "error.wholeNumber",
    nonNumericKey: String = "error.nonNumeric",
    args: Seq[String] = Seq.empty
  ): FieldMapping[BigInt] =
    of(bigintFormatterWithPercentage(requiredKey, wholeNumberKey, nonNumericKey, args))

  protected def decimal(
    requiredKey: String = "error.required",
    nonNumericKey: String = "error.nonNumeric"
  ): FieldMapping[BigDecimal] =
    of(decimalFormatter(requiredKey, nonNumericKey))

  protected def decimalWithPound(
    requiredKey: String = "error.required",
    nonNumericKey: String = "error.nonNumeric"
  ): FieldMapping[BigDecimal] =
    of(decimalFormatterWithPound(requiredKey, nonNumericKey))

  protected def decimalWithPercentage(
    requiredKey: String = "error.required",
    nonNumericKey: String = "error.nonNumeric"
  ): FieldMapping[BigDecimal] =
    of(decimalFormatterWithPercentage(requiredKey, nonNumericKey))

  protected def boolean(
    requiredKey: String = "error.required",
    invalidKey: String = "error.boolean",
    args: Seq[String] = Seq.empty
  ): FieldMapping[Boolean] =
    of(booleanFormatter(requiredKey, invalidKey, args))

  protected def enumerable[A](
    requiredKey: String = "error.required",
    invalidKey: String = "error.invalid",
    args: Seq[String] = Seq.empty
  )(implicit ev: Enumerable[A]): FieldMapping[A] =
    of(enumerableFormatter[A](requiredKey, invalidKey, args))

  protected def offshoreYears(
    requiredKey: String = "error.required",
    invalidKey: String = "error.invalid",
    args: Seq[String] = Seq.empty
  ): FieldMapping[OffshoreYears] =
    of(offshoreYearsFormatter(requiredKey, invalidKey, args))

  protected def onshoreYears(
    requiredKey: String = "error.required",
    invalidKey: String = "error.invalid",
    args: Seq[String] = Seq.empty
  ): FieldMapping[OnshoreYears] =
    of(onshoreYearsFormatter(requiredKey, invalidKey, args))

  protected def localDate(
    invalidKey: String,
    allRequiredKey: String,
    requiredKey: String,
    invalidDayKey: String,
    invalidMonthKey: String,
    args: Seq[String] = Seq.empty
  ): FieldMapping[LocalDate] =
    of(new LocalDateFormatter(invalidKey, allRequiredKey, requiredKey, invalidDayKey, invalidMonthKey, args))

  protected def monthYear(
    invalidKey: String,
    allRequiredKey: String,
    requiredKey: String,
    invalidMonthKey: String,
    futureDateKey: String,
    minimumDateKey: String,
    args: Seq[String] = Seq.empty
  ): FieldMapping[MonthYear] =
    of(
      new MonthYearFormatter(
        invalidKey,
        allRequiredKey,
        requiredKey,
        invalidMonthKey,
        futureDateKey,
        minimumDateKey,
        args
      )
    )

}
