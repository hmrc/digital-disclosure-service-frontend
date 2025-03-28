/*
 * Copyright 2025 HM Revenue & Customs
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

package models.email

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class EmailAddressSpec extends AnyFreeSpec with Matchers {

  "EmailAddress" - {

    "must accept a valid email address" in {
      val email = "test@example.com"
      val result = EmailAddress(email)
      result.value mustEqual email
    }

    "must throw IllegalArgumentException for an invalid email address" in {
      val invalidEmail = "not-an-email"
      val exception = intercept[IllegalArgumentException] {
        EmailAddress(invalidEmail)
      }
      exception.getMessage must include(s"'$invalidEmail' is not a valid email address")
    }

    "must correctly extract mailbox and domain" in {
      val email = EmailAddress("test@example.com")
      email.mailbox.value mustEqual "test"
      email.domain.value mustEqual "example.com"
    }

    "isValid must return true for valid email addresses" in {
      EmailAddress.isValid("user@example.com") mustBe true
      EmailAddress.isValid("user.name@example.com") mustBe true
      EmailAddress.isValid("user+tag@example.com") mustBe true
    }

    "isValid must return false for invalid email addresses" in {
      EmailAddress.isValid("not-an-email") mustBe false
      EmailAddress.isValid("missing-at.com") mustBe false
      EmailAddress.isValid("@missing-mailbox.com") mustBe false
    }
  }

  "Domain" - {
    "must accept a valid domain" in {
      val domain = "example.com"
      val result = Domain(domain)
      result.value mustEqual domain
    }

    "must throw IllegalArgumentException for an invalid domain" in {
      val invalidDomain = "invalid domain with spaces"
      val exception = intercept[IllegalArgumentException] {
        Domain(invalidDomain)
      }
      exception.getMessage must include(s"'$invalidDomain' is not a valid email domain")
    }
  }

  "EmailAddressValidation" - {
    "validEmail regex must match valid email addresses" in {
      val email = "user@example.com"
      email match {
        case EmailAddressValidation.validEmail(_, _) => succeed
        case _ => fail(s"Email should be valid: $email")
      }
    }

    "validEmail regex must not match invalid email addresses" in {
      val email = "not-an-email"
      email match {
        case EmailAddressValidation.validEmail(_, _) => fail(s"Email should be invalid: $email")
        case _ => succeed
      }
    }
  }
}