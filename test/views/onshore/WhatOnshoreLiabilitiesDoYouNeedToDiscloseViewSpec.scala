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
import forms.WhatOnshoreLiabilitiesDoYouNeedToDiscloseFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.onshore.WhatOnshoreLiabilitiesDoYouNeedToDiscloseView
import models.NormalMode

class WhatOnshoreLiabilitiesDoYouNeedToDiscloseViewSpec extends ViewSpecBase with ViewMatchers {

  val form = new WhatOnshoreLiabilitiesDoYouNeedToDiscloseFormProvider()()
  val page: WhatOnshoreLiabilitiesDoYouNeedToDiscloseView = inject[WhatOnshoreLiabilitiesDoYouNeedToDiscloseView]

  "view" should {

    val isUserCompany = false
    def createView: Html = page(form, NormalMode, isUserCompany)(request, messages)
    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("whatOnshoreLiabilitiesDoYouNeedToDisclose.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("whatOnshoreLiabilitiesDoYouNeedToDisclose.heading")
    }

    "contain checkbox when user is not company" in {
      view.getElementsByClass("govuk-checkboxes__label").get(0).text() mustBe messages("whatOnshoreLiabilitiesDoYouNeedToDisclose.businessIncomeLiabilities")
      view.getElementsByClass("govuk-checkboxes__label").get(1).text() mustBe messages("whatOnshoreLiabilitiesDoYouNeedToDisclose.capitalGainsTaxLiabilities")
      view.getElementsByClass("govuk-checkboxes__label").get(2).text() mustBe messages("whatOnshoreLiabilitiesDoYouNeedToDisclose.lettingIncomeFromResidential")
      view.getElementsByClass("govuk-checkboxes__label").get(3).text() mustBe messages("whatOnshoreLiabilitiesDoYouNeedToDisclose.nonBusinessIncome")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

    "have a task list link" in {
      view.getElementById("task-list-link").attr("href") mustBe controllers.routes.TaskListController.onPageLoad.url
    }

  }

  "view" should {

    val isUserCompany = true
    def createView: Html = page(form, NormalMode, isUserCompany)(request, messages)
    val view = createView

    "contain checkbox when user is company" in {
      view.getElementsByClass("govuk-checkboxes__label").get(0).text() mustBe messages("whatOnshoreLiabilitiesDoYouNeedToDisclose.businessIncomeLiabilities")
      view.getElementsByClass("govuk-checkboxes__label").get(1).text() mustBe messages("whatOnshoreLiabilitiesDoYouNeedToDisclose.capitalGainsTaxLiabilities")
      view.getElementsByClass("govuk-checkboxes__label").get(2).text() mustBe messages("whatOnshoreLiabilitiesDoYouNeedToDisclose.company.corporationTaxLiabilities")
      view.getElementsByClass("govuk-checkboxes__label").get(3).text() mustBe messages("whatOnshoreLiabilitiesDoYouNeedToDisclose.company.directorLoanLiabilities")
      view.getElementsByClass("govuk-checkboxes__label").get(4).text() mustBe messages("whatOnshoreLiabilitiesDoYouNeedToDisclose.lettingIncomeFromResidential")
      view.getElementsByClass("govuk-checkboxes__label").get(5).text() mustBe messages("whatOnshoreLiabilitiesDoYouNeedToDisclose.nonBusinessIncome")
    }
  }

}