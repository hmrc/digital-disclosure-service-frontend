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
import models.store.Metadata
import models.address._
import services.UAToNotificationServiceImpl
import java.time.LocalDateTime

class NavigatorSpec extends SpecBase {

  val uaToNotificationService = new UAToNotificationServiceImpl
  val navigator = new NavigatorImpl(uaToNotificationService)

  "Navigator" - {

    "nextPage" - {

      "must go from a page that doesn't exist in the route map to Index" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, UserAnswers("id")) mustBe controllers.routes.IndexController.onPageLoad
      }

      "must go from the MakeANotificationOrDisclosure page to the ReceivedALetter controller when select an MakeANotification" in {
        val ua = UserAnswers(id = "id", submissionType = SubmissionType.Notification)
        navigator.nextPage(MakeANotificationOrDisclosurePage, ua) mustBe routes.ReceivedALetterController.onPageLoad(NormalMode)
      }

      "must go from the MakeANotificationOrDisclosure page to the TaskList controller when select an MakeADisclosure" in {
        val ua = UserAnswers(id = "id", submissionType = SubmissionType.Disclosure)
        navigator.nextPage(MakeANotificationOrDisclosurePage, ua) mustBe controllers.routes.TaskListController.onPageLoad
      }

      "must go from the NotificationStarted page to the Notification CYA controller when they selected to continue and the notification is complete" in {
        val address = Address("line 1", Some("line 2"), Some("line 3"), Some("line 4"), Some("postcode"), Country("GBR"))
        val contactSet: Set[HowWouldYouPreferToBeContacted] = Set(HowWouldYouPreferToBeContacted.Email)
        val incomeSet: Set[IncomeOrGainSource] = Set(IncomeOrGainSource.Dividends)
        val completedNotificationUA = (for {
          ua1 <- UserAnswers(id = "id", submissionType = SubmissionType.Notification).set(ReceivedALetterPage, true)
          ua2 <- ua1.set(ReceivedALetterPage, false)
          ua3 <- ua2.set(RelatesToPage, RelatesTo.ATrust)
          ua4 <- ua3.set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
          ua5 <- ua4.set(OffshoreLiabilitiesPage, true)
          ua6 <- ua5.set(OnshoreLiabilitiesPage, true)
          ua7 <- ua6.set(WhatIsTheTrustNamePage, "Some trust")
          ua8 <- ua7.set(TrustAddressLookupPage, address)
          ua9 <- ua8.set(WhatIsYourFullNamePage, "My name")
          ua10 <- ua9.set(HowWouldYouPreferToBeContactedPage, contactSet)
          ua11 <- ua10.set(YourEmailAddressPage, "My email")
          ua12 <- ua11.set(IncomeOrGainSourcePage, incomeSet)
          finalUa <- ua12.set(YourAddressLookupPage, address)
        } yield finalUa).success.value

        navigator.nextPage(NotificationStartedPage, completedNotificationUA) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from the NotificationStarted page to the ReceivedALetterController when they selected to continue and the notification is not complete" in {
        val ua = UserAnswers(id = "id", submissionType = SubmissionType.Notification)
        navigator.nextPage(NotificationStartedPage, ua) mustBe routes.ReceivedALetterController.onPageLoad(NormalMode)
      }

      "must go from the NotificationStarted page to the TaskList controller when they selected to start a disclosure" in {
        val ua = UserAnswers(id = "id", submissionType = SubmissionType.Disclosure)
        navigator.nextPage(NotificationStartedPage, ua) mustBe controllers.routes.TaskListController.onPageLoad
      }

      "must go from NotificationSubmittedPage to the task list" in {
        val ua = UserAnswers(id = "id", submissionType = SubmissionType.Disclosure)
        navigator.nextPage(NotificationSubmittedPage, ua) mustBe controllers.routes.TaskListController.onPageLoad
      }

    }

    "indexNextPage" - {

      "must go from the Index page to the TaskListController when the store has a Disclosure started" in {
        val ua = UserAnswers(id = "id", submissionType = SubmissionType.Disclosure)
        navigator.indexNextPage(Some(ua)) mustBe controllers.routes.TaskListController.onPageLoad
      }

      "must go from the Index page to the NotificationSubmittedController when the store has a Notification which has been submitted" in {
        val ua = UserAnswers(id = "id", submissionType = SubmissionType.Notification, metadata = Metadata(Some("Some ref"), Some(LocalDateTime.now())))
        navigator.indexNextPage(Some(ua)) mustBe controllers.routes.NotificationSubmittedController.onPageLoad
      }

      "must go from the Index page to the NotificationStartedController when the store has a Notification which has been submitted" in {
        val ua = UserAnswers(id = "id", submissionType = SubmissionType.Notification, metadata = Metadata(None, None))
        navigator.indexNextPage(Some(ua)) mustBe controllers.routes.NotificationStartedController.onPageLoad
      }

      "must go from the Index page to the MakeANotificationOrDisclosureController when the store is empty" in {
        navigator.indexNextPage(None) mustBe controllers.routes.MakeANotificationOrDisclosureController.onPageLoad
      }

    }

  }


}
