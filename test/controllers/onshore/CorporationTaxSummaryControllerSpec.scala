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

package controllers

import base.SpecBase
import models.{NormalMode, UserAnswers}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.onshore.{CorporationTaxLiabilitiesSummaryViewModel, CorporationTaxLiabilitiesSummaryViewModelCreation}
import views.html.onshore.CorporationTaxSummaryView
import viewmodels.RevealFullText

class CorporationTaxSummaryControllerSpec extends SpecBase {

  val mode = NormalMode
  val userAnswers = UserAnswers(userAnswersId, "session-123")

  val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
  val revealFullText = application.injector.instanceOf[RevealFullText]

  "CorporationTaxSummary Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, onshore.routes.CorporationTaxSummaryController.onPageLoad(mode).url)
        val viewModel: CorporationTaxLiabilitiesSummaryViewModel = new CorporationTaxLiabilitiesSummaryViewModelCreation(revealFullText).create(userAnswers)(messages(application))

        val result = route(application, request).value

        val view = application.injector.instanceOf[CorporationTaxSummaryView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(viewModel, mode)(request, messages(application)).toString
      }
    }
  }
}
