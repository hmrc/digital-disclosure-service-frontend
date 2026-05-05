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

import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary.arbitrary
import models.*
import models.address.*
import java.time.{LocalDate, ZoneOffset}
import io.github.martinhh.derived.scalacheck.deriveArbitrary
import io.github.martinhh.derived.scalacheck.anyGivenArbitrary

trait ModelGenerators {

  private val MAX_BIGINT = BigInt("999999999999999999999999")
  private val MIN_YEAR   = 2002
  private val MAX_YEAR   = 2032

  given arbitraryAreYouTheEntity: Arbitrary[AreYouTheEntity] =
    Arbitrary {
      Gen.oneOf(AreYouTheEntity.values.toSeq)
    }

  given arbitraryWhatTypeOfMortgageDidYouHave: Arbitrary[TypeOfMortgageDidYouHave] =
    Arbitrary {
      Gen.oneOf(TypeOfMortgageDidYouHave.values.toSeq)
    }

  given arbitraryDirectorLoanAccountLiabilities: Arbitrary[DirectorLoanAccountLiabilities] =
    Arbitrary {
      for {
        name              <- arbitrary[String]
        periodEnd          = LocalDate.now(ZoneOffset.UTC).minusDays(1)
        overdrawn         <- Gen.choose(BigInt(1), MAX_BIGINT)
        unpaidTax         <- Gen.choose(BigInt(1), MAX_BIGINT)
        interest          <- Gen.choose(BigInt(1), MAX_BIGINT)
        penaltyRate       <- Gen.choose(0, 200)
        penaltyRateReason <- arbitrary[String]
      } yield DirectorLoanAccountLiabilities(
        name,
        periodEnd,
        overdrawn,
        unpaidTax,
        interest,
        penaltyRate,
        penaltyRateReason
      )
    }

  given arbitraryOnshoreYears: Arbitrary[OnshoreYears] =
    Arbitrary {
      for {
        year <- Gen.choose(MIN_YEAR, MAX_YEAR)
      } yield OnshoreYearStarting(year)
    }

  given arbitraryWhatOnshoreLiabilitiesDoYouNeedToDisclose: Arbitrary[WhatOnshoreLiabilitiesDoYouNeedToDisclose] =
    Arbitrary {
      Gen.oneOf(WhatOnshoreLiabilitiesDoYouNeedToDisclose.values)
    }

  given arbitraryIncomeOrGainSource: Arbitrary[IncomeOrGainSource] =
    Arbitrary {
      Gen.oneOf(IncomeOrGainSource.values)
    }

  given arbitraryWhichTelephoneNumberCanWeContactYouWith: Arbitrary[WhichTelephoneNumberCanWeContactYouWith] =
    Arbitrary {
      Gen.oneOf(WhichTelephoneNumberCanWeContactYouWith.values.toSeq)
    }

  given arbitraryNotificationStarted: Arbitrary[NotificationStarted] =
    Arbitrary {
      Gen.oneOf(NotificationStarted.values.toSeq)
    }

  given arbitraryWhichEmailAddressCanWeContactYouWith: Arbitrary[WhichEmailAddressCanWeContactYouWith] =
    Arbitrary {
      Gen.oneOf(WhichEmailAddressCanWeContactYouWith.values)
    }
  given arbitraryAdviceGiven: Arbitrary[AdviceGiven]                                                   =
    Arbitrary {
      for {
        adviceGiven <- arbitrary[String]
        month       <- Gen.choose(1, 12)
        year        <- Gen.choose(1850, 2023)
        contactPref <- arbitrary[AdviceContactPreference]
      } yield AdviceGiven(adviceGiven, MonthYear(month, year), contactPref)
    }

  given arbitraryMonthYear: Arbitrary[MonthYear] =
    Arbitrary {
      for {
        month <- Gen.choose(1, 12)
        year  <- Gen.choose(1850, 2023)
      } yield MonthYear(month, year)
    }

