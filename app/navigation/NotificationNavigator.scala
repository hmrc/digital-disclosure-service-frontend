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

import javax.inject.{Inject, Singleton}
import play.api.mvc.Call
import controllers.notification.routes
import pages._
import models._

@Singleton
class NotificationNavigator @Inject() () {

  private val normalRoutes: Page => UserAnswers => Call = {

    case ReceivedALetterPage =>
      ua =>
        ua.get(ReceivedALetterPage) match {
          case Some(true)  => routes.LetterReferenceController.onPageLoad(NormalMode)
          case Some(false) => routes.RelatesToController.onPageLoad(NormalMode)
          case None        => routes.ReceivedALetterController.onPageLoad(NormalMode)
        }

    case LetterReferencePage => _ => routes.RelatesToController.onPageLoad(NormalMode)

    case RelatesToPage => _ => routes.AreYouTheEntityController.onPageLoad(NormalMode)

    case AreYouTheEntityPage =>
      ua =>
        ua.get(AreYouTheEntityPage) match {
          case Some(AreYouTheEntity.IAmAnAccountantOrTaxAgent) =>
            routes.AreYouRepresentingAnOrganisationController.onPageLoad(NormalMode)
          case _                                               => routes.OffshoreLiabilitiesController.onPageLoad(NormalMode)
        }

    case OnshoreLiabilitiesPage => ua => routes.IncomeOrGainSourceController.onPageLoad(NormalMode)

    case OffshoreLiabilitiesPage =>
      ua =>
        ua.get(OffshoreLiabilitiesPage) match {
          case Some(true)  => routes.OnshoreLiabilitiesController.onPageLoad(NormalMode)
          case Some(false) => routes.OnlyOnshoreLiabilitiesController.onPageLoad(NormalMode)
          case None        => routes.OffshoreLiabilitiesController.onPageLoad(NormalMode)
        }

    case WhatIsYourFullNamePage => _ => routes.HowWouldYouPreferToBeContactedController.onPageLoad(NormalMode)

    case WhatIsYourDateOfBirthPage => _ => routes.WhatIsYourMainOccupationController.onPageLoad(NormalMode)

    case WhatIsYourMainOccupationPage => _ => routes.DoYouHaveNationalInsuranceNumberController.onPageLoad(NormalMode)

    case DoYouHaveNationalInsuranceNumberPage =>
      ua =>
        ua.get(DoYouHaveNationalInsuranceNumberPage) match {
          case Some(DoYouHaveNationalInsuranceNumber.YesIKnow)       =>
            routes.WhatIsYourNationalInsuranceNumberController.onPageLoad(NormalMode)
          case Some(DoYouHaveNationalInsuranceNumber.YesButDontKnow) =>
            routes.AreYouRegisteredForVATController.onPageLoad(NormalMode)
          case Some(DoYouHaveNationalInsuranceNumber.No)             =>
            routes.AreYouRegisteredForVATController.onPageLoad(NormalMode)
          case None                                                  => routes.DoYouHaveNationalInsuranceNumberController.onPageLoad(NormalMode)
        }

    case HowWouldYouPreferToBeContactedPage =>
      ua =>
        ua.get(HowWouldYouPreferToBeContactedPage) match {
          case Some(value) if value.contains(HowWouldYouPreferToBeContacted.Email) =>
            routes.YourEmailAddressController.onPageLoad(NormalMode)
          case Some(_)                                                             => routes.YourPhoneNumberController.onPageLoad(NormalMode)
          case _                                                                   => routes.HowWouldYouPreferToBeContactedController.onPageLoad(NormalMode)
        }

    case YourEmailAddressPage =>
      ua =>
        ua.get(HowWouldYouPreferToBeContactedPage) match {
          case Some(howWouldYouPreferToBeContacted)
              if howWouldYouPreferToBeContacted.contains(HowWouldYouPreferToBeContacted.Telephone) =>
            routes.YourPhoneNumberController.onPageLoad(NormalMode)
          case _ if ua.isTheUserTheIndividual => routes.WhatIsYourDateOfBirthController.onPageLoad(NormalMode)
          case _                              => routes.YourAddressLookupController.lookupAddress(NormalMode)
        }

    case YourPhoneNumberPage =>
      ua =>
        ua.isTheUserTheIndividual match {
          case true  => routes.WhatIsYourDateOfBirthController.onPageLoad(NormalMode)
          case false => routes.YourAddressLookupController.lookupAddress(NormalMode)
        }

    case WhatIsYourNationalInsuranceNumberPage => _ => routes.AreYouRegisteredForVATController.onPageLoad(NormalMode)

    case AreYouRegisteredForVATPage =>
      ua =>
        ua.get(AreYouRegisteredForVATPage) match {
          case Some(AreYouRegisteredForVAT.YesIKnow)       =>
            routes.WhatIsYourVATRegistrationNumberController.onPageLoad(NormalMode)
          case Some(AreYouRegisteredForVAT.YesButDontKnow) =>
            routes.AreYouRegisteredForSelfAssessmentController.onPageLoad(NormalMode)
          case Some(AreYouRegisteredForVAT.No)             =>
            routes.AreYouRegisteredForSelfAssessmentController.onPageLoad(NormalMode)
          case None                                        => routes.AreYouRegisteredForVATController.onPageLoad(NormalMode)
        }

    case WhatIsYourVATRegistrationNumberPage =>
      _ => routes.AreYouRegisteredForSelfAssessmentController.onPageLoad(NormalMode)

    case AreYouRegisteredForSelfAssessmentPage =>
      ua =>
        ua.get(AreYouRegisteredForSelfAssessmentPage) match {
          case Some(AreYouRegisteredForSelfAssessment.YesIKnowMyUTR)     =>
            routes.WhatIsYourUniqueTaxReferenceController.onPageLoad(NormalMode)
          case Some(AreYouRegisteredForSelfAssessment.YesIDontKnowMyUTR) =>
            routes.YourAddressLookupController.lookupAddress(NormalMode)
          case Some(AreYouRegisteredForSelfAssessment.No)                =>
            routes.YourAddressLookupController.lookupAddress(NormalMode)
          case None                                                      => routes.AreYouRegisteredForSelfAssessmentController.onPageLoad(NormalMode)
        }

    case WhatIsYourUniqueTaxReferencePage => _ => routes.YourAddressLookupController.lookupAddress(NormalMode)

    case YourAddressLookupPage => _ => routes.CheckYourAnswersController.onPageLoad

    case WhatIsTheIndividualsFullNamePage => _ => routes.WhatIsTheIndividualDateOfBirthController.onPageLoad(NormalMode)

    case WhatIsTheIndividualOccupationPage =>
      _ => routes.DoesTheIndividualHaveNationalInsuranceNumberController.onPageLoad(NormalMode)

    case DoesTheIndividualHaveNationalInsuranceNumberPage =>
      ua =>
        ua.get(DoesTheIndividualHaveNationalInsuranceNumberPage) match {
          case Some(DoesTheIndividualHaveNationalInsuranceNumber.YesIKnow)       =>
            routes.WhatIsIndividualsNationalInsuranceNumberController.onPageLoad(NormalMode)
          case Some(DoesTheIndividualHaveNationalInsuranceNumber.YesButDontKnow) =>
            routes.IsTheIndividualRegisteredForVATController.onPageLoad(NormalMode)
          case Some(DoesTheIndividualHaveNationalInsuranceNumber.No)             =>
            routes.IsTheIndividualRegisteredForVATController.onPageLoad(NormalMode)
          case None                                                              => routes.DoesTheIndividualHaveNationalInsuranceNumberController.onPageLoad(NormalMode)
        }

    case WhatIsIndividualsNationalInsuranceNumberPage =>
      _ => routes.IsTheIndividualRegisteredForVATController.onPageLoad(NormalMode)

    case WhatIsTheIndividualDateOfBirthPage =>
      _ => routes.WhatIsTheIndividualOccupationController.onPageLoad(NormalMode)

    case IsTheIndividualRegisteredForVATPage =>
      ua =>
        ua.get(IsTheIndividualRegisteredForVATPage) match {
          case Some(IsTheIndividualRegisteredForVAT.YesIKnow)       =>
            routes.WhatIsTheIndividualsVATRegistrationNumberController.onPageLoad(NormalMode)
          case Some(IsTheIndividualRegisteredForVAT.YesButDontKnow) =>
            routes.IsTheIndividualRegisteredForSelfAssessmentController.onPageLoad(NormalMode)
          case Some(IsTheIndividualRegisteredForVAT.No)             =>
            routes.IsTheIndividualRegisteredForSelfAssessmentController.onPageLoad(NormalMode)
          case None                                                 => routes.IsTheIndividualRegisteredForVATController.onPageLoad(NormalMode)
        }

    case WhatIsTheIndividualsVATRegistrationNumberPage =>
      _ => routes.IsTheIndividualRegisteredForSelfAssessmentController.onPageLoad(NormalMode)

    case WhatIsTheIndividualsUniqueTaxReferencePage =>
      _ => routes.IndividualAddressLookupController.lookupAddress(NormalMode)

    case IsTheIndividualRegisteredForSelfAssessmentPage =>
      ua =>
        ua.get(IsTheIndividualRegisteredForSelfAssessmentPage) match {
          case Some(IsTheIndividualRegisteredForSelfAssessment.YesIKnow)       =>
            routes.WhatIsTheIndividualsUniqueTaxReferenceController.onPageLoad(NormalMode)
          case Some(IsTheIndividualRegisteredForSelfAssessment.YesButDontKnow) =>
            routes.IndividualAddressLookupController.lookupAddress(NormalMode)
          case Some(IsTheIndividualRegisteredForSelfAssessment.No)             =>
            routes.IndividualAddressLookupController.lookupAddress(NormalMode)
          case None                                                            => routes.IsTheIndividualRegisteredForSelfAssessmentController.onPageLoad(NormalMode)
        }

    case IndividualAddressLookupPage => _ => routes.WhatIsYourFullNameController.onPageLoad(NormalMode)

    case CompanyAddressLookupPage => _ => routes.WhatIsYourFullNameController.onPageLoad(NormalMode)

    case AreYouRepresentingAnOrganisationPage =>
      ua =>
        ua.get(AreYouRepresentingAnOrganisationPage) match {
          case Some(true)  => routes.WhatIsTheNameOfTheOrganisationYouRepresentController.onPageLoad(NormalMode)
          case Some(false) => routes.OffshoreLiabilitiesController.onPageLoad(NormalMode)
          case None        => routes.AreYouRepresentingAnOrganisationController.onPageLoad(NormalMode)
        }

    case WhatIsTheNameOfTheOrganisationYouRepresentPage =>
      _ => routes.OffshoreLiabilitiesController.onPageLoad(NormalMode)

    case OnlyOnshoreLiabilitiesPage => ua => routes.IncomeOrGainSourceController.onPageLoad(NormalMode)

    case WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage =>
      _ => routes.WhatIsTheCompanyRegistrationNumberController.onPageLoad(NormalMode)

    case WhatIsTheCompanyRegistrationNumberPage => _ => routes.CompanyAddressLookupController.lookupAddress(NormalMode)

    case WhatIsTheLLPNamePage => _ => routes.LLPAddressLookupController.lookupAddress(NormalMode)

    case LLPAddressLookupPage => _ => routes.WhatIsYourFullNameController.onPageLoad(NormalMode)

    case WhatIsTheTrustNamePage => _ => routes.TrustAddressLookupController.lookupAddress(NormalMode)

    case TrustAddressLookupPage => _ => routes.WhatIsYourFullNameController.onPageLoad(NormalMode)

    case WhatWasTheNameOfThePersonWhoDiedPage =>
      _ => routes.WhatWasThePersonDateOfBirthController.onPageLoad(NormalMode)

    case WhatWasThePersonDateOfBirthPage => _ => routes.WhatWasThePersonOccupationController.onPageLoad(NormalMode)

    case WhatWasThePersonOccupationPage => _ => routes.DidThePersonHaveNINOController.onPageLoad(NormalMode)

    case DidThePersonHaveNINOPage =>
      ua =>
        ua.get(DidThePersonHaveNINOPage) match {
          case Some(DidThePersonHaveNINO.YesIKnow)       => routes.WhatWasThePersonNINOController.onPageLoad(NormalMode)
          case Some(DidThePersonHaveNINO.YesButDontKnow) =>
            routes.WasThePersonRegisteredForVATController.onPageLoad(NormalMode)
          case Some(DidThePersonHaveNINO.No)             => routes.WasThePersonRegisteredForVATController.onPageLoad(NormalMode)
          case None                                      => routes.DidThePersonHaveNINOController.onPageLoad(NormalMode)
        }

    case WhatWasThePersonNINOPage => _ => routes.WasThePersonRegisteredForVATController.onPageLoad(NormalMode)

    case WasThePersonRegisteredForVATPage =>
      ua =>
        ua.get(WasThePersonRegisteredForVATPage) match {
          case Some(WasThePersonRegisteredForVAT.YesIKnow)        =>
            routes.WhatWasThePersonVATRegistrationNumberController.onPageLoad(NormalMode)
          case Some(WasThePersonRegisteredForVAT.YesButIDontKnow) =>
            routes.WasThePersonRegisteredForSAController.onPageLoad(NormalMode)
          case Some(WasThePersonRegisteredForVAT.No)              =>
            routes.WasThePersonRegisteredForSAController.onPageLoad(NormalMode)
          case None                                               => routes.WasThePersonRegisteredForVATController.onPageLoad(NormalMode)
        }

    case WhatWasThePersonVATRegistrationNumberPage =>
      _ => routes.WasThePersonRegisteredForSAController.onPageLoad(NormalMode)

    case WasThePersonRegisteredForSAPage =>
      ua =>
        ua.get(WasThePersonRegisteredForSAPage) match {
          case Some(WasThePersonRegisteredForSA.YesIKnow)        => routes.WhatWasThePersonUTRController.onPageLoad(NormalMode)
          case Some(WasThePersonRegisteredForSA.YesButIDontKnow) =>
            routes.EstateAddressLookupController.lookupAddress(NormalMode)
          case Some(WasThePersonRegisteredForSA.No)              => routes.EstateAddressLookupController.lookupAddress(NormalMode)
          case None                                              => routes.WasThePersonRegisteredForVATController.onPageLoad(NormalMode)
        }

    case WhatWasThePersonUTRPage => _ => routes.EstateAddressLookupController.lookupAddress(NormalMode)

    case EstateAddressLookupPage => _ => routes.WhatIsYourFullNameController.onPageLoad(NormalMode)

    case IncomeOrGainSourcePage =>
      ua =>
        ua.get(IncomeOrGainSourcePage) match {
          case Some(value) if value.contains(IncomeOrGainSource.SomewhereElse) =>
            routes.OtherIncomeOrGainSourceController.onPageLoad(NormalMode)
          case _                                                               => aboutSectionRouting(ua)
        }

    case OtherIncomeOrGainSourcePage => ua => aboutSectionRouting(ua)

    case _ => _ => controllers.routes.IndexController.onPageLoad
  }

