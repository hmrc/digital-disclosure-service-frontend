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
import forms.ContractualDisclosureFacilityFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.offshore.ContractualDisclosureFacilityView
import models.NormalMode

class ContractualDisclosureFacilityViewSpec extends ViewSpecBase with ViewMatchers {

  val form = new ContractualDisclosureFacilityFormProvider()()
  val page: ContractualDisclosureFacilityView = inject[ContractualDisclosureFacilityView]

  private def createView: Html = page(form, NormalMode)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("contractualDisclosureFacility.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("contractualDisclosureFacility.heading")

    }

    "contain first paragraph" in {
      view.getElementById("body").text() mustBe 
        messages("contractualDisclosureFacility.body.first") +
        messages("contractualDisclosureFacility.link.first") +
        messages("contractualDisclosureFacility.body.second") +
        messages("contractualDisclosureFacility.link.second")
    }

    "have a first guidance link" in {
      view.getElementById("guidance-link-first").attr("href") mustBe "https://www.gov.uk/guidance/admitting-tax-fraud-the-contractual-disclosure-facility-cdf"
    }

    "have a second guidance link" in {
      view.getElementById("guidance-link-second").attr("href") mustBe "https://www.gov.uk/government/publications/voluntary-disclosure-contractual-disclosure-facility-cdf1"
    }

    "contain warning text" in {
      view.getElementsByClass("govuk-warning-text").text() mustBe "! " + messages("contractualDisclosureFacility.warningText")
    }

    "contain label" in {
      view.getElementsByClass("govuk-fieldset__legend--m").text() mustBe messages("contractualDisclosureFacility.label")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

  }

}