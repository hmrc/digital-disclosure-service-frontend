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

package viewmodels.offshore

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
import pages.{TaxYearLiabilitiesPage, WhichYearsPage}
import uk.gov.hmrc.time.CurrentTaxYear

import java.time.LocalDate
import org.scalacheck.Gen
import play.api.i18n.Messages
import services.OffshoreWhichYearsService

class CheckYourAnswersViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with CurrentTaxYear {

  lazy val app                = application
  implicit val mess: Messages = messages

  def now = () => LocalDate.now()

  val yearsService = app.injector.instanceOf[OffshoreWhichYearsService]
  val sut          = app.injector.instanceOf[CheckYourAnswersViewModelCreation]

  "penaltyAmount" - {

    "apply the penalty rate to the amount of unpaid tax" in {
      forAll(arbitrary[TaxYearWithLiabilities]) { taxYearWithLiabilities =>
        val penaltyRate    = taxYearWithLiabilities.taxYearLiabilities.penaltyRate
        val unpaidTax      = taxYearWithLiabilities.taxYearLiabilities.unpaidTax
        val expectedAmount = (penaltyRate * BigDecimal(unpaidTax)) / 100
        sut.penaltyAmount(taxYearWithLiabilities.taxYearLiabilities) mustEqual expectedAmount
      }
    }

  }

  "yearTotal" - {

    "add the penalty amount to the unpaid tax and the interest" in {
      forAll(arbitrary[TaxYearWithLiabilities]) { taxYearWithLiabilities =>
        val penaltyRate                = taxYearWithLiabilities.taxYearLiabilities.penaltyRate
        val unpaidTax                  = taxYearWithLiabilities.taxYearLiabilities.unpaidTax
        val penaltyAmount: BigDecimal  = (penaltyRate / 100) * BigDecimal(unpaidTax)
        val interest                   = taxYearWithLiabilities.taxYearLiabilities.interest
        val expectedAmount: BigDecimal = penaltyAmount + BigDecimal(interest) + BigDecimal(unpaidTax)
        sut.yearTotal(taxYearWithLiabilities.taxYearLiabilities) mustEqual expectedAmount
      }
    }

  }

