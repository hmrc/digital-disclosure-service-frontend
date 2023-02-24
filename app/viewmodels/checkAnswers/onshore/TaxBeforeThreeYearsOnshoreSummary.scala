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
import models.{Behaviour, CheckMode, UserAnswers}
import pages.TaxBeforeThreeYearsOnshorePage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import services.OnshoreWhichYearsService
import com.google.inject.{Inject, Singleton}

@Singleton
class TaxBeforeThreeYearsOnshoreSummary @Inject() (onshoreWhichYearsService: OnshoreWhichYearsService)  {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(TaxBeforeThreeYearsOnshorePage).map {
      answer =>

      val year = onshoreWhichYearsService.getEarliestYearByBehaviour(Behaviour.ReasonableExcuse).toString

      SummaryListRowViewModel(
        key     = messages("taxBeforeThreeYears.checkYourAnswersLabel", year),
        value   = ValueViewModel(HtmlFormat.escape(answer).toString),
        actions = Seq(
          ActionItemViewModel("site.change", routes.TaxBeforeThreeYearsOnshoreController.onPageLoad(CheckMode).url)
            .withVisuallyHiddenText(messages("taxBeforeThreeYears.change.hidden"))
        )
      )
    }
}
