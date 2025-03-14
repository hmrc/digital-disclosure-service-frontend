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

import pages.QuestionPage
import play.api.libs.json.Writes
import scala.util.Try

final case class PageWithValue[A](page: QuestionPage[A], value: A)(implicit writes: Writes[A]) {
  def addToUserAnswers(userAnswers: UserAnswers): Try[UserAnswers] =
    userAnswers.set(page, value)
}

object PageWithValue {
  def pagesToUserAnswers(pages: List[PageWithValue[_]], userAnswers: UserAnswers): Try[UserAnswers] =
    pages.foldLeft(Try(userAnswers))((tryUa, page) => tryUa.flatMap(page.addToUserAnswers(_)))
}
