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
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json}
import models.store.notification._

class DisclosureEntitySpec extends AnyWordSpec with Matchers {

  "reads" should {
    "convert JsString Individual to Individual" in {
      val actual = Json.parse("""{"entity": "Individual"}""")
      actual.validate[DisclosureEntity] shouldEqual JsSuccess(DisclosureEntity(Individual))
    }

    "convert JsString Estate to Estate" in {
      val actual = Json.parse("""{"entity": "Estate"}""")
      actual.validate[DisclosureEntity] shouldEqual JsSuccess(DisclosureEntity(Estate))
    }

    "convert JsString Company to Company" in {
      val actual = Json.parse("""{"entity": "Company"}""")
      actual.validate[DisclosureEntity] shouldEqual JsSuccess(DisclosureEntity(Company))
    }

    "convert JsString LLP to LLP" in {
      val actual = Json.parse("""{"entity": "LLP"}""")
      actual.validate[DisclosureEntity] shouldEqual JsSuccess(DisclosureEntity(LLP))
    }

    "convert JsString Trust to Trust" in {
      val actual = Json.parse("""{"entity": "Trust"}""")
      actual.validate[DisclosureEntity] shouldEqual JsSuccess(DisclosureEntity(Trust))
    }

    "convert other JsString to JsError" in {
      val actual = Json.parse("""{"entity": "Other"}""")
      actual.validate[DisclosureEntity] shouldEqual JsError(JsPath \ "entity", "error.invalid")
    }

  }

  "writes" should {
    "convert Individual to JsString Individual" in {
      val actual = DisclosureEntity(Individual)
      Json.toJson(actual) shouldEqual Json.parse("""{"entity": "Individual"}""")
    }

    "convert Estate to JsString Estate" in {
      val actual = DisclosureEntity(Estate)
      Json.toJson(actual) shouldEqual Json.parse("""{"entity": "Estate"}""")
    }

    "convert Company to JsString Company" in {
      val actual = DisclosureEntity(Company)
      Json.toJson(actual) shouldEqual Json.parse("""{"entity": "Company"}""")
    }

    "convert LLP to JsString LLP" in {
      val actual = DisclosureEntity(LLP)
      Json.toJson(actual) shouldEqual Json.parse("""{"entity": "LLP"}""")
    }

    "convert Trust to JsString Trust" in {
      val actual = DisclosureEntity(Trust)
      Json.toJson(actual) shouldEqual Json.parse("""{"entity": "Trust"}""")
    }
  }

}
