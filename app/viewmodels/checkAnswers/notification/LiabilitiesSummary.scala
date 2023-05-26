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

import controllers.notification.routes
import models.{CheckMode, UserAnswers}
import pages.{OffshoreLiabilitiesPage, OnshoreLiabilitiesPage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import uk.gov.hmrc.govukfrontend.views.Aliases.Text

object LiabilitiesSummary  {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] = {

    val offshoreAnswer = answers.get(OffshoreLiabilitiesPage).getOrElse(true)
    val onshoreAnswer = answers.get(OnshoreLiabilitiesPage).getOrElse(true)

    val value = (offshoreAnswer, onshoreAnswer) match {
      case (true, false) => Text(messages("liabilities.offshore"))
      case (false, _) => Text(messages("liabilities.onshore"))
      case (true, true) => Text(messages("liabilities.offshoreOnshore"))
    }

    Some(createRow(value))
  }

  def createRow(value: Text)(implicit messages: Messages): SummaryListRow = {
    SummaryListRowViewModel(
      key     = "liabilities.checkYourAnswersLabel",
      value   = ValueViewModel(value),
      actions = Seq(
        ActionItemViewModel("site.change", routes.OffshoreLiabilitiesController.onPageLoad(CheckMode).url)
          .withVisuallyHiddenText(messages("liabilities.change.hidden"))
      )
    )
  }  
}