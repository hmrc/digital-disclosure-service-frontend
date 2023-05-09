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

sealed trait OnshoreYears extends Ordered[OnshoreYears] {
  def compare(that: OnshoreYears) = (this, that) match {
    case (thisYear: OnshoreYearStarting, thatYear: OnshoreYearStarting) => (thatYear.startYear) - (thisYear.startYear)
    case (thisYear: OnshoreYearStarting, _) => -1
    case (_, thisYear: OnshoreYearStarting) => 1
    case (_, _) => 0
  }
}

final case class OnshoreYearStarting(startYear: Int) extends OnshoreYears  {
  override def toString = startYear.toString

  def compare(that: OnshoreYearStarting) = (that.startYear) - (this.startYear)
}

object OnshoreYearStarting {
  def findMissingYears(yearList: List[OnshoreYearStarting]): List[OnshoreYearStarting] =
    yearList.sorted(Ordering[OnshoreYearStarting].reverse).map(_.startYear) match {
      case (head :: tail) :+ last => 
        val yearsBetweenFirstAndLast = Range(head, last)
        val missingYearsAsInts = yearsBetweenFirstAndLast.filterNot{int => yearList.contains(OnshoreYearStarting(int))}
        missingYearsAsInts.map(OnshoreYearStarting(_)).sorted(Ordering[OnshoreYearStarting]).toList
      case _ => Nil
    }

  implicit val format: Format[OnshoreYearStarting] =  Json.format[OnshoreYearStarting]
}

case object PriorToThreeYears extends OnshoreYears {
  override def toString = "priorToThreeYears"
}

case object PriorToFiveYears extends OnshoreYears {
  override def toString = "priorToFiveYears"
}

case object PriorToNineteenYears extends OnshoreYears {
  override def toString = "priorToNineteenYears"
}

object RawOnshoreYears {
  def unapply(s: String): Option[Int] = Try(s.toInt).toOption
}

object OnshoreYears {

  def fromString(str: String): Option[OnshoreYears] = {
    str match {
      case "priorToThreeYears"        => Some(PriorToThreeYears)
      case "priorToFiveYears"         => Some(PriorToFiveYears)
      case "priorToNineteenYears"     => Some(PriorToNineteenYears)
      case RawOnshoreYears(year) => Some(OnshoreYearStarting(year))
      case _ => None
    }
  }


  implicit def reads: Reads[OnshoreYears] = Reads {
    case JsString(str) =>
      fromString(str).map {
        s => JsSuccess(s)
      }.getOrElse(JsError("error.invalid"))
    case _ =>
      JsError("error.invalid")
  }

  implicit val writes = Writes[OnshoreYears] {
    value => JsString(value.toString)
  }
  
  implicit val format: Format[OnshoreYears] =  Format(reads, writes)

}