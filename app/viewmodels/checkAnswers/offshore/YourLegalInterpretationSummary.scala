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
import models.{CheckMode, UserAnswers, YourLegalInterpretation}
import pages.YourLegalInterpretationPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object YourLegalInterpretationSummary  {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(YourLegalInterpretationPage).map {
      answers =>

        val value = ValueViewModel(
            answers.map { answer =>
              answer match {
                case YourLegalInterpretation.TheTransferOfAssets => Text(messages(s"yourLegalInterpretation.$answer.first")).toString
                case YourLegalInterpretation.WhetherIncomeShouldBeTaxed => Text(messages(s"yourLegalInterpretation.$answer.first")).toString
                case _ => (messages(s"yourLegalInterpretation.$answer")).toString 
              }  
            }
            .mkString(",<br>")
        )

        SummaryListRowViewModel(
          key     = "yourLegalInterpretation.checkYourAnswersLabel",
          value   = value,
          actions = Seq(
            ActionItemViewModel("site.change", routes.YourLegalInterpretationController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText(messages("yourLegalInterpretation.change.hidden"))
          )
        )
    }
}
