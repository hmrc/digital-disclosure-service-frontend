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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import models.store.notification._
import pages._
import models._
import models.address._
import models.store._
import org.scalatest.TryValues
import java.time.{LocalDate, Instant, LocalDateTime}

class NotificationDataServiceSpec extends AnyWordSpec with Matchers with TryValues {

  val sut = new NotificationDataServiceImpl

  val testNotification = Notification("userId", "notificationId", Instant.now(), Metadata(), Background(), AboutYou())

  val emptyUA = UserAnswers("id")

  "entityPagesWithValues" should {

    "return the pagesWithValues for an Individual who has answered if they are the individual" in {
      val disclosureEntity = DisclosureEntity(Individual, Some(true))
      val expectedResult = List(PageWithValue(RelatesToPage, RelatesTo.AnIndividual), PageWithValue(AreYouTheIndividualPage, true))
      sut.entityPagesWithValues(disclosureEntity) shouldEqual expectedResult
    }

    "return the pagesWithValues for an Individual who has not answered if they are the individual" in {
      val disclosureEntity = DisclosureEntity(Individual, None)
      val expectedResult = List(PageWithValue(RelatesToPage, RelatesTo.AnIndividual))
      sut.entityPagesWithValues(disclosureEntity) shouldEqual expectedResult
    }

    "return the pagesWithValues for a Company who has answered if they are the Company" in {
      val disclosureEntity = DisclosureEntity(Company, Some(true))
      val expectedResult = List(PageWithValue(RelatesToPage, RelatesTo.ACompany), PageWithValue(AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage, true))
      sut.entityPagesWithValues(disclosureEntity) shouldEqual expectedResult
    }

    "return the pagesWithValues for a Company who has not answered if they are the Company" in {
      val disclosureEntity = DisclosureEntity(Company, None)
      val expectedResult = List(PageWithValue(RelatesToPage, RelatesTo.ACompany))
      sut.entityPagesWithValues(disclosureEntity) shouldEqual expectedResult
    }

    "return the pagesWithValues for a Trust who has answered if they are the Trust" in {
      val disclosureEntity = DisclosureEntity(Trust, Some(true))
      val expectedResult = List(PageWithValue(RelatesToPage, RelatesTo.ATrust), PageWithValue(AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage, true))
      sut.entityPagesWithValues(disclosureEntity) shouldEqual expectedResult
    }

    "return the pagesWithValues for a Trust who has not answered if they are the Trust" in {
      val disclosureEntity = DisclosureEntity(Trust, None)
      val expectedResult = List(PageWithValue(RelatesToPage, RelatesTo.ATrust))
      sut.entityPagesWithValues(disclosureEntity) shouldEqual expectedResult
    }

    "return the pagesWithValues for an LLP who has answered if they are the LLP" in {
      val disclosureEntity = DisclosureEntity(LLP, Some(true))
      val expectedResult = List(PageWithValue(RelatesToPage, RelatesTo.ALimitedLiabilityPartnership), PageWithValue(AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage, true))
      sut.entityPagesWithValues(disclosureEntity) shouldEqual expectedResult
    }

    "return the pagesWithValues for an LLP who has not answered if they are the LLP" in {
      val disclosureEntity = DisclosureEntity(LLP, None)
      val expectedResult = List(PageWithValue(RelatesToPage, RelatesTo.ALimitedLiabilityPartnership))
      sut.entityPagesWithValues(disclosureEntity) shouldEqual expectedResult
    }

    "return the pagesWithValues for an Estate who has answered if they are an executor" in {
      val disclosureEntity = DisclosureEntity(Estate, Some(true))
      val expectedResult = List(PageWithValue(RelatesToPage, RelatesTo.AnEstate), PageWithValue(AreYouTheExecutorOfTheEstatePage, true))
      sut.entityPagesWithValues(disclosureEntity) shouldEqual expectedResult
    }

    "return the pagesWithValues for an Estate who has not answered if they are an executor" in {
      val disclosureEntity = DisclosureEntity(Estate, None)
      val expectedResult = List(PageWithValue(RelatesToPage, RelatesTo.AnEstate))
      sut.entityPagesWithValues(disclosureEntity) shouldEqual expectedResult
    }

  }

