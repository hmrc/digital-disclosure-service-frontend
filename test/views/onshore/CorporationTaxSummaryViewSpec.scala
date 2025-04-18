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

package views.onshore

import base.ViewSpecBase
import play.twirl.api.Html
import support.ViewMatchers
import views.html.onshore.CorporationTaxSummaryView
import models.{NormalMode, UserAnswers}
import viewmodels.onshore.{CorporationTaxLiabilitiesSummaryViewModel, CorporationTaxLiabilitiesSummaryViewModelCreation}
import viewmodels.RevealFullText

class CorporationTaxSummaryViewSpec extends ViewSpecBase with ViewMatchers {

  val page: CorporationTaxSummaryView                      = inject[CorporationTaxSummaryView]
  val revealFullText                                       = inject[RevealFullText]
  val viewModel: CorporationTaxLiabilitiesSummaryViewModel = new CorporationTaxLiabilitiesSummaryViewModelCreation(
    revealFullText
  ).create(UserAnswers("id", "session-123"))(messages)

  private def createView: Html = page(viewModel, NormalMode)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("checkYourAnswers.ct.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("checkYourAnswers.ct.heading")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.continue")
    }

  }

}
