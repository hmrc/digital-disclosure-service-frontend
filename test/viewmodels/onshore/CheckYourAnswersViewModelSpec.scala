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
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.Key
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.govuk.summarylist._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalacheck.Arbitrary.arbitrary
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
        val unpaidTax = BigDecimal(taxYearWithLiabilities.taxYearLiabilities.unpaidTax) + BigDecimal(taxYearWithLiabilities.taxYearLiabilities.niContributions) 
        val expectedAmount = (penaltyRate * unpaidTax) / 100
        sut.penaltyAmount(taxYearWithLiabilities.taxYearLiabilities) mustEqual expectedAmount
      }
    }

  }

  "yearTotal" - {

    "add the penalty amount to the unpaid tax and the interest" in {
      forAll(arbitrary[OnshoreTaxYearWithLiabilities]) { taxYearWithLiabilities =>
        val penaltyRate = taxYearWithLiabilities.taxYearLiabilities.penaltyRate
        val unpaidTax: BigDecimal = BigDecimal(taxYearWithLiabilities.taxYearLiabilities.unpaidTax) + BigDecimal(taxYearWithLiabilities.taxYearLiabilities.niContributions) 
        val penaltyAmount: BigDecimal = (penaltyRate / 100) * unpaidTax
        val interest = taxYearWithLiabilities.taxYearLiabilities.interest
        val expectedAmount: BigDecimal = penaltyAmount + BigDecimal(interest) + unpaidTax
        sut.yearTotal(taxYearWithLiabilities.taxYearLiabilities) mustEqual expectedAmount
      }
    }

  }

  "CheckYourAnswersViewModel" - {

    "return an empty Seq where the onshore pages isn't populated" in {
      val ua = UserAnswers("id", "session-123")
      val viewModel = sut.create(ua)
      viewModel.taxYearLists mustEqual Nil
    }

    "return the correct view for a TaxBeforeFiveYearsOnshoreSummary is populated" in {
      val year = yearsService.getEarliestYearByBehaviour(Behaviour.Careless).toString
      val ua = UserAnswers("id", "session-123").set(TaxBeforeFiveYearsOnshorePage, "test").success.value
      val viewModel = sut.create(ua)
      val summaryList = viewModel.summaryList
      summaryList.rows(0).key mustEqual Key(Text(mess("taxBeforeFiveYears.checkYourAnswersLabel", year)))
    }

    "return the correct view for a TaxBeforeThreeYearsOnshoreSummary is populated" in {
      val year = yearsService.getEarliestYearByBehaviour(Behaviour.ReasonableExcuse).toString
      val ua = UserAnswers("id", "session-123").set(TaxBeforeThreeYearsOnshorePage, "test").success.value
      val viewModel = sut.create(ua)
      val summaryList = viewModel.summaryList
      summaryList.rows(0).key mustEqual Key(Text(mess("taxBeforeThreeYears.checkYourAnswersLabel", year)))
    }

    "return the correct view for a TaxBeforeNineteenYearsOnshoreSummary is populated" in {
      val year = yearsService.getEarliestYearByBehaviour(Behaviour.Deliberate).toString
      val ua = UserAnswers("id", "session-123").set(TaxBeforeNineteenYearsOnshorePage, "test").success.value
      val viewModel = sut.create(ua)
      val summaryList = viewModel.summaryList
      summaryList.rows(0).key mustEqual Key(Text(mess("taxBeforeNineteenYears.checkYourAnswersLabel", year)))
    }

    "return an empty Seq where the years page isn't populated" in {
      val ua = UserAnswers("id", "session-123")
      val viewModel = sut.create(ua)
      viewModel.taxYearLists mustEqual Nil
    }

    "return a single element where one year is populated" in {
      forAll(arbitrary[OnshoreTaxYearWithLiabilities]) { taxYearWithLiabilities => 
        val yearKey = taxYearWithLiabilities.taxYear.startYear.toString
        val onshoreYears: Set[OnshoreYears] = Set(taxYearWithLiabilities.taxYear)
        val ua = (for {
          uaWithYears <- UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, onshoreYears)
          finalUa <- uaWithYears.setByKey(OnshoreTaxYearLiabilitiesPage, yearKey, taxYearWithLiabilities)
        } yield finalUa).success.value
        val viewModel = sut.create(ua)

        val firstSummaryList = viewModel.taxYearLists(0)._2
        checkSummaryRows(firstSummaryList, taxYearWithLiabilities)

      }
    }

    "create" - {
      "return rows for all populated data" in {
        val date = LocalDate.now
        val liabilities = OnshoreTaxYearLiabilities(
          lettingIncome = Some(BigInt(2000)),
          gains = Some(BigInt(2000)),
          unpaidTax = BigInt(2000),
          niContributions = BigInt(2000),
          interest = BigInt(2000),
          penaltyRate = 12,
          penaltyRateReason = "Reason",
          undeclaredIncomeOrGain = Some("Income or gain"),
          residentialTaxReduction = Some(false)
        )
        val whySet: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHasExcuse)
        val yearsSet: Set[OnshoreYears] = Set(OnshoreYearStarting(2012))
        val corporationTax = Seq(CorporationTaxLiability (
          periodEnd = date,
          howMuchIncome = BigInt(2000),
          howMuchUnpaid = BigInt(2000),
          howMuchInterest = BigInt(2000),
          penaltyRate = 123,
          penaltyRateReason = "Some reason"
        ))
        val directorLoan = Seq(DirectorLoanAccountLiabilities (
          name = "Name",
          periodEnd = date,
          overdrawn = BigInt(2000),
          unpaidTax = BigInt(2000),
          interest = BigInt(2000),
          penaltyRate = 123,
          penaltyRateReason = "Some reason"
        ))
        val lettingProperty = Seq(LettingProperty(
          address = None,
          dateFirstLetOut = Some(date),
          stoppedBeingLetOut = Some(true),
          noLongerBeingLetOut = None,
          fhl = Some(false),
          isJointOwnership = Some(true),
          isMortgageOnProperty = Some(false),
          percentageIncomeOnProperty = Some(123),
          wasFurnished = Some(false),
          typeOfMortgage = None,
          otherTypeOfMortgage = Some("Some mortgage"),
          wasPropertyManagerByAgent = Some(true),
          didTheLettingAgentCollectRentOnYourBehalf = Some(false)
        ))
        val whichLiabilitiesSet: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] = Set(WhatOnshoreLiabilitiesDoYouNeedToDisclose.BusinessIncome)
        val pages = List(
          PageWithValue(WhyAreYouMakingThisOnshoreDisclosurePage, whySet),
          PageWithValue(ReasonableExcuseOnshorePage, ReasonableExcuseOnshore("Some excuse", "Some years")),
          PageWithValue(ReasonableCareOnshorePage, ReasonableCareOnshore("Some excuse", "Some years")),
          PageWithValue(ReasonableExcuseForNotFilingOnshorePage, ReasonableExcuseForNotFilingOnshore("Some excuse", "Some years")),
          PageWithValue(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, whichLiabilitiesSet),
          PageWithValue(WhichOnshoreYearsPage, yearsSet),
          PageWithValue(NotIncludedSingleTaxYearPage, "Not included year"),
          PageWithValue(NotIncludedMultipleTaxYearsPage, "Not included years"),
          PageWithValue(TaxBeforeThreeYearsOnshorePage, "Some liabilities 1"),
          PageWithValue(TaxBeforeFiveYearsOnshorePage, "Some liabilities 2"),
          PageWithValue(TaxBeforeNineteenYearsOnshorePage, "Some liabilities 3"),
          PageWithValue(CDFOnshorePage, true),
          PageWithValue(OnshoreTaxYearLiabilitiesPage, Map("2012" -> OnshoreTaxYearWithLiabilities(OnshoreYearStarting(2012), liabilities))),
          PageWithValue(ResidentialReductionPage, Map("2012" -> BigInt(123))),
          PageWithValue(LettingPropertyPage, lettingProperty),
          PageWithValue(AreYouAMemberOfAnyLandlordAssociationsPage, true),
          PageWithValue(WhichLandlordAssociationsAreYouAMemberOfPage, "Some associations"),
          PageWithValue(HowManyPropertiesDoYouCurrentlyLetOutPage, "Some properties"),
          PageWithValue(CorporationTaxLiabilityPage, corporationTax),
          PageWithValue(DirectorLoanAccountLiabilitiesPage, directorLoan),
        )
        val userAnswers = PageWithValue.pagesToUserAnswers(pages, UserAnswers("id", "session-123")).success.value
        sut.create(userAnswers).liabilitiesTotal mustEqual 19400
      }
    }

  }

  def checkSummaryRows(summaryList: SummaryList, onshoreTaxYearLiabilities: OnshoreTaxYearWithLiabilities) = {
    summaryList.rows(0).key mustEqual Key(Text(mess("onshoreTaxYearLiabilities.unpaidTax.checkYourAnswersLabel")))
    summaryList.rows(0).value mustEqual ValueViewModel(Text(s"&pound;${onshoreTaxYearLiabilities.taxYearLiabilities.unpaidTax}"))

    summaryList.rows(1).key mustEqual Key(Text(mess("onshoreTaxYearLiabilities.niContributions.checkYourAnswersLabel")))
    summaryList.rows(1).value mustEqual ValueViewModel(Text(s"&pound;${onshoreTaxYearLiabilities.taxYearLiabilities.niContributions}"))

    summaryList.rows(2).key mustEqual Key(Text(mess("onshoreTaxYearLiabilities.interest.checkYourAnswersLabel")))
    summaryList.rows(2).value mustEqual ValueViewModel(Text(s"&pound;${onshoreTaxYearLiabilities.taxYearLiabilities.interest}"))

    summaryList.rows(3).key mustEqual Key(Text(mess("onshoreTaxYearLiabilities.penaltyRate.checkYourAnswersLabel")))
    summaryList.rows(3).value mustEqual ValueViewModel(Text(mess("site.2DP", onshoreTaxYearLiabilities.taxYearLiabilities.penaltyRate)+"%"))    
  }
  
}

