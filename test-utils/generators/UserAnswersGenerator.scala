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

import models.UserAnswers
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.TryValues
import pages._
import play.api.libs.json.{JsValue, Json}
import org.scalacheck.Arbitrary.arbitrary

trait UserAnswersGenerator extends TryValues {
  self: Generators =>

  val generators: Seq[Gen[(QuestionPage[_], JsValue)]] =
    arbitrary[(AreYouTheEntityPage.type, JsValue)] ::
      arbitrary[(AccountingPeriodDLAddedPage.type, JsValue)] ::
      arbitrary[(AccountingPeriodCTAddedPage.type, JsValue)] ::
      arbitrary[(PropertyAddedPage.type, JsValue)] ::
      arbitrary[(HowManyPropertiesDoYouCurrentlyLetOutPage.type, JsValue)] ::
      arbitrary[(AreYouAMemberOfAnyLandlordAssociationsPage.type, JsValue)] ::
      arbitrary[(DidTheLettingAgentCollectRentOnYourBehalfPage.type, JsValue)] ::
      arbitrary[(WasALettingAgentUsedToManagePropertyPage.type, JsValue)] ::
      arbitrary[(WhatTypeOfMortgageDidYouHavePage.type, JsValue)] ::
      arbitrary[(WhatWasTheTypeOfMortgagePage.type, JsValue)] ::
      arbitrary[(WasPropertyFurnishedPage.type, JsValue)] ::
      arbitrary[(WhatWasThePercentageIncomeYouReceivedFromPropertyPage.type, JsValue)] ::
      arbitrary[(DidYouHaveAMortgageOnPropertyPage.type, JsValue)] ::
      arbitrary[(JointlyOwnedPropertyPage.type, JsValue)] ::
      arbitrary[(FHLPage.type, JsValue)] ::
      arbitrary[(PropertyIsNoLongerBeingLetOutPage.type, JsValue)] ::
      arbitrary[(PropertyStoppedBeingLetOutPage.type, JsValue)] ::
      arbitrary[(PropertyFirstLetOutPage.type, JsValue)] ::
      arbitrary[(CorporationTaxLiabilityPage.type, JsValue)] ::
      arbitrary[(DirectorLoanAccountLiabilitiesPage.type, JsValue)] ::
      arbitrary[(ResidentialReductionPage.type, JsValue)] ::
      arbitrary[(WhichOnshoreYearsPage.type, JsValue)] ::
      arbitrary[(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage.type, JsValue)] ::
      arbitrary[(OfferLetterPage.type, JsValue)] ::
      arbitrary[(WhichTelephoneNumberCanWeContactYouWithPage.type, JsValue)] ::
      arbitrary[(ForeignTaxCreditPage.type, JsValue)] ::
      arbitrary[(YouHaveNotSelectedCertainTaxYearPage.type, JsValue)] ::
      arbitrary[(TaxBeforeNineteenYearsPage.type, JsValue)] ::
      arbitrary[(TaxBeforeNineteenYearsOnshorePage.type, JsValue)] ::
      arbitrary[(NotificationStartedPage.type, JsValue)] ::
      arbitrary[(YouHaveNotIncludedTheTaxYearPage.type, JsValue)] ::
      arbitrary[(CountriesOrTerritoriesPage.type, JsValue)] ::
      arbitrary[(WhatEmailAddressCanWeContactYouWithPage.type, JsValue)] ::
      arbitrary[(WhichEmailAddressCanWeContactYouWithPage.type, JsValue)] ::
      arbitrary[(WhatIsTheCaseReferencePage.type, JsValue)] ::
      arbitrary[(DoYouHaveACaseReferencePage.type, JsValue)] ::
      arbitrary[(WhatTelephoneNumberCanWeContactYouWithPage.type, JsValue)] ::
      arbitrary[(AdviceGivenPage.type, JsValue)] ::
      arbitrary[(AdviceProfessionPage.type, JsValue)] ::
      arbitrary[(AdviceBusinessNamePage.type, JsValue)] ::
      arbitrary[(AdviceBusinessesOrOrgPage.type, JsValue)] ::
      arbitrary[(PersonWhoGaveAdvicePage.type, JsValue)] ::
      arbitrary[(DidSomeoneGiveYouAdviceNotDeclareTaxPage.type, JsValue)] ::
      arbitrary[(WhyNotBeforeNowPage.type, JsValue)] ::
      arbitrary[(WhatIsTheReasonForMakingADisclosureNowPage.type, JsValue)] ::
      arbitrary[(WhyAreYouMakingADisclosurePage.type, JsValue)] ::
      arbitrary[(DidYouReceiveTaxCreditPage.type, JsValue)] ::
      arbitrary[(WhatOtherLiabilityIssuesPage.type, JsValue)] ::
      arbitrary[(DescribeTheGiftPage.type, JsValue)] ::
      arbitrary[(OtherLiabilityIssuesPage.type, JsValue)] ::
      arbitrary[(TheMaximumValueOfAllAssetsPage.type, JsValue)] ::
      arbitrary[(HowMuchTaxHasNotBeenIncludedPage.type, JsValue)] ::
      arbitrary[(UnderWhatConsiderationPage.type, JsValue)] ::
      arbitrary[(TaxBeforeSevenYearsPage.type, JsValue)] ::
      arbitrary[(TaxBeforeThreeYearsOnshorePage.type, JsValue)] ::
      arbitrary[(TaxBeforeFiveYearsPage.type, JsValue)] ::
      arbitrary[(TaxBeforeFiveYearsOnshorePage.type, JsValue)] ::
      arbitrary[(TaxYearLiabilitiesPage.type, JsValue)] ::
      arbitrary[(YourLegalInterpretationPage.type, JsValue)] ::
      arbitrary[(CountryOfYourOffshoreLiabilityPage.type, JsValue)] ::
      arbitrary[(WhatIsYourReasonableExcusePage.type, JsValue)] ::
      arbitrary[(WhatIsYourReasonableExcuseForNotFilingReturnPage.type, JsValue)] ::
      arbitrary[(WhatReasonableCareDidYouTakePage.type, JsValue)] ::
      arbitrary[(YouHaveLeftTheDDSPage.type, JsValue)] ::
      arbitrary[(HowWouldYouPreferToBeContactedPage.type, JsValue)] ::
      arbitrary[(WhyAreYouMakingThisDisclosurePage.type, JsValue)] ::
      arbitrary[(MakeANotificationOrDisclosurePage.type, JsValue)] ::
      arbitrary[(WhatWasThePersonUTRPage.type, JsValue)] ::
      arbitrary[(WasThePersonRegisteredForSAPage.type, JsValue)] ::
      arbitrary[(WhatWasThePersonVATRegistrationNumberPage.type, JsValue)] ::
      arbitrary[(WasThePersonRegisteredForVATPage.type, JsValue)] ::
      arbitrary[(WhatWasThePersonNINOPage.type, JsValue)] ::
      arbitrary[(DidThePersonHaveNINOPage.type, JsValue)] ::
      arbitrary[(WhatWasThePersonDateOfBirthPage.type, JsValue)] ::
      arbitrary[(WhatWasThePersonOccupationPage.type, JsValue)] ::
      arbitrary[(WhatWasTheNameOfThePersonWhoDiedPage.type, JsValue)] ::
      arbitrary[(WhatIsTheTrustNamePage.type, JsValue)] ::
      arbitrary[(WhatIsTheLLPNamePage.type, JsValue)] ::
      arbitrary[(WhatIsTheCompanyRegistrationNumberPage.type, JsValue)] ::
      arbitrary[(WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage.type, JsValue)] ::
      arbitrary[(WhatIsTheNameOfTheOrganisationYouRepresentPage.type, JsValue)] ::
      arbitrary[(AreYouRepresentingAnOrganisationPage.type, JsValue)] ::
      arbitrary[(WhatIsTheIndividualsUniqueTaxReferencePage.type, JsValue)] ::
      arbitrary[(WhatIsTheIndividualsVATRegistrationNumberPage.type, JsValue)] ::
      arbitrary[(IsTheIndividualRegisteredForSelfAssessmentPage.type, JsValue)] ::
      arbitrary[(IsTheIndividualRegisteredForVATPage.type, JsValue)] ::
      arbitrary[(WhatIsIndividualsNationalInsuranceNumberPage.type, JsValue)] ::
      arbitrary[(DoesTheIndividualHaveNationalInsuranceNumberPage.type, JsValue)] ::
      arbitrary[(WhatIsTheIndividualOccupationPage.type, JsValue)] ::
      arbitrary[(WhatIsTheIndividualDateOfBirthPage.type, JsValue)] ::
      arbitrary[(WhatIsTheIndividualsFullNamePage.type, JsValue)] ::
      arbitrary[(WhatIsYourUniqueTaxReferencePage.type, JsValue)] ::
      arbitrary[(AreYouRegisteredForSelfAssessmentPage.type, JsValue)] ::
      arbitrary[(AreYouRegisteredForVATPage.type, JsValue)] ::
      arbitrary[(WhatIsYourVATRegistrationNumberPage.type, JsValue)] ::
      arbitrary[(WhatIsYourNationalInsuranceNumberPage.type, JsValue)] ::
      arbitrary[(DoYouHaveNationalInsuranceNumberPage.type, JsValue)] ::
      arbitrary[(WhatIsYourMainOccupationPage.type, JsValue)] ::
      arbitrary[(WhatIsYourDateOfBirthPage.type, JsValue)] ::
      arbitrary[(WhatIsYourFullNamePage.type, JsValue)] ::
      arbitrary[(YourEmailAddressPage.type, JsValue)] ::
      arbitrary[(YourPhoneNumberPage.type, JsValue)] ::
      arbitrary[(LetterReferencePage.type, JsValue)] ::
      arbitrary[(OffshoreLiabilitiesPage.type, JsValue)] ::
      arbitrary[(OnshoreLiabilitiesPage.type, JsValue)] ::
      arbitrary[(RelatesToPage.type, JsValue)] ::
      arbitrary[(ReceivedALetterPage.type, JsValue)] ::
      Nil

  implicit lazy val arbitraryUserData: Arbitrary[UserAnswers] = {

    import models._

    Arbitrary {
      for {
        id        <- nonEmptyString
        sessionId <- nonEmptyString
        data      <- generators match {
                       case Nil => Gen.const(Map[QuestionPage[_], JsValue]())
                       case _   => Gen.mapOf(oneOf(generators))
                     }
      } yield UserAnswers(
        id = id,
        sessionId = sessionId,
        data = data.foldLeft(Json.obj()) { case (obj, (path, value)) =>
          obj.setObject(path.path, value).get
        }
      )
    }
  }
}
