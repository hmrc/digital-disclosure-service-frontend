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
import models.store.disclosure._
import models.store.notification._
import pages._
import models._
import org.scalatest.TryValues
import java.time.Instant
import scala.util.{Success, Try}

class DisclosureToUAServiceSpec extends AnyWordSpec with Matchers with TryValues {

  object TestNotificationToUAService extends NotificationToUAService {
    def notificationToUserAnswers(notification: Notification): Try[UserAnswers] = Success(UserAnswers("Id"))
  }

  val sut = new DisclosureToUAServiceImpl(TestNotificationToUAService)

  val testNotification = Notification("userId", "notificationId", Instant.now(), Metadata(), Background(), AboutYou())

  val emptyUA = UserAnswers("id")

  "otherLiabilitiesToUa" should {

    "return no PageWithValues for an empty OtherLiabilities" in {
      val otherLiabilities = OtherLiabilities()
      val updatedUserAnswers = sut.otherLiabilitiesToUa(otherLiabilities, emptyUA).success.value
      updatedUserAnswers.get(OtherLiabilityIssuesPage)                           shouldEqual None
      updatedUserAnswers.get(DescribeTheGiftPage)                                shouldEqual None
      updatedUserAnswers.get(WhatOtherLiabilityIssuesPage)                       shouldEqual None
      updatedUserAnswers.get(DidYouReceiveTaxCreditPage)                         shouldEqual None
    }

    "return a PageWithValue for all that are set" in {
      val otherIssuesSet: Set[OtherLiabilityIssues] = Set(OtherLiabilityIssues.Other, OtherLiabilityIssues.InheritanceTaxIssues)
      val otherLiabilities = OtherLiabilities(
        Some(otherIssuesSet), 
        Some("Some gift"), 
        Some("Some issue"), 
        Some(false)
      )
      val updatedUserAnswers = sut.otherLiabilitiesToUa(otherLiabilities, emptyUA).success.value
      updatedUserAnswers.get(OtherLiabilityIssuesPage)                           shouldEqual Some(otherIssuesSet)
      updatedUserAnswers.get(DescribeTheGiftPage)                                shouldEqual Some("Some gift")
      updatedUserAnswers.get(WhatOtherLiabilityIssuesPage)                       shouldEqual Some("Some issue")
      updatedUserAnswers.get(DidYouReceiveTaxCreditPage)                         shouldEqual Some(false)
    }
  }

  "offshoreLiabilitiesToUa" should {

    "return no PageWithValues for an empty OffshoreLiabilities" in {
      val otherLiabilities = OffshoreLiabilities()
      val updatedUserAnswers = sut.offshoreLiabilitiesToUa(otherLiabilities, emptyUA).success.value
      updatedUserAnswers.get(OtherLiabilityIssuesPage)                           shouldEqual None
      updatedUserAnswers.get(DescribeTheGiftPage)                                shouldEqual None
      updatedUserAnswers.get(WhatOtherLiabilityIssuesPage)                       shouldEqual None
      updatedUserAnswers.get(DidYouReceiveTaxCreditPage)                         shouldEqual None
    }

    "return a PageWithValue for all that are set" in {
      val liabilities = TaxYearLiabilities(
        income = BigInt(2000),
        chargeableTransfers = BigInt(2000),
        capitalGains = BigInt(2000),
        unpaidTax = BigInt(2000),
        interest = BigInt(2000),
        penaltyRate = 12,
        foreignTaxCredit = false
      )
      val whySet: Set[WhyAreYouMakingThisDisclosure] = Set(WhyAreYouMakingThisDisclosure.DidNotNotifyHasExcuse)
      val yearsSet: Set[OffshoreYears] = Set(TaxYearStarting(2012))
      val interpretationSet: Set[YourLegalInterpretation] = Set(YourLegalInterpretation.AnotherIssue)
      val offshoreLiabilities = OffshoreLiabilities(
        Some(whySet), 
        Some(WhatIsYourReasonableExcuse("Some excuse", "Some years")), 
        Some(WhatReasonableCareDidYouTake("Some excuse", "Some years")), 
        Some(WhatIsYourReasonableExcuseForNotFilingReturn("Some excuse", "Some years")), 
        Some(yearsSet), 
        Some("Some liabilities"),
        Some("Some liabilities"),
        Some(Map("2012" -> TaxYearWithLiabilities(TaxYearStarting(2012), liabilities))),
        Some(interpretationSet),
        Some("Some interpretation"),
        Some(HowMuchTaxHasNotBeenIncluded.TenThousandOrLess),
        Some(TheMaximumValueOfAllAssets.TenThousandOrLess)
      )
      val updatedUserAnswers = sut.offshoreLiabilitiesToUa(offshoreLiabilities, emptyUA).success.value
      updatedUserAnswers.get(WhyAreYouMakingThisDisclosurePage)                      shouldEqual Some(whySet)
      updatedUserAnswers.get(WhatIsYourReasonableExcusePage)                         shouldEqual Some(WhatIsYourReasonableExcuse("Some excuse", "Some years"))
      updatedUserAnswers.get(WhatReasonableCareDidYouTakePage)                       shouldEqual Some(WhatReasonableCareDidYouTake("Some excuse", "Some years"))
      updatedUserAnswers.get(WhatIsYourReasonableExcuseForNotFilingReturnPage)       shouldEqual Some(WhatIsYourReasonableExcuseForNotFilingReturn("Some excuse", "Some years"))
      updatedUserAnswers.get(WhichYearsPage)                                         shouldEqual Some(yearsSet)
      updatedUserAnswers.get(TaxBeforeFiveYearsPage)                                 shouldEqual Some("Some liabilities")
      updatedUserAnswers.get(TaxBeforeSevenYearsPage)                                shouldEqual Some("Some liabilities")
      updatedUserAnswers.get(TaxYearLiabilitiesPage)                                 shouldEqual Some(Map("2012" -> TaxYearWithLiabilities(TaxYearStarting(2012), liabilities)))
      updatedUserAnswers.get(YourLegalInterpretationPage)                            shouldEqual Some(interpretationSet)
      updatedUserAnswers.get(UnderWhatConsiderationPage)                             shouldEqual Some("Some interpretation")
      updatedUserAnswers.get(HowMuchTaxHasNotBeenIncludedPage)                       shouldEqual Some(HowMuchTaxHasNotBeenIncluded.TenThousandOrLess)
      updatedUserAnswers.get(TheMaximumValueOfAllAssetsPage)                         shouldEqual Some(TheMaximumValueOfAllAssets.TenThousandOrLess)
    }
  }

