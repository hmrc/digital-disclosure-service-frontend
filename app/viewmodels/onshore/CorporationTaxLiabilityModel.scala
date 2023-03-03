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

import java.time.format.DateTimeFormatter

import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import models.{CorporationTaxLiability, Mode}
import play.api.i18n.Messages
import controllers.onshore.routes
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow}

object CorporationTaxLiabilityModel {

  val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

  def row(corporationTaxLiabilities: Set[CorporationTaxLiability], mode: Mode)(implicit messages: Messages): Seq[SummaryListRow] = {
    (for {
      (corporationTaxLiability, i) <- corporationTaxLiabilities.zipWithIndex
    } yield {
      SummaryListRowViewModel(
        key = Key(s"Ending ${corporationTaxLiability.periodEnd.format(dateFormatter)}").withCssClass("govuk-!-font-weight-regular hmrc-summary-list__key govuk-summary-list__only_key"),
        value = ValueViewModel(""),
        actions = Seq(
          ActionItemViewModel("site.remove", routes.AccountingPeriodCTAddedController.remove(i, mode).url)
            .withCssClass("summary-list-remove-link")
            .withVisuallyHiddenText(messages("propertyLetting.remove.hidden"))
        )
      )
    }).toSeq
  }
}
