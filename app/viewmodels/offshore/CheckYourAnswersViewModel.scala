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
import viewmodels.implicits._
import pages.TaxYearLiabilitiesPage
import play.api.i18n.Messages

case class CheckYourAnswersViewModel(
  taxYearLists: Seq[(Int, SummaryList)]
)

object CheckYourAnswersViewModel {

  def apply(userAnswers: UserAnswers)(implicit messages: Messages): CheckYourAnswersViewModel = {

    val taxYearLists: Seq[(Int, SummaryList)] = for {
      (year, i) <- userAnswers.inverselySortedOffshoreTaxYears.getOrElse(Nil).zipWithIndex
      taxYearWithLiabilities <- userAnswers.getByKey(TaxYearLiabilitiesPage, year.toString)
    } yield (year.startYear, taxYearWithLiabilitiesToSummaryList(i, taxYearWithLiabilities))

    CheckYourAnswersViewModel(taxYearLists)

  }



  def taxYearWithLiabilitiesToSummaryList(i: Int, taxYearWithLiabilities: TaxYearWithLiabilities)(implicit messages: Messages): SummaryList = {
    val liabilities = taxYearWithLiabilities.taxYearLiabilities
    SummaryListViewModel(
      rows = Seq(
        row(i, "taxYearLiabilities.income.checkYourAnswersLabel", s"&pound;${liabilities.income}", "taxYearLiabilities.income.hidden"),
        row(i, "taxYearLiabilities.chargeableTransfers.checkYourAnswersLabel", s"&pound;${liabilities.chargeableTransfers}", "taxYearLiabilities.income.hidden"),
        row(i, "taxYearLiabilities.capitalGains.checkYourAnswersLabel", s"&pound;${liabilities.capitalGains}", "taxYearLiabilities.capitalGains.hidden"),
        row(i, "taxYearLiabilities.unpaidTax.checkYourAnswersLabel", s"&pound;${liabilities.unpaidTax}", "taxYearLiabilities.unpaidTax.hidden"),
        row(i, "taxYearLiabilities.interest.checkYourAnswersLabel", s"&pound;${liabilities.interest}", "taxYearLiabilities.interest.hidden"),
        row(i, "taxYearLiabilities.penaltyRate.checkYourAnswersLabel", s"${liabilities.penaltyRate}%", "taxYearLiabilities.penaltyRate.hidden"),
        row(i, "taxYearLiabilities.foreignTaxCredit.checkYourAnswersLabel", if (liabilities.foreignTaxCredit) "site.yes" else "site.no", "taxYearLiabilities.foreignTaxCredit.hidden"),
        row(i, "taxYearLiabilities.amountDue.checkYourAnswersLabel", s"&pound;${yearTotal(liabilities)}", "taxYearLiabilities.income.hidden"),
        row(i, "taxYearLiabilities.penaltyAmount.checkYourAnswersLabel", s"&pound;${penaltyAmount(liabilities)}", "taxYearLiabilities.income.hidden")
      )
    )
  }

  def yearTax(taxYearLiabilities: TaxYearLiabilities): BigDecimal = {
    BigDecimal(taxYearLiabilities.capitalGains) + BigDecimal(taxYearLiabilities.capitalGains)
  }

  def penaltyAmount(taxYearLiabilities: TaxYearLiabilities): BigDecimal = {
    (taxYearLiabilities.penaltyRate/100) * yearTax(taxYearLiabilities)
  }
  
  def yearTotal(taxYearLiabilities: TaxYearLiabilities): BigDecimal = {
    yearTax(taxYearLiabilities) + penaltyAmount(taxYearLiabilities) + BigDecimal(taxYearLiabilities.interest)
  }

  def row(i: Int,label: String, value: String, hiddenLabel: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(value),
      actions = Seq(
        ActionItemViewModel("site.change", controllers.offshore.routes.TaxYearLiabilitiesController.onPageLoad(i, CheckMode).url)
          .withVisuallyHiddenText(messages(hiddenLabel))
      )
    )
  }

}
