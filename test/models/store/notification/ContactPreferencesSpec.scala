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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsSuccess, Json}

class ContactPreferencesSpec extends AnyWordSpec with Matchers  {

  "reads" should {
    "convert Json array containing Email to Email" in {
      val actual = Json.parse("""{"preferences": ["Email"]}""")
      val set: Set[Preference] = Set(Email)
      actual.validate[ContactPreferences] shouldEqual JsSuccess(ContactPreferences(set))
    }

    "convert Json array containing Telephone to Telephone" in {
      val actual = Json.parse("""{"preferences": ["Telephone"]}""")
      val set: Set[Preference] = Set(Telephone)
      actual.validate[ContactPreferences] shouldEqual JsSuccess(ContactPreferences(set))
    }

  }

  "writes" should {
    "convert Email to Json array containing Email" in {
      val emailSet: Set[Preference] = Set(Email)
      val actual = ContactPreferences(emailSet)
      Json.toJson(actual) shouldEqual Json.parse("""{"preferences": ["Email"]}""")
    }

    "convert Telephone to Json array containing Telephone" in {
      val phoneSet: Set[Preference] = Set(Telephone)
      val actual = ContactPreferences(phoneSet)
      Json.toJson(actual) shouldEqual Json.parse("""{"preferences": ["Telephone"]}""")
    }
  }
  
}
