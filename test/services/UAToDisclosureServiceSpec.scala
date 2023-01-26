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
import java.time.{Instant, LocalDateTime}

class UAToDisclosureServiceSpec extends AnyWordSpec with Matchers with TryValues {

  object TestUAToNotificationService extends UAToNotificationService {
    def userAnswersToNotification(userAnswers: UserAnswers): Notification = {
      val metadata = Metadata(reference = Some("123"), submissionTime = Some(LocalDateTime.now))
      val instant = Instant.now()
      Notification("userId", "notificationId", instant, metadata, Background(), AboutYou())
    }
  }

  val emptyUA = UserAnswers("id")

  val sut = new UAToDisclosureServiceImpl(TestUAToNotificationService)

  "uaToOtherLiabilities" should {

    "populate OtherLiabilities with nothing if nothing is set" in {
      sut.uaToOtherLiabilities(emptyUA) shouldEqual OtherLiabilities()
    }

    "populate OtherLiabilities with everything that is set" in {
      val otherIssuesSet: Set[OtherLiabilityIssues] = Set(OtherLiabilityIssues.Other, OtherLiabilityIssues.InheritanceTaxIssues)
      val pages = List(
        PageWithValue(OtherLiabilityIssuesPage, otherIssuesSet),
        PageWithValue(DescribeTheGiftPage, "Some gift"),
        PageWithValue(WhatOtherLiabilityIssuesPage, "Some issue"),
        PageWithValue(DidYouReceiveTaxCreditPage, false)
      )
      val userAnswers = PageWithValue.pagesToUserAnswers(pages, emptyUA).success.value
      val expected = OtherLiabilities(
        Some(otherIssuesSet), 
        Some("Some gift"), 
        Some("Some issue"), 
        Some(false)
      )
      sut.uaToOtherLiabilities(userAnswers) shouldEqual expected
    }

  }

  "uaToOffshoreLiabilities" should {

    "populate OffshoreLiabilities with nothing if nothing is set" in {
      sut.uaToOffshoreLiabilities(emptyUA) shouldEqual OffshoreLiabilities()
    }

    "populate OffshoreLiabilities with everything that is set" in {
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
      val pages = List(
        PageWithValue(WhyAreYouMakingThisDisclosurePage, whySet),
        PageWithValue(WhatIsYourReasonableExcusePage, WhatIsYourReasonableExcuse("Some excuse", "Some years")),
        PageWithValue(WhatReasonableCareDidYouTakePage, WhatReasonableCareDidYouTake("Some excuse", "Some years")),
        PageWithValue(WhatIsYourReasonableExcuseForNotFilingReturnPage, WhatIsYourReasonableExcuseForNotFilingReturn("Some excuse", "Some years")),
        PageWithValue(WhichYearsPage, yearsSet),
        PageWithValue(TaxBeforeFiveYearsPage, "Some liabilities"),
        PageWithValue(TaxBeforeSevenYearsPage, "Some liabilities"),
        PageWithValue(TaxYearLiabilitiesPage, Map("2012" -> TaxYearWithLiabilities(TaxYearStarting(2012), liabilities))),
        PageWithValue(YourLegalInterpretationPage, interpretationSet),
        PageWithValue(UnderWhatConsiderationPage, "Some interpretation"),
        PageWithValue(HowMuchTaxHasNotBeenIncludedPage, HowMuchTaxHasNotBeenIncluded.TenThousandOrLess),
        PageWithValue(TheMaximumValueOfAllAssetsPage, TheMaximumValueOfAllAssets.TenThousandOrLess)
      )
      val userAnswers = PageWithValue.pagesToUserAnswers(pages, emptyUA).success.value
      val expected = OffshoreLiabilities(
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
      sut.uaToOffshoreLiabilities(userAnswers) shouldEqual expected
    }

  }

  "uaToReasonForDisclosingNow" should {

    "populate ReasonForDisclosingNow with nothing if nothing is set" in {
      sut.uaToReasonForDisclosingNow(emptyUA) shouldEqual ReasonForDisclosingNow()
    }

    "populate ReasonForDisclosingNow with everything that is set" in {
      val reasonSet: Set[WhyAreYouMakingADisclosure] = Set(WhyAreYouMakingADisclosure.GovUkGuidance)
      val pages = List(
        PageWithValue(WhyAreYouMakingADisclosurePage, reasonSet),
        PageWithValue(WhatIsTheReasonForMakingADisclosureNowPage, "Some other"),
        PageWithValue(WhyNotBeforeNowPage, "Some reason"),
        PageWithValue(DidSomeoneGiveYouAdviceNotDeclareTaxPage, true),
        PageWithValue(PersonWhoGaveAdvicePage, "Some guy"),
        PageWithValue(AdviceBusinessesOrOrgPage, true),
        PageWithValue(AdviceBusinessNamePage, "Some business"),
        PageWithValue(AdviceProfessionPage, "Some profession"),
        PageWithValue(AdviceGivenPage, AdviceGiven("Some advice", 12, 2012, AdviceContactPreference.No)),
        PageWithValue(CanWeUseEmailAddressToContactYouPage, false),
        PageWithValue(CanWeUseTelephoneNumberToContactYouPage, false),
        PageWithValue(WhatEmailAddressCanWeContactYouWithPage, "Email"),
        PageWithValue(WhatTelephoneNumberCanWeContactYouWithPage, "Telephone"),
      )
      val userAnswers = PageWithValue.pagesToUserAnswers(pages, emptyUA).success.value
      val expected = ReasonForDisclosingNow(
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
      sut.uaToReasonForDisclosingNow(userAnswers) shouldEqual expected
    }

  }

}