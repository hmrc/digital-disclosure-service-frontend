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
import forms.WhichTelephoneNumberCanWeContactYouWithFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.reason.WhichTelephoneNumberCanWeContactYouWithView
import models.NormalMode

class WhichTelephoneNumberCanWeContactYouWithViewSpec extends ViewSpecBase with ViewMatchers {

  val form                                              = new WhichTelephoneNumberCanWeContactYouWithFormProvider()()
  val page: WhichTelephoneNumberCanWeContactYouWithView = inject[WhichTelephoneNumberCanWeContactYouWithView]
  val telephoneNumber                                   = "07777 777777"
  private def createView: Html                          = page(form, NormalMode, telephoneNumber)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("whichTelephoneNumberCanWeContactYouWith.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-fieldset__heading").text() mustBe messages(
        "whichTelephoneNumberCanWeContactYouWith.heading"
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
