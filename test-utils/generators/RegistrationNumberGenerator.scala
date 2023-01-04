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

package generators

import forms.WhatIsTheCompanyRegistrationNumberFormProvider.registrationNumberRegex
import org.scalacheck.Gen
import org.scalacheck.Gen.{alphaChar, alphaNumStr, listOfN, numChar}

trait RegistrationNumberGenerator {

  val numCharAlphanumericRegistrationNumber = 2
  val numDigitAlphanumericRegistrationNumber = 6
  val numbDigitNumericRegistrationNumber = 8

  def generateAlphaNumericRegistrationNumber(): Gen[String] = for {
    charSection <- listOfN(numCharAlphanumericRegistrationNumber, alphaChar)
    digitSection <- listOfN(numDigitAlphanumericRegistrationNumber, numChar)
  } yield {
    charSection.mkString + digitSection.mkString
  }

  def generateNumericRegistrationNumber(): Gen[String] = for {
    numeric <- listOfN(numbDigitNumericRegistrationNumber, numChar)
  } yield {
    numeric.mkString
  }

  def generateInvalidRegistrationNumber(): Gen[String] = alphaNumStr
    .suchThat(str => str.nonEmpty && !str.matches(registrationNumberRegex))


}
