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

package viewmodels.offshore

import models._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.govuk.summarylist._
import viewmodels.checkAnswers._
import pages.{ForeignTaxCreditPage, TaxYearLiabilitiesPage}
import play.api.i18n.Messages
import com.google.inject.{Inject, Singleton}
import viewmodels.RowHelper
import viewmodels.RevealFullText
import scala.math.BigDecimal.RoundingMode

case class CheckYourAnswersViewModel(
  summaryList: SummaryList,
  legalInterpretationlist: SummaryList,
  taxYearLists: Seq[(Int, SummaryList)],
  totalAmountsList: SummaryList,
  liabilitiesTotal: BigDecimal
)

@Singleton
class CheckYourAnswersViewModelCreation @Inject() 
  (whichYearsSummary: WhichYearsSummary, 
  taxBeforeFiveYearsSummary: TaxBeforeFiveYearsSummary, 
  taxBeforeSevenYearsSummary: TaxBeforeSevenYearsSummary, 
  taxBeforeNineteenYearSummary: TaxBeforeNineteenYearsSummary,
  revealFullText: RevealFullText) extends RowHelper {

  def create(userAnswers: UserAnswers)(implicit messages: Messages): CheckYourAnswersViewModel = {

    val summaryList = SummaryListViewModel(
      rows = Seq(
        WhyAreYouMakingThisDisclosureSummary.row(userAnswers),
        ContractualDisclosureFacilitySummary.row(userAnswers),
        WhatIsYourReasonableExcuseSummary.row("excuse", userAnswers, revealFullText),
        WhatIsYourReasonableExcuseSummary.row("years", userAnswers, revealFullText),
        WhatReasonableCareDidYouTakeSummary.row("reasonableCare", userAnswers, revealFullText),
        WhatReasonableCareDidYouTakeSummary.row("yearsThisAppliesTo", userAnswers, revealFullText),
        WhatIsYourReasonableExcuseForNotFilingReturnSummary.row("reasonableExcuse", userAnswers, revealFullText),
        WhatIsYourReasonableExcuseForNotFilingReturnSummary.row("yearsThisAppliesTo", userAnswers, revealFullText),
        CountriesOrTerritoriesSummary.row(userAnswers),
        whichYearsSummary.row(userAnswers),
        YouHaveNotIncludedTheTaxYearSummary.row(userAnswers),
        YouHaveNotSelectedCertainTaxYearSummary.row(userAnswers),
        taxBeforeFiveYearsSummary.row(userAnswers),
        taxBeforeSevenYearsSummary.row(userAnswers),
        taxBeforeNineteenYearSummary.row(userAnswers)
      ).flatten
    )

    val taxYears: Seq[TaxYearWithLiabilities] = for {
      year <- userAnswers.inverselySortedOffshoreTaxYears.getOrElse(Nil)
      taxYearWithLiabilities <- userAnswers.getByKey(TaxYearLiabilitiesPage, year.toString)
    } yield taxYearWithLiabilities

    val taxYearLists: Seq[(Int, SummaryList)] = taxYears.zipWithIndex.map{ case (yearWithLiabilites, i) => 
      (yearWithLiabilites.taxYear.startYear, taxYearWithLiabilitiesToSummaryList(i, yearWithLiabilites, userAnswers))
    }

    val totalAmountsList = totalAmountsSummaryList(taxYears)
    val liabilitiesTotal: BigDecimal = taxYears.map(yearWithLiabilities => yearTotal(yearWithLiabilities.taxYearLiabilities)).sum

    val legalInterpretationlist = SummaryListViewModel(
      rows = Seq(
        YourLegalInterpretationSummary.row(userAnswers),
        UnderWhatConsiderationSummary.row(userAnswers),
        HowMuchTaxHasNotBeenIncludedSummary.row(userAnswers),
        TheMaximumValueOfAllAssetsSummary.row(userAnswers)
      ).flatten
    )

    CheckYourAnswersViewModel(summaryList, legalInterpretationlist, taxYearLists, totalAmountsList, liabilitiesTotal)

  }

  def taxYearWithLiabilitiesToSummaryList(i: Int, yearWithLiabilites: TaxYearWithLiabilities, userAnswers: UserAnswers)(implicit messages: Messages): SummaryList = {

    val liabilities = yearWithLiabilites.taxYearLiabilities

    val foreignTaxCredit = userAnswers.getByKey(ForeignTaxCreditPage, yearWithLiabilites.taxYear.startYear.toString) match {
      case Some(value) => Seq(poundRowCase(i, "foreignTaxCredit.checkYourAnswersLabel", s"${value}", "foreignTaxCredit.hidden", OFFSHORE))
      case _ => Nil
    }

    val rows = Seq(
      poundRowCase(i, "taxYearLiabilities.income.checkYourAnswersLabel", s"${liabilities.income}", "taxYearLiabilities.income.hidden", OFFSHORE),
      poundRowCase(i, "taxYearLiabilities.chargeableTransfers.checkYourAnswersLabel", s"${liabilities.chargeableTransfers}", "taxYearLiabilities.chargeableTransfers.hidden", OFFSHORE),
      poundRowCase(i, "taxYearLiabilities.capitalGains.checkYourAnswersLabel", s"${liabilities.capitalGains}", "taxYearLiabilities.capitalGains.hidden", OFFSHORE),
      poundRowCase(i, "taxYearLiabilities.unpaidTax.checkYourAnswersLabel", s"${liabilities.unpaidTax}", "taxYearLiabilities.unpaidTax.hidden", OFFSHORE),
      poundRowCase(i, "taxYearLiabilities.interest.checkYourAnswersLabel", s"${liabilities.interest}", "taxYearLiabilities.interest.hidden", OFFSHORE),
      rowCase(i, "taxYearLiabilities.penaltyRate.checkYourAnswersLabel", s"${liabilities.penaltyRate}%", "taxYearLiabilities.penaltyRate.hidden", OFFSHORE),
      totalRow("taxYearLiabilities.penaltyAmount.checkYourAnswersLabel", messages("site.2DP", penaltyAmount(liabilities))),
      rowCase(i, "taxYearLiabilities.foreignTaxCredit.checkYourAnswersLabel", if (liabilities.foreignTaxCredit) messages("site.yes") else messages("site.no"), "taxYearLiabilities.foreignTaxCredit.hidden", OFFSHORE)
    ) ++ foreignTaxCredit ++ Seq(
      totalRow("taxYearLiabilities.amountDue.checkYourAnswersLabel", messages("site.2DP", yearTotal(liabilities))),
      rowCase(i, "onshoreTaxYearLiabilities.penaltyRateReason.checkYourAnswersLabel", s"${liabilities.penaltyRateReason}", "onshoreTaxYearLiabilities.penaltyRateReason.hidden", OFFSHORE)
    )

    SummaryListViewModel(rows)
  }

  def totalAmountsSummaryList(taxYears: Seq[TaxYearWithLiabilities])(implicit messages: Messages): SummaryList = {
    val taxYearLiabilities = taxYears.map(_.taxYearLiabilities)
    val unpaidTaxTotal = taxYearLiabilities.map(_.unpaidTax).sum
    val interestTotal = taxYearLiabilities.map(_.interest).sum
    val penaltyAmountTotal = taxYearLiabilities.map(penaltyAmount).sum
    val amountDueTotal = taxYearLiabilities.map(yearTotal(_)).sum

    SummaryListViewModel(
      rows = Seq(
        totalRow("taxYearLiabilities.unpaidTax.total", s"$unpaidTaxTotal"),
        totalRow("taxYearLiabilities.interest.total", s"$interestTotal"),
        totalRow("taxYearLiabilities.penaltyAmount.total", messages("site.2DP", penaltyAmountTotal)),
        totalRow("taxYearLiabilities.amountDue.total", messages("site.2DP", amountDueTotal))
      )
    )
  }

  def penaltyAmount(taxYearLiabilities: TaxYearLiabilities): BigDecimal = {
    ((taxYearLiabilities.penaltyRate * BigDecimal(taxYearLiabilities.unpaidTax)) /100).setScale(2, RoundingMode.DOWN)
  }
  
  def yearTotal(taxYearLiabilities: TaxYearLiabilities): BigDecimal = {
    BigDecimal(taxYearLiabilities.unpaidTax) + penaltyAmount(taxYearLiabilities) + BigDecimal(taxYearLiabilities.interest)
  }

}
