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
import controllers.onshore.routes
import models.WhatOnshoreLiabilitiesDoYouNeedToDisclose.{BusinessIncome, CorporationTax, DirectorLoan}
import pages._
import models._
import models.YourLegalInterpretation._
import models.store.{FullDisclosure, Metadata}
import models.store.disclosure.{CaseReference, OffshoreLiabilities, OnshoreLiabilities, OtherLiabilities, ReasonForDisclosingNow}
import models.store.notification.{AboutYou, Background, PersonalDetails}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.onshore.WhyDidYouNotFileAReturnOnTimeOnshorePage
import services.UAToDisclosureService
import uk.gov.hmrc.time.CurrentTaxYear

import java.time.{Instant, LocalDate}

class OnshoreNavigatorSpec extends SpecBase with CurrentTaxYear with MockitoSugar {

  val uaToDisclosureService = mock[UAToDisclosureService]
  val navigator             = new OnshoreNavigatorImpl(uaToDisclosureService)

  def now = () => LocalDate.now()

  "Onshore Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {
        case object UnknownPage extends Page
        navigator.nextPage(
          UnknownPage,
          NormalMode,
          UserAnswers("id", "session-123")
        ) mustBe controllers.routes.TaskListController.onPageLoad
      }

      "must go from WhyAreYouMakingThisOnshoreDisclosurePage to WhatOnshoreLiabilitiesDoYouNeedToDiscloseController when selected any other option(s)" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] =
          Set(WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturnNoCare)
        val userAnswers                                    =
          UserAnswers("id", "session-123").set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        navigator.nextPage(
          WhyAreYouMakingThisOnshoreDisclosurePage,
          NormalMode,
          userAnswers
        ) mustBe routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
      }

      "must go from CDFOnshorePage to YouHaveLeftTheDDSOnshoreControllerController when selected any other option(s) & false" in {
        val userAnswers = UserAnswers("id", "session-123").set(CDFOnshorePage, false).success.value
        navigator.nextPage(CDFOnshorePage, NormalMode, userAnswers) mustBe routes.YouHaveLeftTheDDSOnshoreController
          .onPageLoad(NormalMode)
      }

      "must go from CDFOnshorePage to WhatOnshoreLiabilitiesDoYouNeedToDiscloseController when selected any other option(s)" in {
        navigator.nextPage(
          CDFOnshorePage,
          NormalMode,
          UserAnswers("id", "session-123")
        ) mustBe routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
      }

      "must go from ReasonableExcuseOnshorePage to WhatOnshoreLiabilitiesDoYouNeedToDiscloseController when selected any other option(s)" in {
        navigator.nextPage(
          ReasonableExcuseOnshorePage,
          NormalMode,
          UserAnswers("id", "session-123")
        ) mustBe routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
      }

      "must go from ReasonableCareOnshorePage to WhatOnshoreLiabilitiesDoYouNeedToDiscloseController when selected any other option(s)" in {
        navigator.nextPage(
          ReasonableCareOnshorePage,
          NormalMode,
          UserAnswers("id", "session-123")
        ) mustBe routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
      }

      "must go from ReasonableExcuseForNotFilingOnshorePage to WhatOnshoreLiabilitiesDoYouNeedToDiscloseController when selected any other option(s)" in {
        navigator.nextPage(
          ReasonableExcuseForNotFilingOnshorePage,
          NormalMode,
          UserAnswers("id", "session-123")
        ) mustBe routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
      }

      "must go from WhatOnshoreLiabilitiesDoYouNeedToDisclose to WhichOnshoreYearsController when selected any other option(s)" in {
        navigator.nextPage(
          WhatOnshoreLiabilitiesDoYouNeedToDisclosePage,
          NormalMode,
          UserAnswers("id", "session-123")
        ) mustBe routes.WhichOnshoreYearsController.onPageLoad(NormalMode)
      }

      "must go from WhichOnshoreYearsPage to TaxBeforeThreeYearsOnshoreController when selected option PriorToThreeYears" in {
        val set: Set[OnshoreYears] = Set(PriorToThreeYears)
        val userAnswers            = UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, set).success.value
        navigator.nextPage(
          WhichOnshoreYearsPage,
          NormalMode,
          userAnswers
        ) mustBe routes.TaxBeforeThreeYearsOnshoreController.onPageLoad(NormalMode)
      }

      "must go from WhichOnshoreYearsPage to TaxBeforeFiveYearsOnshoreController when selected option PriorToFiveYears" in {
        val set: Set[OnshoreYears] = Set(PriorToFiveYears)
        val userAnswers            = UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, set).success.value
        navigator.nextPage(
          WhichOnshoreYearsPage,
          NormalMode,
          userAnswers
        ) mustBe routes.TaxBeforeFiveYearsOnshoreController.onPageLoad(NormalMode)
      }

      "must go from WhichOnshoreYearsPage to TaxBeforeNineteenYearsController when selected option PriorToNineteenYears" in {
        val set: Set[OnshoreYears] = Set(PriorToNineteenYears)
        val userAnswers            = UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, set).success.value
        navigator.nextPage(
          WhichOnshoreYearsPage,
          NormalMode,
          userAnswers
        ) mustBe routes.TaxBeforeNineteenYearsOnshoreController.onPageLoad(NormalMode)
      }

      "must go from WhichOnshoreYearsPage to NotIncludedSingleTaxYearController when not selected an entire interval" in {
        val year                   = current.back(1).startYear
        val year2                  = current.back(3).startYear
        val set: Set[OnshoreYears] = Set(OnshoreYearStarting(year), OnshoreYearStarting(year2))
        val userAnswers            = UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, set).success.value
        navigator.nextPage(
          WhichOnshoreYearsPage,
          NormalMode,
          userAnswers
        ) mustBe routes.NotIncludedSingleTaxYearController.onPageLoad(NormalMode)
      }

      "must go from WhichOnshoreYearsPage to NotIncludedMultipleTaxYearsController when multiple intervals are missing" in {
        val year                   = current.back(1).startYear
        val year2                  = current.back(3).startYear
        val year3                  = current.back(5).startYear
        val set: Set[OnshoreYears] =
          Set(OnshoreYearStarting(year), OnshoreYearStarting(year2), OnshoreYearStarting(year3))
        val userAnswers            = UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, set).success.value
        navigator.nextPage(
          WhichOnshoreYearsPage,
          NormalMode,
          userAnswers
        ) mustBe routes.NotIncludedMultipleTaxYearsController.onPageLoad(NormalMode)
      }

      "must go from WhichOnshoreYearsPage to OnshoreTaxYearLiabilitiesController" in {
        val year                   = current.back(1).startYear
        val set: Set[OnshoreYears] = Set(OnshoreYearStarting(year))
        val userAnswers            = UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, set).success.value
        navigator.nextPage(
          WhichOnshoreYearsPage,
          NormalMode,
          userAnswers
        ) mustBe routes.OnshoreTaxYearLiabilitiesController.onPageLoad(0, NormalMode)
      }

      "must go from WhichOnshoreYearsPage to PropertyAddedController" in {
        val year                                                                    = current.back(1).startYear
        val setOfOnshoreYears: Set[OnshoreYears]                                    = Set(OnshoreYearStarting(year))
        val setOfOnshoreLiabilities: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] =
          Set(WhatOnshoreLiabilitiesDoYouNeedToDisclose.LettingIncome)
        val userAnswers                                                             = for {
          ua1 <- UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, setOfOnshoreYears)
          ua2 <- ua1.set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, setOfOnshoreLiabilities)
        } yield ua2
        navigator.nextPage(
          WhichOnshoreYearsPage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.PropertyAddedController.onPageLoad(NormalMode)
      }

      "must go from NotIncludedSingleTaxYearPage to PropertyAddedController" in {
        val year                                                                    = current.back(1).startYear
        val setOfOnshoreYears: Set[OnshoreYears]                                    = Set(OnshoreYearStarting(year))
        val setOfOnshoreLiabilities: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] =
          Set(WhatOnshoreLiabilitiesDoYouNeedToDisclose.LettingIncome)
        val userAnswers                                                             = for {
          ua1 <- UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, setOfOnshoreYears)
          ua2 <- ua1.set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, setOfOnshoreLiabilities)
        } yield ua2
        navigator.nextPage(
          NotIncludedSingleTaxYearPage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.PropertyAddedController.onPageLoad(NormalMode)
      }

      "must go from NotIncludedSingleTaxYearPage to OnshoreTaxYearLiabilitiesController" in {
        val year                                                                    = current.back(1).startYear
        val setOfOnshoreYears: Set[OnshoreYears]                                    = Set(OnshoreYearStarting(year))
        val setOfOnshoreLiabilities: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] = Set()
        val userAnswers                                                             = for {
          ua1 <- UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, setOfOnshoreYears)
          ua2 <- ua1.set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, setOfOnshoreLiabilities)
        } yield ua2
        navigator.nextPage(
          NotIncludedSingleTaxYearPage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.OnshoreTaxYearLiabilitiesController.onPageLoad(0, NormalMode)
      }

      "must go from NotIncludedSingleTaxYearPage to TaxBeforeThreeYearsOnshoreController" in {
        val set: Set[OnshoreYears] = Set(PriorToThreeYears)
        val userAnswers            = UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, set).success.value
        navigator.nextPage(
          NotIncludedSingleTaxYearPage,
          NormalMode,
          userAnswers
        ) mustBe routes.TaxBeforeThreeYearsOnshoreController.onPageLoad(NormalMode)
      }

      "must go from NotIncludedSingleTaxYearPage to TaxBeforeFiveYearsOnshoreController" in {
        val set: Set[OnshoreYears] = Set(PriorToFiveYears)
        val userAnswers            = UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, set).success.value
        navigator.nextPage(
          NotIncludedSingleTaxYearPage,
          NormalMode,
          userAnswers
        ) mustBe routes.TaxBeforeFiveYearsOnshoreController.onPageLoad(NormalMode)
      }

      "must go from NotIncludedSingleTaxYearPage to TaxBeforeNineteenYearsOnshoreController" in {
        val set: Set[OnshoreYears] = Set(PriorToNineteenYears)
        val userAnswers            = UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, set).success.value
        navigator.nextPage(
          NotIncludedSingleTaxYearPage,
          NormalMode,
          userAnswers
        ) mustBe routes.TaxBeforeNineteenYearsOnshoreController.onPageLoad(NormalMode)
      }

      "must go from NotIncludedMultipleTaxYearsPage to PropertyAddedController" in {
        val year                                                                    = current.back(1).startYear
        val setOfOnshoreYears: Set[OnshoreYears]                                    = Set(OnshoreYearStarting(year))
        val setOfOnshoreLiabilities: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] =
          Set(WhatOnshoreLiabilitiesDoYouNeedToDisclose.LettingIncome)
        val userAnswers                                                             = for {
          ua1 <- UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, setOfOnshoreYears)
          ua2 <- ua1.set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, setOfOnshoreLiabilities)
        } yield ua2
        navigator.nextPage(
          NotIncludedMultipleTaxYearsPage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.PropertyAddedController.onPageLoad(NormalMode)
      }

      "must go from NotIncludedMultipleTaxYearsPage to OnshoreTaxYearLiabilitiesController" in {
        val year                                                                    = current.back(1).startYear
        val setOfOnshoreYears: Set[OnshoreYears]                                    = Set(OnshoreYearStarting(year))
        val setOfOnshoreLiabilities: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] = Set()
        val userAnswers                                                             = for {
          ua1 <- UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, setOfOnshoreYears)
          ua2 <- ua1.set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, setOfOnshoreLiabilities)
        } yield ua2
        navigator.nextPage(
          NotIncludedMultipleTaxYearsPage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.OnshoreTaxYearLiabilitiesController.onPageLoad(0, NormalMode)
      }

      "must go from NotIncludedMultipleTaxYearsPage to TaxBeforeThreeYearsOnshoreController" in {
        val set: Set[OnshoreYears] = Set(PriorToThreeYears)
        val userAnswers            = UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, set).success.value
        navigator.nextPage(
          NotIncludedMultipleTaxYearsPage,
          NormalMode,
          userAnswers
        ) mustBe routes.TaxBeforeThreeYearsOnshoreController.onPageLoad(NormalMode)
      }

      "must go from NotIncludedMultipleTaxYearsPage to TaxBeforeFiveYearsOnshoreController" in {
        val set: Set[OnshoreYears] = Set(PriorToFiveYears)
        val userAnswers            = UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, set).success.value
        navigator.nextPage(
          NotIncludedMultipleTaxYearsPage,
          NormalMode,
          userAnswers
        ) mustBe routes.TaxBeforeFiveYearsOnshoreController.onPageLoad(NormalMode)
      }

      "must go from NotIncludedMultipleTaxYearsPage to TaxBeforeNineteenYearsOnshoreController" in {
        val set: Set[OnshoreYears] = Set(PriorToNineteenYears)
        val userAnswers            = UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, set).success.value
        navigator.nextPage(
          NotIncludedMultipleTaxYearsPage,
          NormalMode,
          userAnswers
        ) mustBe routes.TaxBeforeNineteenYearsOnshoreController.onPageLoad(NormalMode)
      }

      "must go from TaxBeforeThreeYearsOnshorePage to PropertyAddedController if not a single property added before" in {
        val year                                                                    = current.back(1).startYear
        val setOfOnshoreYears: Set[OnshoreYears]                                    = Set(OnshoreYearStarting(year))
        val setOfOnshoreLiabilities: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] =
          Set(WhatOnshoreLiabilitiesDoYouNeedToDisclose.LettingIncome)
        val userAnswers                                                             = for {
          ua1 <- UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, setOfOnshoreYears)
          ua2 <- ua1.set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, setOfOnshoreLiabilities)
        } yield ua2
        navigator.nextPage(
          TaxBeforeThreeYearsOnshorePage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.PropertyAddedController.onPageLoad(NormalMode)
      }

      "must go from TaxBeforeThreeYearsOnshorePage to YouHaveNoOnshoreLiabilitiesToDiscloseController" in {
        val setOfOnshoreYears: Set[OnshoreYears]                                    = Set(PriorToThreeYears)
        val setOfOnshoreLiabilities: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] = Set()
        val userAnswers                                                             = for {
          ua1 <- UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, setOfOnshoreYears)
          ua2 <- ua1.set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, setOfOnshoreLiabilities)
          ua3 <- ua2.set(OffshoreLiabilitiesPage, true)
          ua4 <- ua3.set(OnshoreLiabilitiesPage, true)
        } yield ua4
        navigator.nextPage(
          TaxBeforeThreeYearsOnshorePage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.YouHaveNoOnshoreLiabilitiesToDiscloseController.onPageLoad
      }

      "must go from TaxBeforeThreeYearsOnshorePage to MakingNilDisclosureController" in {
        val setOfOnshoreYears: Set[OnshoreYears]                                    = Set(PriorToThreeYears)
        val setOfOnshoreLiabilities: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] = Set()
        val userAnswers                                                             = for {
          ua1 <- UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, setOfOnshoreYears)
          ua2 <- ua1.set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, setOfOnshoreLiabilities)
          ua3 <- ua2.set(OffshoreLiabilitiesPage, false)
          ua4 <- ua3.set(OnshoreLiabilitiesPage, false)
        } yield ua4
        navigator.nextPage(
          TaxBeforeThreeYearsOnshorePage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.MakingNilDisclosureController.onPageLoad
      }

      "must go from TaxBeforeThreeYearsOnshorePage to OnshoreTaxYearLiabilitiesController" in {
        val year                                                                    = current.back(1).startYear
        val setOfOnshoreYears: Set[OnshoreYears]                                    = Set(OnshoreYearStarting(year))
        val setOfOnshoreLiabilities: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] = Set()
        val userAnswers                                                             = for {
          ua1 <- UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, setOfOnshoreYears)
          ua2 <- ua1.set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, setOfOnshoreLiabilities)
        } yield ua2
        navigator.nextPage(
          TaxBeforeThreeYearsOnshorePage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.OnshoreTaxYearLiabilitiesController.onPageLoad(0, NormalMode)
      }

      "must go from TaxBeforeFiveYearsOnshorePage to PropertyAddedController if not a single property added before" in {
        val year                                                                    = current.back(1).startYear
        val setOfOnshoreYears: Set[OnshoreYears]                                    = Set(OnshoreYearStarting(year))
        val setOfOnshoreLiabilities: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] =
          Set(WhatOnshoreLiabilitiesDoYouNeedToDisclose.LettingIncome)
        val userAnswers                                                             = for {
          ua1 <- UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, setOfOnshoreYears)
          ua2 <- ua1.set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, setOfOnshoreLiabilities)
        } yield ua2
        navigator.nextPage(
          TaxBeforeFiveYearsOnshorePage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.PropertyAddedController.onPageLoad(NormalMode)
      }

      "must go from TaxBeforeFiveYearsOnshorePage to YouHaveNoOnshoreLiabilitiesToDiscloseController" in {
        val setOfOnshoreYears: Set[OnshoreYears]                                    = Set(PriorToFiveYears)
        val setOfOnshoreLiabilities: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] = Set()
        val userAnswers                                                             = for {
          ua1 <- UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, setOfOnshoreYears)
          ua2 <- ua1.set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, setOfOnshoreLiabilities)
          ua3 <- ua2.set(OffshoreLiabilitiesPage, true)
          ua4 <- ua3.set(OnshoreLiabilitiesPage, true)
        } yield ua4
        navigator.nextPage(
          TaxBeforeFiveYearsOnshorePage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.YouHaveNoOnshoreLiabilitiesToDiscloseController.onPageLoad
      }

      "must go from TaxBeforeFiveYearsOnshorePage to MakingNilDisclosureController" in {
        val setOfOnshoreYears: Set[OnshoreYears]                                    = Set(PriorToFiveYears)
        val setOfOnshoreLiabilities: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] = Set()
        val userAnswers                                                             = for {
          ua1 <- UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, setOfOnshoreYears)
          ua2 <- ua1.set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, setOfOnshoreLiabilities)
          ua3 <- ua2.set(OffshoreLiabilitiesPage, false)
          ua4 <- ua3.set(OnshoreLiabilitiesPage, false)
        } yield ua4
        navigator.nextPage(
          TaxBeforeFiveYearsOnshorePage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.MakingNilDisclosureController.onPageLoad
      }

      "must go from TaxBeforeFiveYearsOnshorePage to OnshoreTaxYearLiabilitiesController" in {
        val year                                                                    = current.back(1).startYear
        val setOfOnshoreYears: Set[OnshoreYears]                                    = Set(OnshoreYearStarting(year))
        val setOfOnshoreLiabilities: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] = Set()
        val userAnswers                                                             = for {
          ua1 <- UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, setOfOnshoreYears)
          ua2 <- ua1.set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, setOfOnshoreLiabilities)
        } yield ua2
        navigator.nextPage(
          TaxBeforeFiveYearsOnshorePage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.OnshoreTaxYearLiabilitiesController.onPageLoad(0, NormalMode)
      }

      "must go from TaxBeforeNineteenYearsOnshorePage to PropertyAddedController if not a single property added before" in {
        val year                                                                    = current.back(1).startYear
        val setOfOnshoreYears: Set[OnshoreYears]                                    = Set(OnshoreYearStarting(year))
        val setOfOnshoreLiabilities: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] =
          Set(WhatOnshoreLiabilitiesDoYouNeedToDisclose.LettingIncome)
        val userAnswers                                                             = for {
          ua1 <- UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, setOfOnshoreYears)
          ua2 <- ua1.set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, setOfOnshoreLiabilities)
        } yield ua2
        navigator.nextPage(
          TaxBeforeNineteenYearsOnshorePage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.PropertyAddedController.onPageLoad(NormalMode)
      }

      "must go from TaxBeforeNineteenYearsOnshorePage to YouHaveNoOnshoreLiabilitiesToDiscloseController" in {
        val setOfOnshoreYears: Set[OnshoreYears]                                    = Set(PriorToNineteenYears)
        val setOfOnshoreLiabilities: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] = Set()
        val userAnswers                                                             = for {
          ua1 <- UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, setOfOnshoreYears)
          ua2 <- ua1.set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, setOfOnshoreLiabilities)
          ua3 <- ua2.set(OffshoreLiabilitiesPage, true)
          ua4 <- ua3.set(OnshoreLiabilitiesPage, true)
        } yield ua4
        navigator.nextPage(
          TaxBeforeNineteenYearsOnshorePage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.YouHaveNoOnshoreLiabilitiesToDiscloseController.onPageLoad
      }

      "must go from TaxBeforeNineteenYearsOnshorePage to MakingNilDisclosureController" in {
        val setOfOnshoreYears: Set[OnshoreYears]                                    = Set(PriorToNineteenYears)
        val setOfOnshoreLiabilities: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] = Set()
        val userAnswers                                                             = for {
          ua1 <- UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, setOfOnshoreYears)
          ua2 <- ua1.set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, setOfOnshoreLiabilities)
          ua3 <- ua2.set(OffshoreLiabilitiesPage, false)
          ua4 <- ua3.set(OnshoreLiabilitiesPage, false)
        } yield ua4
        navigator.nextPage(
          TaxBeforeNineteenYearsOnshorePage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.MakingNilDisclosureController.onPageLoad
      }

      "must go from TaxBeforeNineteenYearsOnshorePage to OnshoreTaxYearLiabilitiesController" in {
        val year                                                                    = current.back(1).startYear
        val setOfOnshoreYears: Set[OnshoreYears]                                    = Set(OnshoreYearStarting(year))
        val setOfOnshoreLiabilities: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] = Set()
        val userAnswers                                                             = for {
          ua1 <- UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, setOfOnshoreYears)
          ua2 <- ua1.set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, setOfOnshoreLiabilities)
        } yield ua2
        navigator.nextPage(
          TaxBeforeNineteenYearsOnshorePage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.OnshoreTaxYearLiabilitiesController.onPageLoad(0, NormalMode)
      }

      "must go from WhatOnshoreLiabilitiesDoYouNeedToDisclose to the CorporationTaxLiabilityController when CorporationTax is selected" in {
        val set: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] = Set(CorporationTax)
        val userAnswers                                         =
          UserAnswers("id", "session-123").set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, set).success.value
        navigator.nextPage(
          WhatOnshoreLiabilitiesDoYouNeedToDisclosePage,
          NormalMode,
          userAnswers
        ) mustBe routes.CorporationTaxLiabilityController.onPageLoad(0, NormalMode)
      }

      "must go from WhatOnshoreLiabilitiesDoYouNeedToDisclose to DirectorLoanAccountLiabilitiesController when DirectorLoan is selected" in {
        val set: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] = Set(DirectorLoan)
        val userAnswers                                         =
          UserAnswers("id", "session-123").set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, set).success.value
        navigator.nextPage(
          WhatOnshoreLiabilitiesDoYouNeedToDisclosePage,
          NormalMode,
          userAnswers
        ) mustBe routes.DirectorLoanAccountLiabilitiesController.onPageLoad(0, NormalMode)
      }

      "must go from WhatOnshoreLiabilitiesDoYouNeedToDisclose to WhichOnshoreYearsController when the user has not selected CorporationTax or DirectorLoan" in {
        val set: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] = Set()
        val userAnswers                                         =
          UserAnswers("id", "session-123").set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, set).success.value
        navigator.nextPage(
          WhatOnshoreLiabilitiesDoYouNeedToDisclosePage,
          NormalMode,
          userAnswers
        ) mustBe routes.WhichOnshoreYearsController.onPageLoad(NormalMode)
      }

      "must go from AreYouAMemberOfAnyLandlordAssociationsPage to WhichLandlordAssociationsAreYouAMemberOfController" in {
        val userAnswers =
          UserAnswers("id", "session-123").set(AreYouAMemberOfAnyLandlordAssociationsPage, true).success.value
        navigator.nextPage(
          AreYouAMemberOfAnyLandlordAssociationsPage,
          NormalMode,
          userAnswers
        ) mustBe routes.WhichLandlordAssociationsAreYouAMemberOfController.onPageLoad(NormalMode)
      }

      "must go from AreYouAMemberOfAnyLandlordAssociationsPage to HowManyPropertiesDoYouCurrentlyLetOutController" in {
        val userAnswers =
          UserAnswers("id", "session-123").set(AreYouAMemberOfAnyLandlordAssociationsPage, false).success.value
        navigator.nextPage(
          AreYouAMemberOfAnyLandlordAssociationsPage,
          NormalMode,
          userAnswers
        ) mustBe routes.HowManyPropertiesDoYouCurrentlyLetOutController.onPageLoad(NormalMode)
      }

      "must go from WhichLandlordAssociationsAreYouAMemberOfPage to HowManyPropertiesDoYouCurrentlyLetOutController" in {
        navigator.nextPage(
          WhichLandlordAssociationsAreYouAMemberOfPage,
          NormalMode,
          UserAnswers("id", "session-123")
        ) mustBe routes.HowManyPropertiesDoYouCurrentlyLetOutController.onPageLoad(NormalMode)
      }

      "must go from HowManyPropertiesDoYouCurrentlyLetOutPage to OnshoreTaxYearLiabilitiesController" in {
        navigator.nextPage(
          HowManyPropertiesDoYouCurrentlyLetOutPage,
          NormalMode,
          UserAnswers("id", "session-123")
        ) mustBe routes.OnshoreTaxYearLiabilitiesController.onPageLoad(0, NormalMode)
      }

      "must go from DirectorLoanAccountLiabilitiesPage to DirectorLoanAccountLiabilitiesSummaryController" in {
        navigator.nextPage(
          DirectorLoanAccountLiabilitiesPage,
          NormalMode,
          UserAnswers("id", "session-123")
        ) mustBe routes.DirectorLoanAccountLiabilitiesSummaryController.onPageLoad(NormalMode)
      }

      "must go from AccountingPeriodDLAddedPage to DirectorLoanAccountLiabilitiesController" in {

        val fullDisclosure = getFullDisclosure()
        when(uaToDisclosureService.uaToFullDisclosure(any())).thenReturn(fullDisclosure)

        val setOfOnshoreLiabilities: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] = Set()

        val directorLoanAccountLiabilities: DirectorLoanAccountLiabilities = DirectorLoanAccountLiabilities(
          name = "Some Name 1",
          periodEnd = LocalDate.now(),
          overdrawn = BigInt(0),
          unpaidTax = BigInt(0),
          interest = BigInt(0),
          penaltyRate = 0,
          penaltyRateReason = "Some reason"
        )

        val set: Seq[DirectorLoanAccountLiabilities] = Seq(directorLoanAccountLiabilities)

        val userAnswers = for {
          ua1 <-
            UserAnswers("id", "session-123").set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, setOfOnshoreLiabilities)
          ua2 <- ua1.set(DirectorLoanAccountLiabilitiesPage, set)
          ua3 <- ua2.set(AccountingPeriodDLAddedPage, true)
        } yield ua3

        navigator.nextPage(
          AccountingPeriodDLAddedPage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.DirectorLoanAccountLiabilitiesController.onPageLoad(set.size, NormalMode)
      }

      "must go from AccountingPeriodDLAddedPage to WhichOnshoreYearsController" in {

        val setOfOnshoreLiabilities: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] = Set(DirectorLoan, BusinessIncome)
        val set: Seq[DirectorLoanAccountLiabilities]                                = Seq()

        val userAnswers = for {
          ua1 <-
            UserAnswers("id", "session-123").set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, setOfOnshoreLiabilities)
          ua2 <- ua1.set(DirectorLoanAccountLiabilitiesPage, set)
          ua3 <- ua2.set(AccountingPeriodDLAddedPage, false)
        } yield ua3

        navigator.nextPage(
          AccountingPeriodDLAddedPage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.WhichOnshoreYearsController.onPageLoad(NormalMode)
      }

      "must go from AccountingPeriodDLAddedPage to CheckYourAnswersController" in {

        val setOfOnshoreLiabilities: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] = Set()
        val set: Seq[DirectorLoanAccountLiabilities]                                = Seq()

        val userAnswers = for {
          ua1 <-
            UserAnswers("id", "session-123").set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, setOfOnshoreLiabilities)
          ua2 <- ua1.set(DirectorLoanAccountLiabilitiesPage, set)
          ua3 <- ua2.set(AccountingPeriodDLAddedPage, false)
        } yield ua3

        navigator.nextPage(
          AccountingPeriodDLAddedPage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from AccountingPeriodCTAddedPage to WhichOnshoreYearsController" in {

        val setOfOnshoreLiabilities: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] =
          Set(CorporationTax, BusinessIncome)

        val userAnswers = for {
          ua1 <-
            UserAnswers("id", "session-123").set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, setOfOnshoreLiabilities)
          ua2 <- ua1.set(CorporationTaxLiabilityPage, Seq())
          ua3 <- ua2.set(AccountingPeriodCTAddedPage, false)
        } yield ua3

        navigator.nextPage(
          AccountingPeriodCTAddedPage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.WhichOnshoreYearsController.onPageLoad(NormalMode)
      }

      "must go from AccountingPeriodCTAddedPage to Director Loans" in {

        val setOfOnshoreLiabilities: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] = Set(CorporationTax, DirectorLoan)

        val userAnswers = for {
          ua1 <-
            UserAnswers("id", "session-123").set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, setOfOnshoreLiabilities)
          ua2 <- ua1.set(CorporationTaxLiabilityPage, Seq())
          ua3 <- ua2.set(AccountingPeriodCTAddedPage, false)
        } yield ua3

        navigator.nextPage(
          AccountingPeriodCTAddedPage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.DirectorLoanAccountLiabilitiesController.onPageLoad(0, NormalMode)
      }

      "must go from AccountingPeriodCTAddedPage to CheckYourAnswersController" in {

        val setOfOnshoreLiabilities: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] = Set()

        val userAnswers = for {
          ua1 <-
            UserAnswers("id", "session-123").set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, setOfOnshoreLiabilities)
          ua2 <- ua1.set(CorporationTaxLiabilityPage, Seq())
          ua3 <- ua2.set(AccountingPeriodCTAddedPage, false)
        } yield ua3

        navigator.nextPage(
          AccountingPeriodCTAddedPage,
          NormalMode,
          userAnswers.success.value
        ) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from PropertyAddedPage to RentalAddressLookupController" in {
        val fullDisclosure = getFullDisclosure()
        when(uaToDisclosureService.uaToFullDisclosure(any())).thenReturn(fullDisclosure)

        val lettingProperty = LettingProperty()

        val userAnswers = for {
          ua1 <- UserAnswers("id", "session-123").set(PropertyAddedPage, true)
          ua2  = ua1.addToSeq(LettingPropertyPage, lettingProperty).success.value
        } yield ua2
        navigator.nextPage(
          PropertyAddedPage,
          NormalMode,
          userAnswers.success.value
        ) mustBe controllers.letting.routes.RentalAddressLookupController.lookupAddress(1, NormalMode)
      }

      "must go from PropertyAddedPage to AreYouAMemberOfAnyLandlordAssociationsController if no is selected and the section is not completed" in {
        val fullDisclosure = getFullDisclosure()
        when(uaToDisclosureService.uaToFullDisclosure(any())).thenReturn(fullDisclosure)

        val ua = UserAnswers("id", "session-123").set(PropertyAddedPage, false).success.value
        navigator.nextPage(
          PropertyAddedPage,
          CheckMode,
          ua
        ) mustBe routes.AreYouAMemberOfAnyLandlordAssociationsController.onPageLoad(NormalMode)
      }

      "must go from PropertyAddedPage to CheckYourAnswersController if no is selected and the section is completed" in {
        val fullDisclosure = getFullDisclosure(onshoreCompleted = true)
        when(uaToDisclosureService.uaToFullDisclosure(any())).thenReturn(fullDisclosure)

        val ua = UserAnswers("id", "session-123").set(PropertyAddedPage, false).success.value
        navigator.nextPage(PropertyAddedPage, CheckMode, ua) mustBe routes.CheckYourAnswersController.onPageLoad
      }

    }

    "nextTaxYearLiabilitiesPage" - {

      "must go to ResidentialReductionController with the current index when the residential reduction is set to true" in {
        val set: Set[OnshoreYears] = Set(OnshoreYearStarting(2012))
        val userAnswers            = UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, set).success.value
        navigator.nextTaxYearLiabilitiesPage(
          0,
          true,
          NormalMode,
          userAnswers,
          false
        ) mustBe routes.ResidentialReductionController.onPageLoad(0, NormalMode)
      }

      "must increment the index and take the user to the tax year liability page when there more years in the which years list" in {
        val whichYears: Set[OnshoreYears] = Set(
          OnshoreYearStarting(2021),
          OnshoreYearStarting(2020),
          OnshoreYearStarting(2019),
          OnshoreYearStarting(2018)
        )
        val userAnswersWithTaxYears       =
          UserAnswers(userAnswersId, "session-123").set(WhichOnshoreYearsPage, whichYears).success.value

        navigator.nextTaxYearLiabilitiesPage(
          0,
          false,
          NormalMode,
          userAnswersWithTaxYears
        ) mustBe routes.OnshoreTaxYearLiabilitiesController.onPageLoad(1, NormalMode)
        navigator.nextTaxYearLiabilitiesPage(
          1,
          false,
          NormalMode,
          userAnswersWithTaxYears
        ) mustBe routes.OnshoreTaxYearLiabilitiesController.onPageLoad(2, NormalMode)
        navigator.nextTaxYearLiabilitiesPage(
          2,
          false,
          NormalMode,
          userAnswersWithTaxYears
        ) mustBe routes.OnshoreTaxYearLiabilitiesController.onPageLoad(3, NormalMode)
      }

      "must take the user to the next page when there are no more years in the which years list" in {
        val whichYears: Set[OnshoreYears] = Set(
          OnshoreYearStarting(2021),
          OnshoreYearStarting(2020),
          OnshoreYearStarting(2019),
          OnshoreYearStarting(2018)
        )
        val userAnswersWithTaxYears       =
          UserAnswers(userAnswersId, "session-123").set(WhichOnshoreYearsPage, whichYears).success.value

        navigator.nextTaxYearLiabilitiesPage(
          3,
          false,
          NormalMode,
          userAnswersWithTaxYears
        ) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must take the user to CheckYourAnswersController when in Check mode and no changes are made" in {
        val whichYears: Set[OnshoreYears] = Set(
          OnshoreYearStarting(2021),
          OnshoreYearStarting(2020),
          OnshoreYearStarting(2019),
          OnshoreYearStarting(2018)
        )
        val userAnswersWithTaxYears       =
          UserAnswers(userAnswersId, "session-123").set(WhichOnshoreYearsPage, whichYears).success.value

        navigator.nextTaxYearLiabilitiesPage(
          3,
          false,
          CheckMode,
          userAnswersWithTaxYears
        ) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must take the user to ResidentialReductionController when in Check mode and changes are made and deduction is true" in {
        val currentIndex                  = 0
        val hasChanged                    = true
        val deduction                     = true
        val whichYears: Set[OnshoreYears] = Set(
          OnshoreYearStarting(2021),
          OnshoreYearStarting(2020),
          OnshoreYearStarting(2019),
          OnshoreYearStarting(2018)
        )
        val userAnswersWithTaxYears       =
          UserAnswers(userAnswersId, "session-123").set(WhichOnshoreYearsPage, whichYears).success.value

        navigator.nextTaxYearLiabilitiesPage(
          currentIndex,
          deduction,
          CheckMode,
          userAnswersWithTaxYears,
          hasChanged
        ) mustBe routes.ResidentialReductionController.onPageLoad(currentIndex, CheckMode)
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

      "must go from WhichOnshoreYearsPage to TaxBeforeThreeYearsOnshoreController when selected option PriorToThreeYears and something has changed" in {
        val set: Set[OnshoreYears] = Set(PriorToThreeYears)
        val userAnswers            = UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, set).success.value
        navigator.nextPage(
          WhichOnshoreYearsPage,
          CheckMode,
          userAnswers
        ) mustBe routes.TaxBeforeThreeYearsOnshoreController.onPageLoad(NormalMode)
      }

      "must go from WhichOnshoreYearsPage to TaxBeforeFiveYearsOnshoreController when selected option PriorToFiveYears and something has changed" in {
        val set: Set[OnshoreYears] = Set(PriorToFiveYears)
        val userAnswers            = UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, set).success.value
        navigator.nextPage(
          WhichOnshoreYearsPage,
          CheckMode,
          userAnswers
        ) mustBe routes.TaxBeforeFiveYearsOnshoreController.onPageLoad(NormalMode)
      }

      "must go from WhichOnshoreYearsPage to TaxBeforeNineteenYearsController when selected option PriorToNineteenYears and something has changed" in {
        val set: Set[OnshoreYears] = Set(PriorToNineteenYears)
        val userAnswers            = UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, set).success.value
        navigator.nextPage(
          WhichOnshoreYearsPage,
          CheckMode,
          userAnswers
        ) mustBe routes.TaxBeforeNineteenYearsOnshoreController.onPageLoad(NormalMode)
      }

      "must go from WhichOnshoreYearsPage to NotIncludedSingleTaxYearController when not selected an entire interval and something has changed" in {
        val year                   = current.back(1).startYear
        val year2                  = current.back(3).startYear
        val set: Set[OnshoreYears] = Set(OnshoreYearStarting(year), OnshoreYearStarting(year2))
        val userAnswers            = UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, set).success.value
        navigator.nextPage(
          WhichOnshoreYearsPage,
          CheckMode,
          userAnswers
        ) mustBe routes.NotIncludedSingleTaxYearController.onPageLoad(NormalMode)
      }

      "must go from WhichOnshoreYearsPage to NotIncludedMultipleTaxYearsController when multiple intervals are missing and something has changed" in {
        val year                   = current.back(1).startYear
        val year2                  = current.back(3).startYear
        val year3                  = current.back(5).startYear
        val set: Set[OnshoreYears] =
          Set(OnshoreYearStarting(year), OnshoreYearStarting(year2), OnshoreYearStarting(year3))
        val userAnswers            = UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, set).success.value
        navigator.nextPage(
          WhichOnshoreYearsPage,
          CheckMode,
          userAnswers
        ) mustBe routes.NotIncludedMultipleTaxYearsController.onPageLoad(NormalMode)
      }

      "must go from WhichOnshoreYearsPage to PropertyAddedController and something has changed and letting income selected and not a single property added" in {
        val year                                                                    = current.back(1).startYear
        val set: Set[OnshoreYears]                                                  = Set(OnshoreYearStarting(year))
        val setOfOnshoreLiabilities: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] =
          Set(WhatOnshoreLiabilitiesDoYouNeedToDisclose.LettingIncome)
        val userAnswers                                                             = for {
          ua1 <- UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, set)
          ua2 <- ua1.set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, setOfOnshoreLiabilities)
        } yield ua2
        navigator.nextPage(
          WhichOnshoreYearsPage,
          CheckMode,
          userAnswers.success.value
        ) mustBe routes.PropertyAddedController.onPageLoad(NormalMode)
      }

      "must go from WhichOnshoreYearsPage to OnshoreTaxYearLiabilitiesController and something has changed" in {
        val year                   = current.back(1).startYear
        val set: Set[OnshoreYears] = Set(OnshoreYearStarting(year))
        val userAnswers            = UserAnswers("id", "session-123").set(WhichOnshoreYearsPage, set).success.value
        navigator.nextPage(
          WhichOnshoreYearsPage,
          CheckMode,
          userAnswers
        ) mustBe routes.OnshoreTaxYearLiabilitiesController.onPageLoad(0, NormalMode)
      }

      "must go from WhichOnshoreYearsPage to CheckYourAnswersController if something has changed" in {
        navigator.nextPage(
          WhichOnshoreYearsPage,
          CheckMode,
          UserAnswers("id", "session-123"),
          hasAnswerChanged = false
        ) mustBe routes.CheckYourAnswersController.onPageLoad
      }
    }

    "must go from DirectorLoanAccountLiabilitiesPage to DirectorLoanAccountLiabilitiesSummaryController" in {
      navigator.nextPage(
        DirectorLoanAccountLiabilitiesPage,
        CheckMode,
        UserAnswers("id", "session-123")
      ) mustBe routes.DirectorLoanAccountLiabilitiesSummaryController.onPageLoad(NormalMode)
    }

    "must go from CorporationTaxLiabilityPage to CorporationTaxSummaryController" in {
      navigator.nextPage(
        CorporationTaxLiabilityPage,
        CheckMode,
        UserAnswers("id", "session-123")
      ) mustBe routes.CorporationTaxSummaryController.onPageLoad(NormalMode)
    }

    "must go from PropertyAddedPage to AreYouAMemberOfAnyLandlordAssociationsController if no is selected and the section is not completed" in {
      val fullDisclosure = getFullDisclosure()
      when(uaToDisclosureService.uaToFullDisclosure(any())).thenReturn(fullDisclosure)

      val ua = UserAnswers("id", "session-123").set(PropertyAddedPage, false).success.value
      navigator.nextPage(
        PropertyAddedPage,
        CheckMode,
        ua
      ) mustBe routes.AreYouAMemberOfAnyLandlordAssociationsController.onPageLoad(NormalMode)
    }

    "must go from PropertyAddedPage to CheckYourAnswersController if no is selected and the section is completed" in {
      val fullDisclosure = getFullDisclosure(onshoreCompleted = true)
      when(uaToDisclosureService.uaToFullDisclosure(any())).thenReturn(fullDisclosure)

      val ua = UserAnswers("id", "session-123").set(PropertyAddedPage, false).success.value
      navigator.nextPage(PropertyAddedPage, CheckMode, ua) mustBe routes.CheckYourAnswersController.onPageLoad
    }

    "must go from PropertyAddedPage to AreYouAMemberOfAnyLandlordAssociationsController if yes is selected and the section is completed" in {
      val fullDisclosure = getFullDisclosure(onshoreCompleted = true)
      when(uaToDisclosureService.uaToFullDisclosure(any())).thenReturn(fullDisclosure)

      val ua = UserAnswers("id", "session-123").set(PropertyAddedPage, true).success.value
      navigator.nextPage(
        PropertyAddedPage,
        CheckMode,
        ua
      ) mustBe routes.AreYouAMemberOfAnyLandlordAssociationsController.onPageLoad(NormalMode)
    }

    "must go from AreYouAMemberOfAnyLandlordAssociationsPage to WhichLandlordAssociationsAreYouAMemberOfController if previous answer changed from no to yes" in {
      val ua = UserAnswers("id", "session-123").set(AreYouAMemberOfAnyLandlordAssociationsPage, true).success.value
      navigator.nextPage(
        AreYouAMemberOfAnyLandlordAssociationsPage,
        CheckMode,
        ua,
        true
      ) mustBe routes.WhichLandlordAssociationsAreYouAMemberOfController.onPageLoad(CheckMode)
    }

    "must go from AreYouAMemberOfAnyLandlordAssociationsPage to CheckYourAnswersController if previous answer changed from yes to no" in {
      val ua = UserAnswers("id", "session-123").set(AreYouAMemberOfAnyLandlordAssociationsPage, false).success.value
      navigator.nextPage(
        AreYouAMemberOfAnyLandlordAssociationsPage,
        CheckMode,
        ua,
        false
      ) mustBe routes.CheckYourAnswersController.onPageLoad
    }

    "must go from WhyAreYouMakingThisOnshoreDisclosurePage to WhyDidYouNotNotifyOnshoreController when DidNotNotifyHMRC selected" in {
      val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHMRC)
      val userAnswers                                    =
        UserAnswers("id", "session-123").set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
      navigator.nextPage(WhyAreYouMakingThisOnshoreDisclosurePage, NormalMode, userAnswers) mustBe
        routes.WhyDidYouNotNotifyOnshoreController.onPageLoad(NormalMode)
    }

    "must go from WhyAreYouMakingThisOnshoreDisclosurePage to WhyDidYouNotFileAReturnOnTimeOnshoreController when DidNotFile selected" in {
      val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.DidNotFile)
      val userAnswers                                    =
        UserAnswers("id", "session-123").set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
      navigator.nextPage(WhyAreYouMakingThisOnshoreDisclosurePage, NormalMode, userAnswers) mustBe
        routes.WhyDidYouNotFileAReturnOnTimeOnshoreController.onPageLoad(NormalMode)
    }

    "must go from WhyAreYouMakingThisOnshoreDisclosurePage to WhyYouSubmittedAnInaccurateOnshoreReturnController when InaccurateReturn selected" in {
      val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturn)
      val userAnswers                                    =
        UserAnswers("id", "session-123").set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
      navigator.nextPage(WhyAreYouMakingThisOnshoreDisclosurePage, NormalMode, userAnswers) mustBe
        routes.WhyYouSubmittedAnInaccurateOnshoreReturnController.onPageLoad(NormalMode)
    }

    "must go from WhyDidYouNotNotifyOnshorePage to WhyDidYouNotFileAReturnOnTimeOnshoreController when DidNotFile also selected" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(
                 WhyAreYouMakingThisOnshoreDisclosurePage,
                 Set[WhyAreYouMakingThisOnshoreDisclosure](WhyAreYouMakingThisOnshoreDisclosure.DidNotFile)
               )
        ua2 <- ua.set(
                 WhyDidYouNotNotifyOnshorePage,
                 Set[WhyDidYouNotNotifyOnshore](WhyDidYouNotNotifyOnshore.NotDeliberatelyNoReasonableExcuseOnshore)
               )
      } yield ua2).success.value
      navigator.nextPage(WhyDidYouNotNotifyOnshorePage, NormalMode, userAnswers) mustBe
        routes.WhyDidYouNotFileAReturnOnTimeOnshoreController.onPageLoad(NormalMode)
    }

    "must go from WhyDidYouNotNotifyOnshorePage to WhyYouSubmittedAnInaccurateOnshoreReturnController when InaccurateReturn also selected" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(
                 WhyAreYouMakingThisOnshoreDisclosurePage,
                 Set[WhyAreYouMakingThisOnshoreDisclosure](WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturn)
               )
        ua2 <- ua.set(
                 WhyDidYouNotNotifyOnshorePage,
                 Set[WhyDidYouNotNotifyOnshore](WhyDidYouNotNotifyOnshore.NotDeliberatelyNoReasonableExcuseOnshore)
               )
      } yield ua2).success.value
      navigator.nextPage(WhyDidYouNotNotifyOnshorePage, NormalMode, userAnswers) mustBe
        routes.WhyYouSubmittedAnInaccurateOnshoreReturnController.onPageLoad(NormalMode)
    }

    "must go from WhyDidYouNotNotifyOnshorePage to CDFOnshoreController when deliberate selected" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.AnIndividual)
        ua2 <- ua.set(
                 WhyDidYouNotNotifyOnshorePage,
                 Set[WhyDidYouNotNotifyOnshore](WhyDidYouNotNotifyOnshore.DeliberatelyDidNotNotifyOnshore)
               )
      } yield ua2).success.value
      navigator.nextPage(WhyDidYouNotNotifyOnshorePage, NormalMode, userAnswers) mustBe
        routes.CDFOnshoreController.onPageLoad(NormalMode)
    }

    "must go from WhyDidYouNotNotifyOnshorePage to ReasonableExcuseOnshoreController when ReasonableExcuse selected and no deliberate" in {
      val userAnswers = UserAnswers("id", "session-123")
        .set(
          WhyDidYouNotNotifyOnshorePage,
          Set[WhyDidYouNotNotifyOnshore](WhyDidYouNotNotifyOnshore.ReasonableExcuseOnshore)
        )
        .success
        .value
      navigator.nextPage(WhyDidYouNotNotifyOnshorePage, NormalMode, userAnswers) mustBe
        routes.ReasonableExcuseOnshoreController.onPageLoad(NormalMode)
    }

    "must go from WhyDidYouNotNotifyOnshorePage to WhatOnshoreLiabilitiesDoYouNeedToDiscloseController when no special conditions" in {
      val userAnswers = UserAnswers("id", "session-123")
        .set(
          WhyDidYouNotNotifyOnshorePage,
          Set[WhyDidYouNotNotifyOnshore](WhyDidYouNotNotifyOnshore.NotDeliberatelyNoReasonableExcuseOnshore)
        )
        .success
        .value
      navigator.nextPage(WhyDidYouNotNotifyOnshorePage, NormalMode, userAnswers) mustBe
        routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
    }

    "must go from WhyDidYouNotNotifyOnshorePage to WhatOnshoreLiabilitiesDoYouNeedToDiscloseController when entity is AnEstate with deliberate" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.AnEstate)
        ua2 <- ua.set(
                 WhyDidYouNotNotifyOnshorePage,
                 Set[WhyDidYouNotNotifyOnshore](WhyDidYouNotNotifyOnshore.DeliberatelyDidNotNotifyOnshore)
               )
      } yield ua2).success.value
      navigator.nextPage(WhyDidYouNotNotifyOnshorePage, NormalMode, userAnswers) mustBe
        routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
    }

    "must go from WhyDidYouNotFileAReturnOnTimeOnshorePage to WhyYouSubmittedAnInaccurateOnshoreReturnController when InaccurateReturn also selected" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(
                 WhyAreYouMakingThisOnshoreDisclosurePage,
                 Set[WhyAreYouMakingThisOnshoreDisclosure](WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturn)
               )
        ua2 <- ua.set(
                 WhyDidYouNotFileAReturnOnTimeOnshorePage,
                 Set[WhyDidYouNotFileAReturnOnTimeOnshore](
                   WhyDidYouNotFileAReturnOnTimeOnshore.DidNotWithholdInformationOnPurpose
                 )
               )
      } yield ua2).success.value
      navigator.nextPage(WhyDidYouNotFileAReturnOnTimeOnshorePage, NormalMode, userAnswers) mustBe
        routes.WhyYouSubmittedAnInaccurateOnshoreReturnController.onPageLoad(NormalMode)
    }

    "must go from WhyDidYouNotFileAReturnOnTimeOnshorePage to CDFOnshoreController when deliberate selected" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.AnIndividual)
        ua2 <- ua.set(
                 WhyDidYouNotFileAReturnOnTimeOnshorePage,
                 Set[WhyDidYouNotFileAReturnOnTimeOnshore](
                   WhyDidYouNotFileAReturnOnTimeOnshore.DeliberatelyWithheldInformation
                 )
               )
      } yield ua2).success.value
      navigator.nextPage(WhyDidYouNotFileAReturnOnTimeOnshorePage, NormalMode, userAnswers) mustBe
        routes.CDFOnshoreController.onPageLoad(NormalMode)
    }

    "must go from WhyDidYouNotFileAReturnOnTimeOnshorePage to ReasonableExcuseOnshoreController when DidNotNotifyHMRC also selected and page2a has ReasonableExcuse" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(
                 WhyAreYouMakingThisOnshoreDisclosurePage,
                 Set[WhyAreYouMakingThisOnshoreDisclosure](WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHMRC)
               )
        ua2 <- ua.set(
                 WhyDidYouNotNotifyOnshorePage,
                 Set[WhyDidYouNotNotifyOnshore](WhyDidYouNotNotifyOnshore.ReasonableExcuseOnshore)
               )
        ua3 <- ua2.set(
                 WhyDidYouNotFileAReturnOnTimeOnshorePage,
                 Set[WhyDidYouNotFileAReturnOnTimeOnshore](
                   WhyDidYouNotFileAReturnOnTimeOnshore.DidNotWithholdInformationOnPurpose
                 )
               )
      } yield ua3).success.value
      navigator.nextPage(WhyDidYouNotFileAReturnOnTimeOnshorePage, NormalMode, userAnswers) mustBe
        routes.ReasonableExcuseOnshoreController.onPageLoad(NormalMode)
    }

    "must go from WhyDidYouNotFileAReturnOnTimeOnshorePage to ReasonableExcuseForNotFilingOnshoreController when ReasonableExcuse selected" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(
                 WhyAreYouMakingThisOnshoreDisclosurePage,
                 Set[WhyAreYouMakingThisOnshoreDisclosure](WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHMRC)
               )
        ua2 <- ua.set(
                 WhyDidYouNotNotifyOnshorePage,
                 Set[WhyDidYouNotNotifyOnshore](WhyDidYouNotNotifyOnshore.NotDeliberatelyNoReasonableExcuseOnshore)
               )
        ua3 <- ua2.set(
                 WhyDidYouNotFileAReturnOnTimeOnshorePage,
                 Set[WhyDidYouNotFileAReturnOnTimeOnshore](WhyDidYouNotFileAReturnOnTimeOnshore.ReasonableExcuse)
               )
      } yield ua3).success.value
      navigator.nextPage(WhyDidYouNotFileAReturnOnTimeOnshorePage, NormalMode, userAnswers) mustBe
        routes.ReasonableExcuseForNotFilingOnshoreController.onPageLoad(NormalMode)
    }

    "must go from WhyDidYouNotFileAReturnOnTimeOnshorePage to ReasonableExcuseForNotFilingOnshoreController when ReasonableExcuse selected and no DidNotNotifyHMRC" in {
      val userAnswers = UserAnswers("id", "session-123")
        .set(
          WhyDidYouNotFileAReturnOnTimeOnshorePage,
          Set[WhyDidYouNotFileAReturnOnTimeOnshore](WhyDidYouNotFileAReturnOnTimeOnshore.ReasonableExcuse)
        )
        .success
        .value
      navigator.nextPage(WhyDidYouNotFileAReturnOnTimeOnshorePage, NormalMode, userAnswers) mustBe
        routes.ReasonableExcuseForNotFilingOnshoreController.onPageLoad(NormalMode)
    }

    "must go from WhyDidYouNotFileAReturnOnTimeOnshorePage to WhatOnshoreLiabilitiesDoYouNeedToDiscloseController when no special conditions" in {
      val userAnswers = UserAnswers("id", "session-123")
        .set(
          WhyDidYouNotFileAReturnOnTimeOnshorePage,
          Set[WhyDidYouNotFileAReturnOnTimeOnshore](
            WhyDidYouNotFileAReturnOnTimeOnshore.DidNotWithholdInformationOnPurpose
          )
        )
        .success
        .value
      navigator.nextPage(WhyDidYouNotFileAReturnOnTimeOnshorePage, NormalMode, userAnswers) mustBe
        routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
    }

    // WhyYouSubmittedAnInaccurateOnshoreReturnPage routing
    "must go from WhyYouSubmittedAnInaccurateOnshoreReturnPage to CDFOnshoreController when deliberate selected" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.AnIndividual)
        ua2 <- ua.set(
                 WhyYouSubmittedAnInaccurateOnshoreReturnPage,
                 Set[WhyYouSubmittedAnInaccurateOnshoreReturn](
                   WhyYouSubmittedAnInaccurateOnshoreReturn.DeliberatelyInaccurate
                 )
               )
      } yield ua2).success.value
      navigator.nextPage(WhyYouSubmittedAnInaccurateOnshoreReturnPage, NormalMode, userAnswers) mustBe
        routes.CDFOnshoreController.onPageLoad(NormalMode)
    }

    "must go from WhyYouSubmittedAnInaccurateOnshoreReturnPage to ReasonableCareOnshoreController when ReasonableMistake selected" in {
      val userAnswers = UserAnswers("id", "session-123")
        .set(
          WhyYouSubmittedAnInaccurateOnshoreReturnPage,
          Set[WhyYouSubmittedAnInaccurateOnshoreReturn](WhyYouSubmittedAnInaccurateOnshoreReturn.ReasonableMistake)
        )
        .success
        .value
      navigator.nextPage(WhyYouSubmittedAnInaccurateOnshoreReturnPage, NormalMode, userAnswers) mustBe
        routes.ReasonableCareOnshoreController.onPageLoad(NormalMode)
    }

    "must go from WhyYouSubmittedAnInaccurateOnshoreReturnPage to WhatOnshoreLiabilitiesDoYouNeedToDiscloseController when no special conditions" in {
      val userAnswers = UserAnswers("id", "session-123")
        .set(
          WhyYouSubmittedAnInaccurateOnshoreReturnPage,
          Set[WhyYouSubmittedAnInaccurateOnshoreReturn](WhyYouSubmittedAnInaccurateOnshoreReturn.NoReasonableCare)
        )
        .success
        .value
      navigator.nextPage(WhyYouSubmittedAnInaccurateOnshoreReturnPage, NormalMode, userAnswers) mustBe
        routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
    }

    "must go from WhyYouSubmittedAnInaccurateOnshoreReturnPage to ReasonableExcuseOnshoreController when DidNotNotifyHMRC selected and page2a has ReasonableExcuse" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(
                 WhyAreYouMakingThisOnshoreDisclosurePage,
                 Set[WhyAreYouMakingThisOnshoreDisclosure](WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHMRC)
               )
        ua2 <- ua.set(
                 WhyDidYouNotNotifyOnshorePage,
                 Set[WhyDidYouNotNotifyOnshore](WhyDidYouNotNotifyOnshore.ReasonableExcuseOnshore)
               )
        ua3 <-
          ua2.set(
            WhyYouSubmittedAnInaccurateOnshoreReturnPage,
            Set[WhyYouSubmittedAnInaccurateOnshoreReturn](WhyYouSubmittedAnInaccurateOnshoreReturn.NoReasonableCare)
          )
      } yield ua3).success.value
      navigator.nextPage(WhyYouSubmittedAnInaccurateOnshoreReturnPage, NormalMode, userAnswers) mustBe
        routes.ReasonableExcuseOnshoreController.onPageLoad(NormalMode)
    }

    "must go from WhyYouSubmittedAnInaccurateOnshoreReturnPage to ReasonableExcuseForNotFilingOnshoreController when DidNotFile selected and page2b has ReasonableExcuse" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(
                 WhyAreYouMakingThisOnshoreDisclosurePage,
                 Set[WhyAreYouMakingThisOnshoreDisclosure](WhyAreYouMakingThisOnshoreDisclosure.DidNotFile)
               )
        ua2 <- ua.set(
                 WhyDidYouNotFileAReturnOnTimeOnshorePage,
                 Set[WhyDidYouNotFileAReturnOnTimeOnshore](WhyDidYouNotFileAReturnOnTimeOnshore.ReasonableExcuse)
               )
        ua3 <-
          ua2.set(
            WhyYouSubmittedAnInaccurateOnshoreReturnPage,
            Set[WhyYouSubmittedAnInaccurateOnshoreReturn](WhyYouSubmittedAnInaccurateOnshoreReturn.NoReasonableCare)
          )
      } yield ua3).success.value
      navigator.nextPage(WhyYouSubmittedAnInaccurateOnshoreReturnPage, NormalMode, userAnswers) mustBe
        routes.ReasonableExcuseForNotFilingOnshoreController.onPageLoad(NormalMode)
    }

    "must go from WhyYouSubmittedAnInaccurateOnshoreReturnPage to ReasonableCareOnshoreController when DidNotFile selected and page2b no ReasonableExcuse but page2c has ReasonableMistake" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(
                 WhyAreYouMakingThisOnshoreDisclosurePage,
                 Set[WhyAreYouMakingThisOnshoreDisclosure](WhyAreYouMakingThisOnshoreDisclosure.DidNotFile)
               )
        ua2 <- ua.set(
                 WhyDidYouNotFileAReturnOnTimeOnshorePage,
                 Set[WhyDidYouNotFileAReturnOnTimeOnshore](
                   WhyDidYouNotFileAReturnOnTimeOnshore.DidNotWithholdInformationOnPurpose
                 )
               )
        ua3 <- ua2.set(
                 WhyYouSubmittedAnInaccurateOnshoreReturnPage,
                 Set[WhyYouSubmittedAnInaccurateOnshoreReturn](
                   WhyYouSubmittedAnInaccurateOnshoreReturn.ReasonableMistake
                 )
               )
      } yield ua3).success.value
      navigator.nextPage(WhyYouSubmittedAnInaccurateOnshoreReturnPage, NormalMode, userAnswers) mustBe
        routes.ReasonableCareOnshoreController.onPageLoad(NormalMode)
    }

    "must go from CDFOnshorePage to ReasonableExcuseOnshoreController when true and DidNotNotifyHMRC selected with ReasonableExcuse" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(CDFOnshorePage, true)
        ua2 <- ua.set(
                 WhyAreYouMakingThisOnshoreDisclosurePage,
                 Set[WhyAreYouMakingThisOnshoreDisclosure](WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHMRC)
               )
        ua3 <- ua2.set(
                 WhyDidYouNotNotifyOnshorePage,
                 Set[WhyDidYouNotNotifyOnshore](WhyDidYouNotNotifyOnshore.ReasonableExcuseOnshore)
               )
      } yield ua3).success.value
      navigator.nextPage(CDFOnshorePage, NormalMode, userAnswers) mustBe
        routes.ReasonableExcuseOnshoreController.onPageLoad(NormalMode)
    }

    "must go from CDFOnshorePage to ReasonableExcuseForNotFilingOnshoreController when true and DidNotFile selected with ReasonableExcuse" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(CDFOnshorePage, true)
        ua2 <- ua.set(
                 WhyAreYouMakingThisOnshoreDisclosurePage,
                 Set[WhyAreYouMakingThisOnshoreDisclosure](
                   WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHMRC,
                   WhyAreYouMakingThisOnshoreDisclosure.DidNotFile
                 )
               )
        ua3 <- ua2.set(
                 WhyDidYouNotNotifyOnshorePage,
                 Set[WhyDidYouNotNotifyOnshore](WhyDidYouNotNotifyOnshore.NotDeliberatelyNoReasonableExcuseOnshore)
               )
        ua4 <- ua3.set(
                 WhyDidYouNotFileAReturnOnTimeOnshorePage,
                 Set[WhyDidYouNotFileAReturnOnTimeOnshore](WhyDidYouNotFileAReturnOnTimeOnshore.ReasonableExcuse)
               )
      } yield ua4).success.value
      navigator.nextPage(CDFOnshorePage, NormalMode, userAnswers) mustBe
        routes.ReasonableExcuseForNotFilingOnshoreController.onPageLoad(NormalMode)
    }

    "must go from CDFOnshorePage to ReasonableCareOnshoreController when true, DidNotNotifyHMRC+DidNotFile+InaccurateReturn selected and page2c has ReasonableMistake" in {
      val userAnswers = (for {
        ua1 <- UserAnswers("id", "session-123").set(CDFOnshorePage, true)
        ua2 <- ua1.set(
                 WhyAreYouMakingThisOnshoreDisclosurePage,
                 Set[WhyAreYouMakingThisOnshoreDisclosure](
                   WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHMRC,
                   WhyAreYouMakingThisOnshoreDisclosure.DidNotFile,
                   WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturn
                 )
               )
        ua3 <- ua2.set(
                 WhyDidYouNotNotifyOnshorePage,
                 Set[WhyDidYouNotNotifyOnshore](WhyDidYouNotNotifyOnshore.NotDeliberatelyNoReasonableExcuseOnshore)
               )
        ua4 <- ua3.set(
                 WhyDidYouNotFileAReturnOnTimeOnshorePage,
                 Set[WhyDidYouNotFileAReturnOnTimeOnshore](
                   WhyDidYouNotFileAReturnOnTimeOnshore.DidNotWithholdInformationOnPurpose
                 )
               )
        ua5 <- ua4.set(
                 WhyYouSubmittedAnInaccurateOnshoreReturnPage,
                 Set[WhyYouSubmittedAnInaccurateOnshoreReturn](
                   WhyYouSubmittedAnInaccurateOnshoreReturn.ReasonableMistake
                 )
               )
      } yield ua5).success.value
      navigator.nextPage(CDFOnshorePage, NormalMode, userAnswers) mustBe
        routes.ReasonableCareOnshoreController.onPageLoad(NormalMode)
    }

    "must go from CDFOnshorePage to WhatOnshoreLiabilitiesDoYouNeedToDiscloseController when true and all pages answered with no further excuses" in {
      val userAnswers = UserAnswers("id", "session-123").set(CDFOnshorePage, true).success.value
      navigator.nextPage(CDFOnshorePage, NormalMode, userAnswers) mustBe
        routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
    }

    "must go from CDFOnshorePage to ReasonableCareOnshoreController when true, only InaccurateReturn selected and page2c has ReasonableMistake" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(CDFOnshorePage, true)
        ua2 <- ua.set(
                 WhyAreYouMakingThisOnshoreDisclosurePage,
                 Set[WhyAreYouMakingThisOnshoreDisclosure](WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturn)
               )
        ua3 <- ua2.set(
                 WhyYouSubmittedAnInaccurateOnshoreReturnPage,
                 Set[WhyYouSubmittedAnInaccurateOnshoreReturn](
                   WhyYouSubmittedAnInaccurateOnshoreReturn.ReasonableMistake
                 )
               )
      } yield ua3).success.value
      navigator.nextPage(CDFOnshorePage, NormalMode, userAnswers) mustBe
        routes.ReasonableCareOnshoreController.onPageLoad(NormalMode)
    }

    "must go from CDFOnshorePage to WhatOnshoreLiabilitiesDoYouNeedToDiscloseController when true, only InaccurateReturn and no ReasonableMistake" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(CDFOnshorePage, true)
        ua2 <- ua.set(
                 WhyAreYouMakingThisOnshoreDisclosurePage,
                 Set[WhyAreYouMakingThisOnshoreDisclosure](WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturn)
               )
        ua3 <-
          ua2.set(
            WhyYouSubmittedAnInaccurateOnshoreReturnPage,
            Set[WhyYouSubmittedAnInaccurateOnshoreReturn](WhyYouSubmittedAnInaccurateOnshoreReturn.NoReasonableCare)
          )
      } yield ua3).success.value
      navigator.nextPage(CDFOnshorePage, NormalMode, userAnswers) mustBe
        routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
    }

    "must go from CDFOnshorePage to ReasonableExcuseForNotFilingOnshoreController when true, only DidNotFile and page2b has ReasonableExcuse" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(CDFOnshorePage, true)
        ua2 <- ua.set(
                 WhyAreYouMakingThisOnshoreDisclosurePage,
                 Set[WhyAreYouMakingThisOnshoreDisclosure](WhyAreYouMakingThisOnshoreDisclosure.DidNotFile)
               )
        ua3 <- ua2.set(
                 WhyDidYouNotFileAReturnOnTimeOnshorePage,
                 Set[WhyDidYouNotFileAReturnOnTimeOnshore](WhyDidYouNotFileAReturnOnTimeOnshore.ReasonableExcuse)
               )
      } yield ua3).success.value
      navigator.nextPage(CDFOnshorePage, NormalMode, userAnswers) mustBe
        routes.ReasonableExcuseForNotFilingOnshoreController.onPageLoad(NormalMode)
    }

    "must go from CDFOnshorePage to ReasonableCareOnshoreController when true, DidNotFile+InaccurateReturn and page2c has ReasonableMistake" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(CDFOnshorePage, true)
        ua2 <- ua.set(
                 WhyAreYouMakingThisOnshoreDisclosurePage,
                 Set[WhyAreYouMakingThisOnshoreDisclosure](
                   WhyAreYouMakingThisOnshoreDisclosure.DidNotFile,
                   WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturn
                 )
               )
        ua3 <- ua2.set(
                 WhyDidYouNotFileAReturnOnTimeOnshorePage,
                 Set[WhyDidYouNotFileAReturnOnTimeOnshore](
                   WhyDidYouNotFileAReturnOnTimeOnshore.DidNotWithholdInformationOnPurpose
                 )
               )
        ua4 <- ua3.set(
                 WhyYouSubmittedAnInaccurateOnshoreReturnPage,
                 Set[WhyYouSubmittedAnInaccurateOnshoreReturn](
                   WhyYouSubmittedAnInaccurateOnshoreReturn.ReasonableMistake
                 )
               )
      } yield ua4).success.value
      navigator.nextPage(CDFOnshorePage, NormalMode, userAnswers) mustBe
        routes.ReasonableCareOnshoreController.onPageLoad(NormalMode)
    }

    "must go from ReasonableExcuseOnshorePage to ReasonableExcuseForNotFilingOnshoreController when DidNotFile and page2b has ReasonableExcuse" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(
                 WhyAreYouMakingThisOnshoreDisclosurePage,
                 Set[WhyAreYouMakingThisOnshoreDisclosure](WhyAreYouMakingThisOnshoreDisclosure.DidNotFile)
               )
        ua2 <- ua.set(
                 WhyDidYouNotFileAReturnOnTimeOnshorePage,
                 Set[WhyDidYouNotFileAReturnOnTimeOnshore](WhyDidYouNotFileAReturnOnTimeOnshore.ReasonableExcuse)
               )
      } yield ua2).success.value
      navigator.nextPage(ReasonableExcuseOnshorePage, NormalMode, userAnswers) mustBe
        routes.ReasonableExcuseForNotFilingOnshoreController.onPageLoad(NormalMode)
    }

    "must go from ReasonableExcuseOnshorePage to ReasonableCareOnshoreController when DidNotFile, no page2b ReasonableExcuse, InaccurateReturn with ReasonableMistake" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(
                 WhyAreYouMakingThisOnshoreDisclosurePage,
                 Set[WhyAreYouMakingThisOnshoreDisclosure](
                   WhyAreYouMakingThisOnshoreDisclosure.DidNotFile,
                   WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturn
                 )
               )
        ua2 <- ua.set(
                 WhyDidYouNotFileAReturnOnTimeOnshorePage,
                 Set[WhyDidYouNotFileAReturnOnTimeOnshore](
                   WhyDidYouNotFileAReturnOnTimeOnshore.DidNotWithholdInformationOnPurpose
                 )
               )
        ua3 <- ua2.set(
                 WhyYouSubmittedAnInaccurateOnshoreReturnPage,
                 Set[WhyYouSubmittedAnInaccurateOnshoreReturn](
                   WhyYouSubmittedAnInaccurateOnshoreReturn.ReasonableMistake
                 )
               )
      } yield ua3).success.value
      navigator.nextPage(ReasonableExcuseOnshorePage, NormalMode, userAnswers) mustBe
        routes.ReasonableCareOnshoreController.onPageLoad(NormalMode)
    }

    "must go from ReasonableExcuseOnshorePage to WhatOnshoreLiabilitiesDoYouNeedToDiscloseController when DidNotFile, no ReasonableExcuse, InaccurateReturn but no ReasonableMistake" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(
                 WhyAreYouMakingThisOnshoreDisclosurePage,
                 Set[WhyAreYouMakingThisOnshoreDisclosure](
                   WhyAreYouMakingThisOnshoreDisclosure.DidNotFile,
                   WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturn
                 )
               )
        ua2 <- ua.set(
                 WhyDidYouNotFileAReturnOnTimeOnshorePage,
                 Set[WhyDidYouNotFileAReturnOnTimeOnshore](
                   WhyDidYouNotFileAReturnOnTimeOnshore.DidNotWithholdInformationOnPurpose
                 )
               )
        ua3 <-
          ua2.set(
            WhyYouSubmittedAnInaccurateOnshoreReturnPage,
            Set[WhyYouSubmittedAnInaccurateOnshoreReturn](WhyYouSubmittedAnInaccurateOnshoreReturn.NoReasonableCare)
          )
      } yield ua3).success.value
      navigator.nextPage(ReasonableExcuseOnshorePage, NormalMode, userAnswers) mustBe
        routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
    }

    "must go from ReasonableExcuseOnshorePage to ReasonableCareOnshoreController when no DidNotFile but InaccurateReturn with ReasonableMistake" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(
                 WhyAreYouMakingThisOnshoreDisclosurePage,
                 Set[WhyAreYouMakingThisOnshoreDisclosure](WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturn)
               )
        ua2 <- ua.set(
                 WhyYouSubmittedAnInaccurateOnshoreReturnPage,
                 Set[WhyYouSubmittedAnInaccurateOnshoreReturn](
                   WhyYouSubmittedAnInaccurateOnshoreReturn.ReasonableMistake
                 )
               )
      } yield ua2).success.value
      navigator.nextPage(ReasonableExcuseOnshorePage, NormalMode, userAnswers) mustBe
        routes.ReasonableCareOnshoreController.onPageLoad(NormalMode)
    }

    "must go from ReasonableExcuseOnshorePage to WhatOnshoreLiabilitiesDoYouNeedToDiscloseController when InaccurateReturn but no ReasonableMistake" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(
                 WhyAreYouMakingThisOnshoreDisclosurePage,
                 Set[WhyAreYouMakingThisOnshoreDisclosure](WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturn)
               )
        ua2 <-
          ua.set(
            WhyYouSubmittedAnInaccurateOnshoreReturnPage,
            Set[WhyYouSubmittedAnInaccurateOnshoreReturn](WhyYouSubmittedAnInaccurateOnshoreReturn.NoReasonableCare)
          )
      } yield ua2).success.value
      navigator.nextPage(ReasonableExcuseOnshorePage, NormalMode, userAnswers) mustBe
        routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
    }

    // ReasonableExcuseForNotFilingOnshorePage routing
    "must go from ReasonableExcuseForNotFilingOnshorePage to ReasonableCareOnshoreController when InaccurateReturn and ReasonableMistake" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(
                 WhyAreYouMakingThisOnshoreDisclosurePage,
                 Set[WhyAreYouMakingThisOnshoreDisclosure](WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturn)
               )
        ua2 <- ua.set(
                 WhyYouSubmittedAnInaccurateOnshoreReturnPage,
                 Set[WhyYouSubmittedAnInaccurateOnshoreReturn](
                   WhyYouSubmittedAnInaccurateOnshoreReturn.ReasonableMistake
                 )
               )
      } yield ua2).success.value
      navigator.nextPage(ReasonableExcuseForNotFilingOnshorePage, NormalMode, userAnswers) mustBe
        routes.ReasonableCareOnshoreController.onPageLoad(NormalMode)
    }

    "must go from ReasonableExcuseForNotFilingOnshorePage to WhatOnshoreLiabilitiesDoYouNeedToDiscloseController when InaccurateReturn but no ReasonableMistake" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(
                 WhyAreYouMakingThisOnshoreDisclosurePage,
                 Set[WhyAreYouMakingThisOnshoreDisclosure](WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturn)
               )
        ua2 <-
          ua.set(
            WhyYouSubmittedAnInaccurateOnshoreReturnPage,
            Set[WhyYouSubmittedAnInaccurateOnshoreReturn](WhyYouSubmittedAnInaccurateOnshoreReturn.NoReasonableCare)
          )
      } yield ua2).success.value
      navigator.nextPage(ReasonableExcuseForNotFilingOnshorePage, NormalMode, userAnswers) mustBe
        routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
    }

    "must go from WhyAreYouMakingThisOnshoreDisclosurePage in CheckMode to normal route when answer changed" in {
      val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHMRC)
      val userAnswers                                    =
        UserAnswers("id", "session-123").set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
      navigator.nextPage(
        WhyAreYouMakingThisOnshoreDisclosurePage,
        CheckMode,
        userAnswers,
        hasAnswerChanged = true
      ) mustBe
        routes.WhyDidYouNotNotifyOnshoreController.onPageLoad(NormalMode)
    }

    "must go from WhyAreYouMakingThisOnshoreDisclosurePage in CheckMode to CYA when answer not changed" in {
      val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHMRC)
      val userAnswers                                    =
        UserAnswers("id", "session-123").set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
      navigator.nextPage(
        WhyAreYouMakingThisOnshoreDisclosurePage,
        CheckMode,
        userAnswers,
        hasAnswerChanged = false
      ) mustBe
        routes.CheckYourAnswersController.onPageLoad
    }

    "must go from WhatOnshoreLiabilitiesDoYouNeedToDisclosePage in CheckMode to normal route when answer changed" in {
      val set: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] = Set(CorporationTax)
      val userAnswers                                         =
        UserAnswers("id", "session-123").set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, set).success.value
      navigator.nextPage(
        WhatOnshoreLiabilitiesDoYouNeedToDisclosePage,
        CheckMode,
        userAnswers,
        hasAnswerChanged = true
      ) mustBe
        routes.CorporationTaxLiabilityController.onPageLoad(0, NormalMode)
    }

    "must go from WhatOnshoreLiabilitiesDoYouNeedToDisclosePage in CheckMode to CYA when answer not changed" in {
      val set: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] = Set(CorporationTax)
      val userAnswers                                         =
        UserAnswers("id", "session-123").set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, set).success.value
      navigator.nextPage(
        WhatOnshoreLiabilitiesDoYouNeedToDisclosePage,
        CheckMode,
        userAnswers,
        hasAnswerChanged = false
      ) mustBe
        routes.CheckYourAnswersController.onPageLoad
    }

    "must go from AccountingPeriodCTAddedPage in CheckMode to next page when true" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(
                 WhatOnshoreLiabilitiesDoYouNeedToDisclosePage,
                 Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose](CorporationTax)
               )
        ua2 <- ua.set(CorporationTaxLiabilityPage, Seq())
        ua3 <- ua2.set(AccountingPeriodCTAddedPage, true)
      } yield ua3).success.value
      navigator.nextPage(AccountingPeriodCTAddedPage, CheckMode, userAnswers) mustBe
        routes.CorporationTaxLiabilityController.onPageLoad(0, NormalMode)
    }

    "must go from AccountingPeriodCTAddedPage in CheckMode to CYA when false" in {
      val userAnswers = UserAnswers("id", "session-123").set(AccountingPeriodCTAddedPage, false).success.value
      navigator.nextPage(AccountingPeriodCTAddedPage, CheckMode, userAnswers) mustBe
        routes.CheckYourAnswersController.onPageLoad
    }

    "must go from AccountingPeriodDLAddedPage in CheckMode to next page when true" in {
      val directorLoan = DirectorLoanAccountLiabilities(
        name = "Test",
        periodEnd = java.time.LocalDate.now(),
        overdrawn = BigInt(0),
        unpaidTax = BigInt(0),
        interest = BigInt(0),
        penaltyRate = 0,
        penaltyRateReason = "reason"
      )
      val userAnswers  = (for {
        ua  <- UserAnswers("id", "session-123").set(DirectorLoanAccountLiabilitiesPage, Seq(directorLoan))
        ua2 <- ua.set(AccountingPeriodDLAddedPage, true)
      } yield ua2).success.value
      navigator.nextPage(AccountingPeriodDLAddedPage, CheckMode, userAnswers) mustBe
        routes.DirectorLoanAccountLiabilitiesController.onPageLoad(1, NormalMode)
    }

    "must go from AccountingPeriodDLAddedPage in CheckMode to CYA when false" in {
      val userAnswers = UserAnswers("id", "session-123").set(AccountingPeriodDLAddedPage, false).success.value
      navigator.nextPage(AccountingPeriodDLAddedPage, CheckMode, userAnswers) mustBe
        routes.CheckYourAnswersController.onPageLoad
    }

    "must go from NotIncludedSingleTaxYearPage to NotIncludedSingleTaxYearController when no years set" in {
      navigator.nextPage(NotIncludedSingleTaxYearPage, NormalMode, UserAnswers("id", "session-123")) mustBe
        routes.NotIncludedSingleTaxYearController.onPageLoad(NormalMode)
    }

    "must go from NotIncludedMultipleTaxYearsPage to NotIncludedMultipleTaxYearsController when no years set" in {
      navigator.nextPage(NotIncludedMultipleTaxYearsPage, NormalMode, UserAnswers("id", "session-123")) mustBe
        routes.NotIncludedMultipleTaxYearsController.onPageLoad(NormalMode)
    }
  }

  private def getFullDisclosure(onshoreCompleted: Boolean = false): FullDisclosure = {
    val whatLiabilities: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose] =
      if (onshoreCompleted) Set() else Set(CorporationTax)

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

    val onshoreLiabilities = OnshoreLiabilities(
      behaviour = Some(Set()),
      whatLiabilities = Some(whatLiabilities),
      whichYears = Some(Set(OnshoreYearStarting(2012))),
      taxYearLiabilities = Some(Map("2012" -> OnshoreTaxYearWithLiabilities(OnshoreYearStarting(2012), liabilities)))
    )

    FullDisclosure(
      submissionId = "",
      lastUpdated = Instant.now(),
      metadata = Metadata(),
      caseReference = CaseReference(),
      personalDetails = PersonalDetails(Background(), AboutYou()),
      onshoreLiabilities = Some(onshoreLiabilities),
      offshoreLiabilities = OffshoreLiabilities(),
      otherLiabilities = OtherLiabilities(),
      reasonForDisclosingNow = ReasonForDisclosingNow(),
      userId = ""
    )
  }
}
