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

package viewmodels.onshore

import models._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import viewmodels.checkAnswers._
import pages.{OnshoreTaxYearLiabilitiesPage, WhatOnshoreLiabilitiesDoYouNeedToDisclosePage}
import play.api.i18n.Messages
import com.google.inject.{Inject, Singleton}

case class CheckYourAnswersViewModel(
  whatOnshoreLiabilitiesDoYouNeedToDiscloseSummaryList: SummaryList,
  summaryList: SummaryList,
  taxYearLists: Seq[(Int, SummaryList)],
  totalAmountsList: SummaryList,
  summaryList1: SummaryList
)

@Singleton
class CheckYourAnswersViewModelCreation @Inject() (
                          whichYearsSummary: WhichOnshoreYearsSummary,
                          taxBeforeFiveYearsSummary: TaxBeforeFiveYearsOnshoreSummary,
                          taxBeforeSevenYearsSummary: TaxBeforeThreeYearsOnshoreSummary,
                          taxBeforeNineteenYearSummary: TaxBeforeNineteenYearsOnshoreSummary) {

  def create(userAnswers: UserAnswers)(implicit messages: Messages): CheckYourAnswersViewModel = {

    val whatOnshoreLiabilitiesDoYouNeedToDiscloseSummaryList = SummaryListViewModel(
      rows = Seq(
        WhatOnshoreLiabilitiesDoYouNeedToDiscloseSummary.row(userAnswers)
      ).flatten
    )

    val summaryList = SummaryListViewModel(
      rows = Seq(
        WhyAreYouMakingThisOnshoreDisclosureSummary.row(userAnswers),
        CDFOnshoreSummary.row(userAnswers),
        ReasonableExcuseOnshoreSummary.row("excuse", userAnswers),
        ReasonableExcuseOnshoreSummary.row("years", userAnswers),
        ReasonableCareOnshoreSummary.row("reasonableCare", userAnswers),
        ReasonableCareOnshoreSummary.row("yearsThisAppliesTo", userAnswers),
        ReasonableExcuseForNotFilingOnshoreSummary.row("reasonableExcuse", userAnswers),
        ReasonableExcuseForNotFilingOnshoreSummary.row("yearsThisAppliesTo", userAnswers),
        whichYearsSummary.row(userAnswers),
        taxBeforeFiveYearsSummary.row(userAnswers),
        taxBeforeSevenYearsSummary.row(userAnswers),
        taxBeforeNineteenYearSummary.row(userAnswers)
      ).flatten
    )

    val taxYears: Seq[OnshoreTaxYearWithLiabilities] = for {
      year <- userAnswers.inverselySortedOnshoreTaxYears.getOrElse(Nil)
      taxYearWithLiabilities <- userAnswers.getByKey(OnshoreTaxYearLiabilitiesPage, year.toString)
    } yield taxYearWithLiabilities

    val taxYearLists: Seq[(Int, SummaryList)] = taxYears.zipWithIndex.map{ case (yearWithLiabilites, i) => 
      (yearWithLiabilites.taxYear.startYear, taxYearWithLiabilitiesToSummaryList(i, yearWithLiabilites, userAnswers))
    }

    val totalAmountsList = totalAmountsSummaryList(taxYears)

    val summaryList1 = SummaryListViewModel(
      rows = Seq(
        IncomeOrGainSourceSummary.row(userAnswers),
        OtherIncomeOrGainSourceSummary.row(userAnswers)
      ).flatten
    )

    CheckYourAnswersViewModel(whatOnshoreLiabilitiesDoYouNeedToDiscloseSummaryList, summaryList, taxYearLists, totalAmountsList, summaryList1)

  }

  def taxYearWithLiabilitiesToSummaryList(i: Int, yearWithLiabilites: OnshoreTaxYearWithLiabilities, userAnswers: UserAnswers)(implicit messages: Messages): SummaryList = {

    val liabilities = yearWithLiabilites.taxYearLiabilities

    val nonBusinessIncome = userAnswers.get(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage) match {
      case Some(value) if(value.contains(WhatOnshoreLiabilitiesDoYouNeedToDisclose.NonBusinessIncome)) => Seq(row(i, "onshoreTaxYearLiabilities.nonBusinessIncome.checkYourAnswersLabel", s"&pound;${liabilities.nonBusinessIncome.getOrElse(0)}", "onshoreTaxYearLiabilities.nonBusinessIncome.hidden"))
      case _ => Nil
    }

    val businessIncome = userAnswers.get(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage) match {
      case Some(value) if(value.contains(WhatOnshoreLiabilitiesDoYouNeedToDisclose.BusinessIncome)) => Seq(row(i, "onshoreTaxYearLiabilities.businessIncome.checkYourAnswersLabel", s"&pound;${liabilities.businessIncome.getOrElse(0)}", "onshoreTaxYearLiabilities.businessIncome.hidden"))
      case _ => Nil
    }

    val lettingIncome = userAnswers.get(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage) match {
      case Some(value) if(value.contains(WhatOnshoreLiabilitiesDoYouNeedToDisclose.LettingIncome)) => Seq(row(i, "onshoreTaxYearLiabilities.lettingIncome.checkYourAnswersLabel", s"&pound;${liabilities.lettingIncome.getOrElse(0)}", "onshoreTaxYearLiabilities.lettingIncome.hidden"))
      case _ => Nil
    }

    val gains = userAnswers.get(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage) match {
      case Some(value) if(value.contains(WhatOnshoreLiabilitiesDoYouNeedToDisclose.Gains)) => Seq(row(i, "onshoreTaxYearLiabilities.gains.checkYourAnswersLabel", s"&pound;${liabilities.gains.getOrElse(0)}", "onshoreTaxYearLiabilities.gains.hidden"))
      case _ => Nil
    } 

    val residentialTaxReduction = userAnswers.get(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage) match {
      case Some(value) if(value.contains(WhatOnshoreLiabilitiesDoYouNeedToDisclose.LettingIncome)) => Seq(row(i,
        "onshoreTaxYearLiabilities.residentialTaxReduction.checkYourAnswersLabel",
        s"${if(liabilities.residentialTaxReduction.getOrElse(false)) messages("site.yes") else messages("site.no")}",
        "onshoreTaxYearLiabilities.residentialTaxReduction.hidden")
      )
      case _ => Nil
    }

    val rows = 
      lettingIncome ++
      gains ++
      businessIncome ++
      nonBusinessIncome ++
      Seq(
        row(i, "onshoreTaxYearLiabilities.unpaidTax.checkYourAnswersLabel", s"&pound;${liabilities.unpaidTax}", "onshoreTaxYearLiabilities.unpaidTax.hidden"),
        row(i, "onshoreTaxYearLiabilities.niContributions.checkYourAnswersLabel", s"&pound;${liabilities.niContributions}", "onshoreTaxYearLiabilities.niContributions.hidden"),
        row(i, "onshoreTaxYearLiabilities.interest.checkYourAnswersLabel", s"&pound;${liabilities.interest}", "onshoreTaxYearLiabilities.interest.hidden"),
        row(i, "onshoreTaxYearLiabilities.penaltyRate.checkYourAnswersLabel", s"&pound;${liabilities.penaltyRate}", "onshoreTaxYearLiabilities.penaltyRate.hidden"),
        totalRow("onshoreTaxYearLiabilities.penaltyAmount.checkYourAnswersLabel", messages("site.2DP", penaltyAmount(liabilities))),
        totalRow("onshoreTaxYearLiabilities.amountDue.checkYourAnswersLabel", messages("site.2DP", yearTotal(liabilities))),
        row(i, "onshoreTaxYearLiabilities.penaltyRateReason.checkYourAnswersLabel", s"${liabilities.penaltyRateReason}", "onshoreTaxYearLiabilities.penaltyRateReason.hidden")
      ) ++ residentialTaxReduction ++ ResidentialReductionSummary.row(i, yearWithLiabilites.taxYear.toString, userAnswers)
      
    SummaryListViewModel(rows)
  }

  def totalAmountsSummaryList(taxYears: Seq[OnshoreTaxYearWithLiabilities])(implicit messages: Messages): SummaryList = {
    val taxYearLiabilities = taxYears.map(_.taxYearLiabilities)
    val unpaidTaxTotal = taxYearLiabilities.map(_.unpaidTax).sum
    val interestTotal = taxYearLiabilities.map(_.interest).sum
    val penaltyAmountTotal = taxYearLiabilities.map(penaltyAmount).sum
    val amountDueTotal = taxYearLiabilities.map(yearTotal(_)).sum

    SummaryListViewModel(
      rows = Seq(
        totalRow("onshoreTaxYearLiabilities.unpaidTax.total", s"&pound;$unpaidTaxTotal"),
        totalRow("onshoreTaxYearLiabilities.interest.total", s"&pound;$interestTotal"),
        totalRow("onshoreTaxYearLiabilities.penaltyAmount.total", messages("site.2DP", penaltyAmountTotal)),
        totalRow("onshoreTaxYearLiabilities.amountDue.total", messages("site.2DP", amountDueTotal))
      )
    )
  }

  def penaltyAmount(taxYearLiabilities: OnshoreTaxYearLiabilities): BigDecimal = {
    (BigDecimal(taxYearLiabilities.penaltyRate) * BigDecimal(taxYearLiabilities.unpaidTax)) /100
  }
  
  def yearTotal(taxYearLiabilities: OnshoreTaxYearLiabilities): BigDecimal = {
    BigDecimal(taxYearLiabilities.unpaidTax) + penaltyAmount(taxYearLiabilities) + BigDecimal(taxYearLiabilities.interest)
  }

  def row(i: Int,label: String, value: String, hiddenLabel: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(HtmlContent(value)),
      actions = Seq(
        ActionItemViewModel("site.change", controllers.onshore.routes.OnshoreTaxYearLiabilitiesController.onPageLoad(i, CheckMode).url)
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
