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
import models.{OnshoreTaxYearLiabilities, WhatOnshoreLiabilitiesDoYouNeedToDisclose}

class OnshoreTaxYearLiabilitiesFormProvider @Inject() extends Mappings {

  val MAX_BIGINT = BigInt("999999999999999999999999")

  def apply(taxTypes: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose]): Form[OnshoreTaxYearLiabilities] = Form(
    mapping(
      "nonBusinessIncome"       -> bigintOptionalUnless(
        "nonBusinessIncome",
        taxTypes.contains(WhatOnshoreLiabilitiesDoYouNeedToDisclose.NonBusinessIncome)
      ),
      "businessIncome"          -> bigintOptionalUnless(
        "businessIncome",
        taxTypes.contains(WhatOnshoreLiabilitiesDoYouNeedToDisclose.BusinessIncome)
      ),
      "lettingIncome"           -> bigintOptionalUnless(
        "lettingIncome",
        taxTypes.contains(WhatOnshoreLiabilitiesDoYouNeedToDisclose.LettingIncome)
      ),
      "gains"                   -> bigintOptionalUnless("gains", taxTypes.contains(WhatOnshoreLiabilitiesDoYouNeedToDisclose.Gains)),
      "unpaidTax"               -> bigintWithPound(
        "onshoreTaxYearLiabilities.unpaidTax.error.required",
        "onshoreTaxYearLiabilities.unpaidTax.error.wholeNumber",
        "onshoreTaxYearLiabilities.unpaidTax.error.nonNumeric"
      )
        .verifying(inRange(BigInt(0), MAX_BIGINT, "onshoreTaxYearLiabilities.unpaidTax.error.outOfRange")),
      "niContributions"         -> bigintWithPound(
        "onshoreTaxYearLiabilities.niContributions.error.required",
        "onshoreTaxYearLiabilities.niContributions.error.wholeNumber",
        "onshoreTaxYearLiabilities.niContributions.error.nonNumeric"
      )
        .verifying(inRange(BigInt(0), MAX_BIGINT, "onshoreTaxYearLiabilities.niContributions.error.outOfRange")),
      "interest"                -> bigintWithPound(
        "onshoreTaxYearLiabilities.interest.error.required",
        "onshoreTaxYearLiabilities.interest.error.wholeNumber",
        "onshoreTaxYearLiabilities.interest.error.nonNumeric"
      )
        .verifying(inRange(BigInt(0), MAX_BIGINT, "onshoreTaxYearLiabilities.interest.error.outOfRange")),
      "penaltyRate"             -> decimalWithPercentage(
        "onshoreTaxYearLiabilities.penaltyRate.error.required",
        "onshoreTaxYearLiabilities.penaltyRate.error.nonNumeric"
      )
        .verifying(
          inRange(BigDecimal(0.00), BigDecimal(200.00), "onshoreTaxYearLiabilities.penaltyRate.error.outOfRange")
        ),
      "penaltyRateReason"       -> text("onshoreTaxYearLiabilities.penaltyRateReason.error.required")
        .verifying(maxLength(5000, "onshoreTaxYearLiabilities.penaltyRateReason.error.length"))
        .verifying(validUnicodeCharacters),
      "undeclaredIncomeOrGain"  -> stringOptionalUnless("undeclaredIncomeOrGain"),
      "residentialTaxReduction" -> optional(boolean("onshoreTaxYearLiabilities.residentialTaxReduction.error.required"))
        .verifying(
          optionalUnless(
            taxTypes.contains(WhatOnshoreLiabilitiesDoYouNeedToDisclose.LettingIncome),
            "onshoreTaxYearLiabilities.residentialTaxReduction.error.required"
          )
        )
    )(OnshoreTaxYearLiabilities.apply)(OnshoreTaxYearLiabilities.unapply)
  )

  def bigintOptionalUnless(field: String, isRequired: Boolean): Mapping[Option[BigInt]] =
    optional(
      bigintWithPound(
        s"onshoreTaxYearLiabilities.$field.error.required",
        s"onshoreTaxYearLiabilities.$field.error.wholeNumber",
        s"onshoreTaxYearLiabilities.$field.error.nonNumeric"
      )
        .verifying(inRange(BigInt(0), MAX_BIGINT, s"onshoreTaxYearLiabilities.$field.error.outOfRange"))
    )
      .verifying(optionalUnless(isRequired, s"onshoreTaxYearLiabilities.$field.error.required"))

  def stringOptionalUnless(field: String): Mapping[Option[String]] =
    optional(
      text(s"onshoreTaxYearLiabilities.$field.error.required")
        .verifying(maxLength(5000, "onshoreTaxYearLiabilities.undeclaredIncomeOrGain.error.length"))
        .verifying(validUnicodeCharacters)
    )
      .verifying(optionalUnless(true, s"onshoreTaxYearLiabilities.$field.error.required"))

  def optionalUnless[A](isRequired: Boolean, errorKey: String): Constraint[Option[A]] =
    Constraint[Option[A]] { data: Option[A] =>
      if (data.isDefined || !isRequired) {
        Valid
      } else {
        Invalid(ValidationError(errorKey))
      }
    }
}
