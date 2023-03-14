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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import models.store.notification._
import models.store.Metadata
import pages._
import models._
import models.address._
import models.store._
import org.scalatest.TryValues
import java.time.{LocalDate, Instant, LocalDateTime}

class UAToNotificationServiceSpec extends AnyWordSpec with Matchers with TryValues {

  val sut = new UAToNotificationServiceImpl

  val metadata = Metadata(reference = Some("123"), submissionTime = Some(LocalDateTime.now))
  val instant = Instant.now()
  val testNotification = Notification("userId", "submissionId", instant, metadata, PersonalDetails(Background(), AboutYou()))
  val address = Address("line 1", Some("line 2"), Some("line 3"), Some("line 4"), Some("postcode"), Country("GBR"))
  val emptyUA = UserAnswers("id")
  val userAnswers = UserAnswers("userId", "submissionId", lastUpdated = instant, metadata = metadata, created = testNotification.created)

  "userAnswersToNotification" should {
    val result = sut.userAnswersToNotification(userAnswers)

    "get the Id from UserAnswers" in {
      result.userId shouldEqual "userId"
    }

    "get the submissionId from UserAnswers" in {
      result.submissionId shouldEqual "submissionId"
    }

    "get the lastUpdated from UserAnswers" in {
      result.lastUpdated shouldEqual instant
    }

  }

  "userAnswerToDisclosureEntity" should {

    "populate DisclosureEntity for an individual when are you the entity is populated" in {
      val pages = List(PageWithValue(RelatesToPage, RelatesTo.AnIndividual), PageWithValue(AreYouTheIndividualPage, true))
      val userAnswers = PageWithValue.pagesToUserAnswers(pages, emptyUA).success.value
      sut.userAnswerToDisclosureEntity(userAnswers) shouldEqual Some(DisclosureEntity(Individual, Some(true)))
    }

    "populate DisclosureEntity for an individual when are you the entity is NOT populated" in {
      val pages = List(PageWithValue(RelatesToPage, RelatesTo.AnIndividual))
      val userAnswers = PageWithValue.pagesToUserAnswers(pages, emptyUA).success.value
      sut.userAnswerToDisclosureEntity(userAnswers) shouldEqual Some(DisclosureEntity(Individual, None))
    }

    "populate DisclosureEntity for a Company when are you the entity is populated" in {
      val pages = List(PageWithValue(RelatesToPage, RelatesTo.ACompany), PageWithValue(AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage, true))
      val userAnswers = PageWithValue.pagesToUserAnswers(pages, emptyUA).success.value
      sut.userAnswerToDisclosureEntity(userAnswers) shouldEqual Some(DisclosureEntity(Company, Some(true)))
    }

    "populate DisclosureEntity for anCompany when are you the entity is NOT populated" in {
      val pages = List(PageWithValue(RelatesToPage, RelatesTo.ACompany))
      val userAnswers = PageWithValue.pagesToUserAnswers(pages, emptyUA).success.value
      sut.userAnswerToDisclosureEntity(userAnswers) shouldEqual Some(DisclosureEntity(Company, None))
    }

    "populate DisclosureEntity for a Trust when are you the entity is populated" in {
      val pages = List(PageWithValue(RelatesToPage, RelatesTo.ATrust), PageWithValue(AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage, true))
      val userAnswers = PageWithValue.pagesToUserAnswers(pages, emptyUA).success.value
      sut.userAnswerToDisclosureEntity(userAnswers) shouldEqual Some(DisclosureEntity(Trust, Some(true)))
    }

    "populate DisclosureEntity for a Trust when are you the entity is NOT populated" in {
      val pages = List(PageWithValue(RelatesToPage, RelatesTo.ATrust))
      val userAnswers = PageWithValue.pagesToUserAnswers(pages, emptyUA).success.value
      sut.userAnswerToDisclosureEntity(userAnswers) shouldEqual Some(DisclosureEntity(Trust, None))
    }

    "populate DisclosureEntity for a LLP when are you the entity is populated" in {
      val pages = List(PageWithValue(RelatesToPage, RelatesTo.ALimitedLiabilityPartnership), PageWithValue(AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage, true))
      val userAnswers = PageWithValue.pagesToUserAnswers(pages, emptyUA).success.value
      sut.userAnswerToDisclosureEntity(userAnswers) shouldEqual Some(DisclosureEntity(LLP, Some(true)))
    }

    "populate DisclosureEntity for a LLP when are you the entity is NOT populated" in {
      val pages = List(PageWithValue(RelatesToPage, RelatesTo.ALimitedLiabilityPartnership))
      val userAnswers = PageWithValue.pagesToUserAnswers(pages, emptyUA).success.value
      sut.userAnswerToDisclosureEntity(userAnswers) shouldEqual Some(DisclosureEntity(LLP, None))
    }

    "populate DisclosureEntity for a Estate when are you the entity is populated" in {
      val pages = List(PageWithValue(RelatesToPage, RelatesTo.AnEstate), PageWithValue(AreYouTheExecutorOfTheEstatePage, true))
      val userAnswers = PageWithValue.pagesToUserAnswers(pages, emptyUA).success.value
      sut.userAnswerToDisclosureEntity(userAnswers) shouldEqual Some(DisclosureEntity(Estate, Some(true)))
    }

    "populate DisclosureEntity for a Estate when are you the entity is NOT populated" in {
      val pages = List(PageWithValue(RelatesToPage, RelatesTo.AnEstate))
      val userAnswers = PageWithValue.pagesToUserAnswers(pages, emptyUA).success.value
      sut.userAnswerToDisclosureEntity(userAnswers) shouldEqual Some(DisclosureEntity(Estate, None))
    }

  }

