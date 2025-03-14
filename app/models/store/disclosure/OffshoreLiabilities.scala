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
import config.Country

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
  disregardedCDF: Option[Boolean] = None,
  taxYearLiabilities: Option[Map[String, TaxYearWithLiabilities]] = None,
  taxYearForeignTaxDeductions: Option[Map[String, BigInt]] = None,
  countryOfYourOffshoreLiability: Option[Map[String, Country]] = None,
  legalInterpretation: Option[Set[YourLegalInterpretation]] = None,
  otherInterpretation: Option[String] = None,
  notIncludedDueToInterpretation: Option[HowMuchTaxHasNotBeenIncluded] = None,
  maximumValueOfAssets: Option[TheMaximumValueOfAllAssets] = None
) {
  def isComplete = isCompleteForNilDisclosure || isCompleteForFullDisclosure

  def isCompleteForNilDisclosure: Boolean = {
    val yearsIsSetForNil                  = whichYears
      .map(years =>
        years == Set(ReasonableExcusePriorTo) || years == Set(CarelessPriorTo) || years == Set(DeliberatePriorTo)
      )
      .getOrElse(false)
    val descriptionOfYearsBeforeIsDefined =
      taxBeforeFiveYears.isDefined || taxBeforeSevenYears.isDefined || taxBeforeNineteenYears.isDefined
    yearsIsSetForNil && behaviour.isDefined && descriptionOfYearsBeforeIsDefined
  }

  def isCompleteForFullDisclosure: Boolean =
    behaviour.isDefined &&
      whichYears.isDefined &&
      taxYearLiabilities.isDefined &&
      countryOfYourOffshoreLiability.isDefined &&
      legalInterpretation.isDefined &&
      maximumValueOfAssets.isDefined

}

object OffshoreLiabilities {
  implicit val format: OFormat[OffshoreLiabilities] = Json.format[OffshoreLiabilities]
}
