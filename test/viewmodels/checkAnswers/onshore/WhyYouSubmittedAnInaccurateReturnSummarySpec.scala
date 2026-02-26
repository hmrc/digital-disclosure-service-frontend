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

package viewmodels.checkAnswers.onshore

import base.SpecBase
import models.{AreYouTheEntity, RelatesTo, UserAnswers, WhyYouSubmittedAnInaccurateOnshoreReturn}
import pages.{AreYouTheEntityPage, RelatesToPage, WhyYouSubmittedAnInaccurateOnshoreReturnPage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.Key
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text

class WhyYouSubmittedAnInaccurateReturnSummarySpec extends SpecBase {

  lazy val app                = application
  implicit val mess: Messages = messages

  "WhyYouSubmittedAnInaccurateReturnSummary.row" - {

    "must return None when the page has no answer" in {
      val userAnswers = UserAnswers("id", "session-123")
      WhyYouSubmittedAnInaccurateReturnSummary.row(userAnswers) mustBe None
    }

    "must return a row with 'you' message keys when user is the individual" in {
      val answers     =
        Set[WhyYouSubmittedAnInaccurateOnshoreReturn](WhyYouSubmittedAnInaccurateOnshoreReturn.ReasonableMistake)
      val userAnswers = UserAnswers("id", "session-123")
        .set(RelatesToPage, RelatesTo.AnIndividual)
        .success
        .value
        .set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        .success
        .value
        .set(WhyYouSubmittedAnInaccurateOnshoreReturnPage, answers)
        .success
        .value

      WhyYouSubmittedAnInaccurateReturnSummary.row(userAnswers).map { row =>
        row.key mustBe Key(Text(mess("WhyYouSubmittedAnInaccurateReturn.checkYourAnswersLabel")))
        row.value.content.toString must include(mess("WhyYouSubmittedAnInaccurateReturn.you.reasonableMistake"))
      }
    }

    "must return a row with 'notYou' message keys when user is not the individual" in {
      val answers     =
        Set[WhyYouSubmittedAnInaccurateOnshoreReturn](WhyYouSubmittedAnInaccurateOnshoreReturn.DeliberatelyInaccurate)
      val userAnswers = UserAnswers("id", "session-123")
        .set(RelatesToPage, RelatesTo.ACompany)
        .success
        .value
        .set(WhyYouSubmittedAnInaccurateOnshoreReturnPage, answers)
        .success
        .value

      WhyYouSubmittedAnInaccurateReturnSummary.row(userAnswers).map { row =>
        row.key mustBe Key(Text(mess("WhyYouSubmittedAnInaccurateReturn.checkYourAnswersLabel")))
        row.value.content.toString must include(mess("WhyYouSubmittedAnInaccurateReturn.notYou.deliberatelyInaccurate"))
      }
    }

    "must return a row with multiple answers joined by <br>" in {
      val answers     = Set[WhyYouSubmittedAnInaccurateOnshoreReturn](
        WhyYouSubmittedAnInaccurateOnshoreReturn.ReasonableMistake,
        WhyYouSubmittedAnInaccurateOnshoreReturn.NoReasonableCare
      )
      val userAnswers = UserAnswers("id", "session-123")
        .set(RelatesToPage, RelatesTo.AnIndividual)
        .success
        .value
        .set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        .success
        .value
        .set(WhyYouSubmittedAnInaccurateOnshoreReturnPage, answers)
        .success
        .value

      WhyYouSubmittedAnInaccurateReturnSummary.row(userAnswers).map { row =>
        row.value.content.toString must include("<br>")
      }
    }
  }
}
