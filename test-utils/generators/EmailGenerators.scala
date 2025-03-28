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
import org.scalacheck.Gen.{alphaChar, alphaStr, chooseNum, listOfN}
import models.email.EmailAddress

trait EmailGenerators {

  val minUsernameLength = 3
  val maxUsernameLength = 64

  val minDomainLength = 5
  val maxDomainLength = 253

  val minPointDomainPosition = 2

  def email(): Gen[String] =
    for {
      usernameLength <- chooseNum(minUsernameLength, maxUsernameLength)
      userName       <- listOfN(usernameLength, alphaChar)
      domainLength   <- chooseNum(minDomainLength, maxDomainLength)
      pointPosition  <- chooseNum(minPointDomainPosition, domainLength - 1)
      domain         <- listOfN(pointPosition, alphaChar)
      extension      <- listOfN(domainLength - pointPosition, alphaChar)
    } yield userName.mkString + "@" + domain.mkString + "." + extension.mkString

  def invalidEmail(): Gen[String] =
    for {
      str <- arbitrary[String] suchThat (_.nonEmpty)
      if !EmailAddress.isValid(str)
    } yield str

  def invalidEmailDomain(): Gen[String] =
    for {
      username <- alphaStr
      domain   <- alphaStr
    } yield username + "@" + domain
  def invalidLengthEmail(): Gen[String] = {
    val emailMaxLength = 320
    for {
      userName <- listOfN(emailMaxLength, alphaChar)
      domain   <- listOfN(emailMaxLength, alphaChar)
    } yield userName.mkString + "@" + domain.mkString + ".ext"
  }

}
