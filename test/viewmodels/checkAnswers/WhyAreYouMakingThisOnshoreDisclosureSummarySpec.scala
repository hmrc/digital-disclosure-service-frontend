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
import models.{AreYouTheEntity, RelatesTo, UserAnswers, WhyAreYouMakingThisOnshoreDisclosure}
import pages.{AreYouTheEntityPage, RelatesToPage, WhyAreYouMakingThisOnshoreDisclosurePage}
import play.api.i18n.Messages

class WhyAreYouMakingThisOnshoreDisclosureSummarySpec extends SpecBase {

  implicit val mess: Messages = messages

  "WhyAreYouMakingThisOnshoreDisclosureSummary" - {

    "must return None when the page has no answer" in {
      val userAnswers = UserAnswers("id", "session-123")
      WhyAreYouMakingThisOnshoreDisclosureSummary.row(userAnswers) mustBe None
    }

    "must return a row when the user is the individual" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        ua2 <- ua.set(RelatesToPage, RelatesTo.AnIndividual)
        ua3 <- ua2.set(WhyAreYouMakingThisOnshoreDisclosurePage, Set[WhyAreYouMakingThisOnshoreDisclosure](
          WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHMRC,
          WhyAreYouMakingThisOnshoreDisclosure.DidNotFile
        ))
      } yield ua3).success.value

      val result = WhyAreYouMakingThisOnshoreDisclosureSummary.row(userAnswers)
      result mustBe defined
    }

    "must return a row when the user is not the individual" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, AreYouTheEntity.IAmAnAccountantOrTaxAgent)
        ua2 <- ua.set(RelatesToPage, RelatesTo.AnIndividual)
        ua3 <- ua2.set(WhyAreYouMakingThisOnshoreDisclosurePage, Set[WhyAreYouMakingThisOnshoreDisclosure](
          WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHMRC,
          WhyAreYouMakingThisOnshoreDisclosure.DidNotFile
        ))
      } yield ua3).success.value

      val result = WhyAreYouMakingThisOnshoreDisclosureSummary.row(userAnswers)
      result mustBe defined
    }

    "must return a row with multiple selections" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        ua2 <- ua.set(RelatesToPage, RelatesTo.AnIndividual)
        ua3 <- ua2.set(WhyAreYouMakingThisOnshoreDisclosurePage, Set[WhyAreYouMakingThisOnshoreDisclosure](
          WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHMRC,
          WhyAreYouMakingThisOnshoreDisclosure.DidNotFile
        ))
      } yield ua3).success.value

      val result = WhyAreYouMakingThisOnshoreDisclosureSummary.row(userAnswers)
      result mustBe defined
    }
  }
}