  given arbitraryAdviceContactPreference: Arbitrary[AdviceContactPreference]       =
    Arbitrary(
      Gen.oneOf(
        AdviceContactPreference.values
      )
    )
  //  given arbitraryAdviceContactPreference: Arbitrary[AdviceContactPreference] =
  //    Arbitrary {
  //      Gen.oneOf(AdviceContactPreference.values)
  //    }
  given arbitraryWhyAreYouMakingADisclosure: Arbitrary[WhyAreYouMakingADisclosure] =
    Arbitrary {
      Gen.oneOf(WhyAreYouMakingADisclosure.values)
    }
  given arbitraryOtherLiabilityIssues: Arbitrary[OtherLiabilityIssues]             =
    Arbitrary {
      Gen.oneOf(OtherLiabilityIssues.values)
    }

  given arbitraryTheMaximumValueOfAllAssets: Arbitrary[TheMaximumValueOfAllAssets] =
    Arbitrary {
      Gen.oneOf(TheMaximumValueOfAllAssets.values.toSeq)
    }

  given arbitraryHowMuchTaxHasNotBeenIncluded: Arbitrary[HowMuchTaxHasNotBeenIncluded] =
    Arbitrary {
      Gen.oneOf(HowMuchTaxHasNotBeenIncluded.values.toSeq)
    }

  given abitraryTaxYearWithLiabilities: Arbitrary[TaxYearWithLiabilities] =
    Arbitrary {
      for {
        year                   <- Gen.choose(MIN_YEAR, MAX_YEAR)
        income                 <- Gen.choose(BigInt(1), MAX_BIGINT)
        chargeableTransfers    <- Gen.choose(BigInt(1), MAX_BIGINT)
        capitalGains           <- Gen.choose(BigInt(1), MAX_BIGINT)
        unpaidTax              <- Gen.choose(BigInt(1), MAX_BIGINT)
        interest               <- Gen.choose(BigInt(1), MAX_BIGINT)
        penaltyRate            <- arbitrary[Int]
        penaltyRateReason      <- arbitrary[String]
        undeclaredIncomeOrGain <- arbitrary[String]
        foreignTaxCredit       <- arbitrary[Boolean]
      } yield {
        val taxYearLiabilities = TaxYearLiabilities(
          income,
          chargeableTransfers,
          capitalGains,
          unpaidTax,
          interest,
          penaltyRate,
          penaltyRateReason,
          Some(undeclaredIncomeOrGain),
          foreignTaxCredit
        )
        TaxYearWithLiabilities(TaxYearStarting(year), taxYearLiabilities)
      }
    }

  given abitraryOnshoreTaxYearWithLiabilities: Arbitrary[OnshoreTaxYearWithLiabilities] =
    Arbitrary {
      for {
        year                   <- Gen.choose(MIN_YEAR, MAX_YEAR)
        income                 <- Gen.choose(BigInt(1), MAX_BIGINT)
        capitalGains           <- Gen.choose(BigInt(1), MAX_BIGINT)
        unpaidTax              <- Gen.choose(BigInt(1), MAX_BIGINT)
        niContributions        <- Gen.choose(BigInt(1), MAX_BIGINT)
        interest               <- Gen.choose(BigInt(1), MAX_BIGINT)
        penaltyRate            <- arbitrary[Int]
        penaltyRateReason      <- arbitrary[String]
        undeclaredIncomeOrGain <- arbitrary[String]
        deduction              <- arbitrary[Boolean]
      } yield {
        val taxYearLiabilities = OnshoreTaxYearLiabilities(
          Some(income),
          Some(income),
          Some(income),
          Some(capitalGains),
          unpaidTax,
          niContributions,
          interest,
          penaltyRate,
          penaltyRateReason,
          Some(undeclaredIncomeOrGain),
          Some(deduction)
        )
        OnshoreTaxYearWithLiabilities(OnshoreYearStarting(year), taxYearLiabilities)
      }
    }

  given arbitraryYourLegalInterpretation: Arbitrary[YourLegalInterpretation] =
    Arbitrary {
      Gen.oneOf(YourLegalInterpretation.values)
    }

  given arbitraryOffshoreYears: Arbitrary[OffshoreYears] =
    Arbitrary {
      for {
        year <- Gen.choose(MIN_YEAR, MAX_YEAR)
      } yield TaxYearStarting(year)
    }

