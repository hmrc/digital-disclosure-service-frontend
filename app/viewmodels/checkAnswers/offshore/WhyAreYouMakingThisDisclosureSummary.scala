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
import models.{CheckMode, RelatesTo, UserAnswers}
import pages.{RelatesToPage, WhyAreYouMakingThisDisclosurePage}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.all.FluentText
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object WhyAreYouMakingThisDisclosureSummary {

  def row(userAnswers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    userAnswers.get(WhyAreYouMakingThisDisclosurePage).map { answers =>
      val areTheyTheIndividual = userAnswers.isTheUserTheIndividual
      val entity               = userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      val value = ValueViewModel(
        HtmlContent(
          Text(
            answers
              .map { answer =>
                HtmlFormat
                  .escape(
                    messages(
                      if (areTheyTheIndividual) s"whyAreYouMakingThisDisclosure.you.$answer"
                      else s"whyAreYouMakingThisDisclosure.$entity.$answer"
                    )
                  )
                  .toString
              }
              .mkString("<br>")
          ).withEllipsisOverflow(150).value
        )
      )

      SummaryListRowViewModel(
        key = "whyAreYouMakingThisDisclosure.checkYourAnswersLabel",
        value = value,
        actions = Seq(
          ActionItemViewModel("site.change", routes.WhyAreYouMakingThisDisclosureController.onPageLoad(CheckMode).url)
            .withVisuallyHiddenText(messages("whyAreYouMakingThisDisclosure.change.hidden"))
        )
      )
    }

}
