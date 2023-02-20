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
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import viewmodels.checkAnswers._
import pages.{ForeignTaxCreditPage, TaxYearLiabilitiesPage}
import play.api.i18n.Messages
import com.google.inject.{Inject, Singleton}

case class CheckYourAnswersViewModel(
  summaryList: SummaryList,
  legalInterpretationlist: SummaryList,
  taxYearLists: Seq[(Int, SummaryList)],
  totalAmountsList: SummaryList,
  liabilitiesTotal: BigDecimal
)

@Singleton
class CheckYourAnswersViewModelCreation @Inject() (whichYearsSummary: WhichYearsSummary, taxBeforeFiveYearsSummary: TaxBeforeFiveYearsSummary, taxBeforeSevenYearsSummary: TaxBeforeSevenYearsSummary, taxBeforeNineteenYearSummary: CanYouTellUsMoreAboutTaxBeforeNineteenYearSummary) {

  def create(userAnswers: UserAnswers)(implicit messages: Messages): CheckYourAnswersViewModel = {

    val summaryList = SummaryListViewModel(
      rows = Seq(
        WhyAreYouMakingThisDisclosureSummary.row(userAnswers),
        ContractualDisclosureFacilitySummary.row(userAnswers),
        WhatIsYourReasonableExcuseSummary.row("excuse", userAnswers),
        WhatIsYourReasonableExcuseSummary.row("years", userAnswers),
        WhatReasonableCareDidYouTakeSummary.row("reasonableCare", userAnswers),
        WhatReasonableCareDidYouTakeSummary.row("yearsThisAppliesTo", userAnswers),
        WhatIsYourReasonableExcuseForNotFilingReturnSummary.row("reasonableExcuse", userAnswers),
        WhatIsYourReasonableExcuseForNotFilingReturnSummary.row("yearsThisAppliesTo", userAnswers),
        CountriesOrTerritoriesSummary.row(userAnswers),
        whichYearsSummary.row(userAnswers),
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
      case Some(value) => Seq(row(i, "foreignTaxCredit.checkYourAnswersLabel", s"&pound;${value}", "foreignTaxCredit.hidden"))
      case _ => Nil
    }

    val rows = Seq(
      row(i, "taxYearLiabilities.income.checkYourAnswersLabel", s"&pound;${liabilities.income}", "taxYearLiabilities.income.hidden"),
      row(i, "taxYearLiabilities.chargeableTransfers.checkYourAnswersLabel", s"&pound;${liabilities.chargeableTransfers}", "taxYearLiabilities.chargeableTransfers.hidden"),
      row(i, "taxYearLiabilities.capitalGains.checkYourAnswersLabel", s"&pound;${liabilities.capitalGains}", "taxYearLiabilities.capitalGains.hidden"),
      row(i, "taxYearLiabilities.unpaidTax.checkYourAnswersLabel", s"&pound;${liabilities.unpaidTax}", "taxYearLiabilities.unpaidTax.hidden"),
      row(i, "taxYearLiabilities.interest.checkYourAnswersLabel", s"&pound;${liabilities.interest}", "taxYearLiabilities.interest.hidden"),
      row(i, "taxYearLiabilities.penaltyRate.checkYourAnswersLabel", s"${liabilities.penaltyRate}%", "taxYearLiabilities.penaltyRate.hidden"),
      row(i, "taxYearLiabilities.penaltyAmount.checkYourAnswersLabel", messages("site.2DP", penaltyAmount(liabilities)), "taxYearLiabilities.penaltyRate.hidden"),
      row(i, "taxYearLiabilities.foreignTaxCredit.checkYourAnswersLabel", if (liabilities.foreignTaxCredit) messages("site.yes") else messages("site.no"), "taxYearLiabilities.foreignTaxCredit.hidden")
    ) ++ foreignTaxCredit ++ Seq(row(i, "taxYearLiabilities.amountDue.checkYourAnswersLabel", messages("site.2DP", yearTotal(liabilities)), "taxYearLiabilities.amountDue.hidden"))

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
        totalRow("taxYearLiabilities.unpaidTax.total", s"&pound;$unpaidTaxTotal"),
        totalRow("taxYearLiabilities.interest.total", s"&pound;$interestTotal"),
        totalRow("taxYearLiabilities.penaltyAmount.total", messages("site.2DP", penaltyAmountTotal)),
        totalRow("taxYearLiabilities.amountDue.total", messages("site.2DP", amountDueTotal))
      )
    )
  }

  def penaltyAmount(taxYearLiabilities: TaxYearLiabilities): BigDecimal = {
    (BigDecimal(taxYearLiabilities.penaltyRate) * BigDecimal(taxYearLiabilities.unpaidTax)) /100
  }
  
  def yearTotal(taxYearLiabilities: TaxYearLiabilities): BigDecimal = {
    BigDecimal(taxYearLiabilities.unpaidTax) + penaltyAmount(taxYearLiabilities) + BigDecimal(taxYearLiabilities.interest)
  }

  def row(i: Int,label: String, value: String, hiddenLabel: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(HtmlContent(value)),
      actions = Seq(
        ActionItemViewModel("site.change", controllers.offshore.routes.TaxYearLiabilitiesController.onPageLoad(i, CheckMode).url)
          .withVisuallyHiddenText(messages(hiddenLabel))
      )
    )
  }

  def totalRow(label: String, value: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(HtmlContent(value)),
      actions = Nil
    )
  }

}
