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
import models.{IncomeOrGainSource, UserAnswers}
import pages.IncomeOrGainSourcePage
import play.api.i18n.Messages

class IncomeOrGainSourceSummarySpec extends SpecBase {

  implicit val mess: Messages = messages

  "IncomeOrGainSourceSummary" - {

    "must return None when the page has no answer" in {
      val userAnswers = UserAnswers("id", "session-123")
      IncomeOrGainSourceSummary.row(userAnswers) mustBe None
    }

    "must return a row with a single selection" in {
      val userAnswers = UserAnswers("id", "session-123")
        .set(IncomeOrGainSourcePage, Set[IncomeOrGainSource](IncomeOrGainSource.Dividends))
        .success
        .value

      val result = IncomeOrGainSourceSummary.row(userAnswers)
      result mustBe defined
    }

    "must return a row with multiple selections" in {
      val userAnswers = UserAnswers("id", "session-123")
        .set(
          IncomeOrGainSourcePage,
          Set[IncomeOrGainSource](
            IncomeOrGainSource.Dividends,
            IncomeOrGainSource.Interest
          )
        )
        .success
        .value

      val result = IncomeOrGainSourceSummary.row(userAnswers)
      result mustBe defined
    }
  }
}