  "backgroundToUserAnswers" should {

    "return a PageWithValue for haveYouReceivedALetter" in {
      val background = Background(haveYouReceivedALetter = Some(true))
      val updatedUserAnswers = sut.backgroundToUserAnswers(background, emptyUA).success.value
      updatedUserAnswers.get(ReceivedALetterPage)                             shouldEqual Some(true)
      updatedUserAnswers.get(LetterReferencePage)                             shouldEqual None
      updatedUserAnswers.get(AreYouRepresentingAnOrganisationPage)            shouldEqual None
      updatedUserAnswers.get(WhatIsTheNameOfTheOrganisationYouRepresentPage)  shouldEqual None
      updatedUserAnswers.get(OffshoreLiabilitiesPage)                         shouldEqual None
      updatedUserAnswers.get(OnshoreLiabilitiesPage)                          shouldEqual None
      updatedUserAnswers.get(RelatesToPage)                                   shouldEqual None
    }

    "return a PageWithValue for letterReferenceNumber" in {
      val background = Background(letterReferenceNumber = Some("123"))
      val updatedUserAnswers = sut.backgroundToUserAnswers(background, emptyUA).success.value
      updatedUserAnswers.get(ReceivedALetterPage)                             shouldEqual None
      updatedUserAnswers.get(LetterReferencePage)                             shouldEqual Some("123")
      updatedUserAnswers.get(AreYouRepresentingAnOrganisationPage)            shouldEqual None
      updatedUserAnswers.get(WhatIsTheNameOfTheOrganisationYouRepresentPage)  shouldEqual None
      updatedUserAnswers.get(OffshoreLiabilitiesPage)                         shouldEqual None
      updatedUserAnswers.get(OnshoreLiabilitiesPage)                          shouldEqual None
      updatedUserAnswers.get(RelatesToPage)                                   shouldEqual None
    }

    "return a PageWithValue for areYouRepresetingAnOrganisation" in {
      val background = Background(areYouRepresetingAnOrganisation = Some(false))
      val updatedUserAnswers = sut.backgroundToUserAnswers(background, emptyUA).success.value
      updatedUserAnswers.get(ReceivedALetterPage)                             shouldEqual None
      updatedUserAnswers.get(LetterReferencePage)                             shouldEqual None
      updatedUserAnswers.get(AreYouRepresentingAnOrganisationPage)            shouldEqual Some(false)
      updatedUserAnswers.get(WhatIsTheNameOfTheOrganisationYouRepresentPage)  shouldEqual None
      updatedUserAnswers.get(OffshoreLiabilitiesPage)                         shouldEqual None
      updatedUserAnswers.get(OnshoreLiabilitiesPage)                          shouldEqual None
      updatedUserAnswers.get(RelatesToPage)                                   shouldEqual None
    }

    "return a PageWithValue for organisationName" in {
      val background = Background(organisationName = Some("Some name"))
      val updatedUserAnswers = sut.backgroundToUserAnswers(background, emptyUA).success.value
      updatedUserAnswers.get(ReceivedALetterPage)                             shouldEqual None
      updatedUserAnswers.get(LetterReferencePage)                             shouldEqual None
      updatedUserAnswers.get(AreYouRepresentingAnOrganisationPage)            shouldEqual None
      updatedUserAnswers.get(WhatIsTheNameOfTheOrganisationYouRepresentPage)  shouldEqual Some("Some name")
      updatedUserAnswers.get(OffshoreLiabilitiesPage)                         shouldEqual None
      updatedUserAnswers.get(OnshoreLiabilitiesPage)                          shouldEqual None
      updatedUserAnswers.get(RelatesToPage)                                   shouldEqual None
    }

    "return a PageWithValue for offshoreLiabilities" in {
      val background = Background(offshoreLiabilities = Some(true))
      val updatedUserAnswers = sut.backgroundToUserAnswers(background, emptyUA).success.value
      updatedUserAnswers.get(ReceivedALetterPage)                             shouldEqual None
      updatedUserAnswers.get(LetterReferencePage)                             shouldEqual None
      updatedUserAnswers.get(AreYouRepresentingAnOrganisationPage)            shouldEqual None
      updatedUserAnswers.get(WhatIsTheNameOfTheOrganisationYouRepresentPage)  shouldEqual None
      updatedUserAnswers.get(OffshoreLiabilitiesPage)                         shouldEqual Some(true)
      updatedUserAnswers.get(OnshoreLiabilitiesPage)                          shouldEqual None
      updatedUserAnswers.get(RelatesToPage)                                   shouldEqual None
    }

    "return a PageWithValue for onshoreLiabilities" in {
      val background = Background(onshoreLiabilities = Some(false))
      val updatedUserAnswers = sut.backgroundToUserAnswers(background, emptyUA).success.value
      updatedUserAnswers.get(ReceivedALetterPage)                             shouldEqual None
      updatedUserAnswers.get(LetterReferencePage)                             shouldEqual None
      updatedUserAnswers.get(AreYouRepresentingAnOrganisationPage)            shouldEqual None
      updatedUserAnswers.get(WhatIsTheNameOfTheOrganisationYouRepresentPage)  shouldEqual None
      updatedUserAnswers.get(OffshoreLiabilitiesPage)                         shouldEqual None
      updatedUserAnswers.get(OnshoreLiabilitiesPage)                          shouldEqual Some(false)
      updatedUserAnswers.get(RelatesToPage)                                   shouldEqual None
    }

    "return a PageWithValue for relatesTo" in {
      val background = Background(disclosureEntity = Some(DisclosureEntity(Individual, Some(true))))
      val updatedUserAnswers = sut.backgroundToUserAnswers(background, emptyUA).success.value
      updatedUserAnswers.get(ReceivedALetterPage)                             shouldEqual None
      updatedUserAnswers.get(LetterReferencePage)                             shouldEqual None
      updatedUserAnswers.get(AreYouRepresentingAnOrganisationPage)            shouldEqual None
      updatedUserAnswers.get(WhatIsTheNameOfTheOrganisationYouRepresentPage)  shouldEqual None
      updatedUserAnswers.get(OffshoreLiabilitiesPage)                         shouldEqual None
      updatedUserAnswers.get(OnshoreLiabilitiesPage)                          shouldEqual None
      updatedUserAnswers.get(RelatesToPage)                                   shouldEqual Some(RelatesTo.AnIndividual)
      updatedUserAnswers.get(AreYouTheIndividualPage)                         shouldEqual Some(true)
    }

    "return a PageWithValue where offshore is false" in {
      val background = Background(
        haveYouReceivedALetter = Some(false),
        letterReferenceNumber = Some("456"),
        areYouRepresetingAnOrganisation = Some(true),
        organisationName = Some("Some other name"),
        offshoreLiabilities = Some(false),
        onshoreLiabilities = Some(true),
        disclosureEntity = Some(DisclosureEntity(Individual, Some(false)))
      )
      val updatedUserAnswers = sut.backgroundToUserAnswers(background, emptyUA).success.value
      updatedUserAnswers.get(ReceivedALetterPage)                             shouldEqual Some(false)
      updatedUserAnswers.get(LetterReferencePage)                             shouldEqual Some("456")
      updatedUserAnswers.get(AreYouRepresentingAnOrganisationPage)            shouldEqual Some(true)
      updatedUserAnswers.get(WhatIsTheNameOfTheOrganisationYouRepresentPage)  shouldEqual Some("Some other name")
      updatedUserAnswers.get(OffshoreLiabilitiesPage)                         shouldEqual Some(false)
      updatedUserAnswers.get(OnshoreLiabilitiesPage)                          shouldEqual None
      updatedUserAnswers.get(RelatesToPage)                                   shouldEqual Some(RelatesTo.AnIndividual)
      updatedUserAnswers.get(AreYouTheIndividualPage)                         shouldEqual Some(false)
    }

    "return a PageWithValue for all that are set" in {
      val background = Background(
        haveYouReceivedALetter = Some(false),
        letterReferenceNumber = Some("456"),
        areYouRepresetingAnOrganisation = Some(true),
        organisationName = Some("Some other name"),
        offshoreLiabilities = Some(true),
        onshoreLiabilities = Some(false),
        disclosureEntity = Some(DisclosureEntity(Individual, Some(false)))
      )
      val updatedUserAnswers = sut.backgroundToUserAnswers(background, emptyUA).success.value
      updatedUserAnswers.get(ReceivedALetterPage)                             shouldEqual Some(false)
      updatedUserAnswers.get(LetterReferencePage)                             shouldEqual Some("456")
      updatedUserAnswers.get(AreYouRepresentingAnOrganisationPage)            shouldEqual Some(true)
      updatedUserAnswers.get(WhatIsTheNameOfTheOrganisationYouRepresentPage)  shouldEqual Some("Some other name")
      updatedUserAnswers.get(OffshoreLiabilitiesPage)                         shouldEqual Some(true)
      updatedUserAnswers.get(OnshoreLiabilitiesPage)                          shouldEqual Some(false)
      updatedUserAnswers.get(RelatesToPage)                                   shouldEqual Some(RelatesTo.AnIndividual)
      updatedUserAnswers.get(AreYouTheIndividualPage)                         shouldEqual Some(false)
    }

  }
  
