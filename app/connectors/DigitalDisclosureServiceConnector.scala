/*
 * Copyright 2022 HM Revenue & Customs
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

import akka.actor.ActorSystem
import config.{FrontendAppConfig, Service}
import play.api.Configuration
import play.api.http.Status._
import play.api.libs.json.{JsError, JsSuccess, Json, Reads}
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import com.google.inject.{ImplementedBy, Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NoStackTrace
import models.store.notification._

import java.time.Clock
import models.submission.SubmissionResponse
import uk.gov.hmrc.http.HttpResponse
import akka.util.ByteString
import play.api.libs.ws.WSClient

@Singleton
class DigitalDisclosureServiceConnectorImpl @Inject() (
                                val actorSystem: ActorSystem,
                                httpClient: HttpClientV2,
                                ws: WSClient,
                                configuration: Configuration,
                                clock: Clock
                              )(implicit val ec: ExecutionContext, frontendAppConfig:FrontendAppConfig) extends DigitalDisclosureServiceConnector with ConnectorErrorHandler with Retries {

  private val service: Service = configuration.get[Service]("microservice.services.digital-disclosure-service")
  private val baseUrl = s"${service.baseUrl}/digital-disclosure-service"

  def submitNotification(notification: Notification)(implicit hc: HeaderCarrier): Future[String] =
    retry {
      httpClient
        .post(url"$baseUrl/notification/submit")
        .withBody(Json.toJson(notification))
        .execute
        .flatMap { response =>
          response.status match {
            case ACCEPTED => handleResponse[SubmissionResponse.Success](response).map(_.id)
            case _ => handleError(DigitalDisclosureServiceConnector.UnexpectedResponseException(response.status, response.body))
          }
        }
    }

  def generateNotificationPDF(notification: Notification)(implicit hc: HeaderCarrier): Future[ByteString] = {
    ws
      .url(s"$baseUrl/notification/pdf")
      .post(Json.toJson(notification)) 
      .flatMap { response =>
        response.status match {
          case OK => Future.successful(response.bodyAsBytes)
          case _ => Future.failed(DigitalDisclosureServiceConnector.UnexpectedResponseException(response.status, response.body))
        }
      }
  }

  def handleResponse[A](response: HttpResponse)(implicit reads: Reads[A]): Future[A] = {
    response.json.validate[A] match {
      case JsSuccess(a, _) => Future.successful(a)
      case JsError(_) => handleError(NotificationStoreConnector.UnexpectedResponseException(response.status, response.body))
    }
  }

}

@ImplementedBy(classOf[DigitalDisclosureServiceConnectorImpl])
trait DigitalDisclosureServiceConnector {
  def submitNotification(notification: Notification)(implicit hc: HeaderCarrier): Future[String]
  def generateNotificationPDF(notification: Notification)(implicit hc: HeaderCarrier): Future[ByteString]
}

object DigitalDisclosureServiceConnector {
  final case class UnexpectedResponseException(status: Int, body: String) extends Exception with NoStackTrace {
    override def getMessage: String = s"Unexpected response from DDS, status: $status, body: $body"
  }
}