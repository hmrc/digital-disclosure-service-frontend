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
import pages.YourAddressLookupPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import play.twirl.api.HtmlFormat

object YourAddressLookupSummary {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(YourAddressLookupPage).map { answer =>
      SummaryListRowViewModel(
        key = "yourAddress.checkYourAnswersLabel",
        value = ValueViewModel(HtmlContent(answer.getAddressLines.map(HtmlFormat.escape).mkString("<br>"))),
        actions = Seq(
          ActionItemViewModel("site.change", routes.YourAddressLookupController.lookupAddress(CheckMode).url)
            .withVisuallyHiddenText(messages("yourAddress.change.hidden"))
        )
      )
    }
}
