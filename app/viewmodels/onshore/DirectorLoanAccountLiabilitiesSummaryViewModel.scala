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
      case (directorLoanAccountLiability, i) => (i+1, directorLoanAccountLiabilitiesToSummaryList(i, directorLoanAccountLiability))
    }).toSeq

    val totalAmountsList = totalAmountsSummaryList(directorLoanAccountLiabilities)

    DirectorLoanAccountLiabilitiesSummaryViewModel(directorLoanAccountLiabilitiesList, totalAmountsList)
  }

  def directorLoanAccountLiabilitiesToSummaryList(i: Int, directorLoanAccountLiability: DirectorLoanAccountLiabilities)(implicit messages: Messages): SummaryList = {
    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
    SummaryListViewModel(
      rows = Seq(
        row(i, "directorLoanAccountLiabilities.name.checkYourAnswersLabel", s"${directorLoanAccountLiability.name}", "directorLoanAccountLiability.name.hidden"),
        row(i, "directorLoanAccountLiabilities.periodEnd.checkYourAnswersLabel", s"${directorLoanAccountLiability.periodEnd.format(dateFormatter)}", "directorLoanAccountLiability.periodEnd.hidden"),
        row(i, "directorLoanAccountLiabilities.overdrawn.checkYourAnswersLabel", s"&pound;${directorLoanAccountLiability.overdrawn}", "directorLoanAccountLiability.overdrawn.hidden"),
        row(i, "directorLoanAccountLiabilities.unpaidTax.checkYourAnswersLabel", s"&pound;${directorLoanAccountLiability.unpaidTax}", "directorLoanAccountLiability.unpaidTax.hidden"),
        row(i, "directorLoanAccountLiabilities.interest.checkYourAnswersLabel", s"&pound;${directorLoanAccountLiability.interest}", "directorLoanAccountLiability.interest.hidden"),
        row(i, "directorLoanAccountLiabilities.penaltyRate.checkYourAnswersLabel", s"&pound;${directorLoanAccountLiability.penaltyRate}", "directorLoanAccountLiability.penaltyRate.hidden"),
        row(i, "directorLoanAccountLiabilities.penaltyRateReason.checkYourAnswersLabel", s"${directorLoanAccountLiability.penaltyRateReason}", "directorLoanAccountLiability.penaltyRateReason.hidden"),
        row(i, "checkYourAnswers.dl.total.heading", s"${directorLoanAccountLiability.penaltyRateReason}", "directorLoanAccountLiability.penaltyRateReason.hidden")
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

  def totalAmountsSummaryList(directorLoanAccountLiabilities: Set[DirectorLoanAccountLiabilities])(implicit messages: Messages): SummaryList = {
    val unpaidTaxTotal = directorLoanAccountLiabilities.map(_.unpaidTax).sum
    val interestTotal = directorLoanAccountLiabilities.map(_.interest).sum
    val penaltyRateTotal = directorLoanAccountLiabilities.map(_.penaltyRate).sum
    val penaltyAmountTotal = directorLoanAccountLiabilities.map(penaltyAmount).sum
    val amountDueTotal = unpaidTaxTotal + interestTotal + penaltyRateTotal + penaltyAmountTotal

    SummaryListViewModel(
      rows = Seq(
        totalRow("checkYourAnswers.dl.total.taxDue", s"&pound;${unpaidTaxTotal}"),
        totalRow("checkYourAnswers.dl.total.interestDue", s"&pound;${interestTotal}"),
        totalRow("checkYourAnswers.dl.total.penaltyRate", s"&pound;${penaltyRateTotal}"),
        totalRow("checkYourAnswers.dl.total.penaltyAmount", s"&pound;${penaltyAmountTotal}"),
        totalRow("checkYourAnswers.dl.total.totalAmountDue", s"&pound;${amountDueTotal}")
      )
    )
  }

  def penaltyAmount(directorLoanAccountLiabilities: DirectorLoanAccountLiabilities): BigInt = {
    (BigInt(directorLoanAccountLiabilities.penaltyRate) * directorLoanAccountLiabilities.unpaidTax) /100
  }

  def totalRow(label: String, value: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(HtmlContent(value)),
      actions = Nil
    )
  }
}
