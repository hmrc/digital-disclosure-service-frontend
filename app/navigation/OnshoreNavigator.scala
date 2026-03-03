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

import com.google.inject.ImplementedBy

import javax.inject.{Inject, Singleton}
import play.api.mvc.Call
import controllers.onshore.routes
import models.WhatOnshoreLiabilitiesDoYouNeedToDisclose.{CorporationTax, DirectorLoan, LettingIncome}
import pages.{WhyDidYouNotNotifyOnshorePage, WhyYouSubmittedAnInaccurateOnshoreReturnPage, _}
import models._
import models.WhyAreYouMakingThisOnshoreDisclosure._
import models.WhyDidYouNotFileAReturnOnTimeOnshore.{DeliberatelyWithheldInformation, ReasonableExcuse}
import models.WhyDidYouNotNotifyOnshore.DeliberatelyDidNotNotifyOnshore
import models.WhyYouSubmittedAnInaccurateOnshoreReturn.{DeliberatelyInaccurate, ReasonableMistake}
import pages.onshore.WhyDidYouNotFileAReturnOnTimeOnshorePage
import pages.RelatesToPage
import services.UAToDisclosureService

@Singleton
class OnshoreNavigatorImpl @Inject() (uaToDisclosure: UAToDisclosureService) extends OnshoreNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {

    case WhyAreYouMakingThisOnshoreDisclosurePage =>
      ua =>
        val page1Selections = ua.get(WhyAreYouMakingThisOnshoreDisclosurePage).getOrElse(Set.empty)

        if (page1Selections.contains(DidNotNotifyHMRC)) {
          routes.WhyDidYouNotNotifyOnshoreController.onPageLoad(NormalMode)
        } else if (page1Selections.contains(DidNotFile)) {
          routes.WhyDidYouNotFileAReturnOnTimeOnshoreController.onPageLoad(NormalMode)
        } else if (page1Selections.contains(InaccurateReturn)) {
          routes.WhyYouSubmittedAnInaccurateOnshoreReturnController.onPageLoad(NormalMode)
        } else {
          routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
        }

    case WhyDidYouNotNotifyOnshorePage =>
      ua =>
        val page1Selections = ua.get(WhyAreYouMakingThisOnshoreDisclosurePage).getOrElse(Set.empty)

        if (page1Selections.contains(DidNotFile)) {
          routes.WhyDidYouNotFileAReturnOnTimeOnshoreController.onPageLoad(NormalMode)
        } else if (page1Selections.contains(InaccurateReturn)) {
          routes.WhyYouSubmittedAnInaccurateOnshoreReturnController.onPageLoad(NormalMode)
        } else if (hasAnyDeliberate(ua)) {
          routes.CDFOnshoreController.onPageLoad(NormalMode)
        } else {
          val page2aSelections = ua.get(WhyDidYouNotNotifyOnshorePage).getOrElse(Set.empty)
          if (page2aSelections.contains(WhyDidYouNotNotifyOnshore.ReasonableExcuseOnshore)) {
            routes.ReasonableExcuseOnshoreController.onPageLoad(NormalMode)
          } else {
            routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
          }
        }

    case WhyDidYouNotFileAReturnOnTimeOnshorePage =>
      ua =>
        val page1Selections = ua.get(WhyAreYouMakingThisOnshoreDisclosurePage).getOrElse(Set.empty)

        if (page1Selections.contains(InaccurateReturn)) {
          routes.WhyYouSubmittedAnInaccurateOnshoreReturnController.onPageLoad(NormalMode)
        } else if (hasAnyDeliberate(ua)) {
          routes.CDFOnshoreController.onPageLoad(NormalMode)
        } else if (page1Selections.contains(DidNotNotifyHMRC)) {
          val page2aSelections = ua.get(WhyDidYouNotNotifyOnshorePage).getOrElse(Set.empty)
          if (page2aSelections.contains(WhyDidYouNotNotifyOnshore.ReasonableExcuseOnshore)) {
            routes.ReasonableExcuseOnshoreController.onPageLoad(NormalMode)
          } else {
            val page2bSelections = ua.get(WhyDidYouNotFileAReturnOnTimeOnshorePage).getOrElse(Set.empty)
            if (page2bSelections.contains(ReasonableExcuse)) {
              routes.ReasonableExcuseForNotFilingOnshoreController.onPageLoad(NormalMode)
            } else {
              routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
            }
          }
        } else {
          val page2bSelections = ua.get(WhyDidYouNotFileAReturnOnTimeOnshorePage).getOrElse(Set.empty)
          if (page2bSelections.contains(ReasonableExcuse)) {
            routes.ReasonableExcuseForNotFilingOnshoreController.onPageLoad(NormalMode)
          } else {
            routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
          }
        }