  given arbitraryWhatIsYourReasonableExcuse: Arbitrary[WhatIsYourReasonableExcuse] =
    Arbitrary {
      for {
        excuse <- arbitrary[String]
        years  <- arbitrary[String]
      } yield WhatIsYourReasonableExcuse(excuse, years)
    }

  given arbitraryWhatIsYourReasonableExcuseForNotFilingReturn: Arbitrary[WhatIsYourReasonableExcuseForNotFilingReturn] =
    Arbitrary {
      for {
        reasonableExcuse   <- arbitrary[String]
        yearsThisAppliesTo <- arbitrary[String]
      } yield WhatIsYourReasonableExcuseForNotFilingReturn(reasonableExcuse, yearsThisAppliesTo)
    }

  given arbitraryWhatReasonableCareDidYouTake: Arbitrary[WhatReasonableCareDidYouTake] =
    Arbitrary {
      for {
        reasonableCare     <- arbitrary[String]
        yearsThisAppliesTo <- arbitrary[String]
      } yield WhatReasonableCareDidYouTake(reasonableCare, yearsThisAppliesTo)
    }

  given arbitraryReasonableExcuseOnshore: Arbitrary[ReasonableExcuseOnshore] =
    Arbitrary {
      for {
        excuse <- arbitrary[String]
        years  <- arbitrary[String]
      } yield ReasonableExcuseOnshore(excuse, years)
    }

  given arbitraryReasonableExcuseForNotFilingOnshore: Arbitrary[ReasonableExcuseForNotFilingOnshore] =
    Arbitrary {
      for {
        reasonableExcuse   <- arbitrary[String]
        yearsThisAppliesTo <- arbitrary[String]
      } yield ReasonableExcuseForNotFilingOnshore(reasonableExcuse, yearsThisAppliesTo)
    }

  given arbitraryReasonableCareOnshore: Arbitrary[ReasonableCareOnshore] =
    Arbitrary {
      for {
        reasonableCare     <- arbitrary[String]
        yearsThisAppliesTo <- arbitrary[String]
      } yield ReasonableCareOnshore(reasonableCare, yearsThisAppliesTo)
    }

  given arbitraryWhyAreYouMakingThisDisclosure: Arbitrary[WhyAreYouMakingThisDisclosure] =
    Arbitrary {
      Gen.oneOf(WhyAreYouMakingThisDisclosure.values)
    }

  given arbitraryWhyAreYouMakingThisOnshoreDisclosure: Arbitrary[WhyAreYouMakingThisOnshoreDisclosure] =
    Arbitrary {
      Gen.oneOf(WhyAreYouMakingThisOnshoreDisclosure.values)
    }

  given arbitraryMakeANotificationOrDisclosure: Arbitrary[MakeANotificationOrDisclosure] =
    Arbitrary {
      Gen.oneOf(MakeANotificationOrDisclosure.values.toSeq)
    }

  given arbitraryHowWouldYouPreferToBeContacted: Arbitrary[HowWouldYouPreferToBeContacted] =
    Arbitrary {
      Gen.oneOf(HowWouldYouPreferToBeContacted.values)
    }

  given arbitraryWasThePersonRegisteredForSA: Arbitrary[WasThePersonRegisteredForSA] =
    Arbitrary {
      Gen.oneOf(WasThePersonRegisteredForSA.values.toSeq)
    }

  given arbitraryWasThePersonRegisteredForVAT: Arbitrary[WasThePersonRegisteredForVAT] =
    Arbitrary {
      Gen.oneOf(WasThePersonRegisteredForVAT.values.toSeq)
    }

  given arbitraryDidThePersonHaveNINO: Arbitrary[DidThePersonHaveNINO] =
    Arbitrary {
      Gen.oneOf(DidThePersonHaveNINO.values.toSeq)
    }

  given arbitraryIsTheIndividualRegisteredForSelfAssessment: Arbitrary[IsTheIndividualRegisteredForSelfAssessment] =
    Arbitrary {
      Gen.oneOf(IsTheIndividualRegisteredForSelfAssessment.values.toSeq)
    }

  given arbitraryIsTheIndividualRegisteredForVAT: Arbitrary[IsTheIndividualRegisteredForVAT] =
    Arbitrary {
      Gen.oneOf(IsTheIndividualRegisteredForVAT.values.toSeq)
    }

