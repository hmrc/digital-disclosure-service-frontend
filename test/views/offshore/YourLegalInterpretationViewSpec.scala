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
import forms.YourLegalInterpretationFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.offshore.YourLegalInterpretationView
import models.{NormalMode, YourLegalInterpretationCheckboxes}

class YourLegalInterpretationViewSpec extends ViewSpecBase with ViewMatchers {

  val form                              = new YourLegalInterpretationFormProvider()()
  val page: YourLegalInterpretationView = inject[YourLegalInterpretationView]
  val items                             = inject[YourLegalInterpretationCheckboxes]

  private def createView: Html = page(form, NormalMode, items.checkboxItems)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("yourLegalInterpretation.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("yourLegalInterpretation.heading")
    }

    "contain warning" in {
      view.getElementsByClass("govuk-warning-text").text() mustBe "! Warning " + messages(
        "yourLegalInterpretation.warning"
      )
    }

    "contain checkboxes" in {
      view.getElementsByClass("govuk-checkboxes__item").get(0).text() mustBe messages(
        "yourLegalInterpretation.yourResidenceStatus"
      )
      view.getElementsByClass("govuk-checkboxes__item").get(1).text() mustBe messages(
        "yourLegalInterpretation.yourDomicileStatus"
      )
      view.getElementsByClass("govuk-checkboxes__item").get(2).text() mustBe messages(
        "yourLegalInterpretation.theRemittanceBasis"
      )
      view.getElementsByClass("govuk-checkboxes__item").get(3).text() mustBe messages(
        "yourLegalInterpretation.howIncomeArisingInATrust"
      )
      view.getElementsByClass("govuk-checkboxes__item").get(4).text() mustBe messages(
        "yourLegalInterpretation.theTransferOfAssets.first"
      ) + messages("yourLegalInterpretation.theTransferOfAssets.second") + messages(
        "yourLegalInterpretation.theTransferOfAssets.link"
      )
      view.getElementsByClass("govuk-checkboxes__item").get(5).text() mustBe messages(
        "yourLegalInterpretation.howIncomeArisingInAnOffshore"
      )
      view.getElementsByClass("govuk-checkboxes__item").get(6).text() mustBe messages(
        "yourLegalInterpretation.inheritanceTaxIssues"
      )
      view.getElementsByClass("govuk-checkboxes__item").get(7).text() mustBe messages(
        "yourLegalInterpretation.whetherIncomeShouldBeTaxed.first"
      ) + messages("yourLegalInterpretation.whetherIncomeShouldBeTaxed.second") + messages(
        "yourLegalInterpretation.whetherIncomeShouldBeTaxed.link"
      )
      view.getElementsByClass("govuk-checkboxes__item").get(8).text() mustBe messages(
        "yourLegalInterpretation.anotherIssue"
      )
      view.getElementsByClass("govuk-checkboxes__item").get(9).text() mustBe messages(
        "yourLegalInterpretation.noExclusion"
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
