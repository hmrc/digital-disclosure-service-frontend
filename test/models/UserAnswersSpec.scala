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

import scala.util.Success

class UserAnswersSpec extends AnyFreeSpec with Matchers with Generators {

  private val id: String = "id"

  case object TestPage extends Gettable[String] with Settable[String] { override def path: JsPath = JsPath \ toString }
  case object TestPage1 extends Gettable[String] with Settable[String] { override def path: JsPath = JsPath \ toString }
  case object TestPage2 extends Gettable[String] with Settable[String] { override def path: JsPath = JsPath \ toString }
  case object TestPage3 extends Gettable[String] with Settable[String] { override def path: JsPath = JsPath \ toString }


  "UserAnswers" - {

    "should set the value of an answer for a give page and get the same value" in {
      val userAnswers = UserAnswers(id)

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

      val userAnswers = UserAnswers(id).set(TestPage, answerValue) match {
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

      val userAnswers = pages.foldLeft(UserAnswers(id))((ua, page) => {
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
}
