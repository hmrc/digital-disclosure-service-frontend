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
import java.util.Locale
import viewmodels.{DL, RevealFullText, RowHelper}

import scala.math.BigDecimal.RoundingMode
import com.google.inject.Inject

case class DirectorLoanAccountLiabilitiesSummaryViewModel(
  directorLoanAccountLiabilitiesList: Seq[(Int, SummaryList)],
  accountEndingsSummaryList: SummaryList,
  totalAmountsList: SummaryList
)

class DirectorLoanAccountLiabilitiesSummaryViewModelCreation @Inject() (revealFullText: RevealFullText)
    extends RowHelper {

  val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

  def create(userAnswers: UserAnswers)(implicit messages: Messages): DirectorLoanAccountLiabilitiesSummaryViewModel = {

    val directorLoanAccountLiabilities: Seq[DirectorLoanAccountLiabilities] =
      userAnswers.get(DirectorLoanAccountLiabilitiesPage).getOrElse(Seq())

    val directorLoanAccountLiabilitiesList: Seq[(Int, SummaryList)] = directorLoanAccountLiabilities.zipWithIndex.map {
      case (dLLiability, i) => (i + 1, directorLoanAccountLiabilitiesToSummaryList(i, dLLiability, revealFullText))
    }

    val totalAmountsList = totalAmountsSummaryList(directorLoanAccountLiabilities)

    val accountEndingsSummary = accountEndingsSummaryList(directorLoanAccountLiabilities)

    DirectorLoanAccountLiabilitiesSummaryViewModel(
      directorLoanAccountLiabilitiesList,
      accountEndingsSummary,
      totalAmountsList
    )
  }

  def directorLoanAccountLiabilitiesToSummaryList(
    i: Int,
    dLLiability: DirectorLoanAccountLiabilities,
    revealFullText: RevealFullText
  )(implicit messages: Messages): SummaryList = {
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale(messages.lang.code))

    val amountDueTotal =
      BigDecimal(dLLiability.unpaidTax) + BigDecimal(dLLiability.interest) + penaltyAmount(dLLiability)

    SummaryListViewModel(
      rows = Seq(
        rowCase(
          i,
          "directorLoanAccountLiabilities.name.checkYourAnswersLabel",
          s"${dLLiability.name}",
          "directorLoanAccountLiabilities.name.hidden",
          DL,
          revealFullText,
          false
        ),
        rowCase(
          i,
          "directorLoanAccountLiabilities.periodEnd.checkYourAnswersLabel",
          s"${dLLiability.periodEnd.format(dateFormatter)}",
          "directorLoanAccountLiabilities.periodEnd.hidden",
          DL,
          revealFullText,
          false
        ),
        poundRowCase(
          i,
          "directorLoanAccountLiabilities.overdrawn.checkYourAnswersLabel",
          s"${dLLiability.overdrawn}",
          "directorLoanAccountLiabilities.overdrawn.hidden",
          DL
        ),
        poundRowCase(
          i,
          "directorLoanAccountLiabilities.unpaidTax.checkYourAnswersLabel",
          s"${dLLiability.unpaidTax}",
          "directorLoanAccountLiabilities.unpaidTax.hidden",
          DL
        ),
        poundRowCase(
          i,
          "directorLoanAccountLiabilities.interest.checkYourAnswersLabel",
          s"${dLLiability.interest}",
          "directorLoanAccountLiabilities.interest.hidden",
          DL
        ),
        rowCase(
          i,
          "directorLoanAccountLiabilities.penaltyRate.checkYourAnswersLabel",
          messages("site.2DP", dLLiability.penaltyRate) + "%",
          "directorLoanAccountLiabilities.penaltyRate.hidden",
          DL,
          revealFullText,
          false
        ),
        totalRow("checkYourAnswers.dl.total.penaltyAmount", messages("site.2DP", penaltyAmount(dLLiability))),
        rowCase(
          i,
          "directorLoanAccountLiabilities.penaltyRateReason",
          s"${dLLiability.penaltyRateReason}",
          "directorLoanAccountLiabilities.penaltyRateReason.hidden",
          DL,
          revealFullText,
          true
        ),
        totalRow("checkYourAnswers.dl.total.heading", messages("site.2DP", amountDueTotal))
      )
    )
  }

  private def accountEndingsSummaryList(
    directorLoans: Seq[DirectorLoanAccountLiabilities]
  )(implicit messages: Messages): SummaryList = {

    val periodEnding = directorLoans.map(ct => ct.periodEnd.format(dateFormatter)).mkString(", ")

    SummaryListViewModel(rows =
      Seq(
        SummaryListRowViewModel(
          key = "checkYourAnswers.onshore.dl.subheading",
          value = ValueViewModel(HtmlContent(periodEnding)),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              controllers.onshore.routes.AccountingPeriodDLAddedController.onPageLoad(CheckMode).url
            )
              .withVisuallyHiddenText(messages("checkYourAnswers.onshore.dl.hidden"))
          )
        )
      )
    )
  }

  def totalAmountsSummaryList(
    directorLoanAccountLiabilities: Seq[DirectorLoanAccountLiabilities]
  )(implicit messages: Messages): SummaryList = {
    val unpaidTaxTotal     = directorLoanAccountLiabilities.map(_.unpaidTax).sum
    val interestTotal      = directorLoanAccountLiabilities.map(_.interest).sum
    val penaltyAmountTotal = directorLoanAccountLiabilities.map(penaltyAmount).sum
    val amountDueTotal     = BigDecimal(unpaidTaxTotal) + BigDecimal(interestTotal) + penaltyAmountTotal

    SummaryListViewModel(
      rows = Seq(
        totalRow("checkYourAnswers.dl.total.taxDue", s"$unpaidTaxTotal"),
        totalRow("checkYourAnswers.dl.total.interestDue", s"$interestTotal"),
        totalRow("checkYourAnswers.dl.total.penaltyAmount", messages("site.2DP", penaltyAmountTotal)),
        totalRow("checkYourAnswers.dl.total.totalAmountDue", messages("site.2DP", amountDueTotal))
      )
    )
  }

  def penaltyAmount(directorLoanAccountLiabilities: DirectorLoanAccountLiabilities): BigDecimal =
    ((directorLoanAccountLiabilities.penaltyRate * BigDecimal(directorLoanAccountLiabilities.unpaidTax)) / 100)
      .setScale(2, RoundingMode.DOWN)

}
