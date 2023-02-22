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
import forms.DirectorLoanAccountLiabilitiesFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.onshore.DirectorLoanAccountLiabilitiesView
import models.NormalMode

class DirectorLoanAccountLiabilitiesViewSpec extends ViewSpecBase with ViewMatchers {

  val form = new DirectorLoanAccountLiabilitiesFormProvider()()
  val page: DirectorLoanAccountLiabilitiesView = inject[DirectorLoanAccountLiabilitiesView]

  val index = 0

  private def createView: Html = page(form, NormalMode, 0)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("directorLoanAccountLiabilities.title", index + 1))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("directorLoanAccountLiabilities.heading", index + 1)
    }

    "contain the name question" in {
      view.getElementsByClass("govuk-label--m").get(0).text() mustBe messages("directorLoanAccountLiabilities.name.question")
    }

    "contain the periodEnd question" in {
      view.getElementsByClass("govuk-label--m").get(1).text() mustBe messages("directorLoanAccountLiabilities.periodEnd.label")
    }

    "contain the overdrawn question" in {
      view.getElementsByClass("govuk-label--m").get(2).text() mustBe messages("directorLoanAccountLiabilities.overdrawn.question")
    }

    "contain the unpaidTax question" in {
      view.getElementsByClass("govuk-label--m").get(3).text() mustBe messages("directorLoanAccountLiabilities.unpaidTax.question")
    }

    "contain the interest question" in {
      view.getElementsByClass("govuk-label--m").get(4).text() mustBe messages("directorLoanAccountLiabilities.interest.question")
    }

    "contain the penaltyRate question" in {
      view.getElementsByClass("govuk-label--m").get(5).text() mustBe messages("directorLoanAccountLiabilities.penaltyRate.question")
    }

    "contain the penalty rate reason question" in {
      view.getElementsByClass("govuk-label--m").get(6).text() mustBe messages("directorLoanAccountLiabilities.penaltyRateReason.question")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

  }

}