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
import models._
import models.address._
import org.scalacheck.magnolia.Typeclass
import org.scalacheck.magnolia.gen
import java.time.{LocalDate, ZoneOffset}


trait ModelGenerators {

  implicit lazy val arbitraryWhatTypeOfMortgageDidYouHave: Arbitrary[TypeOfMortgageDidYouHave] =
    Arbitrary {
      Gen.oneOf(TypeOfMortgageDidYouHave.values.toSeq)
    }

  implicit lazy val arbitraryDirectorLoanAccountLiabilities: Arbitrary[DirectorLoanAccountLiabilities] =
    Arbitrary {
      for {
        name <- arbitrary[String]
        periodEnd = LocalDate.now(ZoneOffset.UTC).minusDays(1)
        overdrawn <- Gen.choose(BigInt(1), BigInt("9999999999999999999"))
        unpaidTax <- Gen.choose(BigInt(1), BigInt("9999999999999999999"))
        interest <- Gen.choose(BigInt(1), BigInt("9999999999999999999"))
        penaltyRate <- Gen.choose(0, 200)
        penaltyRateReason <- arbitrary[String]
      } yield DirectorLoanAccountLiabilities(name, periodEnd, overdrawn, unpaidTax, interest, penaltyRate, penaltyRateReason)
    }

  implicit lazy val arbitraryOnshoreYears: Arbitrary[OnshoreYears] =
    Arbitrary {
      for {
        year <- Gen.choose(2002, 2032)
      } yield OnshoreYearStarting(year)
    }

  implicit lazy val arbitraryWhatOnshoreLiabilitiesDoYouNeedToDisclose: Arbitrary[WhatOnshoreLiabilitiesDoYouNeedToDisclose] =
    Arbitrary {
      Gen.oneOf(WhatOnshoreLiabilitiesDoYouNeedToDisclose.values)
    }

  implicit lazy val arbitraryIncomeOrGainSource: Arbitrary[IncomeOrGainSource] =
    Arbitrary {
      Gen.oneOf(IncomeOrGainSource.values)
    }

  implicit lazy val arbitraryWhereDidTheUndeclaredIncomeOrGainIncluded: Arbitrary[WhereDidTheUndeclaredIncomeOrGainIncluded] =
    Arbitrary {
      Gen.oneOf(WhereDidTheUndeclaredIncomeOrGainIncluded.values)
    }

  implicit lazy val arbitraryWhichTelephoneNumberCanWeContactYouWith: Arbitrary[WhichTelephoneNumberCanWeContactYouWith] =
    Arbitrary {
      Gen.oneOf(WhichTelephoneNumberCanWeContactYouWith.values.toSeq)
    }

  implicit lazy val arbitraryNotificationStarted: Arbitrary[NotificationStarted] =
    Arbitrary {
      Gen.oneOf(NotificationStarted.values.toSeq)
    }

  implicit lazy val arbitraryWhichEmailAddressCanWeContactYouWith: Arbitrary[WhichEmailAddressCanWeContactYouWith] =
    Arbitrary {
      Gen.oneOf(WhichEmailAddressCanWeContactYouWith.values.toSeq)
    }

  implicit lazy val arbitraryAdviceGiven: Arbitrary[AdviceGiven] =
    Arbitrary {
      for {
        adviceGiven <- arbitrary[String]
        month <- Gen.choose(1, 12)
        year <- Gen.choose(1850, 2023)
        contactPref <- arbitrary[AdviceContactPreference]
      } yield AdviceGiven(adviceGiven, MonthYear(month, year), contactPref)
    }

    implicit lazy val arbitraryMonthYear: Arbitrary[MonthYear] = 
      Arbitrary {
      for {
        month <- Gen.choose(1, 12)
        year <- Gen.choose(1850, 2023)
      } yield MonthYear(month, year)
    }

  implicit lazy val arbitraryAdviceContactPreference: Arbitrary[AdviceContactPreference] =
    Arbitrary {
      Gen.oneOf(AdviceContactPreference.values)
    }

