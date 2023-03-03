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
import pages.DirectorLoanAccountLiabilitiesPage
import play.api.i18n.Messages

case class DirectorLoanAccountLiabilitiesSummaryViewModel (
  directorLoanAccountLiabilitiesList: Seq[(Int, SummaryList)]
)

class DirectorLoanAccountLiabilitiesSummaryViewModelCreation {

  def create(userAnswers: UserAnswers)(implicit messages: Messages): DirectorLoanAccountLiabilitiesSummaryViewModel = {

    val directorLoanAccountLiabilities = userAnswers.get(DirectorLoanAccountLiabilitiesPage).getOrElse(Set())

    val directorLoanAccountLiabilitiesList: Seq[(Int, SummaryList)] = directorLoanAccountLiabilities.zipWithIndex.map { 
      case (directorLoanAccountLiability, i) => (i+1, directorLoanAccountLiabilitiesToSummaryList(i, directorLoanAccountLiability))
    }

    DirectorLoanAccountLiabilitiesSummaryViewModel(directorLoanAccountLiabilitiesList)
  }

  def directorLoanAccountLiabilitiesToSummaryList(i: Int, directorLoanAccountLiability: DirectorLoanAccountLiabilities)(implicit messages: Messages): SummaryList = {
    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
    val rows = Seq(
      row(i, "directorLoanAccountLiability.name.checkYourAnswersLabel", s"${directorLoanAccountLiability.name}", "directorLoanAccountLiability.name.hidden"),
      row(i, "directorLoanAccountLiability.periodEnd.checkYourAnswersLabel", s"${directorLoanAccountLiability.periodEnd.format(dateFormatter)}", "directorLoanAccountLiability.periodEnd.hidden"),
      row(i, "directorLoanAccountLiability.overdrawn.checkYourAnswersLabel", s"&pound;${directorLoanAccountLiability.overdrawn}", "directorLoanAccountLiability.overdrawn.hidden"),
      row(i, "directorLoanAccountLiability.unpaidTax.checkYourAnswersLabel", s"&pound;${directorLoanAccountLiability.unpaidTax}", "directorLoanAccountLiability.unpaidTax.hidden"),
      row(i, "directorLoanAccountLiability.interest.checkYourAnswersLabel", s"${directorLoanAccountLiability.interest}%", "directorLoanAccountLiability.interest.hidden"),
      row(i, "directorLoanAccountLiability.penaltyRate.checkYourAnswersLabel", s"&pound;${directorLoanAccountLiability.penaltyRate}", "directorLoanAccountLiability.penaltyRate.hidden"),
      row(i, "directorLoanAccountLiability.penaltyRateReason.checkYourAnswersLabel", s"${directorLoanAccountLiability.penaltyRateReason}", "directorLoanAccountLiability.penaltyRateReason.hidden")
    )

    SummaryListViewModel(rows)
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
}
