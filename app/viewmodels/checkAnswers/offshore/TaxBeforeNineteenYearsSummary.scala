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
import models.{Behaviour, CheckMode, UserAnswers}
import pages.TaxBeforeNineteenYearsPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import services.OffshoreWhichYearsService
import viewmodels.implicits._
import com.google.inject.{Inject, Singleton}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent

@Singleton
class TaxBeforeNineteenYearsSummary @Inject() (offshoreWhichYearsService: OffshoreWhichYearsService)  {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(TaxBeforeNineteenYearsPage).map {
      answer =>

        val year = offshoreWhichYearsService.getEarliestYearByBehaviour(Behaviour.Deliberate).toString

        SummaryListRowViewModel(
          key     = messages("taxBeforeNineteenYears.checkYourAnswersLabel", year),
          value   = ValueViewModel(HtmlContent(HtmlFormat.escape(answer))),
          actions = Seq(
            ActionItemViewModel("site.change", routes.TaxBeforeNineteenYearsController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText(messages("taxBeforeNineteenYears.change.hidden", year))
          )
        )
    }
}
