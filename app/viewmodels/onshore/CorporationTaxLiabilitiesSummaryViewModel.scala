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
import viewmodels.RowHelper

import java.time.format.DateTimeFormatter

case class CorporationTaxLiabilitiesSummaryViewModel (
  corporationTaxLiabilitiesList: Seq[(Int, SummaryList)],
  accountEndingsSummaryList: SummaryList,
  totalAmountsList: SummaryList
)

object CorporationTaxLiabilitiesSummaryViewModelCreation extends RowHelper {
  val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

  def create(userAnswers: UserAnswers)(implicit messages: Messages): CorporationTaxLiabilitiesSummaryViewModel = {
    val corporationTaxLiabilities = userAnswers.get(CorporationTaxLiabilityPage).getOrElse(Seq())

    val corporationTaxLiabilitiesList: Seq[(Int, SummaryList)] = corporationTaxLiabilities.zipWithIndex.map {
      case (corporationTaxLiability, i) => (i + 1, corporationTaxLiabilityToSummaryList(i, corporationTaxLiability))
    }

    val accountEndings = accountEndingsSummaryList(corporationTaxLiabilities)

    val totalAmountsList = totalAmountsSummaryList(corporationTaxLiabilities)

    CorporationTaxLiabilitiesSummaryViewModel(corporationTaxLiabilitiesList, accountEndings, totalAmountsList)
  }

   def corporationTaxLiabilityToSummaryList(i: Int, liability: CorporationTaxLiability)(implicit messages: Messages):SummaryList = {

    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

    val amountDueTotal = BigDecimal(liability.howMuchUnpaid) + BigDecimal(liability.howMuchInterest) + penaltyAmount(liability)

    SummaryListViewModel(
      rows = Seq(
        rowCase(i, "corporationTaxLiability.periodEnd.checkYourAnswersLabel", s"${liability.periodEnd.format(dateFormatter)}", "corporationTaxLiability.periodEnd.hidden", CT),
        poundRowCase(i, "corporationTaxLiability.howMuchIncome.checkYourAnswersLabel", s"${liability.howMuchIncome}", "corporationTaxLiability.howMuchIncome.hidden", CT),
        poundRowCase(i, "corporationTaxLiability.howMuchUnpaid.checkYourAnswersLabel", s"${liability.howMuchUnpaid}", "corporationTaxLiability.howMuchUnpaid.hidden", CT),
        poundRowCase(i, "corporationTaxLiability.howMuchInterest.checkYourAnswersLabel", s"${liability.howMuchInterest}", "corporationTaxLiability.howMuchInterest.hidden", CT),
        rowCase(i, "corporationTaxLiability.penaltyRate.checkYourAnswersLabel", s"${liability.penaltyRate}%", "corporationTaxLiability.penaltyRate.hidden", CT),
        totalRow("corporationTaxLiability.penaltyAmount.checkYourAnswersLabel", messages("site.2DP", penaltyAmount(liability))),
        rowCase(i, "corporationTaxLiability.penaltyRateReason.checkYourAnswersLabel", s"${liability.penaltyRateReason}", "corporationTaxLiability.penaltyRateReason.hidden", CT),
        totalRow("corporationTaxLiability.ct.total.heading", messages("site.2DP", amountDueTotal))
      )
    )
  }

  private def accountEndingsSummaryList(corporationTaxLiabilities: Seq[CorporationTaxLiability])(implicit messages: Messages): SummaryList = {

    val periodEnding = corporationTaxLiabilities.map(ct => ct.periodEnd.format(dateFormatter)).mkString(", ")

    SummaryListViewModel( rows = Seq(
      SummaryListRowViewModel(
        key = "checkYourAnswers.onshore.ct.subheading",
        value = ValueViewModel(HtmlContent(periodEnding)),
        actions = Seq(
          ActionItemViewModel("site.change", controllers.onshore.routes.AccountingPeriodCTAddedController.onPageLoad(CheckMode).url)
            .withVisuallyHiddenText(messages("checkYourAnswers.onshore.ct.hidden"))
        )
    )))
  }

  private def totalAmountsSummaryList(corporationTaxLiabilities: Seq[CorporationTaxLiability])(implicit messages: Messages): SummaryList = {

    val unpaidTaxTotal = corporationTaxLiabilities.map(_.howMuchUnpaid).sum
    val interestTotal = corporationTaxLiabilities.map(_.howMuchInterest).sum
    val penaltyAmountTotal = corporationTaxLiabilities.map(penaltyAmount).sum
    val amountDueTotal = BigDecimal(unpaidTaxTotal) + BigDecimal(interestTotal) + penaltyAmountTotal

    SummaryListViewModel(
     rows = Seq(
       totalRow("checkYourAnswers.ct.total.taxDue", s"${unpaidTaxTotal}"),
       totalRow("checkYourAnswers.ct.total.interestDue", s"${interestTotal}"),
       totalRow("checkYourAnswers.ct.total.penaltyAmount", messages("site.2DP", penaltyAmountTotal)),
       totalRow("checkYourAnswers.ct.total.totalAmountDue", messages("site.2DP", amountDueTotal))
     )
    )
  }

  def penaltyAmount(corporationTaxLiability: CorporationTaxLiability): BigDecimal = {
    (corporationTaxLiability.penaltyRate * BigDecimal(corporationTaxLiability.howMuchUnpaid)) / 100
  }

}

