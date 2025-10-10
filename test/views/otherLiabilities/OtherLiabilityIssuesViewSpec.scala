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

package views.OtherLiabilities

import base.ViewSpecBase
import forms.OtherLiabilityIssuesFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.otherLiabilities.OtherLiabilityIssuesView
import models.NormalMode

class OtherLiabilityIssuesViewSpec extends ViewSpecBase with ViewMatchers {

  val form                           = new OtherLiabilityIssuesFormProvider()()
  val page: OtherLiabilityIssuesView = inject[OtherLiabilityIssuesView]

  private def createView: Html = page(form, NormalMode)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("otherLiabilityIssues.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("otherLiabilityIssues.heading")
    }

    "contain body" in {
      view.getElementById("body").text() mustBe messages("otherLiabilityIssues.body")
    }

    "contain checkboxes" in {
      view.getElementsByClass("govuk-checkboxes__item").get(0).text() mustBe messages(
        "otherLiabilityIssues.employerLiabilities"
      )
      view.getElementsByClass("govuk-checkboxes__item").get(1).text() mustBe messages(
        "otherLiabilityIssues.class2National"
      )
      view.getElementsByClass("govuk-checkboxes__item").get(2).text() mustBe messages(
        "otherLiabilityIssues.inheritanceTaxIssues"
      )
      view.getElementsByClass("govuk-checkboxes__item").get(3).text() mustBe messages("otherLiabilityIssues.vatIssues")
      view.getElementsByClass("govuk-checkboxes__item").get(4).text() mustBe messages("otherLiabilityIssues.other")
      view.getElementsByClass("govuk-checkboxes__item").get(5).text() mustBe messages(
        "otherLiabilityIssues.noExclusion"
      )
    }

    "display the continue button" in {
      view.getElementById("continue").text() mustBe messages("site.saveAndContinue")
    }

    "have a task list link" in {
      view.getElementById("task-list-link").attr("href") mustBe controllers.routes.TaskListController.onPageLoad.url
    }

  }

}
