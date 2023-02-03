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

package generators

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Arbitrary
import pages._
import models._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryNotificationStartedUserAnswersEntry: Arbitrary[(NotificationStartedPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[NotificationStartedPage.type]
        value <- arbitrary[NotificationStarted].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatEmailAddressCanWeContactYouWithUserAnswersEntry: Arbitrary[(WhatEmailAddressCanWeContactYouWithPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatEmailAddressCanWeContactYouWithPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhichEmailAddressCanWeContactYouWithUserAnswersEntry: Arbitrary[(WhichEmailAddressCanWeContactYouWithPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhichEmailAddressCanWeContactYouWithPage.type]
        value <- arbitrary[WhichEmailAddressCanWeContactYouWith].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsTheCaseReferenceUserAnswersEntry: Arbitrary[(WhatIsTheCaseReferencePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsTheCaseReferencePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDoYouHaveACaseReferenceUserAnswersEntry: Arbitrary[(DoYouHaveACaseReferencePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DoYouHaveACaseReferencePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatTelephoneNumberCanWeContactYouWithUserAnswersEntry: Arbitrary[(WhatTelephoneNumberCanWeContactYouWithPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatTelephoneNumberCanWeContactYouWithPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryCanWeUseTelephoneNumberToContactYouUserAnswersEntry: Arbitrary[(CanWeUseTelephoneNumberToContactYouPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[CanWeUseTelephoneNumberToContactYouPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAdviceGivenUserAnswersEntry: Arbitrary[(AdviceGivenPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AdviceGivenPage.type]
        value <- arbitrary[AdviceGiven].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAdviceProfessionUserAnswersEntry: Arbitrary[(AdviceProfessionPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AdviceProfessionPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAdviceBusinessNameUserAnswersEntry: Arbitrary[(AdviceBusinessNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AdviceBusinessNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAdviceBusinessesOrOrgUserAnswersEntry: Arbitrary[(AdviceBusinessesOrOrgPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AdviceBusinessesOrOrgPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDidSomeoneGiveYouAdviceNotDeclareTaxUserAnswersEntry: Arbitrary[(DidSomeoneGiveYouAdviceNotDeclareTaxPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DidSomeoneGiveYouAdviceNotDeclareTaxPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPersonWhoGaveAdviceUserAnswersEntry: Arbitrary[(PersonWhoGaveAdvicePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PersonWhoGaveAdvicePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhyNotBeforeNowUserAnswersEntry: Arbitrary[(WhyNotBeforeNowPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhyNotBeforeNowPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }
    
  implicit lazy val arbitraryWhatIsTheReasonForMakingADisclosureNowUserAnswersEntry: Arbitrary[(WhatIsTheReasonForMakingADisclosureNowPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsTheReasonForMakingADisclosureNowPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhyAreYouMakingADisclosureUserAnswersEntry: Arbitrary[(WhyAreYouMakingADisclosurePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhyAreYouMakingADisclosurePage.type]
        value <- arbitrary[WhyAreYouMakingADisclosure].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDidYouReceiveTaxCreditUserAnswersEntry: Arbitrary[(DidYouReceiveTaxCreditPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DidYouReceiveTaxCreditPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatOtherLiabilityIssuesUserAnswersEntry: Arbitrary[(WhatOtherLiabilityIssuesPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatOtherLiabilityIssuesPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDescribeTheGiftUserAnswersEntry: Arbitrary[(DescribeTheGiftPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DescribeTheGiftPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryOtherLiabilityIssuesUserAnswersEntry: Arbitrary[(OtherLiabilityIssuesPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[OtherLiabilityIssuesPage.type]
        value <- arbitrary[OtherLiabilityIssues].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTheMaximumValueOfAllAssetsUserAnswersEntry: Arbitrary[(TheMaximumValueOfAllAssetsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TheMaximumValueOfAllAssetsPage.type]
        value <- arbitrary[TheMaximumValueOfAllAssets].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHowMuchTaxHasNotBeenIncludedUserAnswersEntry: Arbitrary[(HowMuchTaxHasNotBeenIncludedPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HowMuchTaxHasNotBeenIncludedPage.type]
        value <- arbitrary[HowMuchTaxHasNotBeenIncluded].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryUnderWhatConsiderationUserAnswersEntry: Arbitrary[(UnderWhatConsiderationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[UnderWhatConsiderationPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTaxBeforeSevenYearsUserAnswersEntry: Arbitrary[(TaxBeforeSevenYearsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TaxBeforeSevenYearsPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTaxYearLiabilitiesUserAnswersEntry: Arbitrary[(TaxYearLiabilitiesPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TaxYearLiabilitiesPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTaxBeforeFiveYearsUserAnswersEntry: Arbitrary[(TaxBeforeFiveYearsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TaxBeforeFiveYearsPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
       } yield (page, value)
    }

  implicit lazy val arbitraryYourLegalInterpretationUserAnswersEntry: Arbitrary[(YourLegalInterpretationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[YourLegalInterpretationPage.type]
        value <- arbitrary[YourLegalInterpretation].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryCountryOfYourOffshoreLiabilityUserAnswersEntry: Arbitrary[(CountryOfYourOffshoreLiabilityPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[CountryOfYourOffshoreLiabilityPage.type]
        value <- arbitrary[Set[config.Country]].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhichYearsUserAnswersEntry: Arbitrary[(WhichYearsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhichYearsPage.type]
        value <- arbitrary[OffshoreYears].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsYourReasonableExcuseUserAnswersEntry: Arbitrary[(WhatIsYourReasonableExcusePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsYourReasonableExcusePage.type]
        value <- arbitrary[WhatIsYourReasonableExcuse].map(Json.toJson(_))
      } yield (page, value)
    }
      
  implicit lazy val arbitraryWhatIsYourReasonableExcuseForNotFilingReturnUserAnswersEntry: Arbitrary[(WhatIsYourReasonableExcuseForNotFilingReturnPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsYourReasonableExcuseForNotFilingReturnPage.type]
        value <- arbitrary[WhatIsYourReasonableExcuseForNotFilingReturn].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatReasonableCareDidYouTakeUserAnswersEntry: Arbitrary[(WhatReasonableCareDidYouTakePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatReasonableCareDidYouTakePage.type]
        value <- arbitrary[WhatReasonableCareDidYouTake].map(Json.toJson(_))
        } yield (page, value)
    }
    
  implicit lazy val arbitraryYouHaveLeftTheDDSUserAnswersEntry: Arbitrary[(YouHaveLeftTheDDSPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[YouHaveLeftTheDDSPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhyAreYouMakingThisDisclosureUserAnswersEntry: Arbitrary[(WhyAreYouMakingThisDisclosurePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhyAreYouMakingThisDisclosurePage.type]
        value <- arbitrary[WhyAreYouMakingThisDisclosure].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryMakeANotificationOrDisclosureUserAnswersEntry: Arbitrary[(MakeANotificationOrDisclosurePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[MakeANotificationOrDisclosurePage.type]
        value <- arbitrary[MakeANotificationOrDisclosure].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHowWouldYouPreferToBeContactedUserAnswersEntry: Arbitrary[(HowWouldYouPreferToBeContactedPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HowWouldYouPreferToBeContactedPage.type]
        value <- arbitrary[HowWouldYouPreferToBeContacted].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatWasThePersonUTRUserAnswersEntry: Arbitrary[(WhatWasThePersonUTRPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatWasThePersonUTRPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWasThePersonRegisteredForSAUserAnswersEntry: Arbitrary[(WasThePersonRegisteredForSAPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WasThePersonRegisteredForSAPage.type]
        value <- arbitrary[WasThePersonRegisteredForSA].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatWasThePersonVATRegistrationNumberUserAnswersEntry: Arbitrary[(WhatWasThePersonVATRegistrationNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatWasThePersonVATRegistrationNumberPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWasThePersonRegisteredForVATUserAnswersEntry: Arbitrary[(WasThePersonRegisteredForVATPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WasThePersonRegisteredForVATPage.type]
        value <- arbitrary[WasThePersonRegisteredForVAT].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatWasThePersonNINOUserAnswersEntry: Arbitrary[(WhatWasThePersonNINOPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatWasThePersonNINOPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDidThePersonHaveNINOUserAnswersEntry: Arbitrary[(DidThePersonHaveNINOPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DidThePersonHaveNINOPage.type]
        value <- arbitrary[DidThePersonHaveNINO].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatWasThePersonDateOfBirthUserAnswersEntry: Arbitrary[(WhatWasThePersonDateOfBirthPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatWasThePersonDateOfBirthPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatWasThePersonOccupationUserAnswersEntry: Arbitrary[(WhatWasThePersonOccupationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatWasThePersonOccupationPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAreYouTheExecutorOfTheEstateUserAnswersEntry: Arbitrary[(AreYouTheExecutorOfTheEstatePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AreYouTheExecutorOfTheEstatePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatWasTheNameOfThePersonWhoDiedUserAnswersEntry: Arbitrary[(WhatWasTheNameOfThePersonWhoDiedPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatWasTheNameOfThePersonWhoDiedPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsTheTrustNameUserAnswersEntry: Arbitrary[(WhatIsTheTrustNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsTheTrustNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutUserAnswersEntry: Arbitrary[(AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsTheLLPNameUserAnswersEntry: Arbitrary[(WhatIsTheLLPNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsTheLLPNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutUserAnswersEntry: Arbitrary[(AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsTheCompanyRegistrationNumberUserAnswersEntry: Arbitrary[(WhatIsTheCompanyRegistrationNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsTheCompanyRegistrationNumberPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutUserAnswersEntry: Arbitrary[(WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsTheNameOfTheOrganisationYouRepresentUserAnswersEntry: Arbitrary[(WhatIsTheNameOfTheOrganisationYouRepresentPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsTheNameOfTheOrganisationYouRepresentPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAreYouRepresentingAnOrganisationUserAnswersEntry: Arbitrary[(AreYouRepresentingAnOrganisationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AreYouRepresentingAnOrganisationPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutUserAnswersEntry: Arbitrary[(AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsTheIndividualsUniqueTaxReferenceUserAnswersEntry: Arbitrary[(WhatIsTheIndividualsUniqueTaxReferencePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsTheIndividualsUniqueTaxReferencePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsTheIndividualsVATRegistrationNumberUserAnswersEntry: Arbitrary[(WhatIsTheIndividualsVATRegistrationNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsTheIndividualsVATRegistrationNumberPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsTheIndividualRegisteredForSelfAssessmentUserAnswersEntry: Arbitrary[(IsTheIndividualRegisteredForSelfAssessmentPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsTheIndividualRegisteredForSelfAssessmentPage.type]
        value <- arbitrary[IsTheIndividualRegisteredForSelfAssessment].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsTheIndividualRegisteredForVATUserAnswersEntry: Arbitrary[(IsTheIndividualRegisteredForVATPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsTheIndividualRegisteredForVATPage.type]
        value <- arbitrary[IsTheIndividualRegisteredForVAT].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsIndividualsNationalInsuranceNumberUserAnswersEntry: Arbitrary[(WhatIsIndividualsNationalInsuranceNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsIndividualsNationalInsuranceNumberPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDoesTheIndividualHaveNationalInsuranceNumberUserAnswersEntry: Arbitrary[(DoesTheIndividualHaveNationalInsuranceNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DoesTheIndividualHaveNationalInsuranceNumberPage.type]
        value <- arbitrary[DoesTheIndividualHaveNationalInsuranceNumber].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsTheIndividualOccupationUserAnswersEntry: Arbitrary[(WhatIsTheIndividualOccupationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsTheIndividualOccupationPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsTheIndividualDateOfBirthUserAnswersEntry: Arbitrary[(WhatIsTheIndividualDateOfBirthPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsTheIndividualDateOfBirthPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsTheIndividualsFullNameUserAnswersEntry: Arbitrary[(WhatIsTheIndividualsFullNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsTheIndividualsFullNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsYourUniqueTaxReferenceUserAnswersEntry: Arbitrary[(WhatIsYourUniqueTaxReferencePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsYourUniqueTaxReferencePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAreYouRegisteredForSelfAssessmentUserAnswersEntry: Arbitrary[(AreYouRegisteredForSelfAssessmentPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AreYouRegisteredForSelfAssessmentPage.type]
        value <- arbitrary[AreYouRegisteredForSelfAssessment].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAreYouRegisteredForVATUserAnswersEntry: Arbitrary[(AreYouRegisteredForVATPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AreYouRegisteredForVATPage.type]
        value <- arbitrary[AreYouRegisteredForVAT].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsYourVATRegistrationNumberUserAnswersEntry: Arbitrary[(WhatIsYourVATRegistrationNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsYourVATRegistrationNumberPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsYourNationalInsuranceNumberUserAnswersEntry: Arbitrary[(WhatIsYourNationalInsuranceNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsYourNationalInsuranceNumberPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDoYouHaveNationalInsuranceNumberUserAnswersEntry: Arbitrary[(DoYouHaveNationalInsuranceNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DoYouHaveNationalInsuranceNumberPage.type]
        value <- arbitrary[DoYouHaveNationalInsuranceNumber].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsYourMainOccupationUserAnswersEntry: Arbitrary[(WhatIsYourMainOccupationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsYourMainOccupationPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsYourDateOfBirthUserAnswersEntry: Arbitrary[(WhatIsYourDateOfBirthPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsYourDateOfBirthPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsYourFullNameUserAnswersEntry: Arbitrary[(WhatIsYourFullNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhatIsYourFullNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }
    
  implicit lazy val arbitraryYourEmailAddressUserAnswersEntry: Arbitrary[(YourEmailAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[YourEmailAddressPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryYourPhoneNumberUserAnswersEntry: Arbitrary[(YourPhoneNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[YourPhoneNumberPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryLetterReferenceUserAnswersEntry: Arbitrary[(LetterReferencePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[LetterReferencePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryOffshoreLiabilitiesUserAnswersEntry: Arbitrary[(OffshoreLiabilitiesPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[OffshoreLiabilitiesPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAreYouTheIndividualUserAnswersEntry: Arbitrary[(AreYouTheIndividualPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AreYouTheIndividualPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }
  
  implicit lazy val arbitraryOnshoreLiabilitiesUserAnswersEntry: Arbitrary[(OnshoreLiabilitiesPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[OnshoreLiabilitiesPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryrelatesToUserAnswersEntry: Arbitrary[(RelatesToPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[RelatesToPage.type]
        value <- arbitrary[RelatesTo].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryReceivedALetterUserAnswersEntry: Arbitrary[(ReceivedALetterPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ReceivedALetterPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }
}
