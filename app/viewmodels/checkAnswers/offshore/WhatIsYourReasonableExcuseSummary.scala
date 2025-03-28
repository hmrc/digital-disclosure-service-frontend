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
import pages.WhatIsYourReasonableExcusePage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import viewmodels.RevealFullText

object WhatIsYourReasonableExcuseSummary {

  def row(fieldName: String, answers: UserAnswers, revealFullText: RevealFullText)(implicit
    messages: Messages
  ): Option[SummaryListRow] =
    answers.get(WhatIsYourReasonableExcusePage).map { answer =>
      if (fieldName == "excuse") {
        SummaryListRowViewModel(
          key = "whatIsYourReasonableExcuse.excuse.checkYourAnswersLabel",
          value =
            ValueViewModel(revealFullText.addRevealToText(answer.excuse, "whatIsYourReasonableExcuse.excuse.reveal")),
          actions = Seq(
            ActionItemViewModel("site.change", routes.WhatIsYourReasonableExcuseController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText(messages("whatIsYourReasonableExcuse.excuse.change.hidden"))
          )
        )
      } else {
        SummaryListRowViewModel(
          key = "whatIsYourReasonableExcuse.years.checkYourAnswersLabel",
          value = ValueViewModel(answer.years.toString),
          actions = Seq(
            ActionItemViewModel("site.change", routes.WhatIsYourReasonableExcuseController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText(messages("whatIsYourReasonableExcuse.years.change.hidden"))
          )
        )
      }
    }
}
