/*
 * Copyright 2022 HM Revenue & Customs
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

import org.scalacheck.Gen
import org.scalacheck.Gen.{chooseNum, listOfN, numChar, numStr}
import forms.YourPhoneNumberFormProvider

trait TelephoneNumberGenerators {

  val minimumDigitsInternationalPhoneNumber = 7
  val maxDigitsInternationalPhoneNumber = 19

  val minimumDigitsUKPhoneNumber = 9
  val maxDigitsUKPhoneNumber = 10

  def internationalPhoneNumber(doubleZero:Boolean = false): Gen[String] = {
    val prefix = if (doubleZero) "00" else "+"
    generateInternationalPhoneNumber(
      minimumDigitsInternationalPhoneNumber,
      maxDigitsInternationalPhoneNumber,
      prefix
    )
  }

  def ukPhoneNumber(): Gen[String] = {
    val prefix = "+"
    generateInternationalPhoneNumber(
      minimumDigitsUKPhoneNumber,
      maxDigitsUKPhoneNumber,
      prefix
    ) suchThat(charList => charList(1) != '0')
  }

  def invalidPhoneNumber : Gen[String] = for {
    number <- numStr
    if !number.matches(YourPhoneNumberFormProvider.telephoneRegex)
  } yield {
    number
  }

  private def generateInternationalPhoneNumber(min: Int, max: Int, prefix:String): Gen[String] = for {
    length <- chooseNum(min, max)
    number <- listOfN(length, numChar)
  } yield {
    prefix + number.mkString
  }

}
