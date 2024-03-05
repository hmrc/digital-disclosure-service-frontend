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
      implicit val mess: Messages = messages(application)
      val ua = UserAnswers("id", "session-123")

      running(application) {
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckYourAnswersView]
        val backgroundList = SummaryListViewModel(Seq(LiabilitiesSummary.row(ua)(mess)).flatten)
        val aboutYouList = SummaryListViewModel(Seq.empty)
        val aboutTheIndividualList = None
        val aboutTheCompanyList = None
        val aboutThePersonWhoDiedList = None
        val list = SummaryLists(backgroundList, aboutYouList, aboutTheIndividualList, aboutTheCompanyList, aboutThePersonWhoDiedList)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(list, true, true, false)(request, mess).toString
      }
    }

    "must redirect to Index for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad.url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
      }
    }

    def rowIsDisplayedWhenPageIsPopulated(ua: UserAnswers, isTheEntity: Boolean = true)(summaryList: Messages => SummaryLists) = {
      val application = applicationBuilder(userAnswers = Some(ua)).build()
      implicit val mess: Messages = messages(application)
      val list = summaryList(mess)

      running(application) {
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckYourAnswersView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(list, isTheEntity, true, false)(request, messages(application)).toString
      }
    }

    "must return OK and the correct view for a GET when ReceivedALetterPage is populated" in {
      val ua = UserAnswers("id", "session-123").set(ReceivedALetterPage, arbitrary[Boolean].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        SummaryListViewModel(Seq(ReceivedALetterSummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten), 
        SummaryListViewModel(Seq.empty)
      ))
    }

    "must return OK and the correct view for a GET when LetterReferencePage is populated" in {
      val ua = UserAnswers("id", "session-123").set(LetterReferencePage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(SummaryListViewModel(Seq(LetterReferenceSummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten), SummaryListViewModel(Seq.empty)))
    }

    "must return OK and the correct view for a GET when RelatesTo (A Company) is populated" in {
      val uaWithRelatesTo = UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.ACompany).success.value
      rowIsDisplayedWhenPageIsPopulated(uaWithRelatesTo)(messages => SummaryLists(
        SummaryListViewModel(Seq(RelatesToSummary.row(uaWithRelatesTo)(messages), LiabilitiesSummary.row(uaWithRelatesTo)(messages)).flatten), 
        SummaryListViewModel(Seq.empty),
        aboutTheCompanyList = Some(SummaryListViewModel(Seq.empty))
      ))
    }

    "must return OK and the correct view for a GET when RelatesTo (An Estate) is populated" in {
      val uaWithRelatesTo = UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.AnEstate).success.value
      rowIsDisplayedWhenPageIsPopulated(uaWithRelatesTo)(messages => SummaryLists(
        SummaryListViewModel(Seq(RelatesToSummary.row(uaWithRelatesTo)(messages), LiabilitiesSummary.row(uaWithRelatesTo)(messages)).flatten), 
        SummaryListViewModel(Seq.empty),
        aboutThePersonWhoDiedList = Some(SummaryListViewModel(Seq.empty))
      ))
    }

    "must return OK and the correct view for a GET when RelatesTo (A Limited Liability Partnership) is populated" in {
      val uaWithRelatesTo = UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.ALimitedLiabilityPartnership).success.value
      rowIsDisplayedWhenPageIsPopulated(uaWithRelatesTo)(messages => SummaryLists(
        SummaryListViewModel(Seq(RelatesToSummary.row(uaWithRelatesTo)(messages), LiabilitiesSummary.row(uaWithRelatesTo)(messages)).flatten), 
        SummaryListViewModel(Seq.empty),
        aboutTheLLPList = Some(SummaryListViewModel(Seq.empty))
      ))
    }

    "must return OK and the correct view for a GET when RelatesTo (A Trust) is populated" in {
      val uaWithRelatesTo = UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.ATrust).success.value
      rowIsDisplayedWhenPageIsPopulated(uaWithRelatesTo)(messages => SummaryLists(
        SummaryListViewModel(Seq(RelatesToSummary.row(uaWithRelatesTo)(messages), LiabilitiesSummary.row(uaWithRelatesTo)(messages)).flatten),
        SummaryListViewModel(Seq.empty),
        aboutTheTrustList = Some(SummaryListViewModel(Seq.empty))
      ))
    }

    "must return OK and the correct view for a GET when AreYouRepresentingAnOrganisationPage is populated" in {
      val ua = UserAnswers("id", "session-123").set(AreYouRepresentingAnOrganisationPage, arbitrary[Boolean].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        SummaryListViewModel(Seq(AreYouRepresentingAnOrganisationSummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten),
        SummaryListViewModel(Seq.empty),
        None
      ))
    }

    "must return OK and the correct view for a GET when WhatIsTheNameOfTheOrganisationYouRepresentPage is populated" in {
      val ua = UserAnswers("id", "session-123").set(WhatIsTheNameOfTheOrganisationYouRepresentPage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        SummaryListViewModel(Seq(WhatIsTheNameOfTheOrganisationYouRepresentSummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten),
        SummaryListViewModel(Seq.empty),
        None
      ))
    }

    "must return OK and the correct view for a GET when LiabilitiesPage is populated" in {
      val ua = UserAnswers("id", "session-123").set(OffshoreLiabilitiesPage,  true).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(SummaryListViewModel(Seq(LiabilitiesSummary.row(ua)(messages)).flatten), SummaryListViewModel(Seq.empty)))
    }

    "must return OK and the correct view for a GET when YourPhoneNumberPage is populated" in {
      val ua = UserAnswers("id", "session-123").set(YourPhoneNumberPage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(LiabilitiesSummary.row(ua)(messages)).flatten),
        aboutYou = SummaryListViewModel(Seq(YourPhoneNumberSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when YourEmailAddressPage is populated" in {
      val ua = UserAnswers("id", "session-123").set(YourEmailAddressPage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(LiabilitiesSummary.row(ua)(messages)).flatten),
        aboutYou = SummaryListViewModel(Seq(YourEmailAddressSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when YourAddressLookupPage is populated" in {
      val ua = UserAnswers("id", "session-123").set(YourAddressLookupPage, arbitrary[Address].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(LiabilitiesSummary.row(ua)(messages)).flatten),
        aboutYou = SummaryListViewModel(Seq(YourAddressLookupSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when WhatIsYourDateOfBirthPage is populated" in {
      val ua = UserAnswers("id", "session-123").set(WhatIsYourDateOfBirthPage, arbitrary[LocalDate].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(LiabilitiesSummary.row(ua)(messages)).flatten),
        aboutYou = SummaryListViewModel(Seq(WhatIsYourDateOfBirthSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when WhatIsYourMainOccupationPage is populated" in {
      val ua = UserAnswers("id", "session-123").set( WhatIsYourMainOccupationPage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(LiabilitiesSummary.row(ua)(messages)).flatten),
        aboutYou = SummaryListViewModel(Seq(WhatIsYourMainOccupationSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when DoYouHaveNationalInsuranceNumberPage is populated" in {
      val ua = UserAnswers("id", "session-123").set( DoYouHaveNationalInsuranceNumberPage, arbitrary[DoYouHaveNationalInsuranceNumber].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(LiabilitiesSummary.row(ua)(messages)).flatten),
        aboutYou = SummaryListViewModel(Seq(DoYouHaveNationalInsuranceNumberSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when WhatIsYourNationalInsuranceNumberPage is populated" in {
      val ua = UserAnswers("id", "session-123").set( WhatIsYourNationalInsuranceNumberPage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(LiabilitiesSummary.row(ua)(messages)).flatten),
        aboutYou = SummaryListViewModel(Seq(WhatIsYourNationalInsuranceNumberSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when AreYouRegisteredForVATPage is populated" in {
      val ua = UserAnswers("id", "session-123").set( AreYouRegisteredForVATPage, arbitrary[AreYouRegisteredForVAT].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(LiabilitiesSummary.row(ua)(messages)).flatten),
        aboutYou = SummaryListViewModel(Seq(AreYouRegisteredForVATSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when WhatIsYourVATRegistrationNumberPage is populated" in {
      val ua = UserAnswers("id", "session-123").set( WhatIsYourVATRegistrationNumberPage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(LiabilitiesSummary.row(ua)(messages)).flatten),
        aboutYou = SummaryListViewModel(Seq(WhatIsYourVATRegistrationNumberSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when AreYouRegisteredForSelfAssessmentPage is populated" in {
      val ua = UserAnswers("id", "session-123").set( AreYouRegisteredForSelfAssessmentPage, arbitrary[AreYouRegisteredForSelfAssessment].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(LiabilitiesSummary.row(ua)(messages)).flatten),
        aboutYou = SummaryListViewModel(Seq(AreYouRegisteredForSelfAssessmentSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when WhatIsYourUniqueTaxReferencePage is populated" in {
      val ua = UserAnswers("id", "session-123").set( WhatIsYourUniqueTaxReferencePage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(LiabilitiesSummary.row(ua)(messages)).flatten),
        aboutYou = SummaryListViewModel(Seq(WhatIsYourUniqueTaxReferenceSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when WhatIsYourFullNamePage is populated" in {
      val ua = UserAnswers("id", "session-123").set( WhatIsYourFullNamePage, arbitrary[String].sample.value).success.value
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(LiabilitiesSummary.row(ua)(messages)).flatten),
        aboutYou = SummaryListViewModel(Seq(WhatIsYourFullNameSummary.row(ua)(messages)).flatten)
      ))
    }

    "must return OK and the correct view for a GET when WhatIsTheIndividualsFullNamePage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id", "session-123").set(WhatIsTheIndividualsFullNamePage, arbitrary[String].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
        uaWithAreYouTheEntityPage <- uaWithRelatesToPage.set(AreYouTheEntityPage, AreYouTheEntity.IAmAnAccountantOrTaxAgent)
      } yield uaWithAreYouTheEntityPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua, false)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages), AreYouTheEntitySummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty), 
        aboutTheIndividualList = Some(SummaryListViewModel(Seq(WhatIsTheIndividualsFullNameSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when WhatIsTheIndividualDateOfBirthPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id", "session-123").set(WhatIsTheIndividualDateOfBirthPage, arbitrary[LocalDate].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
        uaWithAreYouTheEntityPage <- uaWithRelatesToPage.set(AreYouTheEntityPage, AreYouTheEntity.IAmAnAccountantOrTaxAgent)
      } yield uaWithAreYouTheEntityPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua, false)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages), AreYouTheEntitySummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty), 
        aboutTheIndividualList = Some(SummaryListViewModel(Seq(WhatIsTheIndividualDateOfBirthSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when WhatIsTheIndividualOccupationPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id", "session-123").set(WhatIsTheIndividualOccupationPage, arbitrary[String].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
        uaWithAreYouTheEntityPage <- uaWithRelatesToPage.set(AreYouTheEntityPage, AreYouTheEntity.IAmAnAccountantOrTaxAgent)
      } yield uaWithAreYouTheEntityPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua, false)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages), AreYouTheEntitySummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty), 
        aboutTheIndividualList = Some(SummaryListViewModel(Seq(WhatIsTheIndividualOccupationSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when DoesTheIndividualHaveNationalInsuranceNumberPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id", "session-123").set(DoesTheIndividualHaveNationalInsuranceNumberPage, arbitrary[DoesTheIndividualHaveNationalInsuranceNumber].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
        uaWithAreYouTheEntityPage <- uaWithRelatesToPage.set(AreYouTheEntityPage, AreYouTheEntity.IAmAnAccountantOrTaxAgent)
      } yield uaWithAreYouTheEntityPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua, false)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages), AreYouTheEntitySummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty), 
        aboutTheIndividualList = Some(SummaryListViewModel(Seq(DoesTheIndividualHaveNationalInsuranceNumberSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when WhatIsIndividualsNationalInsuranceNumberPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id", "session-123").set(WhatIsIndividualsNationalInsuranceNumberPage, arbitrary[String].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
        uaWithAreYouTheEntityPage <- uaWithRelatesToPage.set(AreYouTheEntityPage, AreYouTheEntity.IAmAnAccountantOrTaxAgent)
      } yield uaWithAreYouTheEntityPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua, false)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages), AreYouTheEntitySummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty), 
        aboutTheIndividualList = Some(SummaryListViewModel(Seq(WhatIsIndividualsNationalInsuranceNumberSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when IsTheIndividualRegisteredForVATPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id", "session-123").set(IsTheIndividualRegisteredForVATPage, arbitrary[IsTheIndividualRegisteredForVAT].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
        uaWithAreYouTheEntityPage <- uaWithRelatesToPage.set(AreYouTheEntityPage, AreYouTheEntity.IAmAnAccountantOrTaxAgent)
      } yield uaWithAreYouTheEntityPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua, false)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages), AreYouTheEntitySummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty), 
        aboutTheIndividualList = Some(SummaryListViewModel(Seq(IsTheIndividualRegisteredForVATSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when WhatIsTheIndividualsVATRegistrationNumberPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id", "session-123").set(WhatIsTheIndividualsVATRegistrationNumberPage, arbitrary[String].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
        uaWithAreYouTheEntityPage <- uaWithRelatesToPage.set(AreYouTheEntityPage, AreYouTheEntity.IAmAnAccountantOrTaxAgent)
      } yield uaWithAreYouTheEntityPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua, false)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages), AreYouTheEntitySummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty), 
        aboutTheIndividualList = Some(SummaryListViewModel(Seq(WhatIsTheIndividualsVATRegistrationNumberSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when IsTheIndividualRegisteredForSelfAssessmentPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id", "session-123").set(IsTheIndividualRegisteredForSelfAssessmentPage, arbitrary[IsTheIndividualRegisteredForSelfAssessment].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
        uaWithAreYouTheEntityPage <- uaWithRelatesToPage.set(AreYouTheEntityPage, AreYouTheEntity.IAmAnAccountantOrTaxAgent)
      } yield uaWithAreYouTheEntityPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua, false)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages), AreYouTheEntitySummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty), 
        aboutTheIndividualList = Some(SummaryListViewModel(Seq(IsTheIndividualRegisteredForSelfAssessmentSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when WhatIsTheIndividualsUniqueTaxReferencePage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id", "session-123").set(WhatIsTheIndividualsUniqueTaxReferencePage, arbitrary[String].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
        uaWithAreYouTheEntityPage <- uaWithRelatesToPage.set(AreYouTheEntityPage, AreYouTheEntity.IAmAnAccountantOrTaxAgent)
      } yield uaWithAreYouTheEntityPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua, false)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages), AreYouTheEntitySummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty), 
        aboutTheIndividualList = Some(SummaryListViewModel(Seq(WhatIsTheIndividualsUniqueTaxReferenceSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when IndividualAddressLookupPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id", "session-123").set(IndividualAddressLookupPage, arbitrary[Address].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
        uaWithAreYouTheEntityPage <- uaWithRelatesToPage.set(AreYouTheEntityPage, AreYouTheEntity.IAmAnAccountantOrTaxAgent)
      } yield uaWithAreYouTheEntityPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua, false)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages), AreYouTheEntitySummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty), 
        aboutTheIndividualList = Some(SummaryListViewModel(Seq(IndividualAddressLookupSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id", "session-123").set(WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage, arbitrary[String].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.ACompany)
      } yield uaWithRelatesToPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty), 
        aboutTheIndividualList = None,
        aboutTheCompanyList = Some(SummaryListViewModel(Seq(WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when WhatIsTheCompanyRegistrationNumberPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id", "session-123").set(WhatIsTheCompanyRegistrationNumberPage, arbitrary[String].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.ACompany)
      } yield uaWithRelatesToPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty), 
        aboutTheIndividualList = None,
        aboutTheCompanyList = Some(SummaryListViewModel(Seq(WhatIsTheCompanyRegistrationNumberSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when CompanyAddressLookupPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id", "session-123").set(CompanyAddressLookupPage, arbitrary[Address].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.ACompany)
      } yield uaWithRelatesToPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty), 
        aboutTheIndividualList = None,
        aboutTheCompanyList = Some(SummaryListViewModel(Seq(CompanyAddressLookupSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when WhatIsTheLLPNamePage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id", "session-123").set(WhatIsTheLLPNamePage, arbitrary[String].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.ALimitedLiabilityPartnership)
      } yield uaWithRelatesToPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty), 
        aboutTheIndividualList = None,
        aboutTheCompanyList = None,
        aboutTheLLPList = Some(SummaryListViewModel(Seq(WhatIsTheLLPNameSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when LLPAddressLookupPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id", "session-123").set(LLPAddressLookupPage, arbitrary[Address].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.ALimitedLiabilityPartnership)
      } yield uaWithRelatesToPage).success.value
        
      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten), 
        aboutYou = SummaryListViewModel(Seq.empty),
        aboutTheIndividualList = None,
        aboutTheCompanyList = None,
        aboutTheLLPList = Some(SummaryListViewModel(Seq(LLPAddressLookupSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when WhatIsTheTrustNamePage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id", "session-123").set(WhatIsTheTrustNamePage, arbitrary[String].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.ATrust)
      } yield uaWithRelatesToPage).success.value

      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten),
        aboutYou = SummaryListViewModel(Seq.empty),
        aboutTheIndividualList = None,
        aboutTheCompanyList = None,
        aboutTheLLPList = None,
        aboutTheTrustList = Some(SummaryListViewModel(Seq(WhatIsTheTrustNameSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when TrustAddressLookupPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id", "session-123").set(TrustAddressLookupPage, arbitrary[Address].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.ATrust)
      } yield uaWithRelatesToPage).success.value

      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten),
        aboutYou = SummaryListViewModel(Seq.empty),
        aboutTheIndividualList = None,
        aboutTheCompanyList = None,
        aboutTheLLPList = None,
        aboutTheTrustList = Some(SummaryListViewModel(Seq(TrustAddressLookupSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when WhatWasTheNameOfThePersonWhoDiedPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id", "session-123").set(WhatWasTheNameOfThePersonWhoDiedPage, arbitrary[String].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnEstate)
      } yield uaWithRelatesToPage).success.value

      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten),
        aboutYou = SummaryListViewModel(Seq.empty),
        aboutTheIndividualList = None,
        aboutTheCompanyList = None,
        aboutTheLLPList = None,
        aboutTheTrustList = None,
        aboutThePersonWhoDiedList = Some(SummaryListViewModel(Seq(WhatWasTheNameOfThePersonWhoDiedSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when WhatWasThePersonDateOfBirthPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id", "session-123").set(WhatWasThePersonDateOfBirthPage, arbitrary[LocalDate].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnEstate)
      } yield uaWithRelatesToPage).success.value

      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten),
        aboutYou = SummaryListViewModel(Seq.empty),
        aboutTheIndividualList = None,
        aboutTheCompanyList = None,
        aboutTheLLPList = None,
        aboutTheTrustList = None,
        aboutThePersonWhoDiedList = Some(SummaryListViewModel(Seq(WhatWasThePersonDateOfBirthSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when WhatWasThePersonOccupationPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id", "session-123").set(WhatWasThePersonOccupationPage, arbitrary[String].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnEstate)
      } yield uaWithRelatesToPage).success.value

      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten),
        aboutYou = SummaryListViewModel(Seq.empty),
        aboutTheIndividualList = None,
        aboutTheCompanyList = None,
        aboutTheLLPList = None,
        aboutTheTrustList = None,
        aboutThePersonWhoDiedList = Some(SummaryListViewModel(Seq(WhatWasThePersonOccupationSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when DidThePersonHaveNINOPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id", "session-123").set(DidThePersonHaveNINOPage, arbitrary[DidThePersonHaveNINO].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnEstate)
      } yield uaWithRelatesToPage).success.value

      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten),
        aboutYou = SummaryListViewModel(Seq.empty),
        aboutTheIndividualList = None,
        aboutTheCompanyList = None,
        aboutTheLLPList = None,
        aboutTheTrustList = None,
        aboutThePersonWhoDiedList = Some(SummaryListViewModel(Seq(DidThePersonHaveNINOSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when WhatWasThePersonNINOPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id", "session-123").set(WhatWasThePersonNINOPage, arbitrary[String].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnEstate)
      } yield uaWithRelatesToPage).success.value

      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten),
        aboutYou = SummaryListViewModel(Seq.empty),
        aboutTheIndividualList = None,
        aboutTheCompanyList = None,
        aboutTheLLPList = None,
        aboutTheTrustList = None,
        aboutThePersonWhoDiedList = Some(SummaryListViewModel(Seq(WhatWasThePersonNINOSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when WasThePersonRegisteredForVATPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id", "session-123").set(WasThePersonRegisteredForVATPage, arbitrary[WasThePersonRegisteredForVAT].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnEstate)
      } yield uaWithRelatesToPage).success.value

      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten),
        aboutYou = SummaryListViewModel(Seq.empty),
        aboutTheIndividualList = None,
        aboutTheCompanyList = None,
        aboutTheLLPList = None,
        aboutTheTrustList = None,
        aboutThePersonWhoDiedList = Some(SummaryListViewModel(Seq(WasThePersonRegisteredForVATSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when WhatWasThePersonVATRegistrationNumberPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id", "session-123").set(WhatWasThePersonVATRegistrationNumberPage, arbitrary[String].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnEstate)
      } yield uaWithRelatesToPage).success.value

      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten),
        aboutYou = SummaryListViewModel(Seq.empty),
        aboutTheIndividualList = None,
        aboutTheCompanyList = None,
        aboutTheLLPList = None,
        aboutTheTrustList = None,
        aboutThePersonWhoDiedList = Some(SummaryListViewModel(Seq(WhatWasThePersonVATRegistrationNumberSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when WasThePersonRegisteredForSAPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id", "session-123").set(WasThePersonRegisteredForSAPage, arbitrary[WasThePersonRegisteredForSA].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnEstate)
      } yield uaWithRelatesToPage).success.value

      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten),
        aboutYou = SummaryListViewModel(Seq.empty),
        aboutTheIndividualList = None,
        aboutTheCompanyList = None,
        aboutTheLLPList = None,
        aboutTheTrustList = None,
        aboutThePersonWhoDiedList = Some(SummaryListViewModel(Seq(WasThePersonRegisteredForSASummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when WhatWasThePersonUTRPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id", "session-123").set(WhatWasThePersonUTRPage, arbitrary[String].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnEstate)
      } yield uaWithRelatesToPage).success.value

      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten),
        aboutYou = SummaryListViewModel(Seq.empty),
        aboutTheIndividualList = None,
        aboutTheCompanyList = None,
        aboutTheLLPList = None,
        aboutTheTrustList = None,
        aboutThePersonWhoDiedList = Some(SummaryListViewModel(Seq(WhatWasThePersonUTRSummary.row(ua)(messages)).flatten))
      ))
    }

    "must return OK and the correct view for a GET when EstateAddressLookupPage is populated" in {
      val ua = (for {
        userAnswer <- UserAnswers("id", "session-123").set(EstateAddressLookupPage, arbitrary[Address].sample.value)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnEstate)
      } yield uaWithRelatesToPage).success.value

      rowIsDisplayedWhenPageIsPopulated(ua)(messages => SummaryLists(
        background = SummaryListViewModel(Seq(RelatesToSummary.row(ua)(messages), LiabilitiesSummary.row(ua)(messages)).flatten),
        aboutYou = SummaryListViewModel(Seq.empty),
        aboutTheIndividualList = None,
        aboutTheCompanyList = None,
        aboutTheLLPList = None,
        aboutTheTrustList = None,
        aboutThePersonWhoDiedList = Some(SummaryListViewModel(Seq(EstateAddressLookupSummary.row(ua)(messages)).flatten))
      ))
    }

  }
}
