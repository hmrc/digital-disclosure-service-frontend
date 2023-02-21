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

package models

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.checkbox._
import uk.gov.hmrc.govukfrontend.views.viewmodels.hint.Hint

sealed trait WhatOnshoreLiabilitiesDoYouNeedToDisclose

object WhatOnshoreLiabilitiesDoYouNeedToDisclose extends Enumerable.Implicits {

  case object BusinessIncome extends WithName("businessIncomeLiabilities") with WhatOnshoreLiabilitiesDoYouNeedToDisclose
  case object Gains extends WithName("capitalGainsTaxLiabilities") with WhatOnshoreLiabilitiesDoYouNeedToDisclose
  case object CorporationTax extends WithName("company.corporationTaxLiabilities") with WhatOnshoreLiabilitiesDoYouNeedToDisclose
  case object DirectorLoan extends WithName("company.directorLoanLiabilities") with WhatOnshoreLiabilitiesDoYouNeedToDisclose
  case object LettingIncome extends WithName("lettingIncomeFromResidential") with WhatOnshoreLiabilitiesDoYouNeedToDisclose
  case object NonBusinessIncome extends WithName("nonBusinessIncome") with WhatOnshoreLiabilitiesDoYouNeedToDisclose

  val values: Seq[WhatOnshoreLiabilitiesDoYouNeedToDisclose] = Seq(
    BusinessIncome,
    Gains,
    CorporationTax,
    DirectorLoan,
    LettingIncome,
    NonBusinessIncome
  )

  def checkboxItems(isUserCompany: Boolean)(implicit messages: Messages): Seq[CheckboxItem] = {
    val updatedValues = isUserCompany match {
      case true => values
      case false => values.patch(2, Nil, 2)
    }

    updatedValues.zipWithIndex.map {
      case (value, index) =>
        CheckboxItemViewModel(
          content = Text(messages(s"whatOnshoreLiabilitiesDoYouNeedToDisclose.${value.toString}")),
          fieldId = "value",
          index   = index,
          value   = value.toString
        ).withHint(Hint(content = Text(messages(s"whatOnshoreLiabilitiesDoYouNeedToDisclose.${value.toString}.hint"))))
    }
  }

  implicit val enumerable: Enumerable[WhatOnshoreLiabilitiesDoYouNeedToDisclose] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
