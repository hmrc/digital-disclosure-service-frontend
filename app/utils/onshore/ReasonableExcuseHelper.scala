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

import models.WhyDidYouNotFileAReturnOnTimeOnshore.ReasonableExcuse
import models.WhyDidYouNotNotifyOnshore.ReasonableExcuseOnshore
import models.WhyYouSubmittedAnInaccurateOnshoreReturn.ReasonableMistake
import models.WhyAreYouMakingThisOnshoreDisclosure.{DidNotFile, DidNotNotifyHMRC, InaccurateReturn}
import models.{UserAnswers, WhyAreYouMakingThisOnshoreDisclosure, WhyDidYouNotFileAReturnOnTimeOnshore,
  WhyDidYouNotNotifyOnshore, WhyYouSubmittedAnInaccurateOnshoreReturn}
import pages.onshore.WhyDidYouNotFileAReturnOnTimeOnshorePage
import pages.{WhyAreYouMakingThisOnshoreDisclosurePage, WhyDidYouNotNotifyOnshorePage, WhyYouSubmittedAnInaccurateOnshoreReturnPage}
import utils.DynamicNonPenaltyFlags

object ReasonableExcuseHelper {

  def dynamicContentFlags(userAnswers: UserAnswers): DynamicNonPenaltyFlags = {

    val whyAreYouMakingThisDisclosure: Set[WhyAreYouMakingThisOnshoreDisclosure] =
      userAnswers.get(WhyAreYouMakingThisOnshoreDisclosurePage).getOrElse(Set.empty)

    val inaccurateSelections: Set[WhyYouSubmittedAnInaccurateOnshoreReturn] =
      userAnswers.get(WhyYouSubmittedAnInaccurateOnshoreReturnPage).getOrElse(Set.empty)

    val lateReturnSelections: Set[WhyDidYouNotFileAReturnOnTimeOnshore] =
      userAnswers.get(WhyDidYouNotFileAReturnOnTimeOnshorePage).getOrElse(Set.empty)

    val notifySelections: Set[WhyDidYouNotNotifyOnshore] =
      userAnswers.get(WhyDidYouNotNotifyOnshorePage).getOrElse(Set.empty)

    val inaccurateReturnSelected: Boolean =
      whyAreYouMakingThisDisclosure.contains(InaccurateReturn)

    val lateReturnSelected: Boolean =
      whyAreYouMakingThisDisclosure.contains(DidNotFile)

    val notifySelected: Boolean =
      whyAreYouMakingThisDisclosure.contains(DidNotNotifyHMRC)

    val hasOnlyInaccurateReasonableMistake: Boolean =
      inaccurateReturnSelected &&
        inaccurateSelections == Set(ReasonableMistake)

    val hasOnlyLateReturnReasonableExcuse: Boolean =
      lateReturnSelected &&
        lateReturnSelections == Set(ReasonableExcuse)

    val hasOnlyNotifyReasonableExcuse: Boolean =
      notifySelected &&
        notifySelections == Set(ReasonableExcuseOnshore)

    val selectedFlowsAreOnlyReasonable: Boolean =
      (!inaccurateReturnSelected || hasOnlyInaccurateReasonableMistake) &&
        (!lateReturnSelected || hasOnlyLateReturnReasonableExcuse) &&
        (!notifySelected || hasOnlyNotifyReasonableExcuse)

    DynamicNonPenaltyFlags(
      showInaccurateReasonableParagraph = hasOnlyInaccurateReasonableMistake,
      showLateReturnReasonableParagraph = hasOnlyLateReturnReasonableExcuse,
      showNotifyReasonableParagraph = hasOnlyNotifyReasonableExcuse,
      showPenaltyTextbox = !selectedFlowsAreOnlyReasonable
    )
  }
}
