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

import com.google.inject.{Inject, Singleton, ImplementedBy}

import play.api.mvc.Call
import controllers.notification.routes
import pages._
import models._
import services.UAToNotificationService

@Singleton
class NavigatorImpl @Inject()(notificationDataService: UAToNotificationService) extends Navigator {

  private val normalRoutes: Page => UserAnswers => Call = {

    case MakeANotificationOrDisclosurePage => ua => ua.submissionType match {
      case SubmissionType.Notification => routes.ReceivedALetterController.onPageLoad(NormalMode)
      case SubmissionType.Disclosure => controllers.routes.TaskListController.onPageLoad
    }

    case NotificationStartedPage => ua => ua.submissionType match {
      case SubmissionType.Notification => 
        val notification = notificationDataService.userAnswersToNotification(ua)
        if (notification.isComplete) controllers.notification.routes.CheckYourAnswersController.onPageLoad
        else controllers.notification.routes.ReceivedALetterController.onPageLoad(NormalMode)
      case SubmissionType.Disclosure => 
        controllers.routes.TaskListController.onPageLoad
    }

    case NotificationSubmittedPage => _ => controllers.routes.TaskListController.onPageLoad
    
    case _ => _ => controllers.routes.IndexController.onPageLoad
  }

  def nextPage(page: Page, userAnswers: UserAnswers): Call = normalRoutes(page)(userAnswers)

  def indexNextPage(userAnswers: Option[UserAnswers]): Call = userAnswers match {
    case Some(ua) if ua.submissionType == SubmissionType.Disclosure => 
      controllers.routes.TaskListController.onPageLoad
    case Some(ua) if ua.submissionType == SubmissionType.Notification && ua.metadata.submissionTime.isDefined => 
      controllers.routes.NotificationSubmittedController.onPageLoad
    case Some(ua) if ua.submissionType == SubmissionType.Notification =>
      controllers.routes.NotificationStartedController.onPageLoad
    case _ => 
      controllers.routes.MakeANotificationOrDisclosureController.onPageLoad
  }

  def submitPage(reference: String): Call = controllers.routes.SubmittedController.onPageLoad(reference)

}


@ImplementedBy(classOf[NavigatorImpl])
trait Navigator {
  def nextPage(page: Page, userAnswers: UserAnswers): Call
  def indexNextPage(userAnswers: Option[UserAnswers]): Call 
  def submitPage(reference: String): Call
}