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
import pages._
import models._
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.Key
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.govuk.summarylist._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalacheck.Arbitrary.arbitrary
import pages.{WhichOnshoreYearsPage, OnshoreTaxYearLiabilitiesPage}
import uk.gov.hmrc.time.CurrentTaxYear
import java.time.LocalDate
import services.OnshoreWhichYearsService

class CheckYourAnswersViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with CurrentTaxYear {

  lazy val app = applicationBuilder(Some(emptyUserAnswers)).build()
  implicit val mess = messages(app)

  def now = () => LocalDate.now()

  val yearsService = app.injector.instanceOf[OnshoreWhichYearsService]
  val sut = app.injector.instanceOf[CheckYourAnswersViewModelCreation]

  "penaltyAmount" - {

    "apply the penalty rate to the amount of unpaid tax" in {
      forAll(arbitrary[OnshoreTaxYearWithLiabilities]) { taxYearWithLiabilities =>
        val penaltyRate = taxYearWithLiabilities.taxYearLiabilities.penaltyRate
        val unpaidTax = taxYearWithLiabilities.taxYearLiabilities.unpaidTax
        val expectedAmount = (BigDecimal(penaltyRate) * BigDecimal(unpaidTax)) / 100
        sut.penaltyAmount(taxYearWithLiabilities.taxYearLiabilities) mustEqual expectedAmount
      }
    }

  }

  "yearTotal" - {

    "add the penalty amount to the unpaid tax and the interest" in {
      forAll(arbitrary[OnshoreTaxYearWithLiabilities]) { taxYearWithLiabilities =>
        val penaltyRate = taxYearWithLiabilities.taxYearLiabilities.penaltyRate
        val unpaidTax = taxYearWithLiabilities.taxYearLiabilities.unpaidTax
        val penaltyAmount: BigDecimal = (BigDecimal(penaltyRate) / 100) * BigDecimal(unpaidTax)
        val interest = taxYearWithLiabilities.taxYearLiabilities.interest
        val expectedAmount: BigDecimal = penaltyAmount + BigDecimal(interest) + BigDecimal(unpaidTax)
        sut.yearTotal(taxYearWithLiabilities.taxYearLiabilities) mustEqual expectedAmount
      }
    }

  }

  "CheckYourAnswersViewModel" - {

    "return an empty Seq where the onshore pages isn't populated" in {
      val ua = UserAnswers("id")
      val viewModel = sut.create(ua)
      viewModel.taxYearLists mustEqual Nil
    }

    "return the correct view for a TaxBeforeFiveYearsOnshoreSummary is populated" in {
      val year = yearsService.getEarliestYearByBehaviour(Behaviour.Careless).toString
      val ua = UserAnswers("id").set(TaxBeforeFiveYearsOnshorePage, "test").success.value
      val viewModel = sut.create(ua)
      val summaryList = viewModel.summaryList
      summaryList.rows(0).key mustEqual Key(Text(mess("taxBeforeFiveYears.checkYourAnswersLabel", year)))
    }

    "return the correct view for a TaxBeforeThreeYearsOnshoreSummary is populated" in {
      val year = yearsService.getEarliestYearByBehaviour(Behaviour.ReasonableExcuse).toString
      val ua = UserAnswers("id").set(TaxBeforeThreeYearsOnshorePage, "test").success.value
      val viewModel = sut.create(ua)
      val summaryList = viewModel.summaryList
      summaryList.rows(0).key mustEqual Key(Text(mess("taxBeforeThreeYears.checkYourAnswersLabel", year)))
    }

    "return the correct view for a TaxBeforeNineteenYearsOnshoreSummary is populated" in {
      val year = yearsService.getEarliestYearByBehaviour(Behaviour.Deliberate).toString
      val ua = UserAnswers("id").set(TaxBeforeNineteenYearsOnshorePage, "test").success.value
      val viewModel = sut.create(ua)
      val summaryList = viewModel.summaryList
      summaryList.rows(0).key mustEqual Key(Text(mess("taxBeforeNineteenYears.checkYourAnswersLabel", year)))
    }

    "return an empty Seq where the years page isn't populated" in {
      val ua = UserAnswers("id")
      val viewModel = sut.create(ua)
      viewModel.taxYearLists mustEqual Nil
    }

    "return a single element where one year is populated" in {
      forAll(arbitrary[OnshoreTaxYearWithLiabilities]) { taxYearWithLiabilities => 
        val yearKey = taxYearWithLiabilities.taxYear.startYear.toString
        val onshoreYears: Set[OnshoreYears] = Set(taxYearWithLiabilities.taxYear)
        val ua = (for {
          uaWithYears <- UserAnswers("id").set(WhichOnshoreYearsPage, onshoreYears)
          finalUa <- uaWithYears.setByKey(OnshoreTaxYearLiabilitiesPage, yearKey, taxYearWithLiabilities)
        } yield finalUa).success.value
        val viewModel = sut.create(ua)

        val firstSummaryList = viewModel.taxYearLists(0)._2
        checkSummaryRows(firstSummaryList, taxYearWithLiabilities)

      }
    }

  }

  def checkSummaryRows(summaryList: SummaryList, onshoreTaxYearLiabilities: OnshoreTaxYearWithLiabilities) = {
    summaryList.rows(0).key mustEqual Key(Text(mess("onshoreTaxYearLiabilities.unpaidTax.checkYourAnswersLabel")))
    summaryList.rows(0).value mustEqual ValueViewModel(HtmlContent(s"&pound;${onshoreTaxYearLiabilities.taxYearLiabilities.unpaidTax}"))

    summaryList.rows(1).key mustEqual Key(Text(mess("onshoreTaxYearLiabilities.niContributions.checkYourAnswersLabel")))
    summaryList.rows(1).value mustEqual ValueViewModel(HtmlContent(s"&pound;${onshoreTaxYearLiabilities.taxYearLiabilities.niContributions}"))

    summaryList.rows(2).key mustEqual Key(Text(mess("onshoreTaxYearLiabilities.interest.checkYourAnswersLabel")))
    summaryList.rows(2).value mustEqual ValueViewModel(HtmlContent(s"&pound;${onshoreTaxYearLiabilities.taxYearLiabilities.interest}"))

    summaryList.rows(3).key mustEqual Key(Text(mess("onshoreTaxYearLiabilities.penaltyRate.checkYourAnswersLabel")))
    summaryList.rows(3).value mustEqual ValueViewModel(HtmlContent(s"&pound;${onshoreTaxYearLiabilities.taxYearLiabilities.penaltyRate}"))    
  }
  
}

