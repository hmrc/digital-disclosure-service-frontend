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

package utils.offshore

import models.{UserAnswers, WhyDidYouNotFileAReturnOnTimeOffshore, WhyDidYouNotNotify, WhyYouSubmittedAnInaccurateReturn}
import pages.{WhyDidYouNotNotifyPage, WhyYouSubmittedAnInaccurateOffshoreReturnPage, WhyDidYouNotFileAReturnOnTimeOffshorePage}

object ReasonableExcuseHelper {

  def hidePenaltyWhenReasonableExcuse(userAnswers: UserAnswers): Boolean = {

    val inaccurateSelections = userAnswers.get(WhyYouSubmittedAnInaccurateOffshoreReturnPage).getOrElse(Set())

    val lateReturnSelections = userAnswers.get(WhyDidYouNotFileAReturnOnTimeOffshorePage).getOrElse(Set())

    val notifySelections = userAnswers.get(WhyDidYouNotNotifyPage).getOrElse(Set())

    val reasonableSelected =
      Seq(
        inaccurateSelections.contains(WhyYouSubmittedAnInaccurateReturn.ReasonableMistake),
        lateReturnSelections.contains(WhyDidYouNotFileAReturnOnTimeOffshore.ReasonableExcuse),
        notifySelections.contains(WhyDidYouNotNotify.ReasonableExcuse)
      ).count(identity) == 1

    val noOtherSelections =
      inaccurateSelections.size <= 1 &&
        lateReturnSelections.size <= 1 &&
        notifySelections.size <= 1 &&
        inaccurateSelections.forall(_ == WhyYouSubmittedAnInaccurateReturn.ReasonableMistake) &&
        lateReturnSelections.forall(_ == WhyDidYouNotFileAReturnOnTimeOffshore.ReasonableExcuse) &&
        notifySelections.forall(_ == WhyDidYouNotNotify.ReasonableExcuse)

    reasonableSelected && noOtherSelections
  }
}