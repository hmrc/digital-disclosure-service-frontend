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

package controllers.filters

import base.SpecBase
import akka.actor.ActorSystem
import akka.stream.Materializer
import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfterAll
import play.api.Configuration
import play.api.mvc.{Request, RequestHeader, Result, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.auth.core.authorise._
import uk.gov.hmrc.auth.core.retrieve._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import controllers.routes
import uk.gov.hmrc.http.HeaderCarrier

import org.scalamock.scalatest.MockFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class EmailAllowedListFilterSpec
    extends SpecBase with BeforeAndAfterAll with MockFactory {

  implicit val system            = ActorSystem()
  implicit val mat: Materializer = Materializer(system)

  val mockAuthConnector = mock[AuthConnector]

  def mockAuth[R](predicate: Predicate, retrieval: Retrieval[R])(
    result: Future[R]
  ): Unit =
    (mockAuthConnector
      .authorise(_: Predicate, _: Retrieval[R])(
        _: HeaderCarrier,
        _: ExecutionContext
      ))
      .expects(predicate, retrieval, *, *)
      .returning(result)

  def additionalConfig(isEnabled: Boolean): Configuration = Configuration(
    ConfigFactory.parseString(
      s"""
        | email-allow-list {
        | enabled = $isEnabled
        | list = ["user@test.com"]
        | }
        | """.stripMargin
    )
  )

  override def afterAll(): Unit = {
    super.afterAll()
    await(system.terminate())
  }

  val retrievals = Retrievals.email

  def mockAuthWithRetrievals(
    retrievedEmailAddress: Option[String]
  ) =
    mockAuth(EmptyPredicate, retrievals)(
      Future.successful(retrievedEmailAddress)
    )

  def emailAllowedListFilter(isEnabled: Boolean) =
    new EmailAllowedListFilter(mat, mockAuthConnector, additionalConfig(isEnabled))


  val requestHandler: RequestHeader => Future[Result] = _ => Future.successful(Results.Ok)

  "EmailAllowedListFilterSpec" - {

    "email allowed config is false, move to next page, irrespective of enrollment contains the email id or not" in {

      val request = FakeRequest()
      val result  = emailAllowedListFilter(false)(requestHandler)(request)
      status(result) mustBe 200
    }

    "email allowed config is true" - {

      "move to the next page" - {

        def test(request: Request[_], emailAddress: String, authExpected: Boolean) = {
          if (authExpected) mockAuthWithRetrievals(Some(emailAddress))

          val result = emailAllowedListFilter(true)(requestHandler)(request)
          status(result) mustBe 200
        }

        "user allowed email list contains the email in enrollment" in {
          test(FakeRequest(), "user@test.com", authExpected = true)
        }

        "user allowed email list contains the email with different case in enrollment" in {
          test(FakeRequest(), "UsEr@teSt.cOm", authExpected = true)
        }

        "the user is not on the allowed email list and" - {

          "is for the access denied page" in {
            test(
              FakeRequest(routes.UnauthorisedController.onPageLoad),
              "user1@test.com",
              authExpected = false
            )
          }

          "uri contains 'hmrc-frontend'" in {
            test(
              FakeRequest("GET", "http://host/hmrc-frontend/x"),
              "user1@test.com",
              authExpected = false
            )
          }

          "uri contains 'assets'" in {
            test(FakeRequest("GET", "http://host/assets/x"), "user1@test.com", authExpected = false)
          }

          "uri contains 'ping/ping'" in {
            test(FakeRequest("GET", "http://host/ping/ping"), "user1@test.com", authExpected = false)
          }

        }

      }

      "move to access denied" - {

        "user allowed email list doesn't contain the email in enrollment" in {

          val request = FakeRequest()
          mockAuthWithRetrievals(
            Some("user1@test.com")
          )
          val result  = emailAllowedListFilter(true)(requestHandler)(request)
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad.url)
        }

      }

    }

  }
}