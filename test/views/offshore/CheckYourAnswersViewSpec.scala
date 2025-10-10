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

package views.offshore

import base.ViewSpecBase
import play.twirl.api.Html
import support.ViewMatchers
import views.html.offshore.CheckYourAnswersView
import models.UserAnswers
import viewmodels.offshore.CheckYourAnswersViewModel
import viewmodels.govuk.SummaryListFluency
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import play.api.i18n.Messages

class CheckYourAnswersViewSpec extends ViewSpecBase with ViewMatchers with SummaryListFluency {

  val userAnswers = UserAnswers("id", "session-123")

  def totalRows(implicit messages: Messages) = SummaryListViewModel(
    rows = Seq(
      SummaryListRowViewModel(
        key = Key(Text(messages("taxYearLiabilities.unpaidTax.total"))),
        value = ValueViewModel(HtmlContent("&pound;0")),
        actions = Nil
      ),
      SummaryListRowViewModel(
        key = Key(Text(messages("taxYearLiabilities.interest.total"))),
        value = ValueViewModel(HtmlContent("&pound;0")),
        actions = Nil
      ),
      SummaryListRowViewModel(
        key = Key(Text(messages("taxYearLiabilities.penaltyAmount.total"))),
        value = ValueViewModel(HtmlContent("&pound;0")),
        actions = Nil
      ),
      SummaryListRowViewModel(
        key = Key(Text(messages("taxYearLiabilities.amountDue.total"))),
        value = ValueViewModel(HtmlContent("&pound;0")),
        actions = Nil
      )
    )
  )
  val viewModel                              = CheckYourAnswersViewModel(
    SummaryListViewModel(rows = Nil),
    SummaryListViewModel(rows = Nil),
    Nil,
    totalRows,
    0
  )
  val page: CheckYourAnswersView             = inject[CheckYourAnswersView]

  private def createView: Html = page(viewModel, true, false)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("checkYourAnswers.offshore.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("checkYourAnswers.offshore.heading")
    }

    "have a heading" in {
      view.getElementsByClass("govuk-heading-m").get(0).text() mustBe messages(
        "checkYourAnswers.offshore.subheading.reason"
      )
      view.getElementsByClass("govuk-heading-m").get(1).text() mustBe messages(
        "checkYourAnswers.offshore.total.heading"
      )
      view.getElementsByClass("govuk-heading-m").get(2).text() mustBe messages(
        "checkYourAnswers.yourLegalInterpretation.heading"
      )
      view.getElementsByClass("govuk-heading-m").get(3).text() mustBe messages(
        "checkYourAnswers.offshore.offer.heading"
      )
    }

    "have a first offer paragraph" in {
      view.getElementById("offer-paragraph-1").text() mustBe messages(
        "checkYourAnswers.offshore.offer.paragraph1",
        viewModel.liabilitiesTotal
      )
    }

    "have a second offer paragraph" in {
      view.getElementById("offer-paragraph-2").text() mustBe messages("checkYourAnswers.offshore.offer.paragraph2")
    }

    "have a fullAmount heading" in {
      view.getElementsByClass("govuk-heading-m").get(4).text() mustBe messages(
        "checkYourAnswers.offshore.fullAmount.heading"
      )
    }

    "have a first fullAmount paragraph" in {
      view.getElementById("fullAmount-paragraph-1").text() mustBe messages(
        "checkYourAnswers.offshore.fullAmount.paragraph1",
        viewModel.liabilitiesTotal
      ) + messages("checkYourAnswers.offshore.fullAmount.link") + messages("site.dot")
    }

    "have a second fullAmount paragraph" in {
      view.getElementById("fullAmount-paragraph-2").text() mustBe messages(
        "checkYourAnswers.offshore.fullAmount.paragraph2"
      )
    }

    "display the continue button" in {
      view.getElementById("continue").text() mustBe messages("site.continue")
    }

  }

}
