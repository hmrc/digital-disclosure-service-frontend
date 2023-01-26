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

package models.store.disclosure

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import models.OtherLiabilityIssues

class OtherLiabilitiesSpec extends AnyFreeSpec with Matchers with OptionValues {

  "isComplete" - {

    "must return true where they have answered necessary questions" in {
      val otherLiabilities = OtherLiabilities(Some(Set(OtherLiabilityIssues.VatIssues)), None, None, Some(true))
      otherLiabilities.isComplete mustBe true
    }

    "must return true where they have answered all questions" in {
      val otherLiabilities = OtherLiabilities(Some(Set(OtherLiabilityIssues.Other, OtherLiabilityIssues.InheritanceTaxIssues)), Some("Some gift"), Some("Some issue"), Some(false))
      otherLiabilities.isComplete mustBe true
    }

    "must return false where they have not answered all necessary questions" in {
      val otherLiabilities = OtherLiabilities()
      otherLiabilities.isComplete mustBe false
    }

  }
}