  implicit lazy val arbitraryWhyAreYouMakingADisclosure: Arbitrary[WhyAreYouMakingADisclosure] =
    Arbitrary {
      Gen.oneOf(WhyAreYouMakingADisclosure.values)
    }

  implicit lazy val arbitraryOtherLiabilityIssues: Arbitrary[OtherLiabilityIssues] =
    Arbitrary {
      Gen.oneOf(OtherLiabilityIssues.values)
    }

  implicit lazy val arbitraryTheMaximumValueOfAllAssets: Arbitrary[TheMaximumValueOfAllAssets] =
    Arbitrary {
      Gen.oneOf(TheMaximumValueOfAllAssets.values.toSeq)
    }

  implicit lazy val arbitraryHowMuchTaxHasNotBeenIncluded: Arbitrary[HowMuchTaxHasNotBeenIncluded] =
    Arbitrary {
      Gen.oneOf(HowMuchTaxHasNotBeenIncluded.values.toSeq)
    }

  implicit lazy val abitraryTaxYearWithLiabilities: Arbitrary[TaxYearWithLiabilities] =
    Arbitrary {
      for {
        year <- Gen.choose(2002, 2032)
        income <- Gen.choose(BigInt(1), BigInt("9999999999999999999"))
        chargeableTransfers <- Gen.choose(BigInt(1), BigInt("9999999999999999999"))
        capitalGains <- Gen.choose(BigInt(1), BigInt("9999999999999999999"))
        unpaidTax <- Gen.choose(BigInt(1), BigInt("9999999999999999999"))
        interest <- Gen.choose(BigInt(1), BigInt("9999999999999999999"))
        penaltyRate <- arbitrary[Int]
        penaltyRateReason <- arbitrary[String]
        foreignTaxCredit <- arbitrary[Boolean]
      } yield {
        val taxYearLiabilities = TaxYearLiabilities(
          income, 
          chargeableTransfers,
          capitalGains,
          unpaidTax,
          interest,
          penaltyRate,
          penaltyRateReason,
          foreignTaxCredit
        )
        TaxYearWithLiabilities(TaxYearStarting(year), taxYearLiabilities)
      }
    }

