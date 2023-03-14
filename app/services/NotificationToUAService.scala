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

package services

import models._
import models.store.Notification
import models.store.notification._
import pages._
import scala.util.{Success, Try}
import com.google.inject.{Singleton, ImplementedBy}

@Singleton
class NotificationToUAServiceImpl extends NotificationToUAService {
  
  def notificationToUserAnswers(notification: Notification): Try[UserAnswers] = {

    val userAnswers = initialiseUserAnswers(notification)
    personalDetailsToUserAnswers(notification.personalDetails, userAnswers)

  }

  def personalDetailsToUserAnswers(personalDetails: PersonalDetails, userAnswers: UserAnswers): Try[UserAnswers] = 
    for {
      uaWithBackground  <- backgroundToUserAnswers(personalDetails.background, userAnswers)
      uaWithAboutYou    <- aboutYouToUserAnswers(personalDetails.aboutYou, uaWithBackground)
      updatedUa         <- aboutTheEntityToUserAnswers(personalDetails, uaWithAboutYou)
    } yield updatedUa

  def aboutTheEntityToUserAnswers(personalDetails: PersonalDetails, userAnswers: UserAnswers): Try[UserAnswers] = {
    personalDetails.background.disclosureEntity.flatMap(de => (de.entity, de.areYouTheEntity) match {
      case (Individual, Some(false)) => personalDetails.aboutTheIndividual.map(aboutTheIndividualToUserAnswers(_, userAnswers))
      case (Company, _) => personalDetails.aboutTheCompany.map(aboutTheCompanyToUserAnswers(_, userAnswers))
      case (LLP, _) => personalDetails.aboutTheLLP.map(aboutTheLLPToUserAnswers(_, userAnswers))
      case (Trust, _) => personalDetails.aboutTheTrust.map(aboutTheTrustToUserAnswers(_, userAnswers))
      case (Estate, _) => personalDetails.aboutTheEstate.map(aboutTheEstateToUserAnswers(_, userAnswers))
      case _ => Some(Success(userAnswers))
    }).getOrElse(Success(userAnswers))
  }

  def initialiseUserAnswers(notification: Notification): UserAnswers = {
    import notification._

    UserAnswers(
      id = userId, 
      submissionId = submissionId, 
      submissionType = SubmissionType.Notification, 
      lastUpdated = lastUpdated,
      created = created,
      metadata = notification.metadata,
      madeDeclaration = notification.madeDeclaration
    )
  }

  def backgroundToUserAnswers(background: Background, userAnswers: UserAnswers): Try[UserAnswers] = {
    import background._

    val pages = List(
      haveYouReceivedALetter.map(PageWithValue(ReceivedALetterPage, _)),
      letterReferenceNumber.map(PageWithValue(LetterReferencePage, _)),
      areYouRepresetingAnOrganisation.map(PageWithValue(AreYouRepresentingAnOrganisationPage, _)),
      organisationName.map(PageWithValue(WhatIsTheNameOfTheOrganisationYouRepresentPage, _)),
      incomeSource.map(PageWithValue(IncomeOrGainSourcePage, _)),
      otherIncomeSource.map(PageWithValue(OtherIncomeOrGainSourcePage, _))
    ).flatten

    val disclosureEntityPages = background.disclosureEntity.map(de => entityPagesWithValues(de)).getOrElse(Nil)
    val liabilitiesPages = liabilitiesPagesWithValues(background)
    
    PageWithValue.pagesToUserAnswers(pages ++ disclosureEntityPages ++ liabilitiesPages, userAnswers)
  }

  def liabilitiesPagesWithValues(background: Background): List[PageWithValue[Boolean]] = {
    background.offshoreLiabilities match {
      case Some(false) => 
        List(PageWithValue(OffshoreLiabilitiesPage, false))
      case offshore => 
        List(
          offshore.map(PageWithValue(OffshoreLiabilitiesPage, _)),
          background.onshoreLiabilities.map(PageWithValue(OnshoreLiabilitiesPage, _)) 
        ).flatten
    }
  }

