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

package views.letting

import base.ViewSpecBase
import models.LettingSummaryLists
import play.twirl.api.Html
import support.ViewMatchers
import views.html.letting.CheckYourAnswersView
import viewmodels.govuk.SummaryListFluency
import viewmodels.implicits._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent

class CheckYourAnswersViewSpec extends ViewSpecBase with ViewMatchers with SummaryListFluency {

  val testRow1 = SummaryListRowViewModel(
    key     = "testKey",
    value   = ValueViewModel(HtmlContent(HtmlFormat.escape(messages("Test answer")))),
    actions = Seq(
      ActionItemViewModel("site.change", "http://test.url")
    )
  )

  val testRow2 = SummaryListRowViewModel(
    key     = "testKey2",
    value   = ValueViewModel(HtmlContent(HtmlFormat.escape(messages("Test answer 2")))),
    actions = Seq(
      ActionItemViewModel("site.change", "http://test2.url")
    )
  )

  val lettingList = SummaryListViewModel(rows = Seq(testRow1, testRow2))
  val list = LettingSummaryLists(lettingList)
  val page: CheckYourAnswersView = inject[CheckYourAnswersView]

  "view" should {

    def createView: Html = page(list, 0)(request, messages)

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("checkYourAnswers.letting.title", 1))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("checkYourAnswers.letting.heading", 1)
    }

    "contain a letting summary list" in {
      val lettingList = view.select("#letting-list").first
      lettingList must haveClass("govuk-summary-list")
    }

  }

}