    case WhyYouSubmittedAnInaccurateOnshoreReturnPage =>
      ua =>
        val page1Selections  = ua.get(WhyAreYouMakingThisOnshoreDisclosurePage).getOrElse(Set.empty)
        val page2cSelections = ua.get(WhyYouSubmittedAnInaccurateOnshoreReturnPage).getOrElse(Set.empty)

        if (hasAnyDeliberate(ua)) {
          routes.CDFOnshoreController.onPageLoad(NormalMode)
        } else if (page1Selections.contains(DidNotNotifyHMRC)) {
          val page2aSelections = ua.get(WhyDidYouNotNotifyOnshorePage).getOrElse(Set.empty)
          if (page2aSelections.contains(WhyDidYouNotNotifyOnshore.ReasonableExcuseOnshore)) {
            routes.ReasonableExcuseOnshoreController.onPageLoad(NormalMode)
          } else if (page1Selections.contains(DidNotFile)) {
            val page2bSelections = ua.get(WhyDidYouNotFileAReturnOnTimeOnshorePage).getOrElse(Set.empty)
            if (page2bSelections.contains(ReasonableExcuse)) {
              routes.ReasonableExcuseForNotFilingOnshoreController.onPageLoad(NormalMode)
            } else if (page2cSelections.contains(ReasonableMistake)) {
              routes.ReasonableCareOnshoreController.onPageLoad(NormalMode)
            } else {
              routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
            }
          } else if (page2cSelections.contains(ReasonableMistake)) {
            routes.ReasonableCareOnshoreController.onPageLoad(NormalMode)
          } else {
            routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
          }
        } else if (page1Selections.contains(DidNotFile)) {
          val page2bSelections = ua.get(WhyDidYouNotFileAReturnOnTimeOnshorePage).getOrElse(Set.empty)
          if (page2bSelections.contains(ReasonableExcuse)) {
            routes.ReasonableExcuseForNotFilingOnshoreController.onPageLoad(NormalMode)
          } else if (page2cSelections.contains(ReasonableMistake)) {
            routes.ReasonableCareOnshoreController.onPageLoad(NormalMode)
          } else {
            routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
          }
        } else if (page2cSelections.contains(ReasonableMistake)) {
          routes.ReasonableCareOnshoreController.onPageLoad(NormalMode)
        } else {
          routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
        }

    case CDFOnshorePage =>
      ua =>
        val page1Selections = ua.get(WhyAreYouMakingThisOnshoreDisclosurePage).getOrElse(Set.empty)

