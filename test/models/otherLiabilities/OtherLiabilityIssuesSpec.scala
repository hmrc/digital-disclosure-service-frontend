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

package models

import generators.ModelGenerators
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import play.api.libs.json.{JsError, JsString, Json}

class OtherLiabilityIssuesSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with OptionValues
    with ModelGenerators {

  "OtherLiabilityIssues" - {

    "must deserialise valid values" in {

      val gen = arbitrary[OtherLiabilityIssues]

      forAll(gen) { otherLiabilityIssues =>
        JsString(otherLiabilityIssues.toString)
          .validate[OtherLiabilityIssues]
          .asOpt
          .value mustEqual otherLiabilityIssues
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!OtherLiabilityIssues.values.map(_.toString).contains(_))

      forAll(gen) { invalidValue =>
        JsString(invalidValue).validate[OtherLiabilityIssues] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = arbitrary[OtherLiabilityIssues]

      forAll(gen) { otherLiabilityIssues =>
        Json.toJson(otherLiabilityIssues) mustEqual JsString(otherLiabilityIssues.toString)
      }
    }
  }
}
