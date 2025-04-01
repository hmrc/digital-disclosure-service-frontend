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
import forms.ContractualDisclosureFacilityFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.offshore.ContractualDisclosureFacilityView
import models.{NormalMode, RelatesTo}

class ContractualDisclosureFacilityViewSpec extends ViewSpecBase with ViewMatchers {

  val form                                    = new ContractualDisclosureFacilityFormProvider()()
  val page: ContractualDisclosureFacilityView = inject[ContractualDisclosureFacilityView]
  val entity                                  = RelatesTo.ACompany

  private def createView: Html = page(form, NormalMode, entity)(request, messages)

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
        messages(s"contractualDisclosureFacility.$entity.body.first") +
        messages(s"contractualDisclosureFacility.$entity.link.first") +
        messages("site.dot") +
        messages(s"contractualDisclosureFacility.$entity.body.second") +
        messages(s"contractualDisclosureFacility.$entity.link.second") +
        messages("site.dot")
    }

    "have a first guidance link" in {
      view
        .getElementById("guidance-link-first")
        .attr("href") mustBe "https://www.gov.uk/guidance/admitting-tax-fraud-the-contractual-disclosure-facility-cdf"
    }

    "have a second guidance link" in {
      view
        .getElementById("guidance-link-second")
        .attr(
          "href"
        ) mustBe "https://www.gov.uk/government/publications/voluntary-disclosure-contractual-disclosure-facility-cdf1"
    }

    "contain warning text" in {
      view.getElementsByClass("govuk-warning-text").text() mustBe "! Warning " + messages(
        s"contractualDisclosureFacility.$entity.warningText"
      )
    }

    "contain label" in {
      view.getElementsByClass("govuk-fieldset__legend--m").text() mustBe messages(
        s"contractualDisclosureFacility.$entity.label"
      )
    }

    "contain third paragraph" in {
      view.getElementById("third-body").text() mustBe messages(s"contractualDisclosureFacility.$entity.body.third")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

  }

}
