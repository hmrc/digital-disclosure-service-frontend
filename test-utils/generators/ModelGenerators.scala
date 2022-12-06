/*
 * Copyright 2022 HM Revenue & Customs
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
import scala.language.higherKinds

trait ModelGenerators {

  implicit lazy val arbitraryMakeANotificationOrDisclosure: Arbitrary[MakeANotificationOrDisclosure] =
    Arbitrary {
      Gen.oneOf(MakeANotificationOrDisclosure.values.toSeq)
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

}
