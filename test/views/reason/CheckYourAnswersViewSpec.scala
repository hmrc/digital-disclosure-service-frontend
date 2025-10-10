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

package views.reason

import base.ViewSpecBase
import play.twirl.api.Html
import support.ViewMatchers
import views.html.reason.CheckYourAnswersView
import viewmodels.reason.CheckYourAnswersViewModel
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._

class CheckYourAnswersViewSpec extends ViewSpecBase with ViewMatchers {

  val page: CheckYourAnswersView = inject[CheckYourAnswersView]

  private def createView(viewModel: CheckYourAnswersViewModel): Html = page(viewModel)(request, messages)

  "view where the advice section wasn't entered" should {

    val viewModel = CheckYourAnswersViewModel(SummaryList(rows = Nil), None)
    val view      = createView(viewModel)

    "have title" in {
      view.select("title").text() must include(messages("checkYourAnswers.reason.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("checkYourAnswers.reason.heading")
    }

    "contain subheading for list 1" in {
      view.getElementsByClass("govuk-heading-l").text() mustBe messages("checkYourAnswers.reason.section1.heading")
    }

    "display the continue button" in {
      view.getElementById("continue").text() mustBe messages("site.continue")
    }

  }

  "view where the advice section was entered" should {

    val viewModel = CheckYourAnswersViewModel(SummaryList(rows = Nil), Some(SummaryList(rows = Nil)))
    val view      = createView(viewModel)

    "have title" in {
      view.select("title").text() must include(messages("checkYourAnswers.reason.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("checkYourAnswers.reason.heading")
    }

    "contain subheading for list 1" in {
      view.getElementsByClass("govuk-heading-l").get(0).text() mustBe messages(
        "checkYourAnswers.reason.section1.heading"
      )
    }

    "contain subheading for list 2" in {
      view.getElementsByClass("govuk-heading-l").get(1).text() mustBe messages(
        "checkYourAnswers.reason.section2.heading"
      )
    }

    "display the continue button" in {
      view.getElementById("continue").text() mustBe messages("site.continue")
    }

  }

}
