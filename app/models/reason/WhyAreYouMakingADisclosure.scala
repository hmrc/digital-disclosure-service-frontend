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

sealed trait WhyAreYouMakingADisclosure

object WhyAreYouMakingADisclosure extends Enumerable.Implicits {

  case object GovUkGuidance extends WithName("govUkGuidance") with WhyAreYouMakingADisclosure
  case object LetterFromHMRC extends WithName("letterFromHMRC") with WhyAreYouMakingADisclosure
  case object Employer extends WithName("employer") with WhyAreYouMakingADisclosure
  case object News extends WithName("news") with WhyAreYouMakingADisclosure
  case object Publication extends WithName("publication") with WhyAreYouMakingADisclosure
  case object Accountant extends WithName("accountant") with WhyAreYouMakingADisclosure
  case object ROE extends WithName("roe") with WhyAreYouMakingADisclosure
  case object Other extends WithName("other") with WhyAreYouMakingADisclosure

  val values: Seq[WhyAreYouMakingADisclosure] = Seq(
    GovUkGuidance,
    LetterFromHMRC,
    Employer,
    News,
    Publication,
    Accountant,
    ROE,
    Other
  )

  def checkboxItems(implicit messages: Messages): Seq[CheckboxItem] =
    values.zipWithIndex.map { case (value, index) =>
      CheckboxItemViewModel(
        content = Text(messages(s"whyAreYouMakingADisclosure.${value.toString}")),
        fieldId = "value",
        index = index,
        value = value.toString
      )
    }

  implicit val enumerable: Enumerable[WhyAreYouMakingADisclosure] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