        ua.get(CDFOnshorePage) match {
          case Some(false) => routes.YouHaveLeftTheDDSOnshoreController.onPageLoad(NormalMode)
          case Some(true)  =>
            if (page1Selections.contains(DidNotNotifyHMRC)) {
              val page2aSelections = ua.get(WhyDidYouNotNotifyOnshorePage).getOrElse(Set.empty)
              if (page2aSelections.contains(WhyDidYouNotNotifyOnshore.ReasonableExcuseOnshore)) {
                routes.ReasonableExcuseOnshoreController.onPageLoad(NormalMode)
              } else if (page1Selections.contains(DidNotFile)) {
                val page2bSelections = ua.get(WhyDidYouNotFileAReturnOnTimeOnshorePage).getOrElse(Set.empty)
                if (page2bSelections.contains(ReasonableExcuse)) {
                  routes.ReasonableExcuseForNotFilingOnshoreController.onPageLoad(NormalMode)
                } else if (page1Selections.contains(InaccurateReturn)) {
                  val page2cSelections = ua.get(WhyYouSubmittedAnInaccurateOnshoreReturnPage).getOrElse(Set.empty)
                  if (page2cSelections.contains(ReasonableMistake)) {
                    routes.ReasonableCareOnshoreController.onPageLoad(NormalMode)
                  } else {
                    routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
                  }
                } else {
                  routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
                }
              } else if (page1Selections.contains(InaccurateReturn)) {
                val page2cSelections = ua.get(WhyYouSubmittedAnInaccurateOnshoreReturnPage).getOrElse(Set.empty)
                if (page2cSelections.contains(ReasonableMistake)) {
                  routes.ReasonableCareOnshoreController.onPageLoad(NormalMode)
                } else {
                  routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
                }
              } else {
                routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
              }
            } else if (page1Selections.contains(DidNotFile)) {
              val page2bSelections = ua.get(WhyDidYouNotFileAReturnOnTimeOnshorePage).getOrElse(Set.empty)
              if (page2bSelections.contains(ReasonableExcuse)) {
                routes.ReasonableExcuseForNotFilingOnshoreController.onPageLoad(NormalMode)
              } else if (page1Selections.contains(InaccurateReturn)) {
                val page2cSelections = ua.get(WhyYouSubmittedAnInaccurateOnshoreReturnPage).getOrElse(Set.empty)
                if (page2cSelections.contains(ReasonableMistake)) {
                  routes.ReasonableCareOnshoreController.onPageLoad(NormalMode)
                } else {
                  routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
                }
              } else {
                routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
              }
            } else if (page1Selections.contains(InaccurateReturn)) {
              val page2cSelections = ua.get(WhyYouSubmittedAnInaccurateOnshoreReturnPage).getOrElse(Set.empty)
              if (page2cSelections.contains(ReasonableMistake)) {
                routes.ReasonableCareOnshoreController.onPageLoad(NormalMode)
              } else {
                routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
              }
            } else {
              routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
            }
          case _           => routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
        }

    case ReasonableExcuseOnshorePage =>
      ua =>
        val page1Selections = ua.get(WhyAreYouMakingThisOnshoreDisclosurePage).getOrElse(Set.empty)

        if (page1Selections.contains(DidNotFile)) {
          val page2bSelections = ua.get(WhyDidYouNotFileAReturnOnTimeOnshorePage).getOrElse(Set.empty)
          if (page2bSelections.contains(ReasonableExcuse)) {
            routes.ReasonableExcuseForNotFilingOnshoreController.onPageLoad(NormalMode)
          } else if (page1Selections.contains(InaccurateReturn)) {
            val page2cSelections = ua.get(WhyYouSubmittedAnInaccurateOnshoreReturnPage).getOrElse(Set.empty)
            if (page2cSelections.contains(ReasonableMistake)) {
              routes.ReasonableCareOnshoreController.onPageLoad(NormalMode)
            } else {
              routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
            }
          } else {
            routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
          }
        } else if (page1Selections.contains(InaccurateReturn)) {
          val page2cSelections = ua.get(WhyYouSubmittedAnInaccurateOnshoreReturnPage).getOrElse(Set.empty)
          if (page2cSelections.contains(ReasonableMistake)) {
            routes.ReasonableCareOnshoreController.onPageLoad(NormalMode)
          } else {
            routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
          }
        } else {
          routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
        }

