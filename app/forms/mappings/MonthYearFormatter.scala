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

import play.api.data.FormError
import play.api.data.format.Formatter
import models.MonthYear
import java.time.LocalDate

private[mappings] class MonthYearFormatter(
                                            invalidKey: String,
                                            allRequiredKey: String,
                                            requiredKey: String,
                                            invalidMonthKey: String,
                                            futureDateKey: String,
                                            minimumDateKey: String,
                                            args: Seq[String] = Seq.empty
                                          ) extends Formatter[MonthYear] with Formatters {

  private val fieldKeys: List[String] = List("month", "year")


  private def validateMonth(key: String, month: Int): Either[Seq[FormError], Int] = 
    if (month >=1 && month <=12) Right(month)
    else Left(Seq(FormError(key, invalidMonthKey, args)))

  private def validateYear(key: String, year: Int): Either[Seq[FormError], Int] = {
    val futureYearError = if (year > LocalDate.now().getYear()) Some(FormError(key, futureDateKey, args)) else None
    val minimumDateError = if (year < 1850) Some(FormError(key, minimumDateKey, args)) else None
    
    List(futureYearError, minimumDateError).flatten match {
      case Nil => Right(year)
      case list => Left(list.toSeq)
    }
  }

  private def formatDate(key: String, data: Map[String, String]): Either[Seq[FormError], MonthYear] = {

    val int = intFormatter(
      requiredKey = invalidKey,
      wholeNumberKey = invalidKey,
      nonNumericKey = invalidKey,
      args
    )

    for {
      month <- defaultKey(key, int.bind(s"$key.month", data)).right
      year  <- defaultKey(key, int.bind(s"$key.year", data)).right
      _     <- combineErrors(validateYear(s"$key", year), validateMonth(s"$key.month", month)).right
    } yield MonthYear(month, year)
  }

  private def combineErrors(eitherA: Either[Seq[FormError], Any], eitherB: Either[Seq[FormError], Any]): Either[Seq[FormError], Any] = {
    Seq(eitherA.swap.toOption, eitherB.swap.toOption).flatten.flatten match {
      case Nil => eitherA
      case errors => Left(errors)
    }
  }

  override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], MonthYear] = {

    val fields = fieldKeys.map {
      field =>
        field -> data.get(s"$key.$field").filter(_.nonEmpty)
    }.toMap

    lazy val missingFields = fields
      .withFilter(_._2.isEmpty)
      .map(_._1)
      .toList

    fields.count(_._2.isDefined) match {
      case 2 =>
        formatDate(key, data)
      case 1 =>
        Left(List(FormError(key, requiredKey, missingFields ++ args)))
      case _ =>
        Left(List(FormError(key, allRequiredKey, args)))
    }
  }

  def defaultKey[A](key: String, either: Either[Seq[FormError], A]): Either[Seq[FormError], A] = {
    either.left.map {
      _.map(_.copy(key = key, args = args))
    }
  }

  override def unbind(key: String, value: MonthYear): Map[String, String] =
    Map(
      s"$key.month" -> value.month.toString,
      s"$key.year" -> value.year.toString
    )
}
