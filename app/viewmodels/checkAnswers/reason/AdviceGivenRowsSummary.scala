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

import controllers.reason.routes
import models.{CheckMode, UserAnswers, AdviceContactPreference}
import pages._
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent

object AdviceGivenRowsSummary  {

  def rows(answers: UserAnswers)(implicit messages: Messages): Seq[SummaryListRow] =
    answers.get(AdviceGivenPage).map { answer =>
      Seq(
        adviceGivenRow(answer.adviceGiven),
        dateRow(answer.adviceMonth, answer.adviceYear),
        contactRow(answer.contactPreference)
      )
    }.getOrElse(Nil)

  def adviceGivenRow(adviceGiven: String)(implicit messages: Messages): SummaryListRow =
    SummaryListRowViewModel(
      key     = "adviceGiven.adviceGiven.checkYourAnswersLabel",
      value   = ValueViewModel(HtmlFormat.escape(adviceGiven).toString),
      actions = Seq(
        ActionItemViewModel("site.change", routes.AdviceGivenController.onPageLoad(CheckMode).url)
          .withVisuallyHiddenText(messages("adviceGiven.adviceGiven.change.hidden"))
      )
    )

  def dateRow(month: Int, year: Int)(implicit messages: Messages): SummaryListRow = {

    val FIRST_DAY = 1
    val dateFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    val dateAtStartOfMonth = LocalDate.of(year, month, FIRST_DAY)

    SummaryListRowViewModel(
      key     = "adviceGiven.date.checkYourAnswersLabel",
      value   = ValueViewModel(dateAtStartOfMonth.format(dateFormatter)),
      actions = Seq(
        ActionItemViewModel("site.change", routes.AdviceGivenController.onPageLoad(CheckMode).url)
          .withVisuallyHiddenText(messages("adviceGiven.date.change.hidden"))
      )
    )
  }

  def contactRow(contactPreference: AdviceContactPreference)(implicit messages: Messages): SummaryListRow = {

    val value = ValueViewModel(
      HtmlContent(
        HtmlFormat.escape(messages(s"adviceGiven.contact.$contactPreference"))
      )
    )

    SummaryListRowViewModel(
      key     = "adviceGiven.contact.checkYourAnswersLabel",
      value   = value,
      actions = Seq(
        ActionItemViewModel("site.change", routes.AdviceGivenController.onPageLoad(CheckMode).url)
          .withVisuallyHiddenText(messages("adviceGiven.contact.change.hidden"))
      )
    )
  }
    
}
