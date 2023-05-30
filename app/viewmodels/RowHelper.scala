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
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import models.CheckMode
import viewmodels.RevealFullText

sealed abstract class RowHelperSection(name:String)
case object ONSHORE extends RowHelperSection("onshore")
case object OFFSHORE extends RowHelperSection("offshore")
case object CT extends RowHelperSection("corporationTax")
case object DL extends RowHelperSection("directorLoan")


trait RowHelper {

  def poundRowCase(i: Int,label: String, value: String, hiddenLabel: String, section: RowHelperSection)(implicit messages: Messages) = {
    section match {
      case ONSHORE => onshorePoundRow(i: Int,label: String, value: String, hiddenLabel: String)
      case OFFSHORE => offshorePoundRow(i: Int,label: String, value: String, hiddenLabel: String)
      case CT => ctPoundRow(i: Int,label: String, value: String, hiddenLabel: String)
      case DL => dlPoundRow(i: Int,label: String, value: String, hiddenLabel: String)
    }
  }

  def rowCase(i: Int,label: String, value: String, hiddenLabel: String, section: RowHelperSection, revealFullText: RevealFullText, isRevealText: Boolean)(implicit messages: Messages) = {
    section match {
      case ONSHORE => if(isRevealText) onshoreRevealRow(i: Int,label: String, value: String, hiddenLabel: String, revealFullText: RevealFullText) else onshoreRow(i: Int,label: String, value: String, hiddenLabel: String)
      case OFFSHORE => if(isRevealText) offshoreRevealRow(i: Int,label: String, value: String, hiddenLabel: String, revealFullText: RevealFullText) else offshoreRow(i: Int,label: String, value: String, hiddenLabel: String)
      case CT => if(isRevealText) ctRevealRow(i: Int,label: String, value: String, hiddenLabel: String, revealFullText: RevealFullText) else ctRow(i: Int,label: String, value: String, hiddenLabel: String)
      case DL => if(isRevealText) dlRevealRow(i: Int,label: String, value: String, hiddenLabel: String, revealFullText: RevealFullText) else dlRow(i: Int,label: String, value: String, hiddenLabel: String)
    }
  }

  private def onshorePoundRow(i: Int, label: String, value: String, hiddenLabel: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(HtmlContent(s"&pound;$value")),
      actions = Seq(
        ActionItemViewModel("site.change", controllers.onshore.routes.OnshoreTaxYearLiabilitiesController.onPageLoad(i, CheckMode).url)
          .withVisuallyHiddenText(messages(hiddenLabel))
      )
    )
  }

  private def offshorePoundRow(i: Int, label: String, value: String, hiddenLabel: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(HtmlContent(s"&pound;$value")),
      actions = Seq(
        ActionItemViewModel("site.change", controllers.offshore.routes.TaxYearLiabilitiesController.onPageLoad(i, CheckMode).url)
          .withVisuallyHiddenText(messages(hiddenLabel))
      )
    )
  }

  private def ctPoundRow(i: Int, label: String, value: String, hiddenLabel: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(HtmlContent(s"&pound;$value")),
      actions = Seq(
        ActionItemViewModel("site.change", controllers.onshore.routes.CorporationTaxLiabilityController.onPageLoad(i, CheckMode).url)
          .withVisuallyHiddenText(messages(hiddenLabel))
      )
    )
  }

  private def dlPoundRow(i: Int, label: String, value: String, hiddenLabel: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(HtmlContent(s"&pound;$value")),
      actions = Seq(
        ActionItemViewModel("site.change", controllers.onshore.routes.DirectorLoanAccountLiabilitiesController.onPageLoad(i, CheckMode).url)
          .withVisuallyHiddenText(messages(hiddenLabel))
      )
    )
  }

  private def onshoreRow(i: Int, label: String, value: String, hiddenLabel: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(Text(value)),
      actions = Seq(
        ActionItemViewModel("site.change", controllers.onshore.routes.OnshoreTaxYearLiabilitiesController.onPageLoad(i, CheckMode).url)
          .withVisuallyHiddenText(messages(hiddenLabel))
      )
    )
  }

  private def onshoreRevealRow(i: Int, label: String, value: String, hiddenLabel: String, revealFullText: RevealFullText)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = s"${label}.checkYourAnswersLabel",
      value   = ValueViewModel(revealFullText.addRevealToText(value, s"${label}.reveal")),
      actions = Seq(
        ActionItemViewModel("site.change", controllers.onshore.routes.OnshoreTaxYearLiabilitiesController.onPageLoad(i, CheckMode).url)
          .withVisuallyHiddenText(messages(hiddenLabel))
      )
    )
  }

  private def offshoreRow(i: Int, label: String, value: String, hiddenLabel: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(Text(value)),
      actions = Seq(
        ActionItemViewModel("site.change", controllers.offshore.routes.TaxYearLiabilitiesController.onPageLoad(i, CheckMode).url)
          .withVisuallyHiddenText(messages(hiddenLabel))
      )
    )
  }

  private def offshoreRevealRow(i: Int, label: String, value: String, hiddenLabel: String, revealFullText: RevealFullText)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = s"${label}.checkYourAnswersLabel",
      value   = ValueViewModel(revealFullText.addRevealToText(value, s"${label}.reveal")),
      actions = Seq(
        ActionItemViewModel("site.change", controllers.offshore.routes.TaxYearLiabilitiesController.onPageLoad(i, CheckMode).url)
          .withVisuallyHiddenText(messages(hiddenLabel))
      )
    )
  }

  private def ctRow(i: Int, label: String, value: String, hiddenLabel: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(Text(value)),
      actions = Seq(
        ActionItemViewModel("site.change", controllers.onshore.routes.CorporationTaxLiabilityController.onPageLoad(i, CheckMode).url)
          .withVisuallyHiddenText(messages(hiddenLabel))
      )
    )
  }

  private def ctRevealRow(i: Int, label: String, value: String, hiddenLabel: String, revealFullText: RevealFullText)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = s"${label}.checkYourAnswersLabel",
      value   = ValueViewModel(revealFullText.addRevealToText(value, s"${label}.reveal", (i + 1).toString)),
      actions = Seq(
        ActionItemViewModel("site.change", controllers.onshore.routes.CorporationTaxLiabilityController.onPageLoad(i, CheckMode).url)
          .withVisuallyHiddenText(messages(hiddenLabel))
      )
    )
  }

  def dlRow(i: Int,label: String, value: String, hiddenLabel: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(Text(value)),
      actions = Seq(
        ActionItemViewModel("site.change", controllers.onshore.routes.DirectorLoanAccountLiabilitiesController.onPageLoad(i, CheckMode).url)
          .withVisuallyHiddenText(messages(hiddenLabel))
      )
    )
  }

  def dlRevealRow(i: Int,label: String, value: String, hiddenLabel: String, revealFullText: RevealFullText)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = s"${label}.checkYourAnswersLabel",
      value   = ValueViewModel(revealFullText.addRevealToText(value, s"${label}.reveal", (i+1).toString)),
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
