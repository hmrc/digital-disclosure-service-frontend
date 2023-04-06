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

package viewmodels

import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import models.CheckMode

trait RowHelper {

  val ONSHORE = "onshore"
  val OFFSHORE = "offshore"
  val CT = "corporationTax"
  val DL = "directorLoan"
  
  def poundRowCase(i: Int,label: String, value: String, hiddenLabel: String, section: String)(implicit messages: Messages) = {
    section match {
      case ONSHORE => onshorePoundRow(i: Int,label: String, value: String, hiddenLabel: String)
      case OFFSHORE => offshorePoundRow(i: Int,label: String, value: String, hiddenLabel: String)
      case CT => ctPoundRow(i: Int,label: String, value: String, hiddenLabel: String)
      case DL => dlPoundRow(i: Int,label: String, value: String, hiddenLabel: String)
    }
  }

  def rowCase(i: Int,label: String, value: String, hiddenLabel: String, section: String)(implicit messages: Messages) = {
    section match {
      case ONSHORE => onshoreRow(i: Int,label: String, value: String, hiddenLabel: String)
      case OFFSHORE => offshoreRow(i: Int,label: String, value: String, hiddenLabel: String)
      case CT => ctRow(i: Int,label: String, value: String, hiddenLabel: String)
      case DL => dlRow(i: Int,label: String, value: String, hiddenLabel: String)
    }
  }

  def onshorePoundRow(i: Int,label: String, value: String, hiddenLabel: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(HtmlContent(s"&pound;$value")),
      actions = Seq(
        ActionItemViewModel("site.change", controllers.onshore.routes.OnshoreTaxYearLiabilitiesController.onPageLoad(i, CheckMode).url)
          .withVisuallyHiddenText(messages(hiddenLabel))
      )
    )
  }

  def offshorePoundRow(i: Int,label: String, value: String, hiddenLabel: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(HtmlContent(s"&pound;$value")),
      actions = Seq(
        ActionItemViewModel("site.change", controllers.offshore.routes.TaxYearLiabilitiesController.onPageLoad(i, CheckMode).url)
          .withVisuallyHiddenText(messages(hiddenLabel))
      )
    )
  }

  def ctPoundRow(i: Int,label: String, value: String, hiddenLabel: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(HtmlContent(s"&pound;$value")),
      actions = Seq(
        ActionItemViewModel("site.change", controllers.onshore.routes.CorporationTaxLiabilityController.onPageLoad(i, CheckMode).url)
          .withVisuallyHiddenText(messages(hiddenLabel))
      )
    )
  }

  def dlPoundRow(i: Int,label: String, value: String, hiddenLabel: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(HtmlContent(s"&pound;$value")),
      actions = Seq(
        ActionItemViewModel("site.change", controllers.onshore.routes.DirectorLoanAccountLiabilitiesController.onPageLoad(i, CheckMode).url)
          .withVisuallyHiddenText(messages(hiddenLabel))
      )
    )
  }

  def onshoreRow(i: Int,label: String, value: String, hiddenLabel: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(HtmlContent(HtmlFormat.escape(value))),
      actions = Seq(
        ActionItemViewModel("site.change", controllers.onshore.routes.OnshoreTaxYearLiabilitiesController.onPageLoad(i, CheckMode).url)
          .withVisuallyHiddenText(messages(hiddenLabel))
      )
    )
  }

  def offshoreRow(i: Int,label: String, value: String, hiddenLabel: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(HtmlContent(HtmlFormat.escape(value))),
      actions = Seq(
        ActionItemViewModel("site.change", controllers.offshore.routes.TaxYearLiabilitiesController.onPageLoad(i, CheckMode).url)
          .withVisuallyHiddenText(messages(hiddenLabel))
      )
    )
  }

  def ctRow(i: Int,label: String, value: String, hiddenLabel: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(HtmlContent(HtmlFormat.escape(value))),
      actions = Seq(
        ActionItemViewModel("site.change", controllers.onshore.routes.CorporationTaxLiabilityController.onPageLoad(i, CheckMode).url)
          .withVisuallyHiddenText(messages(hiddenLabel))
      )
    )
  }

  def dlRow(i: Int,label: String, value: String, hiddenLabel: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(HtmlContent(HtmlFormat.escape(value))),
      actions = Seq(
        ActionItemViewModel("site.change", controllers.onshore.routes.DirectorLoanAccountLiabilitiesController.onPageLoad(i, CheckMode).url)
          .withVisuallyHiddenText(messages(hiddenLabel))
      )
    )
  }

  def totalRow(label: String, value: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(HtmlContent(s"&pound;$value")),
      actions = Nil
    )
  }

}
