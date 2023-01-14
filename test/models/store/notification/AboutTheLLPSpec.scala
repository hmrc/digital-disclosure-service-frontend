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

package models.store.notification

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import models.address._

class AboutTheLLPSpec extends AnyFreeSpec with Matchers with OptionValues {

  val address = Address("line 1", Some("line 2"), Some("line 3"), Some("line 4"), Some("postcode"), Country("GBR"))

  "isComplete" - {

    "must return true where they have answered necessary questions" in {
      val aboutTheLLP = AboutTheLLP(Some("name"), Some(address))
      aboutTheLLP.isComplete mustBe true
    }

    "must return false where they have not answered all necessary questions" in {
      val aboutTheLLP = AboutTheLLP()
      aboutTheLLP.isComplete mustBe false
    }

  }
}
