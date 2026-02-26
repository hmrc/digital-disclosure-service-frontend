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

package controllers.onshore

import base.SpecBase
import models.{DirectorLoanAccountLiabilities, NormalMode, UserAnswers}
import pages.DirectorLoanAccountLiabilitiesPage
import play.api.test.FakeRequest
import play.api.test.Helpers._

import java.time.LocalDate

class DirectorLoanAccountLiabilitiesSummaryControllerSpec extends SpecBase {

  lazy val directorLoanSummaryRoute =
    routes.DirectorLoanAccountLiabilitiesSummaryController.onPageLoad(NormalMode).url

  "DirectorLoanAccountLiabilitiesSummaryController" - {

    "must return OK and the correct view for a GET with no liabilities" in {
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, directorLoanSummaryRoute)
      val result  = route(application, request).value

      status(result) mustEqual OK
    }

    "must return OK and the correct view for a GET with existing liabilities" in {
      val liability = DirectorLoanAccountLiabilities(
        name = "Test Director",
        periodEnd = LocalDate.of(2021, 4, 5),
        overdrawn = BigInt(1000),
        unpaidTax = BigInt(200),
        interest = BigInt(50),
        penaltyRate = 15.0,
        penaltyRateReason = "Some reason"
      )
      val userAnswers = UserAnswers("id", "session-123")
        .set(DirectorLoanAccountLiabilitiesPage, Seq(liability)).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, directorLoanSummaryRoute)
      val result  = route(application, request).value

      status(result) mustEqual OK
    }

    "must redirect to Index for a GET if no existing data is found" in {
      setupMockSessionResponse()

      val request = FakeRequest(GET, directorLoanSummaryRoute)
      val result  = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }
  }
}