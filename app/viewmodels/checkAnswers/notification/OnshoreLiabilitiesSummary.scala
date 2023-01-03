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
import pages.OnshoreLiabilitiesPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import pages._
import models._

object OnshoreLiabilitiesSummary  {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] = {
    
    val offshoreAnswer = answers.get(OffshoreLiabilitiesPage).getOrElse(true)
    val onshoreAnswer = answers.get(OnshoreLiabilitiesPage)

    if (offshoreAnswer == false) {
      Some(createRow(
        "onshoreLiabilities.default.checkYourAnswersLabel",
        "onshoreLiabilities.yes",
        routes.OffshoreLiabilitiesController.onPageLoad(CheckMode).url,
        "offshoreLiabilities.change.hidden"
      ))
    } else {
      onshoreAnswer.map {  answer => 
        
        val answerString = if (answer) "yes" else "no"

        createRow(
          "onshoreLiabilities.checkYourAnswersLabel",
          s"onshoreLiabilities.$answerString",
          routes.OnshoreLiabilitiesController.onPageLoad(CheckMode).url,
          "onshoreLiabilities.change.hidden"
        )
      }
    }
  }

  def createRow(key: String, answerKey: String, url: String, visuallyHidden: String)(implicit messages: Messages): SummaryListRow = {

    val value = ValueViewModel(
      HtmlContent(
        HtmlFormat.escape(messages(answerKey))
      )
    )

    SummaryListRowViewModel(
      key     = key,
      value   = value,
      actions = Seq(
        ActionItemViewModel("site.change", url)
          .withVisuallyHiddenText(messages(visuallyHidden))
      )
    )

  }

}
