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

package forms.mappings

import java.time.LocalDate
import uk.gov.hmrc.domain.Nino
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationResult, ValidationError}
import uk.gov.hmrc.emailaddress.EmailAddress

trait Constraints {

  protected def firstError[A](constraints: Constraint[A]*): Constraint[A] =
    Constraint {
      input =>
        constraints
          .map(_.apply(input))
          .find(_ != Valid)
          .getOrElse(Valid)
    }

  protected def minimumValue[A](minimum: A, errorKey: String)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint {
      input =>

        import ev._

        if (input >= minimum) {
          Valid
        } else {
          Invalid(errorKey, minimum)
        }
    }

  protected def maximumValue[A](maximum: A, errorKey: String)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint {
      input =>

        import ev._

        if (input <= maximum) {
          Valid
        } else {
          Invalid(errorKey, maximum)
        }
    }

  protected def inRange[A](minimum: A, maximum: A, errorKey: String)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint {
      input =>

        import ev._

        if (input >= minimum && input <= maximum) {
          Valid
        } else {
          Invalid(errorKey, minimum, maximum)
        }
    }

  protected def regexp(regex: String, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.matches(regex) =>
        Valid
      case _ =>
        Invalid(errorKey, regex)
    }

  protected def regexpIgnoreWhiteSpaces(regex: String, errorKey: String, args: Any*): Constraint[String] =
    Constraint {
      case str if str.filterNot(_.isWhitespace).matches(regex) =>
        Valid
      case _ =>
        Invalid(errorKey, args: _*)
    }

  protected def maxLength(maximum: Int, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.length <= maximum =>
        Valid
      case _ =>
        Invalid(errorKey, maximum)
    }

  protected def maxLength(maximum: Int, errorKey: String, args: Any*): Constraint[String] =
    Constraint {
      case str if str.length <= maximum =>
        Valid
      case _ =>
        Invalid(errorKey, args: _*)
    }  

  protected def minLength(minimum: Int, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.length >= minimum =>
        Valid
      case _ =>
        Invalid(errorKey, minimum)
    }

  protected def maxDate(maximum: LocalDate, errorKey: String, args: Any*): Constraint[LocalDate] =
    Constraint {
      case date if date.isAfter(maximum) =>
        Invalid(errorKey, args: _*)
      case _ =>
        Valid
    }

  protected def minDate(minimum: LocalDate, errorKey: String, args: Any*): Constraint[LocalDate] =
    Constraint {
      case date if date.isBefore(minimum) =>
        Invalid(errorKey, args: _*)
      case _ =>
        Valid
    }

  protected def nonEmptySet(errorKey: String): Constraint[Set[_]] =
    Constraint {
      case set if set.nonEmpty =>
        Valid
      case _ =>
        Invalid(errorKey)
    }

  protected def validEmail(errorKey: String): Constraint[String] =
    Constraint {
      case str if emailValidation(str.trim) =>
        Valid
      case _ =>
        Invalid(errorKey)
    }

  private def emailValidation(email: String): Boolean = {
    EmailAddress.isValid(email) && email.split('@')(1).contains('.')
  }

  protected def validNino(errorKey: String): Constraint[String] =
    Constraint {
      case str if Nino.isValid(str.toUpperCase()) =>
        Valid
      case _ =>
        Invalid(errorKey)
    }

  protected def validUTR(length: Int, errorKey: String): Constraint[String] =
    Constraint {
      s => {
        val value = s.filterNot(c => c.isWhitespace)
        validLengthAncDigits(value, length, errorKey)

      }
    }

  protected def validVAT(length: Int, errorKey: String): Constraint[String] =
    Constraint {
      s => {
        val value = if (s.startsWith("GB")) s.split("GB")(1) else s
        validLengthAncDigits(value, length, errorKey)
      }
    }

  private def validLengthAncDigits(value: String, length: Int, errorKey: String): ValidationResult = {
    value.filterNot(c => c.isWhitespace) match {
      case str if str.length == length && str.forall(_.isDigit) => Valid
      case _ => Invalid(errorKey)
    }
  }

  protected def allOrNoneCheckboxConstraint[A](errorKey: String, singleOption: A): Constraint[Set[A]] = 
    Constraint { 
      s => {
        if (s.contains(singleOption) && s.size > 1) {
          Invalid(Seq(ValidationError(errorKey)))
        } else {
          Valid
        }
      }
    }
}