  "userAnswersToBackground" should {

    "populate background with nothing if nothing is set" in {
      sut.userAnswersToBackground(emptyUA) shouldEqual Background()
    }

    "populate background with everything that is set" in {
      val incomeSet: Set[IncomeOrGainSource] = Set(IncomeOrGainSource.Dividends)
      val pages = List(
        PageWithValue(ReceivedALetterPage, true),
        PageWithValue(LetterReferencePage, "Some ref"),
        PageWithValue(AreYouRepresentingAnOrganisationPage, true),
        PageWithValue(WhatIsTheNameOfTheOrganisationYouRepresentPage, "Some org"),
        PageWithValue(OffshoreLiabilitiesPage, true),
        PageWithValue(OnshoreLiabilitiesPage, false), 
        PageWithValue(RelatesToPage, RelatesTo.AnIndividual), 
        PageWithValue(AreYouTheIndividualPage, true),
        PageWithValue(IncomeOrGainSourcePage, incomeSet),
        PageWithValue(OtherIncomeOrGainSourcePage, "Some income")
      )
      val userAnswers = PageWithValue.pagesToUserAnswers(pages, emptyUA).success.value
      val expectedBackground = Background(
        Some(true),
        Some("Some ref"),
        Some(DisclosureEntity(Individual, Some(true))),
        Some(true),
        Some("Some org"),
        Some(true),
        Some(false),
        incomeSource = Some(incomeSet),
        otherIncomeSource = Some("Some income"),
      )
      sut.userAnswersToBackground(userAnswers) shouldEqual expectedBackground
    }

  }


  "userAnswersToAboutYou" should {

    "populate AboutYou with nothing if nothing is set" in {
      sut.userAnswersToAboutYou(emptyUA) shouldEqual AboutYou()
    }

    "populate AboutYou with everything that is set" in {
      val localDate = LocalDate.now()
      val pages = List(
        PageWithValue(WhatIsYourFullNamePage, "Full name"),
        PageWithValue(YourPhoneNumberPage, "Phone number"),
        PageWithValue(YourEmailAddressPage, "Email address"),
        PageWithValue(WhatIsYourDateOfBirthPage, localDate),
        PageWithValue(WhatIsYourMainOccupationPage, "Occupation"), 
        PageWithValue(DoYouHaveNationalInsuranceNumberPage, DoYouHaveNationalInsuranceNumber.YesIKnow), 
        PageWithValue(WhatIsYourNationalInsuranceNumberPage, "NINO"),
        PageWithValue(AreYouRegisteredForVATPage, AreYouRegisteredForVAT.YesIKnow),
        PageWithValue(WhatIsYourVATRegistrationNumberPage, "Reg number"),
        PageWithValue(AreYouRegisteredForSelfAssessmentPage, AreYouRegisteredForSelfAssessment.YesIKnowMyUTR),
        PageWithValue(WhatIsYourUniqueTaxReferencePage, "UTR"),
        PageWithValue(YourAddressLookupPage, address)
      )
      val userAnswers = PageWithValue.pagesToUserAnswers(pages, emptyUA).success.value
      val expected = AboutYou(
        fullName = Some("Full name"),
        telephoneNumber = Some("Phone number"),
        emailAddress = Some("Email address"),
        dateOfBirth = Some(localDate),
        mainOccupation = Some("Occupation"),
        doYouHaveANino = Some(YesNoOrUnsure.Yes),
        nino = Some("NINO"),
        registeredForVAT = Some(YesNoOrUnsure.Yes),
        vatRegNumber = Some("Reg number"),
        registeredForSA = Some(YesNoOrUnsure.Yes),
        sautr = Some("UTR"),
        address = Some(address)
      )
      sut.userAnswersToAboutYou(userAnswers) shouldEqual expected
    }

  }

