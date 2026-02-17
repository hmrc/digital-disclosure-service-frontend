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
import viewmodels.{CT, RevealFullText, RowHelper}

import scala.math.BigDecimal.RoundingMode
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.google.inject.Inject
import pages.onshore.WhyDidYouNotFileAReturnOnTimeOnshorePage

case class CorporationTaxLiabilitiesSummaryViewModel(
                                                      corporationTaxLiabilitiesList: Seq[(Int, SummaryList)],
                                                      accountEndingsSummaryList: SummaryList,
                                                      totalAmountsList: SummaryList
                                                    )

class CorporationTaxLiabilitiesSummaryViewModelCreation @Inject() (revealFullText: RevealFullText) extends RowHelper {
  val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

  def create(userAnswers: UserAnswers)(implicit messages: Messages): CorporationTaxLiabilitiesSummaryViewModel = {
    val corporationTaxLiabilities = userAnswers.get(CorporationTaxLiabilityPage).getOrElse(Seq())

    val showPenaltyQuestions = shouldShowPenaltyQuestions(userAnswers)

    val corporationTaxLiabilitiesList: Seq[(Int, SummaryList)] = corporationTaxLiabilities.zipWithIndex.map {
      case (corporationTaxLiability, i) =>
        (i + 1, corporationTaxLiabilityToSummaryList(i, corporationTaxLiability, revealFullText, showPenaltyQuestions))
    }

    val accountEndings = accountEndingsSummaryList(corporationTaxLiabilities)

    val totalAmountsList = totalAmountsSummaryList(corporationTaxLiabilities, showPenaltyQuestions)

    CorporationTaxLiabilitiesSummaryViewModel(corporationTaxLiabilitiesList, accountEndings, totalAmountsList)
  }

  def corporationTaxLiabilityToSummaryList(
                                            i: Int,
                                            liability: CorporationTaxLiability,
                                            revealFullText: RevealFullText,
                                            showPenaltyQuestions: Boolean
                                          )(implicit messages: Messages): SummaryList = {

    val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale(messages.lang.code))

    val amountDueTotal =
      BigDecimal(liability.howMuchUnpaid) + BigDecimal(liability.howMuchInterest) +
        (if (showPenaltyQuestions) penaltyAmount(liability) else BigDecimal(0))

    val baseRows = Seq(
      rowCase(
        i,
        "corporationTaxLiability.periodEnd.checkYourAnswersLabel",
        s"${liability.periodEnd.format(dateFormatter)}",
        "corporationTaxLiability.periodEnd.hidden",
        CT,
        revealFullText,
        false
      ),
      poundRowCase(
        i,
        "corporationTaxLiability.howMuchIncome.checkYourAnswersLabel",
        s"${liability.howMuchIncome}",
        "corporationTaxLiability.howMuchIncome.hidden",
        CT
      ),
      poundRowCase(
        i,
        "corporationTaxLiability.howMuchUnpaid.checkYourAnswersLabel",
        s"${liability.howMuchUnpaid}",
        "corporationTaxLiability.howMuchUnpaid.hidden",
        CT
      ),
      poundRowCase(
        i,
        "corporationTaxLiability.howMuchInterest.checkYourAnswersLabel",
        s"${liability.howMuchInterest}",
        "corporationTaxLiability.howMuchInterest.hidden",
        CT
      )
    )

    val penaltyRows = if (showPenaltyQuestions) {
      Seq(
        rowCase(
          i,
          "corporationTaxLiability.penaltyRate.checkYourAnswersLabel",
          messages("site.2DP", liability.penaltyRate) + "%",
          "corporationTaxLiability.penaltyRate.hidden",
          CT,
          revealFullText,
          false
        ),
        totalRow(
          "corporationTaxLiability.penaltyAmount.checkYourAnswersLabel",
          messages("site.2DP", penaltyAmount(liability))
        ),
        rowCase(
          i,
          "corporationTaxLiability.penaltyRateReason",
          s"${liability.penaltyRateReason}",
          "corporationTaxLiability.penaltyRateReason.hidden",
          CT,
          revealFullText,
          true
        )
      )
    } else {
      Seq.empty
    }

