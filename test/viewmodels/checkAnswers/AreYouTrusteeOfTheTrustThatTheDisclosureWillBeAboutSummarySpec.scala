/*
 * Copyright 2022 HM Revenue & Customs
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
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.Key
import viewmodels.govuk.summarylist._

class AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutSummarySpec extends SpecBase {

  lazy val app = applicationBuilder(Some(emptyUserAnswers)).build()
  implicit val mess = messages(app)

  "AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutSummary.row" - {

    "must return a row where the user selects Yes for offshore liabilities" in {
      val ua = UserAnswers("id").set(AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage, true).success.value

      AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutSummary.row(ua).map { row =>
        row.key mustBe Key(Text(mess("areYouTrusteeOfTheTrustThatTheDisclosureWillBeAbout.checkYourAnswersLabel")))
        row.value mustBe ValueViewModel(HtmlContent(HtmlFormat.escape(mess("areYouTrusteeOfTheTrustThatTheDisclosureWillBeAbout.yes"))))
      }
    }

    "must return a row where the user selects No for offshore liabilities" in {
      val ua = UserAnswers("id").set(AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage, false).success.value

      AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutSummary.row(ua).map { row => 
        row.key mustBe Key(Text(mess("areYouTrusteeOfTheTrustThatTheDisclosureWillBeAbout.checkYourAnswersLabel")))
        row.value mustBe ValueViewModel(HtmlContent(HtmlFormat.escape(mess("areYouTrusteeOfTheTrustThatTheDisclosureWillBeAbout.no"))))
      }
    }


  }
  
}
