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
import java.util.Locale

import controllers.letting.routes
import models.{CheckMode, LettingProperty}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import viewmodels.RevealFullText

object PropertyIsNoLongerBeingLetOutSummary  {

  def row(i: Int, lettingProperty: LettingProperty, fieldName: String, revealFullText: RevealFullText)(implicit messages: Messages): Option[SummaryListRow] =
    lettingProperty.noLongerBeingLetOut.map {
      answer =>

        if(fieldName == "stopDate") {
          val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale(messages.lang.code))

          SummaryListRowViewModel(
            key     = "propertyIsNoLongerBeingLetOut.stopDate.checkYourAnswersLabel",
            value   = ValueViewModel(answer.stopDate.format(dateFormatter)),
            actions = Seq(
              ActionItemViewModel("site.change", routes.PropertyIsNoLongerBeingLetOutController.onPageLoad(i, CheckMode).url)
                .withVisuallyHiddenText(messages("propertyIsNoLongerBeingLetOut.stopDate.hidden"))
            )
          )
        } else {
          SummaryListRowViewModel(
            key     = "propertyIsNoLongerBeingLetOut.whatHasHappenedToProperty.checkYourAnswersLabel",
            value   = ValueViewModel(revealFullText.addRevealToText(answer.whatHasHappenedToProperty, "propertyIsNoLongerBeingLetOut.whatHasHappenedToProperty.reveal", (i+1).toString)),
            actions = Seq(
              ActionItemViewModel("site.change", routes.PropertyIsNoLongerBeingLetOutController.onPageLoad(i, CheckMode).url)
                .withVisuallyHiddenText(messages("propertyIsNoLongerBeingLetOut.whatHasHappenedToProperty.hidden"))
            )
          )
        }
    }
}
