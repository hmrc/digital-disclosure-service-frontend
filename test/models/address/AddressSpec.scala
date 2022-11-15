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

package models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import models.address._
import models.address.Address._

class AddressSpec extends AnyWordSpec with Matchers  {

  "AddressOps" should {

    "convert an address to a List of strings" when {
      "all lines are populated" in {
        val address = Address(line1 = "line1", line2 = Some("line2"), line3 = Some("line3"), line4 = "line4", postcode = "postcode", country = Country("GBR"))
        val list: List[String] = address
        address shouldEqual List("line1", "line2", "line3", "line4", "postcode", "GBR")
      }

      "only minimal lines are populated" in {
        val address = Address(line1 = "line1", line2 = None, line3 = None, line4 = "line4", postcode = "postcode", country = Country("GBR"))
        val list: List[String] = address
        list shouldEqual List("line1", "line4", "postcode", "GBR")
      }
    }

  }
  
}
