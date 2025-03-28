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
import views.html.onshore.YouHaveNoOnshoreLiabilitiesToDiscloseView
import models.RelatesTo

class YouHaveNoOnshoreLiabilitiesToDiscloseViewSpec extends ViewSpecBase with ViewMatchers {

  val page: YouHaveNoOnshoreLiabilitiesToDiscloseView = inject[YouHaveNoOnshoreLiabilitiesToDiscloseView]

  "view" should {

    val areTheyTheIndividual = true
    val entity               = RelatesTo.AnIndividual
    val years                = 20

    def createView: Html = page(areTheyTheIndividual, entity, years)(request, messages)
    val view             = createView

    "have title" in {
      view.select("title").text() must include(messages("haveNoOnshoreLiabilities.agent.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("haveNoOnshoreLiabilities.agent.heading")
    }

    "contain body" in {
      view.getElementById("body").text() mustBe messages("haveNoOnshoreLiabilities.you.body", years)
    }

    "have a continue link" in {
      view
        .getElementById("continue")
        .attr("href") mustBe controllers.onshore.routes.CheckYourAnswersController.onPageLoad.url
    }

    "have a task list link" in {
      view.getElementById("task-list-link").attr("href") mustBe controllers.routes.TaskListController.onPageLoad.url
    }

  }

}
