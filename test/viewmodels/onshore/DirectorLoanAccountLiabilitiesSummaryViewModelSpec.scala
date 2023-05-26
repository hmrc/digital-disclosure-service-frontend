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

package viewmodels.onshore

import base.SpecBase
import models._
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.Key
import viewmodels.govuk.summarylist._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalacheck.Arbitrary.arbitrary
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import viewmodels.RevealFullText

class DirectorLoanAccountLiabilitiesSummaryViewModelSpec extends SpecBase with ScalaCheckPropertyChecks {

    lazy val app = applicationBuilder(Some(emptyUserAnswers)).build()
    implicit val mess = messages(app)
    val revealFullText = app.injector.instanceOf[RevealFullText]
    
    val sut = app.injector.instanceOf[DirectorLoanAccountLiabilitiesSummaryViewModelCreation]

    "penaltyAmount" - {

        "apply the penalty rate to the amount of unpaid tax" in {
            forAll(arbitrary[DirectorLoanAccountLiabilities]) { directorLoanAccountLiabilities =>
                val penaltyRate = directorLoanAccountLiabilities.penaltyRate
                val unpaidTax = directorLoanAccountLiabilities.unpaidTax
                val expectedAmount = (penaltyRate * BigDecimal(unpaidTax)) / 100
                sut.penaltyAmount(directorLoanAccountLiabilities) mustEqual expectedAmount
            }
        }

    }

    "DirectorLoanAccountLiabilitiesSummaryViewModel" - {

        "return an empty Seq where the director loan account pages isn't populated" in {
            val ua = UserAnswers("id", "session-123")
            val viewModel = sut.create(ua)
            viewModel.directorLoanAccountLiabilitiesList mustEqual Nil
        }

        "return the director loan account section if user have added one or more accounting details" in {

            val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

            val directorLoanAccountLiabilities = DirectorLoanAccountLiabilities (
                name = "name",
                periodEnd = LocalDate.now(),
                overdrawn = BigInt(0),
                unpaidTax = BigInt(0),
                interest = BigInt(0),
                penaltyRate = 0,
                penaltyRateReason = "reason"
            )

            val summaryList = sut.directorLoanAccountLiabilitiesToSummaryList(0, directorLoanAccountLiabilities, revealFullText)

            summaryList.rows(0).key mustEqual Key(Text(mess("directorLoanAccountLiabilities.name.checkYourAnswersLabel")))
            summaryList.rows(0).value mustEqual ValueViewModel(Text("name"))  

            summaryList.rows(1).key mustEqual Key(Text(mess("directorLoanAccountLiabilities.periodEnd.checkYourAnswersLabel")))
            summaryList.rows(1).value mustEqual ValueViewModel(Text(directorLoanAccountLiabilities.periodEnd.format(dateFormatter)))  

            summaryList.rows(2).key mustEqual Key(Text(mess("directorLoanAccountLiabilities.overdrawn.checkYourAnswersLabel")))
            summaryList.rows(2).value mustEqual ValueViewModel(Text(s"&pound;0"))  

            summaryList.rows(3).key mustEqual Key(Text(mess("directorLoanAccountLiabilities.unpaidTax.checkYourAnswersLabel")))
            summaryList.rows(3).value mustEqual ValueViewModel(Text(s"&pound;0"))

            summaryList.rows(4).key mustEqual Key(Text(mess("directorLoanAccountLiabilities.interest.checkYourAnswersLabel")))
            summaryList.rows(4).value mustEqual ValueViewModel(Text(s"&pound;0"))

            summaryList.rows(5).key mustEqual Key(Text(mess("directorLoanAccountLiabilities.penaltyRate.checkYourAnswersLabel")))
            summaryList.rows(5).value mustEqual ValueViewModel(Text(s"0.00%"))

            summaryList.rows(6).key mustEqual Key(Text(mess("checkYourAnswers.dl.total.penaltyAmount")))
            summaryList.rows(6).value mustEqual ValueViewModel(Text(s"&pound;0.00"))

            summaryList.rows(7).key mustEqual Key(Text(mess("directorLoanAccountLiabilities.penaltyRateReason.checkYourAnswersLabel")))
            summaryList.rows(7).value mustEqual ValueViewModel(Text("reason"))  
        }

        "return an empty total section where the director loan account pages isn't populated" in {
            val ua = UserAnswers("id", "session-123")
            val viewModel = sut.create(ua)

            viewModel.totalAmountsList.rows(0).key mustEqual Key(Text(mess("checkYourAnswers.dl.total.taxDue")))
            viewModel.totalAmountsList.rows(0).value mustEqual ValueViewModel(Text(s"&pound;0"))  

            viewModel.totalAmountsList.rows(1).key mustEqual Key(Text(mess("checkYourAnswers.dl.total.interestDue")))
            viewModel.totalAmountsList.rows(1).value mustEqual ValueViewModel(Text(s"&pound;0"))  

            viewModel.totalAmountsList.rows(2).key mustEqual Key(Text(mess("checkYourAnswers.dl.total.penaltyAmount")))
            viewModel.totalAmountsList.rows(2).value mustEqual ValueViewModel(Text(s"&pound;0.00"))  

            viewModel.totalAmountsList.rows(3).key mustEqual Key(Text(mess("checkYourAnswers.dl.total.totalAmountDue")))
            viewModel.totalAmountsList.rows(3).value mustEqual ValueViewModel(Text(s"&pound;0.00"))  
        }    
    }
}
