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

package models.store

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import models.address._
import java.time.{Instant, LocalDate}
import models.store.notification._
import models.store.YesNoOrUnsure._
import models.store.disclosure._
import models._

class FullDisclosureSpec extends AnyFreeSpec with Matchers with OptionValues {

  val address = Address("line 1", Some("line 2"), Some("line 3"), Some("line 4"), Some("postcode"), Country("GBR"))

  val background                  = Background(
    Some(false),
    None,
    Some(DisclosureEntity(LLP, Some(AreYouTheEntity.YesIAm))),
    Some(false),
    None,
    Some(true),
    Some(false),
    Some(Set(IncomeOrGainSource.Dividends))
  )
  val aboutYou                    = AboutYou(
    Some("name"),
    None,
    Some("email"),
    None,
    None,
    Some(ContactPreferences(Set(Email))),
    None,
    None,
    None,
    None,
    None,
    None,
    Some(address)
  )
  val aboutTheLLP                 = AboutTheLLP(Some("name"), Some(address))
  val completedLLPPersonalDetails = PersonalDetails(background, aboutYou, None, None, None, Some(aboutTheLLP), None)

  val indBackground               = Background(
    Some(false),
    None,
    Some(DisclosureEntity(Individual, Some(AreYouTheEntity.YesIAm))),
    Some(false),
    None,
    Some(true),
    Some(false),
    Some(Set(IncomeOrGainSource.Dividends))
  )
  val indAboutYou                 = AboutYou(
    Some("name"),
    None,
    Some("email"),
    Some(LocalDate.now),
    Some("mainOccupation"),
    Some(ContactPreferences(Set(Email))),
    Some(No),
    None,
    Some(No),
    None,
    Some(No),
    None,
    Some(address)
  )
  val completedIndPersonalDetails = PersonalDetails(indBackground, indAboutYou, None, None, None, None, None)

  val liabilities                   = TaxYearLiabilities(
    income = BigInt(2000),
    chargeableTransfers = BigInt(2000),
    capitalGains = BigInt(2000),
    unpaidTax = BigInt(2000),
    interest = BigInt(2000),
    penaltyRate = 12,
    penaltyRateReason = "Reason",
    foreignTaxCredit = false
  )
  val completedOfffshoreLiabilities = OffshoreLiabilities(
    behaviour = Some(Set(WhyAreYouMakingThisDisclosure.DidNotNotifyHasExcuse)),
    whichYears = Some(Set(TaxYearStarting(2012))),
    taxYearLiabilities = Some(Map("2012" -> TaxYearWithLiabilities(TaxYearStarting(2012), liabilities))),
    countryOfYourOffshoreLiability = Some(Map()),
    legalInterpretation = Some(Set(YourLegalInterpretation.NoExclusion)),
    maximumValueOfAssets = Some(TheMaximumValueOfAllAssets.Below500k)
  )

  val completedLLPOtherLiabilities =
    OtherLiabilities(Some(Set(OtherLiabilityIssues.InheritanceTaxIssues)), Some("Some string"), None, None)
  val completedIndOtherLiabilities =
    OtherLiabilities(Some(Set(OtherLiabilityIssues.InheritanceTaxIssues)), Some("Some string"), None, Some(true))

  val completedReasonForDisclosingNow =
    ReasonForDisclosingNow(Some(Set(WhyAreYouMakingADisclosure.GovUkGuidance)), None, Some("Some reason"), Some(false))

  val completedCaseReference = CaseReference(Some(true), Some("Reference"))

  val instant = Instant.now

  "isComplete" - {

    "must return true where they are an individual and all sections are complete" in {
      val fullDisclosure = FullDisclosure(
        "userId",
        "submissionId",
        instant,
        Metadata(),
        completedCaseReference,
        completedIndPersonalDetails,
        None,
        completedOfffshoreLiabilities,
        completedIndOtherLiabilities,
        completedReasonForDisclosingNow
      )
      fullDisclosure.isComplete mustBe true
    }

    "must return true where they are not an individual and all sections are complete" in {
      val fullDisclosure = FullDisclosure(
        "userId",
        "submissionId",
        instant,
        Metadata(),
        completedCaseReference,
        completedLLPPersonalDetails,
        None,
        completedOfffshoreLiabilities,
        completedLLPOtherLiabilities,
        completedReasonForDisclosingNow
      )
      fullDisclosure.isComplete mustBe true
    }

    "must return false where the ReasonForDisclosingNow section is not complete" in {
      val fullDisclosure = FullDisclosure(
        "userId",
        "submissionId",
        instant,
        Metadata(),
        completedCaseReference,
        completedLLPPersonalDetails,
        None,
        completedOfffshoreLiabilities,
        completedLLPOtherLiabilities,
        ReasonForDisclosingNow()
      )
      fullDisclosure.isComplete mustBe false
    }

    "must return false where the OffshoreLiabilities section is not complete" in {
      val fullDisclosure = FullDisclosure(
        "userId",
        "submissionId",
        instant,
        Metadata(),
        completedCaseReference,
        completedLLPPersonalDetails,
        None,
        OffshoreLiabilities(),
        completedLLPOtherLiabilities,
        completedReasonForDisclosingNow
      )
      fullDisclosure.isComplete mustBe false
    }

    "must return false where the user is an individual and other liabilities isn't complete for an individual" in {
      val fullDisclosure = FullDisclosure(
        "userId",
        "submissionId",
        instant,
        Metadata(),
        completedCaseReference,
        completedIndPersonalDetails,
        None,
        completedOfffshoreLiabilities,
        completedLLPOtherLiabilities,
        completedReasonForDisclosingNow
      )
      fullDisclosure.isComplete mustBe false
    }

    "must return false where the personal details section is not complete" in {
      val fullDisclosure = FullDisclosure(
        "userId",
        "submissionId",
        instant,
        Metadata(),
        completedCaseReference,
        PersonalDetails(Background(), AboutYou()),
        None,
        completedOfffshoreLiabilities,
        completedIndOtherLiabilities,
        completedReasonForDisclosingNow
      )
      fullDisclosure.isComplete mustBe false
    }

    "must return false where the OtherLiabilities section is not complete" in {
      val fullDisclosure = FullDisclosure(
        "userId",
        "submissionId",
        instant,
        Metadata(),
        completedCaseReference,
        completedIndPersonalDetails,
        None,
        completedOfffshoreLiabilities,
        OtherLiabilities(),
        completedReasonForDisclosingNow
      )
      fullDisclosure.isComplete mustBe false
    }

    "must return false where the CaseReference section is not complete" in {
      val fullDisclosure = FullDisclosure(
        "userId",
        "submissionId",
        instant,
        Metadata(),
        CaseReference(),
        completedIndPersonalDetails,
        None,
        completedOfffshoreLiabilities,
        completedIndOtherLiabilities,
        completedReasonForDisclosingNow
      )
      fullDisclosure.isComplete mustBe false
    }

    "must return false where they have not answered all necessary questions" in {
      val fullDisclosure = FullDisclosure(
        "userId",
        "submissionId",
        instant,
        Metadata(),
        CaseReference(),
        PersonalDetails(Background(), AboutYou()),
        None,
        OffshoreLiabilities(),
        OtherLiabilities(),
        ReasonForDisclosingNow()
      )
      fullDisclosure.isComplete mustBe false
    }

  }
}