  implicit lazy val abitraryOnshoreTaxYearWithLiabilities: Arbitrary[OnshoreTaxYearWithLiabilities] =
    Arbitrary {
      for {
        year <- Gen.choose(2002, 2032)
        income <- Gen.choose(BigInt(1), BigInt("9999999999999999999"))
        capitalGains <- Gen.choose(BigInt(1), BigInt("9999999999999999999"))
        unpaidTax <- Gen.choose(BigInt(1), BigInt("9999999999999999999"))
        niContributions <- Gen.choose(BigInt(1), BigInt("9999999999999999999"))
        interest <- Gen.choose(BigInt(1), BigInt("9999999999999999999"))
        penaltyRate <- arbitrary[Int]
        penaltyRateReason <- arbitrary[String]
        deduction <- arbitrary[Boolean]
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
          Some(deduction)
        )
        OnshoreTaxYearWithLiabilities(OnshoreYearStarting(year), taxYearLiabilities)
      }
    }

  implicit lazy val arbitraryYourLegalInterpretation: Arbitrary[YourLegalInterpretation] =
    Arbitrary {
      Gen.oneOf(YourLegalInterpretation.values)
    }

  implicit lazy val arbitraryOffshoreYears: Arbitrary[OffshoreYears] =
    Arbitrary {
      for {
        year <- Gen.choose(2002, 2032)
      } yield TaxYearStarting(year)
    }

  implicit lazy val arbitraryWhatIsYourReasonableExcuse: Arbitrary[WhatIsYourReasonableExcuse] =
    Arbitrary {
      for {
        excuse <- arbitrary[String]
        years <- arbitrary[String]
      } yield WhatIsYourReasonableExcuse(excuse, years)
    }
    
  implicit lazy val arbitraryWhatIsYourReasonableExcuseForNotFilingReturn: Arbitrary[WhatIsYourReasonableExcuseForNotFilingReturn] =
    Arbitrary {
      for {
        reasonableExcuse <- arbitrary[String]
        yearsThisAppliesTo <- arbitrary[String]
      } yield WhatIsYourReasonableExcuseForNotFilingReturn(reasonableExcuse, yearsThisAppliesTo)
    }

  implicit lazy val arbitraryWhatReasonableCareDidYouTake: Arbitrary[WhatReasonableCareDidYouTake] =
    Arbitrary {
      for {
        reasonableCare <- arbitrary[String]
        yearsThisAppliesTo <- arbitrary[String]
      } yield WhatReasonableCareDidYouTake(reasonableCare, yearsThisAppliesTo)
    }




  implicit lazy val arbitraryReasonableExcuseOnshore: Arbitrary[ReasonableExcuseOnshore] =
    Arbitrary {
      for {
        excuse <- arbitrary[String]
        years <- arbitrary[String]
      } yield ReasonableExcuseOnshore(excuse, years)
    }
    
  implicit lazy val arbitraryReasonableExcuseForNotFilingOnshore: Arbitrary[ReasonableExcuseForNotFilingOnshore] =
    Arbitrary {
      for {
        reasonableExcuse <- arbitrary[String]
        yearsThisAppliesTo <- arbitrary[String]
      } yield ReasonableExcuseForNotFilingOnshore(reasonableExcuse, yearsThisAppliesTo)
    }

  implicit lazy val arbitraryReasonableCareOnshore: Arbitrary[ReasonableCareOnshore] =
    Arbitrary {
      for {
        reasonableCare <- arbitrary[String]
        yearsThisAppliesTo <- arbitrary[String]
      } yield ReasonableCareOnshore(reasonableCare, yearsThisAppliesTo)
    }





  implicit lazy val arbitraryWhyAreYouMakingThisDisclosure: Arbitrary[WhyAreYouMakingThisDisclosure] =
    Arbitrary {
      Gen.oneOf(WhyAreYouMakingThisDisclosure.values)
    }

  implicit lazy val arbitraryWhyAreYouMakingThisOnshoreDisclosure: Arbitrary[WhyAreYouMakingThisOnshoreDisclosure] =
    Arbitrary {
      Gen.oneOf(WhyAreYouMakingThisOnshoreDisclosure.values)
    }

  implicit lazy val arbitraryMakeANotificationOrDisclosure: Arbitrary[MakeANotificationOrDisclosure] =
    Arbitrary {
      Gen.oneOf(MakeANotificationOrDisclosure.values.toSeq)
    }

  implicit lazy val arbitraryHowWouldYouPreferToBeContacted: Arbitrary[HowWouldYouPreferToBeContacted] =
    Arbitrary {
      Gen.oneOf(HowWouldYouPreferToBeContacted.values)
    }

  implicit lazy val arbitraryWasThePersonRegisteredForSA: Arbitrary[WasThePersonRegisteredForSA] =
    Arbitrary {
      Gen.oneOf(WasThePersonRegisteredForSA.values.toSeq)
    }

  implicit lazy val arbitraryWasThePersonRegisteredForVAT: Arbitrary[WasThePersonRegisteredForVAT] =
    Arbitrary {
      Gen.oneOf(WasThePersonRegisteredForVAT.values.toSeq)
    }

  implicit lazy val arbitraryDidThePersonHaveNINO: Arbitrary[DidThePersonHaveNINO] =
    Arbitrary {
      Gen.oneOf(DidThePersonHaveNINO.values.toSeq)
    }

  implicit lazy val arbitraryIsTheIndividualRegisteredForSelfAssessment: Arbitrary[IsTheIndividualRegisteredForSelfAssessment] =
    Arbitrary {
      Gen.oneOf(IsTheIndividualRegisteredForSelfAssessment.values.toSeq)
    }

  implicit lazy val arbitraryIsTheIndividualRegisteredForVAT: Arbitrary[IsTheIndividualRegisteredForVAT] =
    Arbitrary {
      Gen.oneOf(IsTheIndividualRegisteredForVAT.values.toSeq)
    }

  implicit lazy val arbitraryDoesTheIndividualHaveNationalInsuranceNumber: Arbitrary[DoesTheIndividualHaveNationalInsuranceNumber] =
    Arbitrary {
      Gen.oneOf(DoesTheIndividualHaveNationalInsuranceNumber.values.toSeq)
    }

  implicit lazy val arbitraryAreYouRegisteredForSelfAssessment: Arbitrary[AreYouRegisteredForSelfAssessment] =
    Arbitrary {
      Gen.oneOf(AreYouRegisteredForSelfAssessment.values.toSeq)
    }

  implicit lazy val arbitraryAreYouRegisteredForVAT: Arbitrary[AreYouRegisteredForVAT] =
    Arbitrary {
      Gen.oneOf(AreYouRegisteredForVAT.values.toSeq)
    }

  implicit lazy val arbitraryDoYouHaveNationalInsuranceNumber: Arbitrary[DoYouHaveNationalInsuranceNumber] =
    Arbitrary {
      Gen.oneOf(DoYouHaveNationalInsuranceNumber.values.toSeq)
    }

  implicit lazy val arbitraryrelatesTo: Arbitrary[RelatesTo] =
    Arbitrary {
      Gen.oneOf(RelatesTo.values.toSeq)
    }

  implicit lazy val arbitraryCountry: Arbitrary[Map[String, config.Country]] =
    Arbitrary {
      for {
        alpha3 <- strGen(3)
        name <- strGen(10)
      } yield Map(alpha3.mkString -> config.Country(alpha3.mkString, name.mkString))
    }

  implicit lazy val arbitraryAddressLookupRequest: Typeclass[AddressLookupRequest] =
    gen[AddressLookupRequest]

  def sampleAddressLookupRequest: AddressLookupRequest = arbitrary[AddressLookupRequest].sample.getOrElse(sys.error(s"Could not generate instance"))
  def sampleAddress: Address = genAddress.sample.getOrElse(sys.error(s"Could not generate instance"))

  val strGen = (n: Int) => Gen.listOfN(n, Gen.alphaChar).map(_.mkString)

  implicit lazy val genPostcode: Gen[String] = for {
    first <- Gen.listOfN(3, Gen.alphaNumChar)
    last  <- Gen.listOfN(3, Gen.alphaNumChar)
  } yield s"${first.mkString("")} ${last.mkString("")}"

  implicit lazy val genAddress: Gen[Address] =
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

  implicit lazy val aritraryAddress: Arbitrary[Address] =
    Arbitrary {
      genAddress
    }

  implicit lazy val arbitraryCorporationTaxLiability: Arbitrary[CorporationTaxLiability] =
    Arbitrary {
      for {
        periodEnd <- arbitrary[LocalDate]
        howMuchIncome <- Gen.choose(BigInt(1), BigInt("9999999999999999999"))
        howMuchUnpaid <- Gen.choose(BigInt(1), BigInt("9999999999999999999"))
        howMuchInterest <- Gen.choose(BigInt(1), BigInt("9999999999999999999"))
        penaltyRate <- Gen.choose(1, 200)
        penaltyRateReason <- arbitrary[String]
      } yield CorporationTaxLiability(periodEnd, howMuchIncome, howMuchUnpaid, howMuchInterest, penaltyRate, penaltyRateReason)
    }  

  implicit lazy val arbitraryNoLongerBeingLetOut: Arbitrary[NoLongerBeingLetOut] =
    Arbitrary {
      for {
        stopDate <- arbitrary[LocalDate]
        whatHasHappenedToProperty <- arbitrary[String]
      } yield NoLongerBeingLetOut(stopDate, whatHasHappenedToProperty)
    }    

}
