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
import pages.onshore.WhyDidYouNotFileAReturnOnTimeOnshorePage
import scala.util.Try
import com.google.inject.{ImplementedBy, Inject, Singleton}

@Singleton
class DisclosureToUAServiceImpl @Inject() (
  notificationService: NotificationToUAService
) extends DisclosureToUAService {

  def fullDisclosureToUa(sessionId: String, fullDisclosure: FullDisclosure): Try[UserAnswers] = {
    val userAnswers = initialiseUserAnswers(sessionId, fullDisclosure)

    for {
      uaWithCaseRef         <- caseReferenceToUa(fullDisclosure.caseReference, userAnswers)
      uaWithPersonalDetails <-
        notificationService.personalDetailsToUserAnswers(fullDisclosure.personalDetails, uaWithCaseRef)
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

  private def isLegacyOnshoreBehaviour(value: WhyAreYouMakingThisOnshoreDisclosure): Boolean =
    value match {
      case WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyNoExcuse       => true
      case WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHasExcuse      => true
      case WhyAreYouMakingThisOnshoreDisclosure.DeliberatelyDidNotNotify   => true
      case WhyAreYouMakingThisOnshoreDisclosure.DidNotFileNoExcuse         => true
      case WhyAreYouMakingThisOnshoreDisclosure.NotFileHasExcuse           => true
      case WhyAreYouMakingThisOnshoreDisclosure.DeliberatelyDidNotFile     => true
      case WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturnNoCare     => true
      case WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturnWithCare   => true
      case WhyAreYouMakingThisOnshoreDisclosure.DeliberateInaccurateReturn => true
      case _                                                               => false
    }

  private def migrateLegacyOnshoreBehaviour(oldValues: Set[WhyAreYouMakingThisOnshoreDisclosure]): (
    Set[WhyAreYouMakingThisOnshoreDisclosure],
    Option[Set[WhyDidYouNotNotifyOnshore]],
    Option[Set[WhyDidYouNotFileAReturnOnTimeOnshore]],
    Option[Set[WhyYouSubmittedAnInaccurateOnshoreReturn]]
  ) = {
    val page1 = oldValues.flatMap {
      case WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyNoExcuse       =>
        Some(WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHMRC)
      case WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHasExcuse      =>
        Some(WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHMRC)
      case WhyAreYouMakingThisOnshoreDisclosure.DeliberatelyDidNotNotify   =>
        Some(WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHMRC)
      case WhyAreYouMakingThisOnshoreDisclosure.DidNotFileNoExcuse         =>
        Some(WhyAreYouMakingThisOnshoreDisclosure.DidNotFile)
      case WhyAreYouMakingThisOnshoreDisclosure.NotFileHasExcuse           =>
        Some(WhyAreYouMakingThisOnshoreDisclosure.DidNotFile)
      case WhyAreYouMakingThisOnshoreDisclosure.DeliberatelyDidNotFile     =>
        Some(WhyAreYouMakingThisOnshoreDisclosure.DidNotFile)
      case WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturnNoCare     =>
        Some(WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturn)
      case WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturnWithCare   =>
        Some(WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturn)
      case WhyAreYouMakingThisOnshoreDisclosure.DeliberateInaccurateReturn =>
        Some(WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturn)
      case other                                                           => Some(other)
    }

    val whyNotNotify: Set[WhyDidYouNotNotifyOnshore] = oldValues.flatMap {
      case WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyNoExcuse     =>
        Some(WhyDidYouNotNotifyOnshore.NotDeliberatelyNoReasonableExcuseOnshore)
      case WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHasExcuse    =>
        Some(WhyDidYouNotNotifyOnshore.ReasonableExcuseOnshore)
      case WhyAreYouMakingThisOnshoreDisclosure.DeliberatelyDidNotNotify =>
        Some(WhyDidYouNotNotifyOnshore.DeliberatelyDidNotNotifyOnshore)
      case _                                                             => None
    }

    val whyNotFile: Set[WhyDidYouNotFileAReturnOnTimeOnshore] = oldValues.flatMap {
      case WhyAreYouMakingThisOnshoreDisclosure.DidNotFileNoExcuse     =>
        Some(WhyDidYouNotFileAReturnOnTimeOnshore.DidNotWithholdInformationOnPurpose)
      case WhyAreYouMakingThisOnshoreDisclosure.NotFileHasExcuse       =>
        Some(WhyDidYouNotFileAReturnOnTimeOnshore.ReasonableExcuse)
      case WhyAreYouMakingThisOnshoreDisclosure.DeliberatelyDidNotFile =>
        Some(WhyDidYouNotFileAReturnOnTimeOnshore.DeliberatelyWithheldInformation)
      case _                                                           => None
    }

    val whyInaccurate: Set[WhyYouSubmittedAnInaccurateOnshoreReturn] = oldValues.flatMap {
      case WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturnNoCare     =>
        Some(WhyYouSubmittedAnInaccurateOnshoreReturn.NoReasonableCare)
      case WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturnWithCare   =>
        Some(WhyYouSubmittedAnInaccurateOnshoreReturn.ReasonableMistake)
      case WhyAreYouMakingThisOnshoreDisclosure.DeliberateInaccurateReturn =>
        Some(WhyYouSubmittedAnInaccurateOnshoreReturn.DeliberatelyInaccurate)
      case _                                                               => None
    }

    (
      page1,
      if (whyNotNotify.nonEmpty) Some(whyNotNotify) else None,
      if (whyNotFile.nonEmpty) Some(whyNotFile) else None,
      if (whyInaccurate.nonEmpty) Some(whyInaccurate) else None
    )
  }

  def onshoreLiabilitiesToUa(
    onshoreLiabilities: Option[OnshoreLiabilities],
    userAnswers: UserAnswers
  ): Try[UserAnswers] = {

    val liabilities = onshoreLiabilities.getOrElse(OnshoreLiabilities())

    import liabilities._

    val (migratedPage1, migratedWhyNotNotify, migratedWhyNotFile, migratedWhyInaccurate) =
      behaviour match {
        case Some(oldValues) if oldValues.exists(isLegacyOnshoreBehaviour) =>
          migrateLegacyOnshoreBehaviour(oldValues)
        case other                                                         =>
          (other.getOrElse(Set.empty), None, None, None)
      }

    val pages = List(
      Some(migratedPage1).filter(_.nonEmpty).map(PageWithValue(WhyAreYouMakingThisOnshoreDisclosurePage, _)),
      migratedWhyNotNotify.map(PageWithValue(WhyDidYouNotNotifyOnshorePage, _)),
      migratedWhyNotFile.map(PageWithValue(WhyDidYouNotFileAReturnOnTimeOnshorePage, _)),
      migratedWhyInaccurate.map(PageWithValue(WhyYouSubmittedAnInaccurateOnshoreReturnPage, _)),
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

  def reasonForDisclosingNowToUa(
    reasonForDisclosingNow: ReasonForDisclosingNow,
    userAnswers: UserAnswers
  ): Try[UserAnswers] = {
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

  private def isLegacyOffshoreBehaviour(value: WhyAreYouMakingThisDisclosure): Boolean =
    value match {
      case WhyAreYouMakingThisDisclosure.DidNotNotifyNoExcuse       => true
      case WhyAreYouMakingThisDisclosure.DidNotNotifyHasExcuse      => true
      case WhyAreYouMakingThisDisclosure.DeliberatelyDidNotNotify   => true
      case WhyAreYouMakingThisDisclosure.DidNotFileNoExcuse         => true
      case WhyAreYouMakingThisDisclosure.NotFileHasExcuse           => true
      case WhyAreYouMakingThisDisclosure.DeliberatelyDidNotFile     => true
      case WhyAreYouMakingThisDisclosure.InaccurateReturnNoCare     => true
      case WhyAreYouMakingThisDisclosure.InaccurateReturnWithCare   => true
      case WhyAreYouMakingThisDisclosure.DeliberateInaccurateReturn => true
      case _                                                        => false
    }

  private def migrateLegacyOffshoreBehaviour(oldValues: Set[WhyAreYouMakingThisDisclosure]): (
    Set[WhyAreYouMakingThisDisclosure],
    Option[Set[WhyDidYouNotNotify]],
    Option[Set[WhyDidYouNotFileAReturnOnTimeOffshore]],
    Option[Set[WhyYouSubmittedAnInaccurateReturn]]
  ) = {
    val page1 = oldValues.flatMap {
      case WhyAreYouMakingThisDisclosure.DidNotNotifyNoExcuse       => Some(WhyAreYouMakingThisDisclosure.DidNotNotifyHMRC)
      case WhyAreYouMakingThisDisclosure.DidNotNotifyHasExcuse      => Some(WhyAreYouMakingThisDisclosure.DidNotNotifyHMRC)
      case WhyAreYouMakingThisDisclosure.DeliberatelyDidNotNotify   =>
        Some(WhyAreYouMakingThisDisclosure.DidNotNotifyHMRC)
      case WhyAreYouMakingThisDisclosure.DidNotFileNoExcuse         => Some(WhyAreYouMakingThisDisclosure.DidNotFile)
      case WhyAreYouMakingThisDisclosure.NotFileHasExcuse           => Some(WhyAreYouMakingThisDisclosure.DidNotFile)
      case WhyAreYouMakingThisDisclosure.DeliberatelyDidNotFile     => Some(WhyAreYouMakingThisDisclosure.DidNotFile)
      case WhyAreYouMakingThisDisclosure.InaccurateReturnNoCare     => Some(WhyAreYouMakingThisDisclosure.InaccurateReturn)
      case WhyAreYouMakingThisDisclosure.InaccurateReturnWithCare   =>
        Some(WhyAreYouMakingThisDisclosure.InaccurateReturn)
      case WhyAreYouMakingThisDisclosure.DeliberateInaccurateReturn =>
        Some(WhyAreYouMakingThisDisclosure.InaccurateReturn)
      case other                                                    => Some(other)
    }

    val whyNotNotify: Set[WhyDidYouNotNotify] = oldValues.flatMap {
      case WhyAreYouMakingThisDisclosure.DidNotNotifyNoExcuse     =>
        Some(WhyDidYouNotNotify.NotDeliberatelyNoReasonableExcuse)
      case WhyAreYouMakingThisDisclosure.DidNotNotifyHasExcuse    => Some(WhyDidYouNotNotify.ReasonableExcuse)
      case WhyAreYouMakingThisDisclosure.DeliberatelyDidNotNotify => Some(WhyDidYouNotNotify.DeliberatelyDidNotNotify)
      case _                                                      => None
    }

    val whyNotFile: Set[WhyDidYouNotFileAReturnOnTimeOffshore] = oldValues.flatMap {
      case WhyAreYouMakingThisDisclosure.DidNotFileNoExcuse     =>
        Some(WhyDidYouNotFileAReturnOnTimeOffshore.DidNotWithholdInformationOnPurpose)
      case WhyAreYouMakingThisDisclosure.NotFileHasExcuse       =>
        Some(WhyDidYouNotFileAReturnOnTimeOffshore.ReasonableExcuse)
      case WhyAreYouMakingThisDisclosure.DeliberatelyDidNotFile =>
        Some(WhyDidYouNotFileAReturnOnTimeOffshore.DeliberatelyWithheldInformation)
      case _                                                    => None
    }

    val whyInaccurate: Set[WhyYouSubmittedAnInaccurateReturn] = oldValues.flatMap {
      case WhyAreYouMakingThisDisclosure.InaccurateReturnNoCare     =>
        Some(WhyYouSubmittedAnInaccurateReturn.NoReasonableCare)
      case WhyAreYouMakingThisDisclosure.InaccurateReturnWithCare   =>
        Some(WhyYouSubmittedAnInaccurateReturn.ReasonableMistake)
      case WhyAreYouMakingThisDisclosure.DeliberateInaccurateReturn =>
        Some(WhyYouSubmittedAnInaccurateReturn.DeliberatelyInaccurate)
      case _                                                        => None
    }

    (
      page1,
      if (whyNotNotify.nonEmpty) Some(whyNotNotify) else None,
      if (whyNotFile.nonEmpty) Some(whyNotFile) else None,
      if (whyInaccurate.nonEmpty) Some(whyInaccurate) else None
    )
  }

  def offshoreLiabilitiesToUa(offshoreLiabilities: OffshoreLiabilities, userAnswers: UserAnswers): Try[UserAnswers] = {
    import offshoreLiabilities._

    val (migratedPage1, migratedWhyNotNotify, migratedWhyNotFile, migratedWhyInaccurate) =
      behaviour match {
        case Some(oldValues) if oldValues.exists(isLegacyOffshoreBehaviour) =>
          migrateLegacyOffshoreBehaviour(oldValues)
        case other                                                          =>
          (other.getOrElse(Set.empty), None, None, None)
      }

    val pages = List(
      Some(migratedPage1).filter(_.nonEmpty).map(PageWithValue(WhyAreYouMakingThisDisclosurePage, _)),
      migratedWhyNotNotify.map(PageWithValue(WhyDidYouNotNotifyPage, _)),
      migratedWhyNotFile.map(PageWithValue(WhyDidYouNotFileAReturnOnTimeOffshorePage, _)),
      migratedWhyInaccurate.map(PageWithValue(WhyYouSubmittedAnInaccurateOffshoreReturnPage, _)),
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

}

@ImplementedBy(classOf[DisclosureToUAServiceImpl])
trait DisclosureToUAService {
  def fullDisclosureToUa(sessionId: String, fullDisclosure: FullDisclosure): Try[UserAnswers]
}
