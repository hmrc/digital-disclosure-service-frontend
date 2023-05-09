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
import controllers.onshore.routes
import forms.AccountingPeriodDLAddedFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.onshore.AccountingPeriodDLAddedView
import models.{DirectorLoanAccountLiabilities, NormalMode}
import viewmodels.onshore.DirectorLoanAccountLiabilityModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AccountingPeriodDLAddedViewSpec extends ViewSpecBase with ViewMatchers {

  val form = new AccountingPeriodDLAddedFormProvider()()
  val page: AccountingPeriodDLAddedView = inject[AccountingPeriodDLAddedView]

  val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

  val directorLoanAccountLiabilities: DirectorLoanAccountLiabilities = DirectorLoanAccountLiabilities(
    name = "Some Name 1",
    periodEnd = LocalDate.now(),
    overdrawn = BigInt(1),
    unpaidTax = BigInt(1),
    interest = BigInt(1),
    penaltyRate = 1,
    penaltyRateReason = "Some reason"
  )

  val directorLoanAccountLiabilities2: DirectorLoanAccountLiabilities = DirectorLoanAccountLiabilities(
    name = "Some Name 2",
    periodEnd = LocalDate.now(),
    overdrawn = BigInt(2),
    unpaidTax = BigInt(2),
    interest = BigInt(2),
    penaltyRate = 2,
    penaltyRateReason = "Some reason"
  )

  "view for a single account period end date" should {

    val directorLoanAccountLiabilitiesSummaries = DirectorLoanAccountLiabilityModel.row(Seq(directorLoanAccountLiabilities), NormalMode)

    def createView: Html = page(form, directorLoanAccountLiabilitiesSummaries, NormalMode)(request, messages)
    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("accountingPeriodDLAdded.title.single"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("accountingPeriodDLAdded.heading.single")
    }

    "contain summary row" in {
      view.getElementsByClass("govuk-summary-list__key govuk-!-font-weight-regular hmrc-summary-list__key").text() mustBe s"Ending ${directorLoanAccountLiabilities.periodEnd.format(dateFormatter)}"
    }

    "contain change/remove link" in {
      view.getElementsByClass("govuk-link").get(3).text() must include(messages("site.change"))
      view.getElementsByClass("govuk-link").get(4).text() must include(messages("site.remove"))
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

  }

  "view for a multiple properties" should {

    val directorLoanAccountLiabilitiesSummaries = DirectorLoanAccountLiabilityModel.row(Seq(directorLoanAccountLiabilities, directorLoanAccountLiabilities2), NormalMode)

    def createView: Html = page(form, directorLoanAccountLiabilitiesSummaries, NormalMode)(request, messages)

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("accountingPeriodDLAdded.title.multi", 2))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("accountingPeriodDLAdded.title.multi", 2)
    }

    "contain summary row" in {
      view.getElementsByClass("govuk-summary-list__key govuk-!-font-weight-regular hmrc-summary-list__key").first().text() mustBe s"Ending ${directorLoanAccountLiabilities.periodEnd.format(dateFormatter)}"
      view.getElementsByClass("govuk-summary-list__key govuk-!-font-weight-regular hmrc-summary-list__key").last().text() mustBe s"Ending ${directorLoanAccountLiabilities2.periodEnd.format(dateFormatter)}"
    }

    "contain remove link" in {
      view.getElementsByClass("govuk-link").get(3).text() must include(messages("site.change"))
      view.getElementsByClass("govuk-link").get(3).attr("href") mustBe routes.DirectorLoanAccountLiabilitiesController.onPageLoad(0, NormalMode).url
      view.getElementsByClass("govuk-link").get(4).text() must include(messages("site.remove"))
      view.getElementsByClass("govuk-link").get(4).attr("href") mustBe routes.AccountingPeriodDLAddedController.remove(0, NormalMode).url
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

  }

}