  "userAnswersToAboutTheIndividual" should {

    "populate AboutTheIndividual with nothing if nothing is set" in {
      sut.userAnswersToAboutTheIndividual(emptyUA) shouldEqual AboutTheIndividual()
    }

    "populate AboutTheIndividual with everything that is set" in {
      val localDate = LocalDate.now()
      val pages = List(
        PageWithValue(WhatIsTheIndividualsFullNamePage, "Full name"),
        PageWithValue(WhatIsTheIndividualDateOfBirthPage, localDate),
        PageWithValue(WhatIsTheIndividualOccupationPage, "Occupation"), 
        PageWithValue(DoesTheIndividualHaveNationalInsuranceNumberPage, DoesTheIndividualHaveNationalInsuranceNumber.YesIKnow), 
        PageWithValue(WhatIsIndividualsNationalInsuranceNumberPage, "NINO"),
        PageWithValue(IsTheIndividualRegisteredForVATPage, IsTheIndividualRegisteredForVAT.YesIKnow),
        PageWithValue(WhatIsTheIndividualsVATRegistrationNumberPage, "Reg number"),
        PageWithValue(IsTheIndividualRegisteredForSelfAssessmentPage, IsTheIndividualRegisteredForSelfAssessment.YesIKnow),
        PageWithValue(WhatIsTheIndividualsUniqueTaxReferencePage, "UTR"),
        PageWithValue(IndividualAddressLookupPage, address)
      )
      val userAnswers = PageWithValue.pagesToUserAnswers(pages, emptyUA).success.value
      val expected = AboutTheIndividual(
        fullName = Some("Full name"),
        dateOfBirth = Some(localDate),
        mainOccupation = Some("Occupation"),
        doTheyHaveANino = Some(YesNoOrUnsure.Yes),
        nino = Some("NINO"),
        registeredForVAT = Some(YesNoOrUnsure.Yes),
        vatRegNumber = Some("Reg number"),
        registeredForSA = Some(YesNoOrUnsure.Yes),
        sautr = Some("UTR"),
        address = Some(address)
      )
      sut.userAnswersToAboutTheIndividual(userAnswers) shouldEqual expected
    }

  }

  "userAnswersToAboutTheEstate" should {

    "populate AboutTheEstate with nothing if nothing is set" in {
      sut.userAnswersToAboutTheEstate(emptyUA) shouldEqual AboutTheEstate()
    }

    "populate AboutTheIndividual with everything that is set" in {
      val localDate = LocalDate.now()
      val pages = List(
        PageWithValue(WhatWasTheNameOfThePersonWhoDiedPage, "Full name"),
        PageWithValue(WhatWasThePersonDateOfBirthPage, localDate),
        PageWithValue(WhatWasThePersonOccupationPage, "Occupation"), 
        PageWithValue(DidThePersonHaveNINOPage, DidThePersonHaveNINO.YesIKnow), 
        PageWithValue(WhatWasThePersonNINOPage, "NINO"),
        PageWithValue(WasThePersonRegisteredForVATPage, WasThePersonRegisteredForVAT.YesIKnow),
        PageWithValue(WhatWasThePersonVATRegistrationNumberPage, "Reg number"),
        PageWithValue(WasThePersonRegisteredForSAPage, WasThePersonRegisteredForSA.YesIKnow),
        PageWithValue(WhatWasThePersonUTRPage, "UTR"),
        PageWithValue(EstateAddressLookupPage, address)
      )
      val userAnswers = PageWithValue.pagesToUserAnswers(pages, emptyUA).success.value
      val expected = AboutTheEstate(
        fullName = Some("Full name"),
        dateOfBirth = Some(localDate),
        mainOccupation = Some("Occupation"),
        doTheyHaveANino = Some(YesNoOrUnsure.Yes),
        nino = Some("NINO"),
        registeredForVAT = Some(YesNoOrUnsure.Yes),
        vatRegNumber = Some("Reg number"),
        registeredForSA = Some(YesNoOrUnsure.Yes),
        sautr = Some("UTR"),
        address = Some(address)
      )
      sut.userAnswersToAboutTheEstate(userAnswers) shouldEqual expected
    }

  }

  "userAnswersToAboutTheCompany" should {

    "populate AboutTheCompany with nothing if nothing is set" in {
      sut.userAnswersToAboutTheCompany(emptyUA) shouldEqual AboutTheCompany()
    }

    "populate AboutTheCompany with everything that is set" in {
      val pages = List(
        PageWithValue(WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage, "Name"),
        PageWithValue(WhatIsTheCompanyRegistrationNumberPage, "Reg"),
        PageWithValue(CompanyAddressLookupPage, address)
      )
      val userAnswers = PageWithValue.pagesToUserAnswers(pages, emptyUA).success.value
      val expected = AboutTheCompany(
        name = Some("Name"),
        registrationNumber = Some("Reg"),
        address = Some(address)
      )
      sut.userAnswersToAboutTheCompany(userAnswers) shouldEqual expected
    }

  }

