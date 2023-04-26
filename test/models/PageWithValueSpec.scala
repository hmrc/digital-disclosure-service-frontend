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

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import pages.QuestionPage
import org.scalatest.TryValues
import play.api.libs.json.JsPath

class PageWithValueSpec extends AnyFreeSpec with Matchers with TryValues  {

  case object TestPage extends QuestionPage[String] { override def path: JsPath = JsPath \ toString }
  case object TestPage2 extends QuestionPage[Int] { override def path: JsPath = JsPath \ toString }
  case object TestPage3 extends QuestionPage[Boolean] { override def path: JsPath = JsPath \ toString }

  val testPageWithValue = PageWithValue(TestPage, "123456")
  val emptyUserAnswers = UserAnswers("id", "session-123")

  "addToUserAnswers" - {

    "should set the page and value in UserAnswers" in {
      val updatedUserAnswers = testPageWithValue.addToUserAnswers(emptyUserAnswers)
      updatedUserAnswers.success.value.get(TestPage) mustEqual Some("123456")
    }
  }

  "pagesToUserAnswers" - {

    "should set all pages and values entered in UserAnswers" in {
      val pagesWithValues = List(
        PageWithValue(TestPage, "123"),
        PageWithValue(TestPage2, 123),
        PageWithValue(TestPage3, true)
      )

      val updatedUserAnswers = PageWithValue.pagesToUserAnswers(pagesWithValues, emptyUserAnswers).success.value
      updatedUserAnswers.get(TestPage) mustEqual Some("123")
      updatedUserAnswers.get(TestPage2) mustEqual Some(123)
      updatedUserAnswers.get(TestPage3) mustEqual Some(true)
    }

    "should not update answers where Nil is passed in" in {
      val updatedUserAnswers = PageWithValue.pagesToUserAnswers(Nil, emptyUserAnswers).success.value
      updatedUserAnswers mustEqual emptyUserAnswers
    }

  }
}
