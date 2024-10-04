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

package connectors

import config.Service
import play.api.Configuration
import play.api.http.Status._
import play.api.libs.json.{Json, JsSuccess, JsError}
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import com.google.inject.{Inject, Singleton, ImplementedBy}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NoStackTrace
import models.store.Submission
import play.api.mvc.Result
import play.api.mvc.Results.NoContent
import java.time.Clock
import play.mvc.Http.HeaderNames.AUTHORIZATION

@Singleton
class SubmissionStoreConnectorImpl @Inject() (
                                httpClient: HttpClientV2,
                                configuration: Configuration,
                                clock: Clock
                              )(implicit ec: ExecutionContext)
  extends SubmissionStoreConnector with ConnectorErrorHandler {

  private val service: Service = configuration.get[Service]("microservice.services.digital-disclosure-service-store")
  private val baseUrl = s"${service.baseUrl}/digital-disclosure-service-store"

  private val clientAuthToken = configuration.get[String]("internal-auth.token")

  def getSubmission(userId: String, submissionId: String)(implicit hc: HeaderCarrier): Future[Option[Submission]] = {
    httpClient
      .get(url"$baseUrl/submission/user/$userId/id/$submissionId")
      .setHeader(AUTHORIZATION -> clientAuthToken)
      .execute
      .flatMap { response =>
        if (response.status == OK) {
            response.json.validate[Submission] match {
              case JsSuccess(submission, _) => Future.successful(Some(submission))
              case JsError(_) => Future.failed(SubmissionStoreConnector.UnexpectedResponseException(response.status, response.body))
            }
        } else if (response.status == NOT_FOUND) {
          Future.successful(None)
        } else {
          handleError(SubmissionStoreConnector.UnexpectedResponseException(response.status, response.body))
        }
      }
  }

  def getAllSubmissions(userId: String)(implicit hc: HeaderCarrier): Future[Seq[Submission]] = {
    httpClient
      .get(url"$baseUrl/submission/user/$userId")
      .setHeader(AUTHORIZATION -> clientAuthToken)
      .execute
      .flatMap { response =>
        if (response.status == OK) {
            response.json.validate[Seq[Submission]] match {
              case JsSuccess(submissions, _) => Future.successful(submissions)
              case JsError(_) => Future.failed(SubmissionStoreConnector.UnexpectedResponseException(response.status, response.body))
            }
        } else if (response.status == NOT_FOUND) {
          Future.successful(Nil)
        } else {
          handleError(SubmissionStoreConnector.UnexpectedResponseException(response.status, response.body))
        }
      }
  }

  def setSubmission(submission: Submission)(implicit hc: HeaderCarrier): Future[Result] = {
    httpClient
      .put(url"$baseUrl/submission")
      .setHeader(AUTHORIZATION -> clientAuthToken)
      .withBody(Json.toJson(submission))
      .execute
      .flatMap { response =>
        if (response.status == NO_CONTENT) {
          Future.successful(NoContent)
        } else {
          handleError(SubmissionStoreConnector.UnexpectedResponseException(response.status, response.body))
        }
      }
  }

  def deleteSubmission(userId: String, submissionId: String)(implicit hc: HeaderCarrier): Future[Result] = {
    httpClient
      .delete(url"$baseUrl/submission/user/$userId/id/$submissionId")
      .setHeader(AUTHORIZATION -> clientAuthToken)
      .execute
      .flatMap { response =>
        if (response.status == NO_CONTENT) {
          Future.successful(NoContent)
        } else {
          handleError(SubmissionStoreConnector.UnexpectedResponseException(response.status, response.body))
        }
      }
  }
}

@ImplementedBy(classOf[SubmissionStoreConnectorImpl])
trait SubmissionStoreConnector {
  def getSubmission(userId: String, submissionId: String)(implicit hc: HeaderCarrier): Future[Option[Submission]]
  def getAllSubmissions(userId: String)(implicit hc: HeaderCarrier): Future[Seq[Submission]]
  def setSubmission(submission: Submission)(implicit hc: HeaderCarrier): Future[Result]
  def deleteSubmission(userId: String, submissionId: String)(implicit hc: HeaderCarrier): Future[Result]
}

object SubmissionStoreConnector {
  final case class UnexpectedResponseException(status: Int, body: String) extends Exception with NoStackTrace {
    override def getMessage: String = s"Unexpected response from DDS Store, status: $status, body: $body"
  }
}