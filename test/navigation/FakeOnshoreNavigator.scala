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

package navigation

import play.api.mvc.Call
import pages._
import models.{Mode, UserAnswers}

class FakeOnshoreNavigator(desiredRoute: Call) extends OnshoreNavigator {

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers, hasChanged: Boolean = true): Call =
    desiredRoute

  override def nextTaxYearLiabilitiesPage(
    currentIndex: Int,
    deduction: Boolean,
    mode: Mode,
    userAnswers: UserAnswers,
    hasAnswerChanged: Boolean = false
  ): Call =
    desiredRoute

}
