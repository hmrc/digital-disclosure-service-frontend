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
import models.store._
import models.store.disclosure._
import pages._
import scala.util.Try
import com.google.inject.{Inject, Singleton, ImplementedBy}

@Singleton
class DisclosureToUAServiceImpl @Inject()(
  notificationService: NotificationToUAService
) extends DisclosureToUAService {

  def fullDisclosureToUa(sessionId: String, fullDisclosure: FullDisclosure): Try[UserAnswers] = {
    val userAnswers = initialiseUserAnswers(sessionId, fullDisclosure)

    for {
      uaWithCaseRef         <- caseReferenceToUa(fullDisclosure.caseReference, userAnswers)
      uaWithPersonalDetails <- notificationService.personalDetailsToUserAnswers(fullDisclosure.personalDetails, uaWithCaseRef)
      uaWithOffshore        <- offshoreLiabilitiesToUa(fullDisclosure.offshoreLiabilities, uaWithPersonalDetails)
      uaWithOnshore         <- onshoreLiabilitiesToUa(fullDisclosure.onshoreLiabilities, uaWithOffshore)
      uaWithOther           <- otherLiabilitiesToUa(fullDisclosure.otherLiabilities, uaWithOnshore)
      updatedUa             <- reasonForDisclosingNowToUa(fullDisclosure.reasonForDisclosingNow, uaWithOther)
    } yield updatedUa
  }

  def initialiseUserAnswers(sessionId: String, fullDisclosure: FullDisclosure): UserAnswers = {
    import fullDisclosure._

    UserAnswers(
      id = userId,
      sessionId = sessionId,
      submissionId = submissionId,
      submissionType = SubmissionType.Disclosure,
      lastUpdated = lastUpdated,
      created = created,
      metadata = metadata,
      madeDeclaration = madeDeclaration,
      customerId = customerId
    )
  }

  def caseReferenceToUa(caseReference: CaseReference, userAnswers: UserAnswers): Try[UserAnswers] = {
    import caseReference._

    val pages = List(
      doYouHaveACaseReference.map(PageWithValue(DoYouHaveACaseReferencePage, _)),
      whatIsTheCaseReference.map(PageWithValue(WhatIsTheCaseReferencePage, _))
    ).flatten
    
    PageWithValue.pagesToUserAnswers(pages, userAnswers)
  }
  
  def otherLiabilitiesToUa(otherLiabilities: OtherLiabilities, userAnswers: UserAnswers): Try[UserAnswers] = {
    import otherLiabilities._

    val pages = List(
      issues.map(PageWithValue(OtherLiabilityIssuesPage, _)),
      inheritanceGift.map(PageWithValue(DescribeTheGiftPage, _)),
      other.map(PageWithValue(WhatOtherLiabilityIssuesPage, _)),
      taxCreditsReceived.map(PageWithValue(DidYouReceiveTaxCreditPage, _))
    ).flatten
    
    PageWithValue.pagesToUserAnswers(pages, userAnswers)
  }

  def offshoreLiabilitiesToUa(offshoreLiabilities: OffshoreLiabilities, userAnswers: UserAnswers): Try[UserAnswers] = {
    import offshoreLiabilities._

    val pages = List(
      behaviour.map(PageWithValue(WhyAreYouMakingThisDisclosurePage, _)),
      excuseForNotNotifying.map(PageWithValue(WhatIsYourReasonableExcusePage, _)),
      reasonableCare.map(PageWithValue(WhatReasonableCareDidYouTakePage, _)),
      excuseForNotFiling.map(PageWithValue(WhatIsYourReasonableExcuseForNotFilingReturnPage, _)),
      whichYears.map(PageWithValue(WhichYearsPage, _)),
      youHaveNotIncludedTheTaxYear.map(PageWithValue(YouHaveNotIncludedTheTaxYearPage, _)),
      youHaveNotSelectedCertainTaxYears.map(PageWithValue(YouHaveNotSelectedCertainTaxYearPage, _)),
      taxBeforeFiveYears.map(PageWithValue(TaxBeforeFiveYearsPage, _)),
      taxBeforeSevenYears.map(PageWithValue(TaxBeforeSevenYearsPage, _)),
      taxBeforeNineteenYears.map(PageWithValue(TaxBeforeNineteenYearsPage, _)),
      disregardedCDF.map(PageWithValue(ContractualDisclosureFacilityPage, _)),
      taxYearLiabilities.map(PageWithValue(TaxYearLiabilitiesPage, _)),
      taxYearForeignTaxDeductions.map(PageWithValue(ForeignTaxCreditPage, _)),
      countryOfYourOffshoreLiability.map(PageWithValue(CountryOfYourOffshoreLiabilityPage, _)),
      legalInterpretation.map(PageWithValue(YourLegalInterpretationPage, _)),
      otherInterpretation.map(PageWithValue(UnderWhatConsiderationPage, _)),
      notIncludedDueToInterpretation.map(PageWithValue(HowMuchTaxHasNotBeenIncludedPage, _)),
      maximumValueOfAssets.map(PageWithValue(TheMaximumValueOfAllAssetsPage, _))
    ).flatten
    
    PageWithValue.pagesToUserAnswers(pages, userAnswers)
  }

  def onshoreLiabilitiesToUa(onshoreLiabilities: Option[OnshoreLiabilities], userAnswers: UserAnswers): Try[UserAnswers] = {

    val liabilities = onshoreLiabilities.getOrElse(OnshoreLiabilities())

    import liabilities._

    val pages = List(
      behaviour.map(PageWithValue(WhyAreYouMakingThisOnshoreDisclosurePage, _)),
      excuseForNotNotifying.map(PageWithValue(ReasonableExcuseOnshorePage, _)),
      reasonableCare.map(PageWithValue(ReasonableCareOnshorePage, _)),
      excuseForNotFiling.map(PageWithValue(ReasonableExcuseForNotFilingOnshorePage, _)),
      whatLiabilities.map(PageWithValue(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, _)),
      whichYears.map(PageWithValue(WhichOnshoreYearsPage, _)),
      youHaveNotIncludedTheTaxYear.map(PageWithValue(NotIncludedSingleTaxYearPage, _)),
      youHaveNotSelectedCertainTaxYears.map(PageWithValue(NotIncludedMultipleTaxYearsPage, _)),
      taxBeforeThreeYears.map(PageWithValue(TaxBeforeThreeYearsOnshorePage, _)),
      taxBeforeFiveYears.map(PageWithValue(TaxBeforeFiveYearsOnshorePage, _)),
      taxBeforeNineteenYears.map(PageWithValue(TaxBeforeNineteenYearsOnshorePage, _)),
      disregardedCDF.map(PageWithValue(CDFOnshorePage, _)),
      taxYearLiabilities.map(PageWithValue(OnshoreTaxYearLiabilitiesPage, _)),
      lettingDeductions.map(PageWithValue(ResidentialReductionPage, _)),
      lettingProperties.map(PageWithValue(LettingPropertyPage, _)),
      memberOfLandlordAssociations.map(PageWithValue(AreYouAMemberOfAnyLandlordAssociationsPage, _)),
      landlordAssociations.map(PageWithValue(WhichLandlordAssociationsAreYouAMemberOfPage, _)),
      howManyProperties.map(PageWithValue(HowManyPropertiesDoYouCurrentlyLetOutPage, _)),
      corporationTaxLiabilities.map(PageWithValue(CorporationTaxLiabilityPage, _)),
      directorLoanAccountLiabilities.map(PageWithValue(DirectorLoanAccountLiabilitiesPage, _))
    ).flatten
    
    PageWithValue.pagesToUserAnswers(pages, userAnswers)
  }

  def reasonForDisclosingNowToUa(reasonForDisclosingNow: ReasonForDisclosingNow, userAnswers: UserAnswers): Try[UserAnswers] = {
    import reasonForDisclosingNow._

    val pages = List(
      reason.map(PageWithValue(WhyAreYouMakingADisclosurePage, _)),
      otherReason.map(PageWithValue(WhatIsTheReasonForMakingADisclosureNowPage, _)),
      whyNotBeforeNow.map(PageWithValue(WhyNotBeforeNowPage, _)),
      receivedAdvice.map(PageWithValue(DidSomeoneGiveYouAdviceNotDeclareTaxPage, _)),
      personWhoGaveAdvice.map(PageWithValue(PersonWhoGaveAdvicePage, _)),
      adviceOnBehalfOfBusiness.map(PageWithValue(AdviceBusinessesOrOrgPage, _)),
      adviceBusinessName.map(PageWithValue(AdviceBusinessNamePage, _)),
      personProfession.map(PageWithValue(AdviceProfessionPage, _)),
      adviceGiven.map(PageWithValue(AdviceGivenPage, _)),
      whichEmail.map(PageWithValue(WhichEmailAddressCanWeContactYouWithPage, _)),
      whichPhone.map(PageWithValue(WhichTelephoneNumberCanWeContactYouWithPage, _)),
      email.map(PageWithValue(WhatEmailAddressCanWeContactYouWithPage, _)),
      telephone.map(PageWithValue(WhatTelephoneNumberCanWeContactYouWithPage, _))
    ).flatten
    
    PageWithValue.pagesToUserAnswers(pages, userAnswers)
  }

}

@ImplementedBy(classOf[DisclosureToUAServiceImpl])
trait DisclosureToUAService {
  def fullDisclosureToUa(sessionId: String, fullDisclosure: FullDisclosure): Try[UserAnswers]
}
