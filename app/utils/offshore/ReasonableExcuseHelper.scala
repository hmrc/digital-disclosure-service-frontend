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

import models.WhyAreYouMakingThisDisclosure.{DidNotFile, DidNotNotifyHMRC, InaccurateReturn}
import models.WhyDidYouNotFileAReturnOnTimeOffshore.ReasonableExcuse
import models.WhyYouSubmittedAnInaccurateReturn.ReasonableMistake
import models.{UserAnswers, WhyAreYouMakingThisDisclosure, WhyDidYouNotFileAReturnOnTimeOffshore, WhyDidYouNotNotify, WhyYouSubmittedAnInaccurateReturn}
import pages.{WhyAreYouMakingThisDisclosurePage, WhyDidYouNotFileAReturnOnTimeOffshorePage,
  WhyDidYouNotNotifyPage, WhyYouSubmittedAnInaccurateOffshoreReturnPage}
import utils.DynamicNonPenaltyFlags

object ReasonableExcuseHelper {

//  def showPenaltyWhenNotReasonableExcuse(userAnswers: UserAnswers): Boolean =
//    dynamicContentFlags(userAnswers).showPenaltyTextbox

  def dynamicContentFlags(userAnswers: UserAnswers): DynamicNonPenaltyFlags = {

    val whyAreYouMakingThisDisclosure: Set[WhyAreYouMakingThisDisclosure] =
      userAnswers.get(WhyAreYouMakingThisDisclosurePage).getOrElse(Set.empty)

    val inaccurateSelections: Set[WhyYouSubmittedAnInaccurateReturn] =
      userAnswers.get(WhyYouSubmittedAnInaccurateOffshoreReturnPage).getOrElse(Set.empty)

    val lateReturnSelections: Set[WhyDidYouNotFileAReturnOnTimeOffshore] =
      userAnswers.get(WhyDidYouNotFileAReturnOnTimeOffshorePage).getOrElse(Set.empty)

    val notifySelections: Set[WhyDidYouNotNotify] =
      userAnswers.get(WhyDidYouNotNotifyPage).getOrElse(Set.empty)

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
        notifySelections == Set(ReasonableExcuse)

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