  given arbitraryDoesTheIndividualHaveNationalInsuranceNumber: Arbitrary[DoesTheIndividualHaveNationalInsuranceNumber] =
    Arbitrary {
      Gen.oneOf(DoesTheIndividualHaveNationalInsuranceNumber.values.toSeq)
    }

  given arbitraryAreYouRegisteredForSelfAssessment: Arbitrary[AreYouRegisteredForSelfAssessment] =
    Arbitrary {
      Gen.oneOf(AreYouRegisteredForSelfAssessment.values.toSeq)
    }

  given arbitraryAreYouRegisteredForVAT: Arbitrary[AreYouRegisteredForVAT] =
    Arbitrary {
      Gen.oneOf(AreYouRegisteredForVAT.values.toSeq)
    }

  given arbitraryDoYouHaveNationalInsuranceNumber: Arbitrary[DoYouHaveNationalInsuranceNumber] =
    Arbitrary {
      Gen.oneOf(DoYouHaveNationalInsuranceNumber.values.toSeq)
    }

  given arbitraryrelatesTo: Arbitrary[RelatesTo] =
    Arbitrary {
      Gen.oneOf(RelatesTo.values.toSeq)
    }

  given arbitraryCountry: Arbitrary[Map[String, config.Country]]       =
    Arbitrary {
      for {
        alpha3 <- strGen(3)
        name   <- strGen(10)
      } yield Map(alpha3.mkString -> config.Country(alpha3.mkString, name.mkString))
    }
  given arbitraryAddressLookupRequest: Arbitrary[AddressLookupRequest] =
    deriveArbitrary
  def sampleAddressLookupRequest: AddressLookupRequest                 =
    arbitrary[AddressLookupRequest].sample.getOrElse(sys.error(s"Could not generate instance"))
  def sampleAddress: Address                                           = genAddress.sample.getOrElse(sys.error(s"Could not generate instance"))

  val strGen = (n: Int) => Gen.listOfN(n, Gen.alphaChar).map(_.mkString)

  given genPostcode: Gen[String] = for {
    first <- Gen.listOfN(3, Gen.alphaNumChar)
    last  <- Gen.listOfN(3, Gen.alphaNumChar)
  } yield s"${first.mkString("")} ${last.mkString("")}"

  given genAddress: Gen[Address] =
    for {
      num      <- Gen.choose(1, 100)
      street   <- strGen(7)
      district <- Gen.option(strGen(5))
      road     <- if (district.isDefined) Gen.option(strGen(5)) else Gen.const(None)
      town     <- if (road.isDefined) Gen.option(strGen(10)) else Gen.const(None)
      postcode <- Gen.option(genPostcode)
      country  <- strGen(2)
    } yield Address(
      line1 = s"$num $street",
      line2 = district,
      line3 = road,
      line4 = town,
      postcode = postcode,
      country = Country(country)
    )

  given aritraryAddress: Arbitrary[Address] =
    Arbitrary {
      genAddress
    }

  given arbitraryCorporationTaxLiability: Arbitrary[CorporationTaxLiability] =
    Arbitrary {
      for {
        periodEnd         <- arbitrary[LocalDate]
        howMuchIncome     <- Gen.choose(BigInt(1), MAX_BIGINT)
        howMuchUnpaid     <- Gen.choose(BigInt(1), MAX_BIGINT)
        howMuchInterest   <- Gen.choose(BigInt(1), MAX_BIGINT)
        penaltyRate       <- Gen.choose(1, 200)
        penaltyRateReason <- arbitrary[String]
      } yield CorporationTaxLiability(
        periodEnd,
        howMuchIncome,
        howMuchUnpaid,
        howMuchInterest,
        penaltyRate,
        penaltyRateReason
      )
    }
  given arbitraryNoLongerBeingLetOut: Arbitrary[NoLongerBeingLetOut]         =
    Arbitrary {
      for {
        stopDate                  <- arbitrary[LocalDate]
        whatHasHappenedToProperty <- arbitrary[String]
      } yield NoLongerBeingLetOut(stopDate, whatHasHappenedToProperty)
    }

}
