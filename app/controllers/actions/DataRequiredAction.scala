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

package controllers.actions

import javax.inject.Inject
import controllers.routes
import models.requests.{DataRequest, OptionalDataRequest}
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}
import models.SubmissionType

import scala.concurrent.{ExecutionContext, Future}

class DataRequiredActionImpl @Inject() (implicit val executionContext: ExecutionContext) extends DataRequiredAction {

  override protected def refine[A](request: OptionalDataRequest[A]): Future[Either[Result, DataRequest[A]]] =
    request.userAnswers match {
      case None                                                                                                       =>
        Future.successful(Left(Redirect(routes.IndexController.onPageLoad)))
      case Some(data) if data.submissionType == SubmissionType.Disclosure && data.metadata.submissionTime.isDefined   =>
        if (request.isAgent) Future.successful(Left(Redirect(routes.CaseManagementController.onPageLoad(1))))
        else
          Future.successful(
            Left(Redirect(routes.SubmittedController.onPageLoad(data.metadata.reference.getOrElse("-"))))
          )
      case Some(data) if data.submissionType == SubmissionType.Notification && data.metadata.submissionTime.isDefined =>
        if (request.isAgent) Future.successful(Left(Redirect(routes.CaseManagementController.onPageLoad(1))))
        else Future.successful(Left(Redirect(routes.IndexController.onPageLoad)))
      case Some(data)                                                                                                 =>
        Future.successful(
          Right(
            DataRequest(request.request, request.userId, request.sessionId, data, request.isAgent, request.customerId)
          )
        )
    }
}

trait DataRequiredAction extends ActionRefiner[OptionalDataRequest, DataRequest]

class DataRequiredActionEvenSubmittedImpl @Inject() (implicit val executionContext: ExecutionContext)
    extends DataRequiredActionEvenSubmitted {

  override protected def refine[A](request: OptionalDataRequest[A]): Future[Either[Result, DataRequest[A]]] =
    request.userAnswers match {
      case None       =>
        Future.successful(Left(Redirect(routes.IndexController.onPageLoad)))
      case Some(data) =>
        Future.successful(
          Right(
            DataRequest(request.request, request.userId, request.sessionId, data, request.isAgent, request.customerId)
          )
        )
    }
}

trait DataRequiredActionEvenSubmitted extends ActionRefiner[OptionalDataRequest, DataRequest]
