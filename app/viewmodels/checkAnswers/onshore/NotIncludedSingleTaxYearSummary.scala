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

package viewmodels.checkAnswers

import controllers.onshore.routes
import models.{CheckMode, OnshoreYearStarting, UserAnswers}
import pages.NotIncludedSingleTaxYearPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import viewmodels.RevealFullText

object NotIncludedSingleTaxYearSummary {

  def row(answers: UserAnswers, revealFullText: RevealFullText)(implicit messages: Messages): Option[SummaryListRow] =
    for {
      years       <- answers.inverselySortedOnshoreTaxYears
      notIncluded <- answers.get(NotIncludedSingleTaxYearPage)
    } yield {
      val yearsNotIncluded = OnshoreYearStarting.findMissingYears(years.toList).map(_.startYear + 1).mkString(", ")
      SummaryListRowViewModel(
        key = messages("youHaveNotIncludedTheTaxYear.checkYourAnswersLabel", yearsNotIncluded),
        value = ValueViewModel(revealFullText.addRevealToText(notIncluded, "youHaveNotIncludedTheTaxYear.reveal")),
        actions = Seq(
          ActionItemViewModel("site.change", routes.NotIncludedSingleTaxYearController.onPageLoad(CheckMode).url)
            .withVisuallyHiddenText(messages("youHaveNotIncludedTheTaxYear.change.hidden"))
        )
      )
    }
}
