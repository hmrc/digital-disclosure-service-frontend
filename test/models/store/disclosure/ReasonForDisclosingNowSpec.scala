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

package models.store.disclosure

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import models.{MonthYear, WhyAreYouMakingADisclosure, AdviceGiven, AdviceContactPreference, WhichEmailAddressCanWeContactYouWith}

class ReasonForDisclosingNowSpec extends AnyFreeSpec with Matchers with OptionValues {

  "isComplete" - {

    "must return true where they have answered necessary questions and no advice was given" in {
      val reasonForDisclosingNow = ReasonForDisclosingNow(Some(Set(WhyAreYouMakingADisclosure.GovUkGuidance)), None, Some("Some reason"), Some(false))
      reasonForDisclosingNow.isComplete mustBe true
    }

    "must return true where they have answered necessary questions and advice was given" in {
      val reasonForDisclosingNow = ReasonForDisclosingNow(
        Some(Set(WhyAreYouMakingADisclosure.GovUkGuidance)),
        None,
        Some("Some reason"),
        Some(true),
        Some("Some guy"),
        Some(false),
        None,
        Some("Some profession"),
        Some(AdviceGiven("Some advice", MonthYear(12, 2012), AdviceContactPreference.Email)),
        Some(WhichEmailAddressCanWeContactYouWith.ExistingEmail),
        None,
        None,
        None
      )
      reasonForDisclosingNow.isComplete mustBe true
    }

    "must return false where they have not answered all necessary questions" in {
      val reasonForDisclosingNow = ReasonForDisclosingNow()
      reasonForDisclosingNow.isComplete mustBe false
    }

  }
}

