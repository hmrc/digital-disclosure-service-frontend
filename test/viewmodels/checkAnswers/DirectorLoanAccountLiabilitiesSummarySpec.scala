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
import models.{DirectorLoanAccountLiabilities, UserAnswers}
import pages.DirectorLoanAccountLiabilitiesPage
import play.api.i18n.Messages

import java.time.LocalDate

class DirectorLoanAccountLiabilitiesSummarySpec extends SpecBase {

  implicit val mess: Messages = messages

  "DirectorLoanAccountLiabilitiesSummary" - {

    "must return None when the page has no answer" in {
      val userAnswers = UserAnswers("id", "session-123")
      DirectorLoanAccountLiabilitiesSummary.row(userAnswers) mustBe None
    }

    "must return a row when the page has an answer" in {
      val liability   = DirectorLoanAccountLiabilities(
        name = "Test Director",
        periodEnd = LocalDate.of(2021, 4, 5),
        overdrawn = BigInt(1000),
        unpaidTax = BigInt(200),
        interest = BigInt(50),
        penaltyRate = 15.0,
        penaltyRateReason = "Some reason"
      )
      val userAnswers = UserAnswers("id", "session-123")
        .set(DirectorLoanAccountLiabilitiesPage, Seq(liability))
        .success
        .value

      val result = DirectorLoanAccountLiabilitiesSummary.row(userAnswers)
      result mustBe defined
    }
  }
}
