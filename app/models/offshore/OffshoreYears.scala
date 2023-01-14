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

package models

import play.api.libs.json._
import scala.util.Try

sealed trait OffshoreYears

final case class TaxYearStarting(startYear: Int) extends OffshoreYears with Ordered[TaxYearStarting]  {
  override def toString = startYear.toString

  def compare(that: TaxYearStarting) = (that.startYear) - (this.startYear)
}

object TaxYearStarting {
  implicit val format: Format[TaxYearStarting] =  Json.format[TaxYearStarting]
}

case object PriorTo5Years extends OffshoreYears {
  override def toString = "priorTo5Years"
}

case object PriorTo7Years extends OffshoreYears {
  override def toString = "priorTo7Years"
}

case object NoneOfTheseYears extends OffshoreYears {
  override def toString = "noneOfTheseYears"
}

object RawOffshoreYears {
  def unapply(s: String): Option[Int] = Try(s.toInt).toOption
}

object OffshoreYears {

  def fromString(str: String): Option[OffshoreYears] = {
    str match {
      case "priorTo5Years"        => Some(PriorTo5Years)
      case "priorTo7Years"        => Some(PriorTo7Years)
      case "noneOfTheseYears"     => Some(NoneOfTheseYears)
      case RawOffshoreYears(year) => Some(TaxYearStarting(year))
      case _ => None
    }
  }

  implicit def reads: Reads[OffshoreYears] = Reads {
    case JsString(str) =>
      fromString(str).map {
        s => JsSuccess(s)
      }.getOrElse(JsError("error.invalid"))
    case _ =>
      JsError("error.invalid")
  }

  implicit val writes = Writes[OffshoreYears] {
    value => JsString(value.toString)
  }
  
  implicit val format: Format[OffshoreYears] =  Format(reads, writes)

}