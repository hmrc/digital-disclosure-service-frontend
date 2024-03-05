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

package viewmodels.checkAnswers

import base.SpecBase
import pages._
import models._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.Key
import viewmodels.govuk.summarylist._

class LiabilitiesSummarySpec extends SpecBase {

  lazy val app = applicationBuilder(Some(emptyUserAnswers)).build()
  implicit val mess: Messages = messages(app)

  "LiabilitiesSummary.row" - {

    "must return a row where the user selects Yes for offshore liabilities" in {
      val userAnswers = for {
        uaWithOffshore <- UserAnswers("id", "session-123").set(OffshoreLiabilitiesPage, true)
        uaWithOnshore  <- uaWithOffshore.set(OnshoreLiabilitiesPage, false)
      } yield uaWithOnshore
      
      LiabilitiesSummary.row(userAnswers.success.value).map { row =>
        row.key mustBe Key(Text(mess("liabilities.checkYourAnswersLabel")))
        row.value mustBe ValueViewModel(Text(mess("liabilities.offshore")))
      }
    }

    "must return a row where the user selects No for offshore liabilities" in {
      val userAnswers = for {
        uaWithOffshore <- UserAnswers("id", "session-123").set(OffshoreLiabilitiesPage, false)
        uaWithOnshore  <- uaWithOffshore.set(OnshoreLiabilitiesPage, true)
      } yield uaWithOnshore

      LiabilitiesSummary.row(userAnswers.success.value).map { row => 
        row.key mustBe Key(Text(mess("liabilities.checkYourAnswersLabel")))
        row.value mustBe ValueViewModel(Text(mess("liabilities.onshore")))
      }
    }

    "must return a row where the user selects Yes for offshore liabilities & Yes for onshore liabilities" in {
      val userAnswers = for {
        uaWithOffshore <- UserAnswers("id", "session-123").set(OffshoreLiabilitiesPage, true)
        uaWithOnshore  <- uaWithOffshore.set(OnshoreLiabilitiesPage, true)
      } yield uaWithOnshore

      LiabilitiesSummary.row(userAnswers.success.value).map { row => 
        row.key mustBe Key(Text(mess("liabilities.checkYourAnswersLabel")))
        row.value mustBe ValueViewModel(Text(mess("liabilities.offshoreOnshore")))
      }
    }

  }
  
}
