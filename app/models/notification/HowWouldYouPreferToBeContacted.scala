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
import uk.gov.hmrc.govukfrontend.views.viewmodels.hint.Hint
import viewmodels.govuk.checkbox._

sealed trait HowWouldYouPreferToBeContacted

object HowWouldYouPreferToBeContacted extends Enumerable.Implicits {

  case object Email extends WithName("email") with HowWouldYouPreferToBeContacted
  case object Telephone extends WithName("telephone") with HowWouldYouPreferToBeContacted

  val values: Seq[HowWouldYouPreferToBeContacted] = Seq(
    Email,
    Telephone
  )

  def checkboxItems(implicit messages: Messages): Seq[CheckboxItem] =
    values.zipWithIndex.map { case (value, index) =>
      val checkboxItem = CheckboxItemViewModel(
        content = Text(messages(s"howWouldYouPreferToBeContacted.${value.toString}")),
        fieldId = "value",
        index = index,
        value = value.toString
      )
      if (value == Telephone)
        checkboxItem.withHint(Hint(content = Text(messages(s"howWouldYouPreferToBeContacted.telephone.hint"))))
      else checkboxItem
    }

  implicit val enumerable: Enumerable[HowWouldYouPreferToBeContacted] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
