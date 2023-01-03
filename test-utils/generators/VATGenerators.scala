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

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalacheck.Gen.{alphaChar, listOfN, numChar, numStr}

trait VATGenerators {

  val vatLength = 9
  val prefix = "GB"

  def generateValidVAT(): Gen[String] = for {
    isWithPrefix <- arbitrary[Boolean]
    digits <- listOfN(vatLength, numChar)
  } yield {
    val pref = if (isWithPrefix) prefix else ""
    pref + digits.mkString
  }

  def generateInvalidLengthVAT(): Gen[String] = numStr
    .suchThat(s => s.nonEmpty && (s.length != vatLength))

  def generateIllegalCharVAT(): Gen[String] = for {
    isWithPrefix <- arbitrary[Boolean]
    str <- listOfN(vatLength, alphaChar) suchThat (!_.mkString.startsWith(prefix))
  } yield {
    val pref = if (isWithPrefix) prefix else ""
    pref + str.mkString
  }
}
