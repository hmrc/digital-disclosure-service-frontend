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

sealed trait WhyDidYouNotNotify

object WhyDidYouNotNotify extends Enumerable.Implicits {

  case object DeliberatelyDidNotNotify extends WithName("deliberatelyDidNotNotify") with WhyDidYouNotNotify
  case object ReasonableExcuse extends WithName("reasonableExcuse") with WhyDidYouNotNotify
  case object NotDeliberatelyNoReasonableExcuse
      extends WithName("notDeliberatelyNoReasonableExcuse")
      with WhyDidYouNotNotify

  val values: Seq[WhyDidYouNotNotify] = Seq(
    NotDeliberatelyNoReasonableExcuse,
    ReasonableExcuse,
    DeliberatelyDidNotNotify
  )

  def checkboxItems(areTheyTheIndividual: Boolean, entity: RelatesTo)(implicit messages: Messages): Seq[CheckboxItem] =
    values.zipWithIndex.map { case (value, index) =>
      CheckboxItemViewModel(
        content = Text(messages(constructMessageKey(value, areTheyTheIndividual, entity))),
        fieldId = "value",
        index = index,
        value = value.toString
      )
    }

  def constructMessageKey(
                           value: WhyDidYouNotNotify,
                           areTheyTheIndividual: Boolean,
                           entity: RelatesTo
                         ) =
    if (areTheyTheIndividual) {
      s"whyDidYouNotNotify.you.${value.toString}"
    } else if (entity == RelatesTo.AnEstate) {
      s"whyDidYouNotNotify.estate.${value.toString}"
    } else {
      s"whyDidYouNotNotify.notYou.${value.toString}"
    }

  implicit val enumerable: Enumerable[WhyDidYouNotNotify] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
