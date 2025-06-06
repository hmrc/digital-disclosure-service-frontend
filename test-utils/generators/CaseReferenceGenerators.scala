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

import org.scalacheck.Gen
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen.{listOfN, numChar}

trait CaseReferenceGenerators {

  val numberOfDigitsReferenceNumber = 8
  val shortPrefix                   = "CFS"
  val longPrefix                    = "CFSS"
  val caseReferenceFormatRegex      = "(?i)CFS[Ss|Cc]?[\\s]?[-]?[0-9]{7,8}\\.?$"

  def generateValidCaseReference(): Gen[String] = for {
    isWithLongPrefix <- arbitrary[Boolean]
    isWithHyphen     <- arbitrary[Boolean]
    isWithDot        <- arbitrary[Boolean]
    number           <- listOfN(numberOfDigitsReferenceNumber, numChar)
  } yield {
    val prefix = if (isWithLongPrefix) longPrefix else shortPrefix
    val hyphen = if (isWithHyphen) "-" else ""
    val dot    = if (isWithDot) "." else ""
    prefix + hyphen + number.mkString + dot
  }

  def generateInvalidCaseReference(): Gen[String] = arbitrary[String]
    .suchThat(_.nonEmpty)
    .suchThat(!_.matches(caseReferenceFormatRegex))

}
