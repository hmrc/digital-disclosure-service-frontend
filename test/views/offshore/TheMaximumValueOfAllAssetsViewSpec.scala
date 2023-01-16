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
import forms.TheMaximumValueOfAllAssetsFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.offshore.TheMaximumValueOfAllAssetsView
import models.NormalMode

class TheMaximumValueOfAllAssetsViewSpec extends ViewSpecBase with ViewMatchers {

  val form = new TheMaximumValueOfAllAssetsFormProvider()()
  val page: TheMaximumValueOfAllAssetsView = inject[TheMaximumValueOfAllAssetsView]

  private def createView: Html = page(form, NormalMode)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("theMaximumValueOfAllAssets.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-fieldset__heading").text() mustBe messages("theMaximumValueOfAllAssets.heading")
    }

    "have radio buttons" in {
      view.getElementsByClass("govuk-radios__label").get(0).text() mustBe messages("theMaximumValueOfAllAssets.tenThousandOrLess")
      view.getElementsByClass("govuk-radios__label").get(1).text() mustBe messages("theMaximumValueOfAllAssets.moreThanTenThousandLessThanOneLakh")
      view.getElementsByClass("govuk-radios__label").get(2).text() mustBe messages("theMaximumValueOfAllAssets.oneLakhAndMore")
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