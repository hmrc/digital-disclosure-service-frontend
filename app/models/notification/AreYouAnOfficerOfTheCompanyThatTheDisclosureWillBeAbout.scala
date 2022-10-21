/*
 * Copyright 2022 HM Revenue & Customs
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
import viewmodels.govuk.HintFluency

sealed trait AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAbout

object AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAbout extends Enumerable.Implicits with HintFluency {

  case object Yes extends WithName("yes") with AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAbout
  case object No extends WithName("no") with AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAbout

  val values: Seq[AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAbout] = Seq(
    Yes, No
  )

  def options(implicit messages: Messages): Seq[RadioItem] = Seq(
    RadioItem(
      content = Text(messages(s"areYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAbout.${Yes.toString}")),
      value   = Some(Yes.toString),
      id      = Some("value_0")
    ),
    RadioItem(
      content = Text(messages(s"areYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAbout.${No.toString}")),
      value   = Some(No.toString),
      id      = Some("value_1"),
      hint    = Some(HintViewModel(Text(messages(s"areYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAbout.hint.no"))))
    )
  )

  implicit val enumerable: Enumerable[AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAbout] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
