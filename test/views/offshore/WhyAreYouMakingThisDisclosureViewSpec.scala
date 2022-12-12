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

package views.offshore

import base.ViewSpecBase
import forms.WhyAreYouMakingThisDisclosureFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.offshore.WhyAreYouMakingThisDisclosureView
import models.NormalMode

class WhyAreYouMakingThisDisclosureViewSpec extends ViewSpecBase with ViewMatchers {

  val form = new WhyAreYouMakingThisDisclosureFormProvider()()
  val page: WhyAreYouMakingThisDisclosureView = inject[WhyAreYouMakingThisDisclosureView]

  private def createView: Html = page(form, NormalMode)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("whyAreYouMakingThisDisclosure.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("whyAreYouMakingThisDisclosure.heading")
    }

    "contain first paragraph" in {
      view.getElementById("first-paragraph").text() mustBe messages("whyAreYouMakingThisDisclosure.paragraph.first")
    }

    "contain second paragraph" in {
      view.getElementById("second-paragraph").text() mustBe messages("whyAreYouMakingThisDisclosure.paragraph.second")
    }

    "contain multiple checkboxes" in {
      view.getElementsByClass("govuk-checkboxes__item").get(0).text() mustBe messages("whyAreYouMakingThisDisclosure.checkbox1")
      view.getElementsByClass("govuk-checkboxes__item").get(1).text() mustBe messages("whyAreYouMakingThisDisclosure.checkbox2")
      view.getElementsByClass("govuk-checkboxes__item").get(2).text() mustBe messages("whyAreYouMakingThisDisclosure.checkbox3")
      view.getElementsByClass("govuk-checkboxes__item").get(3).text() mustBe messages("whyAreYouMakingThisDisclosure.checkbox4")
      view.getElementsByClass("govuk-checkboxes__item").get(4).text() mustBe messages("whyAreYouMakingThisDisclosure.checkbox5")
      view.getElementsByClass("govuk-checkboxes__item").get(5).text() mustBe messages("whyAreYouMakingThisDisclosure.checkbox6")
      view.getElementsByClass("govuk-checkboxes__item").get(6).text() mustBe messages("whyAreYouMakingThisDisclosure.checkbox7")
      view.getElementsByClass("govuk-checkboxes__item").get(7).text() mustBe messages("whyAreYouMakingThisDisclosure.checkbox8")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

    "have a task list link" in {
      view.getElementById("task-list-link").attr("href") mustBe controllers.routes.TaskListController.onPageLoad.url
    }

  }

}