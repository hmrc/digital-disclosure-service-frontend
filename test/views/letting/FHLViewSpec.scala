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
import forms.FHLFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.letting.FHLView
import models.NormalMode

class FHLViewSpec extends ViewSpecBase with ViewMatchers {

  val form = new FHLFormProvider()()
  val page: FHLView = inject[FHLView]

  private def createView: Html = page(form, 0, NormalMode)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("fhl.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("fhl.heading")
    }

    "have all the elements in the bullet-list" in {
      view.getElementsByClass("dashed-list-item").get(0).text() mustBe messages("fhl.bulletList.first")
      view.getElementsByClass("dashed-list-item").get(1).text() mustBe messages("fhl.bulletList.second")
      view.getElementsByClass("dashed-list-item").get(2).text() mustBe messages("fhl.bulletList.third")
      view.getElementsByClass("dashed-list-item").get(3).text() mustBe messages("fhl.bulletList.forth")
      view.getElementsByClass("dashed-list-item").get(4).text() mustBe messages("fhl.bulletList.fifth")
    }

    "contain the fhl question" in {
      view.getElementsByClass("govuk-label--s").get(0).text() mustBe messages("fhl.criteria", 1)
    }

    "have yes option" in {
      view.getElementsByClass("govuk-radios__label").first().text() mustBe messages("fhl.yes")
    }

    "have no option" in {
      view.getElementsByClass("govuk-radios__label").last().text() mustBe messages("fhl.no")
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