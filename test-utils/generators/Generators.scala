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

import forms.mappings.FormBindConstants.invalidUnicodeCharacters

import java.time.{Instant, LocalDate, ZoneOffset}
import org.scalacheck.Arbitrary._
import org.scalacheck.Gen._
import org.scalacheck.{Gen, Shrink}
import uk.gov.hmrc.domain.Nino

trait Generators
    extends UserAnswersGenerator
    with PageGenerators
    with ModelGenerators
    with UserAnswersEntryGenerators
    with EmailGenerators
    with TelephoneNumberGenerators
    with VATGenerators
    with CaseReferenceGenerators {

  implicit val dontShrink: Shrink[String] = Shrink.shrinkAny

  def genIntersperseString(gen: Gen[String], value: String, frequencyV: Int = 1, frequencyN: Int = 10): Gen[String] = {

    val genValue: Gen[Option[String]] = Gen.frequency(frequencyN -> None, frequencyV -> Gen.const(Some(value)))

    for {
      seq1 <- gen
      seq2 <- Gen.listOfN(seq1.length, genValue)
    } yield seq1.toSeq.zip(seq2).foldLeft("") {
      case (acc, (n, Some(v))) =>
        acc + n + v
      case (acc, (n, _))       =>
        acc + n
    }
  }

  def intsInRangeWithCommas(min: Int, max: Int): Gen[String] = {
    val numberGen = choose[Int](min, max).map(_.toString)
    genIntersperseString(numberGen, ",")
  }

  def decimalsInRangeWithCommas(min: BigDecimal, max: BigDecimal): Gen[String] = {
    val numberGen = choose[BigDecimal](min, max).map(_.toString)
    genIntersperseString(numberGen, ",")
  }

  def decimalsInRangeWithCommasWithPercentage(min: BigDecimal, max: BigDecimal): Gen[String] = {
    val numberGen = for {
      bool  <- arbitrary[Boolean]
      str   <- choose[BigDecimal](min, max).map(_.toString)
      answer = if (bool) "%" + str else str
    } yield answer
    genIntersperseString(numberGen, ",")
  }

  def intsLargerThanMaxValue: Gen[BigInt] =
    arbitrary[BigInt] suchThat (x => x > Int.MaxValue)

  def intsSmallerThanMinValue: Gen[BigInt] =
    arbitrary[BigInt] suchThat (x => x < Int.MinValue)

  def nonNumerics: Gen[String] =
    alphaStr suchThat (_.size > 0)

  def decimals: Gen[String] =
    arbitrary[BigDecimal]
      .suchThat(_.abs < Int.MaxValue)
      .suchThat(!_.isValidInt)
      .map("%f".format(_))

  def intsBelowValue(value: Int): Gen[Int] =
    arbitrary[Int] suchThat (_ < value)

  def intsAboveValue(value: Int): Gen[Int] =
    arbitrary[Int] suchThat (_ > value)

  def intsOutsideRange(min: Int, max: Int): Gen[Int] =
    arbitrary[Int] suchThat (x => x < min || x > max)

  def bigintsInRangeWithCommas(min: BigInt, max: BigInt): Gen[String] = {
    val numberGen = choose[BigInt](min, max).map(_.toString)
    genIntersperseString(numberGen, ",")
  }

  def bigintsInRange(min: BigInt, max: BigInt): Gen[String] =
    choose[BigInt](min, max).map(_.toString)

  def bigintsInRangeWithPound(min: BigInt, max: BigInt): Gen[String] = for {
    bool  <- arbitrary[Boolean]
    str   <- choose[BigInt](min, max).map(_.toString)
    answer = if (bool) "£" + str else str
  } yield answer

  def bigintsBelowZero: Gen[BigInt] =
    arbitrary[Int].suchThat(_ < 0).map(BigInt(_))

  def bigintsBelowValue(value: BigInt): Gen[BigInt] =
    arbitrary[Int].suchThat(int => (int * -1) < value).map(int => BigInt(int * -1))

  def bigintsAboveValue(value: BigInt): Gen[BigInt] =
    arbitrary[BigInt] suchThat (_ > value)

  def bigintsOutsideRange(min: BigInt, max: BigInt): Gen[BigInt] =
    arbitrary[BigInt] suchThat (x => x < min || x > max)

  def bigdecimalsBelowZero: Gen[BigDecimal] =
    arbitrary[BigDecimal].suchThat(_ < 0)

  def bigdecimalsBelowValue(value: BigDecimal): Gen[BigDecimal] =
    arbitrary[BigDecimal].suchThat(decimal => (decimal * -1) < value).map(decimal => decimal * -1)

  def bigdecimalsAboveValue(value: BigDecimal): Gen[BigDecimal] =
    arbitrary[BigDecimal] suchThat (_ > value)

  def bigdecimalsOutsideRange(min: BigDecimal, max: BigDecimal): Gen[BigDecimal] =
    arbitrary[BigDecimal] suchThat (x => x < min || x > max)

  def nonBooleans: Gen[String] =
    arbitrary[String]
      .suchThat(_.nonEmpty)
      .suchThat(_ != "true")
      .suchThat(_ != "false")

  def nonEmptyString: Gen[String] =
    arbitrary[String] suchThat (_.trim.nonEmpty)

  def stringsWithMaxLength(maxLength: Int): Gen[String] =
    stringsExcludingCRWithLengthBetween(1, maxLength)

  def stringsLongerThan(minLength: Int): Gen[String] = {
    val maxLength = (minLength * 2).max(100)
    stringsExcludingCRWithLengthBetween(minLength, maxLength)
  }

  def stringsWithLengthBetween(minLength: Int, maxLength: Int): Gen[String] = for {
    length <- Gen.chooseNum(minLength + 1, maxLength)
    chars  <- listOfN(length, arbitrary[Char])
  } yield chars.mkString

  def stringsExcludingCRWithLengthBetween(minLength: Int, maxLength: Int): Gen[String] = for {
    length <- Gen.chooseNum(minLength + 1, maxLength)
    chars  <- listOfN(
                length,
                arbitrary[Char] suchThat (char => char != '\r' && !invalidUnicodeCharacters.contains(char.toString))
              )
  } yield chars.mkString

  def stringsExceptSpecificValues(excluded: Seq[String]): Gen[String] =
    nonEmptyString suchThat (!excluded.contains(_))

  def oneOf[T](xs: Seq[Gen[T]]): Gen[T] =
    if (xs.isEmpty) {
      throw new IllegalArgumentException("oneOf called on empty collection")
    } else {
      val vector = xs.toVector
      choose(0, vector.size - 1).flatMap(vector(_))
    }

  def datesBetween(min: LocalDate, max: LocalDate): Gen[LocalDate] = {

    def toMillis(date: LocalDate): Long =
      date.atStartOfDay.atZone(ZoneOffset.UTC).toInstant.toEpochMilli

    Gen.choose(toMillis(min), toMillis(max)).map { millis =>
      Instant.ofEpochMilli(millis).atOffset(ZoneOffset.UTC).toLocalDate
    }
  }

  def generateValidUTR(length: Int): Gen[String] = for {
    digits <- listOfN(length, numChar)
  } yield digits.mkString

  def generateInvalidLengthUTR(length: Int): Gen[String] = numStr
    .suchThat(s => s.nonEmpty && (s.length != length))

  def generateIllegalCharUTR(length: Int): Gen[String] = for {
    str <- listOfN(length, alphaChar)
  } yield str.mkString

  def nino(): Gen[String] = for {
    bool <- arbitrary[Boolean]
    upper = NinoGenerator.generateNino
    nino  = if (bool) upper.toLowerCase else upper
  } yield nino

  def invalidNino(): Gen[String] = Gen.alphaNumStr
    .suchThat(_.trim.nonEmpty)
    .suchThat(str => !Nino.isValid(str.toUpperCase))
}
