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
import pages.TaxYearLiabilitiesPage
import play.api.i18n.Messages

case class CheckYourAnswersViewModel(
  list: SummaryList,
  taxYearLists: Seq[(Int, SummaryList)],
  liabilitiesTotal: BigDecimal
)

object CheckYourAnswersViewModel {

  def apply(userAnswers: UserAnswers)(implicit messages: Messages): CheckYourAnswersViewModel = {

    val taxYears: Seq[TaxYearWithLiabilities] = for {
      year <- userAnswers.inverselySortedOffshoreTaxYears.getOrElse(Nil)
      taxYearWithLiabilities <- userAnswers.getByKey(TaxYearLiabilitiesPage, year.toString)
    } yield taxYearWithLiabilities

    val taxYearLists: Seq[(Int, SummaryList)] = taxYears.zipWithIndex.map{ case (yearWithLiabilites, i) => 
      (yearWithLiabilites.taxYear.startYear, taxYearWithLiabilitiesToSummaryList(i, yearWithLiabilites.taxYearLiabilities))
    }

    val liabilitiesTotal: BigDecimal = taxYears.map(yearWithLiabilities => yearTotal(yearWithLiabilities.taxYearLiabilities)).sum

    val list = SummaryListViewModel(
      rows = Seq(
        YourLegalInterpretationSummary.row(userAnswers),
        UnderWhatConsiderationSummary.row(userAnswers),
        HowMuchTaxHasNotBeenIncludedSummary.row(userAnswers),
        TheMaximumValueOfAllAssetsSummary.row(userAnswers),
        TaxBeforeFiveYearsSummary.row(userAnswers),
        TaxBeforeSevenYearsSummary.row(userAnswers)
      ).flatten
    )

    CheckYourAnswersViewModel(list, taxYearLists, liabilitiesTotal)

  }

  def taxYearWithLiabilitiesToSummaryList(i: Int, liabilities: TaxYearLiabilities)(implicit messages: Messages): SummaryList =
    SummaryListViewModel(
      rows = Seq(
        row(i, "taxYearLiabilities.income.checkYourAnswersLabel", s"&pound;${liabilities.income}", "taxYearLiabilities.income.hidden"),
        row(i, "taxYearLiabilities.chargeableTransfers.checkYourAnswersLabel", s"&pound;${liabilities.chargeableTransfers}", "taxYearLiabilities.chargeableTransfers.hidden"),
        row(i, "taxYearLiabilities.capitalGains.checkYourAnswersLabel", s"&pound;${liabilities.capitalGains}", "taxYearLiabilities.capitalGains.hidden"),
        row(i, "taxYearLiabilities.unpaidTax.checkYourAnswersLabel", s"&pound;${liabilities.unpaidTax}", "taxYearLiabilities.unpaidTax.hidden"),
        row(i, "taxYearLiabilities.interest.checkYourAnswersLabel", s"&pound;${liabilities.interest}", "taxYearLiabilities.interest.hidden"),
        row(i, "taxYearLiabilities.penaltyRate.checkYourAnswersLabel", s"${liabilities.penaltyRate}%", "taxYearLiabilities.penaltyRate.hidden"),
        row(i, "taxYearLiabilities.penaltyAmount.checkYourAnswersLabel", s"&pound;${penaltyAmount(liabilities)}", "taxYearLiabilities.penaltyRate.hidden"),
        row(i, "taxYearLiabilities.foreignTaxCredit.checkYourAnswersLabel", if (liabilities.foreignTaxCredit) messages("site.yes") else messages("site.no"), "taxYearLiabilities.foreignTaxCredit.hidden"),
        row(i, "taxYearLiabilities.amountDue.checkYourAnswersLabel", s"&pound;${yearTotal(liabilities)}", "taxYearLiabilities.amountDue.hidden")
      )
    )

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

}
