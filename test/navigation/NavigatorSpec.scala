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
import controllers.notification.routes
import pages._
import models._

import scala.util.{Success, Failure}

class NavigatorSpec extends SpecBase {

  val navigator = new Navigator

  "Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {
          case object UnknownPage extends Page
          navigator.nextPage(UnknownPage, NormalMode, UserAnswers("id")) mustBe controllers.routes.IndexController.onPageLoad
      }

      "must go from the MakeANotificationOrDisclosure page to the ReceivedALetter controller when select an MakeANotification" in {
        val ua = UserAnswers(id = "id", submissionType = SubmissionType.Notification)
        navigator.nextPage(MakeANotificationOrDisclosurePage, NormalMode, ua) mustBe routes.ReceivedALetterController.onPageLoad(NormalMode)
      }

      "must go from the MakeANotificationOrDisclosure page to the TaskList controller when select an MakeADisclosure" in {
        val ua = UserAnswers(id = "id", submissionType = SubmissionType.Disclosure)
        navigator.nextPage(MakeANotificationOrDisclosurePage, NormalMode, ua) mustBe controllers.routes.TaskListController.onPageLoad
      }
    }

    "in Check mode" - {

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, UserAnswers("id")) mustBe controllers.routes.IndexController.onPageLoad
      }
    }
  }
}