  "userAnswersToAboutTheTrust" should {

    "populate AboutTheTrust with nothing if nothing is set" in {
      sut.userAnswersToAboutTheTrust(emptyUA) shouldEqual AboutTheTrust()
    }

    "populate AboutTheTrust with everything that is set" in {
      val pages = List(
        PageWithValue(WhatIsTheTrustNamePage, "Name"),
        PageWithValue(TrustAddressLookupPage, address)
      )
      val userAnswers = PageWithValue.pagesToUserAnswers(pages, emptyUA).success.value
      val expected = AboutTheTrust(
        name = Some("Name"),
        address = Some(address)
      )
      sut.userAnswersToAboutTheTrust(userAnswers) shouldEqual expected
    }

  }


  "userAnswersToAboutTheLLP" should {

    "populate AboutTheLLP with nothing if nothing is set" in {
      sut.userAnswersToAboutTheLLP(emptyUA) shouldEqual AboutTheLLP()
    }

    "populate AboutTheLLP with everything that is set" in {
      val pages = List(
        PageWithValue(WhatIsTheLLPNamePage, "Name"),
        PageWithValue(LLPAddressLookupPage, address)
      )
      val userAnswers = PageWithValue.pagesToUserAnswers(pages, emptyUA).success.value
      val expected = AboutTheLLP(
        name = Some("Name"),
        address = Some(address)
      )
      sut.userAnswersToAboutTheLLP(userAnswers) shouldEqual expected
    }

  }

  "userAnswersToNotification" should {

    "populate AboutTheIndividual when RelatesTo is set to AnIndividual and AreYouTheIndividual is set to false" in {
      val pages = List(
        PageWithValue(RelatesToPage, RelatesTo.AnIndividual),
        PageWithValue(AreYouTheIndividualPage, false)
      )
      val ua = PageWithValue.pagesToUserAnswers(pages, userAnswers).success.value
      val personalDetails = testNotification.personalDetails.copy(background = Background(disclosureEntity = Some(DisclosureEntity(Individual, Some(false)))), aboutTheIndividual = Some(AboutTheIndividual()))
      val expected = testNotification.copy(personalDetails = personalDetails)
      sut.userAnswersToNotification(ua) shouldEqual expected
    }

    " dont populate AboutTheIndividual when RelatesTo is set to AnIndividual and AreYouTheIndividual is set to true" in {
      val pages = List(
        PageWithValue(RelatesToPage, RelatesTo.AnIndividual),
        PageWithValue(AreYouTheIndividualPage, true)
      )
      val ua = PageWithValue.pagesToUserAnswers(pages, userAnswers).success.value
      val personalDetails = testNotification.personalDetails.copy(background = Background(disclosureEntity = Some(DisclosureEntity(Individual, Some(true)))), aboutTheIndividual = None)
      val expected = testNotification.copy(personalDetails = personalDetails)
      sut.userAnswersToNotification(ua) shouldEqual expected
    }

    "populate AboutTheCompany when RelatesTo is set to ACompany" in {
      val pages = List(
        PageWithValue(RelatesToPage, RelatesTo.ACompany)
      )
      val ua = PageWithValue.pagesToUserAnswers(pages, userAnswers).success.value
      val personalDetails = testNotification.personalDetails.copy(background = Background(disclosureEntity = Some(DisclosureEntity(Company, None))), aboutTheCompany = Some(AboutTheCompany()))
      val expected = testNotification.copy(personalDetails = personalDetails)
      sut.userAnswersToNotification(ua) shouldEqual expected
    }

    "populate AboutTheTrust when RelatesTo is set to ATrust" in {
      val pages = List(
        PageWithValue(RelatesToPage, RelatesTo.ATrust)
      )
      val ua = PageWithValue.pagesToUserAnswers(pages, userAnswers).success.value
      val personalDetails = testNotification.personalDetails.copy(background = Background(disclosureEntity = Some(DisclosureEntity(Trust, None))), aboutTheTrust = Some(AboutTheTrust()))
      val expected = testNotification.copy(personalDetails = personalDetails)
      sut.userAnswersToNotification(ua) shouldEqual expected
    }

    "populate AboutTheLLP when RelatesTo is set to ALimitedLiabilityPartnership" in {
      val pages = List(
        PageWithValue(RelatesToPage, RelatesTo.ALimitedLiabilityPartnership)
      )
      val ua = PageWithValue.pagesToUserAnswers(pages, userAnswers).success.value
      val personalDetails = testNotification.personalDetails.copy(background = Background(disclosureEntity = Some(DisclosureEntity(LLP, None))), aboutTheLLP = Some(AboutTheLLP()))
      val expected = testNotification.copy(personalDetails = personalDetails)
      sut.userAnswersToNotification(ua) shouldEqual expected
    }

  }

}