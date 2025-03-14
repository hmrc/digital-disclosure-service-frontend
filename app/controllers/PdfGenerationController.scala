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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, ResponseHeader, Result}
import services.{SubmissionPDFService, SubmissionStoreService, SubmissionToUAService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import scala.concurrent.{ExecutionContext, Future}
import play.api.http.HttpEntity
import models.store.Notification

class PdfGenerationController @Inject() (
  override val messagesApi: MessagesApi,
  pdfService: SubmissionPDFService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredActionEvenSubmitted,
  submissionStoreService: SubmissionStoreService,
  submissionToUAService: SubmissionToUAService,
  val controllerComponents: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def generate: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    pdfService.generatePdf(request.userAnswers).map { byteString =>
      Result(
        header = ResponseHeader(200, Map.empty),
        body = HttpEntity.Strict(byteString, None)
      )
      Ok(byteString)
    }
  }

  def generateForSubmissionId(id: String): Action[AnyContent] = (identify andThen getData).async { implicit request =>
    for {
      submissionOpt <- submissionStoreService.getSubmission(request.userId, id)
      submission     = submissionOpt.getOrElse(Notification(request.userId, id))
      userAnswers   <- Future.fromTry(submissionToUAService.submissionToUa(request.sessionId, submission))
      byteString    <- pdfService.generatePdf(userAnswers)
    } yield Ok(byteString)

  }

}
