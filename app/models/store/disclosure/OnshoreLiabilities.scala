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
import models.WhatOnshoreLiabilitiesDoYouNeedToDisclose._

final case class OnshoreLiabilities(
  behaviour: Option[Set[WhyAreYouMakingThisOnshoreDisclosure]] = None,
  excuseForNotNotifying: Option[ReasonableExcuseOnshore] = None,
  reasonableCare: Option[ReasonableCareOnshore] = None,
  excuseForNotFiling: Option[ReasonableExcuseForNotFilingOnshore] = None,
  whatLiabilities: Option[Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose]] = None,
  whichYears: Option[Set[OnshoreYears]] = None,
  youHaveNotIncludedTheTaxYear: Option[String] = None,
  youHaveNotSelectedCertainTaxYears: Option[String] = None,
  taxBeforeThreeYears: Option[String] = None,
  taxBeforeFiveYears: Option[String] = None,
  taxBeforeNineteenYears: Option[String] = None,
  disregardedCDF: Option[Boolean] = None,
  taxYearLiabilities: Option[Map[String, OnshoreTaxYearWithLiabilities]] = None,
  lettingDeductions: Option[Map[String, BigInt]] = None,
  lettingProperties: Option[Seq[LettingProperty]] = None,
  memberOfLandlordAssociations: Option[Boolean] = None,
  landlordAssociations: Option[String] = None,
  howManyProperties: Option[String] = None,
  corporationTaxLiabilities: Option[Seq[CorporationTaxLiability]] = None,
  directorLoanAccountLiabilities: Option[Seq[DirectorLoanAccountLiabilities]] = None
) {

  def isComplete: Boolean = {

    val typesOfTax               = whatLiabilities.getOrElse(Set())
    val years                    = whichYears.getOrElse(Set())
    val isNilDisclosure          =
      years == Set(PriorToThreeYears) || years == Set(PriorToFiveYears) || years == Set(PriorToNineteenYears)
    val lettingQuestionsAnswered =
      lettingProperties.isDefined && memberOfLandlordAssociations.isDefined && howManyProperties.isDefined

    behaviour.isDefined &&
    whatLiabilities.isDefined &&
    (!typesOfTax.contains(CorporationTax) || (corporationTaxLiabilities.isDefined && corporationTaxLiabilities
      .getOrElse(Set())
      .size > 0)) &&
    (!typesOfTax.contains(DirectorLoan) || (directorLoanAccountLiabilities.isDefined && directorLoanAccountLiabilities
      .getOrElse(Set())
      .size > 0)) &&
    (!typesOfTax.contains(LettingIncome) || lettingQuestionsAnswered || isNilDisclosure) &&
    ((typesOfTax.contains(CorporationTax) || typesOfTax.contains(
      DirectorLoan
    )) || isNilDisclosure || taxYearQuestionsAnswered)
  }

  def taxYearQuestionsAnswered: Boolean =
    (whichYears, taxYearLiabilities) match {
      case (Some(values), Some(liabilities)) =>
        val taxYears = values.collect { case OnshoreYearStarting(y) => y.toString }.toSet
        liabilities.keys.toSet == taxYears
      case _                                 => false
    }

}

object OnshoreLiabilities {
  implicit val format: OFormat[OnshoreLiabilities] = Json.format[OnshoreLiabilities]
}
