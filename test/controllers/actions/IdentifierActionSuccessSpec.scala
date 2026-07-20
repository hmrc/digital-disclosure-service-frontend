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

import base.SpecBase
import config.FrontendAppConfig
import models.{ARN, CAUTR, NINO, SAUTR}
import models.requests.IdentifierRequest
import play.api.mvc.{BodyParsers, Result, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class IdentifierActionSuccessSpec extends SpecBase {

  private val bodyParsers = application.injector.instanceOf[BodyParsers.Default]
  private val appConfig   = application.injector.instanceOf[FrontendAppConfig]

  private def enrolment(key: String, idName: String, idValue: String): Enrolment =
    Enrolment(key).withIdentifier(idName, idValue)

  private def action(connector: AuthConnector): AuthenticatedIdentifierAction =
    new AuthenticatedIdentifierAction(connector, appConfig, bodyParsers)

  "AuthenticatedIdentifierAction" - {

    "must allow an organisation user through and expose their internal id" in {
      val retrievals =
        new ~(new ~(Some("internal-id"), Some(AffinityGroup.Organisation)), Enrolments(Set.empty))

      val block: IdentifierRequest[?] => Future[Result] = req => Future.successful(Results.Ok(req.userId))
      val result                                        = action(new FakeAuthConnector(retrievals)).invokeBlock(FakeRequest(), block)

      status(result) mustBe OK
      contentAsString(result) mustBe "internal-id"
    }

    "must throw when no internal id is returned" in {
      val retrievals =
        new ~(new ~(Option.empty[String], Some(AffinityGroup.Organisation)), Enrolments(Set.empty))

      val block: IdentifierRequest[?] => Future[Result] = _ => Future.successful(Results.Ok)
      val thrown                                        = action(new FakeAuthConnector(retrievals)).invokeBlock(FakeRequest(), block).failed.futureValue
      thrown mustBe a[Exception]
    }
  }

  "getCustomerId" - {

    val underTest = action(new FakeAuthConnector(null))

    "must extract a NINO" in {
      val enrolments = Enrolments(Set(enrolment("HMRC-NI", "nino", "AA000000A")))
      underTest.getCustomerId(enrolments) mustBe Some(NINO("AA000000A"))
    }

    "must extract an SA UTR" in {
      val enrolments = Enrolments(Set(enrolment("IR-SA", "UTR", "1234567890")))
      underTest.getCustomerId(enrolments) mustBe Some(SAUTR("1234567890"))
    }

    "must extract a CT UTR" in {
      val enrolments = Enrolments(Set(enrolment("IR-CT", "UTR", "0987654321")))
      underTest.getCustomerId(enrolments) mustBe Some(CAUTR("0987654321"))
    }

    "must return None when there are no matching enrolments" in {
      underTest.getCustomerId(Enrolments(Set.empty)) mustBe None
    }
  }

  "getAgentCustomerId" - {

    val underTest = action(new FakeAuthConnector(null))

    "must extract an ARN" in {
      val enrolments = Enrolments(Set(enrolment("HMRC-AS-AGENT", "AgentReferenceNumber", "ARN123")))
      underTest.getAgentCustomerId(enrolments) mustBe Some(ARN("ARN123"))
    }

    "must return None when the agent enrolment is absent" in {
      underTest.getAgentCustomerId(Enrolments(Set.empty)) mustBe None
    }
  }
}

class FakeAuthConnector(retrievals: Any) extends AuthConnector {
  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[A] =
    Future.successful(retrievals.asInstanceOf[A])
}
