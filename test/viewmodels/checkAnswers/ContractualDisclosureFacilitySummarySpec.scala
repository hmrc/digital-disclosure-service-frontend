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
import pages.ContractualDisclosureFacilityPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.Key

class ContractualDisclosureFacilitySummarySpec extends SpecBase {

  lazy val app                = application
  implicit val mess: Messages = messages

  "ContractualDisclosureFacilitySummary.row" - {

    "must return None when the page has no answer" in {
      val userAnswers = UserAnswers("id", "session-123")
      ContractualDisclosureFacilitySummary.row(userAnswers) mustBe None
    }

    "must return a row with 'Yes' when answer is true" in {
      val userAnswers = UserAnswers("id", "session-123")
        .set(ContractualDisclosureFacilityPage, true)
        .success
        .value

      ContractualDisclosureFacilitySummary.row(userAnswers).map { row =>
        row.key mustBe Key(Text(mess("contractualDisclosureFacility.checkYourAnswersLabel")))
        row.value.content.toString must include(mess("Yes"))
      }
    }

    "must return a row with 'No' when answer is false" in {
      val userAnswers = UserAnswers("id", "session-123")
        .set(ContractualDisclosureFacilityPage, false)
        .success
        .value

      ContractualDisclosureFacilitySummary.row(userAnswers).map { row =>
        row.key mustBe Key(Text(mess("contractualDisclosureFacility.checkYourAnswersLabel")))
        row.value.content.toString must include(mess("No"))
      }
    }
  }
}
