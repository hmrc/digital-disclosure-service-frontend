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

package models

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait TheMaximumValueOfAllAssets

object TheMaximumValueOfAllAssets extends Enumerable.Implicits {

  case object Below500k extends WithName("below500k") with TheMaximumValueOfAllAssets
  case object Between500kAnd1M extends WithName("between500kAnd1M") with TheMaximumValueOfAllAssets
  case object Between1MAnd100M extends WithName("between1MAnd100M") with TheMaximumValueOfAllAssets
  case object Between100MAnd500M extends WithName("between100MAnd500M") with TheMaximumValueOfAllAssets
  case object Between500MAnd1B extends WithName("between500MAnd1B") with TheMaximumValueOfAllAssets
  case object Over1B extends WithName("over1B") with TheMaximumValueOfAllAssets

  val values: Seq[TheMaximumValueOfAllAssets] = Seq(
    Below500k, Between500kAnd1M, Between1MAnd100M, Between100MAnd500M, Between500MAnd1B, Over1B
  )

  def options(implicit messages: Messages): Seq[RadioItem] = values.zipWithIndex.map {
    case (value, index) =>
      RadioItem(
        content = Text(messages(s"theMaximumValueOfAllAssets.${value.toString}")),
        value   = Some(value.toString),
        id      = Some(s"value_$index")
      )
  }

  implicit val enumerable: Enumerable[TheMaximumValueOfAllAssets] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
