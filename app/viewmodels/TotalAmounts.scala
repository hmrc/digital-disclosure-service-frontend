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

package viewmodels

import models._
import models.store._
import models.store.disclosure._
import scala.math.BigDecimal.RoundingMode

case class TotalAmounts(
  unpaidTaxTotal: BigInt,
  niContributionsTotal: BigInt,
  interestTotal: BigInt,
  penaltyAmountTotal: BigDecimal,
  amountDueTotal: BigDecimal
) {
  def +(that: TotalAmounts): TotalAmounts = {
    val unpaidTaxTotal: BigInt = this.unpaidTaxTotal + that.unpaidTaxTotal
    val niContributionsTotal: BigInt = this.niContributionsTotal + that.niContributionsTotal
    val interestTotal: BigInt = this.interestTotal + that.interestTotal
    val penaltyAmountTotal: BigDecimal = this.penaltyAmountTotal + that.penaltyAmountTotal
    val amountDueTotal: BigDecimal = this.amountDueTotal + that.amountDueTotal
    TotalAmounts(
      unpaidTaxTotal = unpaidTaxTotal,
      niContributionsTotal = niContributionsTotal,
      interestTotal = interestTotal,
      penaltyAmountTotal = penaltyAmountTotal,
      amountDueTotal = amountDueTotal
    )
  }
}

object TotalAmounts {

  val emptyTotals = TotalAmounts(
    unpaidTaxTotal = BigInt(0),
    niContributionsTotal = BigInt(0),
    interestTotal = BigInt(0),
    penaltyAmountTotal = BigDecimal(0),
    amountDueTotal = BigDecimal(0)
  )

  def apply(fullDisclosure: FullDisclosure): TotalAmounts = {

    val onshoreTotals = 
      fullDisclosure.onshoreLiabilities
        .map(getOnshoreTotals)
        .getOrElse(emptyTotals)

    val offshoreTotals = 
      fullDisclosure.offshoreLiabilities.taxYearLiabilities
        .map(getOffshoreTaxYearTotals)
        .getOrElse(emptyTotals)

    onshoreTotals + offshoreTotals
    
  }

  def getPenaltyAmount(penaltyRate: BigDecimal, unpaidAmount: BigInt): BigDecimal = {
    ((penaltyRate * BigDecimal(unpaidAmount)) /100).setScale(2, RoundingMode.DOWN)
  }
  
  def getPeriodTotal(penaltyRate: BigDecimal, unpaidAmount: BigInt, interest: BigInt): BigDecimal = {
    BigDecimal(unpaidAmount) + getPenaltyAmount(penaltyRate, unpaidAmount) + BigDecimal(interest)
  }

  def getOnshoreTotals(onshoreLiabilities: OnshoreLiabilities): TotalAmounts = {

    val taxYearTotals = onshoreLiabilities.taxYearLiabilities.map(getOnshoreTaxYearTotals).getOrElse(emptyTotals)
    val ctTotals = onshoreLiabilities.corporationTaxLiabilities.map(getCorporationTaxTotals).getOrElse(emptyTotals)
    val directorLoanTotals = onshoreLiabilities.directorLoanAccountLiabilities.map(getDirectorLoanTotals).getOrElse(emptyTotals)

    taxYearTotals + ctTotals + directorLoanTotals
  }

  def getOnshoreTaxYearTotals(liabilities: Map[String, OnshoreTaxYearWithLiabilities]): TotalAmounts = {

    val taxYearLiabilities = liabilities.values.map(_.taxYearLiabilities)

    val unpaidTaxTotal = taxYearLiabilities.map(_.unpaidTax).sum
    val niContributionsTotal = taxYearLiabilities.map(_.niContributions).sum
    val interestTotal = taxYearLiabilities.map(_.interest).sum
    val penaltyAmountTotal = taxYearLiabilities.map(l => getPenaltyAmount(l.penaltyRate, l.niContributions + l.unpaidTax)).sum
    val amountDueTotal = taxYearLiabilities.map(l => getPeriodTotal(l.penaltyRate, l.niContributions + l.unpaidTax, l.interest)).sum

    TotalAmounts(
      unpaidTaxTotal = unpaidTaxTotal,
      niContributionsTotal = niContributionsTotal,
      interestTotal = interestTotal,
      penaltyAmountTotal = penaltyAmountTotal,
      amountDueTotal = amountDueTotal
    )
    
  }

  def getOffshoreTaxYearTotals(liabilities: Map[String, TaxYearWithLiabilities]): TotalAmounts = {

    val taxYearLiabilities = liabilities.values.map(_.taxYearLiabilities)

    val unpaidTaxTotal = taxYearLiabilities.map(_.unpaidTax).sum
    val interestTotal = taxYearLiabilities.map(_.interest).sum
    val penaltyAmountTotal = taxYearLiabilities.map(l => getPenaltyAmount(l.penaltyRate, l.unpaidTax)).sum
    val amountDueTotal = taxYearLiabilities.map(l => getPeriodTotal(l.penaltyRate, l.unpaidTax, l.interest)).sum

    TotalAmounts(
      unpaidTaxTotal = unpaidTaxTotal,
      niContributionsTotal = 0,
      interestTotal = interestTotal,
      penaltyAmountTotal = penaltyAmountTotal,
      amountDueTotal = amountDueTotal
    )
  }

  def getCorporationTaxTotals(liabilities: Seq[CorporationTaxLiability]): TotalAmounts = {
    val unpaidTaxTotal = liabilities.map(_.howMuchUnpaid).sum
    val interestTotal = liabilities.map(_.howMuchInterest).sum
    val penaltyAmountTotal = liabilities.map(l => getPenaltyAmount(l.penaltyRate, l.howMuchUnpaid)).sum
    val amountDueTotal = liabilities.map(l => getPeriodTotal(l.penaltyRate, l.howMuchUnpaid, l.howMuchInterest)).sum

    TotalAmounts(
      unpaidTaxTotal = unpaidTaxTotal,
      niContributionsTotal = 0,
      interestTotal = interestTotal,
      penaltyAmountTotal = penaltyAmountTotal,
      amountDueTotal = amountDueTotal
    )
  }

  def getDirectorLoanTotals(liabilities: Seq[DirectorLoanAccountLiabilities]): TotalAmounts = {
    val unpaidTaxTotal = liabilities.map(_.unpaidTax).sum
    val interestTotal = liabilities.map(_.interest).sum
    val penaltyAmountTotal = liabilities.map(l => getPenaltyAmount(l.penaltyRate, l.unpaidTax)).sum
    val amountDueTotal = liabilities.map(l => getPeriodTotal(l.penaltyRate, l.unpaidTax, l.interest)).sum

    TotalAmounts(
      unpaidTaxTotal = unpaidTaxTotal,
      niContributionsTotal = 0,
      interestTotal = interestTotal,
      penaltyAmountTotal = penaltyAmountTotal,
      amountDueTotal = amountDueTotal
    )
  }


}