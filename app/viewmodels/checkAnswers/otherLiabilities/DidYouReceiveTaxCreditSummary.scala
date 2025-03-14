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

import controllers.otherLiabilities.routes
import models.{CheckMode, RelatesTo, UserAnswers}
import pages.{DidYouReceiveTaxCreditPage, RelatesToPage}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object DidYouReceiveTaxCreditSummary {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(DidYouReceiveTaxCreditPage).map { answer =>
      val answerString = if (answer) "yes" else "no"

      val value = ValueViewModel(
        HtmlContent(
          HtmlFormat.escape(messages(s"didYouReceiveTaxCredit.$answerString"))
        )
      )

      val areTheyTheIndividual = answers.isTheUserTheIndividual
      val entity               = answers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)
      val key                  = messages(
        if (areTheyTheIndividual) s"didYouReceiveTaxCredit.you.checkYourAnswersLabel"
        else s"didYouReceiveTaxCredit.$entity.checkYourAnswersLabel"
      )

      SummaryListRowViewModel(
        key = key,
        value = value,
        actions = Seq(
          ActionItemViewModel("site.change", routes.DidYouReceiveTaxCreditController.onPageLoad(CheckMode).url)
            .withVisuallyHiddenText(
              messages(
                if (areTheyTheIndividual) s"didYouReceiveTaxCredit.you.hidden"
                else s"didYouReceiveTaxCredit.$entity.hidden"
              )
            )
        )
      )
    }

}
