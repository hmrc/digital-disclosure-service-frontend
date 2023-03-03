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

package controllers

import controllers.actions._
import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.IndexView
import services.{SessionService, UAToNotificationService}
import config.FrontendAppConfig
import models.{UserAnswers, NormalMode, SubmissionType}
import play.api.Logging
import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.http.HeaderCarrier
import navigation.Navigator

class IndexController @Inject()(
                                val controllerComponents: MessagesControllerComponents,
                                identify: IdentifierAction,
                                getData: DataRetrievalAction,
                                view: IndexView,
                                sessionService: SessionService,
                                dataService: UAToNotificationService,
                                navigator: Navigator,
                                appConfig: FrontendAppConfig
                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  def onPageLoad: Action[AnyContent] = (identify andThen getData).async { implicit request =>

    if (appConfig.fullDisclosureJourneyEnabled) {

      if (request.isAgent) 
        Future.successful(Ok(view(controllers.routes.CaseManagementController.onPageLoad(1).url)))
      else {
        for {
          uaOpt  <- sessionService.getIndividualUserAnswers(request.userId, UserAnswers.defaultSubmissionId)
          url    = navigator.indexNextPage(uaOpt).url
          _      = uaOpt.map(sessionService.set)
        } yield Ok(view(url))
      }

    } else {

      for {
        ua <- retrieveUserAnswers(request.userId, request.userAnswers)
        updatedUa <- renewNotificationIfSubmitted(request.userId, appConfig.fullDisclosureJourneyEnabled, ua)
      } yield {

        val notification = dataService.userAnswersToNotification(updatedUa)

        val url = 
          if (notification.isComplete) controllers.notification.routes.CheckYourAnswersController.onPageLoad.url
          else controllers.notification.routes.ReceivedALetterController.onPageLoad(NormalMode).url
        
        Ok(view(url))
      }
    }

  }

  def renewNotificationIfSubmitted(userId: String, fullDisclosureJourneyEnabled: Boolean, userAnswers: UserAnswers)(implicit hc: HeaderCarrier) = {
    if (!fullDisclosureJourneyEnabled) {
      val notification = dataService.userAnswersToNotification(userAnswers)
      if (notification.metadata.submissionTime.isDefined)
        sessionService.clearAndRestartSessionAndDraft(userId, UserAnswers.defaultSubmissionId, SubmissionType.Notification)
      else 
        Future.successful(userAnswers)
    } else Future.successful(userAnswers)
  }

  def retrieveUserAnswers(userId: String, userAnswers: Option[UserAnswers])(implicit hc: HeaderCarrier): Future[UserAnswers] = {
    userAnswers match {
      case Some(ua) => 
        Future.successful(ua)
      case None => 
        sessionService.newSession(userId, UserAnswers.defaultSubmissionId, SubmissionType.Notification)
    }
  }
  
}