  "initialiseUserAnswers" should {
    "retrieve the userId, notificationId and lastUpdated from the notification" in {
      val instant = Instant.now()
      val metadata = Metadata(reference = Some("123"), submissionTime = Some(LocalDateTime.now))
      val expectedResult = UserAnswers(id = "This user Id", notificationId = "Some notification Id", lastUpdated = instant, metadata = metadata)
      val notification = Notification("This user Id", "Some notification Id", instant, metadata, Background(), AboutYou())
      sut.initialiseUserAnswers(notification) shouldEqual expectedResult
    }
  }

  "aboutYouToUserAnswers" should {

    val address = Address("line 1", Some("line 2"), Some("line 3"), "line 4", "postcode", Country("GBR"))
    val localDate = LocalDate.now()

    "return no PageWithValues for an empty AboutYou" in {
      val aboutYou = AboutYou()
      val updatedUserAnswers = sut.aboutYouToUserAnswers(aboutYou, emptyUA).success.value
      updatedUserAnswers.get(WhatIsYourFullNamePage)                  shouldEqual None
      updatedUserAnswers.get(YourPhoneNumberPage)                     shouldEqual None
      updatedUserAnswers.get(DoYouHaveAnEmailAddressPage)             shouldEqual None
      updatedUserAnswers.get(YourEmailAddressPage)                    shouldEqual None
      updatedUserAnswers.get(WhatIsYourDateOfBirthPage)               shouldEqual None
      updatedUserAnswers.get(WhatIsYourMainOccupationPage)            shouldEqual None
      updatedUserAnswers.get(DoYouHaveNationalInsuranceNumberPage)    shouldEqual None
      updatedUserAnswers.get(WhatIsYourNationalInsuranceNumberPage)   shouldEqual None
      updatedUserAnswers.get(AreYouRegisteredForVATPage)              shouldEqual None
      updatedUserAnswers.get(WhatIsYourVATRegistrationNumberPage)     shouldEqual None
      updatedUserAnswers.get(AreYouRegisteredForSelfAssessmentPage)   shouldEqual None
      updatedUserAnswers.get(WhatIsYourUniqueTaxReferencePage)        shouldEqual None
      updatedUserAnswers.get(YourAddressLookupPage)                   shouldEqual None
    }

    "return a PageWithValue for all that are set" in {
      val aboutYou = AboutYou(
        fullName = Some("Some name"),
        telephoneNumber = Some("1234"),
        doYouHaveAEmailAddress = Some(true),
        emailAddress = Some("Some email"),
        dateOfBirth = Some(localDate),
        mainOccupation = Some("Occupation"),
        doYouHaveANino = Some(YesNoOrUnsure.Yes),
        nino = Some("Some NINO"),
        registeredForVAT = Some(YesNoOrUnsure.Unsure),
        vatRegNumber = Some("Some Reg"),
        registeredForSA = Some(YesNoOrUnsure.No),
        sautr = Some("UTR"),
        address = Some(address)
      )
      val updatedUserAnswers = sut.aboutYouToUserAnswers(aboutYou, emptyUA).success.value
      updatedUserAnswers.get(WhatIsYourFullNamePage)                  shouldEqual Some("Some name")
      updatedUserAnswers.get(YourPhoneNumberPage)                     shouldEqual Some("1234")
      updatedUserAnswers.get(DoYouHaveAnEmailAddressPage)             shouldEqual Some(true)
      updatedUserAnswers.get(YourEmailAddressPage)                    shouldEqual Some("Some email")
      updatedUserAnswers.get(WhatIsYourDateOfBirthPage)               shouldEqual Some(localDate)
      updatedUserAnswers.get(WhatIsYourMainOccupationPage)            shouldEqual Some("Occupation")
      updatedUserAnswers.get(DoYouHaveNationalInsuranceNumberPage)    shouldEqual Some(DoYouHaveNationalInsuranceNumber.YesIKnow)
      updatedUserAnswers.get(WhatIsYourNationalInsuranceNumberPage)   shouldEqual Some("Some NINO")
      updatedUserAnswers.get(AreYouRegisteredForVATPage)              shouldEqual Some(AreYouRegisteredForVAT.YesButDontKnow)
      updatedUserAnswers.get(WhatIsYourVATRegistrationNumberPage)     shouldEqual Some("Some Reg")
      updatedUserAnswers.get(AreYouRegisteredForSelfAssessmentPage)   shouldEqual Some(AreYouRegisteredForSelfAssessment.No)
      updatedUserAnswers.get(WhatIsYourUniqueTaxReferencePage)        shouldEqual Some("UTR")
      updatedUserAnswers.get(YourAddressLookupPage)                   shouldEqual Some(address)
    }
  }