  def entityPagesWithValues(disclosureEntity: DisclosureEntity): List[PageWithValue[_]] = {
    disclosureEntity.entity match {
      case Individual => entityToPageWithValue(RelatesTo.AnIndividual, AreYouTheIndividualPage, disclosureEntity.areYouTheEntity)
      case Company => entityToPageWithValue(RelatesTo.ACompany, AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage, disclosureEntity.areYouTheEntity)
      case LLP => entityToPageWithValue(RelatesTo.ALimitedLiabilityPartnership, AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage, disclosureEntity.areYouTheEntity)
      case Trust => entityToPageWithValue(RelatesTo.ATrust, AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage, disclosureEntity.areYouTheEntity)
      case Estate => entityToPageWithValue(RelatesTo.AnEstate, AreYouTheExecutorOfTheEstatePage, disclosureEntity.areYouTheEntity)
    }
  }

  def entityToPageWithValue(relatesTo: RelatesTo, page: QuestionPage[Boolean], areYouTheEntity: Option[Boolean]): List[PageWithValue[_]] = {
    List(Some(PageWithValue(RelatesToPage, relatesTo)), areYouTheEntity.map(PageWithValue(page, _))).flatten
  }

  def contactPreferencePageWithValues(contactPreferences: ContactPreferences): PageWithValue[_] = {
    val values: Set[HowWouldYouPreferToBeContacted] = contactPreferences.preferences.map {
      case Email => HowWouldYouPreferToBeContacted.Email
      case Telephone => HowWouldYouPreferToBeContacted.Telephone
    }

    PageWithValue(HowWouldYouPreferToBeContactedPage, values)
  }

  def aboutYouToUserAnswers(aboutYou: AboutYou, userAnswers: UserAnswers): Try[UserAnswers] = {

    import aboutYou._

    val pages:List[PageWithValue[_]] = List(
      fullName.map(PageWithValue(WhatIsYourFullNamePage, _)),
      telephoneNumber.map(PageWithValue(YourPhoneNumberPage, _)),
      emailAddress.map(PageWithValue(YourEmailAddressPage, _)),
      dateOfBirth.map(PageWithValue(WhatIsYourDateOfBirthPage, _)),
      mainOccupation.map(PageWithValue(WhatIsYourMainOccupationPage, _)),
      contactPreference.map(contactPreferencePageWithValues),
      doYouHaveANino.map(PageWithValue[DoYouHaveNationalInsuranceNumber](DoYouHaveNationalInsuranceNumberPage, _)),
      nino.map(PageWithValue(WhatIsYourNationalInsuranceNumberPage, _)),
      registeredForVAT.map(PageWithValue[AreYouRegisteredForVAT](AreYouRegisteredForVATPage, _)),
      vatRegNumber.map(PageWithValue(WhatIsYourVATRegistrationNumberPage, _)),
      registeredForSA.map(PageWithValue[AreYouRegisteredForSelfAssessment](AreYouRegisteredForSelfAssessmentPage, _)),
      sautr.map(PageWithValue(WhatIsYourUniqueTaxReferencePage, _)),
      address.map(PageWithValue(YourAddressLookupPage, _))
    ).flatten
    
    PageWithValue.pagesToUserAnswers(pages, userAnswers)

  }

