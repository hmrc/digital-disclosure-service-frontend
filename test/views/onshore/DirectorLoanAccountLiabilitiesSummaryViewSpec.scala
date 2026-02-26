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

package views.onshore

import base.ViewSpecBase
import models._
import play.twirl.api.Html
import support.ViewMatchers
import views.html.onshore.DirectorLoanAccountLiabilitiesSummaryView
import viewmodels.onshore.DirectorLoanAccountLiabilitiesSummaryViewModelCreation
import pages.DirectorLoanAccountLiabilitiesPage

import java.time.LocalDate

class DirectorLoanAccountLiabilitiesSummaryViewSpec extends ViewSpecBase with ViewMatchers {

  val page: DirectorLoanAccountLiabilitiesSummaryView                           = inject[DirectorLoanAccountLiabilitiesSummaryView]
  val viewModelCreation: DirectorLoanAccountLiabilitiesSummaryViewModelCreation =
    inject[DirectorLoanAccountLiabilitiesSummaryViewModelCreation]

  val directorLoan = DirectorLoanAccountLiabilities(
    name = "Test Director",
    periodEnd = LocalDate.of(2022, 4, 5),
    overdrawn = BigInt(10000),
    unpaidTax = BigInt(2500),
    interest = BigInt(500),
    penaltyRate = 15,
    penaltyRateReason = "Some reason"
  )

  val userAnswers = UserAnswers("id", "session-123")
    .set(DirectorLoanAccountLiabilitiesPage, Seq(directorLoan))
    .get

  private def createView: Html = {
    val viewModel = viewModelCreation.create(userAnswers)(messages)
    page(viewModel, NormalMode)(request, messages)
  }

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("checkYourAnswers.dl.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("checkYourAnswers.dl.heading")
    }

    "contain a subheading for the director loan entry" in {
      view.getElementById("subheading-dl").text() mustBe messages("checkYourAnswers.dl.subheading", 1)
    }

    "contain total subheading" in {
      view.getElementsByClass("govuk-heading-m").text() must include(messages("checkYourAnswers.dl.total.subheading"))
    }

    "display the continue button" in {
      view.getElementById("continue").text() mustBe messages("site.continue")
    }
  }
}