  "aboutTheIndividualToUserAnswers" should {

    val address = Address("line 1", Some("line 2"), Some("line 3"), "line 4", "postcode", Country("GBR"))
    val localDate = LocalDate.now()

    "return no PageWithValues for an empty AboutYou" in {
      val aboutTheIndividual = AboutTheIndividual()
      val updatedUserAnswers = sut.aboutTheIndividualToUserAnswers(aboutTheIndividual, emptyUA).success.value
      updatedUserAnswers.get(WhatIsTheIndividualsFullNamePage)                  shouldEqual None
      updatedUserAnswers.get(WhatIsTheIndividualDateOfBirthControllerPage)      shouldEqual None
      updatedUserAnswers.get(WhatIsTheIndividualOccupationPage)                 shouldEqual None
      updatedUserAnswers.get(DoesTheIndividualHaveNationalInsuranceNumberPage)  shouldEqual None
      updatedUserAnswers.get(WhatIsIndividualsNationalInsuranceNumberPage)      shouldEqual None
      updatedUserAnswers.get(IsTheIndividualRegisteredForVATPage)               shouldEqual None
      updatedUserAnswers.get(WhatIsTheIndividualsVATRegistrationNumberPage)     shouldEqual None
      updatedUserAnswers.get(IsTheIndividualRegisteredForSelfAssessmentPage)    shouldEqual None
      updatedUserAnswers.get(WhatIsTheIndividualsUniqueTaxReferencePage)        shouldEqual None
      updatedUserAnswers.get(IndividualAddressLookupPage)                       shouldEqual None
    }

    "return a PageWithValue for all that are set" in {
      val aboutTheIndividual = AboutTheIndividual(
        fullName = Some("Some name"),
        dateOfBirth = Some(localDate),
        mainOccupation = Some("Occupation"),
        doTheyHaveANino = Some(YesNoOrUnsure.Yes),
        nino = Some("Some NINO"),
        registeredForVAT = Some(YesNoOrUnsure.Unsure),
        vatRegNumber = Some("Some Reg"),
        registeredForSA = Some(YesNoOrUnsure.No),
        sautr = Some("UTR"),
        address = Some(address)
      )
      val updatedUserAnswers = sut.aboutTheIndividualToUserAnswers(aboutTheIndividual, emptyUA).success.value
      updatedUserAnswers.get(WhatIsTheIndividualsFullNamePage)                  shouldEqual Some("Some name")
      updatedUserAnswers.get(WhatIsTheIndividualDateOfBirthControllerPage)      shouldEqual Some(localDate)
      updatedUserAnswers.get(WhatIsTheIndividualOccupationPage)                 shouldEqual Some("Occupation")
      updatedUserAnswers.get(DoesTheIndividualHaveNationalInsuranceNumberPage)  shouldEqual Some(DoesTheIndividualHaveNationalInsuranceNumber.YesIKnow)
      updatedUserAnswers.get(WhatIsIndividualsNationalInsuranceNumberPage)      shouldEqual Some("Some NINO")
      updatedUserAnswers.get(IsTheIndividualRegisteredForVATPage)               shouldEqual Some(IsTheIndividualRegisteredForVAT.YesButDontKnow)
      updatedUserAnswers.get(WhatIsTheIndividualsVATRegistrationNumberPage)     shouldEqual Some("Some Reg")
      updatedUserAnswers.get(IsTheIndividualRegisteredForSelfAssessmentPage)    shouldEqual Some(IsTheIndividualRegisteredForSelfAssessment.No)
      updatedUserAnswers.get(WhatIsTheIndividualsUniqueTaxReferencePage)        shouldEqual Some("UTR")
      updatedUserAnswers.get(IndividualAddressLookupPage)                       shouldEqual Some(address)
    }
  }