  private val checkRouteMap: Page => UserAnswers => Boolean => Call = {

    case ReceivedALetterPage =>
      ua =>
        hasAnswerChanged =>
          if (hasAnswerChanged) routes.LetterReferenceController.onPageLoad(CheckMode)
          else routes.CheckYourAnswersController.onPageLoad

    case DoYouHaveNationalInsuranceNumberPage =>
      ua =>
        hasAnswerChanged =>
          ua.get(DoYouHaveNationalInsuranceNumberPage) match {
            case Some(DoYouHaveNationalInsuranceNumber.YesIKnow) if hasAnswerChanged =>
              routes.WhatIsYourNationalInsuranceNumberController.onPageLoad(CheckMode)
            case _                                                                   => routes.CheckYourAnswersController.onPageLoad
          }

    case AreYouRegisteredForVATPage =>
      ua =>
        hasAnswerChanged =>
          ua.get(AreYouRegisteredForVATPage) match {
            case Some(AreYouRegisteredForVAT.YesIKnow) if hasAnswerChanged =>
              routes.WhatIsYourVATRegistrationNumberController.onPageLoad(CheckMode)
            case _                                                         => routes.CheckYourAnswersController.onPageLoad
          }

    case AreYouRegisteredForSelfAssessmentPage =>
      _ =>
        hasAnswerChanged =>
          if (hasAnswerChanged) routes.WhatIsYourUniqueTaxReferenceController.onPageLoad(CheckMode)
          else routes.CheckYourAnswersController.onPageLoad

    case IsTheIndividualRegisteredForVATPage =>
      ua =>
        hasAnswerChanged =>
          ua.get(IsTheIndividualRegisteredForVATPage) match {
            case Some(IsTheIndividualRegisteredForVAT.YesIKnow) if hasAnswerChanged =>
              routes.WhatIsTheIndividualsVATRegistrationNumberController.onPageLoad(CheckMode)
            case _                                                                  => routes.CheckYourAnswersController.onPageLoad
          }

    case IsTheIndividualRegisteredForSelfAssessmentPage =>
      ua =>
        hasAnswerChanged =>
          ua.get(IsTheIndividualRegisteredForSelfAssessmentPage) match {
            case Some(IsTheIndividualRegisteredForSelfAssessment.YesIKnow) if hasAnswerChanged =>
              routes.WhatIsTheIndividualsUniqueTaxReferenceController.onPageLoad(CheckMode)
            case _                                                                             => routes.CheckYourAnswersController.onPageLoad
          }

    case DoesTheIndividualHaveNationalInsuranceNumberPage =>
      ua =>
        hasAnswerChanged =>
          ua.get(DoesTheIndividualHaveNationalInsuranceNumberPage) match {
            case Some(DoesTheIndividualHaveNationalInsuranceNumber.YesIKnow) if hasAnswerChanged =>
              routes.WhatIsIndividualsNationalInsuranceNumberController.onPageLoad(CheckMode)
            case _                                                                               => routes.CheckYourAnswersController.onPageLoad
          }

    case AreYouRepresentingAnOrganisationPage =>
      ua =>
        hasAnswerChanged =>
          ua.get(AreYouRepresentingAnOrganisationPage) match {
            case Some(true) if hasAnswerChanged =>
              routes.WhatIsTheNameOfTheOrganisationYouRepresentController.onPageLoad(CheckMode)
            case _                              => routes.CheckYourAnswersController.onPageLoad
          }

    case DidThePersonHaveNINOPage =>
      ua =>
        hasAnswerChanged =>
          ua.get(DidThePersonHaveNINOPage) match {
            case Some(DidThePersonHaveNINO.YesIKnow) if hasAnswerChanged =>
              routes.WhatWasThePersonNINOController.onPageLoad(CheckMode)
            case _                                                       => routes.CheckYourAnswersController.onPageLoad
          }

    case WasThePersonRegisteredForSAPage =>
      ua =>
        hasAnswerChanged =>
          ua.get(WasThePersonRegisteredForSAPage) match {
            case Some(WasThePersonRegisteredForSA.YesIKnow) if hasAnswerChanged =>
              routes.WhatWasThePersonUTRController.onPageLoad(CheckMode)
            case _                                                              => routes.CheckYourAnswersController.onPageLoad
          }

    case WasThePersonRegisteredForVATPage =>
      ua =>
        hasAnswerChanged =>
          ua.get(WasThePersonRegisteredForVATPage) match {
            case Some(WasThePersonRegisteredForVAT.YesIKnow) if hasAnswerChanged =>
              routes.WhatWasThePersonVATRegistrationNumberController.onPageLoad(CheckMode)
            case _                                                               => routes.CheckYourAnswersController.onPageLoad
          }

    case RelatesToPage =>
      ua =>
        hasAnswerChanged =>
          if (hasAnswerChanged) nextPage(RelatesToPage, NormalMode, ua)
          else routes.CheckYourAnswersController.onPageLoad

    case HowWouldYouPreferToBeContactedPage =>
      ua =>
        hasAnswerChanged =>
          (
            ua.get(HowWouldYouPreferToBeContactedPage),
            ua.get(YourEmailAddressPage),
            ua.get(YourPhoneNumberPage)
          ) match {
            case (Some(preferences), None, _)
                if hasAnswerChanged && preferences.contains(HowWouldYouPreferToBeContacted.Email) =>
              routes.YourEmailAddressController.onPageLoad(CheckMode)
            case (Some(preferences), _, None)
                if hasAnswerChanged && preferences.contains(HowWouldYouPreferToBeContacted.Telephone) =>
              routes.YourPhoneNumberController.onPageLoad(CheckMode)
            case _ => routes.CheckYourAnswersController.onPageLoad
          }

    case YourEmailAddressPage =>
      ua =>
        _ =>
          (ua.get(HowWouldYouPreferToBeContactedPage), ua.get(YourPhoneNumberPage)) match {
            case (Some(preferences), None) if preferences.contains(HowWouldYouPreferToBeContacted.Telephone) =>
              routes.YourPhoneNumberController.onPageLoad(CheckMode)
            case (_, _)                                                                                      => routes.CheckYourAnswersController.onPageLoad
          }

    case OffshoreLiabilitiesPage =>
      ua =>
        _ =>
          ua.get(OffshoreLiabilitiesPage) match {
            case Some(true)  => routes.OnshoreLiabilitiesController.onPageLoad(CheckMode)
            case Some(false) => routes.OnlyOnshoreLiabilitiesController.onPageLoad(CheckMode)
            case _           => routes.CheckYourAnswersController.onPageLoad
          }

    case IncomeOrGainSourcePage =>
      ua =>
        hasAnswerChanged =>
          ua.get(IncomeOrGainSourcePage) match {
            case Some(value) if value.contains(IncomeOrGainSource.SomewhereElse) && hasAnswerChanged =>
              routes.OtherIncomeOrGainSourceController.onPageLoad(CheckMode)
            case _                                                                                   => routes.CheckYourAnswersController.onPageLoad
          }

    case AreYouTheEntityPage =>
      ua =>
        hasAnswerChanged => {
          val entity = ua.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)
          (entity, ua.get(AreYouTheEntityPage)) match {
            case (RelatesTo.AnIndividual, _) if hasAnswerChanged                          => normalRoutes(AreYouTheEntityPage)(ua)
            case (_, Some(AreYouTheEntity.IAmAnAccountantOrTaxAgent)) if hasAnswerChanged =>
              routes.AreYouRepresentingAnOrganisationController.onPageLoad(CheckMode)
            case _                                                                        => routes.CheckYourAnswersController.onPageLoad
          }
        }

