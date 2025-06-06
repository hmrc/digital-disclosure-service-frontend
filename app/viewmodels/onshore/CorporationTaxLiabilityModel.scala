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
import java.util.Locale
import viewmodels.implicits._
import viewmodels.SummaryListRowNoValue
import models.{CorporationTaxLiability, Mode}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import play.api.i18n.Messages
import controllers.onshore.routes
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Actions, Key}

object CorporationTaxLiabilityModel {

  def row(corporationTaxLiabilities: Seq[CorporationTaxLiability], mode: Mode)(implicit
    messages: Messages
  ): Seq[SummaryListRowNoValue] = {

    val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale(messages.lang.code))

    (for {
      (corporationTaxLiability, i) <- corporationTaxLiabilities.zipWithIndex
    } yield SummaryListRowNoValue(
      key = Key(
        messages("site.ending") + s" ${corporationTaxLiability.periodEnd.format(dateFormatter)}",
        "govuk-!-font-weight-regular hmrc-summary-list__key"
      ),
      actions = Some(
        Actions(items =
          Seq(
            ActionItem(
              href = routes.CorporationTaxLiabilityController.onPageLoad(i, mode).url,
              content = Text(messages("site.change")),
              visuallyHiddenText = Some(
                messages("corporationTaxLiability.change.hidden") + corporationTaxLiability.periodEnd.format(
                  dateFormatter
                )
              )
            ),
            ActionItem(
              href = routes.AccountingPeriodCTAddedController.remove(i, mode).url,
              content = Text(messages("site.remove")),
              visuallyHiddenText = Some(
                messages("corporationTaxLiability.remove.hidden") + corporationTaxLiability.periodEnd.format(
                  dateFormatter
                )
              )
            )
          )
        )
      )
    )).toSeq
  }
}
