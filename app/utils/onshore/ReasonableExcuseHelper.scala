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

package utils.onshore

import models.{UserAnswers, WhyDidYouNotFileAReturnOnTimeOnshore, WhyDidYouNotNotifyOnshore, WhyYouSubmittedAnInaccurateOnshoreReturn}
import pages.onshore.WhyDidYouNotFileAReturnOnTimeOnshorePage
import pages.{WhyDidYouNotNotifyOnshorePage, WhyYouSubmittedAnInaccurateOnshoreReturnPage}

object ReasonableExcuseHelper {

  def hidePenaltyWhenReasonableExcuse(userAnswers: UserAnswers): Boolean = {

    val inaccurateSelections = userAnswers.get(WhyYouSubmittedAnInaccurateOnshoreReturnPage).getOrElse(Set())

    val lateReturnSelections = userAnswers.get(WhyDidYouNotFileAReturnOnTimeOnshorePage).getOrElse(Set())

    val notifySelections = userAnswers.get(WhyDidYouNotNotifyOnshorePage).getOrElse(Set())

    val reasonableSelected =
      inaccurateSelections.contains(WhyYouSubmittedAnInaccurateOnshoreReturn.ReasonableMistake) ||
      lateReturnSelections.contains(WhyDidYouNotFileAReturnOnTimeOnshore.ReasonableExcuse) ||
      notifySelections.contains(WhyDidYouNotNotifyOnshore.ReasonableExcuseOnshore)

    val noOtherSelections =
      inaccurateSelections.size <= 1 &&
        lateReturnSelections.size <= 1 &&
        notifySelections.size <= 1 &&
        inaccurateSelections.forall(_ == WhyYouSubmittedAnInaccurateOnshoreReturn.ReasonableMistake) &&
        lateReturnSelections.forall(_ == WhyDidYouNotFileAReturnOnTimeOnshore.ReasonableExcuse) &&
        notifySelections.forall(_ == WhyDidYouNotNotifyOnshore.ReasonableExcuseOnshore)

    reasonableSelected && noOtherSelections
  }
}