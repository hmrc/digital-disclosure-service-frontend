/*
 * Copyright 2026 HM Revenue & Customs
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

package models

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.checkbox._

sealed trait WhyYouSubmittedAnInaccurateReturn

object WhyYouSubmittedAnInaccurateReturn extends Enumerable.Implicits {

  case object NoReasonableCare extends WithName("noReasonableCare") with WhyYouSubmittedAnInaccurateReturn
  case object ReasonableMistake extends WithName("reasonableMistake") with WhyYouSubmittedAnInaccurateReturn
  case object DeliberatelyInaccurate extends WithName("deliberatelyInaccurate") with WhyYouSubmittedAnInaccurateReturn

  val values: Seq[WhyYouSubmittedAnInaccurateReturn] = Seq(
    NoReasonableCare,
    ReasonableMistake,
    DeliberatelyInaccurate
  )

  def checkboxItems(areTheyTheIndividual: Boolean, entity: RelatesTo)(implicit messages: Messages): Seq[CheckboxItem] =
    values.zipWithIndex.map { case (value, index) =>
      CheckboxItemViewModel(
        content = Text(messages(constructMessageKey(value, areTheyTheIndividual))),
        fieldId = "value",
        index = index,
        value = value.toString
      )
    }

  def constructMessageKey(
    value: WhyYouSubmittedAnInaccurateReturn,
    areTheyTheIndividual: Boolean
  ): String =
    if (areTheyTheIndividual) {
      s"WhyYouSubmittedAnInaccurateReturn.you.${value.toString}"
    } else {
      s"WhyYouSubmittedAnInaccurateReturn.notYou.${value.toString}"
    }

  implicit val enumerable: Enumerable[WhyYouSubmittedAnInaccurateReturn] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
