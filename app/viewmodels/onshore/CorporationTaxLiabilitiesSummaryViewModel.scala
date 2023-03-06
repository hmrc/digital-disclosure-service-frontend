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

import models.{CheckMode, CorporationTaxLiability, UserAnswers}
import pages.CorporationTaxLiabilityPage
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent

import java.time.format.DateTimeFormatter

case class CorporationTaxLiabilitiesSummaryViewModel (
  corporationTaxLiabilitiesList: Seq[(Int, SummaryList)],
  totalAmountsList: SummaryList
)

object CorporationTaxLiabilitiesSummaryViewModelCreation {
  def create(userAnswers: UserAnswers)(implicit messages: Messages): CorporationTaxLiabilitiesSummaryViewModel = {
    val corporationTaxLiabilities = userAnswers.get(CorporationTaxLiabilityPage).getOrElse(Set())

    val corporationTaxLiabilitiesList: Seq[(Int, SummaryList)] = (corporationTaxLiabilities.zipWithIndex.map {
      case (corporationTaxLiability, i) => (i+1, corporationTaxLiabilityToSummaryList(i, corporationTaxLiability))
    }).toSeq

    val totalAmountsList = totalAmountsSummaryList(corporationTaxLiabilities)

    CorporationTaxLiabilitiesSummaryViewModel(corporationTaxLiabilitiesList, totalAmountsList)
  }

  private def corporationTaxLiabilityToSummaryList(i: Int, liability: CorporationTaxLiability)(implicit messages: Messages):SummaryList = {
    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

    SummaryListViewModel(
      rows = Seq(
        row(i, "corporationTaxLiability.periodEnd.checkYourAnswersLabel", s"${liability.periodEnd.format(dateFormatter)}", "corporationTaxLiability.periodEnd.hidden"),
        row(i, "corporationTaxLiability.howMuchIncome.checkYourAnswersLabel", s"${liability.howMuchIncome}", "corporationTaxLiability.howMuchIncome.hidden"),
        row(i, "corporationTaxLiability.howMuchUnpaid.checkYourAnswersLabel", s"${liability.howMuchUnpaid}", "corporationTaxLiability.howMuchUnpaid.hidden"),
        row(i, "corporationTaxLiability.howMuchInterest.checkYourAnswersLabel", s"${liability.howMuchInterest}", "corporationTaxLiability.howMuchInterest.hidden"),
        row(i, "corporationTaxLiability.penaltyRate.checkYourAnswersLabel", s"${liability.penaltyRate}", "corporationTaxLiability.penaltyRate.hidden"),
        row(i, "corporationTaxLiability.penaltyAmount.checkYourAnswersLabel", s"${penaltyAmount(liability)}", "corporationTaxLiability.penaltyAmount.hidden"),
        row(i, "corporationTaxLiability.penaltyRateReason.checkYourAnswersLabel", s"${liability.penaltyRateReason}", "corporationTaxLiability.penaltyRateReason.hidden"),
      )
    )
  }

  private def row(i: Int, label: String, value: String, hiddenLabel: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(HtmlContent(value)),
      actions = Seq(
        ActionItemViewModel("site.change", controllers.onshore.routes.CorporationTaxLiabilityController.onPageLoad(i, CheckMode).url)
          .withVisuallyHiddenText(messages(hiddenLabel))
      )
    )
  }

  private def totalAmountsSummaryList(corporationTaxLiabilities: Set[CorporationTaxLiability])(implicit messages: Messages): SummaryList = {

    val unpaidTaxTotal = corporationTaxLiabilities.map(_.howMuchUnpaid).sum
    val interestTotal = corporationTaxLiabilities.map(_.howMuchInterest).sum
    val penaltyAmountTotal = corporationTaxLiabilities.map(penaltyAmount).sum
    val amountDueTotal = BigDecimal(unpaidTaxTotal) + BigDecimal(interestTotal) + penaltyAmountTotal

    SummaryListViewModel(
     rows = Seq(
       totalRow("checkYourAnswers.ct.total.taxDue", s"&pound;${unpaidTaxTotal}"),
       totalRow("checkYourAnswers.ct.total.interestDue", s"&pound;${interestTotal}"),
       totalRow("checkYourAnswers.ct.total.penaltyAmount", messages("site.2DP", penaltyAmountTotal)),
       totalRow("checkYourAnswers.ct.total.totalAmountDue", messages("site.2DP", amountDueTotal))
     )
    )
  }

  private def penaltyAmount(corporationTaxLiability: CorporationTaxLiability): BigDecimal = {
    (BigDecimal(corporationTaxLiability.penaltyRate) * BigDecimal(corporationTaxLiability.howMuchUnpaid)) / 100
  }

  private def totalRow(label: String, value: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(HtmlContent(value)),
      actions = Nil
    )
  }
}

