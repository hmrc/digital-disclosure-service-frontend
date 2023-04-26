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

package models

import generators.Generators
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.JsPath
import queries.{Gettable, Settable}
import pages.WhichYearsPage

import scala.util.Success

class UserAnswersSpec extends AnyFreeSpec with Matchers with Generators {

  private val id: String = "id"

  case object TestPage extends Gettable[String] with Settable[String] { override def path: JsPath = JsPath \ toString }
  case object TestPage1 extends Gettable[String] with Settable[String] { override def path: JsPath = JsPath \ toString }
  case object TestPage2 extends Gettable[String] with Settable[String] { override def path: JsPath = JsPath \ toString }
  case object TestPage3 extends Gettable[String] with Settable[String] { override def path: JsPath = JsPath \ toString }

  case object TestSeqPage extends Gettable[Set[String]] with Settable[Set[String]] { override def path: JsPath = JsPath \ toString }
  case object TestMapPage extends Gettable[Map[String, String]] with Settable[Map[String, String]] { override def path: JsPath = JsPath \ toString }


  "UserAnswers" - {

    "should set the value of an answer for a give page and get the same value" in {
      val userAnswers = UserAnswers(id, "session-123")

      val expectedValue = "value"

      val updatedUserAnswer = userAnswers.set(TestPage, expectedValue) match {
        case Success(value) => value
        case _ => fail()
      }

      val actualValue = updatedUserAnswer.get(TestPage) match {
        case Some(value) => value
        case _ => fail()
      }

      expectedValue mustBe actualValue
    }

    "should remove a value for a given Page" in {
      val answerValue = "value"

      val userAnswers = UserAnswers(id, "session-123").set(TestPage, answerValue) match {
        case Success(ua) => ua
        case _ => fail()
      }

      val updatedUserAnswer = userAnswers.remove(TestPage) match {
        case Success(ua) => ua
        case _ => fail()
      }

      val actualValueOption = updatedUserAnswer.get(TestPage)
      actualValueOption mustBe None
    }

    "should remove a list of pages" in {
      val pages = List(TestPage, TestPage1, TestPage2, TestPage3)

      val maxStringLength = 100

      val userAnswers = pages.foldLeft(UserAnswers(id, "session-123"))((ua, page) => {
          val randomString = stringsWithMaxLength(maxStringLength).sample.get
          ua.set(page, randomString).success.value
        }
      )

      val updatedAnswers = userAnswers.remove(pages) match {
        case Success(value) => value
        case _ => fail()
      }

      pages.forall(page => updatedAnswers.get[String](page).isEmpty) mustBe true
    }

  }

  "indexed functionality" - {


    "should get the value of an answer for a given page and index" in {
      val userAnswers = UserAnswers(id, "session-123").set(TestSeqPage, Set("123", "456", "789")).success.value
      userAnswers.getByIndex(TestSeqPage, 0) mustBe Some("123")
      userAnswers.getByIndex(TestSeqPage, 1) mustBe Some("456")
      userAnswers.getByIndex(TestSeqPage, 2) mustBe Some("789")
      userAnswers.getByIndex(TestSeqPage, 3) mustBe None
    }

    "should set the value of an answer for a given page and index and get the same value" in {
      val userAnswers = UserAnswers(id, "session-123")

      val expectedValue = "value"

      val updatedUserAnswer = userAnswers.setByIndex(TestSeqPage, 0, expectedValue) match {
        case Success(value) => value
        case _ => fail()
      }

      val actualValue = updatedUserAnswer.getByIndex(TestSeqPage, 0) match {
        case Some(value) => value
        case _ => fail()
      }

      expectedValue mustBe actualValue
    }

    "should add a value to a set for a given page and get the same value" in {
      val userAnswers = UserAnswers(id, "session-123")

      val expectedValue = "value"

      val updatedUserAnswer = userAnswers.addToSet(TestSeqPage, expectedValue) match {
        case Success(value) => value
        case _ => fail()
      }

      val actualValue = updatedUserAnswer.getByIndex(TestSeqPage, 0) match {
        case Some(value) => value
        case _ => fail()
      }

      expectedValue mustBe actualValue
    }

    "should remove a value for a given Page" in {
      val userAnswers = UserAnswers(id, "session-123").set(TestSeqPage, Set("123", "456", "789")).success.value

      val updatedUserAnswer = userAnswers.removeByIndex(TestSeqPage, 2) match {
        case Success(ua) => ua
        case _ => fail()
      }

      val actualValueOption = updatedUserAnswer.getByIndex(TestSeqPage, 2)
      actualValueOption mustBe None
    }

  }

  "map functionality" - {


    "should get the value of an answer for a given page and index" in {
      val userAnswers = UserAnswers(id, "session-123").set(TestMapPage, Map("1" -> "123", "2" -> "456", "3" -> "789")).success.value
      userAnswers.getByKey(TestMapPage, "1") mustBe Some("123")
      userAnswers.getByKey(TestMapPage, "2") mustBe Some("456")
      userAnswers.getByKey(TestMapPage, "3") mustBe Some("789")
      userAnswers.getByKey(TestMapPage, "4") mustBe None
    }

    "should set the value of an answer for a given page and index and get the same value" in {
      val userAnswers = UserAnswers(id, "session-123")

      val expectedValue = "value"

      val updatedUserAnswer = userAnswers.setByKey(TestMapPage, "1", expectedValue) match {
        case Success(value) => value
        case _ => fail()
      }

      val actualValue = updatedUserAnswer.getByKey(TestMapPage, "1") match {
        case Some(value) => value
        case _ => fail()
      }

      expectedValue mustBe actualValue
    }

    "should remove a value for a given Page" in {
      val userAnswers = UserAnswers(id, "session-123").set(TestMapPage, Map("1" -> "123", "2" -> "456", "3" -> "789")).success.value

      val updatedUserAnswer = userAnswers.removeByKey(TestMapPage, "3") match {
        case Success(ua) => ua
        case _ => fail()
      }

      val actualValueOption = updatedUserAnswer.getByKey(TestMapPage, "3")
      actualValueOption mustBe None
    }

  }

  "inverselySortedOffshoreTaxYears" - {

    "should return years from WhichYearsPage in reverse chronological order" in {
      val whichYears: Set[OffshoreYears] = Set(TaxYearStarting(2020), TaxYearStarting(2018), TaxYearStarting(2021), TaxYearStarting(2019))
      val userAnswers = UserAnswers(id, "session-123").set(WhichYearsPage, whichYears).success.value

      userAnswers.inverselySortedOffshoreTaxYears mustBe Some(Seq(TaxYearStarting(2021), TaxYearStarting(2020), TaxYearStarting(2019), TaxYearStarting(2018)))
    }

    "should remove all values which aren't TaxYearStarting" in {
      val whichYears: Set[OffshoreYears] = Set(TaxYearStarting(2020), ReasonableExcusePriorTo, CarelessPriorTo, DeliberatePriorTo)
      val userAnswers = UserAnswers(id, "session-123").set(WhichYearsPage, whichYears).success.value

      userAnswers.inverselySortedOffshoreTaxYears mustBe Some(Seq(TaxYearStarting(2020)))
    }

  }

}
