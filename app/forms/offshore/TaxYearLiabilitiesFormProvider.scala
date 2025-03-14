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
import play.api.data.{Form, Mapping}
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import models.TaxYearLiabilities

class TaxYearLiabilitiesFormProvider @Inject() extends Mappings {

  val MAX_BIGINT        = BigInt("999999999999999999999999")
  val MAX_TEXT_BOX_SIZE = 5000

  def apply(): Form[TaxYearLiabilities] = Form(
    mapping(
      "income"                 -> bigintWithPound(
        "taxYearLiabilities.income.error.required",
        "taxYearLiabilities.income.error.wholeNumber",
        "taxYearLiabilities.income.error.nonNumeric"
      )
        .verifying(inRange(BigInt(0), MAX_BIGINT, "taxYearLiabilities.income.error.outOfRange")),
      "chargeableTransfers"    -> bigintWithPound(
        "taxYearLiabilities.chargeableTransfers.error.required",
        "taxYearLiabilities.chargeableTransfers.error.wholeNumber",
        "taxYearLiabilities.chargeableTransfers.error.nonNumeric"
      )
        .verifying(inRange(BigInt(0), MAX_BIGINT, "taxYearLiabilities.chargeableTransfers.error.outOfRange")),
      "capitalGains"           -> bigintWithPound(
        "taxYearLiabilities.capitalGains.error.required",
        "taxYearLiabilities.capitalGains.error.wholeNumber",
        "taxYearLiabilities.capitalGains.error.nonNumeric"
      )
        .verifying(inRange(BigInt(0), MAX_BIGINT, "taxYearLiabilities.capitalGains.error.outOfRange")),
      "unpaidTax"              -> bigintWithPound(
        "taxYearLiabilities.unpaidTax.error.required",
        "taxYearLiabilities.unpaidTax.error.wholeNumber",
        "taxYearLiabilities.unpaidTax.error.nonNumeric"
      )
        .verifying(inRange(BigInt(0), MAX_BIGINT, "taxYearLiabilities.unpaidTax.error.outOfRange")),
      "interest"               -> bigintWithPound(
        "taxYearLiabilities.interest.error.required",
        "taxYearLiabilities.interest.error.wholeNumber",
        "taxYearLiabilities.interest.error.nonNumeric"
      )
        .verifying(inRange(BigInt(0), MAX_BIGINT, "taxYearLiabilities.interest.error.outOfRange")),
      "penaltyRate"            -> decimalWithPercentage(
        "taxYearLiabilities.penaltyRate.error.required",
        "taxYearLiabilities.penaltyRate.error.nonNumeric"
      )
        .verifying(inRange(BigDecimal(0.00), BigDecimal(200.00), "taxYearLiabilities.penaltyRate.error.outOfRange")),
      "penaltyRateReason"      -> text("taxYearLiabilities.penaltyRateReason.error.required")
        .verifying(maxLength(MAX_TEXT_BOX_SIZE, "taxYearLiabilities.penaltyRateReason.error.length"))
        .verifying(validUnicodeCharacters),
      "undeclaredIncomeOrGain" -> stringOptionalUnless("undeclaredIncomeOrGain"),
      "foreignTaxCredit"       -> boolean("taxYearLiabilities.foreignTaxCredit.error.required")
    )(TaxYearLiabilities.apply)(TaxYearLiabilities.unapply)
  )

  def stringOptionalUnless(field: String): Mapping[Option[String]] =
    optional(
      text(s"taxYearLiabilities.$field.error.required")
        .verifying(maxLength(MAX_TEXT_BOX_SIZE, "taxYearLiabilities.undeclaredIncomeOrGain.error.length"))
        .verifying(validUnicodeCharacters)
    )
      .verifying(optionalUnless(true, s"taxYearLiabilities.$field.error.required"))

  def optionalUnless[A](isRequired: Boolean, errorKey: String): Constraint[Option[A]] =
    Constraint[Option[A]] { data: Option[A] =>
      if (data.isDefined || !isRequired) {
        Valid
      } else {
        Invalid(ValidationError(errorKey))
      }
    }
}
