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

package views.notification

import base.ViewSpecBase
import play.twirl.api.Html
import support.ViewMatchers
import views.html.IndexView
import models.NormalMode

class IndexViewSpec extends ViewSpecBase with ViewMatchers {

  val page: IndexView = inject[IndexView]

  private def createView: Html = page()(request, messages)

  "view" should {

    val view = createView

    "display the start now button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("start")
      view.getElementsByClass("govuk-button").text() mustBe messages("index.start")
    }

  }

}