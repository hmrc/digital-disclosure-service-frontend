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

package controllers.onshore

import base.SpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.onshore.CheckYourAnswersView
import viewmodels.onshore.{CheckYourAnswersViewModel, CorporationTaxLiabilitiesSummaryViewModel, DirectorLoanAccountLiabilitiesSummaryViewModel}
import viewmodels.govuk.SummaryListFluency
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import play.api.i18n.Messages

class CheckYourAnswersControllerSpec extends SpecBase with SummaryListFluency {

  "CheckYourAnswers Controller" - {

    "must return OK and the correct view for a GET" in {

      def totalRows(implicit messages: Messages) = SummaryListViewModel(
        rows = Seq(
          SummaryListRowViewModel(
            key = Key(Text(messages("onshoreTaxYearLiabilities.unpaidTax.total"))),
            value = ValueViewModel(HtmlContent("&pound;0")),
            actions = Nil
          ),
          SummaryListRowViewModel(
            key = Key(Text(messages("onshoreTaxYearLiabilities.niContributions.total"))),
            value = ValueViewModel(HtmlContent("&pound;0")),
            actions = Nil
          ),
          SummaryListRowViewModel(
            key = Key(Text(messages("onshoreTaxYearLiabilities.interest.total"))),
            value = ValueViewModel(HtmlContent("&pound;0")),
            actions = Nil
          ),
          SummaryListRowViewModel(
            key = Key(Text(messages("onshoreTaxYearLiabilities.penaltyAmount.total"))),
            value = ValueViewModel(HtmlContent(s"&pound;${messages("site.2DP", 0)}")),
            actions = Nil
          ),
          SummaryListRowViewModel(
            key = Key(Text(messages("onshoreTaxYearLiabilities.amountDue.total"))),
            value = ValueViewModel(HtmlContent(s"&pound;${messages("site.2DP", 0)}")),
            actions = Nil
          )
        )
      )

      setupMockSessionResponse(Some(emptyUserAnswers))
      implicit val mess: Messages = messages

      val viewmodel = CheckYourAnswersViewModel(
        SummaryListViewModel(rows = Nil),
        Nil,
        CorporationTaxLiabilitiesSummaryViewModel(
          Nil,
          SummaryListViewModel(rows = Nil),
          SummaryListViewModel(rows = Nil)
        ),
        DirectorLoanAccountLiabilitiesSummaryViewModel(
          Nil,
          SummaryListViewModel(rows = Nil),
          SummaryListViewModel(rows = Nil)
        ),
        totalRows,
        BigDecimal(0)
      )

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad.url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckYourAnswersView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(viewmodel, false, false)(request, messages).toString
    }
  }
}
