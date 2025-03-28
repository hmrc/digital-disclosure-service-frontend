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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json._

class YesNoOrUnsureSpec extends AnyWordSpec with Matchers {

  case class TestWrapper(yesNoOrUnsure: YesNoOrUnsure)
  object TestWrapper {
    implicit val format: OFormat[TestWrapper] = Json.format[TestWrapper]
  }

  "reads" should {
    "convert JsString Yes to Yes" in {
      val actual = Json.parse("""{"yesNoOrUnsure": "Yes"}""")
      actual.validate[TestWrapper] shouldEqual JsSuccess(TestWrapper(YesNoOrUnsure.Yes))
    }

    "convert JsString No to No" in {
      val actual = Json.parse("""{"yesNoOrUnsure": "No"}""")
      actual.validate[TestWrapper] shouldEqual JsSuccess(TestWrapper(YesNoOrUnsure.No))
    }

    "convert JsString Unsure to Unsure" in {
      val actual = Json.parse("""{"yesNoOrUnsure": "Unsure"}""")
      actual.validate[TestWrapper] shouldEqual JsSuccess(TestWrapper(YesNoOrUnsure.Unsure))
    }

    "convert other JsString to JsError" in {
      val actual = Json.parse("""{"yesNoOrUnsure": "Other"}""")
      actual.validate[TestWrapper] shouldEqual JsError(JsPath \ "yesNoOrUnsure", "error.invalid")
    }

  }

  "writes" should {
    "convert Yes to JsString Yes" in {
      val actual = TestWrapper(YesNoOrUnsure.Yes)
      Json.toJson(actual) shouldEqual Json.parse("""{"yesNoOrUnsure": "Yes"}""")
    }

    "convert No to JsString No" in {
      val actual = TestWrapper(YesNoOrUnsure.No)
      Json.toJson(actual) shouldEqual Json.parse("""{"yesNoOrUnsure": "No"}""")
    }

    "convert Unsure to JsString Unsure" in {
      val actual = TestWrapper(YesNoOrUnsure.Unsure)
      Json.toJson(actual) shouldEqual Json.parse("""{"yesNoOrUnsure": "Unsure"}""")
    }

  }

}
