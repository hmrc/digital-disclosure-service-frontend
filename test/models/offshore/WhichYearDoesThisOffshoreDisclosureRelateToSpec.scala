package models

import generators.ModelGenerators
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import play.api.libs.json.{JsError, JsString, Json}

class WhichYearDoesThisOffshoreDisclosureRelateToSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with OptionValues with ModelGenerators {

  "WhichYearDoesThisOffshoreDisclosureRelateTo" - {

    "must deserialise valid values" in {

      val gen = arbitrary[WhichYearDoesThisOffshoreDisclosureRelateTo]

      forAll(gen) {
        whichYearDoesThisOffshoreDisclosureRelateTo =>

          JsString(whichYearDoesThisOffshoreDisclosureRelateTo.toString).validate[WhichYearDoesThisOffshoreDisclosureRelateTo].asOpt.value mustEqual whichYearDoesThisOffshoreDisclosureRelateTo
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!WhichYearDoesThisOffshoreDisclosureRelateTo.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>

          JsString(invalidValue).validate[WhichYearDoesThisOffshoreDisclosureRelateTo] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = arbitrary[WhichYearDoesThisOffshoreDisclosureRelateTo]

      forAll(gen) {
        whichYearDoesThisOffshoreDisclosureRelateTo =>

          Json.toJson(whichYearDoesThisOffshoreDisclosureRelateTo) mustEqual JsString(whichYearDoesThisOffshoreDisclosureRelateTo.toString)
      }
    }
  }
}
