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
import viewmodels.SummaryListRowNoValue
import models.{DirectorLoanAccountLiabilities, Mode}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import play.api.i18n.Messages
import controllers.onshore.routes
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Actions, ActionItem, Key}

object DirectorLoanAccountLiabilityModel {

  val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

  def row(directorLoanAccountLiabilities: Seq[DirectorLoanAccountLiabilities], mode: Mode)(implicit messages: Messages): Seq[SummaryListRowNoValue] = {
    (for {
      (directorLoanAccountLiability, i) <- directorLoanAccountLiabilities.zipWithIndex
    } yield {
      SummaryListRowNoValue(
        key = Key(s"Ending ${directorLoanAccountLiability.periodEnd.format(dateFormatter)}", "govuk-!-font-weight-regular hmrc-summary-list__key"),
        actions = Some(
          Actions(items =
            Seq( 
              ActionItem(
                href = routes.DirectorLoanAccountLiabilitiesController.onPageLoad(i, mode).url,
                content = Text(messages("site.change")),
                visuallyHiddenText = Some(messages("directorLoanAccountLiabilities.change.hidden") + directorLoanAccountLiability.periodEnd.format(dateFormatter))
              ),
              ActionItem(
                href = routes.AccountingPeriodDLAddedController.remove(i, mode).url,
                content = Text(messages("site.remove")),
                visuallyHiddenText = Some(messages("directorLoanAccountLiabilities.remove.hidden") + directorLoanAccountLiability.periodEnd.format(dateFormatter))
              )
            )
          )
        )
      )
    }).toSeq
  }
}
