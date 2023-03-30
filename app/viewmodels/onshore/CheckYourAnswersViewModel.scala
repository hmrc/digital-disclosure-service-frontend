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
import models.store.FullDisclosure
import models.store.disclosure.OnshoreLiabilities
import viewmodels.TotalAmounts
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import viewmodels.checkAnswers._
import pages.{OnshoreTaxYearLiabilitiesPage, WhatOnshoreLiabilitiesDoYouNeedToDisclosePage}
import play.api.i18n.Messages
import com.google.inject.{Inject, Singleton}
import services.UAToDisclosureService
import viewmodels.RowHelper

case class CheckYourAnswersViewModel(
  summaryList: SummaryList,
  taxYearLists: Seq[(Int, SummaryList)],
  corporationTax: CorporationTaxLiabilitiesSummaryViewModel,
  directorLoan: DirectorLoanAccountLiabilitiesSummaryViewModel,
  totalAmountsList: SummaryList,
  liabilitiesTotal: BigDecimal
)

@Singleton
class CheckYourAnswersViewModelCreation @Inject() (
                          whichYearsSummary: WhichOnshoreYearsSummary,
                          taxBeforeFiveYearsSummary: TaxBeforeFiveYearsOnshoreSummary,
                          taxBeforeSevenYearsSummary: TaxBeforeThreeYearsOnshoreSummary,
                          taxBeforeNineteenYearSummary: TaxBeforeNineteenYearsOnshoreSummary,
                          uaToDisclosureService: UAToDisclosureService) extends RowHelper {

  def create(userAnswers: UserAnswers)(implicit messages: Messages): CheckYourAnswersViewModel = {

    val summaryList = SummaryListViewModel(
      rows = Seq(
        WhatOnshoreLiabilitiesDoYouNeedToDiscloseSummary.row(userAnswers),
        WhyAreYouMakingThisOnshoreDisclosureSummary.row(userAnswers),
        CDFOnshoreSummary.row(userAnswers),
        ReasonableExcuseOnshoreSummary.row("excuse", userAnswers),
        ReasonableExcuseOnshoreSummary.row("years", userAnswers),
        ReasonableCareOnshoreSummary.row("reasonableCare", userAnswers),
        ReasonableCareOnshoreSummary.row("yearsThisAppliesTo", userAnswers),
        ReasonableExcuseForNotFilingOnshoreSummary.row("reasonableExcuse", userAnswers),
        ReasonableExcuseForNotFilingOnshoreSummary.row("yearsThisAppliesTo", userAnswers),
        whichYearsSummary.row(userAnswers),
        NotIncludedSingleTaxYearSummary.row(userAnswers),
        NotIncludedMultipleTaxYearsSummary.row(userAnswers),
        taxBeforeFiveYearsSummary.row(userAnswers),
        taxBeforeSevenYearsSummary.row(userAnswers),
        taxBeforeNineteenYearSummary.row(userAnswers),
        PropertyAddedSummary.row(userAnswers)
      ).flatten
    )

    val taxYears: Seq[OnshoreTaxYearWithLiabilities] = for {
      year <- userAnswers.inverselySortedOnshoreTaxYears.getOrElse(Nil)
      taxYearWithLiabilities <- userAnswers.getByKey(OnshoreTaxYearLiabilitiesPage, year.toString)
    } yield taxYearWithLiabilities

    val taxYearLists: Seq[(Int, SummaryList)] = taxYears.zipWithIndex.map{ case (yearWithLiabilites, i) => 
      (yearWithLiabilites.taxYear.startYear, taxYearWithLiabilitiesToSummaryList(i, yearWithLiabilites, userAnswers))
    }

    val corporationTaxLiabilities = CorporationTaxLiabilitiesSummaryViewModelCreation.create(userAnswers)
    val directorLoanAccountLiabilities = new DirectorLoanAccountLiabilitiesSummaryViewModelCreation().create(userAnswers)

    val disclosure = uaToDisclosureService.uaToFullDisclosure(userAnswers)
    val totalAmountsList = totalAmountsSummaryList(disclosure)

    val liabilitiesTotal = TotalAmounts.getOnshoreTotals(disclosure.onshoreLiabilities.getOrElse(OnshoreLiabilities())).amountDueTotal

    CheckYourAnswersViewModel(summaryList, taxYearLists, corporationTaxLiabilities, directorLoanAccountLiabilities, totalAmountsList, liabilitiesTotal)
  }

  def taxYearWithLiabilitiesToSummaryList(i: Int, yearWithLiabilites: OnshoreTaxYearWithLiabilities, userAnswers: UserAnswers)(implicit messages: Messages): SummaryList = {

    val liabilities = yearWithLiabilites.taxYearLiabilities

    val nonBusinessIncome = userAnswers.get(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage) match {
      case Some(value) if(value.contains(WhatOnshoreLiabilitiesDoYouNeedToDisclose.NonBusinessIncome)) => Seq(poundRow(i, "onshoreTaxYearLiabilities.nonBusinessIncome.checkYourAnswersLabel", s"${liabilities.nonBusinessIncome.getOrElse(0)}", "onshoreTaxYearLiabilities.nonBusinessIncome.hidden"))
      case _ => Nil
    }

    val businessIncome = userAnswers.get(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage) match {
      case Some(value) if(value.contains(WhatOnshoreLiabilitiesDoYouNeedToDisclose.BusinessIncome)) => Seq(poundRow(i, "onshoreTaxYearLiabilities.businessIncome.checkYourAnswersLabel", s"${liabilities.businessIncome.getOrElse(0)}", "onshoreTaxYearLiabilities.businessIncome.hidden"))
      case _ => Nil
    }

    val lettingIncome = userAnswers.get(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage) match {
      case Some(value) if(value.contains(WhatOnshoreLiabilitiesDoYouNeedToDisclose.LettingIncome)) => Seq(poundRow(i, "onshoreTaxYearLiabilities.lettingIncome.checkYourAnswersLabel", s"${liabilities.lettingIncome.getOrElse(0)}", "onshoreTaxYearLiabilities.lettingIncome.hidden"))
      case _ => Nil
    }

    val gains = userAnswers.get(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage) match {
      case Some(value) if(value.contains(WhatOnshoreLiabilitiesDoYouNeedToDisclose.Gains)) => Seq(poundRow(i, "onshoreTaxYearLiabilities.gains.checkYourAnswersLabel", s"${liabilities.gains.getOrElse(0)}", "onshoreTaxYearLiabilities.gains.hidden"))
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
        poundRow(i, "onshoreTaxYearLiabilities.unpaidTax.checkYourAnswersLabel", s"${liabilities.unpaidTax}", "onshoreTaxYearLiabilities.unpaidTax.hidden"),
        poundRow(i, "onshoreTaxYearLiabilities.niContributions.checkYourAnswersLabel", s"${liabilities.niContributions}", "onshoreTaxYearLiabilities.niContributions.hidden"),
        poundRow(i, "onshoreTaxYearLiabilities.interest.checkYourAnswersLabel", s"${liabilities.interest}", "onshoreTaxYearLiabilities.interest.hidden"),
        row(i, "onshoreTaxYearLiabilities.penaltyRate.checkYourAnswersLabel", s"${liabilities.penaltyRate}%", "onshoreTaxYearLiabilities.penaltyRate.hidden"),
        totalRow("onshoreTaxYearLiabilities.penaltyAmount.checkYourAnswersLabel", messages("site.2DP", penaltyAmount(liabilities))),
        totalRow("onshoreTaxYearLiabilities.amountDue.checkYourAnswersLabel", messages("site.2DP", yearTotal(liabilities))),
        row(i, "onshoreTaxYearLiabilities.penaltyRateReason.checkYourAnswersLabel", s"${liabilities.penaltyRateReason}", "onshoreTaxYearLiabilities.penaltyRateReason.hidden")
      ) ++ residentialTaxReduction ++ ResidentialReductionSummary.row(i, yearWithLiabilites.taxYear.toString, userAnswers)
      
    SummaryListViewModel(rows)
  }

  def totalAmountsSummaryList(disclosure: FullDisclosure)(implicit messages: Messages): SummaryList = {
    
    val totals = TotalAmounts.getOnshoreTotals(disclosure.onshoreLiabilities.getOrElse(OnshoreLiabilities()))

    SummaryListViewModel(
      rows = Seq(
        totalRow("onshoreTaxYearLiabilities.unpaidTax.total", s"${totals.unpaidTaxTotal}"),
        totalRow("onshoreTaxYearLiabilities.niContributions.total", s"${totals.niContributionsTotal}"),
        totalRow("onshoreTaxYearLiabilities.interest.total", s"${totals.interestTotal}"),
        totalRow("onshoreTaxYearLiabilities.penaltyAmount.total", messages("site.2DP", totals.penaltyAmountTotal)),
        totalRow("onshoreTaxYearLiabilities.amountDue.total", messages("site.2DP", totals.amountDueTotal))
      )
    )
  }

  def penaltyAmount(taxYearLiabilities: OnshoreTaxYearLiabilities): BigDecimal = {
    val unpaidAmount = BigDecimal(taxYearLiabilities.unpaidTax) + BigDecimal(taxYearLiabilities.niContributions)
    (BigDecimal(taxYearLiabilities.penaltyRate) * unpaidAmount) /100
  }
  
  def yearTotal(taxYearLiabilities: OnshoreTaxYearLiabilities): BigDecimal = {
    val unpaidAmount = BigDecimal(taxYearLiabilities.unpaidTax) + BigDecimal(taxYearLiabilities.niContributions)
    unpaidAmount + penaltyAmount(taxYearLiabilities) + BigDecimal(taxYearLiabilities.interest)
  }

}
