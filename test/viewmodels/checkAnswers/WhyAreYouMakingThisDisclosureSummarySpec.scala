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

package viewmodels.checkAnswers

import base.SpecBase
import models.{AreYouTheEntity, RelatesTo, UserAnswers, WhyAreYouMakingThisDisclosure}
import pages.{AreYouTheEntityPage, RelatesToPage, WhyAreYouMakingThisDisclosurePage}
import play.api.i18n.Messages

class WhyAreYouMakingThisDisclosureSummarySpec extends SpecBase {

  implicit val mess: Messages = messages

  "WhyAreYouMakingThisDisclosureSummary" - {

    "must return None when the page has no answer" in {
      val userAnswers = UserAnswers("id", "session-123")
      WhyAreYouMakingThisDisclosureSummary.row(userAnswers) mustBe None
    }

    "must return a row when the user is the individual" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        ua2 <- ua.set(RelatesToPage, RelatesTo.AnIndividual)
        ua3 <- ua2.set(WhyAreYouMakingThisDisclosurePage, Set[WhyAreYouMakingThisDisclosure](
          WhyAreYouMakingThisDisclosure.DidNotNotifyHMRC
        ))
      } yield ua3).success.value

      val result = WhyAreYouMakingThisDisclosureSummary.row(userAnswers)
      result mustBe defined
    }

    "must return a row when the user is not the individual" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, AreYouTheEntity.IAmAnAccountantOrTaxAgent)
        ua2 <- ua.set(RelatesToPage, RelatesTo.AnIndividual)
        ua3 <- ua2.set(WhyAreYouMakingThisDisclosurePage, Set[WhyAreYouMakingThisDisclosure](
          WhyAreYouMakingThisDisclosure.DidNotNotifyHMRC
        ))
      } yield ua3).success.value

      val result = WhyAreYouMakingThisDisclosureSummary.row(userAnswers)
      result mustBe defined
    }

    "must return a row with multiple selections" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        ua2 <- ua.set(RelatesToPage, RelatesTo.AnIndividual)
        ua3 <- ua2.set(WhyAreYouMakingThisDisclosurePage, Set[WhyAreYouMakingThisDisclosure](
          WhyAreYouMakingThisDisclosure.DidNotNotifyHMRC,
          WhyAreYouMakingThisDisclosure.DidNotFile
        ))
      } yield ua3).success.value

      val result = WhyAreYouMakingThisDisclosureSummary.row(userAnswers)
      result mustBe defined
    }
  }
}