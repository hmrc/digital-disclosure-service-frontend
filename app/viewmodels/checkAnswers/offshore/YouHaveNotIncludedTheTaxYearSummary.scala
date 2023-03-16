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

import controllers.offshore.routes
import models.{CheckMode, UserAnswers, TaxYearStarting}
import pages.YouHaveNotIncludedTheTaxYearPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object YouHaveNotIncludedTheTaxYearSummary  {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    for {
      years <- answers.inverselySortedOffshoreTaxYears
      notIncluded <- answers.get(YouHaveNotIncludedTheTaxYearPage)
    } yield {
      val yearsNotIncluded = TaxYearStarting.findMissingYears(years.toList).map(_.startYear + 1).mkString(", ")
      SummaryListRowViewModel(
        key = messages("youHaveNotIncludedTheTaxYear.checkYourAnswersLabel", yearsNotIncluded),
        value = ValueViewModel(HtmlFormat.escape(notIncluded).toString),
        actions = Seq(
          ActionItemViewModel("site.change", routes.YouHaveNotIncludedTheTaxYearController.onPageLoad(CheckMode).url)
            .withVisuallyHiddenText(messages("youHaveNotIncludedTheTaxYear.change.hidden"))
        )
      )
    }
}
