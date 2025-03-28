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

package views.otherLiabilities

import base.ViewSpecBase
import models.OtherLiabilitiesSummaryLists
import play.twirl.api.Html
import support.ViewMatchers
import views.html.otherLiabilities.CheckYourAnswersView
import viewmodels.govuk.SummaryListFluency
import viewmodels.implicits._
import uk.gov.hmrc.govukfrontend.views.Aliases.Text

class CheckYourAnswersViewSpec extends ViewSpecBase with ViewMatchers with SummaryListFluency {

  val testRow1 = SummaryListRowViewModel(
    key = "testKey",
    value = ValueViewModel(Text(messages("Test answer"))),
    actions = Seq(
      ActionItemViewModel("site.change", "http://test.url")
    )
  )

  val testRow2 = SummaryListRowViewModel(
    key = "testKey2",
    value = ValueViewModel(Text(messages("Test answer 2"))),
    actions = Seq(
      ActionItemViewModel("site.change", "http://test2.url")
    )
  )

  val testRow3 = SummaryListRowViewModel(
    key = "testKey3",
    value = ValueViewModel(Text(messages("Test answer 3"))),
    actions = Seq(
      ActionItemViewModel("site.change", "http://test3.url")
    )
  )

  val testRow4 = SummaryListRowViewModel(
    key = "testKey4",
    value = ValueViewModel(Text(messages("Test answer 4"))),
    actions = Seq(
      ActionItemViewModel("site.change", "http://test4.url")
    )
  )

  val otherLiabilitiesList       = SummaryListViewModel(rows = Seq(testRow1, testRow2, testRow3, testRow4))
  val list                       = OtherLiabilitiesSummaryLists(otherLiabilitiesList)
  val page: CheckYourAnswersView = inject[CheckYourAnswersView]

  "view" should {

    def createView: Html = page(list)(request, messages)

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("checkYourAnswers.otherLiabilities.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("checkYourAnswers.otherLiabilities.heading")
    }

    "contain a other liabilities summary list" in {
      val otherLiabilitiesList = view.select("#other-liabilities-list").first
      otherLiabilitiesList must haveClass("govuk-summary-list")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").text() mustBe messages("site.continue")
    }

    "have a task list link" in {
      view.getElementById("govuk-button").attr("href") mustBe controllers.routes.TaskListController.onPageLoad.url
    }

  }

}