  "CheckYourAnswersViewModel" - {

    "return an empty Seq where the offshore pages isn't populated" in {
      val ua        = UserAnswers("id", "session-123")
      val viewModel = sut.create(ua)
      viewModel.legalInterpretationlist mustEqual SummaryListViewModel(rows = Seq.empty)
      viewModel.taxYearLists mustEqual Nil
      viewModel.liabilitiesTotal mustEqual BigDecimal(0)
    }

    "return the correct view for a TaxBeforeFiveYearsSummary is populated" in {
      val year        = yearsService.getEarliestYearByBehaviour(Behaviour.ReasonableExcuse).toString
      val ua          = UserAnswers("id", "session-123").set(TaxBeforeFiveYearsPage, "test").success.value
      val viewModel   = sut.create(ua)
      val summaryList = viewModel.summaryList
      summaryList.rows(0).key mustEqual Key(Text(mess("taxBeforeFiveYears.checkYourAnswersLabel", year)))
    }

    "return the correct view for a TaxBeforeSevenYearsSummary is populated" in {
      val year        = yearsService.getEarliestYearByBehaviour(Behaviour.Careless).toString
      val ua          = UserAnswers("id", "session-123").set(TaxBeforeSevenYearsPage, "test").success.value
      val viewModel   = sut.create(ua)
      val summaryList = viewModel.summaryList
      summaryList.rows(0).key mustEqual Key(Text(mess("taxBeforeSevenYears.checkYourAnswersLabel", year)))
    }

    "return the correct view for a TaxBeforeNineteenYearSummary is populated" in {
      val year        = yearsService.getEarliestYearByBehaviour(Behaviour.Deliberate).toString
      val ua          = UserAnswers("id", "session-123").set(TaxBeforeNineteenYearsPage, "test").success.value
      val viewModel   = sut.create(ua)
      val summaryList = viewModel.summaryList
      summaryList.rows(0).key mustEqual Key(Text(mess("taxBeforeNineteenYears.checkYourAnswersLabel", year)))
    }

    "return the correct view for a TheMaximumValueOfAllAssetsSummary is populated" in {
      val gen = Gen.oneOf(TheMaximumValueOfAllAssets.values.toSeq)
      forAll(gen) { value =>
        val ua          = UserAnswers("id", "session-123").set(TheMaximumValueOfAllAssetsPage, value).success.value
        val viewModel   = sut.create(ua)
        val summaryList = viewModel.legalInterpretationlist
        summaryList.rows(0).key mustEqual Key(Text(mess("theMaximumValueOfAllAssets.checkYourAnswersLabel")))
        summaryList.rows(0).value mustEqual ValueViewModel(Text(mess(s"theMaximumValueOfAllAssets.$value")))
      }
    }

    "return the correct view for a HowMuchTaxHasNotBeenIncludedSummary is populated" in {
      val gen = Gen.oneOf(HowMuchTaxHasNotBeenIncluded.values.toSeq)
      forAll(gen) { value =>
        val ua          = UserAnswers("id", "session-123").set(HowMuchTaxHasNotBeenIncludedPage, value).success.value
        val viewModel   = sut.create(ua)
        val summaryList = viewModel.legalInterpretationlist
        summaryList.rows(0).key mustEqual Key(Text(mess("howMuchTaxHasNotBeenIncluded.checkYourAnswersLabel")))
        summaryList.rows(0).value mustEqual ValueViewModel(Text(mess(s"howMuchTaxHasNotBeenIncluded.$value")))
      }
    }

    "return the correct view for a UnderWhatConsiderationSummary is populated" in {
      val yourLegalInterpretation: Set[YourLegalInterpretation] = Set(YourLegalInterpretation.AnotherIssue)
      val ua                                                    = (for {
        ua1 <- UserAnswers("id", "session-123").set(YourLegalInterpretationPage, yourLegalInterpretation)
        ua2 <- ua1.set(UnderWhatConsiderationPage, "test")
      } yield ua2).success.value

      val viewModel   = sut.create(ua)
      val summaryList = viewModel.legalInterpretationlist
      summaryList.rows(1).key mustEqual Key(Text(mess("underWhatConsideration.checkYourAnswersLabel")))
    }

    "return the correct view for a YourLegalInterpretationSummary is populated" in {
      val yourLegalInterpretation: Set[YourLegalInterpretation] = Set(YourLegalInterpretation.AnotherIssue)
      val value                                                 = YourLegalInterpretation.AnotherIssue.toString
      val ua                                                    = UserAnswers("id", "session-123").set(YourLegalInterpretationPage, yourLegalInterpretation).success.value
      val viewModel                                             = sut.create(ua)
      val summaryList                                           = viewModel.legalInterpretationlist
      summaryList.rows(0).key mustEqual Key(Text(mess("yourLegalInterpretation.checkYourAnswersLabel")))
      summaryList.rows(0).value mustEqual ValueViewModel(Text(mess(s"yourLegalInterpretation.$value")))
    }

    "return an empty Seq where the years page isn't populated" in {
      val ua        = UserAnswers("id", "session-123")
      val viewModel = sut.create(ua)
      viewModel.taxYearLists mustEqual Nil
      viewModel.liabilitiesTotal mustEqual BigDecimal(0)
    }

    "return a single element where one year is populated" in {
      forAll(arbitrary[TaxYearWithLiabilities]) { taxYearWithLiabilities =>
        val yearKey                           = taxYearWithLiabilities.taxYear.startYear.toString
        val offshoreYears: Set[OffshoreYears] = Set(taxYearWithLiabilities.taxYear)
        val ua                                = (for {
          uaWithYears <- UserAnswers("id", "session-123").set(WhichYearsPage, offshoreYears)
          finalUa     <- uaWithYears.setByKey(TaxYearLiabilitiesPage, yearKey, taxYearWithLiabilities)
        } yield finalUa).success.value
        val viewModel                         = sut.create(ua)

        val firstSummaryList = viewModel.taxYearLists(0)._2
        checkSummaryRows(firstSummaryList, taxYearWithLiabilities)

      }
    }

    "return two elements where two years are populated" in {
      val firstTaxYearLiabilities  = TaxYearLiabilities(
        income = BigInt(100),
        chargeableTransfers = BigInt(100),
        capitalGains = BigInt(100),
        unpaidTax = BigInt(100),
        interest = BigInt(100),
        penaltyRate = 100,
        penaltyRateReason = "Reason",
        undeclaredIncomeOrGain = Some("Income or gain"),
        foreignTaxCredit = true
      )
      val firstTaxYear             = TaxYearWithLiabilities(TaxYearStarting(2019), firstTaxYearLiabilities)
      val secondTaxYearLiabilities = TaxYearLiabilities(
        income = BigInt(200),
        chargeableTransfers = BigInt(200),
        capitalGains = BigInt(200),
        unpaidTax = BigInt(200),
        interest = BigInt(200),
        penaltyRate = 200,
        penaltyRateReason = "Other reason",
        undeclaredIncomeOrGain = Some("Income or gain"),
        foreignTaxCredit = false
      )
      val secondTaxYear            = TaxYearWithLiabilities(TaxYearStarting(2018), secondTaxYearLiabilities)

      val offshoreYears: Set[OffshoreYears] = Set(TaxYearStarting(2018), TaxYearStarting(2019))
      val ua                                = (for {
        ua1     <- UserAnswers("id", "session-123").set(WhichYearsPage, offshoreYears)
        ua2     <- ua1.setByKey(TaxYearLiabilitiesPage, "2018", secondTaxYear)
        finalUa <- ua2.setByKey(TaxYearLiabilitiesPage, "2019", firstTaxYear)
      } yield finalUa).success.value
      val viewModel                         = sut.create(ua)

      checkSummaryRows(viewModel.taxYearLists(0)._2, firstTaxYear)
      checkSummaryRows(viewModel.taxYearLists(1)._2, secondTaxYear)
    }

  }

