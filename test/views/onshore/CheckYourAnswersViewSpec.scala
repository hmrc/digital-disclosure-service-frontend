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

package views.onshore

import base.ViewSpecBase
import play.twirl.api.Html
import support.ViewMatchers
import views.html.onshore.CheckYourAnswersView
import models.UserAnswers
import viewmodels.onshore.{CheckYourAnswersViewModel, CorporationTaxLiabilitiesSummaryViewModel, DirectorLoanAccountLiabilitiesSummaryViewModel}
import viewmodels.govuk.SummaryListFluency
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import play.api.i18n.Messages

class CheckYourAnswersViewSpec extends ViewSpecBase with ViewMatchers with SummaryListFluency {

  val userAnswers = UserAnswers("id")

  def totalRows(implicit messages: Messages) = SummaryListViewModel(
    rows = Seq(
      SummaryListRowViewModel(
        key     = Key(Text(messages("onshoreTaxYearLiabilities.unpaidTax.total"))),
        value   = ValueViewModel(HtmlContent("&pound;0")),
        actions = Nil
      ),
      SummaryListRowViewModel(
        key     = Key(Text(messages("onshoreTaxYearLiabilities.interest.total"))),
        value   = ValueViewModel(HtmlContent("&pound;0")),
        actions = Nil
      ),
      SummaryListRowViewModel(
        key     = Key(Text(messages("onshoreTaxYearLiabilities.penaltyAmount.total"))),
        value   = ValueViewModel(HtmlContent("&pound;0")),
        actions = Nil
      ),
      SummaryListRowViewModel(
        key     = Key(Text(messages("onshoreTaxYearLiabilities.amountDue.total"))),
        value   = ValueViewModel(HtmlContent("&pound;0")),
        actions = Nil
      )
    )
  )
  val viewModel = CheckYourAnswersViewModel(
    SummaryListViewModel(rows = Nil),
    Nil,
    CorporationTaxLiabilitiesSummaryViewModel(Nil, SummaryListViewModel(rows = Nil), SummaryListViewModel(rows = Nil)),
    DirectorLoanAccountLiabilitiesSummaryViewModel(Nil, SummaryListViewModel(rows = Nil), SummaryListViewModel(rows = Nil)),
    totalRows,
    BigDecimal(0)
  )
  val page: CheckYourAnswersView = inject[CheckYourAnswersView]

  private def createView: Html = page(viewModel, true)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("checkYourAnswers.onshore.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("checkYourAnswers.onshore.heading")
    }

    "have a heading" in {
      view.getElementsByClass("govuk-heading-m").get(0).text() mustBe messages("checkYourAnswers.onshore.subheading.reason")
      view.getElementsByClass("govuk-heading-m").get(1).text() mustBe messages("checkYourAnswers.onshore.total.heading")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.continue")
    }

  }

}