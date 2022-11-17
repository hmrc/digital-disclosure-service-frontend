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
import scala.util.{Success, Try}
import com.google.inject.{Singleton, ImplementedBy}

@Singleton
class NotificationDataServiceImpl extends NotificationDataService {
  
  def notificationToUserAnswers(notification: Notification): Try[UserAnswers] = {

    val userAnswers = initialiseUserAnswers(notification)

    for {
      uaWithBackground  <- backgroundToUserAnswers(notification.background, userAnswers)
      uaWithAboutYou    <- aboutYouToUserAnswers(notification.aboutYou, uaWithBackground)
      updatedUa         <- aboutTheEntityToUserAnswers(notification, uaWithAboutYou)
    } yield updatedUa

  }

  def aboutTheEntityToUserAnswers(notification: Notification, userAnswers: UserAnswers): Try[UserAnswers] = {
    notification.background.disclosureEntity.flatMap(_.entity match {
      case Individual => notification.aboutTheIndividual.map(aboutTheIndividualToUserAnswers(_, userAnswers))
      case Company => notification.aboutTheCompany.map(aboutTheCompanyToUserAnswers(_, userAnswers))
      case LLP => notification.aboutTheLLP.map(aboutTheLLPToUserAnswers(_, userAnswers))
      case Trust => notification.aboutTheTrust.map(aboutTheTrustToUserAnswers(_, userAnswers))
      //TODO change this when Estate is developed
      case Estate => None
    }).getOrElse(Success(userAnswers))
  }

  def initialiseUserAnswers(notification: Notification): UserAnswers = {
    import notification._

    UserAnswers(
      id = userId, 
      notificationId = notificationId, 
      submissionType = SubmissionType.Notification, 
      lastUpdated = lastUpdated
    )
  }

  def backgroundToUserAnswers(background: Background, userAnswers: UserAnswers): Try[UserAnswers] = {
    import background._

    val pages = List(
      haveYouReceivedALetter.map(PageWithValue(ReceivedALetterPage, _)),
      letterReferenceNumber.map(PageWithValue(LetterReferencePage, _)),
      areYouRepresetingAnOrganisation.map(PageWithValue(AreYouRepresentingAnOrganisationPage, _)),
      organisationName.map(PageWithValue(WhatIsTheNameOfTheOrganisationYouRepresentPage, _)),
      offshoreLiabilities.map(PageWithValue(OffshoreLiabilitiesPage, _)),
      onshoreLiabilities.map(PageWithValue(OnshoreLiabilitiesPage, _)) 
    ).flatten

    val disclosureEntityPages = background.disclosureEntity.map(de => entityPagesWithValues(de)).getOrElse(Nil)
    
    PageWithValue.pagesToUserAnswers(pages ++ disclosureEntityPages, userAnswers)
  }

  def entityPagesWithValues(disclosureEntity: DisclosureEntity): List[PageWithValue[_]] = {
    disclosureEntity.entity match {
      case Individual => entityToPageWithValue(RelatesTo.AnIndividual, AreYouTheIndividualPage, disclosureEntity.areYouTheEntity)
      case Company => entityToPageWithValue(RelatesTo.ACompany, AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage, disclosureEntity.areYouTheEntity)
      case LLP => entityToPageWithValue(RelatesTo.ALimitedLiabilityPartnership, AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage, disclosureEntity.areYouTheEntity)
      case Trust => entityToPageWithValue(RelatesTo.ATrust, AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage, disclosureEntity.areYouTheEntity)
      //TODO change this when Estate is developed
      case Estate => entityToPageWithValue(RelatesTo.AnIndividual, AreYouTheIndividualPage, disclosureEntity.areYouTheEntity)
    }
  }

  def entityToPageWithValue(relatesTo: RelatesTo, page: QuestionPage[Boolean], areYouTheEntity: Option[Boolean]): List[PageWithValue[_]] = {
    List(Some(PageWithValue(RelatesToPage, relatesTo)), areYouTheEntity.map(PageWithValue(page, _))).flatten
  }

  def aboutYouToUserAnswers(aboutYou: AboutYou, userAnswers: UserAnswers): Try[UserAnswers] = {

    import aboutYou._

    val pages = List(
      fullName.map(PageWithValue(WhatIsYourFullNamePage, _)),
      telephoneNumber.map(PageWithValue(YourPhoneNumberPage, _)),
      doYouHaveAEmailAddress.map(PageWithValue(DoYouHaveAnEmailAddressPage, _)),
      emailAddress.map(PageWithValue(YourEmailAddressPage, _)),
      dateOfBirth.map(PageWithValue(WhatIsYourDateOfBirthPage, _)),
      mainOccupation.map(PageWithValue(WhatIsYourMainOccupationPage, _)),
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
      dateOfBirth.map(PageWithValue(WhatIsTheIndividualDateOfBirthControllerPage, _)),
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

@ImplementedBy(classOf[NotificationDataServiceImpl])
trait NotificationDataService {
  def notificationToUserAnswers(notification: Notification): Try[UserAnswers] 
}