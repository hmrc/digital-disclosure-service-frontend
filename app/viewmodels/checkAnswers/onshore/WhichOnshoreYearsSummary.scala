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
import models.{Behaviour, CheckMode, PriorToFiveYears, PriorToNineteenYears, PriorToThreeYears, UserAnswers}
import pages.WhichOnshoreYearsPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import services.OnshoreWhichYearsService
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import com.google.inject.{Inject, Singleton}

@Singleton
class WhichOnshoreYearsSummary @Inject() (onshoreWhichYearsService: OnshoreWhichYearsService) {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(WhichOnshoreYearsPage).map { answers =>
      val value = ValueViewModel(
        HtmlContent(
          answers.toSeq.sorted
            .map { answer =>
              answer match {
                case PriorToThreeYears    =>
                  HtmlFormat
                    .escape(
                      messages(
                        s"whichOnshoreYears.checkbox.any",
                        onshoreWhichYearsService.getEarliestYearByBehaviour(Behaviour.ReasonableExcuse).toString
                      )
                    )
                    .toString
                case PriorToFiveYears     =>
                  HtmlFormat
                    .escape(
                      messages(
                        s"whichOnshoreYears.checkbox.any",
                        onshoreWhichYearsService.getEarliestYearByBehaviour(Behaviour.Careless).toString
                      )
                    )
                    .toString
                case PriorToNineteenYears =>
                  HtmlFormat
                    .escape(
                      messages(
                        s"whichOnshoreYears.checkbox.any",
                        onshoreWhichYearsService.getEarliestYearByBehaviour(Behaviour.Deliberate).toString
                      )
                    )
                    .toString
                case _                    =>
                  HtmlFormat
                    .escape(messages(s"whichOnshoreYears.checkbox", answer, (answer.toString.toInt + 1).toString))
                    .toString
              }
            }
            .mkString(",<br>")
        )
      )

      SummaryListRowViewModel(
        key = "whichOnshoreYears.checkYourAnswersLabel",
        value = value,
        actions = Seq(
          ActionItemViewModel("site.change", routes.WhichOnshoreYearsController.onPageLoad(CheckMode).url)
            .withVisuallyHiddenText(messages("whichOnshoreYears.change.hidden"))
        )
      )
    }
}
