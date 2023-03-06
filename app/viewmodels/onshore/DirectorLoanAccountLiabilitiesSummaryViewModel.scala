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
import pages.DirectorLoanAccountLiabilitiesPage
import play.api.i18n.Messages
import java.time.format.DateTimeFormatter

case class DirectorLoanAccountLiabilitiesSummaryViewModel (
  directorLoanAccountLiabilitiesList: Seq[(Int, SummaryList)],
  totalAmountsList: SummaryList
)

class DirectorLoanAccountLiabilitiesSummaryViewModelCreation {

  def create(userAnswers: UserAnswers)(implicit messages: Messages): DirectorLoanAccountLiabilitiesSummaryViewModel = {

    val directorLoanAccountLiabilities = userAnswers.get(DirectorLoanAccountLiabilitiesPage).getOrElse(Set())

    val directorLoanAccountLiabilitiesList: Seq[(Int, SummaryList)] = (directorLoanAccountLiabilities.zipWithIndex.map { 
      case (dLLiability, i) => (i+1, directorLoanAccountLiabilitiesToSummaryList(i, dLLiability))
    }).toSeq

    val totalAmountsList = totalAmountsSummaryList(directorLoanAccountLiabilities.toSeq)

    DirectorLoanAccountLiabilitiesSummaryViewModel(directorLoanAccountLiabilitiesList, totalAmountsList)
  }

  def directorLoanAccountLiabilitiesToSummaryList(i: Int, dLLiability: DirectorLoanAccountLiabilities)(implicit messages: Messages): SummaryList = {
    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

    val amountDueTotal = BigDecimal(dLLiability.unpaidTax) + BigDecimal(dLLiability.interest) + penaltyAmount(dLLiability)

    SummaryListViewModel(
      rows = Seq(
        row(i, "directorLoanAccountLiabilities.name.checkYourAnswersLabel", s"${dLLiability.name}", "dLLiability.name.hidden"),
        row(i, "directorLoanAccountLiabilities.periodEnd.checkYourAnswersLabel", s"${dLLiability.periodEnd.format(dateFormatter)}", "dLLiability.periodEnd.hidden"),
        row(i, "directorLoanAccountLiabilities.overdrawn.checkYourAnswersLabel", s"&pound;${dLLiability.overdrawn}", "dLLiability.overdrawn.hidden"),
        row(i, "directorLoanAccountLiabilities.unpaidTax.checkYourAnswersLabel", s"&pound;${dLLiability.unpaidTax}", "dLLiability.unpaidTax.hidden"),
        row(i, "directorLoanAccountLiabilities.interest.checkYourAnswersLabel", s"&pound;${dLLiability.interest}", "dLLiability.interest.hidden"),
        row(i, "directorLoanAccountLiabilities.penaltyRate.checkYourAnswersLabel", s"&pound;${dLLiability.penaltyRate}", "dLLiability.penaltyRate.hidden"),
        totalRow("checkYourAnswers.dl.total.penaltyAmount", messages("site.2DP", penaltyAmount(dLLiability))),
        row(i, "directorLoanAccountLiabilities.penaltyRateReason.checkYourAnswersLabel", s"${dLLiability.penaltyRateReason}", "dLLiability.penaltyRateReason.hidden"),
        totalRow("checkYourAnswers.dl.total.heading", messages("site.2DP", amountDueTotal))
      )
    )
  }

  def row(i: Int, label: String, value: String, hiddenLabel: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(HtmlContent(value)),
      actions = Seq(
        ActionItemViewModel("site.change", controllers.onshore.routes.DirectorLoanAccountLiabilitiesController.onPageLoad(i, CheckMode).url)
          .withVisuallyHiddenText(messages(hiddenLabel))
      )
    )
  }

  def totalAmountsSummaryList(directorLoanAccountLiabilities: Seq[DirectorLoanAccountLiabilities])(implicit messages: Messages): SummaryList = {
    val unpaidTaxTotal = directorLoanAccountLiabilities.map(_.unpaidTax).sum
    val interestTotal = directorLoanAccountLiabilities.map(_.interest).sum
    val penaltyAmountTotal = directorLoanAccountLiabilities.map(penaltyAmount).sum
    val amountDueTotal = BigDecimal(unpaidTaxTotal) + BigDecimal(interestTotal) + penaltyAmountTotal

    SummaryListViewModel(
      rows = Seq(
        totalRow("checkYourAnswers.dl.total.taxDue", s"&pound;${unpaidTaxTotal}"),
        totalRow("checkYourAnswers.dl.total.interestDue", s"&pound;${interestTotal}"),
        totalRow("checkYourAnswers.dl.total.penaltyAmount", messages("site.2DP", penaltyAmountTotal)),
        totalRow("checkYourAnswers.dl.total.totalAmountDue", messages("site.2DP", amountDueTotal))
      )
    )
  }

  def penaltyAmount(directorLoanAccountLiabilities: DirectorLoanAccountLiabilities): BigDecimal = {
    (BigDecimal(directorLoanAccountLiabilities.penaltyRate) * BigDecimal(directorLoanAccountLiabilities.unpaidTax)) / 100
  }

  def totalRow(label: String, value: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(HtmlContent(value)),
      actions = Nil
    )
  }
}