  "aboutTheCompanyToUserAnswers" should {

    val address = Address("line 1", Some("line 2"), Some("line 3"), "line 4", "postcode", Country("GBR"))

    "return no PageWithValues for an empty AboutYou" in {
      val aboutTheCompany = AboutTheCompany()
()
      val updatedUserAnswers = sut.aboutTheCompanyToUserAnswers(aboutTheCompany, emptyUA).success.value
      updatedUserAnswers.get(WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage) shouldEqual None
      updatedUserAnswers.get(WhatIsTheCompanyRegistrationNumberPage)                shouldEqual None
      updatedUserAnswers.get(CompanyAddressLookupPage)                              shouldEqual None
    }

    "return a PageWithValue for all that are set" in {
      val aboutTheCompany = AboutTheCompany(
        name = Some("Some name"),
        registrationNumber = Some("Some reg"),
        address = Some(address)
      )
      val updatedUserAnswers = sut.aboutTheCompanyToUserAnswers(aboutTheCompany, emptyUA).success.value
      updatedUserAnswers.get(WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage) shouldEqual Some("Some name")
      updatedUserAnswers.get(WhatIsTheCompanyRegistrationNumberPage)                shouldEqual Some("Some reg")
      updatedUserAnswers.get(CompanyAddressLookupPage)                              shouldEqual Some(address)
    }
  }

