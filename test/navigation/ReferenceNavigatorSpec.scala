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

import base.SpecBase
import models.{NormalMode, UserAnswers}
import pages.{DoYouHaveACaseReferencePage, Page, WhatIsTheCaseReferencePage}
import controllers.reference.routes

class ReferenceNavigatorSpec extends SpecBase {
  val navigator = new ReferenceNavigator

  "Reference Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {
        case object UnknownPage extends Page
        navigator.nextPage(
          UnknownPage,
          NormalMode,
          UserAnswers("id", "session-123")
        ) mustBe controllers.routes.IndexController.onPageLoad
      }

      "must go from DoYouHaveACaseReferencePage to WhatIsTheCaseReferenceController when selected Yes" in {
        val userAnswers = UserAnswers("id", "session-123").set(DoYouHaveACaseReferencePage, true).success.value
        navigator.nextPage(
          DoYouHaveACaseReferencePage,
          NormalMode,
          userAnswers
        ) mustBe routes.WhatIsTheCaseReferenceController.onPageLoad(NormalMode)
      }

      "must go from DoYouHaveACaseReferencePage to WhatIsTheCaseReferenceController when selected No" in {
        val userAnswers = UserAnswers("id", "session-123").set(DoYouHaveACaseReferencePage, false).success.value
        navigator.nextPage(
          DoYouHaveACaseReferencePage,
          NormalMode,
          userAnswers
        ) mustBe controllers.routes.TaskListController.onPageLoad
      }

      "must go from WhatIsTheCaseReferencePage to TaskListController when the user inserts a case reference" in {
        val userAnswers = UserAnswers("id", "session-123").set(WhatIsTheCaseReferencePage, "answer").success.value
        navigator.nextPage(
          WhatIsTheCaseReferencePage,
          NormalMode,
          userAnswers
        ) mustBe controllers.routes.TaskListController.onPageLoad
      }

    }
  }
}
