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
import play.api.i18n.{I18nSupport, MessagesApi, Messages}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.NotificationSubmittedView
import scala.concurrent.{ExecutionContext, Future}
import models.{UserAnswers, SubmissionType, ARN}
import navigation.Navigator
import models.store.Metadata
import pages.{NotificationSubmittedPage, LetterReferencePage, DoYouHaveACaseReferencePage, WhatIsTheCaseReferencePage}
import services.{SessionService, DisclosureToUAService, AuditService}
import java.time.format.DateTimeFormatter
import java.util.Locale
import scala.util.{Try, Success}
import models.audit.DisclosureStart
import models.requests.DataRequest

class NotificationSubmittedController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionService: SessionService,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredActionEvenSubmitted,
                                       navigator: Navigator,
                                       disclosureToUAService: DisclosureToUAService,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: NotificationSubmittedView,
                                       auditService: AuditService
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      request.userAnswers match {
        case UserAnswers(_, _, _, SubmissionType.Notification, _, _, _, Metadata(Some(reference), Some(time)), _, _) =>
          val messages: Messages = messagesApi.preferred(request)
          val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale(messages.lang.code))
          val formattedDate = time.format(dateFormatter)
          Ok(view(formattedDate, reference))
        case _ =>
          Redirect(routes.IndexController.onPageLoad)
      }
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      for {
        updatedAnswers <- Future.fromTry(convertToDisclosure(request.userAnswers))
        _ = auditStartOfDisclosure
        _ <- sessionService.set(updatedAnswers)
      } yield Redirect(navigator.nextPage(NotificationSubmittedPage, updatedAnswers))
  }

  def convertToDisclosure(userAnswers: UserAnswers): Try[UserAnswers] = {
    val updatedUa = userAnswers.get(LetterReferencePage) match {
      case Some(reference) => 
        for {
          uaWithCaseRef <- userAnswers.set(DoYouHaveACaseReferencePage, true)
          finalUa <- uaWithCaseRef.set(WhatIsTheCaseReferencePage, reference)
        } yield finalUa
      case None => 
        Success(userAnswers)
    }
    updatedUa.map(_.copy(submissionType = SubmissionType.Disclosure, metadata = Metadata()))
  }

  def auditStartOfDisclosure(implicit request: DataRequest[_]) = {
    val disclosureStart = DisclosureStart(
      userId = request.userId,
      submissionId = request.userAnswers.submissionId,
      isAgent = request.isAgent,
      agentReference = request.customerId.collect{case ARN(arn) => arn},
      notificationSubmitted = true
    )
    auditService.auditDisclosureStart(disclosureStart)
  }
}