  "aboutTheTrustToUserAnswers" should {

    val address = Address("line 1", Some("line 2"), Some("line 3"), "line 4", "postcode", Country("GBR"))

    "return no PageWithValues for an empty AboutYou" in {
      val aboutTheTrust = AboutTheTrust()
()
      val updatedUserAnswers = sut.aboutTheTrustToUserAnswers(aboutTheTrust, emptyUA).success.value
      updatedUserAnswers.get(WhatIsTheTrustNamePage) shouldEqual None
      updatedUserAnswers.get(TrustAddressLookupPage) shouldEqual None
    }

    "return a PageWithValue for all that are set" in {
      val aboutTheTrust = AboutTheTrust(
        name = Some("Some name"),
        address = Some(address)
      )
      val updatedUserAnswers = sut.aboutTheTrustToUserAnswers(aboutTheTrust, emptyUA).success.value
      updatedUserAnswers.get(WhatIsTheTrustNamePage) shouldEqual Some("Some name")
      updatedUserAnswers.get(TrustAddressLookupPage) shouldEqual Some(address)
    }
  }

  "aboutTheLLPToUserAnswers" should {

    val address = Address("line 1", Some("line 2"), Some("line 3"), "line 4", "postcode", Country("GBR"))

    "return no PageWithValues for an empty AboutYou" in {
      val aboutTheLLP = AboutTheLLP()
()
      val updatedUserAnswers = sut.aboutTheLLPToUserAnswers(aboutTheLLP, emptyUA).success.value
      updatedUserAnswers.get(WhatIsTheLLPNamePage) shouldEqual None
      updatedUserAnswers.get(LLPAddressLookupPage) shouldEqual None
    }

    "return a PageWithValue for all that are set" in {
      val aboutTheLLP = AboutTheLLP(
        name = Some("Some name"),
        address = Some(address)
      )
      val updatedUserAnswers = sut.aboutTheLLPToUserAnswers(aboutTheLLP, emptyUA).success.value
      updatedUserAnswers.get(WhatIsTheLLPNamePage) shouldEqual Some("Some name")
      updatedUserAnswers.get(LLPAddressLookupPage) shouldEqual Some(address)
    }
  }

