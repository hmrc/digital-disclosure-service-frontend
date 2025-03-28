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
import forms.PropertyIsNoLongerBeingLetOutFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.letting.PropertyIsNoLongerBeingLetOutView
import models.NormalMode

class PropertyIsNoLongerBeingLetOutViewSpec extends ViewSpecBase with ViewMatchers {

  val form                                    = new PropertyIsNoLongerBeingLetOutFormProvider()()
  val page: PropertyIsNoLongerBeingLetOutView = inject[PropertyIsNoLongerBeingLetOutView]

  private def createView: Html = page(form, 0, NormalMode)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("propertyIsNoLongerBeingLetOut.title", 1))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("propertyIsNoLongerBeingLetOut.heading", 1)
    }

    "contain inset text" in {
      view.getElementById("inset-body").text() mustBe messages("propertyIsNoLongerBeingLetOut.insetBody")
    }

    "contain the stopDate question" in {
      view.getElementsByClass("govuk-label--s").get(0).text() mustBe messages("propertyIsNoLongerBeingLetOut.stopDate")
    }

    "contain day, month & year" in {
      view.getElementsByClass("govuk-date-input__item").get(0).text() mustBe messages("date.day")
      view.getElementsByClass("govuk-date-input__item").get(1).text() mustBe messages("date.month")
      view.getElementsByClass("govuk-date-input__item").get(2).text() mustBe messages("date.year")
    }

    "contain the whatHasHappenedToProperty question" in {
      view.getElementsByClass("govuk-label--s").get(1).text() mustBe messages(
        "propertyIsNoLongerBeingLetOut.whatHasHappenedToProperty"
      )
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

    "have a task list link" in {
      view.getElementById("task-list-link").attr("href") mustBe controllers.routes.TaskListController.onPageLoad.url
    }

  }

}