  def checkSummaryRows(summaryList: SummaryList, taxYearWithLiabilities: TaxYearWithLiabilities) = {
    summaryList.rows(0).key mustEqual Key(Text(mess("taxYearLiabilities.income.checkYourAnswersLabel")))
    summaryList.rows(0).value mustEqual ValueViewModel(
      HtmlContent(s"&pound;${taxYearWithLiabilities.taxYearLiabilities.income}")
    )

    summaryList.rows(1).key mustEqual Key(Text(mess("taxYearLiabilities.chargeableTransfers.checkYourAnswersLabel")))
    summaryList.rows(1).value mustEqual ValueViewModel(
      HtmlContent(s"&pound;${taxYearWithLiabilities.taxYearLiabilities.chargeableTransfers}")
    )

    summaryList.rows(2).key mustEqual Key(Text(mess("taxYearLiabilities.capitalGains.checkYourAnswersLabel")))
    summaryList.rows(2).value mustEqual ValueViewModel(
      HtmlContent(s"&pound;${taxYearWithLiabilities.taxYearLiabilities.capitalGains}")
    )

    summaryList.rows(3).key mustEqual Key(Text(mess("taxYearLiabilities.unpaidTax.checkYourAnswersLabel")))
    summaryList.rows(3).value mustEqual ValueViewModel(
      HtmlContent(s"&pound;${taxYearWithLiabilities.taxYearLiabilities.unpaidTax}")
    )

    summaryList.rows(4).key mustEqual Key(Text(mess("taxYearLiabilities.interest.checkYourAnswersLabel")))
    summaryList.rows(4).value mustEqual ValueViewModel(
      HtmlContent(s"&pound;${taxYearWithLiabilities.taxYearLiabilities.interest}")
    )

    summaryList.rows(5).key mustEqual Key(Text(mess("taxYearLiabilities.penaltyRate.checkYourAnswersLabel")))
    summaryList.rows(5).value mustEqual ValueViewModel(
      Text(mess("site.2DP", taxYearWithLiabilities.taxYearLiabilities.penaltyRate) + "%")
    )

    summaryList.rows(6).key mustEqual Key(Text(mess("taxYearLiabilities.penaltyAmount.checkYourAnswersLabel")))

    summaryList.rows(7).key mustEqual Key(Text(mess("taxYearLiabilities.undeclaredIncomeOrGain.checkYourAnswersLabel")))

    summaryList.rows(8).key mustEqual Key(Text(mess("taxYearLiabilities.foreignTaxCredit.checkYourAnswersLabel")))
    summaryList.rows(8).value mustEqual ValueViewModel(
      Text(if (taxYearWithLiabilities.taxYearLiabilities.foreignTaxCredit) mess("site.yes") else mess("site.no"))
    )

    summaryList.rows(9).key mustEqual Key(Text(mess("taxYearLiabilities.amountDue.checkYourAnswersLabel")))
  }

}
