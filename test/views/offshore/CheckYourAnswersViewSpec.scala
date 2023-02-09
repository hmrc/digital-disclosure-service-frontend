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

class CheckYourAnswersViewSpec extends ViewSpecBase with ViewMatchers with SummaryListFluency {

  val userAnswers = UserAnswers("id")
  val viewModel = CheckYourAnswersViewModel(
    SummaryListViewModel(rows = Nil),
    SummaryListViewModel(rows = Nil),
    SummaryListViewModel(rows = Nil),
    Nil,
    0
  )
  val page: CheckYourAnswersView = inject[CheckYourAnswersView]

  private def createView: Html = page(viewModel)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("checkYourAnswers.offshore.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("checkYourAnswers.offshore.heading")
    }

    "have a offer heading" in {
      view.getElementsByClass("govuk-heading-m").get(1).text() mustBe messages("checkYourAnswers.offshore.offer.heading")
    }

    "have a first offer paragraph" in {
      view.getElementById("offer-paragraph-1").text() mustBe messages("checkYourAnswers.offshore.offer.paragraph1", viewModel.liabilitiesTotal)
    }

    "have a second offer paragraph" in {
      view.getElementById("offer-paragraph-2").text() mustBe messages("checkYourAnswers.offshore.offer.paragraph2")
    }

    "have a fullAmount heading" in {
      view.getElementsByClass("govuk-heading-m").get(2).text() mustBe messages("checkYourAnswers.offshore.fullAmount.heading")
    }

    "have a first fullAmount paragraph" in {
      view.getElementById("fullAmount-paragraph-1").text() mustBe messages("checkYourAnswers.offshore.fullAmount.paragraph1", viewModel.liabilitiesTotal) + messages("checkYourAnswers.offshore.fullAmount.link")
    }

    "have a second fullAmount paragraph" in {
      view.getElementById("fullAmount-paragraph-2").text() mustBe messages("checkYourAnswers.offshore.fullAmount.paragraph2")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

  }

}