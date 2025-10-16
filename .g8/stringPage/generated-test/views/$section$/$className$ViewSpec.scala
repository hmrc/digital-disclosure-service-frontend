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

package views.$section$

import base.ViewSpecBase
import forms.$className$FormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.$section$.$className$View
import models.NormalMode

class $className$ViewSpec extends ViewSpecBase with ViewMatchers {

  val form = new $className$FormProvider()()
  val page: $className$View = inject[$className$View]

  private def createView: Html = page(form, NormalMode)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("$className;format="decap"$.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-label--xl").text() mustBe messages("$className;format="decap"$.heading")
    }

    "display the continue button" in {
      view.getElementById("continue").text() mustBe messages("site.saveAndContinue")
    }

  }

}