    case _ => _ => _ => routes.CheckYourAnswersController.onPageLoad
  }

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers, hasAnswerChanged: Boolean = true): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode  =>
      checkRouteMap(page)(userAnswers)(hasAnswerChanged)
  }

  def submitPage(userAnswers: UserAnswers, reference: String): Call =
    routes.YouHaveSentYourNotificationController.onPageLoad

  def aboutSectionRouting(userAnswers: UserAnswers): Call = {
    val relatesToPage = userAnswers.get(RelatesToPage)

    relatesToPage match {
      case Some(RelatesTo.AnIndividual) if userAnswers.isTheUserTheIndividual =>
        controllers.notification.routes.WhatIsYourFullNameController.onPageLoad(NormalMode)
      case Some(RelatesTo.AnIndividual)                                       =>
        controllers.notification.routes.WhatIsTheIndividualsFullNameController.onPageLoad(NormalMode)
      case Some(RelatesTo.ACompany)                                           =>
        controllers.notification.routes.WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutController
          .onPageLoad(NormalMode)
      case Some(RelatesTo.ALimitedLiabilityPartnership)                       =>
        controllers.notification.routes.WhatIsTheLLPNameController.onPageLoad(NormalMode)
      case Some(RelatesTo.ATrust)                                             => controllers.notification.routes.WhatIsTheTrustNameController.onPageLoad(NormalMode)
      case Some(RelatesTo.AnEstate)                                           =>
        controllers.notification.routes.WhatWasTheNameOfThePersonWhoDiedController.onPageLoad(NormalMode)
      case _                                                                  => controllers.notification.routes.RelatesToController.onPageLoad(NormalMode)
    }
  }
}
