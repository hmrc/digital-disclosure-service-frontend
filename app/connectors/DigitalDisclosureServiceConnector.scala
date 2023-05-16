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

import akka.actor.ActorSystem
import config.{FrontendAppConfig, Service}
import play.api.{Configuration, Logging}
import play.api.http.Status._
import play.api.libs.json.{JsError, JsSuccess, Json, Reads}
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import com.google.inject.{ImplementedBy, Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NoStackTrace
import models.store.{FullDisclosure, Notification}

import java.time.Clock
import models.submission.SubmissionResponse
import uk.gov.hmrc.http.HttpResponse
import akka.util.ByteString
import play.api.i18n.MessagesApi
import play.api.libs.ws.WSClient
import play.api.mvc.{Cookie, Cookies}
import play.mvc.Http.HeaderNames.{ACCEPT_LANGUAGE, AUTHORIZATION}

@Singleton
class DigitalDisclosureServiceConnectorImpl @Inject() (
                                val actorSystem: ActorSystem,
                                httpClient: HttpClientV2,
                                ws: WSClient,
                                configuration: Configuration,
                                clock: Clock,
                                messagesApi:MessagesApi
                              )(implicit val ec: ExecutionContext, frontendAppConfig:FrontendAppConfig) extends DigitalDisclosureServiceConnector with ConnectorErrorHandler with Retries with Logging {

  private val service: Service = configuration.get[Service]("microservice.services.digital-disclosure-service")
  private val baseUrl = s"${service.baseUrl}/digital-disclosure-service"

  private val clientAuthToken = configuration.get[String]("internal-auth.token")

  def submitNotification(notification: Notification)(implicit hc: HeaderCarrier): Future[String] = 
    retry {
      httpClient
        .post(url"$baseUrl/notification/submit")
        .setHeader(AUTHORIZATION -> clientAuthToken,
          ACCEPT_LANGUAGE -> getLanguage)
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
      .withHttpHeaders(AUTHORIZATION -> clientAuthToken,
        ACCEPT_LANGUAGE -> getLanguage)
      .post(Json.toJson(notification))
      .flatMap { response =>
        response.status match {
          case OK => Future.successful(response.bodyAsBytes)
          case _ => Future.failed(DigitalDisclosureServiceConnector.UnexpectedResponseException(response.status, response.body))
        }
      }
  }

  def submitDisclosure(disclosure: FullDisclosure)(implicit hc: HeaderCarrier): Future[String] =
    retry {
      httpClient
        .post(url"$baseUrl/disclosure/submit")
        .setHeader(AUTHORIZATION -> clientAuthToken,
          ACCEPT_LANGUAGE -> getLanguage)
        .withBody(Json.toJson(disclosure))
        .execute
        .flatMap { response =>
          response.status match {
            case ACCEPTED => handleResponse[SubmissionResponse.Success](response).map(_.id)
            case _ => handleError(DigitalDisclosureServiceConnector.UnexpectedResponseException(response.status, response.body))
          }
        }
    }

  def generateDisclosurePDF(disclosure: FullDisclosure)(implicit hc: HeaderCarrier): Future[ByteString] = {
    ws
      .url(s"$baseUrl/disclosure/pdf")
      .withHttpHeaders(AUTHORIZATION -> clientAuthToken,
        ACCEPT_LANGUAGE -> getLanguage)
      .post(Json.toJson(disclosure))
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
      case JsError(_) => handleError(SubmissionStoreConnector.UnexpectedResponseException(response.status, response.body))
    }
  }

  private def getLanguage(implicit hc:HeaderCarrier): String = {
    val cookies = hc.otherHeaders.toMap.get("Cookie").map(cookieHeader => Cookies.fromCookieHeader(Some(cookieHeader))).getOrElse(Seq.empty[Cookie]).toSeq
    cookies.find(c => c.name == "PLAY_LANG") match {
      case Some(c) => c.value
      case _ => "en"
    }
  }

}

@ImplementedBy(classOf[DigitalDisclosureServiceConnectorImpl])
trait DigitalDisclosureServiceConnector {
  def submitNotification(notification: Notification)(implicit hc: HeaderCarrier): Future[String]
  def generateNotificationPDF(notification: Notification)(implicit hc: HeaderCarrier): Future[ByteString]
  def submitDisclosure(disclosure: FullDisclosure)(implicit hc: HeaderCarrier): Future[String]
  def generateDisclosurePDF(disclosure: FullDisclosure)(implicit hc: HeaderCarrier): Future[ByteString]
}

object DigitalDisclosureServiceConnector {
  final case class UnexpectedResponseException(status: Int, body: String) extends Exception with NoStackTrace {
    override def getMessage: String = s"Unexpected response from DDS, status: $status, body: $body"
  }
}