    val totalRowSeq = Seq(
      totalRow("corporationTaxLiability.ct.total.heading", messages("site.2DP", amountDueTotal))
    )

    SummaryListViewModel(rows = baseRows ++ penaltyRows ++ totalRowSeq)
  }

  private def accountEndingsSummaryList(
                                         corporationTaxLiabilities: Seq[CorporationTaxLiability]
                                       )(implicit messages: Messages): SummaryList = {

    val periodEnding = corporationTaxLiabilities.map(ct => ct.periodEnd.format(dateFormatter)).mkString(", ")

    SummaryListViewModel(rows =
      Seq(
        SummaryListRowViewModel(
          key = "checkYourAnswers.onshore.ct.subheading",
          value = ValueViewModel(HtmlContent(periodEnding)),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              controllers.onshore.routes.AccountingPeriodCTAddedController.onPageLoad(CheckMode).url
            )
              .withVisuallyHiddenText(messages("checkYourAnswers.onshore.ct.hidden"))
          )
        )
      )
    )
  }

  private def totalAmountsSummaryList(
                                       corporationTaxLiabilities: Seq[CorporationTaxLiability],
                                       showPenaltyQuestions: Boolean
                                     )(implicit messages: Messages): SummaryList = {

    val unpaidTaxTotal     = corporationTaxLiabilities.map(_.howMuchUnpaid).sum
    val interestTotal      = corporationTaxLiabilities.map(_.howMuchInterest).sum
    val penaltyAmountTotal = if (showPenaltyQuestions) corporationTaxLiabilities.map(penaltyAmount).sum else BigDecimal(0)
    val amountDueTotal     = BigDecimal(unpaidTaxTotal) + BigDecimal(interestTotal) + penaltyAmountTotal

    val baseRows = Seq(
      totalRow("checkYourAnswers.ct.total.taxDue", s"$unpaidTaxTotal"),
      totalRow("checkYourAnswers.ct.total.interestDue", s"$interestTotal")
    )

    val penaltyRow = if (showPenaltyQuestions) {
      Seq(totalRow("checkYourAnswers.ct.total.penaltyAmount", messages("site.2DP", penaltyAmountTotal)))
    } else {
      Seq.empty
    }

    val totalRowSeq = Seq(
      totalRow("checkYourAnswers.ct.total.totalAmountDue", messages("site.2DP", amountDueTotal))
    )

    SummaryListViewModel(rows = baseRows ++ penaltyRow ++ totalRowSeq)
  }

  private def shouldShowPenaltyQuestions(userAnswers: UserAnswers): Boolean = {
    import models.WhyDidYouNotNotifyOnshore._
    import models.WhyDidYouNotFileAReturnOnTimeOnshore._
    import models.WhyYouSubmittedAnInaccurateOnshoreReturn._
    import pages._

    val page2aSelections = userAnswers.get(WhyDidYouNotNotifyOnshorePage).getOrElse(Set.empty)
    val page2bSelections = userAnswers.get(WhyDidYouNotFileAReturnOnTimeOnshorePage).getOrElse(Set.empty)
    val page2cSelections = userAnswers.get(WhyYouSubmittedAnInaccurateOnshoreReturnPage).getOrElse(Set.empty)

    val allSelections: Set[Any] = page2aSelections ++ page2bSelections ++ page2cSelections

    val reasonableOnly: Set[Any] = Set(
      ReasonableExcuseOnshore,
      ReasonableExcuse,
      ReasonableMistake
    )

    !(allSelections.nonEmpty && allSelections.subsetOf(reasonableOnly))
  }

  def penaltyAmount(corporationTaxLiability: CorporationTaxLiability): BigDecimal =
    ((corporationTaxLiability.penaltyRate * BigDecimal(corporationTaxLiability.howMuchUnpaid)) / 100)
      .setScale(2, RoundingMode.DOWN)
}