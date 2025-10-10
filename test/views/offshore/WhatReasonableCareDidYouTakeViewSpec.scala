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
import forms.WhatReasonableCareDidYouTakeFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.offshore.WhatReasonableCareDidYouTakeView
import models.{NormalMode, RelatesTo}

class WhatReasonableCareDidYouTakeViewSpec extends ViewSpecBase with ViewMatchers {

  val page: WhatReasonableCareDidYouTakeView = inject[WhatReasonableCareDidYouTakeView]

  "view" should {

    val areTheyTheIndividual = true
    val entity               = RelatesTo.AnIndividual
    val form                 = new WhatReasonableCareDidYouTakeFormProvider()(areTheyTheIndividual)
    def createView: Html     = page(form, NormalMode, areTheyTheIndividual, entity)(request, messages)

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("whatReasonableCareDidYouTake.entity.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("whatReasonableCareDidYouTake.entity.heading")
    }

    "contain reasonableCare & yearsThisAppliesTo labels" in {
      view.getElementsByClass("govuk-label").get(0).text() mustBe messages(
        "whatReasonableCareDidYouTake.entity.heading"
      )
      view.getElementsByClass("govuk-label").get(1).text() mustBe messages(
        "whatReasonableCareDidYouTake.yearsThisAppliesTo"
      )
    }

    "contain reasonableCare paragraph" in {
      view.getElementById("label").text() mustBe messages("whatReasonableCareDidYouTake.you.reasonableCare")
    }

    "contain input 2 hint" in {
      view.getElementById("yearsThisAppliesTo-hint").text() mustBe messages(
        "whatReasonableCareDidYouTake.yearsThisAppliesTo.hint"
      )
    }

    "display the continue button" in {
      view.getElementById("continue").text() mustBe messages("site.saveAndContinue")
    }

    "have a task list link" in {
      view.getElementById("task-list-link").attr("href") mustBe controllers.routes.TaskListController.onPageLoad.url
    }

  }

  "view" should {

    val areTheyTheIndividual = false
    val entity               = RelatesTo.AnIndividual
    val form                 = new WhatReasonableCareDidYouTakeFormProvider()(areTheyTheIndividual)
    def createView: Html     = page(form, NormalMode, areTheyTheIndividual, entity)(request, messages)

    val view = createView

    "have title when you have selected onbehalf of individual agent" in {
      view.select("title").text() must include(messages("whatReasonableCareDidYouTake.agent.title"))
    }

    "contain header when you have selected onbehalf of individual agent" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("whatReasonableCareDidYouTake.agent.heading")
    }

    "contain reasonableCare paragraph when you have selected onbehalf of individual agent" in {
      view.getElementById("label").text() mustBe messages(s"whatReasonableCareDidYouTake.$entity.reasonableCare")
    }
  }

  "view" should {

    val areTheyTheIndividual = false
    val entity               = RelatesTo.AnEstate
    val form                 = new WhatReasonableCareDidYouTakeFormProvider()(areTheyTheIndividual)
    def createView: Html     = page(form, NormalMode, areTheyTheIndividual, entity)(request, messages)

    val view = createView

    "have title when you have selected AnEstate agent" in {
      view.select("title").text() must include(messages("whatReasonableCareDidYouTake.agent.title"))
    }

    "contain header when you have selected AnEstate agent" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("whatReasonableCareDidYouTake.agent.heading")
    }

    "contain reasonableCare labels when you have selected AnEstate agent" in {
      view.getElementById("label").text() mustBe messages(s"whatReasonableCareDidYouTake.$entity.reasonableCare")
    }
  }

  "view" should {

    val areTheyTheIndividual = false
    val entity               = RelatesTo.ACompany
    val form                 = new WhatReasonableCareDidYouTakeFormProvider()(areTheyTheIndividual)
    def createView: Html     = page(form, NormalMode, areTheyTheIndividual, entity)(request, messages)

    val view = createView

    "have title when you have selected ACompany agent" in {
      view.select("title").text() must include(messages("whatReasonableCareDidYouTake.agent.title"))
    }

    "contain header when you have selected ACompany agent" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("whatReasonableCareDidYouTake.agent.heading")
    }

    "contain reasonableCare labels when you have selected ACompany agent" in {
      view.getElementById("label").text() mustBe messages(s"whatReasonableCareDidYouTake.$entity.reasonableCare")
    }
  }

  "view" should {

    val areTheyTheIndividual = false
    val entity               = RelatesTo.ALimitedLiabilityPartnership
    val form                 = new WhatReasonableCareDidYouTakeFormProvider()(areTheyTheIndividual)
    def createView: Html     = page(form, NormalMode, areTheyTheIndividual, entity)(request, messages)

    val view = createView

    "have title when you have selected ALimitedLiabilityPartnership agent" in {
      view.select("title").text() must include(messages("whatReasonableCareDidYouTake.agent.title"))
    }

    "contain header when you have selected ALimitedLiabilityPartnership agent" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("whatReasonableCareDidYouTake.agent.heading")
    }

    "contain reasonableCare labels when you have selected ALimitedLiabilityPartnership agent" in {
      view.getElementById("label").text() mustBe messages(s"whatReasonableCareDidYouTake.$entity.reasonableCare")
    }
  }

  "view" should {

    val areTheyTheIndividual = false
    val entity               = RelatesTo.ATrust
    val form                 = new WhatReasonableCareDidYouTakeFormProvider()(areTheyTheIndividual)
    def createView: Html     = page(form, NormalMode, areTheyTheIndividual, entity)(request, messages)

    val view = createView

    "have title when you have selected ATrust agent" in {
      view.select("title").text() must include(messages("whatReasonableCareDidYouTake.agent.title"))
    }

    "contain header when you have selected ATrust agent" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("whatReasonableCareDidYouTake.agent.heading")
    }

    "contain reasonableCare labels when you have selected ATrust agent" in {
      view.getElementById("label").text() mustBe messages(s"whatReasonableCareDidYouTake.$entity.reasonableCare")
    }
  }
}
