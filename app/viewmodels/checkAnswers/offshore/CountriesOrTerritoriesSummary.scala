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
import models.{CheckMode, UserAnswers}
import pages.CountryOfYourOffshoreLiabilityPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import viewmodels.RevealFullText

object CountriesOrTerritoriesSummary  {

  def row(answers: UserAnswers, revealFullText: RevealFullText)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(CountryOfYourOffshoreLiabilityPage).map {
      answer =>

        val value = answer.values.map(_.name).mkString(", ")

        SummaryListRowViewModel(
          key     = "countriesOrTerritories.checkYourAnswersLabel",
          value   = ValueViewModel(revealFullText.addRevealToText(value, "countriesOrTerritories.reveal")),
          actions = Seq(
            ActionItemViewModel("site.change", routes.CountriesOrTerritoriesController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText(messages("countriesOrTerritories.change.hidden"))
          )
        )
    }
}
