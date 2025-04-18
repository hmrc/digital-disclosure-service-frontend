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

package models.store.disclosure

import play.api.libs.json.{Json, OFormat}
import models.OtherLiabilityIssues

final case class OtherLiabilities(
  issues: Option[Set[OtherLiabilityIssues]] = None,
  inheritanceGift: Option[String] = None,
  other: Option[String] = None,
  taxCreditsReceived: Option[Boolean] = None
) {
  def isComplete(isAnIndividual: Boolean) = {
    val taxCreditsRequiredAndCompleted = !isAnIndividual || taxCreditsReceived.isDefined
    this match {
      case OtherLiabilities(Some(set), _, _, _)
          if inheritanceTaxComplete(set) && otherComplete(set) && taxCreditsRequiredAndCompleted =>
        true
      case _ => false
    }
  }

  def inheritanceTaxComplete(set: Set[OtherLiabilityIssues]) =
    !set.contains(OtherLiabilityIssues.InheritanceTaxIssues) || inheritanceGift.isDefined
  def otherComplete(set: Set[OtherLiabilityIssues])          = !set.contains(OtherLiabilityIssues.Other) || other.isDefined
}

object OtherLiabilities {
  implicit val format: OFormat[OtherLiabilities] = Json.format[OtherLiabilities]
}
