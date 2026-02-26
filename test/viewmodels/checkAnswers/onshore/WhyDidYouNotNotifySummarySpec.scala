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
import models.{AreYouTheEntity, RelatesTo, UserAnswers, WhyDidYouNotNotifyOnshore}
import pages.{AreYouTheEntityPage, RelatesToPage, WhyDidYouNotNotifyOnshorePage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.Key
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text

class WhyDidYouNotNotifySummarySpec extends SpecBase {

  lazy val app                = application
  implicit val mess: Messages = messages

  "WhyDidYouNotNotifySummary.row" - {

    "must return None when the page has no answer" in {
      val userAnswers = UserAnswers("id", "session-123")
      WhyDidYouNotNotifySummary.row(userAnswers) mustBe None
    }

    "must return a row with 'you' message keys when user is the individual" in {
      val answers     = Set[WhyDidYouNotNotifyOnshore](WhyDidYouNotNotifyOnshore.ReasonableExcuseOnshore)
      val userAnswers = UserAnswers("id", "session-123")
        .set(RelatesToPage, RelatesTo.AnIndividual)
        .success
        .value
        .set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        .success
        .value
        .set(WhyDidYouNotNotifyOnshorePage, answers)
        .success
        .value

      WhyDidYouNotNotifySummary.row(userAnswers).map { row =>
        row.key mustBe Key(Text(mess("whyDidYouNotNotify.checkYourAnswersLabel")))
        row.value.content.toString must include(mess("whyDidYouNotNotify.you.reasonableExcuse"))
      }
    }

    "must return a row with 'notYou' message keys when user is not the individual" in {
      val answers     = Set[WhyDidYouNotNotifyOnshore](WhyDidYouNotNotifyOnshore.DeliberatelyDidNotNotifyOnshore)
      val userAnswers = UserAnswers("id", "session-123")
        .set(RelatesToPage, RelatesTo.ACompany)
        .success
        .value
        .set(WhyDidYouNotNotifyOnshorePage, answers)
        .success
        .value

      WhyDidYouNotNotifySummary.row(userAnswers).map { row =>
        row.key mustBe Key(Text(mess("whyDidYouNotNotify.checkYourAnswersLabel")))
        row.value.content.toString must include(mess("whyDidYouNotNotify.notYou.deliberatelyDidNotNotify"))
      }
    }

    "must return a row with multiple answers joined by <br>" in {
      val answers     = Set[WhyDidYouNotNotifyOnshore](
        WhyDidYouNotNotifyOnshore.ReasonableExcuseOnshore,
        WhyDidYouNotNotifyOnshore.NotDeliberatelyNoReasonableExcuseOnshore
      )
      val userAnswers = UserAnswers("id", "session-123")
        .set(RelatesToPage, RelatesTo.AnIndividual)
        .success
        .value
        .set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        .success
        .value
        .set(WhyDidYouNotNotifyOnshorePage, answers)
        .success
        .value

      WhyDidYouNotNotifySummary.row(userAnswers).map { row =>
        row.value.content.toString must include("<br>")
      }
    }
  }
}
