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

class ReferenceServiceSpec extends AnyWordSpec with Matchers {

  val sut = new ReferenceServiceImpl

  "generateReference" should {

    "return a random ID" in {
      sut.generateReference should not be sut.generateReference
    }

    "return an ID of length 12 separated by dashes" in {
      sut.generateReference should startWith regex "^([A-Z0-9]{4})-([A-Z0-9]{4})-([A-Z0-9]{4})$"
    }

  }

}
