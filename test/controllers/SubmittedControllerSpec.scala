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
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.SubmittedView
import models._
import pages._

class SubmittedControllerSpec extends SpecBase {

  "Submitted Controller" - {

    "must return OK and the correct view for a GET when tax year is not empty" in {

      val answer = TaxYearLiabilities(
        income = BigInt(1000),
        chargeableTransfers = BigInt(100),
        capitalGains = BigInt(1000),
        unpaidTax = BigInt(200),
        interest = BigInt(20),
        penaltyRate = 30,
        penaltyRateReason = "Reason",
        foreignTaxCredit = true
      )

      val userAnswers = (for {
        ua <- UserAnswers("id").set(TaxYearLiabilitiesPage, Map("2021" -> TaxYearWithLiabilities(TaxYearStarting(2021), answer)))
        updatedUa <- ua.set(LetterReferencePage, "CSFF-12345")  
      } yield updatedUa).success.value 
      
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.SubmittedController.onPageLoad("reference").url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[SubmittedView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(true, false, "reference")(request, messages(application)).toString
      }
    }

    "must return OK and the correct view for a GET when tax year is empty" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.SubmittedController.onPageLoad("reference").url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[SubmittedView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(false, true, "reference")(request, messages(application)).toString
      }
    }
  }
}
