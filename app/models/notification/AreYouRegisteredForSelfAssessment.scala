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
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import models.store.YesNoOrUnsure

import scala.language.implicitConversions

sealed trait AreYouRegisteredForSelfAssessment

object AreYouRegisteredForSelfAssessment extends Enumerable.Implicits {

  case object YesIKnowMyUTR extends WithName("yesIKnowMyUTR") with AreYouRegisteredForSelfAssessment
  case object YesIDontKnowMyUTR extends WithName("yesIDontKnowMyUTR") with AreYouRegisteredForSelfAssessment
  case object No extends WithName("no") with AreYouRegisteredForSelfAssessment

  val values: Seq[AreYouRegisteredForSelfAssessment] = Seq(
    YesIKnowMyUTR,
    YesIDontKnowMyUTR,
    No
  )

  def options(implicit messages: Messages): Seq[RadioItem] = values.zipWithIndex.map { case (value, index) =>
    RadioItem(
      content = Text(messages(s"areYouRegisteredForSelfAssessment.${value.toString}")),
      value = Some(value.toString),
      id = Some(s"value_$index")
    )
  }

  implicit val enumerable: Enumerable[AreYouRegisteredForSelfAssessment] =
    Enumerable(values.map(v => v.toString -> v): _*)

  implicit def fromYesNoOrUnsure(yesNoOrUnsure: YesNoOrUnsure): AreYouRegisteredForSelfAssessment =
    yesNoOrUnsure match {
      case YesNoOrUnsure.Yes    => YesIKnowMyUTR
      case YesNoOrUnsure.Unsure => YesIDontKnowMyUTR
      case YesNoOrUnsure.No     => No
    }

  implicit def toYesNoOrUnsure(
    areYouRegisteredForSelfAssessment: Option[AreYouRegisteredForSelfAssessment]
  ): Option[YesNoOrUnsure] =
    areYouRegisteredForSelfAssessment.map(_ match {
      case YesIKnowMyUTR     => YesNoOrUnsure.Yes
      case YesIDontKnowMyUTR => YesNoOrUnsure.Unsure
      case No                => YesNoOrUnsure.No
    })
}
