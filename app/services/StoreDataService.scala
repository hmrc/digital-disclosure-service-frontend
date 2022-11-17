/*
 * Copyright 2022 HM Revenue & Customs
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

package services

import models._
import models.store.notification._
import pages._
import com.google.inject.{Singleton, ImplementedBy}

@Singleton
class StoreDataServiceImpl extends StoreDataService {
  
  def userAnswersToNotification(userAnswers: UserAnswers): Notification = {

    val notification = Notification(
      userId = userAnswers.id,
      notificationId = userAnswers.notificationId,
      lastUpdated = userAnswers.lastUpdated,
      metadata = Metadata(),
      background = userAnswersToBackground(userAnswers),
      aboutYou = userAnswersToAboutYou(userAnswers)
    )

    userAnswers.get(RelatesToPage) match {
      case Some(RelatesTo.AnIndividual) => notification.copy(aboutTheIndividual = Some(userAnswersToAboutTheIndividual(userAnswers)))
      case Some(RelatesTo.ACompany) => notification.copy(aboutTheCompany = Some(userAnswersToAboutTheCompany(userAnswers)))
      case Some(RelatesTo.ATrust) => notification.copy(aboutTheTrust = Some(userAnswersToAboutTheTrust(userAnswers)))
      case Some(RelatesTo.ALimitedLiabilityPartnership) => notification.copy(aboutTheLLP = Some(userAnswersToAboutTheLLP(userAnswers)))
      //TODO change this when Estate is developed
      case _ => notification
    }

  }

  def userAnswersToBackground(userAnswers: UserAnswers): Background = 
    Background(
      haveYouReceivedALetter = userAnswers.get(ReceivedALetterPage),
      letterReferenceNumber = userAnswers.get(LetterReferencePage),
      disclosureEntity = userAnswerToDisclosureEntity(userAnswers),
      areYouRepresetingAnOrganisation = userAnswers.get(AreYouRepresentingAnOrganisationPage),
      organisationName = userAnswers.get(WhatIsTheNameOfTheOrganisationYouRepresentPage),
      offshoreLiabilities = userAnswers.get(OffshoreLiabilitiesPage),
      onshoreLiabilities = userAnswers.get(OnshoreLiabilitiesPage) 
    )

  def userAnswerToDisclosureEntity(userAnswers: UserAnswers): Option[DisclosureEntity] = {
    userAnswers.get(RelatesToPage).map(_ match {
      case RelatesTo.AnIndividual => DisclosureEntity(Individual, userAnswers.get(AreYouTheIndividualPage))
      case RelatesTo.ACompany => DisclosureEntity(Company, userAnswers.get(AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage))
      case RelatesTo.ALimitedLiabilityPartnership => DisclosureEntity(LLP, userAnswers.get(AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage))
      case RelatesTo.ATrust => DisclosureEntity(Trust, userAnswers.get(AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage))      
      //TODO change this when Estate is developed
      case RelatesTo.AnEstate => DisclosureEntity(Estate, None)
    })
  }

  def userAnswersToAboutYou(userAnswers: UserAnswers): AboutYou = 
    AboutYou(
      fullName = userAnswers.get(WhatIsYourFullNamePage),
      telephoneNumber = userAnswers.get(YourPhoneNumberPage),
      doYouHaveAEmailAddress = userAnswers.get(DoYouHaveAnEmailAddressPage),
      emailAddress = userAnswers.get(YourEmailAddressPage),
      dateOfBirth = userAnswers.get(WhatIsYourDateOfBirthPage),
      mainOccupation = userAnswers.get(WhatIsYourMainOccupationPage),
      doYouHaveANino = userAnswers.get(DoYouHaveNationalInsuranceNumberPage),
      nino = userAnswers.get(WhatIsYourNationalInsuranceNumberPage),
      registeredForVAT = userAnswers.get(AreYouRegisteredForVATPage),
      vatRegNumber = userAnswers.get(WhatIsYourVATRegistrationNumberPage),
      registeredForSA = userAnswers.get(AreYouRegisteredForSelfAssessmentPage),
      sautr = userAnswers.get(WhatIsYourUniqueTaxReferencePage),
      address = userAnswers.get(YourAddressLookupPage)
    )

  def userAnswersToAboutTheIndividual(userAnswers: UserAnswers): AboutTheIndividual = 
    AboutTheIndividual(
      fullName = userAnswers.get(WhatIsTheIndividualsFullNamePage),
      dateOfBirth = userAnswers.get(WhatIsTheIndividualDateOfBirthControllerPage),
      mainOccupation = userAnswers.get(WhatIsTheIndividualOccupationPage),
      doTheyHaveANino = userAnswers.get(DoesTheIndividualHaveNationalInsuranceNumberPage),
      nino = userAnswers.get(WhatIsIndividualsNationalInsuranceNumberPage),
      registeredForVAT = userAnswers.get(IsTheIndividualRegisteredForVATPage),
      vatRegNumber = userAnswers.get(WhatIsTheIndividualsVATRegistrationNumberPage),
      registeredForSA = userAnswers.get(IsTheIndividualRegisteredForSelfAssessmentPage),
      sautr = userAnswers.get(WhatIsTheIndividualsUniqueTaxReferencePage),
      address = userAnswers.get(IndividualAddressLookupPage)
    )

  def userAnswersToAboutTheCompany(userAnswers: UserAnswers): AboutTheCompany = 
    AboutTheCompany(
      name = userAnswers.get(WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage),
      registrationNumber = userAnswers.get(WhatIsTheCompanyRegistrationNumberPage),
      address = userAnswers.get(CompanyAddressLookupPage)
    )

  def userAnswersToAboutTheTrust(userAnswers: UserAnswers): AboutTheTrust = 
    AboutTheTrust(
      name = userAnswers.get(WhatIsTheTrustNamePage),
      address = userAnswers.get(TrustAddressLookupPage)
    )

  def userAnswersToAboutTheLLP(userAnswers: UserAnswers): AboutTheLLP = 
    AboutTheLLP(
      name = userAnswers.get(WhatIsTheLLPNamePage),
      address = userAnswers.get(LLPAddressLookupPage)
    )

}

@ImplementedBy(classOf[StoreDataServiceImpl])
trait StoreDataService {
  def userAnswersToNotification(userAnswers: UserAnswers): Notification
}