  "aboutTheEntityToUserAnswers" should {

    "return populated AboutTheIndividual information where the entity is set to Individual" in {
      val background = Background(disclosureEntity = Some(DisclosureEntity(Individual, Some(false))))
      val notification = testNotification.copy(background = background, aboutTheIndividual = Some(AboutTheIndividual(fullName = Some("Some full name"))))

      val updatedUserAnswers = sut.aboutTheEntityToUserAnswers(notification, emptyUA).success.value
      updatedUserAnswers.get(WhatIsTheIndividualsFullNamePage) shouldEqual Some("Some full name")
    }

    "return populated AboutTheCompany information where the entity is set to Company" in {
      val background = Background(disclosureEntity = Some(DisclosureEntity(Company, Some(false))))
      val notification = testNotification.copy(background = background, aboutTheCompany = Some(AboutTheCompany(name = Some("Some name"))))

      val updatedUserAnswers = sut.aboutTheEntityToUserAnswers(notification, emptyUA).success.value
      updatedUserAnswers.get(WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage) shouldEqual Some("Some name")
    }

    "return populated AboutTheTrust information where the entity is set to Trust" in {
      val background = Background(disclosureEntity = Some(DisclosureEntity(Trust, Some(false))))
      val notification = testNotification.copy(background = background, aboutTheTrust = Some(AboutTheTrust(name = Some("Some name"))))

      val updatedUserAnswers = sut.aboutTheEntityToUserAnswers(notification, emptyUA).success.value
      updatedUserAnswers.get(WhatIsTheTrustNamePage) shouldEqual Some("Some name")
    }

    "return populated AboutTheLLP information where the entity is set to LLP" in {
      val background = Background(disclosureEntity = Some(DisclosureEntity(LLP, Some(false))))
      val notification = testNotification.copy(background = background, aboutTheLLP = Some(AboutTheLLP(name = Some("Some name"))))

      val updatedUserAnswers = sut.aboutTheEntityToUserAnswers(notification, emptyUA).success.value
      updatedUserAnswers.get(WhatIsTheLLPNamePage) shouldEqual Some("Some name")
    }

  }