    case ReasonableCareOnshorePage =>
      _ => routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)

    case ReasonableExcuseForNotFilingOnshorePage =>
      ua =>
        val page1Selections = ua.get(WhyAreYouMakingThisOnshoreDisclosurePage).getOrElse(Set.empty)

        if (page1Selections.contains(InaccurateReturn)) {
          val page2cSelections = ua.get(WhyYouSubmittedAnInaccurateOnshoreReturnPage).getOrElse(Set.empty)
          if (page2cSelections.contains(ReasonableMistake)) {
            routes.ReasonableCareOnshoreController.onPageLoad(NormalMode)
          } else {
            routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
          }
        } else {
          routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode)
        }

    case WhatOnshoreLiabilitiesDoYouNeedToDisclosePage =>
      ua =>
        ua.get(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage) match {
          case Some(taxTypes) if taxTypes.contains(CorporationTax) =>
            routes.CorporationTaxLiabilityController.onPageLoad(0, NormalMode)
          case Some(taxTypes) if taxTypes.contains(DirectorLoan)   =>
            routes.DirectorLoanAccountLiabilitiesController.onPageLoad(0, NormalMode)
          case _                                                   => routes.WhichOnshoreYearsController.onPageLoad(NormalMode)
        }

    case WhichOnshoreYearsPage =>
      ua => {
        val missingYearsCount =
          ua.inverselySortedOnshoreTaxYears.map(ty => OnshoreYearStarting.findMissingYears(ty.toList).size).getOrElse(0)
        val lettingsChosen    =
          ua.get(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage).getOrElse(Set()).contains(LettingIncome)
        (ua.get(WhichOnshoreYearsPage), missingYearsCount) match {
          case (Some(_), 1)                                             => routes.NotIncludedSingleTaxYearController.onPageLoad(NormalMode)
          case (Some(_), count) if count > 1                            => routes.NotIncludedMultipleTaxYearsController.onPageLoad(NormalMode)
          case (Some(years), _) if years.contains(PriorToThreeYears)    =>
            routes.TaxBeforeThreeYearsOnshoreController.onPageLoad(NormalMode)
          case (Some(years), _) if years.contains(PriorToFiveYears)     =>
            routes.TaxBeforeFiveYearsOnshoreController.onPageLoad(NormalMode)
          case (Some(years), _) if years.contains(PriorToNineteenYears) =>
            routes.TaxBeforeNineteenYearsOnshoreController.onPageLoad(NormalMode)
          case (_, _) if lettingsChosen                                 => routes.PropertyAddedController.onPageLoad(NormalMode)
          case (_, _)                                                   => routes.OnshoreTaxYearLiabilitiesController.onPageLoad(0, NormalMode)
        }
      }

    case NotIncludedSingleTaxYearPage =>
      ua => {
        val lettingsChosen =
          ua.get(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage).getOrElse(Set()).contains(LettingIncome)
        ua.get(WhichOnshoreYearsPage) match {
          case Some(years) if years.contains(PriorToThreeYears)    =>
            routes.TaxBeforeThreeYearsOnshoreController.onPageLoad(NormalMode)
          case Some(years) if years.contains(PriorToFiveYears)     =>
            routes.TaxBeforeFiveYearsOnshoreController.onPageLoad(NormalMode)
          case Some(years) if years.contains(PriorToNineteenYears) =>
            routes.TaxBeforeNineteenYearsOnshoreController.onPageLoad(NormalMode)
          case Some(years) if lettingsChosen                       => routes.PropertyAddedController.onPageLoad(NormalMode)
          case Some(years)                                         => routes.OnshoreTaxYearLiabilitiesController.onPageLoad(0, NormalMode)
          case _                                                   => routes.NotIncludedSingleTaxYearController.onPageLoad(NormalMode)
        }
      }

    case NotIncludedMultipleTaxYearsPage =>
      ua => {
        val lettingsChosen =
          ua.get(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage).getOrElse(Set()).contains(LettingIncome)
        ua.get(WhichOnshoreYearsPage) match {
          case Some(years) if years.contains(PriorToThreeYears)    =>
            routes.TaxBeforeThreeYearsOnshoreController.onPageLoad(NormalMode)
          case Some(years) if years.contains(PriorToFiveYears)     =>
            routes.TaxBeforeFiveYearsOnshoreController.onPageLoad(NormalMode)
          case Some(years) if years.contains(PriorToNineteenYears) =>
            routes.TaxBeforeNineteenYearsOnshoreController.onPageLoad(NormalMode)
          case Some(years) if lettingsChosen                       => routes.PropertyAddedController.onPageLoad(NormalMode)
          case Some(years)                                         => routes.OnshoreTaxYearLiabilitiesController.onPageLoad(0, NormalMode)
          case _                                                   => routes.NotIncludedMultipleTaxYearsController.onPageLoad(NormalMode)
        }
      }

    case TaxBeforeThreeYearsOnshorePage =>
      ua => {
        val lettingsChosen              =
          ua.get(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage).getOrElse(Set()).contains(LettingIncome)
        val offshoreAnswer              = ua.get(OffshoreLiabilitiesPage).getOrElse(true)
        val onshoreAnswer               = ua.get(OnshoreLiabilitiesPage).getOrElse(true)
        val properties                  = ua.get(LettingPropertyPage)
        val offshoreOnshoreBothSelected = offshoreAnswer && onshoreAnswer

        ua.get(WhichOnshoreYearsPage) match {
          case Some(years) if years.contains(PriorToThreeYears) && years.size == 1 && offshoreOnshoreBothSelected =>
            routes.YouHaveNoOnshoreLiabilitiesToDiscloseController.onPageLoad
          case Some(years) if years.contains(PriorToThreeYears) && years.size == 1                                =>
            routes.MakingNilDisclosureController.onPageLoad
          case Some(years) if lettingsChosen && properties.isEmpty                                                =>
            routes.PropertyAddedController.onPageLoad(NormalMode)
          case _                                                                                                  => routes.OnshoreTaxYearLiabilitiesController.onPageLoad(0, NormalMode)
        }
      }

    case TaxBeforeFiveYearsOnshorePage =>
      ua => {
        val lettingsChosen              =
          ua.get(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage).getOrElse(Set()).contains(LettingIncome)
        val offshoreAnswer              = ua.get(OffshoreLiabilitiesPage).getOrElse(true)
        val onshoreAnswer               = ua.get(OnshoreLiabilitiesPage).getOrElse(true)
        val properties                  = ua.get(LettingPropertyPage)
        val offshoreOnshoreBothSelected = offshoreAnswer && onshoreAnswer

        ua.get(WhichOnshoreYearsPage) match {
          case Some(years) if years.contains(PriorToFiveYears) && years.size == 1 && offshoreOnshoreBothSelected =>
            routes.YouHaveNoOnshoreLiabilitiesToDiscloseController.onPageLoad
          case Some(years) if years.contains(PriorToFiveYears) && years.size == 1                                =>
            routes.MakingNilDisclosureController.onPageLoad
          case Some(years) if lettingsChosen && properties.isEmpty                                               =>
            routes.PropertyAddedController.onPageLoad(NormalMode)
          case _                                                                                                 => routes.OnshoreTaxYearLiabilitiesController.onPageLoad(0, NormalMode)
        }
      }

    case TaxBeforeNineteenYearsOnshorePage =>
      ua => {
        val lettingsChosen              =
          ua.get(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage).getOrElse(Set()).contains(LettingIncome)
        val offshoreAnswer              = ua.get(OffshoreLiabilitiesPage).getOrElse(true)
        val onshoreAnswer               = ua.get(OnshoreLiabilitiesPage).getOrElse(true)
        val properties                  = ua.get(LettingPropertyPage)
        val offshoreOnshoreBothSelected = offshoreAnswer && onshoreAnswer

        ua.get(WhichOnshoreYearsPage) match {
          case Some(years) if years.contains(PriorToNineteenYears) && years.size == 1 && offshoreOnshoreBothSelected =>
            routes.YouHaveNoOnshoreLiabilitiesToDiscloseController.onPageLoad
          case Some(years) if years.contains(PriorToNineteenYears) && years.size == 1                                =>
            routes.MakingNilDisclosureController.onPageLoad
          case Some(years) if lettingsChosen && properties.isEmpty                                                   =>
            routes.PropertyAddedController.onPageLoad(NormalMode)
          case _                                                                                                     => routes.OnshoreTaxYearLiabilitiesController.onPageLoad(0, NormalMode)
        }
      }

    case AreYouAMemberOfAnyLandlordAssociationsPage =>
      ua =>
        ua.get(AreYouAMemberOfAnyLandlordAssociationsPage) match {
          case Some(true) => routes.WhichLandlordAssociationsAreYouAMemberOfController.onPageLoad(NormalMode)
          case _          => routes.HowManyPropertiesDoYouCurrentlyLetOutController.onPageLoad(NormalMode)
        }

    case WhichLandlordAssociationsAreYouAMemberOfPage =>
      _ => routes.HowManyPropertiesDoYouCurrentlyLetOutController.onPageLoad(NormalMode)

    case HowManyPropertiesDoYouCurrentlyLetOutPage =>
      _ => routes.OnshoreTaxYearLiabilitiesController.onPageLoad(0, NormalMode)

    case PropertyAddedPage =>
      ua => {
        val missingYears = ua.inverselySortedOnshoreTaxYears
          .getOrElse(Set())
          .map { year =>
            year.toString
          }
          .toSet

        val keySet               = ua.get(OnshoreTaxYearLiabilitiesPage).getOrElse(Map.empty).keySet
        val yearsStillToComplete = missingYears.intersect(keySet).size

        (
          ua.get(PropertyAddedPage),
          ua.get(LettingPropertyPage),
          uaToDisclosure.uaToFullDisclosure(ua).onshoreLiabilities,
          yearsStillToComplete
        ) match {
          case (Some(true), Some(properties), _, _)                                           =>
            controllers.letting.routes.RentalAddressLookupController.lookupAddress(properties.size, NormalMode)
          case (Some(false), _, Some(onshoreLiabilities), 0) if onshoreLiabilities.isComplete =>
            routes.CheckYourAnswersController.onPageLoad
          case (Some(false), _, Some(onshoreLiabilities), _) if onshoreLiabilities.isComplete =>
            routes.OnshoreTaxYearLiabilitiesController.onPageLoad(0, NormalMode)
          case _                                                                              => routes.AreYouAMemberOfAnyLandlordAssociationsController.onPageLoad(NormalMode)
        }
      }

    case AccountingPeriodCTAddedPage =>
      ua =>
        (
          ua.get(AccountingPeriodCTAddedPage),
          ua.get(CorporationTaxLiabilityPage),
          ua.get(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage),
          uaToDisclosure.uaToFullDisclosure(ua).onshoreLiabilities
        ) match {
          case (Some(true), Some(corporationTaxLiabilities), _, _)                            =>
            routes.CorporationTaxLiabilityController.onPageLoad(corporationTaxLiabilities.size, NormalMode)
          case (Some(false), _, Some(taxTypes), _) if taxTypes.contains(DirectorLoan)         =>
            routes.DirectorLoanAccountLiabilitiesController.onPageLoad(0, NormalMode)
          case (Some(false), _, Some(taxTypes), _) if requiresTaxYears(taxTypes)              =>
            routes.WhichOnshoreYearsController.onPageLoad(NormalMode)
          case (Some(false), _, _, Some(onshoreLiabilities)) if onshoreLiabilities.isComplete =>
            routes.CheckYourAnswersController.onPageLoad
          case _                                                                              => routes.CheckYourAnswersController.onPageLoad
        }

    case DirectorLoanAccountLiabilitiesPage =>
      ua => routes.DirectorLoanAccountLiabilitiesSummaryController.onPageLoad(NormalMode)

    case CorporationTaxLiabilityPage => ua => routes.CorporationTaxSummaryController.onPageLoad(NormalMode)

    case AccountingPeriodDLAddedPage =>
      ua =>
        (
          ua.get(AccountingPeriodDLAddedPage),
          ua.get(DirectorLoanAccountLiabilitiesPage),
          ua.get(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage),
          uaToDisclosure.uaToFullDisclosure(ua).onshoreLiabilities
        ) match {
          case (Some(true), Some(directorLoanAccountLiabilities), _, _)                       =>
            routes.DirectorLoanAccountLiabilitiesController.onPageLoad(directorLoanAccountLiabilities.size, NormalMode)
          case (Some(false), _, Some(taxTypes), _) if requiresTaxYears(taxTypes)              =>
            routes.WhichOnshoreYearsController.onPageLoad(NormalMode)
          case (Some(false), _, _, Some(onshoreLiabilities)) if onshoreLiabilities.isComplete =>
            routes.CheckYourAnswersController.onPageLoad
          case _                                                                              => routes.CheckYourAnswersController.onPageLoad
        }

    case _ => _ => controllers.routes.TaskListController.onPageLoad
  }

  private val checkRouteMap: Page => UserAnswers => Boolean => Call = {

    case WhyAreYouMakingThisOnshoreDisclosurePage =>
      ua =>
        hasChanged =>
          if (hasChanged) normalRoutes(WhyAreYouMakingThisOnshoreDisclosurePage)(ua)
          else routes.CheckYourAnswersController.onPageLoad

    case WhatOnshoreLiabilitiesDoYouNeedToDisclosePage =>
      ua =>
        hasChanged =>
          if (hasChanged) normalRoutes(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage)(ua)
          else routes.CheckYourAnswersController.onPageLoad

    case DirectorLoanAccountLiabilitiesPage =>
      _ => _ => routes.DirectorLoanAccountLiabilitiesSummaryController.onPageLoad(NormalMode)

    case CorporationTaxLiabilityPage => _ => _ => routes.CorporationTaxSummaryController.onPageLoad(NormalMode)

    case IncomeOrGainSourcePage =>
      ua =>
        hasAnswerChanged =>
          if (hasAnswerChanged) nextPage(IncomeOrGainSourcePage, NormalMode, ua)
          else routes.CheckYourAnswersController.onPageLoad

    case WhichOnshoreYearsPage =>
      ua =>
        hasAnswerChanged => {
          val missingYearsCount = ua.inverselySortedOnshoreTaxYears
            .map(ty => OnshoreYearStarting.findMissingYears(ty.toList).size)
            .getOrElse(0)
          val lettingsChosen    =
            ua.get(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage).getOrElse(Set()).contains(LettingIncome)
          val properties        = ua.get(LettingPropertyPage)
          (ua.get(WhichOnshoreYearsPage), missingYearsCount, hasAnswerChanged) match {
            case (Some(_), 1, true)                                             => routes.NotIncludedSingleTaxYearController.onPageLoad(NormalMode)
            case (Some(_), count, true) if count > 1                            =>
              routes.NotIncludedMultipleTaxYearsController.onPageLoad(NormalMode)
            case (Some(years), _, true) if years.contains(PriorToThreeYears)    =>
              routes.TaxBeforeThreeYearsOnshoreController.onPageLoad(NormalMode)
            case (Some(years), _, true) if years.contains(PriorToFiveYears)     =>
              routes.TaxBeforeFiveYearsOnshoreController.onPageLoad(NormalMode)
            case (Some(years), _, true) if years.contains(PriorToNineteenYears) =>
              routes.TaxBeforeNineteenYearsOnshoreController.onPageLoad(NormalMode)
            case (_, _, true) if lettingsChosen && properties.isEmpty           =>
              routes.PropertyAddedController.onPageLoad(NormalMode)
            case (_, _, true)                                                   => routes.OnshoreTaxYearLiabilitiesController.onPageLoad(0, NormalMode)
            case (_, _, false)                                                  => routes.CheckYourAnswersController.onPageLoad
          }
        }

    case PropertyAddedPage =>
      ua =>
        _ =>
          (ua.get(PropertyAddedPage), uaToDisclosure.uaToFullDisclosure(ua).onshoreLiabilities) match {
            case (Some(true), _)                                                          => nextPage(PropertyAddedPage, NormalMode, ua)
            case (Some(false), Some(onshoreLiabilities)) if onshoreLiabilities.isComplete =>
              routes.CheckYourAnswersController.onPageLoad
            case _                                                                        => nextPage(PropertyAddedPage, NormalMode, ua)
          }

    case AccountingPeriodCTAddedPage =>
      ua =>
        _ =>
          ua.get(AccountingPeriodCTAddedPage) match {
            case Some(true) => nextPage(AccountingPeriodCTAddedPage, NormalMode, ua)
            case _          => routes.CheckYourAnswersController.onPageLoad
          }

    case AccountingPeriodDLAddedPage =>
      ua =>
        _ =>
          ua.get(AccountingPeriodDLAddedPage) match {
            case Some(true) => nextPage(AccountingPeriodDLAddedPage, NormalMode, ua)
            case _          => routes.CheckYourAnswersController.onPageLoad
          }

    case AreYouAMemberOfAnyLandlordAssociationsPage =>
      ua =>
        hasAnswerChanged =>
          ua.get(AreYouAMemberOfAnyLandlordAssociationsPage) match {
            case Some(true) if hasAnswerChanged =>
              routes.WhichLandlordAssociationsAreYouAMemberOfController.onPageLoad(CheckMode)
            case _                              => routes.CheckYourAnswersController.onPageLoad
          }

    case WhyDidYouNotNotifyOnshorePage =>
      ua =>
        hasChanged =>
          if (hasChanged) {
            val page2aSelections = ua.get(WhyDidYouNotNotifyOnshorePage).getOrElse(Set.empty)
            if (page2aSelections.contains(WhyDidYouNotNotifyOnshore.ReasonableExcuseOnshore)) {
              routes.ReasonableExcuseOnshoreController.onPageLoad(CheckMode)
            } else if (hasAnyDeliberate(ua)) {
              routes.CDFOnshoreController.onPageLoad(CheckMode)
            } else {
              routes.CheckYourAnswersController.onPageLoad
            }
          } else routes.CheckYourAnswersController.onPageLoad

    case WhyDidYouNotFileAReturnOnTimeOnshorePage =>
      ua =>
        hasChanged =>
          if (hasChanged) {
            val page2bSelections = ua.get(WhyDidYouNotFileAReturnOnTimeOnshorePage).getOrElse(Set.empty)
            if (page2bSelections.contains(ReasonableExcuse)) {
              routes.ReasonableExcuseForNotFilingOnshoreController.onPageLoad(CheckMode)
            } else if (hasAnyDeliberate(ua)) {
              routes.CDFOnshoreController.onPageLoad(CheckMode)
            } else {
              routes.CheckYourAnswersController.onPageLoad
            }
          } else routes.CheckYourAnswersController.onPageLoad

    case WhyYouSubmittedAnInaccurateOnshoreReturnPage =>
      ua =>
        hasChanged =>
          if (hasChanged) {
            val page2cSelections = ua.get(WhyYouSubmittedAnInaccurateOnshoreReturnPage).getOrElse(Set.empty)
            if (page2cSelections.contains(ReasonableMistake)) {
              routes.ReasonableCareOnshoreController.onPageLoad(CheckMode)
            } else if (hasAnyDeliberate(ua)) {
              routes.CDFOnshoreController.onPageLoad(CheckMode)
            } else {
              routes.CheckYourAnswersController.onPageLoad
            }
          } else routes.CheckYourAnswersController.onPageLoad

    case _ => _ => _ => routes.CheckYourAnswersController.onPageLoad
  }

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers, hasAnswerChanged: Boolean = true): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode  =>
      checkRouteMap(page)(userAnswers)(hasAnswerChanged)
  }

  def nextTaxYearLiabilitiesPage(
    currentIndex: Int,
    deduction: Boolean,
    mode: Mode,
    userAnswers: UserAnswers,
    hasAnswerChanged: Boolean = false
  ): Call =
    (mode, userAnswers.inverselySortedOnshoreTaxYears) match {
      case (NormalMode, _) if deduction                                 => routes.ResidentialReductionController.onPageLoad(currentIndex, mode)
      case (NormalMode, Some(years)) if (years.size - 1) > currentIndex =>
        routes.OnshoreTaxYearLiabilitiesController.onPageLoad(currentIndex + 1, NormalMode)
      case (CheckMode, _) if deduction && hasAnswerChanged              =>
        routes.ResidentialReductionController.onPageLoad(currentIndex, mode)
      case (_, _)                                                       => routes.CheckYourAnswersController.onPageLoad
    }

  private def requiresTaxYears(taxTypes: Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose]) =
    taxTypes.filterNot(_ == CorporationTax).filterNot(_ == DirectorLoan).size > 0

  private def hasAnyDeliberate(ua: UserAnswers): Boolean = {
    val entity = ua.get(RelatesToPage)

    if (entity.contains(RelatesTo.AnEstate)) {
      false
    } else {
      val page2aSelections = ua.get(WhyDidYouNotNotifyOnshorePage).getOrElse(Set.empty)
      val page2bSelections = ua.get(WhyDidYouNotFileAReturnOnTimeOnshorePage).getOrElse(Set.empty)
      val page2cSelections = ua.get(WhyYouSubmittedAnInaccurateOnshoreReturnPage).getOrElse(Set.empty)

      page2aSelections.contains(DeliberatelyDidNotNotifyOnshore) ||
      page2bSelections.contains(DeliberatelyWithheldInformation) ||
      page2cSelections.contains(DeliberatelyInaccurate)
    }
  }

}

@ImplementedBy(classOf[OnshoreNavigatorImpl])
trait OnshoreNavigator {
  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers, hasAnswerChanged: Boolean = true): Call
  def nextTaxYearLiabilitiesPage(
    currentIndex: Int,
    deduction: Boolean,
    mode: Mode,
    userAnswers: UserAnswers,
    hasAnswerChanged: Boolean = false
  ): Call
}
