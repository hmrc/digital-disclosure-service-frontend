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
import com.google.inject.{Inject, Singleton, ImplementedBy}

@Singleton
class UAToDisclosureServiceImpl @Inject()(
  notificationService: UAToNotificationService
) extends UAToDisclosureService {

  def uaToFullDisclosure(userAnswers: UserAnswers): FullDisclosure = 
    FullDisclosure (
      userId = userAnswers.id,
      submissionId = userAnswers.submissionId,
      lastUpdated = userAnswers.lastUpdated,
      metadata = userAnswers.metadata,
      caseReference = uaToCaseReference(userAnswers),
      personalDetails = notificationService.userAnswersToPersonalDetails(userAnswers),
      offshoreLiabilities = uaToOffshoreLiabilities(userAnswers),
      otherLiabilities = uaToOtherLiabilities(userAnswers),
      reasonForDisclosingNow = uaToReasonForDisclosingNow(userAnswers)
    )
  
  def uaToOtherLiabilities(userAnswers: UserAnswers): OtherLiabilities = 
    OtherLiabilities(
      issues = userAnswers.get(OtherLiabilityIssuesPage),
      inheritanceGift = userAnswers.get(DescribeTheGiftPage),
      other = userAnswers.get(WhatOtherLiabilityIssuesPage),
      taxCreditsReceived = userAnswers.get(DidYouReceiveTaxCreditPage)
    )

  def uaToOffshoreLiabilities(userAnswers: UserAnswers): OffshoreLiabilities = 
    OffshoreLiabilities(
      behaviour = userAnswers.get(WhyAreYouMakingThisDisclosurePage),
      excuseForNotNotifying = userAnswers.get(WhatIsYourReasonableExcusePage),
      reasonableCare = userAnswers.get(WhatReasonableCareDidYouTakePage),
      excuseForNotFiling = userAnswers.get(WhatIsYourReasonableExcuseForNotFilingReturnPage),
      whichYears = userAnswers.get(WhichYearsPage),
      youHaveNotIncludedTheTaxYear = userAnswers.get(YouHaveNotIncludedTheTaxYearPage),
      youHaveNotSelectedCertainTaxYears = userAnswers.get(YouHaveNotSelectedCertainTaxYearPage),
      taxBeforeFiveYears = userAnswers.get(TaxBeforeFiveYearsPage),
      taxBeforeSevenYears = userAnswers.get(TaxBeforeSevenYearsPage),
      taxYearLiabilities = userAnswers.get(TaxYearLiabilitiesPage),
      legalInterpretation = userAnswers.get(YourLegalInterpretationPage),
      otherInterpretation = userAnswers.get(UnderWhatConsiderationPage),
      notIncludedDueToInterpretation = userAnswers.get(HowMuchTaxHasNotBeenIncludedPage),
      maximumValueOfAssets = userAnswers.get(TheMaximumValueOfAllAssetsPage)
    )

  def uaToReasonForDisclosingNow(userAnswers: UserAnswers): ReasonForDisclosingNow = 
    ReasonForDisclosingNow(
      reason = userAnswers.get(WhyAreYouMakingADisclosurePage),
      otherReason = userAnswers.get(WhatIsTheReasonForMakingADisclosureNowPage),
      whyNotBeforeNow = userAnswers.get(WhyNotBeforeNowPage),
      receivedAdvice = userAnswers.get(DidSomeoneGiveYouAdviceNotDeclareTaxPage),
      personWhoGaveAdvice = userAnswers.get(PersonWhoGaveAdvicePage),
      adviceOnBehalfOfBusiness = userAnswers.get(AdviceBusinessesOrOrgPage),
      adviceBusinessName = userAnswers.get(AdviceBusinessNamePage),
      personProfession = userAnswers.get(AdviceProfessionPage),
      adviceGiven = userAnswers.get(AdviceGivenPage),
      whichEmail = userAnswers.get(WhichEmailAddressCanWeContactYouWithPage),
      whichPhone = userAnswers.get(WhichTelephoneNumberCanWeContactYouWithPage),
      email = userAnswers.get(WhatEmailAddressCanWeContactYouWithPage),
      telephone = userAnswers.get(WhatTelephoneNumberCanWeContactYouWithPage)
    )

  def uaToCaseReference(userAnswers: UserAnswers): CaseReference = 
    CaseReference(
      doYouHaveACaseReference = userAnswers.get(DoYouHaveACaseReferencePage),
      whatIsTheCaseReference = userAnswers.get(WhatIsTheCaseReferencePage)
    )  

}

@ImplementedBy(classOf[UAToDisclosureServiceImpl])
trait UAToDisclosureService {
  def uaToFullDisclosure(userAnswers: UserAnswers): FullDisclosure
  def uaToOtherLiabilities(userAnswers: UserAnswers): OtherLiabilities
  def uaToOffshoreLiabilities(userAnswers: UserAnswers): OffshoreLiabilities
  def uaToReasonForDisclosingNow(userAnswers: UserAnswers): ReasonForDisclosingNow 
  def uaToCaseReference(userAnswers: UserAnswers): CaseReference 
}
