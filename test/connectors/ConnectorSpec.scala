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

import cats.data.EitherT
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.JsString
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpResponse
import models.Error

import scala.concurrent.Future

trait ConnectorSpec { this: Matchers with AnyWordSpec =>

  def connectorBehaviour(
    mockHttpCall: Option[HttpResponse] => Unit,
    performCall: () => EitherT[Future, Error, HttpResponse]
  ): Unit = {
    "do a http call and return the result" in {
      List(
        HttpResponse(200, "{}"),
        HttpResponse(200, JsString("response"), Map.empty[String, Seq[String]]),
        HttpResponse(400, "{}"),
        HttpResponse(500, "{}")
      ).foreach { httpResponse =>
        withClue(s"For http response [${httpResponse.toString}]") {
          mockHttpCall(Some(httpResponse))
          await(performCall().value) shouldBe Right(httpResponse)
        }
      }
    }

    "return an error" when {
      "the future fails" in {
        mockHttpCall(None)
        await(performCall().value).isLeft shouldBe true
      }
    }
  }

}