  def aboutTheIndividualToUserAnswers(aboutTheIndividual: AboutTheIndividual, userAnswers: UserAnswers): Try[UserAnswers] = {

    import aboutTheIndividual._

    val pages = List(
      fullName.map(PageWithValue(WhatIsTheIndividualsFullNamePage, _)),
      dateOfBirth.map(PageWithValue(WhatIsTheIndividualDateOfBirthPage, _)),
      mainOccupation.map(PageWithValue(WhatIsTheIndividualOccupationPage, _)),
      doTheyHaveANino.map(PageWithValue[DoesTheIndividualHaveNationalInsuranceNumber](DoesTheIndividualHaveNationalInsuranceNumberPage, _)),
      nino.map(PageWithValue(WhatIsIndividualsNationalInsuranceNumberPage, _)),
      registeredForVAT.map(PageWithValue[IsTheIndividualRegisteredForVAT](IsTheIndividualRegisteredForVATPage, _)),
      vatRegNumber.map(PageWithValue(WhatIsTheIndividualsVATRegistrationNumberPage, _)),
      registeredForSA.map(PageWithValue[IsTheIndividualRegisteredForSelfAssessment](IsTheIndividualRegisteredForSelfAssessmentPage, _)),
      sautr.map(PageWithValue(WhatIsTheIndividualsUniqueTaxReferencePage, _)),
      address.map(PageWithValue(IndividualAddressLookupPage, _)) 
    ).flatten
    
    PageWithValue.pagesToUserAnswers(pages, userAnswers)

  }

  def aboutTheEstateToUserAnswers(aboutTheEstate: AboutTheEstate, userAnswers: UserAnswers): Try[UserAnswers] = {

    import aboutTheEstate._

    val pages = List(
      fullName.map(PageWithValue(WhatWasTheNameOfThePersonWhoDiedPage, _)),
      dateOfBirth.map(PageWithValue(WhatWasThePersonDateOfBirthPage, _)),
      mainOccupation.map(PageWithValue(WhatWasThePersonOccupationPage, _)),
      doTheyHaveANino.map(PageWithValue[DidThePersonHaveNINO](DidThePersonHaveNINOPage, _)),
      nino.map(PageWithValue(WhatWasThePersonNINOPage, _)),
      registeredForVAT.map(PageWithValue[WasThePersonRegisteredForVAT](WasThePersonRegisteredForVATPage, _)),
      vatRegNumber.map(PageWithValue(WhatWasThePersonVATRegistrationNumberPage, _)),
      registeredForSA.map(PageWithValue[WasThePersonRegisteredForSA](WasThePersonRegisteredForSAPage, _)),
      sautr.map(PageWithValue(WhatWasThePersonUTRPage, _)),
      address.map(PageWithValue(EstateAddressLookupPage, _)) 
    ).flatten
    
    PageWithValue.pagesToUserAnswers(pages, userAnswers)

  }

  def aboutTheCompanyToUserAnswers(aboutTheCompany: AboutTheCompany, userAnswers: UserAnswers): Try[UserAnswers] = {

    import aboutTheCompany._

    val pages = List(
      name.map(PageWithValue(WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage, _)),
      registrationNumber.map(PageWithValue(WhatIsTheCompanyRegistrationNumberPage, _)),
      address.map(PageWithValue(CompanyAddressLookupPage, _)) 
    ).flatten
    
    PageWithValue.pagesToUserAnswers(pages, userAnswers)

  }

  def aboutTheTrustToUserAnswers(aboutTheTrust: AboutTheTrust, userAnswers: UserAnswers): Try[UserAnswers] = {

    import aboutTheTrust._

    val pages = List(
      name.map(PageWithValue(WhatIsTheTrustNamePage, _)),
      address.map(PageWithValue(TrustAddressLookupPage, _)) 
    ).flatten
    
    PageWithValue.pagesToUserAnswers(pages, userAnswers)

  }


  def aboutTheLLPToUserAnswers(aboutTheLLP: AboutTheLLP, userAnswers: UserAnswers): Try[UserAnswers] = {

    import aboutTheLLP._

    val pages = List(
      name.map(PageWithValue(WhatIsTheLLPNamePage, _)),
      address.map(PageWithValue(LLPAddressLookupPage, _)) 
    ).flatten
    
    PageWithValue.pagesToUserAnswers(pages, userAnswers)

  }

}

@ImplementedBy(classOf[NotificationToUAServiceImpl])
trait NotificationToUAService {
  def notificationToUserAnswers(notification: Notification): Try[UserAnswers] 
  def personalDetailsToUserAnswers(personalDetails: PersonalDetails, userAnswers: UserAnswers): Try[UserAnswers]
}
