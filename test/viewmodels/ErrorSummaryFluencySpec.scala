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

package viewmodels

import base.SpecBase
import forms.OnshoreTaxYearLiabilitiesFormProvider
import models.WhatOnshoreLiabilitiesDoYouNeedToDisclose._
import play.api.i18n.Messages
import viewmodels.govuk.ErrorSummaryFluency
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text

class ErrorSummaryFluencySpec extends SpecBase with ErrorSummaryFluency {

    "check all required field errors are in order for onshore tax year liabilities view" in {
        val form = new OnshoreTaxYearLiabilitiesFormProvider()(Set(NonBusinessIncome, BusinessIncome, LettingIncome, Gains))
        lazy val app = applicationBuilder(Some(emptyUserAnswers)).build()
        implicit val message: Messages = messages(app)

        val errorSummary = ErrorSummaryViewModel(
            form.bind(Map(
                ("nonBusinessIncome", ""),
                ("businessIncome", ""),
                ("lettingIncome", ""),
                ("gains", ""),
                ("unpaidTax", ""),
                ("niContributions", ""),
                ("interest", ""),
                ("penaltyRate", ""),
                ("penaltyRateReason", ""),
                ("undeclaredIncomeOrGain", ""),
                ("residentialTaxReduction", "")
            )
        ))(message)
        val errors = errorSummary.errorList.map(_.content)

        errors(0) mustBe Text(message("onshoreTaxYearLiabilities.nonBusinessIncome.error.required"))
        errors(1) mustBe Text(message("onshoreTaxYearLiabilities.businessIncome.error.required"))
        errors(2) mustBe Text(message("onshoreTaxYearLiabilities.lettingIncome.error.required"))
        errors(3) mustBe Text(message("onshoreTaxYearLiabilities.gains.error.required"))
        errors(4) mustBe Text(message("onshoreTaxYearLiabilities.unpaidTax.error.required"))
        errors(5) mustBe Text(message("onshoreTaxYearLiabilities.niContributions.error.required"))
        errors(6) mustBe Text(message("onshoreTaxYearLiabilities.interest.error.required"))
        errors(7) mustBe Text(message("onshoreTaxYearLiabilities.penaltyRate.error.required"))
        errors(8) mustBe Text(message("onshoreTaxYearLiabilities.penaltyRateReason.error.required"))
        errors(9) mustBe Text(message("onshoreTaxYearLiabilities.undeclaredIncomeOrGain.error.required"))
        errors(10) mustBe Text(message("onshoreTaxYearLiabilities.residentialTaxReduction.error.required"))
    }

}