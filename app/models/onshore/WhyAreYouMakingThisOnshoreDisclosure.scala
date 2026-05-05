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

sealed trait WhyAreYouMakingThisOnshoreDisclosure

object WhyAreYouMakingThisOnshoreDisclosure extends Enumerable.Implicits {

  case object DidNotNotifyHMRC extends WithName("didNotNotifyHMRC") with WhyAreYouMakingThisOnshoreDisclosure
  case object DidNotFile extends WithName("didNotFile") with WhyAreYouMakingThisOnshoreDisclosure
  case object InaccurateReturn extends WithName("inaccurateReturn") with WhyAreYouMakingThisOnshoreDisclosure

  case object DidNotNotifyHasExcuse extends WithName("didNotNotifyHasExcuse") with WhyAreYouMakingThisOnshoreDisclosure
  case object InaccurateReturnWithCare
      extends WithName("inaccurateReturnWithCare")
      with WhyAreYouMakingThisOnshoreDisclosure
  case object NotFileHasExcuse extends WithName("notFileHasExcuse") with WhyAreYouMakingThisOnshoreDisclosure
  case object InaccurateReturnNoCare
      extends WithName("inaccurateReturnNoCare")
      with WhyAreYouMakingThisOnshoreDisclosure
  case object DidNotNotifyNoExcuse extends WithName("didNotNotifyNoExcuse") with WhyAreYouMakingThisOnshoreDisclosure
  case object DeliberatelyDidNotNotify
      extends WithName("deliberatelyDidNotNotify")
      with WhyAreYouMakingThisOnshoreDisclosure
  case object DeliberateInaccurateReturn
      extends WithName("deliberateInaccurateReturn")
      with WhyAreYouMakingThisOnshoreDisclosure
  case object DeliberatelyDidNotFile
      extends WithName("deliberatelyDidNotFile")
      with WhyAreYouMakingThisOnshoreDisclosure
  case object DidNotFileNoExcuse extends WithName("didNotFileNoExcuse") with WhyAreYouMakingThisOnshoreDisclosure

  val page1Values: Seq[WhyAreYouMakingThisOnshoreDisclosure] = Seq(
    DidNotNotifyHMRC,
    DidNotFile,
    InaccurateReturn
  )

  val values: Seq[WhyAreYouMakingThisOnshoreDisclosure] = Seq(
    DidNotNotifyHMRC,
    DidNotFile,
    InaccurateReturn,
    DidNotNotifyHasExcuse,
    InaccurateReturnWithCare,
    NotFileHasExcuse,
    InaccurateReturnNoCare,
    DidNotNotifyNoExcuse,
    DeliberatelyDidNotNotify,
    DeliberateInaccurateReturn,
    DeliberatelyDidNotFile,
    DidNotFileNoExcuse
  )

  def checkboxItems(areTheyTheIndividual: Boolean, entity: RelatesTo)(implicit messages: Messages): Seq[CheckboxItem] =
    page1Values.zipWithIndex.map { case (value, index) =>
      CheckboxItemViewModel(
        content = Text(messages(constructMessageKey(value, areTheyTheIndividual, entity))),
        fieldId = "value",
        index = index,
        value = value.toString
      )
    }

  def constructMessageKey(
    value: WhyAreYouMakingThisOnshoreDisclosure,
    areTheyTheIndividual: Boolean,
    entity: RelatesTo
  ) =
    if (areTheyTheIndividual) {
      s"whyAreYouMakingThisDisclosure.you.${value.toString}"
    } else {
      s"whyAreYouMakingThisDisclosure.${entity.toString}.${value.toString}"
    }

  implicit val enumerable: Enumerable[WhyAreYouMakingThisOnshoreDisclosure] =
    Enumerable(values.map(v => v.toString -> v)*)
}
