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

package forms

import javax.inject.Inject
import forms.mappings.Mappings
import models.CorporationTaxLiability
import play.api.data.Form
import play.api.data.Forms._

import java.time.{LocalDate, Month}

class CorporationTaxLiabilityFormProvider @Inject() extends Mappings {

  val MAX_BIGINT = BigInt("999999999999999999999999")

  def apply(): Form[CorporationTaxLiability] =
    Form(
      mapping(

        "periodEnd" -> localDate(
          invalidKey     = "corporationTaxLiability.periodEnd.error.invalid",
          allRequiredKey = "corporationTaxLiability.periodEnd.error.required.all",
          twoRequiredKey = "corporationTaxLiability.periodEnd.error.required.two",
          requiredKey    = "corporationTaxLiability.periodEnd.error.required",
          invalidDayKey    = "corporationTaxLiability.periodEnd.error.invalidDay",
          invalidMonthKey  = "corporationTaxLiability.periodEnd.error.invalidMonth"
        )
        .verifying(minDate(LocalDate.of(1850, Month.JANUARY, 1), "corporationTaxLiability.periodEnd.error.invalidPastDateOfBirth"))
        .verifying(maxDate(LocalDate.now().minusDays(1), "corporationTaxLiability.periodEnd.error.invalidFutureDateOfBirth")),

        "howMuchIncome" -> bigint(
          "corporationTaxLiability.howMuchIncome.error.required",
          "corporationTaxLiability.howMuchIncome.error.wholeNumber",
          "corporationTaxLiability.howMuchIncome.error.nonNumeric"
        )
        .verifying(inRange(BigInt(0), MAX_BIGINT, "corporationTaxLiability.howMuchIncome.error.outOfRange")),

        "howMuchUnpaid" -> bigint(
          "corporationTaxLiability.howMuchUnpaid.error.required",
          "corporationTaxLiability.howMuchUnpaid.error.wholeNumber",
          "corporationTaxLiability.howMuchUnpaid.error.nonNumeric"
        )
        .verifying(inRange(BigInt(0), MAX_BIGINT, "corporationTaxLiability.howMuchUnpaid.error.outOfRange")),

        "howMuchInterest" -> bigint(
          "corporationTaxLiability.howMuchInterest.error.required",
          "corporationTaxLiability.howMuchInterest.error.wholeNumber",
          "corporationTaxLiability.howMuchInterest.error.nonNumeric"
        )
        .verifying(inRange(BigInt(0), MAX_BIGINT, "corporationTaxLiability.howMuchInterest.error.outOfRange")),

        "penaltyRate" -> int(
          "corporationTaxLiability.penaltyRate.error.required",
          "corporationTaxLiability.penaltyRate.error.wholeNumber",
          "corporationTaxLiability.penaltyRate.error.nonNumeric"
        )
        .verifying(inRange(0, 200, "corporationTaxLiability.penaltyRate.error.outOfRange")),

        "penaltyRateReason" -> text("corporationTaxLiability.penaltyRateReason.error.required")
        .verifying(maxLength(5000, "corporationTaxLiability.penaltyRateReason.error.length"))
        
      )(CorporationTaxLiability.apply)(CorporationTaxLiability.unapply)
    )
}