  "aboutTheEstateToUserAnswers" should {

    val address = Address("line 1", Some("line 2"), Some("line 3"), "line 4", "postcode", Country("GBR"))
    val localDate = LocalDate.now()

    "return no PageWithValues for an empty AboutYou" in {
      val aboutTheEstate = AboutTheEstate()
      val updatedUserAnswers = sut.aboutTheEstateToUserAnswers(aboutTheEstate, emptyUA).success.value
      updatedUserAnswers.get(WhatWasTheNameOfThePersonWhoDiedPage)        shouldEqual None
      updatedUserAnswers.get(WhatWasThePersonDateOfBirthPage)             shouldEqual None
      updatedUserAnswers.get(WhatWasThePersonOccupationPage)              shouldEqual None
      updatedUserAnswers.get(DidThePersonHaveNINOPage)                    shouldEqual None
      updatedUserAnswers.get(WhatWasThePersonNINOPage)                    shouldEqual None
      updatedUserAnswers.get(WasThePersonRegisteredForVATPage)            shouldEqual None
      updatedUserAnswers.get(WhatWasThePersonVATRegistrationNumberPage)   shouldEqual None
      updatedUserAnswers.get(WasThePersonRegisteredForSAPage)             shouldEqual None
      updatedUserAnswers.get(WhatWasThePersonUTRPage)                     shouldEqual None
      updatedUserAnswers.get(EstateAddressLookupPage)                     shouldEqual None
    }

    "return a PageWithValue for all that are set" in {
      val aboutTheEstate = AboutTheEstate(
        fullName = Some("Some name"),
        dateOfBirth = Some(localDate),
        mainOccupation = Some("Occupation"),
        doTheyHaveANino = Some(YesNoOrUnsure.Yes),
        nino = Some("Some NINO"),
        registeredForVAT = Some(YesNoOrUnsure.Unsure),
        vatRegNumber = Some("Some Reg"),
        registeredForSA = Some(YesNoOrUnsure.No),
        sautr = Some("UTR"),
        address = Some(address)
      )
      val updatedUserAnswers = sut.aboutTheEstateToUserAnswers(aboutTheEstate, emptyUA).success.value
      updatedUserAnswers.get(WhatWasTheNameOfThePersonWhoDiedPage)        shouldEqual Some("Some name")
      updatedUserAnswers.get(WhatWasThePersonDateOfBirthPage)             shouldEqual Some(localDate)
      updatedUserAnswers.get(WhatWasThePersonOccupationPage)              shouldEqual Some("Occupation")
      updatedUserAnswers.get(DidThePersonHaveNINOPage)                    shouldEqual Some(DidThePersonHaveNINO.YesIKnow)
      updatedUserAnswers.get(WhatWasThePersonNINOPage)                    shouldEqual Some("Some NINO")
      updatedUserAnswers.get(WasThePersonRegisteredForVATPage)            shouldEqual Some(WasThePersonRegisteredForVAT.YesButIDontKnow)
      updatedUserAnswers.get(WhatWasThePersonVATRegistrationNumberPage)   shouldEqual Some("Some Reg")
      updatedUserAnswers.get(WasThePersonRegisteredForSAPage)             shouldEqual Some(WasThePersonRegisteredForSA.No)
      updatedUserAnswers.get(WhatWasThePersonUTRPage)                     shouldEqual Some("UTR")
      updatedUserAnswers.get(EstateAddressLookupPage)                     shouldEqual Some(address)
    }
  }

}