  "reasonForDisclosingNowToUa" should {

    "return no PageWithValues for an empty ReasonForDisclosingNow" in {
      val reasonForDisclosingNow = ReasonForDisclosingNow()
      val updatedUserAnswers = sut.reasonForDisclosingNowToUa(reasonForDisclosingNow, emptyUA).success.value
      updatedUserAnswers.get(OtherLiabilityIssuesPage)                           shouldEqual None
      updatedUserAnswers.get(DescribeTheGiftPage)                                shouldEqual None
      updatedUserAnswers.get(WhatOtherLiabilityIssuesPage)                       shouldEqual None
      updatedUserAnswers.get(DidYouReceiveTaxCreditPage)                         shouldEqual None
    }

    "return a PageWithValue for all that are set" in {
      val reasonSet: Set[WhyAreYouMakingADisclosure] = Set(WhyAreYouMakingADisclosure.GovUkGuidance)
      val reasonForDisclosingNow = ReasonForDisclosingNow(
        Some(reasonSet), 
        Some("Some other"), 
        Some("Some reason"), 
        Some(true), 
        Some("Some guy"), 
        Some(true), 
        Some("Some business"), 
        Some("Some profession"),
        Some(AdviceGiven("Some advice", 12, 2012, AdviceContactPreference.No)),
        Some(false),
        Some(false),
        Some("Email"),
        Some("Telephone")
      )
      val updatedUserAnswers = sut.reasonForDisclosingNowToUa(reasonForDisclosingNow, emptyUA).success.value
      updatedUserAnswers.get(WhyAreYouMakingADisclosurePage)                       shouldEqual Some(reasonSet)
      updatedUserAnswers.get(WhatIsTheReasonForMakingADisclosureNowPage)           shouldEqual Some("Some other")
      updatedUserAnswers.get(WhyNotBeforeNowPage)                                  shouldEqual Some("Some reason")
      updatedUserAnswers.get(DidSomeoneGiveYouAdviceNotDeclareTaxPage)             shouldEqual Some(true)
      updatedUserAnswers.get(PersonWhoGaveAdvicePage)                              shouldEqual Some("Some guy")
      updatedUserAnswers.get(AdviceBusinessesOrOrgPage)                            shouldEqual Some(true)
      updatedUserAnswers.get(AdviceBusinessNamePage)                               shouldEqual Some("Some business")
      updatedUserAnswers.get(AdviceProfessionPage)                                 shouldEqual Some("Some profession")
      updatedUserAnswers.get(AdviceGivenPage)                                      shouldEqual Some(AdviceGiven("Some advice", 12, 2012, AdviceContactPreference.No))
      updatedUserAnswers.get(CanWeUseEmailAddressToContactYouPage)                 shouldEqual Some(false)
      updatedUserAnswers.get(CanWeUseTelephoneNumberToContactYouPage)              shouldEqual Some(false)
      updatedUserAnswers.get(WhatEmailAddressCanWeContactYouWithPage)              shouldEqual Some("Email")
      updatedUserAnswers.get(WhatTelephoneNumberCanWeContactYouWithPage)           shouldEqual Some("Telephone")
    }
  }

}