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

import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import play.api.libs.ws.BodyWritable
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.http.client.{HttpClientV2, RequestBuilder}

import java.net.URL
import scala.concurrent.{ExecutionContext, Future}


trait HttpSupport { this: MockFactory with Matchers =>

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  val mockHttp: HttpClientV2 = mock[HttpClientV2]

  val mockRequestBuilder: RequestBuilder = mock[RequestBuilder]

  def mockGet(url: URL)(httpResponse: Option[HttpResponse]) = {
    (mockHttp.get(_: URL)(_: HeaderCarrier))
      .expects(url, *)
      .returning(mockRequestBuilder)
      .once()

    mockExecute(None, httpResponse)
  }

  def mockPost[B](url: URL, requestBody: B)(httpResponse: Option[HttpResponse]) = {

    (mockHttp.post(_: URL)(_: HeaderCarrier))
      .expects(url, *)
      .returning(mockRequestBuilder)
      .once()

    mockExecute(Some(requestBody), httpResponse)
  }

  private def mockExecute[B](requestBody: Option[B], httpResponse: Option[HttpResponse])  = {
    requestBody.foreach { body =>
      (mockRequestBuilder.withBody(_: B)(_: BodyWritable[B], _: izumi.reflect.Tag[B], _: ExecutionContext))
        .expects(body, *, *, *)
        .returning(mockRequestBuilder).once()
    }

    (mockRequestBuilder.execute[HttpResponse](_: HttpReads[HttpResponse], _: ExecutionContext))
      .expects(*, *)
      .returning(
        httpResponse.fold[Future[HttpResponse]](
          Future.failed(new Exception("Test exception message"))
        )(Future.successful)
      ).once()
  }

}