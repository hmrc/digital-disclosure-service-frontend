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
import java.time.LocalDate
import models.address._
import models.store.YesNoOrUnsure._

class AboutYouSpec extends AnyFreeSpec with Matchers with OptionValues {

  val address = Address("line 1", Some("line 2"), Some("line 3"), Some("line 4"), Some("postcode"), Country("GBR"))

  "isComplete" - {

    "must return true where they are NOT the individual and have answered full name, contact preferences, email, and address" in {
      val aboutYou = AboutYou(Some("name"), None, Some("email"), None, None, Some(ContactPreferences(Set(Email))), None, None, None, None, None, None, Some(address))
      aboutYou.isComplete(false) mustBe true
    }

    "must return true where they are NOT the individual and have answered full name, contact preferences, phone, and address" in {
      val aboutYou = AboutYou(Some("name"), Some("0123"), None, None, None, Some(ContactPreferences(Set(Telephone))), None, None, None, None, None, None, Some(address))
      aboutYou.isComplete(false) mustBe true
    }

    "must return false where they are NOT the individual and have not answered a form of contact" in {
      val aboutYou = AboutYou(Some("name"), None, None, None, None, Some(ContactPreferences(Set(Telephone))), None, None, None, None, None, None, Some(address))
      aboutYou.isComplete(false) mustBe false
    }

    "must return false where they are NOT the individual and have not answered all necessary questions" in {
      val aboutYou = AboutYou()
      aboutYou.isComplete(false) mustBe false
    }

    "must return true where they ARE the individual and have answered full name, contact preferences, email, and address" in {
      val aboutYou = AboutYou(Some("name"), None, Some("email"), Some(LocalDate.now), Some("mainOccupation"), Some(ContactPreferences(Set(Email))), Some(No), None, Some(No), None, Some(No), None, Some(address))
      aboutYou.isComplete(true) mustBe true
    }

    "must return true where they ARE the individual and have answered full name, contact preferences, phone, and address" in {
      val aboutYou = AboutYou(Some("name"), Some("0123"), None, Some(LocalDate.now), Some("mainOccupation"), Some(ContactPreferences(Set(Telephone))), Some(No), None, Some(No), None, Some(No), None, Some(address))
      aboutYou.isComplete(true) mustBe true
    }

    "must return true where they ARE the individual and have answered necessary questions and have a NINO" in {
      val aboutYou = AboutYou(Some("name"), Some("0123"), None, Some(LocalDate.now), Some("mainOccupation"), Some(ContactPreferences(Set(Telephone))), Some(Yes), Some("NINO"), Some(No), None, Some(No), None, Some(address))
      aboutYou.isComplete(true) mustBe true
    }

    "must return true where they ARE the individual and have answered necessary questions and have a VAT reg" in {
      val aboutYou = AboutYou(Some("name"), Some("0123"), None, Some(LocalDate.now), Some("mainOccupation"), Some(ContactPreferences(Set(Telephone))), Some(No), None, Some(Yes), Some("VAT reg"), Some(No), None, Some(address))
      aboutYou.isComplete(true) mustBe true
    }

    "must return true where they ARE the individual and have answered necessary questions and have an SAUTR" in {
      val aboutYou = AboutYou(Some("name"), Some("0123"), None, Some(LocalDate.now), Some("mainOccupation"), Some(ContactPreferences(Set(Telephone))), Some(No), None, Some(No), None, Some(Yes), Some("SAUTR"), Some(address))
      aboutYou.isComplete(true) mustBe true
    }

    "must return false where they ARE the individual and have not answered all necessary questions" in {
      val aboutYou = AboutYou()
      aboutYou.isComplete(true) mustBe false
    }

  }
}
