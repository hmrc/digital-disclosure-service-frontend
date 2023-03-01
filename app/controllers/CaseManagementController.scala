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
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.CaseManagementView
import services.{SubmissionToUAService, SubmissionStoreService, SessionService, CaseManagementService}
import models.store.{Notification, Submission}
import scala.concurrent.{ExecutionContext, Future}
import java.util.UUID
import models.SubmissionType

class CaseManagementController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       submissionStoreService: SubmissionStoreService,
                                       sessionService: SessionService,
                                       submissionToUAService: SubmissionToUAService,
                                       caseManagementService: CaseManagementService,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: CaseManagementView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      for {
        submissionsFromStore <- submissionStoreService.getAllSubmissions(request.userId)
        table = caseManagementService.generateCaseManagementTable(submissionsFromStore)
      } yield Ok(view(table))

  }

  def newCase: Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      for {
        _ <- sessionService.newSession(request.userId, UUID.randomUUID().toString, SubmissionType.Notification)
      } yield Redirect(controllers.routes.MakeANotificationOrDisclosureController.onPageLoad)

  }

  def navigateToSubmission(submissionId: String): Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      val submissionF: Future[Submission] = for {
        submissionOpt <- submissionStoreService.getSubmission(request.userId, submissionId)
        submission = submissionOpt.getOrElse(Notification(request.userId, submissionId))
        userAnswers <- Future.fromTry(submissionToUAService.submissionToUa(submission))
        _ <- sessionService.set(userAnswers)
      } yield submission

      for {
        submission <- submissionF
        location = caseManagementService.getRedirection(submission)
      } yield Redirect(location)

  }

}
