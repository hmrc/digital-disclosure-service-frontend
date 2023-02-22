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

import java.time.format.DateTimeFormatter

import controllers.onshore.routes
import models.{CheckMode, UserAnswers}
import pages.CorporationTaxLiabilityPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object CorporationTaxLiabilitySummary  {

  def row(i: Int, answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(CorporationTaxLiabilityPage).map {
      answer =>

        SummaryListRowViewModel(
          key     = "corporationTaxLiability.checkYourAnswersLabel",
          value   = ValueViewModel(""),
          actions = Seq(
            ActionItemViewModel("site.change", routes.CorporationTaxLiabilityController.onPageLoad(i, CheckMode).url)
              .withVisuallyHiddenText(messages("corporationTaxLiability.change.hidden"))
          )
        )
    }
}
