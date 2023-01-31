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
import models.store._
import play.api.mvc.Results.NoContent

class SubmissionStoreConnectorSpec extends AnyFreeSpec with Matchers with ScalaFutures with IntegrationPatience with WireMockHelper {

  private lazy val app: Application =
    GuiceApplicationBuilder()
      .configure(
        "microservice.services.digital-disclosure-service-store.port" -> server.port(),
        "microservice.services.digital-disclosure-service-store.host" -> "localhost",
        "microservice.services.digital-disclosure-service-store.protocol" -> "http",
        "create-internal-auth-token-on-start" -> "false"
      )
      .build()

  private lazy val connector = app.injector.instanceOf[SubmissionStoreConnector]

  "getSubmission" - {

    val hc = HeaderCarrier()
    val url = "/digital-disclosure-service-store/submission/user/123/id/456"

    val testSubmission: Submission = Notification("123", "456", Instant.now(), Metadata(), PersonalDetails(Background(), AboutYou()))

    "must return a successful future when the store responds with OK and a Submission object" in {

      server.stubFor(
        get(urlMatching(url))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.toJson(testSubmission).toString)
          )
      )

      connector.getSubmission("123", "456")(hc).futureValue mustEqual Some(testSubmission)
    }

    "must return a successful future when the store responds with NOT_FOUND" in {

      server.stubFor(
        get(urlMatching(url))
          .willReturn(aResponse().withStatus(NOT_FOUND))
      )

      connector.getSubmission("123", "456")(hc).futureValue mustEqual None
    }

    "must return a failed future when the store responds with OK but a different object" in {

      server.stubFor(
        get(urlMatching(url))
          .willReturn(aResponse().withBody("""{ "element" : "value" }""").withStatus(OK))
      )

      val exception = connector.getSubmission("123", "456")(hc).failed.futureValue
      exception mustEqual SubmissionStoreConnector.UnexpectedResponseException(200, """{ "element" : "value" }""")
    }

    "must return a failed future when the store responds with anything else" in {

      server.stubFor(
        get(urlMatching(url))
          .willReturn(aResponse().withBody("body").withStatus(INTERNAL_SERVER_ERROR))
      )

      val exception = connector.getSubmission("123", "456")(hc).failed.futureValue
      exception mustEqual SubmissionStoreConnector.UnexpectedResponseException(500, "body")
    }

    "must return a failed future when there is a connection error" in {

      server.stubFor(
        get(urlMatching(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getSubmission("123", "456")(hc).failed.futureValue
    }

  }

  "getAllSubmissions" - {

    val hc = HeaderCarrier()
    val url = "/digital-disclosure-service-store/submission/user/123"

    val testSubmission: Submission = Notification("123", "456", Instant.now(), Metadata(), PersonalDetails(Background(), AboutYou()))

    "must return a successful future when the store responds with OK and a Submission object" in {

      server.stubFor(
        get(urlMatching(url))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.toJson(Seq(testSubmission)).toString)
          )
      )

      connector.getAllSubmissions("123")(hc).futureValue mustEqual Seq(testSubmission)
    }

    "must return a successful future when the store responds with NOT_FOUND" in {

      server.stubFor(
        get(urlMatching(url))
          .willReturn(aResponse().withStatus(NOT_FOUND))
      )

      connector.getAllSubmissions("123")(hc).futureValue mustEqual Nil
    }

    "must return a failed future when the store responds with OK but a different object" in {

      server.stubFor(
        get(urlMatching(url))
          .willReturn(aResponse().withBody("""{ "element" : "value" }""").withStatus(OK))
      )

      val exception = connector.getAllSubmissions("123")(hc).failed.futureValue
      exception mustEqual SubmissionStoreConnector.UnexpectedResponseException(200, """{ "element" : "value" }""")
    }

    "must return a failed future when the store responds with anything else" in {

      server.stubFor(
        get(urlMatching(url))
          .willReturn(aResponse().withBody("body").withStatus(INTERNAL_SERVER_ERROR))
      )

      val exception = connector.getAllSubmissions("123")(hc).failed.futureValue
      exception mustEqual SubmissionStoreConnector.UnexpectedResponseException(500, "body")
    }

    "must return a failed future when there is a connection error" in {

      server.stubFor(
        get(urlMatching(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getAllSubmissions("123")(hc).failed.futureValue
    }

  }

  "setSubmission" - {

    val hc = HeaderCarrier()
    val url = "/digital-disclosure-service-store/submission"

    val testSubmission: Submission = Notification("123", "456", Instant.now(), Metadata(), PersonalDetails(Background(), AboutYou()))

    "must return a successful future when the store responds with NO_CONTENT" in {

      server.stubFor(
        put(urlMatching(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(testSubmission))))
          .willReturn(
            aResponse()
              .withStatus(NO_CONTENT)
          )
      )

      connector.setSubmission(testSubmission)(hc).futureValue mustEqual NoContent
    }


    "must return a failed future when the store responds with anything else" in {

      server.stubFor(
        put(urlMatching(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(testSubmission))))
          .willReturn(aResponse().withBody("body").withStatus(INTERNAL_SERVER_ERROR))
      )

      val exception = connector.setSubmission(testSubmission)(hc).failed.futureValue
      exception mustEqual SubmissionStoreConnector.UnexpectedResponseException(500, "body")
    }

    "must return a failed future when there is a connection error" in {

      server.stubFor(
        put(urlMatching(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(testSubmission))))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.setSubmission(testSubmission)(hc).failed.futureValue
    }

  }

  "deleteSubmission" - {

    val hc = HeaderCarrier()
    val url = "/digital-disclosure-service-store/submission/user/123/id/456"

    "must return a successful future when the store responds with NO_CONTENT" in {

      server.stubFor(
        delete(urlMatching(url))
          .willReturn(
            aResponse()
              .withStatus(NO_CONTENT)
          )
      )

      connector.deleteSubmission("123", "456")(hc).futureValue mustEqual NoContent
    }


    "must return a failed future when the store responds with anything else" in {

      server.stubFor(
        delete(urlMatching(url))
          .willReturn(aResponse().withBody("body").withStatus(INTERNAL_SERVER_ERROR))
      )

      val exception = connector.deleteSubmission("123", "456")(hc).failed.futureValue
      exception mustEqual SubmissionStoreConnector.UnexpectedResponseException(500, "body")
    }

    "must return a failed future when there is a connection error" in {

      server.stubFor(
        delete(urlMatching(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.deleteSubmission("123", "456")(hc).failed.futureValue
    }

  }
  
}