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

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.Fault
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier
import util.WireMockHelper
import java.time.Instant
import play.api.http.Status._
import models.store.notification._
import models.store.disclosure._
import models.submission.SubmissionResponse
import models.store.{Notification, FullDisclosure, Metadata}
import akka.util.ByteString

class DigitalDisclosureServiceConnectorSpec extends AnyFreeSpec with Matchers with ScalaFutures with IntegrationPatience with WireMockHelper {

  private lazy val app: Application =
    GuiceApplicationBuilder()
      .configure(
        "microservice.services.digital-disclosure-service.port" -> server.port(),
        "microservice.services.digital-disclosure-service.host" -> "localhost",
        "microservice.services.digital-disclosure-service.protocol" -> "http",
        "create-internal-auth-token-on-start" -> "false"
      )
      .build()

  private lazy val connector = app.injector.instanceOf[DigitalDisclosureServiceConnector]

  "submitNotification" - {

    val hc = HeaderCarrier()
    val url = "/digital-disclosure-service/notification/submit"

    val testNotification = Notification("123", "456", Instant.now(), Metadata(), PersonalDetails(Background(), AboutYou()))

    "must return an ID when the store responds with ACCEPTED" in {

      server.stubFor(
        post(urlMatching(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(testNotification))))
          .willReturn(
            aResponse()
              .withStatus(ACCEPTED)
              .withBody(Json.toJson(SubmissionResponse.Success("ID123")).toString)
          )
      )

      connector.submitNotification(testNotification)(hc).futureValue mustEqual "ID123"
    }

    "must return a failed future when the store responds with anything else" in {

      server.stubFor(
        post(urlMatching(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(testNotification))))
          .willReturn(aResponse().withBody("""{"body" : "body"}""").withStatus(INTERNAL_SERVER_ERROR))
      )

      val exception = connector.submitNotification(testNotification)(hc).failed.futureValue
      exception mustEqual DigitalDisclosureServiceConnector.UnexpectedResponseException(500, """{"body" : "body"}""")
    }

    "must return a failed future when there is a connection error" in {

      server.stubFor(
        post(urlMatching(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(testNotification))))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.submitNotification(testNotification)(hc).failed.futureValue
    }

  }

  "generateNotificationPDF" - {

    val hc = HeaderCarrier()
    val url = "/digital-disclosure-service/notification/pdf"

    val testNotification = Notification("123", "456", Instant.now(), Metadata(), PersonalDetails(Background(), AboutYou()))

    val testBodyString = "Some body"
    val testBody = ByteString("Some body".getBytes)

    "must return an ID when the store responds with ACCEPTED" in {

      server.stubFor(
        post(urlMatching(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(testNotification))))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(testBodyString))
      )

      connector.generateNotificationPDF(testNotification)(hc).futureValue mustEqual testBody
    }

    "must return a failed future when there is a connection error" in {

      server.stubFor(
        post(urlMatching(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(testNotification))))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.generateNotificationPDF(testNotification)(hc).failed.futureValue
    }

  }

  "submitDisclosure" - {

    val hc = HeaderCarrier()
    val url = "/digital-disclosure-service/disclosure/submit"

    val testDisclosure = FullDisclosure("123", "123", Instant.now(), Metadata(), CaseReference(), PersonalDetails(Background(), AboutYou()), None, OffshoreLiabilities(), OtherLiabilities(), ReasonForDisclosingNow())

    "must return an ID when the store responds with ACCEPTED" in {

      server.stubFor(
        post(urlMatching(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(testDisclosure))))
          .willReturn(
            aResponse()
              .withStatus(ACCEPTED)
              .withBody(Json.toJson(SubmissionResponse.Success("ID123")).toString)
          )
      )

      connector.submitDisclosure(testDisclosure)(hc).futureValue mustEqual "ID123"
    }

    "must return a failed future when the store responds with anything else" in {

      server.stubFor(
        post(urlMatching(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(testDisclosure))))
          .willReturn(aResponse().withBody("""{"body" : "body"}""").withStatus(INTERNAL_SERVER_ERROR))
      )

      val exception = connector.submitDisclosure(testDisclosure)(hc).failed.futureValue
      exception mustEqual DigitalDisclosureServiceConnector.UnexpectedResponseException(500, """{"body" : "body"}""")
    }

    "must return a failed future when there is a connection error" in {

      server.stubFor(
        post(urlMatching(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(testDisclosure))))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.submitDisclosure(testDisclosure)(hc).failed.futureValue
    }

  }

  "generateDisclosurePDF" - {

    val hc = HeaderCarrier()
    val url = "/digital-disclosure-service/disclosure/pdf"

    val testDisclosure = FullDisclosure("123", "123", Instant.now(), Metadata(), CaseReference(), PersonalDetails(Background(), AboutYou()), None, OffshoreLiabilities(), OtherLiabilities(), ReasonForDisclosingNow())

    val testBodyString = "Some body"
    val testBody = ByteString("Some body".getBytes)

    "must return an ID when the store responds with ACCEPTED" in {

      server.stubFor(
        post(urlMatching(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(testDisclosure))))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(testBodyString))
      )

      connector.generateDisclosurePDF(testDisclosure)(hc).futureValue mustEqual testBody
    }

  }
  
}