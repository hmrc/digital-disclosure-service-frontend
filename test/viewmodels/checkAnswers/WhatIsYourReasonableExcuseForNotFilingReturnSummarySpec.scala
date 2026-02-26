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
import models.{UserAnswers, WhatIsYourReasonableExcuseForNotFilingReturn}
import pages.WhatIsYourReasonableExcuseForNotFilingReturnPage
import play.api.i18n.Messages
import viewmodels.RevealFullText

class WhatIsYourReasonableExcuseForNotFilingReturnSummarySpec extends SpecBase {

  lazy val revealFullText     = application.injector.instanceOf[RevealFullText]
  implicit val mess: Messages = messages

  "WhatIsYourReasonableExcuseForNotFilingReturnSummary" - {

    "must return None when the page has no answer" in {
      val userAnswers = UserAnswers("id", "session-123")
      WhatIsYourReasonableExcuseForNotFilingReturnSummary.row(
        "reasonableExcuse",
        userAnswers,
        revealFullText
      ) mustBe None
    }

    "must return a row for the reasonableExcuse field" in {
      val answer      =
        WhatIsYourReasonableExcuseForNotFilingReturn(reasonableExcuse = "My excuse", yearsThisAppliesTo = "2020-2021")
      val userAnswers = UserAnswers("id", "session-123")
        .set(WhatIsYourReasonableExcuseForNotFilingReturnPage, answer)
        .success
        .value

      val result =
        WhatIsYourReasonableExcuseForNotFilingReturnSummary.row("reasonableExcuse", userAnswers, revealFullText)
      result mustBe defined
      result.map(_.key.content.toString must include("Reasonable excuse"))
    }

    "must return a row for the yearsThisAppliesTo field" in {
      val answer      =
        WhatIsYourReasonableExcuseForNotFilingReturn(reasonableExcuse = "My excuse", yearsThisAppliesTo = "2020-2021")
      val userAnswers = UserAnswers("id", "session-123")
        .set(WhatIsYourReasonableExcuseForNotFilingReturnPage, answer)
        .success
        .value

      val result =
        WhatIsYourReasonableExcuseForNotFilingReturnSummary.row("yearsThisAppliesTo", userAnswers, revealFullText)
      result mustBe defined
      result.map(_.value.content.toString must include("2020-2021"))
    }

    "must handle long text that exceeds the reveal threshold" in {
      val longText    = "A" * 200
      val answer      =
        WhatIsYourReasonableExcuseForNotFilingReturn(reasonableExcuse = longText, yearsThisAppliesTo = "2020-2021")
      val userAnswers = UserAnswers("id", "session-123")
        .set(WhatIsYourReasonableExcuseForNotFilingReturnPage, answer)
        .success
        .value

      val result =
        WhatIsYourReasonableExcuseForNotFilingReturnSummary.row("reasonableExcuse", userAnswers, revealFullText)
      result mustBe defined
      result.map(_.value.content.toString must include("..."))
    }
  }
}
