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

package controllers.notification

import base.SpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.govuk.SummaryListFluency
import views.html.notification.CheckYourAnswersView
import viewmodels.checkAnswers._
import models._
import models.address.Address
import pages._
import play.api.i18n.Messages
import org.scalacheck.Arbitrary.arbitrary

import java.time.LocalDate

class CheckYourAnswersControllerSpec extends SpecBase with SummaryListFluency {


  "Check Your Answers Controller" - {

    "must return OK and the correct view for a GET when userAnswers is empty" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckYourAnswersView]
        val backgroundList = SummaryListViewModel(Seq.empty)
        val aboutYouList = SummaryListViewModel(Seq.empty)
        val aboutTheIndividualList = None
        val aboutTheCompanyList = None
        val list = SummaryLists(backgroundList, aboutYouList, aboutTheIndividualList, aboutTheCompanyList)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(list)(request, messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad.url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    def rowIsDisplayedWhenPageIsPopulated(ua: UserAnswers)(summaryList: Messages => SummaryLists) = {
      val application = applicationBuilder(userAnswers = Some(ua)).build()
      implicit val mess = messages(application)
      val list = summaryList(mess)

      running(application) {
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckYourAnswersView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(list)(request, messages(application)).toString
      }
    }

    "must return OK and the correct view for a GET when ReceivedALetterPage is populated" in {
      val ua = UserAnswers("id").set(ReceivedALetterPage, arbitrary[Boolean].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(SummaryListViewModel(Seq(ReceivedALetterSummary.row(ua)(messages)).flatten), SummaryListViewModel(Seq.empty)))
    }

    "must return OK and the correct view for a GET when LetterReferencePage is populated" in {
      val ua = UserAnswers("id").set(LetterReferencePage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(SummaryListViewModel(Seq(LetterReferenceSummary.row(ua)(messages)).flatten), SummaryListViewModel(Seq.empty)))
    }

    "must return OK and the correct view for a GET when RelatesTo (An Individual) is populated" in {
      val uaWithRelatesTo = UserAnswers("id").set(RelatesToPage, RelatesTo.AnIndividual).success.value
      rowIsDisplayedWhenPageIsPopulated(uaWithRelatesTo)(messages => SummaryLists(
        SummaryListViewModel(Seq(RelatesToSummary.row(uaWithRelatesTo)(messages)).flatten), 
        SummaryListViewModel(Seq.empty)
      ))
    }

    "must return OK and the correct view for a GET when RelatesTo (A Company) is populated" in {
      val uaWithRelatesTo = UserAnswers("id").set(RelatesToPage, RelatesTo.ACompany).success.value
      rowIsDisplayedWhenPageIsPopulated(uaWithRelatesTo)(messages => SummaryLists(
        SummaryListViewModel(Seq(RelatesToSummary.row(uaWithRelatesTo)(messages)).flatten), 
        SummaryListViewModel(Seq.empty),
        aboutTheCompanyList = Some(SummaryListViewModel(Seq.empty))
      ))
    }

    "must return OK and the correct view for a GET when RelatesTo (An Estate) is populated" in {
      val uaWithRelatesTo = UserAnswers("id").set(RelatesToPage, RelatesTo.AnEstate).success.value
      rowIsDisplayedWhenPageIsPopulated(uaWithRelatesTo)(messages => SummaryLists(
        SummaryListViewModel(Seq(RelatesToSummary.row(uaWithRelatesTo)(messages)).flatten), 
        SummaryListViewModel(Seq.empty)
      ))
    }

    "must return OK and the correct view for a GET when RelatesTo (A Limited Liability Partnership) is populated" in {
      val uaWithRelatesTo = UserAnswers("id").set(RelatesToPage, RelatesTo.ALimitedLiabilityPartnership).success.value
      rowIsDisplayedWhenPageIsPopulated(uaWithRelatesTo)(messages => SummaryLists(
        SummaryListViewModel(Seq(RelatesToSummary.row(uaWithRelatesTo)(messages)).flatten), 
        SummaryListViewModel(Seq.empty),
        aboutTheLLPList = Some(SummaryListViewModel(Seq.empty))
      ))
    }

    "must return OK and the correct view for a GET when RelatesTo (A Trust) is populated" in {
      val uaWithRelatesTo = UserAnswers("id").set(RelatesToPage, RelatesTo.ATrust).success.value
      rowIsDisplayedWhenPageIsPopulated(uaWithRelatesTo)(messages => SummaryLists(
        SummaryListViewModel(Seq(RelatesToSummary.row(uaWithRelatesTo)(messages)).flatten),
        SummaryListViewModel(Seq.empty),
        aboutTheTrustList = Some(SummaryListViewModel(Seq.empty))
      ))
    }

    "must return OK and the correct view for a GET when AreYouTheIndividualPage with no is populated" in {
      val ua = UserAnswers("id").set(AreYouTheIndividualPage, AreYouTheIndividual.No).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        SummaryListViewModel(Seq(AreYouTheIndividualSummary.row(ua)(messages)).flatten), 
        SummaryListViewModel(Seq.empty),
        Some(SummaryListViewModel(Seq.empty)) 
      ))
    }

    "must return OK and the correct view for a GET when AreYouTheIndividualPage with yes is populated" in {
      val ua = UserAnswers("id").set(AreYouTheIndividualPage, AreYouTheIndividual.Yes).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        SummaryListViewModel(Seq(AreYouTheIndividualSummary.row(ua)(messages)).flatten),
        SummaryListViewModel(Seq.empty),
        None
      ))
    }

    "must return OK and the correct view for a GET when AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage is populated" in {
      val ua = UserAnswers("id").set(AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage, arbitrary[AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAbout].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        SummaryListViewModel(Seq(AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutSummary.row(ua)(messages)).flatten),
        SummaryListViewModel(Seq.empty),
        None
      ))
    }

    "must return OK and the correct view for a GET when AreYouRepresentingAnOrganisationPage is populated" in {
      val ua = UserAnswers("id").set(AreYouRepresentingAnOrganisationPage, arbitrary[Boolean].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        SummaryListViewModel(Seq(AreYouRepresentingAnOrganisationSummary.row(ua)(messages)).flatten),
        SummaryListViewModel(Seq.empty),
        None
      ))
    }

    "must return OK and the correct view for a GET when WhatIsTheNameOfTheOrganisationYouRepresentPage is populated" in {
      val ua = UserAnswers("id").set(WhatIsTheNameOfTheOrganisationYouRepresentPage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        SummaryListViewModel(Seq(WhatIsTheNameOfTheOrganisationYouRepresentSummary.row(ua)(messages)).flatten),
        SummaryListViewModel(Seq.empty),
        None
      ))
    }

    "must return OK and the correct view for a GET when OffshoreLiabilitiesPage is populated" in {
      val ua = UserAnswers("id").set(OffshoreLiabilitiesPage,  OffshoreLiabilities.IWantTo).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(SummaryListViewModel(Seq(OffshoreLiabilitiesSummary.row(ua)(messages)).flatten), SummaryListViewModel(Seq.empty)))
    }

    "must return OK and the correct view for a GET when OnshoreLiabilitiesPage is populated" in {
      val ua = UserAnswers("id").set(OnshoreLiabilitiesPage, arbitrary[OnshoreLiabilities].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(SummaryListViewModel(Seq(OnshoreLiabilitiesSummary.row(ua)(messages)).flatten), SummaryListViewModel(Seq.empty)))
    }

    "must return OK and the correct view for a GET when YourPhoneNumberPage is populated" in {
      val ua = UserAnswers("id").set(YourPhoneNumberPage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq.empty),
        aboutYou = SummaryListViewModel(Seq(YourPhoneNumberSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when DoYouHaveAnEmailAddressPage is populated" in {
      val ua = UserAnswers("id").set(DoYouHaveAnEmailAddressPage, arbitrary[Boolean].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq.empty),
        aboutYou = SummaryListViewModel(Seq(DoYouHaveAnEmailAddressSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when YourEmailAddressPage is populated" in {
      val ua = UserAnswers("id").set(YourEmailAddressPage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq.empty),
        aboutYou = SummaryListViewModel(Seq(YourEmailAddressSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when YourAddressLookupPage is populated" in {
      val ua = UserAnswers("id").set(YourAddressLookupPage, arbitrary[Address].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq.empty),
        aboutYou = SummaryListViewModel(Seq(YourAddressLookupSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when WhatIsYourDateOfBirthPage is populated" in {
      val ua = UserAnswers("id").set(WhatIsYourDateOfBirthPage, arbitrary[LocalDate].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq.empty),
        aboutYou = SummaryListViewModel(Seq(WhatIsYourDateOfBirthSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when WhatIsYourMainOccupationPage is populated" in {
      val ua = UserAnswers("id").set( WhatIsYourMainOccupationPage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq.empty),
        aboutYou = SummaryListViewModel(Seq(WhatIsYourMainOccupationSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when DoYouHaveNationalInsuranceNumberPage is populated" in {
      val ua = UserAnswers("id").set( DoYouHaveNationalInsuranceNumberPage, arbitrary[DoYouHaveNationalInsuranceNumber].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq.empty),
        aboutYou = SummaryListViewModel(Seq(DoYouHaveNationalInsuranceNumberSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when WhatIsYourNationalInsuranceNumberPage is populated" in {
      val ua = UserAnswers("id").set( WhatIsYourNationalInsuranceNumberPage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq.empty),
        aboutYou = SummaryListViewModel(Seq(WhatIsYourNationalInsuranceNumberSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when AreYouRegisteredForVATPage is populated" in {
      val ua = UserAnswers("id").set( AreYouRegisteredForVATPage, arbitrary[AreYouRegisteredForVAT].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq.empty),
        aboutYou = SummaryListViewModel(Seq(AreYouRegisteredForVATSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when WhatIsYourVATRegistrationNumberPage is populated" in {
      val ua = UserAnswers("id").set( WhatIsYourVATRegistrationNumberPage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq.empty),
        aboutYou = SummaryListViewModel(Seq(WhatIsYourVATRegistrationNumberSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when AreYouRegisteredForSelfAssessmentPage is populated" in {
      val ua = UserAnswers("id").set( AreYouRegisteredForSelfAssessmentPage, arbitrary[AreYouRegisteredForSelfAssessment].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq.empty),
        aboutYou = SummaryListViewModel(Seq(AreYouRegisteredForSelfAssessmentSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when WhatIsYourUniqueTaxReferencePage is populated" in {
      val ua = UserAnswers("id").set( WhatIsYourUniqueTaxReferencePage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq.empty),
        aboutYou = SummaryListViewModel(Seq(WhatIsYourUniqueTaxReferenceSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when WhatIsYourFullNamePage is populated" in {
      val ua = UserAnswers("id").set( WhatIsYourFullNamePage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq.empty),
        aboutYou = SummaryListViewModel(Seq(WhatIsYourFullNameSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when WhatIsTheIndividualsFullNamePage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id").set(WhatIsTheIndividualsFullNamePage, arbitrary[String].sample.value)
        uaWithAreYouTheIndividualPage <- userAnswer.set(AreYouTheIndividualPage, AreYouTheIndividual.No)
      } yield uaWithAreYouTheIndividualPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(AreYouTheIndividualSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty), 
        aboutTheIndividualList = Some(SummaryListViewModel(Seq(WhatIsTheIndividualsFullNameSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when WhatIsTheIndividualDateOfBirthPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id").set(WhatIsTheIndividualDateOfBirthControllerPage, arbitrary[LocalDate].sample.value)
        uaWithAreYouTheIndividualPage <- userAnswer.set(AreYouTheIndividualPage, AreYouTheIndividual.No)
      } yield uaWithAreYouTheIndividualPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(AreYouTheIndividualSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty), 
        aboutTheIndividualList = Some(SummaryListViewModel(Seq(WhatIsTheIndividualDateOfBirthControllerSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when WhatIsTheIndividualOccupationPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id").set(WhatIsTheIndividualOccupationPage, arbitrary[String].sample.value)
        uaWithAreYouTheIndividualPage <- userAnswer.set(AreYouTheIndividualPage, AreYouTheIndividual.No)
      } yield uaWithAreYouTheIndividualPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(AreYouTheIndividualSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty), 
        aboutTheIndividualList = Some(SummaryListViewModel(Seq(WhatIsTheIndividualOccupationSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when DoesTheIndividualHaveNationalInsuranceNumberPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id").set(DoesTheIndividualHaveNationalInsuranceNumberPage, arbitrary[DoesTheIndividualHaveNationalInsuranceNumber].sample.value)
        uaWithAreYouTheIndividualPage <- userAnswer.set(AreYouTheIndividualPage, AreYouTheIndividual.No)
      } yield uaWithAreYouTheIndividualPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(AreYouTheIndividualSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty), 
        aboutTheIndividualList = Some(SummaryListViewModel(Seq(DoesTheIndividualHaveNationalInsuranceNumberSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when WhatIsIndividualsNationalInsuranceNumberPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id").set(WhatIsIndividualsNationalInsuranceNumberPage, arbitrary[String].sample.value)
        uaWithAreYouTheIndividualPage <- userAnswer.set(AreYouTheIndividualPage, AreYouTheIndividual.No)
      } yield uaWithAreYouTheIndividualPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(AreYouTheIndividualSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty), 
        aboutTheIndividualList = Some(SummaryListViewModel(Seq(WhatIsIndividualsNationalInsuranceNumberSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when IsTheIndividualRegisteredForVATPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id").set(IsTheIndividualRegisteredForVATPage, arbitrary[IsTheIndividualRegisteredForVAT].sample.value)
        uaWithAreYouTheIndividualPage <- userAnswer.set(AreYouTheIndividualPage, AreYouTheIndividual.No)
      } yield uaWithAreYouTheIndividualPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(AreYouTheIndividualSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty), 
        aboutTheIndividualList = Some(SummaryListViewModel(Seq(IsTheIndividualRegisteredForVATSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when WhatIsTheIndividualsVATRegistrationNumberPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id").set(WhatIsTheIndividualsVATRegistrationNumberPage, arbitrary[String].sample.value)
        uaWithAreYouTheIndividualPage <- userAnswer.set(AreYouTheIndividualPage, AreYouTheIndividual.No)
      } yield uaWithAreYouTheIndividualPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(AreYouTheIndividualSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty), 
        aboutTheIndividualList = Some(SummaryListViewModel(Seq(WhatIsTheIndividualsVATRegistrationNumberSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when IsTheIndividualRegisteredForSelfAssessmentPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id").set(IsTheIndividualRegisteredForSelfAssessmentPage, arbitrary[IsTheIndividualRegisteredForSelfAssessment].sample.value)
        uaWithAreYouTheIndividualPage <- userAnswer.set(AreYouTheIndividualPage, AreYouTheIndividual.No)
      } yield uaWithAreYouTheIndividualPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(AreYouTheIndividualSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty), 
        aboutTheIndividualList = Some(SummaryListViewModel(Seq(IsTheIndividualRegisteredForSelfAssessmentSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when WhatIsTheIndividualsUniqueTaxReferencePage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id").set(WhatIsTheIndividualsUniqueTaxReferencePage, arbitrary[String].sample.value)
        uaWithAreYouTheIndividualPage <- userAnswer.set(AreYouTheIndividualPage, AreYouTheIndividual.No)
      } yield uaWithAreYouTheIndividualPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(AreYouTheIndividualSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty), 
        aboutTheIndividualList = Some(SummaryListViewModel(Seq(WhatIsTheIndividualsUniqueTaxReferenceSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when IndividualAddressLookupPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id").set(IndividualAddressLookupPage, arbitrary[Address].sample.value)
        uaWithAreYouTheIndividualPage <- userAnswer.set(AreYouTheIndividualPage, AreYouTheIndividual.No)
      } yield uaWithAreYouTheIndividualPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(AreYouTheIndividualSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty), 
        aboutTheIndividualList = Some(SummaryListViewModel(Seq(IndividualAddressLookupSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id").set(WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage, arbitrary[String].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.ACompany)
      } yield uaWithRelatesToPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty), 
        aboutTheIndividualList = None,
        aboutTheCompanyList = Some(SummaryListViewModel(Seq(WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when WhatIsTheCompanyRegistrationNumberPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id").set(WhatIsTheCompanyRegistrationNumberPage, arbitrary[String].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.ACompany)
      } yield uaWithRelatesToPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty), 
        aboutTheIndividualList = None,
        aboutTheCompanyList = Some(SummaryListViewModel(Seq(WhatIsTheCompanyRegistrationNumberSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when CompanyAddressLookupPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id").set(CompanyAddressLookupPage, arbitrary[Address].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.ACompany)
      } yield uaWithRelatesToPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty), 
        aboutTheIndividualList = None,
        aboutTheCompanyList = Some(SummaryListViewModel(Seq(CompanyAddressLookupSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when WhatIsTheLLPNamePage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id").set(WhatIsTheLLPNamePage, arbitrary[String].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.ALimitedLiabilityPartnership)
      } yield uaWithRelatesToPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty), 
        aboutTheIndividualList = None,
        aboutTheCompanyList = None,
        aboutTheLLPList = Some(SummaryListViewModel(Seq(WhatIsTheLLPNameSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when LLPAddressLookupPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id").set(LLPAddressLookupPage, arbitrary[Address].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.ALimitedLiabilityPartnership)
      } yield uaWithRelatesToPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty),
        aboutTheIndividualList = None,
        aboutTheCompanyList = None,
        aboutTheLLPList = Some(SummaryListViewModel(Seq(LLPAddressLookupSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when WhatIsTheTrustNamePage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id").set(WhatIsTheTrustNamePage, arbitrary[String].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.ATrust)
      } yield uaWithRelatesToPage).success.value

      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages)).flatten),
        aboutYou = SummaryListViewModel(Seq.empty),
        aboutTheIndividualList = None,
        aboutTheCompanyList = None,
        aboutTheLLPList = None,
        aboutTheTrustList = Some(SummaryListViewModel(Seq(WhatIsTheTrustNameSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when TrustAddressLookupPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id").set(TrustAddressLookupPage, arbitrary[Address].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.ATrust)
      } yield uaWithRelatesToPage).success.value

      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages)).flatten),
        aboutYou = SummaryListViewModel(Seq.empty),
        aboutTheIndividualList = None,
        aboutTheCompanyList = None,
        aboutTheLLPList = None,
        aboutTheTrustList = Some(SummaryListViewModel(Seq(TrustAddressLookupSummary.row(ua)(messages)).flatten))
      ))
    }

  }
}
