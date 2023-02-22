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
import models.DirectorLoanAccountLiabilities
import play.api.data.Form
import play.api.data.Forms._

import java.time.{LocalDate, Month}

class DirectorLoanAccountLiabilitiesFormProvider @Inject() extends Mappings {

  val MAX_BIGINT = BigInt("999999999999999999999999")

  def apply(): Form[DirectorLoanAccountLiabilities] = Form(
     mapping(
       "name" -> text("directorLoanAccountLiabilities.name.required")
         .verifying(maxLength(30, "directorLoanAccountLiabilities.name.invalid")),

       "periodEnd" -> localDate(
         "directorLoanAccountLiabilities.periodEnd.error.invalid",
         "directorLoanAccountLiabilities.periodEnd.error.required.all",
         "directorLoanAccountLiabilities.periodEnd.error.required.two",
         "directorLoanAccountLiabilities.periodEnd.error.required",
         "directorLoanAccountLiabilities.periodEnd.error.invalidDay",
         "directorLoanAccountLiabilities.periodEnd.error.invalidMonth")
         .verifying(maxDate(LocalDate.now().minusDays(1), "directorLoanAccountLiabilities.periodEnd.error.invalidFuture"))
         .verifying(minDate(LocalDate.of(1850, Month.JANUARY, 1), "directorLoanAccountLiabilities.periodEnd.error.invalidPastDate")),

      "overdrawn" -> bigint(
        "directorLoanAccountLiabilities.overdrawn.error.required",
        "directorLoanAccountLiabilities.overdrawn.error.wholeNumber",
        "directorLoanAccountLiabilities.overdrawn.error.nonNumeric")
        .verifying(inRange(BigInt(0), MAX_BIGINT, "directorLoanAccountLiabilities.overdrawn.error.outOfRange")),

      "unpaidTax" -> bigint(
        "directorLoanAccountLiabilities.unpaidTax.error.required",
        "directorLoanAccountLiabilities.unpaidTax.error.wholeNumber",
        "directorLoanAccountLiabilities.unpaidTax.error.nonNumeric")
        .verifying(inRange(BigInt(0), MAX_BIGINT, "directorLoanAccountLiabilities.unpaidTax.error.outOfRange")),

      "interest" -> bigint(
        "directorLoanAccountLiabilities.interest.error.required",
        "directorLoanAccountLiabilities.interest.error.wholeNumber",
        "directorLoanAccountLiabilities.interest.error.nonNumeric")
        .verifying(inRange(BigInt(0), MAX_BIGINT, "directorLoanAccountLiabilities.interest.error.outOfRange")),

      "penaltyRate" -> int(
        "directorLoanAccountLiabilities.penaltyRate.error.required",
        "directorLoanAccountLiabilities.penaltyRate.error.wholeNumber",
        "directorLoanAccountLiabilities.penaltyRate.error.nonNumeric")
        .verifying(inRange(0, 200, "directorLoanAccountLiabilities.penaltyRate.error.outOfRange")),

      "penaltyRateReason" -> text("directorLoanAccountLiabilities.penaltyRateReason.error.required")
        .verifying(maxLength(5000, "directorLoanAccountLiabilities.penaltyRateReason.error.length"))

    )(DirectorLoanAccountLiabilities.apply)(DirectorLoanAccountLiabilities.unapply)
   )
 }
