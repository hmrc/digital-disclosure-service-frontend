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
import models.UserAnswers
import pages.OtherIncomeOrGainSourcePage
import play.api.i18n.Messages
import viewmodels.RevealFullText

class OtherIncomeOrGainSourceSummarySpec extends SpecBase {

  lazy val revealFullText     = application.injector.instanceOf[RevealFullText]
  implicit val mess: Messages = messages

  "OtherIncomeOrGainSourceSummary" - {

    "must return None when the page has no answer" in {
      val userAnswers = UserAnswers("id", "session-123")
      OtherIncomeOrGainSourceSummary.row(userAnswers, revealFullText) mustBe None
    }

    "must return a row when the page has an answer" in {
      val userAnswers = UserAnswers("id", "session-123")
        .set(OtherIncomeOrGainSourcePage, "Some income source")
        .success
        .value

      val result = OtherIncomeOrGainSourceSummary.row(userAnswers, revealFullText)
      result mustBe defined
    }

    "must handle long text that exceeds the reveal threshold" in {
      val longText    = "A" * 200
      val userAnswers = UserAnswers("id", "session-123")
        .set(OtherIncomeOrGainSourcePage, longText)
        .success
        .value

      val result = OtherIncomeOrGainSourceSummary.row(userAnswers, revealFullText)
      result mustBe defined
      result.map(_.value.content.toString must include("..."))
    }
  }
}
