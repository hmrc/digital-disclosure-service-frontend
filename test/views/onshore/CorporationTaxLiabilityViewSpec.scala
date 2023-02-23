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

package views.onshore

import base.ViewSpecBase
import forms.CorporationTaxLiabilityFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.onshore.CorporationTaxLiabilityView
import models.NormalMode

class CorporationTaxLiabilityViewSpec extends ViewSpecBase with ViewMatchers {

  val form = new CorporationTaxLiabilityFormProvider()()
  val page: CorporationTaxLiabilityView = inject[CorporationTaxLiabilityView]

  private def createView: Html = page(form, NormalMode, 0)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("corporationTaxLiability.title", 1))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("corporationTaxLiability.heading", 1)
    }

    "contain a body" in {
      view.getElementById("body").text() mustBe messages("corporationTaxLiability.body") + messages("corporationTaxLiability.body.link") + messages("site.dot")
    }

    "contain inset text" in {
      view.getElementById("inset-body").text() mustBe s"${messages("corporationTaxLiability.insetBody")}"
    }

    "contain sub header" in {
      view.getElementsByClass("govuk-heading-m").text() mustBe messages("corporationTaxLiability.subheading", 0)
    }

    "contain day, month & year" in {
      view.getElementsByClass("govuk-date-input__item").get(0).text() mustBe messages("date.day")
      view.getElementsByClass("govuk-date-input__item").get(1).text() mustBe messages("date.month")
      view.getElementsByClass("govuk-date-input__item").get(2).text() mustBe messages("date.year")
    }

    "contain the period end question" in {
      view.getElementsByClass("govuk-label--s").get(0).text() mustBe messages("corporationTaxLiability.periodEnd")
    }

    "contain the how much income question" in {
      view.getElementsByClass("govuk-label--s").get(1).text() mustBe messages("corporationTaxLiability.howMuchIncome")
    }

    "contain the how much unpaid question" in {
      view.getElementsByClass("govuk-label--s").get(2).text() mustBe messages("corporationTaxLiability.howMuchUnpaid")
    }

    "contain the how much interest question" in {
      view.getElementsByClass("govuk-label--s").get(3).text() mustBe messages("corporationTaxLiability.howMuchInterest")
    }

    "contain the penalty rate question" in {
      view.getElementsByClass("govuk-label--s").get(4).text() mustBe messages("corporationTaxLiability.penaltyRate")
    }

    "contain the penalty rate reason question" in {
      view.getElementsByClass("govuk-label--s").get(5).text() mustBe messages("corporationTaxLiability.penaltyRateReason")
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