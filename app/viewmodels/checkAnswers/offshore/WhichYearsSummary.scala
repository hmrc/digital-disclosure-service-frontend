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
import models.{Behaviour, CarelessPriorTo, CheckMode, DeliberatePriorTo, ReasonableExcusePriorTo, UserAnswers}
import pages.WhichYearsPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import services.OffshoreWhichYearsService
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import com.google.inject.{Inject, Singleton}

@Singleton
class WhichYearsSummary @Inject() (offshoreWhichYearsService: OffshoreWhichYearsService) {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(WhichYearsPage).map { answers =>
      val value = ValueViewModel(
        HtmlContent(
          answers.toSeq.sorted
            .map { answer =>
              answer match {
                case ReasonableExcusePriorTo =>
                  HtmlFormat
                    .escape(
                      messages(
                        s"whichYears.checkbox.any",
                        offshoreWhichYearsService.getEarliestYearByBehaviour(Behaviour.ReasonableExcuse).toString
                      )
                    )
                    .toString
                case CarelessPriorTo         =>
                  HtmlFormat
                    .escape(
                      messages(
                        s"whichYears.checkbox.any",
                        offshoreWhichYearsService.getEarliestYearByBehaviour(Behaviour.Careless).toString
                      )
                    )
                    .toString
                case DeliberatePriorTo       =>
                  HtmlFormat
                    .escape(
                      messages(
                        s"whichYears.checkbox.any",
                        offshoreWhichYearsService.getEarliestYearByBehaviour(Behaviour.Deliberate).toString
                      )
                    )
                    .toString
                case _                       =>
                  HtmlFormat
                    .escape(messages(s"whichYears.checkbox", answer, (answer.toString.toInt + 1).toString))
                    .toString
              }
            }
            .mkString(",<br>")
        )
      )

      SummaryListRowViewModel(
        key = "whichYears.checkYourAnswersLabel",
        value = value,
        actions = Seq(
          ActionItemViewModel("site.change", routes.WhichYearsController.onPageLoad(CheckMode).url)
            .withVisuallyHiddenText(messages("whichYears.change.hidden"))
        )
      )
    }
}
