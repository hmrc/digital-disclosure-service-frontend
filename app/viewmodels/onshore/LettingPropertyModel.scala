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

package viewmodels.onshore

import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import models.{LettingProperty, Mode}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow}

object LettingPropertyModel {

  def row(properties: Seq[LettingProperty], mode: Mode)(implicit messages: Messages): Seq[SummaryListRow] = {
    for {
      property <- properties
      address <- property.address
      postcode <- address.postcode
    } yield {
      SummaryListRowViewModel(
        key = Key(s"${address.line1}, ${postcode}").withCssClass("govuk-!-font-weight-regular hmrc-summary-list__key"),
        value = ValueViewModel(""),
        actions = Seq(
          ActionItemViewModel("site.remove", "http://")
            .withCssClass("summary-list-remove-link")
            .withVisuallyHiddenText(messages("propertyLetting.remove.hidden"))
        )
      )
    }
  }
}
