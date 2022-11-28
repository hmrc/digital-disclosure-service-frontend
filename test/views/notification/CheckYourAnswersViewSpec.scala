/*
 * Copyright 2022 HM Revenue & Customs
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

package views.notification

import base.ViewSpecBase
import models.SummaryLists
import play.twirl.api.Html
import support.ViewMatchers
import views.html.notification.CheckYourAnswersView
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

  val testRow3 = SummaryListRowViewModel(
    key     = "testKey3",
    value   = ValueViewModel(HtmlContent(HtmlFormat.escape(messages("Test answer 3")))),
    actions = Seq(
      ActionItemViewModel("site.change", "http://test3.url")
    )
  )

  val backgroundList = SummaryListViewModel(rows = Seq(testRow1, testRow2))
  val aboutYouList = SummaryListViewModel(rows = Seq(testRow3))
  val aboutTheIndividualList = Some(SummaryListViewModel(rows = Seq(testRow1, testRow2)))
  val aboutTheCompanyList = Some(SummaryListViewModel(rows = Seq(testRow1, testRow2)))
  val aboutTheLLPList = Some(SummaryListViewModel(rows = Seq(testRow1, testRow2)))
  val aboutTheTrustList = Some(SummaryListViewModel(rows = Seq(testRow1, testRow2)))
  val aboutThePersonWhoDiedList = Some(SummaryListViewModel(rows = Seq(testRow1, testRow2)))
  val list = SummaryLists(backgroundList, aboutYouList, aboutTheIndividualList, aboutTheCompanyList, aboutTheLLPList, aboutTheTrustList, aboutThePersonWhoDiedList)
  val page: CheckYourAnswersView = inject[CheckYourAnswersView]

  private def createView: Html = page(list)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("checkYourAnswers.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("checkYourAnswers.heading")
    }

    "contain background header" in {
      view.getElementsByClass("govuk-heading-l").get(0).text() mustBe messages("notificationCYA.background")
      view.getElementsByClass("govuk-heading-l").get(1).text() mustBe messages("notificationCYA.aboutYou")
      view.getElementsByClass("govuk-heading-l").get(2).text() mustBe messages("notificationCYA.aboutTheIndividual")
      view.getElementsByClass("govuk-heading-l").get(3).text() mustBe messages("notificationCYA.aboutTheCompany")
      view.getElementsByClass("govuk-heading-l").get(4).text() mustBe messages("notificationCYA.aboutTheLLP")
      view.getElementsByClass("govuk-heading-l").get(5).text() mustBe messages("notificationCYA.aboutTheTrust")
      view.getElementsByClass("govuk-heading-l").get(6).text() mustBe messages("notificationCYA.aboutThePersonWhoDied")
    }

    "contain a background summary list" in {
      val backgroundList = view.select("#background-list").first
      backgroundList must haveClass("govuk-summary-list")
    }

    "contain an about you summary list" in {
      val backgroundList = view.select("#about-you-list").first
      backgroundList must haveClass("govuk-summary-list")
    }

    "contain a about the individual summary list" in {
      val aboutTheIndividualList = view.select("#about-the-individual-list").first
      aboutTheIndividualList must haveClass("govuk-summary-list")
    }

    "contain a about the company summary list" in {
      val aboutTheCompanyList = view.select("#about-the-company-list").first
      aboutTheCompanyList must haveClass("govuk-summary-list")
    }

    "contain a about the llp summary list" in {
      val aboutTheLLPList = view.select("#about-the-llp-list").first
      aboutTheLLPList must haveClass("govuk-summary-list")
    }

    "contain a about the trust summary list" in {
      val aboutTheTrustList = view.select("#about-the-trust-list").first
      aboutTheTrustList must haveClass("govuk-summary-list")
    }

    "contain a about the person who died summary list" in {
      val aboutTheTrustList = view.select("#about-the-person-who-died-list").first
      aboutTheTrustList must haveClass("govuk-summary-list")
    }

    "contain submit section header" in {
      view.getElementsByClass("govuk-heading-l").get(7).text() mustBe messages("notificationCYA.send.heading")
    }

    "have a first submit section paragraph" in {
      view.getElementById("first-paragraph").text() mustBe messages("notificationCYA.body1")
    }

    "have a second submit section paragraph" in {
      view.getElementById("second-paragraph").text() mustBe s"${messages("notificationCYA.body2")} ${messages("notificationCYA.link")}"
    }

    "display the send button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("send-button")
      view.getElementsByClass("govuk-button").text() mustBe messages("notificationCYA.send.button")
    }

  }

}
