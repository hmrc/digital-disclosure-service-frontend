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
import java.time.{Instant, LocalDate}
import models.store.YesNoOrUnsure._

class NotificationSpec extends AnyFreeSpec with Matchers with OptionValues {

  val address = Address("line 1", Some("line 2"), Some("line 3"), Some("line 4"), Some("postcode"), Country("GBR"))

  "isComplete" - {

    "must return true where you are the individual and all questions are answered" in {
      val completedBackground = Background(Some(false), None, Some(DisclosureEntity(Individual, Some(true))), Some(false), None, Some(true), Some(false))
      val aboutYou = AboutYou(Some("name"), None, Some("email"), Some(LocalDate.now), Some("mainOccupation"), Some(ContactPreferences(Set(Email))), Some(No), None, Some(No), None, Some(No), None, Some(address))
      val notification = Notification("id", "notificationId", Instant.now, Metadata(), completedBackground, aboutYou, None, None, None, None, None, None)
      notification.isComplete mustBe true
    }

    "must return true where you are NOT the individual and all questions are answered" in {
      val completedBackground = Background(Some(false), None, Some(DisclosureEntity(Individual, Some(false))), Some(false), None, Some(true), Some(false))
      val aboutYou = AboutYou(Some("name"), None, Some("email"), None, None, Some(ContactPreferences(Set(Email))), None, None, None, None, None, None, Some(address))
      val aboutTheIndividual = AboutTheIndividual(Some("name"), Some(LocalDate.now), Some("mainOccupation"), Some(Yes), Some("NINO"), Some(No), None, Some(No), None, Some(address))
      val notification = Notification("id", "notificationId", Instant.now, Metadata(), completedBackground, aboutYou, Some(aboutTheIndividual), None, None, None, None, None)
      notification.isComplete mustBe true
    }

    "must return true where they are an estate and all questions are answered" in {
      val completedBackground = Background(Some(false), None, Some(DisclosureEntity(Estate, Some(true))), Some(false), None, Some(true), Some(false))
      val aboutYou = AboutYou(Some("name"), None, Some("email"), None, None, Some(ContactPreferences(Set(Email))), None, None, None, None, None, None, Some(address))
      val aboutTheEstate = AboutTheEstate(Some("name"), Some(LocalDate.now), Some("mainOccupation"), Some(Yes), Some("NINO"), Some(No), None, Some(No), None, Some(address))
      val notification = Notification("id", "notificationId", Instant.now, Metadata(), completedBackground, aboutYou, None, None, None, None, Some(aboutTheEstate), None)
      notification.isComplete mustBe true
    }

    "must return true where they are a company and all questions are answered" in {
      val completedBackground = Background(Some(false), None, Some(DisclosureEntity(Company, Some(true))), Some(false), None, Some(true), Some(false))
      val aboutYou = AboutYou(Some("name"), None, Some("email"), None, None, Some(ContactPreferences(Set(Email))), None, None, None, None, None, None, Some(address))
      val aboutTheCompany = AboutTheCompany(Some("name"), Some("Reg number"), Some(address))
      val notification = Notification("id", "notificationId", Instant.now, Metadata(), completedBackground, aboutYou, None, Some(aboutTheCompany), None, None, None, None)
      notification.isComplete mustBe true
    }

    "must return true where they are a trust and all questions are answered" in {
      val completedBackground = Background(Some(false), None, Some(DisclosureEntity(Trust, Some(true))), Some(false), None, Some(true), Some(false))
      val aboutYou = AboutYou(Some("name"), None, Some("email"), None, None, Some(ContactPreferences(Set(Email))), None, None, None, None, None, None, Some(address))
      val aboutTheTrust = AboutTheTrust(Some("name"), Some(address))
      val notification = Notification("id", "notificationId", Instant.now, Metadata(), completedBackground, aboutYou, None, None, Some(aboutTheTrust), None, None, None)
      notification.isComplete mustBe true
    }

    "must return true where they are a LLP and all questions are answered" in {
      val completedBackground = Background(Some(false), None, Some(DisclosureEntity(LLP, Some(true))), Some(false), None, Some(true), Some(false))
      val aboutYou = AboutYou(Some("name"), None, Some("email"), None, None, Some(ContactPreferences(Set(Email))), None, None, None, None, None, None, Some(address))
      val aboutTheLLP = AboutTheLLP(Some("name"), Some(address))
      val notification = Notification("id", "notificationId", Instant.now, Metadata(), completedBackground, aboutYou, None, None, None, Some(aboutTheLLP), None, None)
      notification.isComplete mustBe true
    }

    "must return false where they have not answered all necessary questions" in {
      val notification = Notification("id", "notificationId", Instant.now, Metadata(), Background(), AboutYou(), None, None, None, None, None, None)
      notification.isComplete mustBe false
    }

  }
}
