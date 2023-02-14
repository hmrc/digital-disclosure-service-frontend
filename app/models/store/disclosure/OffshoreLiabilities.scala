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
import models._

final case class OffshoreLiabilities(
  behaviour: Option[Set[WhyAreYouMakingThisDisclosure]] = None,
  excuseForNotNotifying: Option[WhatIsYourReasonableExcuse] = None,
  reasonableCare: Option[WhatReasonableCareDidYouTake] = None,
  excuseForNotFiling: Option[WhatIsYourReasonableExcuseForNotFilingReturn] = None,
  whichYears: Option[Set[OffshoreYears]] = None,
  youHaveNotIncludedTheTaxYear: Option[String] = None,
  youHaveNotSelectedCertainTaxYears: Option[String] = None,
  taxBeforeFiveYears: Option[String] = None,
  taxBeforeSevenYears: Option[String] = None,
  taxBeforeNineteenYears: Option[String] = None,
  taxYearLiabilities: Option[Map[String, TaxYearWithLiabilities]] = None,
  legalInterpretation: Option[Set[YourLegalInterpretation]] = None,
  otherInterpretation: Option[String] = None,
  notIncludedDueToInterpretation: Option[HowMuchTaxHasNotBeenIncluded] = None,
  maximumValueOfAssets: Option[TheMaximumValueOfAllAssets] = None
) {
  def isComplete = this match {
    case OffshoreLiabilities(Some(_), _, _, _, Some(years), _, _, _, _, _, Some(reasonableExcusePriorTo), _, _, _, _) if years == Set(ReasonableExcusePriorTo) => true
    case OffshoreLiabilities(Some(_), _, _, _, Some(years), _, _, _, _, _, Some(carelessPriorTo), _, _, _, _) if years == Set(CarelessPriorTo) => true
    case OffshoreLiabilities(Some(_), _, _, _, Some(years), _, _, _, _, _, Some(deliberatePriorTo), _, _, _, _) if years == Set(DeliberatePriorTo) => true
    case OffshoreLiabilities(Some(_), _, _, _, Some(years), _, _, _, _, _, _, _, _, _, _) => true
    case _ => false
  }
}

object OffshoreLiabilities {
  implicit val format: OFormat[OffshoreLiabilities] = Json.format[OffshoreLiabilities]
}