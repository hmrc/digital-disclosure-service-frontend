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

import java.util.UUID
import cats.data.EitherT
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.http.HeaderCarrier
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import com.google.inject.{Inject, ImplementedBy}
import scala.util.control.NonFatal
import models.Error
import models.address._
import config.AddressLookupConfig

class AddressLookupConnectorImpl @Inject()(httpClient: HttpClient, config: AddressLookupConfig)(implicit ec: ExecutionContext)
  extends AddressLookupConnector
    with ConnectorErrorHandler {

  def initialise(request: AddressLookupRequest)(implicit hc: HeaderCarrier): EitherT[Future, Error, HttpResponse] = EitherT {
    httpClient
      .POST[AddressLookupRequest, HttpResponse](config.startLookupUrl, request)
      .map(Right(_))
      .recover { case NonFatal(e) =>
        Left(handleError(Error(e)))
      }
  }

  def retrieveAddress(addressId: UUID)(implicit hc: HeaderCarrier): EitherT[Future, Error, HttpResponse] =
    EitherT {
      httpClient
        .GET[HttpResponse](s"${config.retrieveAddressUrl}?id=$addressId")
        .map(Right(_))
        .recover { case NonFatal(e) =>
          Left(handleError(Error(e)))
        }
    }

}

@ImplementedBy(classOf[AddressLookupConnectorImpl])
trait AddressLookupConnector {
  def initialise(request: AddressLookupRequest)(implicit hc: HeaderCarrier): EitherT[Future, Error, HttpResponse]
  def retrieveAddress(addressId: UUID)(implicit hc: HeaderCarrier): EitherT[Future, Error, HttpResponse]
}