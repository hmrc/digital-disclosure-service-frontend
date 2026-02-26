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

package navigation

import base.SpecBase
import controllers.offshore.routes
import models.RelatesTo.AnIndividual
import models.WhyAreYouMakingThisDisclosure.{DeliberateInaccurateReturn, DeliberatelyDidNotFile, DeliberatelyDidNotNotify}
import pages._
import models._
import models.YourLegalInterpretation._
import uk.gov.hmrc.time.CurrentTaxYear
import config.Country

import java.time.LocalDate

class OffshoreNavigatorSpec extends SpecBase with CurrentTaxYear {

  val navigator = new OffshoreNavigator

  def now = () => LocalDate.now()

  "Offshore Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {
        case object UnknownPage extends Page
        navigator.nextPage(
          UnknownPage,
          NormalMode,
          UserAnswers("id", "session-123")
        ) mustBe controllers.routes.IndexController.onPageLoad
      }

      "must go from WhyAreYouMakingThisDisclosurePage to WhichYearsController when selected a deliberate behaviour, and also selected that they're an estate" in {
        val set: Set[WhyAreYouMakingThisDisclosure] = Set(
          WhyAreYouMakingThisDisclosure.DeliberatelyDidNotNotify,
          WhyAreYouMakingThisDisclosure.DeliberateInaccurateReturn,
          WhyAreYouMakingThisDisclosure.DeliberatelyDidNotFile
        )
        val userAnswers                             = (for {
          ua      <- UserAnswers("id", "session-123").set(WhyAreYouMakingThisDisclosurePage, set)
          finalUa <- ua.set(RelatesToPage, RelatesTo.AnEstate)
        } yield finalUa).success.value
        navigator.nextPage(
          WhyAreYouMakingThisDisclosurePage,
          NormalMode,
          userAnswers
        ) mustBe routes.WhichYearsController.onPageLoad(NormalMode)
      }

      "must go from WhyAreYouMakingThisDisclosurePage to WhichYearsController when selected any other option(s)" in {
        val set: Set[WhyAreYouMakingThisDisclosure] = Set(WhyAreYouMakingThisDisclosure.InaccurateReturnNoCare)
        val userAnswers                             = UserAnswers("id", "session-123").set(WhyAreYouMakingThisDisclosurePage, set).success.value
        navigator.nextPage(
          WhyAreYouMakingThisDisclosurePage,
          NormalMode,
          userAnswers
        ) mustBe routes.WhichYearsController.onPageLoad(NormalMode)
      }

      "must go from ContractualDisclosureFacilityPage to YouHaveLeftTheDDSController when selected any other option(s) & false" in {
        val userAnswers = UserAnswers("id", "session-123").set(ContractualDisclosureFacilityPage, false).success.value
        navigator.nextPage(
          ContractualDisclosureFacilityPage,
          NormalMode,
          userAnswers
        ) mustBe routes.YouHaveLeftTheDDSController.onPageLoad(NormalMode)
      }

      "must go from ContractualDisclosureFacilityPage to WhichYearsController when selected any other option(s)" in {
        navigator.nextPage(
          ContractualDisclosureFacilityPage,
          NormalMode,
          UserAnswers("id", "session-123")
        ) mustBe routes.WhichYearsController.onPageLoad(NormalMode)
      }

      "must go from WhatIsYourReasonableExcusePage to WhichYearsController when selected any other option(s)" in {
        navigator.nextPage(
          WhatIsYourReasonableExcusePage,
          NormalMode,
          UserAnswers("id", "session-123")
        ) mustBe routes.WhichYearsController.onPageLoad(NormalMode)
      }

      "must go from WhatReasonableCareDidYouTakePage to WhichYearsController when selected any other option(s)" in {
        navigator.nextPage(
          WhatReasonableCareDidYouTakePage,
          NormalMode,
          UserAnswers("id", "session-123")
        ) mustBe routes.WhichYearsController.onPageLoad(NormalMode)
      }

      "must go from WhatIsYourReasonableExcuseForNotFilingReturnPage to WhichYearsController when selected any other option(s)" in {
        navigator.nextPage(
          WhatIsYourReasonableExcuseForNotFilingReturnPage,
          NormalMode,
          UserAnswers("id", "session-123")
        ) mustBe routes.WhichYearsController.onPageLoad(NormalMode)
      }

      "must go from CountryOfYourOffshoreLiabilityPage to CountriesOrTerritoriesController" in {
        navigator.nextPage(
          CountryOfYourOffshoreLiabilityPage,
          NormalMode,
          UserAnswers("id", "session-123")
        ) mustBe routes.CountriesOrTerritoriesController.onPageLoad(NormalMode)
      }

      "must go from CountriesOrTerritoriesPage to CountryOfYourOffshoreLiabilityController if the user select yes" in {
        val userAnswers = UserAnswers("id", "session-123").set(CountriesOrTerritoriesPage, true).success.value
        navigator.nextPage(
          CountriesOrTerritoriesPage,
          NormalMode,
          userAnswers
        ) mustBe routes.CountryOfYourOffshoreLiabilityController.onPageLoad(None, NormalMode)
      }

      "must go from CountriesOrTerritoriesPage to TaxYearLiabilitiesController if the user select no" in {
        val userAnswers = UserAnswers("id", "session-123").set(CountriesOrTerritoriesPage, false).success.value
        navigator.nextPage(
          CountriesOrTerritoriesPage,
          NormalMode,
          userAnswers
        ) mustBe routes.TaxYearLiabilitiesController.onPageLoad(0, NormalMode)
      }

      Seq(
        YourResidenceStatus,
        YourDomicileStatus,
        TheRemittanceBasis,
        HowIncomeArisingInATrust,
        TheTransferOfAssets,
        HowIncomeArisingInAnOffshore,
        InheritanceTaxIssues,
        WhetherIncomeShouldBeTaxed
      ).foreach { option =>
        s"must go from YourLegalInterpretationPage to UnderWhatConsiderationController when selected option $option & anotherIssue" in {
          val set: Set[YourLegalInterpretation]        = Set(option)
          val updatedSet: Set[YourLegalInterpretation] = Set(AnotherIssue)

          val userAnswers = for {
            answer        <- UserAnswers("id", "session-123").set(YourLegalInterpretationPage, set)
            updatedAnswer <- answer.set(YourLegalInterpretationPage, updatedSet)
          } yield updatedAnswer

          navigator.nextPage(
            YourLegalInterpretationPage,
            NormalMode,
            userAnswers.success.value
          ) mustBe routes.UnderWhatConsiderationController.onPageLoad(NormalMode)
        }
      }

      Seq(
        YourResidenceStatus,
        YourDomicileStatus,
        TheRemittanceBasis,
        HowIncomeArisingInATrust,
        TheTransferOfAssets,
        HowIncomeArisingInAnOffshore,
        InheritanceTaxIssues,
        WhetherIncomeShouldBeTaxed
      ).foreach { option =>
        s"must go from YourLegalInterpretationPage to HowMuchTaxHasNotBeenIncludedController when selected option $option" in {
          val set: Set[YourLegalInterpretation] = Set(option)
          val userAnswers                       = UserAnswers("id", "session-123").set(YourLegalInterpretationPage, set).success.value
          navigator.nextPage(
            YourLegalInterpretationPage,
            NormalMode,
            userAnswers
          ) mustBe routes.HowMuchTaxHasNotBeenIncludedController.onPageLoad(NormalMode)
        }
      }

      "must go from YourLegalInterpretationPage to UnderWhatConsiderationController when selected option anotherIssue" in {
        val set: Set[YourLegalInterpretation] = Set(AnotherIssue)
        val userAnswers                       = UserAnswers("id", "session-123").set(YourLegalInterpretationPage, set).success.value
        navigator.nextPage(
          YourLegalInterpretationPage,
          NormalMode,
          userAnswers
        ) mustBe routes.UnderWhatConsiderationController.onPageLoad(NormalMode)
      }

      "must go from YourLegalInterpretationPage to TheMaximumValueOfAllAssetsController when selected option NoExclusion" in {
        val set: Set[YourLegalInterpretation] = Set(NoExclusion)
        val userAnswers                       = UserAnswers("id", "session-123").set(YourLegalInterpretationPage, set).success.value
        navigator.nextPage(
          YourLegalInterpretationPage,
          NormalMode,
          userAnswers
        ) mustBe routes.TheMaximumValueOfAllAssetsController.onPageLoad(NormalMode)
      }

      "must go from UnderWhatConsiderationPage to HowMuchTaxHasNotBeenIncludedController" in {
        navigator.nextPage(
          UnderWhatConsiderationPage,
          NormalMode,
          UserAnswers("id", "session-123")
        ) mustBe routes.HowMuchTaxHasNotBeenIncludedController.onPageLoad(NormalMode)
      }

      "must go from HowMuchTaxHasNotBeenIncludedPage to TheMaximumValueOfAllAssetsController" in {
        navigator.nextPage(
          HowMuchTaxHasNotBeenIncludedPage,
          NormalMode,
          UserAnswers("id", "session-123")
        ) mustBe routes.TheMaximumValueOfAllAssetsController.onPageLoad(NormalMode)
      }

      "must go from WhichYearsPage to TaxBeforeFiveYearsController when selected option ReasonableExcusePriorTo" in {
        val set: Set[OffshoreYears] = Set(ReasonableExcusePriorTo)
        val userAnswers             = UserAnswers("id", "session-123").set(WhichYearsPage, set).success.value
        navigator.nextPage(WhichYearsPage, NormalMode, userAnswers) mustBe routes.TaxBeforeFiveYearsController
          .onPageLoad(NormalMode)
      }

      "must go from WhichYearsPage to TaxBeforeSevenYearsController when selected option CarelessPriorTo" in {
        val set: Set[OffshoreYears] = Set(CarelessPriorTo)
        val userAnswers             = UserAnswers("id", "session-123").set(WhichYearsPage, set).success.value
        navigator.nextPage(WhichYearsPage, NormalMode, userAnswers) mustBe routes.TaxBeforeSevenYearsController
          .onPageLoad(NormalMode)
      }

      "must go from WhichYearsPage to TaxBeforeNineteenYearsController when selected option ReasonableExcusePriorTo" in {
        val set: Set[OffshoreYears] = Set(DeliberatePriorTo)
        val userAnswers             = UserAnswers("id", "session-123").set(WhichYearsPage, set).success.value
        navigator.nextPage(WhichYearsPage, NormalMode, userAnswers) mustBe routes.TaxBeforeNineteenYearsController
          .onPageLoad(NormalMode)
      }

      "must go from WhichYearsPage to YouHaveNotIncludedTheTaxYearController when not selected an entire interval" in {
        val year                    = current.back(1).startYear
        val year2                   = current.back(3).startYear
        val set: Set[OffshoreYears] = Set(TaxYearStarting(year), TaxYearStarting(year2))
        val userAnswers             = UserAnswers("id", "session-123").set(WhichYearsPage, set).success.value
        navigator.nextPage(WhichYearsPage, NormalMode, userAnswers) mustBe routes.YouHaveNotIncludedTheTaxYearController
          .onPageLoad(NormalMode)
      }

      "must go from WhichYearsPage to YouHaveNotIncludedTheTaxYearController when multiple intervals are missing" in {
        val year                    = current.back(1).startYear
        val year2                   = current.back(3).startYear
        val year3                   = current.back(5).startYear
        val set: Set[OffshoreYears] = Set(TaxYearStarting(year), TaxYearStarting(year2), TaxYearStarting(year3))
        val countryCode             = "AFG"
        val countriesMap            = Map(countryCode -> Country(countryCode, "Afghanistan"))
        val userAnswers             = UserAnswers("id", "session-123").set(WhichYearsPage, set).success.value
        val updatedUserAnswers      = userAnswers.set(CountryOfYourOffshoreLiabilityPage, countriesMap).success.value
        navigator.nextPage(
          WhichYearsPage,
          NormalMode,
          updatedUserAnswers
        ) mustBe routes.YouHaveNotSelectedCertainTaxYearController.onPageLoad(NormalMode)
      }

      "must go from WhichYearsPage to CountriesOrTerritoriesController" in {
        val year                    = current.back(1).startYear
        val set: Set[OffshoreYears] = Set(TaxYearStarting(year))
        val countryCode             = "AFG"
        val countriesMap            = Map(countryCode -> Country(countryCode, "Afghanistan"))
        val userAnswers             = UserAnswers("id", "session-123").set(WhichYearsPage, set).success.value
        val updatedUserAnswers      = userAnswers.set(CountryOfYourOffshoreLiabilityPage, countriesMap).success.value
        navigator.nextPage(
          WhichYearsPage,
          NormalMode,
          updatedUserAnswers
        ) mustBe routes.CountriesOrTerritoriesController.onPageLoad(NormalMode)
      }

      "must go from TaxBeforeFiveYearsPage to YouHaveNoOffshoreLiabilitiesController when only selected option ReasonableExcusePriorTo and selected both Onshore and Offshore liabilities" in {
        val set: Set[OffshoreYears] = Set(ReasonableExcusePriorTo)
        val userAnswers             = for {
          uaYears    <- UserAnswers("id", "session-123").set(WhichYearsPage, set)
          uaOnshore  <- uaYears.set(OnshoreLiabilitiesPage, true)
          uaOffshore <- uaOnshore.set(OffshoreLiabilitiesPage, true)
        } yield uaOffshore

        val userAnswersValue = userAnswers.success.value
        navigator.nextPage(
          TaxBeforeFiveYearsPage,
          NormalMode,
          userAnswersValue
        ) mustBe routes.YouHaveNoOffshoreLiabilitiesController.onPageLoad
      }

      "must go from TaxBeforeFiveYearsPage to MakingNilDisclosureController when only selected option ReasonableExcusePriorTo" in {
        val set: Set[OffshoreYears] = Set(ReasonableExcusePriorTo)
        val userAnswers             = UserAnswers("id", "session-123").set(WhichYearsPage, set).success.value
        navigator.nextPage(
          TaxBeforeFiveYearsPage,
          NormalMode,
          userAnswers
        ) mustBe routes.MakingNilDisclosureController.onPageLoad
      }

      "must go from TaxBeforeFiveYearsPage to CountryOfYourOffshoreLiabilityController when selected more than one TaxYear option including ReasonableExcusePriorTo" in {
        val year                    = current.back(1).startYear
        val set: Set[OffshoreYears] = Set(ReasonableExcusePriorTo, TaxYearStarting(year))
        val userAnswers             = UserAnswers("id", "session-123").set(WhichYearsPage, set).success.value
        navigator.nextPage(
          TaxBeforeFiveYearsPage,
          NormalMode,
          userAnswers
        ) mustBe routes.CountryOfYourOffshoreLiabilityController.onPageLoad(None, NormalMode)
      }

      "must go from TaxBeforeSevenYearsPage to YouHaveNoOffshoreLiabilitiesController when only selected option CarelessPriorTo and selected both Onshore and Offshore liabilities" in {
        val set: Set[OffshoreYears] = Set(CarelessPriorTo)
        val userAnswers             = for {
          uaYears    <- UserAnswers("id", "session-123").set(WhichYearsPage, set)
          uaOnshore  <- uaYears.set(OnshoreLiabilitiesPage, true)
          uaOffshore <- uaOnshore.set(OffshoreLiabilitiesPage, true)
        } yield uaOffshore

        val userAnswersValue = userAnswers.success.value
        navigator.nextPage(
          TaxBeforeSevenYearsPage,
          NormalMode,
          userAnswersValue
        ) mustBe routes.YouHaveNoOffshoreLiabilitiesController.onPageLoad
      }

      "must go from TaxBeforeSevenYearsPage to MakingNilDisclosureController when only selected option CarelessPriorTo" in {
        val set: Set[OffshoreYears] = Set(CarelessPriorTo)
        val userAnswers             = UserAnswers("id", "session-123").set(WhichYearsPage, set).success.value
        navigator.nextPage(
          TaxBeforeSevenYearsPage,
          NormalMode,
          userAnswers
        ) mustBe routes.MakingNilDisclosureController.onPageLoad
      }

      "must go from TaxBeforeSevenYearsPage to CountryOfYourOffshoreLiabilityController when selected more than one TaxYear option including CarelessPriorTo" in {
        val year                    = current.back(1).startYear
        val set: Set[OffshoreYears] = Set(CarelessPriorTo, TaxYearStarting(year))
        val userAnswers             = UserAnswers("id", "session-123").set(WhichYearsPage, set).success.value
        navigator.nextPage(
          TaxBeforeSevenYearsPage,
          NormalMode,
          userAnswers
        ) mustBe routes.CountryOfYourOffshoreLiabilityController.onPageLoad(None, NormalMode)
      }

      "must go from TaxBeforeNineteenYearsPage to YouHaveNoOffshoreLiabilitiesController when only selected option DeliberatePriorTo and selected both Onshore and Offshore liabilities" in {
        val set: Set[OffshoreYears] = Set(DeliberatePriorTo)
        val userAnswers             = for {
          uaYears    <- UserAnswers("id", "session-123").set(WhichYearsPage, set)
          uaOnshore  <- uaYears.set(OnshoreLiabilitiesPage, true)
          uaOffshore <- uaOnshore.set(OffshoreLiabilitiesPage, true)
        } yield uaOffshore

        val userAnswersValue = userAnswers.success.value
        navigator.nextPage(
          TaxBeforeNineteenYearsPage,
          NormalMode,
          userAnswersValue
        ) mustBe routes.YouHaveNoOffshoreLiabilitiesController.onPageLoad
      }

      "must go from TaxBeforeNineteenYearsPage to MakingNilDisclosureController when only selected option DeliberatePriorTo" in {
        val set: Set[OffshoreYears] = Set(DeliberatePriorTo)
        val userAnswers             = UserAnswers("id", "session-123").set(WhichYearsPage, set).success.value
        navigator.nextPage(
          TaxBeforeNineteenYearsPage,
          NormalMode,
          userAnswers
        ) mustBe routes.MakingNilDisclosureController.onPageLoad
      }

      "must go from TaxBeforeNineteenYearsPage to CountryOfYourOffshoreLiabilityController when selected more than one TaxYear option including DeliberatePriorTo" in {
        val year                    = current.back(1).startYear
        val set: Set[OffshoreYears] = Set(DeliberatePriorTo, TaxYearStarting(year))
        val userAnswers             = UserAnswers("id", "session-123").set(WhichYearsPage, set).success.value
        navigator.nextPage(
          TaxBeforeNineteenYearsPage,
          NormalMode,
          userAnswers
        ) mustBe routes.CountryOfYourOffshoreLiabilityController.onPageLoad(None, NormalMode)
      }

      "must go from YouHaveNotIncludedTheTaxYearPage to CountryOfYourOffshoreLiabilityController" in {
        navigator.nextPage(
          YouHaveNotIncludedTheTaxYearPage,
          NormalMode,
          UserAnswers("id", "session-123")
        ) mustBe routes.CountryOfYourOffshoreLiabilityController.onPageLoad(None, NormalMode)
      }

      "must go from YouHaveNotSelectedCertainTaxYearPage to CountryOfYourOffshoreLiabilityController" in {
        navigator.nextPage(
          YouHaveNotSelectedCertainTaxYearPage,
          NormalMode,
          UserAnswers("id", "session-123")
        ) mustBe routes.CountryOfYourOffshoreLiabilityController.onPageLoad(None, NormalMode)
      }

    }

    "in Check mode" - {

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {

        case object UnknownPage extends Page
        navigator.nextPage(
          UnknownPage,
          CheckMode,
          UserAnswers("id", "session-123")
        ) mustBe routes.CheckYourAnswersController.onPageLoad
      }
    }

    "nextTaxYearLiabilitiesPage" - {

      "must take the user to the CYA page in check mode" in {
        val whichYears: Set[OffshoreYears] = Set(TaxYearStarting(2021), TaxYearStarting(2020), TaxYearStarting(2019))
        val userAnswersWithTaxYears        =
          UserAnswers(userAnswersId, "session-123").set(WhichYearsPage, whichYears).success.value

        navigator.nextTaxYearLiabilitiesPage(
          0,
          false,
          CheckMode,
          userAnswersWithTaxYears
        ) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must take the user to the foreign tax credit page when the param is true, it is in CheckMode and there is a change" in {
        val whichYears: Set[OffshoreYears] =
          Set(TaxYearStarting(2021), TaxYearStarting(2020), TaxYearStarting(2019), TaxYearStarting(2018))
        val userAnswersWithTaxYears        =
          UserAnswers(userAnswersId, "session-123").set(WhichYearsPage, whichYears).success.value

        navigator.nextTaxYearLiabilitiesPage(
          0,
          true,
          CheckMode,
          userAnswersWithTaxYears,
          true
        ) mustBe routes.ForeignTaxCreditController.onPageLoad(0, CheckMode)
      }

      "must increment the index and tax the user to the tax year liability page when there more years in the which years list" in {
        val whichYears: Set[OffshoreYears] =
          Set(TaxYearStarting(2021), TaxYearStarting(2020), TaxYearStarting(2019), TaxYearStarting(2018))
        val userAnswersWithTaxYears        =
          UserAnswers(userAnswersId, "session-123").set(WhichYearsPage, whichYears).success.value

        navigator.nextTaxYearLiabilitiesPage(
          0,
          false,
          NormalMode,
          userAnswersWithTaxYears
        ) mustBe routes.TaxYearLiabilitiesController.onPageLoad(1, NormalMode)
        navigator.nextTaxYearLiabilitiesPage(
          1,
          false,
          NormalMode,
          userAnswersWithTaxYears
        ) mustBe routes.TaxYearLiabilitiesController.onPageLoad(2, NormalMode)
        navigator.nextTaxYearLiabilitiesPage(
          2,
          false,
          NormalMode,
          userAnswersWithTaxYears
        ) mustBe routes.TaxYearLiabilitiesController.onPageLoad(3, NormalMode)
      }

      "must take the user to the next page when there are no more years in the which years list" in {
        val whichYears: Set[OffshoreYears] =
          Set(TaxYearStarting(2021), TaxYearStarting(2020), TaxYearStarting(2019), TaxYearStarting(2018))
        val userAnswersWithTaxYears        =
          UserAnswers(userAnswersId, "session-123").set(WhichYearsPage, whichYears).success.value

        navigator.nextTaxYearLiabilitiesPage(
          3,
          false,
          NormalMode,
          userAnswersWithTaxYears
        ) mustBe routes.YourLegalInterpretationController.onPageLoad(NormalMode)
      }

      "must take the user to the foreign tax credit page where the param is true" in {
        val whichYears: Set[OffshoreYears] =
          Set(TaxYearStarting(2021), TaxYearStarting(2020), TaxYearStarting(2019), TaxYearStarting(2018))
        val userAnswersWithTaxYears        =
          UserAnswers(userAnswersId, "session-123").set(WhichYearsPage, whichYears).success.value

        navigator.nextTaxYearLiabilitiesPage(
          3,
          true,
          NormalMode,
          userAnswersWithTaxYears
        ) mustBe routes.ForeignTaxCreditController.onPageLoad(3, NormalMode)
      }

      "must go from CountryOfYourOffshoreLiabilityPage to CountriesOrTerritoriesController" in {
        navigator.nextPage(
          CountryOfYourOffshoreLiabilityPage,
          CheckMode,
          UserAnswers("id", "session-123")
        ) mustBe routes.CountriesOrTerritoriesController.onPageLoad(CheckMode)
      }

      "must go from CountriesOrTerritoriesPage to CountryOfYourOffshoreLiabilityController if the user select yes" in {
        val userAnswers = UserAnswers("id", "session-123").set(CountriesOrTerritoriesPage, true).success.value
        navigator.nextPage(
          CountriesOrTerritoriesPage,
          CheckMode,
          userAnswers
        ) mustBe routes.CountryOfYourOffshoreLiabilityController.onPageLoad(None, CheckMode)
      }

      "must go from CountriesOrTerritoriesPage to CYA page if the user select no" in {
        val userAnswers = UserAnswers("id", "session-123").set(CountriesOrTerritoriesPage, false).success.value
        navigator.nextPage(
          CountriesOrTerritoriesPage,
          CheckMode,
          userAnswers
        ) mustBe routes.CheckYourAnswersController.onPageLoad
      }

    }

    "must go from WhyAreYouMakingThisDisclosurePage to WhyDidYouNotNotifyController when DidNotNotifyHMRC selected" in {
      val set: Set[WhyAreYouMakingThisDisclosure] = Set(WhyAreYouMakingThisDisclosure.DidNotNotifyHMRC)
      val userAnswers                             = UserAnswers("id", "session-123").set(WhyAreYouMakingThisDisclosurePage, set).success.value
      navigator.nextPage(WhyAreYouMakingThisDisclosurePage, NormalMode, userAnswers) mustBe
        routes.WhyDidYouNotNotifyController.onPageLoad(NormalMode)
    }

    "must go from WhyAreYouMakingThisDisclosurePage to WhyDidYouNotFileAReturnOnTimeOffshoreController when DidNotFile selected" in {
      val set: Set[WhyAreYouMakingThisDisclosure] = Set(WhyAreYouMakingThisDisclosure.DidNotFile)
      val userAnswers                             = UserAnswers("id", "session-123").set(WhyAreYouMakingThisDisclosurePage, set).success.value
      navigator.nextPage(WhyAreYouMakingThisDisclosurePage, NormalMode, userAnswers) mustBe
        routes.WhyDidYouNotFileAReturnOnTimeOffshoreController.onPageLoad(NormalMode)
    }

    "must go from WhyAreYouMakingThisDisclosurePage to WhyYouSubmittedAnInaccurateReturnController when InaccurateReturn selected" in {
      val set: Set[WhyAreYouMakingThisDisclosure] = Set(WhyAreYouMakingThisDisclosure.InaccurateReturn)
      val userAnswers                             = UserAnswers("id", "session-123").set(WhyAreYouMakingThisDisclosurePage, set).success.value
      navigator.nextPage(WhyAreYouMakingThisDisclosurePage, NormalMode, userAnswers) mustBe
        routes.WhyYouSubmittedAnInaccurateReturnController.onPageLoad(NormalMode)
    }

    "must go from WhyDidYouNotNotifyPage to WhatIsYourReasonableExcuseController when ReasonableExcuse selected" in {
      val userAnswers = UserAnswers("id", "session-123")
        .set(WhyDidYouNotNotifyPage, Set[WhyDidYouNotNotify](WhyDidYouNotNotify.ReasonableExcuse))
        .success
        .value
      navigator.nextPage(WhyDidYouNotNotifyPage, NormalMode, userAnswers) mustBe
        routes.WhatIsYourReasonableExcuseController.onPageLoad(NormalMode)
    }

    "must go from WhyDidYouNotNotifyPage to WhyDidYouNotFileAReturnOnTimeOffshoreController when DidNotFile also selected on page 1" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(
                 WhyAreYouMakingThisDisclosurePage,
                 Set[WhyAreYouMakingThisDisclosure](WhyAreYouMakingThisDisclosure.DidNotFile)
               )
        ua2 <-
          ua.set(WhyDidYouNotNotifyPage, Set[WhyDidYouNotNotify](WhyDidYouNotNotify.NotDeliberatelyNoReasonableExcuse))
      } yield ua2).success.value
      navigator.nextPage(WhyDidYouNotNotifyPage, NormalMode, userAnswers) mustBe
        routes.WhyDidYouNotFileAReturnOnTimeOffshoreController.onPageLoad(NormalMode)
    }

    "must go from WhyDidYouNotNotifyPage to WhyYouSubmittedAnInaccurateReturnController when InaccurateReturn also selected on page 1" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(
                 WhyAreYouMakingThisDisclosurePage,
                 Set[WhyAreYouMakingThisDisclosure](WhyAreYouMakingThisDisclosure.InaccurateReturn)
               )
        ua2 <-
          ua.set(WhyDidYouNotNotifyPage, Set[WhyDidYouNotNotify](WhyDidYouNotNotify.NotDeliberatelyNoReasonableExcuse))
      } yield ua2).success.value
      navigator.nextPage(WhyDidYouNotNotifyPage, NormalMode, userAnswers) mustBe
        routes.WhyYouSubmittedAnInaccurateReturnController.onPageLoad(NormalMode)
    }

    "must go from WhyDidYouNotNotifyPage to ContractualDisclosureFacilityController when deliberate selected" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.AnIndividual)
        ua2 <- ua.set(WhyDidYouNotNotifyPage, Set[WhyDidYouNotNotify](WhyDidYouNotNotify.DeliberatelyDidNotNotify))
      } yield ua2).success.value
      navigator.nextPage(WhyDidYouNotNotifyPage, NormalMode, userAnswers) mustBe
        routes.ContractualDisclosureFacilityController.onPageLoad(NormalMode)
    }

    "must go from WhyDidYouNotNotifyPage to WhichYearsController when no deliberate and no further pages needed" in {
      val userAnswers = UserAnswers("id", "session-123")
        .set(WhyDidYouNotNotifyPage, Set[WhyDidYouNotNotify](WhyDidYouNotNotify.NotDeliberatelyNoReasonableExcuse))
        .success
        .value
      navigator.nextPage(WhyDidYouNotNotifyPage, NormalMode, userAnswers) mustBe
        routes.WhichYearsController.onPageLoad(NormalMode)
    }

    "must go from WhyDidYouNotFileAReturnOnTimeOffshorePage to WhatIsYourReasonableExcuseForNotFilingReturnController when ReasonableExcuse selected" in {
      val userAnswers = UserAnswers("id", "session-123")
        .set(
          WhyDidYouNotFileAReturnOnTimeOffshorePage,
          Set[WhyDidYouNotFileAReturnOnTimeOffshore](WhyDidYouNotFileAReturnOnTimeOffshore.ReasonableExcuse)
        )
        .success
        .value
      navigator.nextPage(WhyDidYouNotFileAReturnOnTimeOffshorePage, NormalMode, userAnswers) mustBe
        routes.WhatIsYourReasonableExcuseForNotFilingReturnController.onPageLoad(NormalMode)
    }

    "must go from WhyDidYouNotFileAReturnOnTimeOffshorePage to WhyYouSubmittedAnInaccurateReturnController when InaccurateReturn also selected on page 1" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(
                 WhyAreYouMakingThisDisclosurePage,
                 Set[WhyAreYouMakingThisDisclosure](WhyAreYouMakingThisDisclosure.InaccurateReturn)
               )
        ua2 <- ua.set(
                 WhyDidYouNotFileAReturnOnTimeOffshorePage,
                 Set[WhyDidYouNotFileAReturnOnTimeOffshore](
                   WhyDidYouNotFileAReturnOnTimeOffshore.DidNotWithholdInformationOnPurpose
                 )
               )
      } yield ua2).success.value
      navigator.nextPage(WhyDidYouNotFileAReturnOnTimeOffshorePage, NormalMode, userAnswers) mustBe
        routes.WhyYouSubmittedAnInaccurateReturnController.onPageLoad(NormalMode)
    }

    "must go from WhyDidYouNotFileAReturnOnTimeOffshorePage to ContractualDisclosureFacilityController when deliberate selected" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.AnIndividual)
        ua2 <- ua.set(
                 WhyDidYouNotFileAReturnOnTimeOffshorePage,
                 Set[WhyDidYouNotFileAReturnOnTimeOffshore](
                   WhyDidYouNotFileAReturnOnTimeOffshore.DeliberatelyWithheldInformation
                 )
               )
      } yield ua2).success.value
      navigator.nextPage(WhyDidYouNotFileAReturnOnTimeOffshorePage, NormalMode, userAnswers) mustBe
        routes.ContractualDisclosureFacilityController.onPageLoad(NormalMode)
    }

    "must go from WhyDidYouNotFileAReturnOnTimeOffshorePage to WhichYearsController when no deliberate and no further pages needed" in {
      val userAnswers = UserAnswers("id", "session-123")
        .set(
          WhyDidYouNotFileAReturnOnTimeOffshorePage,
          Set[WhyDidYouNotFileAReturnOnTimeOffshore](
            WhyDidYouNotFileAReturnOnTimeOffshore.DidNotWithholdInformationOnPurpose
          )
        )
        .success
        .value
      navigator.nextPage(WhyDidYouNotFileAReturnOnTimeOffshorePage, NormalMode, userAnswers) mustBe
        routes.WhichYearsController.onPageLoad(NormalMode)
    }
    "must go from WhyYouSubmittedAnInaccurateOffshoreReturnPage to WhatReasonableCareDidYouTakeController when ReasonableMistake selected" in {
      val userAnswers = UserAnswers("id", "session-123")
        .set(
          WhyYouSubmittedAnInaccurateOffshoreReturnPage,
          Set[WhyYouSubmittedAnInaccurateReturn](WhyYouSubmittedAnInaccurateReturn.ReasonableMistake)
        )
        .success
        .value
      navigator.nextPage(WhyYouSubmittedAnInaccurateOffshoreReturnPage, NormalMode, userAnswers) mustBe
        routes.WhatReasonableCareDidYouTakeController.onPageLoad(NormalMode)
    }

    "must go from WhyYouSubmittedAnInaccurateOffshoreReturnPage to ContractualDisclosureFacilityController when deliberate selected" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.AnIndividual)
        ua2 <- ua.set(
                 WhyYouSubmittedAnInaccurateOffshoreReturnPage,
                 Set[WhyYouSubmittedAnInaccurateReturn](WhyYouSubmittedAnInaccurateReturn.DeliberatelyInaccurate)
               )
      } yield ua2).success.value
      navigator.nextPage(WhyYouSubmittedAnInaccurateOffshoreReturnPage, NormalMode, userAnswers) mustBe
        routes.ContractualDisclosureFacilityController.onPageLoad(NormalMode)
    }

    "must go from WhyYouSubmittedAnInaccurateOffshoreReturnPage to WhichYearsController when no deliberate" in {
      val userAnswers = UserAnswers("id", "session-123")
        .set(
          WhyYouSubmittedAnInaccurateOffshoreReturnPage,
          Set[WhyYouSubmittedAnInaccurateReturn](WhyYouSubmittedAnInaccurateReturn.NoReasonableCare)
        )
        .success
        .value
      navigator.nextPage(WhyYouSubmittedAnInaccurateOffshoreReturnPage, NormalMode, userAnswers) mustBe
        routes.WhichYearsController.onPageLoad(NormalMode)
    }

    "must go from ContractualDisclosureFacilityPage to WhyDidYouNotNotifyController when true and DidNotNotifyHMRC selected but page not answered" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(ContractualDisclosureFacilityPage, true)
        ua2 <- ua.set(
                 WhyAreYouMakingThisDisclosurePage,
                 Set[WhyAreYouMakingThisDisclosure](WhyAreYouMakingThisDisclosure.DidNotNotifyHMRC)
               )
        ua3 <- ua2.set(WhyDidYouNotNotifyPage, Set[WhyDidYouNotNotify](WhyDidYouNotNotify.ReasonableExcuse))
      } yield ua3).success.value
      navigator.nextPage(ContractualDisclosureFacilityPage, NormalMode, userAnswers) mustBe
        routes.WhatIsYourReasonableExcuseController.onPageLoad(NormalMode)
    }

    "must go from ContractualDisclosureFacilityPage to WhyDidYouNotFileAReturnOnTimeOffshoreController when true and DidNotFile selected but page not answered" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(ContractualDisclosureFacilityPage, true)
        ua2 <- ua.set(
                 WhyAreYouMakingThisDisclosurePage,
                 Set[WhyAreYouMakingThisDisclosure](
                   WhyAreYouMakingThisDisclosure.DidNotNotifyHMRC,
                   WhyAreYouMakingThisDisclosure.DidNotFile
                 )
               )
        ua3 <-
          ua2.set(WhyDidYouNotNotifyPage, Set[WhyDidYouNotNotify](WhyDidYouNotNotify.NotDeliberatelyNoReasonableExcuse))
        ua4 <- ua3.set(
                 WhyDidYouNotFileAReturnOnTimeOffshorePage,
                 Set[WhyDidYouNotFileAReturnOnTimeOffshore](WhyDidYouNotFileAReturnOnTimeOffshore.ReasonableExcuse)
               )
      } yield ua4).success.value
      navigator.nextPage(ContractualDisclosureFacilityPage, NormalMode, userAnswers) mustBe
        routes.WhatIsYourReasonableExcuseForNotFilingReturnController.onPageLoad(NormalMode)
    }

    "must go from ContractualDisclosureFacilityPage to WhyYouSubmittedAnInaccurateReturnController when true and InaccurateReturn selected but page not answered" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(ContractualDisclosureFacilityPage, true)
        ua2 <- ua.set(
                 WhyAreYouMakingThisDisclosurePage,
                 Set[WhyAreYouMakingThisDisclosure](
                   WhyAreYouMakingThisDisclosure.DidNotNotifyHMRC,
                   WhyAreYouMakingThisDisclosure.DidNotFile,
                   WhyAreYouMakingThisDisclosure.InaccurateReturn
                 )
               )
        ua3 <-
          ua2.set(WhyDidYouNotNotifyPage, Set[WhyDidYouNotNotify](WhyDidYouNotNotify.NotDeliberatelyNoReasonableExcuse))
        ua4 <- ua3.set(
                 WhyDidYouNotFileAReturnOnTimeOffshorePage,
                 Set[WhyDidYouNotFileAReturnOnTimeOffshore](
                   WhyDidYouNotFileAReturnOnTimeOffshore.DidNotWithholdInformationOnPurpose
                 )
               )
        ua5 <- ua4.set(
                 WhyYouSubmittedAnInaccurateOffshoreReturnPage,
                 Set[WhyYouSubmittedAnInaccurateReturn](WhyYouSubmittedAnInaccurateReturn.ReasonableMistake)
               )
      } yield ua5).success.value
      navigator.nextPage(ContractualDisclosureFacilityPage, NormalMode, userAnswers) mustBe
        routes.WhatReasonableCareDidYouTakeController.onPageLoad(NormalMode)
    }

    "must go from WhatIsYourReasonableExcusePage to WhyDidYouNotFileAReturnOnTimeOffshoreController when DidNotFile selected but page not answered" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(
                 WhyAreYouMakingThisDisclosurePage,
                 Set[WhyAreYouMakingThisDisclosure](WhyAreYouMakingThisDisclosure.DidNotFile)
               )
        ua2 <- ua.set(
                 WhyDidYouNotFileAReturnOnTimeOffshorePage,
                 Set[WhyDidYouNotFileAReturnOnTimeOffshore](WhyDidYouNotFileAReturnOnTimeOffshore.ReasonableExcuse)
               )
      } yield ua2).success.value
      navigator.nextPage(WhatIsYourReasonableExcusePage, NormalMode, userAnswers) mustBe
        routes.WhatIsYourReasonableExcuseForNotFilingReturnController.onPageLoad(NormalMode)
    }

    "must go from WhatIsYourReasonableExcusePage to WhyYouSubmittedAnInaccurateReturnController when InaccurateReturn selected but page not answered" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(
                 WhyAreYouMakingThisDisclosurePage,
                 Set[WhyAreYouMakingThisDisclosure](WhyAreYouMakingThisDisclosure.InaccurateReturn)
               )
        ua2 <- ua.set(
                 WhyYouSubmittedAnInaccurateOffshoreReturnPage,
                 Set[WhyYouSubmittedAnInaccurateReturn](WhyYouSubmittedAnInaccurateReturn.ReasonableMistake)
               )
      } yield ua2).success.value
      navigator.nextPage(WhatIsYourReasonableExcusePage, NormalMode, userAnswers) mustBe
        routes.WhatReasonableCareDidYouTakeController.onPageLoad(NormalMode)
    }

    "must go from WhatReasonableCareDidYouTakePage to WhichYearsController when all done" in {
      val userAnswers = UserAnswers("id", "session-123")
      navigator.nextPage(WhatReasonableCareDidYouTakePage, NormalMode, userAnswers) mustBe
        routes.WhichYearsController.onPageLoad(NormalMode)
    }

    "must go from WhatIsYourReasonableExcuseForNotFilingReturnPage to WhyYouSubmittedAnInaccurateReturnController when InaccurateReturn selected but page not answered" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(
                 WhyAreYouMakingThisDisclosurePage,
                 Set[WhyAreYouMakingThisDisclosure](WhyAreYouMakingThisDisclosure.InaccurateReturn)
               )
        ua2 <- ua.set(
                 WhyYouSubmittedAnInaccurateOffshoreReturnPage,
                 Set[WhyYouSubmittedAnInaccurateReturn](WhyYouSubmittedAnInaccurateReturn.ReasonableMistake)
               )
      } yield ua2).success.value
      navigator.nextPage(WhatIsYourReasonableExcuseForNotFilingReturnPage, NormalMode, userAnswers) mustBe
        routes.WhatReasonableCareDidYouTakeController.onPageLoad(NormalMode)
    }

    "must go from YouHaveNotIncludedTheTaxYearPage to TaxBeforeFiveYearsController when ReasonableExcusePriorTo selected" in {
      val userAnswers =
        UserAnswers("id", "session-123").set(WhichYearsPage, Set[OffshoreYears](ReasonableExcusePriorTo)).success.value
      navigator.nextPage(YouHaveNotIncludedTheTaxYearPage, NormalMode, userAnswers) mustBe
        routes.TaxBeforeFiveYearsController.onPageLoad(NormalMode)
    }

    "must go from YouHaveNotIncludedTheTaxYearPage to TaxBeforeSevenYearsController when CarelessPriorTo selected" in {
      val userAnswers =
        UserAnswers("id", "session-123").set(WhichYearsPage, Set[OffshoreYears](CarelessPriorTo)).success.value
      navigator.nextPage(YouHaveNotIncludedTheTaxYearPage, NormalMode, userAnswers) mustBe
        routes.TaxBeforeSevenYearsController.onPageLoad(NormalMode)
    }

    "must go from YouHaveNotIncludedTheTaxYearPage to TaxBeforeNineteenYearsController when DeliberatePriorTo selected" in {
      val userAnswers =
        UserAnswers("id", "session-123").set(WhichYearsPage, Set[OffshoreYears](DeliberatePriorTo)).success.value
      navigator.nextPage(YouHaveNotIncludedTheTaxYearPage, NormalMode, userAnswers) mustBe
        routes.TaxBeforeNineteenYearsController.onPageLoad(NormalMode)
    }

    "must go from YouHaveNotIncludedTheTaxYearPage to CountriesOrTerritoriesController when country map non-empty" in {
      val countryCode  = "AFG"
      val countriesMap = Map(countryCode -> config.Country(countryCode, "Afghanistan"))
      val userAnswers  =
        UserAnswers("id", "session-123").set(CountryOfYourOffshoreLiabilityPage, countriesMap).success.value
      navigator.nextPage(YouHaveNotIncludedTheTaxYearPage, NormalMode, userAnswers) mustBe
        routes.CountriesOrTerritoriesController.onPageLoad(NormalMode)
    }

    "must go from YouHaveNotSelectedCertainTaxYearPage to TaxBeforeFiveYearsController when ReasonableExcusePriorTo selected" in {
      val userAnswers =
        UserAnswers("id", "session-123").set(WhichYearsPage, Set[OffshoreYears](ReasonableExcusePriorTo)).success.value
      navigator.nextPage(YouHaveNotSelectedCertainTaxYearPage, NormalMode, userAnswers) mustBe
        routes.TaxBeforeFiveYearsController.onPageLoad(NormalMode)
    }

    "must go from YouHaveNotSelectedCertainTaxYearPage to TaxBeforeSevenYearsController when CarelessPriorTo selected" in {
      val userAnswers =
        UserAnswers("id", "session-123").set(WhichYearsPage, Set[OffshoreYears](CarelessPriorTo)).success.value
      navigator.nextPage(YouHaveNotSelectedCertainTaxYearPage, NormalMode, userAnswers) mustBe
        routes.TaxBeforeSevenYearsController.onPageLoad(NormalMode)
    }

    "must go from YouHaveNotSelectedCertainTaxYearPage to TaxBeforeNineteenYearsController when DeliberatePriorTo selected" in {
      val userAnswers =
        UserAnswers("id", "session-123").set(WhichYearsPage, Set[OffshoreYears](DeliberatePriorTo)).success.value
      navigator.nextPage(YouHaveNotSelectedCertainTaxYearPage, NormalMode, userAnswers) mustBe
        routes.TaxBeforeNineteenYearsController.onPageLoad(NormalMode)
    }

    "must go from YouHaveNotSelectedCertainTaxYearPage to CountriesOrTerritoriesController when country map non-empty" in {
      val countryCode  = "AFG"
      val countriesMap = Map(countryCode -> config.Country(countryCode, "Afghanistan"))
      val userAnswers  =
        UserAnswers("id", "session-123").set(CountryOfYourOffshoreLiabilityPage, countriesMap).success.value
      navigator.nextPage(YouHaveNotSelectedCertainTaxYearPage, NormalMode, userAnswers) mustBe
        routes.CountriesOrTerritoriesController.onPageLoad(NormalMode)
    }

    "must not treat deliberate selections as deliberate when entity is AnEstate" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.AnEstate)
        ua2 <- ua.set(WhyDidYouNotNotifyPage, Set[WhyDidYouNotNotify](WhyDidYouNotNotify.DeliberatelyDidNotNotify))
      } yield ua2).success.value
      navigator.nextPage(WhyDidYouNotNotifyPage, NormalMode, userAnswers) mustBe
        routes.WhichYearsController.onPageLoad(NormalMode)
    }

    "must go from WhyAreYouMakingThisDisclosurePage in CheckMode to normal route when answer changed" in {
      val set: Set[WhyAreYouMakingThisDisclosure] = Set(WhyAreYouMakingThisDisclosure.DidNotNotifyHMRC)
      val userAnswers                             = UserAnswers("id", "session-123").set(WhyAreYouMakingThisDisclosurePage, set).success.value
      navigator.nextPage(WhyAreYouMakingThisDisclosurePage, CheckMode, userAnswers, hasAnswerChanged = true) mustBe
        routes.WhyDidYouNotNotifyController.onPageLoad(NormalMode)
    }

    "must go from WhyAreYouMakingThisDisclosurePage in CheckMode to CYA when answer not changed" in {
      val set: Set[WhyAreYouMakingThisDisclosure] = Set(WhyAreYouMakingThisDisclosure.DidNotNotifyHMRC)
      val userAnswers                             = UserAnswers("id", "session-123").set(WhyAreYouMakingThisDisclosurePage, set).success.value
      navigator.nextPage(WhyAreYouMakingThisDisclosurePage, CheckMode, userAnswers, hasAnswerChanged = false) mustBe
        routes.CheckYourAnswersController.onPageLoad
    }

    "must go from TaxBeforeFiveYearsPage to CountriesOrTerritoriesController when country map non-empty and multiple years" in {
      val year                    = current.back(1).startYear
      val set: Set[OffshoreYears] = Set(ReasonableExcusePriorTo, TaxYearStarting(year))
      val countryCode             = "AFG"
      val countriesMap            = Map(countryCode -> config.Country(countryCode, "Afghanistan"))
      val userAnswers             = (for {
        ua  <- UserAnswers("id", "session-123").set(WhichYearsPage, set)
        ua2 <- ua.set(CountryOfYourOffshoreLiabilityPage, countriesMap)
      } yield ua2).success.value
      navigator.nextPage(TaxBeforeFiveYearsPage, NormalMode, userAnswers) mustBe
        routes.CountriesOrTerritoriesController.onPageLoad(NormalMode)
    }

    "must go from TaxBeforeSevenYearsPage to CountriesOrTerritoriesController when country map non-empty and multiple years" in {
      val year                    = current.back(1).startYear
      val set: Set[OffshoreYears] = Set(CarelessPriorTo, TaxYearStarting(year))
      val countryCode             = "AFG"
      val countriesMap            = Map(countryCode -> config.Country(countryCode, "Afghanistan"))
      val userAnswers             = (for {
        ua  <- UserAnswers("id", "session-123").set(WhichYearsPage, set)
        ua2 <- ua.set(CountryOfYourOffshoreLiabilityPage, countriesMap)
      } yield ua2).success.value
      navigator.nextPage(TaxBeforeSevenYearsPage, NormalMode, userAnswers) mustBe
        routes.CountriesOrTerritoriesController.onPageLoad(NormalMode)
    }

    "must go from TaxBeforeNineteenYearsPage to CountriesOrTerritoriesController when country map non-empty and multiple years" in {
      val year                    = current.back(1).startYear
      val set: Set[OffshoreYears] = Set(DeliberatePriorTo, TaxYearStarting(year))
      val countryCode             = "AFG"
      val countriesMap            = Map(countryCode -> config.Country(countryCode, "Afghanistan"))
      val userAnswers             = (for {
        ua  <- UserAnswers("id", "session-123").set(WhichYearsPage, set)
        ua2 <- ua.set(CountryOfYourOffshoreLiabilityPage, countriesMap)
      } yield ua2).success.value
      navigator.nextPage(TaxBeforeNineteenYearsPage, NormalMode, userAnswers) mustBe
        routes.CountriesOrTerritoriesController.onPageLoad(NormalMode)
    }
  }
  "must go from WhyDidYouNotFileAReturnOnTimeOffshorePage to WhatIsYourReasonableExcuseController when DidNotNotifyHMRC selected and page2a has ReasonableExcuse" in {
    val userAnswers = (for {
      ua  <- UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.AnIndividual)
      ua2 <- ua.set(
               WhyAreYouMakingThisDisclosurePage,
               Set[WhyAreYouMakingThisDisclosure](WhyAreYouMakingThisDisclosure.DidNotNotifyHMRC)
             )
      ua3 <- ua2.set(WhyDidYouNotNotifyPage, Set[WhyDidYouNotNotify](WhyDidYouNotNotify.ReasonableExcuse))
      ua4 <- ua3.set(
               WhyDidYouNotFileAReturnOnTimeOffshorePage,
               Set[WhyDidYouNotFileAReturnOnTimeOffshore](
                 WhyDidYouNotFileAReturnOnTimeOffshore.DidNotWithholdInformationOnPurpose
               )
             )
    } yield ua4).success.value
    navigator.nextPage(WhyDidYouNotFileAReturnOnTimeOffshorePage, NormalMode, userAnswers) mustBe
      routes.WhatIsYourReasonableExcuseController.onPageLoad(NormalMode)
  }

  "must go from WhyDidYouNotFileAReturnOnTimeOffshorePage to WhatIsYourReasonableExcuseForNotFilingReturnController when DidNotNotifyHMRC selected and page2b has ReasonableExcuse" in {
    val userAnswers = (for {
      ua  <- UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.AnIndividual)
      ua2 <- ua.set(
               WhyAreYouMakingThisDisclosurePage,
               Set[WhyAreYouMakingThisDisclosure](WhyAreYouMakingThisDisclosure.DidNotNotifyHMRC)
             )
      ua3 <-
        ua2.set(WhyDidYouNotNotifyPage, Set[WhyDidYouNotNotify](WhyDidYouNotNotify.NotDeliberatelyNoReasonableExcuse))
      ua4 <- ua3.set(
               WhyDidYouNotFileAReturnOnTimeOffshorePage,
               Set[WhyDidYouNotFileAReturnOnTimeOffshore](WhyDidYouNotFileAReturnOnTimeOffshore.ReasonableExcuse)
             )
    } yield ua4).success.value
    navigator.nextPage(WhyDidYouNotFileAReturnOnTimeOffshorePage, NormalMode, userAnswers) mustBe
      routes.WhatIsYourReasonableExcuseForNotFilingReturnController.onPageLoad(NormalMode)
  }

  "must go from WhyDidYouNotFileAReturnOnTimeOffshorePage to WhichYearsController when DidNotNotifyHMRC selected and no excuses" in {
    val userAnswers = (for {
      ua  <- UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.AnIndividual)
      ua2 <- ua.set(
               WhyAreYouMakingThisDisclosurePage,
               Set[WhyAreYouMakingThisDisclosure](WhyAreYouMakingThisDisclosure.DidNotNotifyHMRC)
             )
      ua3 <-
        ua2.set(WhyDidYouNotNotifyPage, Set[WhyDidYouNotNotify](WhyDidYouNotNotify.NotDeliberatelyNoReasonableExcuse))
      ua4 <- ua3.set(
               WhyDidYouNotFileAReturnOnTimeOffshorePage,
               Set[WhyDidYouNotFileAReturnOnTimeOffshore](
                 WhyDidYouNotFileAReturnOnTimeOffshore.DidNotWithholdInformationOnPurpose
               )
             )
    } yield ua4).success.value
    navigator.nextPage(WhyDidYouNotFileAReturnOnTimeOffshorePage, NormalMode, userAnswers) mustBe
      routes.WhichYearsController.onPageLoad(NormalMode)
  }

  "must go from WhyYouSubmittedAnInaccurateOffshoreReturnPage to WhatIsYourReasonableExcuseController when DidNotNotifyHMRC selected and page2a has ReasonableExcuse" in {
    val userAnswers = (for {
      ua  <- UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.AnIndividual)
      ua2 <- ua.set(
               WhyAreYouMakingThisDisclosurePage,
               Set[WhyAreYouMakingThisDisclosure](WhyAreYouMakingThisDisclosure.DidNotNotifyHMRC)
             )
      ua3 <- ua2.set(WhyDidYouNotNotifyPage, Set[WhyDidYouNotNotify](WhyDidYouNotNotify.ReasonableExcuse))
      ua4 <- ua3.set(
               WhyYouSubmittedAnInaccurateOffshoreReturnPage,
               Set[WhyYouSubmittedAnInaccurateReturn](WhyYouSubmittedAnInaccurateReturn.NoReasonableCare)
             )
    } yield ua4).success.value
    navigator.nextPage(WhyYouSubmittedAnInaccurateOffshoreReturnPage, NormalMode, userAnswers) mustBe
      routes.WhatIsYourReasonableExcuseController.onPageLoad(NormalMode)
  }

  "must go from WhyYouSubmittedAnInaccurateOffshoreReturnPage to WhatIsYourReasonableExcuseForNotFilingReturnController when DidNotNotifyHMRC+DidNotFile selected and page2b has ReasonableExcuse" in {
    val userAnswers = (for {
      ua  <- UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.AnIndividual)
      ua2 <- ua.set(
               WhyAreYouMakingThisDisclosurePage,
               Set[WhyAreYouMakingThisDisclosure](
                 WhyAreYouMakingThisDisclosure.DidNotNotifyHMRC,
                 WhyAreYouMakingThisDisclosure.DidNotFile
               )
             )
      ua3 <-
        ua2.set(WhyDidYouNotNotifyPage, Set[WhyDidYouNotNotify](WhyDidYouNotNotify.NotDeliberatelyNoReasonableExcuse))
      ua4 <- ua3.set(
               WhyDidYouNotFileAReturnOnTimeOffshorePage,
               Set[WhyDidYouNotFileAReturnOnTimeOffshore](WhyDidYouNotFileAReturnOnTimeOffshore.ReasonableExcuse)
             )
      ua5 <- ua4.set(
               WhyYouSubmittedAnInaccurateOffshoreReturnPage,
               Set[WhyYouSubmittedAnInaccurateReturn](WhyYouSubmittedAnInaccurateReturn.NoReasonableCare)
             )
    } yield ua5).success.value
    navigator.nextPage(WhyYouSubmittedAnInaccurateOffshoreReturnPage, NormalMode, userAnswers) mustBe
      routes.WhatIsYourReasonableExcuseForNotFilingReturnController.onPageLoad(NormalMode)
  }

  "must go from WhyYouSubmittedAnInaccurateOffshoreReturnPage to WhatReasonableCareDidYouTakeController when DidNotNotifyHMRC+DidNotFile selected and page2c has ReasonableMistake" in {
    val userAnswers = (for {
      ua  <- UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.AnIndividual)
      ua2 <- ua.set(
               WhyAreYouMakingThisDisclosurePage,
               Set[WhyAreYouMakingThisDisclosure](
                 WhyAreYouMakingThisDisclosure.DidNotNotifyHMRC,
                 WhyAreYouMakingThisDisclosure.DidNotFile
               )
             )
      ua3 <-
        ua2.set(WhyDidYouNotNotifyPage, Set[WhyDidYouNotNotify](WhyDidYouNotNotify.NotDeliberatelyNoReasonableExcuse))
      ua4 <- ua3.set(
               WhyDidYouNotFileAReturnOnTimeOffshorePage,
               Set[WhyDidYouNotFileAReturnOnTimeOffshore](
                 WhyDidYouNotFileAReturnOnTimeOffshore.DidNotWithholdInformationOnPurpose
               )
             )
      ua5 <- ua4.set(
               WhyYouSubmittedAnInaccurateOffshoreReturnPage,
               Set[WhyYouSubmittedAnInaccurateReturn](WhyYouSubmittedAnInaccurateReturn.ReasonableMistake)
             )
    } yield ua5).success.value
    navigator.nextPage(WhyYouSubmittedAnInaccurateOffshoreReturnPage, NormalMode, userAnswers) mustBe
      routes.WhatReasonableCareDidYouTakeController.onPageLoad(NormalMode)
  }

  "must go from WhyYouSubmittedAnInaccurateOffshoreReturnPage to WhichYearsController when DidNotNotifyHMRC+DidNotFile selected and no excuses" in {
    val userAnswers = (for {
      ua  <- UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.AnIndividual)
      ua2 <- ua.set(
               WhyAreYouMakingThisDisclosurePage,
               Set[WhyAreYouMakingThisDisclosure](
                 WhyAreYouMakingThisDisclosure.DidNotNotifyHMRC,
                 WhyAreYouMakingThisDisclosure.DidNotFile
               )
             )
      ua3 <-
        ua2.set(WhyDidYouNotNotifyPage, Set[WhyDidYouNotNotify](WhyDidYouNotNotify.NotDeliberatelyNoReasonableExcuse))
      ua4 <- ua3.set(
               WhyDidYouNotFileAReturnOnTimeOffshorePage,
               Set[WhyDidYouNotFileAReturnOnTimeOffshore](
                 WhyDidYouNotFileAReturnOnTimeOffshore.DidNotWithholdInformationOnPurpose
               )
             )
      ua5 <- ua4.set(
               WhyYouSubmittedAnInaccurateOffshoreReturnPage,
               Set[WhyYouSubmittedAnInaccurateReturn](WhyYouSubmittedAnInaccurateReturn.NoReasonableCare)
             )
    } yield ua5).success.value
    navigator.nextPage(WhyYouSubmittedAnInaccurateOffshoreReturnPage, NormalMode, userAnswers) mustBe
      routes.WhichYearsController.onPageLoad(NormalMode)
  }

  "must go from WhyYouSubmittedAnInaccurateOffshoreReturnPage to WhatIsYourReasonableExcuseForNotFilingReturnController when DidNotFile only and page2b has ReasonableExcuse" in {
    val userAnswers = (for {
      ua  <- UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.AnIndividual)
      ua2 <- ua.set(
               WhyAreYouMakingThisDisclosurePage,
               Set[WhyAreYouMakingThisDisclosure](WhyAreYouMakingThisDisclosure.DidNotFile)
             )
      ua3 <- ua2.set(
               WhyDidYouNotFileAReturnOnTimeOffshorePage,
               Set[WhyDidYouNotFileAReturnOnTimeOffshore](WhyDidYouNotFileAReturnOnTimeOffshore.ReasonableExcuse)
             )
      ua4 <- ua3.set(
               WhyYouSubmittedAnInaccurateOffshoreReturnPage,
               Set[WhyYouSubmittedAnInaccurateReturn](WhyYouSubmittedAnInaccurateReturn.NoReasonableCare)
             )
    } yield ua4).success.value
    navigator.nextPage(WhyYouSubmittedAnInaccurateOffshoreReturnPage, NormalMode, userAnswers) mustBe
      routes.WhatIsYourReasonableExcuseForNotFilingReturnController.onPageLoad(NormalMode)
  }

  "must go from WhyYouSubmittedAnInaccurateOffshoreReturnPage to WhatReasonableCareDidYouTakeController when DidNotFile only and page2c has ReasonableMistake" in {
    val userAnswers = (for {
      ua  <- UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.AnIndividual)
      ua2 <- ua.set(
               WhyAreYouMakingThisDisclosurePage,
               Set[WhyAreYouMakingThisDisclosure](WhyAreYouMakingThisDisclosure.DidNotFile)
             )
      ua3 <- ua2.set(
               WhyDidYouNotFileAReturnOnTimeOffshorePage,
               Set[WhyDidYouNotFileAReturnOnTimeOffshore](
                 WhyDidYouNotFileAReturnOnTimeOffshore.DidNotWithholdInformationOnPurpose
               )
             )
      ua4 <- ua3.set(
               WhyYouSubmittedAnInaccurateOffshoreReturnPage,
               Set[WhyYouSubmittedAnInaccurateReturn](WhyYouSubmittedAnInaccurateReturn.ReasonableMistake)
             )
    } yield ua4).success.value
    navigator.nextPage(WhyYouSubmittedAnInaccurateOffshoreReturnPage, NormalMode, userAnswers) mustBe
      routes.WhatReasonableCareDidYouTakeController.onPageLoad(NormalMode)
  }

  "must go from WhyYouSubmittedAnInaccurateOffshoreReturnPage to WhichYearsController when DidNotFile only and no excuses" in {
    val userAnswers = (for {
      ua  <- UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.AnIndividual)
      ua2 <- ua.set(
               WhyAreYouMakingThisDisclosurePage,
               Set[WhyAreYouMakingThisDisclosure](WhyAreYouMakingThisDisclosure.DidNotFile)
             )
      ua3 <- ua2.set(
               WhyDidYouNotFileAReturnOnTimeOffshorePage,
               Set[WhyDidYouNotFileAReturnOnTimeOffshore](
                 WhyDidYouNotFileAReturnOnTimeOffshore.DidNotWithholdInformationOnPurpose
               )
             )
      ua4 <- ua3.set(
               WhyYouSubmittedAnInaccurateOffshoreReturnPage,
               Set[WhyYouSubmittedAnInaccurateReturn](WhyYouSubmittedAnInaccurateReturn.NoReasonableCare)
             )
    } yield ua4).success.value
    navigator.nextPage(WhyYouSubmittedAnInaccurateOffshoreReturnPage, NormalMode, userAnswers) mustBe
      routes.WhichYearsController.onPageLoad(NormalMode)
  }

  "must go from WhyYouSubmittedAnInaccurateOffshoreReturnPage to WhichYearsController when DidNotNotifyHMRC only and no excuses" in {
    val userAnswers = (for {
      ua  <- UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.AnIndividual)
      ua2 <- ua.set(
               WhyAreYouMakingThisDisclosurePage,
               Set[WhyAreYouMakingThisDisclosure](WhyAreYouMakingThisDisclosure.DidNotNotifyHMRC)
             )
      ua3 <-
        ua2.set(WhyDidYouNotNotifyPage, Set[WhyDidYouNotNotify](WhyDidYouNotNotify.NotDeliberatelyNoReasonableExcuse))
      ua4 <- ua3.set(
               WhyYouSubmittedAnInaccurateOffshoreReturnPage,
               Set[WhyYouSubmittedAnInaccurateReturn](WhyYouSubmittedAnInaccurateReturn.NoReasonableCare)
             )
    } yield ua4).success.value
    navigator.nextPage(WhyYouSubmittedAnInaccurateOffshoreReturnPage, NormalMode, userAnswers) mustBe
      routes.WhichYearsController.onPageLoad(NormalMode)
  }

  "must go from WhyYouSubmittedAnInaccurateOffshoreReturnPage to WhatReasonableCareDidYouTakeController when DidNotNotifyHMRC only and page2c has ReasonableMistake" in {
    val userAnswers = (for {
      ua  <- UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.AnIndividual)
      ua2 <- ua.set(
               WhyAreYouMakingThisDisclosurePage,
               Set[WhyAreYouMakingThisDisclosure](WhyAreYouMakingThisDisclosure.DidNotNotifyHMRC)
             )
      ua3 <-
        ua2.set(WhyDidYouNotNotifyPage, Set[WhyDidYouNotNotify](WhyDidYouNotNotify.NotDeliberatelyNoReasonableExcuse))
      ua4 <- ua3.set(
               WhyYouSubmittedAnInaccurateOffshoreReturnPage,
               Set[WhyYouSubmittedAnInaccurateReturn](WhyYouSubmittedAnInaccurateReturn.ReasonableMistake)
             )
    } yield ua4).success.value
    navigator.nextPage(WhyYouSubmittedAnInaccurateOffshoreReturnPage, NormalMode, userAnswers) mustBe
      routes.WhatReasonableCareDidYouTakeController.onPageLoad(NormalMode)
  }

  "must go from ContractualDisclosureFacilityPage to WhichYearsController when true and DidNotFile only with no excuses" in {
    val userAnswers = (for {
      ua  <- UserAnswers("id", "session-123").set(ContractualDisclosureFacilityPage, true)
      ua2 <- ua.set(
               WhyAreYouMakingThisDisclosurePage,
               Set[WhyAreYouMakingThisDisclosure](WhyAreYouMakingThisDisclosure.DidNotFile)
             )
      ua3 <- ua2.set(
               WhyDidYouNotFileAReturnOnTimeOffshorePage,
               Set[WhyDidYouNotFileAReturnOnTimeOffshore](
                 WhyDidYouNotFileAReturnOnTimeOffshore.DidNotWithholdInformationOnPurpose
               )
             )
    } yield ua3).success.value
    navigator.nextPage(ContractualDisclosureFacilityPage, NormalMode, userAnswers) mustBe
      routes.WhichYearsController.onPageLoad(NormalMode)
  }

  "must go from ContractualDisclosureFacilityPage to WhatReasonableCareDidYouTakeController when true and DidNotFile+InaccurateReturn selected with ReasonableMistake" in {
    val userAnswers = (for {
      ua  <- UserAnswers("id", "session-123").set(ContractualDisclosureFacilityPage, true)
      ua2 <- ua.set(
               WhyAreYouMakingThisDisclosurePage,
               Set[WhyAreYouMakingThisDisclosure](
                 WhyAreYouMakingThisDisclosure.DidNotFile,
                 WhyAreYouMakingThisDisclosure.InaccurateReturn
               )
             )
      ua3 <- ua2.set(
               WhyDidYouNotFileAReturnOnTimeOffshorePage,
               Set[WhyDidYouNotFileAReturnOnTimeOffshore](
                 WhyDidYouNotFileAReturnOnTimeOffshore.DidNotWithholdInformationOnPurpose
               )
             )
      ua4 <- ua3.set(
               WhyYouSubmittedAnInaccurateOffshoreReturnPage,
               Set[WhyYouSubmittedAnInaccurateReturn](WhyYouSubmittedAnInaccurateReturn.ReasonableMistake)
             )
    } yield ua4).success.value
    navigator.nextPage(ContractualDisclosureFacilityPage, NormalMode, userAnswers) mustBe
      routes.WhatReasonableCareDidYouTakeController.onPageLoad(NormalMode)
  }

  "must go from ContractualDisclosureFacilityPage to WhichYearsController when true and DidNotFile+InaccurateReturn selected with no excuses" in {
    val userAnswers = (for {
      ua  <- UserAnswers("id", "session-123").set(ContractualDisclosureFacilityPage, true)
      ua2 <- ua.set(
               WhyAreYouMakingThisDisclosurePage,
               Set[WhyAreYouMakingThisDisclosure](
                 WhyAreYouMakingThisDisclosure.DidNotFile,
                 WhyAreYouMakingThisDisclosure.InaccurateReturn
               )
             )
      ua3 <- ua2.set(
               WhyDidYouNotFileAReturnOnTimeOffshorePage,
               Set[WhyDidYouNotFileAReturnOnTimeOffshore](
                 WhyDidYouNotFileAReturnOnTimeOffshore.DidNotWithholdInformationOnPurpose
               )
             )
      ua4 <- ua3.set(
               WhyYouSubmittedAnInaccurateOffshoreReturnPage,
               Set[WhyYouSubmittedAnInaccurateReturn](WhyYouSubmittedAnInaccurateReturn.NoReasonableCare)
             )
    } yield ua4).success.value
    navigator.nextPage(ContractualDisclosureFacilityPage, NormalMode, userAnswers) mustBe
      routes.WhichYearsController.onPageLoad(NormalMode)
  }

  "must go from ContractualDisclosureFacilityPage to WhatReasonableCareDidYouTakeController when true and InaccurateReturn only with ReasonableMistake" in {
    val userAnswers = (for {
      ua  <- UserAnswers("id", "session-123").set(ContractualDisclosureFacilityPage, true)
      ua2 <- ua.set(
               WhyAreYouMakingThisDisclosurePage,
               Set[WhyAreYouMakingThisDisclosure](WhyAreYouMakingThisDisclosure.InaccurateReturn)
             )
      ua3 <- ua2.set(
               WhyYouSubmittedAnInaccurateOffshoreReturnPage,
               Set[WhyYouSubmittedAnInaccurateReturn](WhyYouSubmittedAnInaccurateReturn.ReasonableMistake)
             )
    } yield ua3).success.value
    navigator.nextPage(ContractualDisclosureFacilityPage, NormalMode, userAnswers) mustBe
      routes.WhatReasonableCareDidYouTakeController.onPageLoad(NormalMode)
  }

  "must go from ContractualDisclosureFacilityPage to WhichYearsController when true and InaccurateReturn only with no excuses" in {
    val userAnswers = (for {
      ua  <- UserAnswers("id", "session-123").set(ContractualDisclosureFacilityPage, true)
      ua2 <- ua.set(
               WhyAreYouMakingThisDisclosurePage,
               Set[WhyAreYouMakingThisDisclosure](WhyAreYouMakingThisDisclosure.InaccurateReturn)
             )
      ua3 <- ua2.set(
               WhyYouSubmittedAnInaccurateOffshoreReturnPage,
               Set[WhyYouSubmittedAnInaccurateReturn](WhyYouSubmittedAnInaccurateReturn.NoReasonableCare)
             )
    } yield ua3).success.value
    navigator.nextPage(ContractualDisclosureFacilityPage, NormalMode, userAnswers) mustBe
      routes.WhichYearsController.onPageLoad(NormalMode)
  }

  "must go from ContractualDisclosureFacilityPage to WhichYearsController when true and no page1 selections" in {
    val userAnswers = UserAnswers("id", "session-123").set(ContractualDisclosureFacilityPage, true).success.value
    navigator.nextPage(ContractualDisclosureFacilityPage, NormalMode, userAnswers) mustBe
      routes.WhichYearsController.onPageLoad(NormalMode)
  }

  "must go from ContractualDisclosureFacilityPage to WhichYearsController when true and DidNotNotifyHMRC only with no excuses" in {
    val userAnswers = (for {
      ua  <- UserAnswers("id", "session-123").set(ContractualDisclosureFacilityPage, true)
      ua2 <- ua.set(
               WhyAreYouMakingThisDisclosurePage,
               Set[WhyAreYouMakingThisDisclosure](WhyAreYouMakingThisDisclosure.DidNotNotifyHMRC)
             )
      ua3 <-
        ua2.set(WhyDidYouNotNotifyPage, Set[WhyDidYouNotNotify](WhyDidYouNotNotify.NotDeliberatelyNoReasonableExcuse))
    } yield ua3).success.value
    navigator.nextPage(ContractualDisclosureFacilityPage, NormalMode, userAnswers) mustBe
      routes.WhichYearsController.onPageLoad(NormalMode)
  }

  "must go from ContractualDisclosureFacilityPage to WhatReasonableCareDidYouTakeController when DidNotNotifyHMRC+InaccurateReturn and ReasonableMistake" in {
    val userAnswers = (for {
      ua  <- UserAnswers("id", "session-123").set(ContractualDisclosureFacilityPage, true)
      ua2 <- ua.set(
               WhyAreYouMakingThisDisclosurePage,
               Set[WhyAreYouMakingThisDisclosure](
                 WhyAreYouMakingThisDisclosure.DidNotNotifyHMRC,
                 WhyAreYouMakingThisDisclosure.InaccurateReturn
               )
             )
      ua3 <-
        ua2.set(WhyDidYouNotNotifyPage, Set[WhyDidYouNotNotify](WhyDidYouNotNotify.NotDeliberatelyNoReasonableExcuse))
      ua4 <- ua3.set(
               WhyYouSubmittedAnInaccurateOffshoreReturnPage,
               Set[WhyYouSubmittedAnInaccurateReturn](WhyYouSubmittedAnInaccurateReturn.ReasonableMistake)
             )
    } yield ua4).success.value
    navigator.nextPage(ContractualDisclosureFacilityPage, NormalMode, userAnswers) mustBe
      routes.WhatReasonableCareDidYouTakeController.onPageLoad(NormalMode)
  }

  "must go from ContractualDisclosureFacilityPage to WhichYearsController when DidNotNotifyHMRC+InaccurateReturn and no excuses" in {
    val userAnswers = (for {
      ua  <- UserAnswers("id", "session-123").set(ContractualDisclosureFacilityPage, true)
      ua2 <- ua.set(
               WhyAreYouMakingThisDisclosurePage,
               Set[WhyAreYouMakingThisDisclosure](
                 WhyAreYouMakingThisDisclosure.DidNotNotifyHMRC,
                 WhyAreYouMakingThisDisclosure.InaccurateReturn
               )
             )
      ua3 <-
        ua2.set(WhyDidYouNotNotifyPage, Set[WhyDidYouNotNotify](WhyDidYouNotNotify.NotDeliberatelyNoReasonableExcuse))
      ua4 <- ua3.set(
               WhyYouSubmittedAnInaccurateOffshoreReturnPage,
               Set[WhyYouSubmittedAnInaccurateReturn](WhyYouSubmittedAnInaccurateReturn.NoReasonableCare)
             )
    } yield ua4).success.value
    navigator.nextPage(ContractualDisclosureFacilityPage, NormalMode, userAnswers) mustBe
      routes.WhichYearsController.onPageLoad(NormalMode)
  }

  "must go from WhatIsYourReasonableExcusePage to WhichYearsController when DidNotFile selected but no ReasonableExcuse on page2b" in {
    val userAnswers = (for {
      ua  <- UserAnswers("id", "session-123").set(
               WhyAreYouMakingThisDisclosurePage,
               Set[WhyAreYouMakingThisDisclosure](WhyAreYouMakingThisDisclosure.DidNotFile)
             )
      ua2 <- ua.set(
               WhyDidYouNotFileAReturnOnTimeOffshorePage,
               Set[WhyDidYouNotFileAReturnOnTimeOffshore](
                 WhyDidYouNotFileAReturnOnTimeOffshore.DidNotWithholdInformationOnPurpose
               )
             )
    } yield ua2).success.value
    navigator.nextPage(WhatIsYourReasonableExcusePage, NormalMode, userAnswers) mustBe
      routes.WhichYearsController.onPageLoad(NormalMode)
  }

  "must go from WhatIsYourReasonableExcusePage to WhichYearsController when DidNotFile+InaccurateReturn selected but no excuses" in {
    val userAnswers = (for {
      ua  <- UserAnswers("id", "session-123").set(
               WhyAreYouMakingThisDisclosurePage,
               Set[WhyAreYouMakingThisDisclosure](
                 WhyAreYouMakingThisDisclosure.DidNotFile,
                 WhyAreYouMakingThisDisclosure.InaccurateReturn
               )
             )
      ua2 <- ua.set(
               WhyDidYouNotFileAReturnOnTimeOffshorePage,
               Set[WhyDidYouNotFileAReturnOnTimeOffshore](
                 WhyDidYouNotFileAReturnOnTimeOffshore.DidNotWithholdInformationOnPurpose
               )
             )
      ua3 <- ua2.set(
               WhyYouSubmittedAnInaccurateOffshoreReturnPage,
               Set[WhyYouSubmittedAnInaccurateReturn](WhyYouSubmittedAnInaccurateReturn.NoReasonableCare)
             )
    } yield ua3).success.value
    navigator.nextPage(WhatIsYourReasonableExcusePage, NormalMode, userAnswers) mustBe
      routes.WhichYearsController.onPageLoad(NormalMode)
  }

  "must go from WhatIsYourReasonableExcusePage to WhichYearsController when InaccurateReturn selected but no ReasonableMistake" in {
    val userAnswers = (for {
      ua  <- UserAnswers("id", "session-123").set(
               WhyAreYouMakingThisDisclosurePage,
               Set[WhyAreYouMakingThisDisclosure](WhyAreYouMakingThisDisclosure.InaccurateReturn)
             )
      ua2 <- ua.set(
               WhyYouSubmittedAnInaccurateOffshoreReturnPage,
               Set[WhyYouSubmittedAnInaccurateReturn](WhyYouSubmittedAnInaccurateReturn.NoReasonableCare)
             )
    } yield ua2).success.value
    navigator.nextPage(WhatIsYourReasonableExcusePage, NormalMode, userAnswers) mustBe
      routes.WhichYearsController.onPageLoad(NormalMode)
  }

  "must go from WhatIsYourReasonableExcuseForNotFilingReturnPage to WhichYearsController when InaccurateReturn selected but no ReasonableMistake" in {
    val userAnswers = (for {
      ua  <- UserAnswers("id", "session-123").set(
               WhyAreYouMakingThisDisclosurePage,
               Set[WhyAreYouMakingThisDisclosure](WhyAreYouMakingThisDisclosure.InaccurateReturn)
             )
      ua2 <- ua.set(
               WhyYouSubmittedAnInaccurateOffshoreReturnPage,
               Set[WhyYouSubmittedAnInaccurateReturn](WhyYouSubmittedAnInaccurateReturn.NoReasonableCare)
             )
    } yield ua2).success.value
    navigator.nextPage(WhatIsYourReasonableExcuseForNotFilingReturnPage, NormalMode, userAnswers) mustBe
      routes.WhichYearsController.onPageLoad(NormalMode)
  }

  "must go from WhichYearsPage to CountryOfYourOffshoreLiabilityController when no countries set" in {
    val year                    = current.back(1).startYear
    val set: Set[OffshoreYears] = Set(TaxYearStarting(year))
    val userAnswers             = UserAnswers("id", "session-123").set(WhichYearsPage, set).success.value
    navigator.nextPage(WhichYearsPage, NormalMode, userAnswers) mustBe
      routes.CountryOfYourOffshoreLiabilityController.onPageLoad(None, NormalMode)
  }

  "must go from WhichYearsPage in CheckMode to normal route when answer changed" in {
    val year                    = current.back(1).startYear
    val set: Set[OffshoreYears] = Set(TaxYearStarting(year))
    val userAnswers             = UserAnswers("id", "session-123").set(WhichYearsPage, set).success.value
    navigator.nextPage(WhichYearsPage, CheckMode, userAnswers, hasAnswerChanged = true) mustBe
      routes.CountryOfYourOffshoreLiabilityController.onPageLoad(None, NormalMode)
  }

  "must go from WhichYearsPage in CheckMode to CYA when answer not changed" in {
    val year                    = current.back(1).startYear
    val set: Set[OffshoreYears] = Set(TaxYearStarting(year))
    val userAnswers             = UserAnswers("id", "session-123").set(WhichYearsPage, set).success.value
    navigator.nextPage(WhichYearsPage, CheckMode, userAnswers, hasAnswerChanged = false) mustBe
      routes.CheckYourAnswersController.onPageLoad
  }

  "must go from YourLegalInterpretationPage in CheckMode to normal route when answer changed" in {
    val set: Set[YourLegalInterpretation] = Set(AnotherIssue)
    val userAnswers                       = UserAnswers("id", "session-123").set(YourLegalInterpretationPage, set).success.value
    navigator.nextPage(YourLegalInterpretationPage, CheckMode, userAnswers, hasAnswerChanged = true) mustBe
      routes.UnderWhatConsiderationController.onPageLoad(NormalMode)
  }

  "must go from YourLegalInterpretationPage in CheckMode to CYA when answer not changed" in {
    val set: Set[YourLegalInterpretation] = Set(AnotherIssue)
    val userAnswers                       = UserAnswers("id", "session-123").set(YourLegalInterpretationPage, set).success.value
    navigator.nextPage(YourLegalInterpretationPage, CheckMode, userAnswers, hasAnswerChanged = false) mustBe
      routes.CheckYourAnswersController.onPageLoad
  }
}
