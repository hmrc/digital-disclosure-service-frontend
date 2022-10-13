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

package services

import java.util.UUID
import cats.data.EitherT
import cats.implicits.toBifunctorOps
import play.api.libs.functional.syntax._
import uk.gov.hmrc.http.HeaderCarrier
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import com.google.inject.{Inject, ImplementedBy}
import play.api.http.HeaderNames.LOCATION
import play.api.http.Status.ACCEPTED
import play.api.http.Status.OK
import java.net.URL
import play.api.mvc.Call
import models.Error
import models.address._
import connectors.AddressLookupConnector
import config.{FrontendAppConfig, AddressLookupConfig}
import play.api.libs.json.{JsPath, JsonValidationError, Reads}
import play.api.libs.json.Reads.minLength
import play.api.i18n.Messages

class AddressLookupServiceImpl @Inject()(connector: AddressLookupConnector, addressConfig: AddressLookupConfig, config: FrontendAppConfig)(implicit ec: ExecutionContext) extends AddressLookupService with AddressLookupRequestHelper {

  def getYourAddressLookupRedirect(redirectUrl: Call)(implicit hc: HeaderCarrier, messages: Messages): EitherT[Future, Error, URL] =  {
    val request = lookupRequestForYourAddress(config.host, redirectUrl.url, addressConfig.addressesShowLimit)
    initialiseAddressLookup(request)
  }

  def getIndividualAddressLookupRedirect(redirectUrl: Call)(implicit hc: HeaderCarrier, messages: Messages): EitherT[Future, Error, URL] =  {
    val request = lookupRequestForIndividualAddress(config.host, redirectUrl.url, addressConfig.addressesShowLimit)
    initialiseAddressLookup(request)
  }

  private[services] def initialiseAddressLookup(request: AddressLookupRequest)(implicit hc: HeaderCarrier): EitherT[Future, Error, URL] = {
    connector
      .initialise(request)
      .subflatMap { response =>
        if (response.status == ACCEPTED)
          response
            .header(LOCATION)
            .map(new URL(_))
            .toRight(Error("The Address Lookup Service user redirect URL is missing in the header"))
        else Left(Error("The request was refused by the Address Lookup Service"))
      }
  }

  def retrieveUserAddress(addressId: UUID)(implicit hc: HeaderCarrier): EitherT[Future, Error, Address] = {
    def formatErrors(errors: Seq[(JsPath, Seq[JsonValidationError])]): Error =
      Error(
        errors
          .map(_.bimap(_.toString(), _.flatMap(_.messages).mkString(", ")))
          .map(pathAndErrors => s"${pathAndErrors._1}: ${pathAndErrors._2}")
          .mkString("Error parsing address lookup response:", "; ", "")
      )


    connector
      .retrieveAddress(addressId)
      .ensure(Error(s"Cannot retrieve an address by ID $addressId"))(_.status == OK)
      .subflatMap { response =>
        if (response.status == OK)
          response.json
            .validate[Address](AddressLookupServiceImpl.addressLookupResponseReads)
            .asEither
            .leftMap(formatErrors)
        else Left(Error("Failed to retrieve address"))
      }
  }

}

object AddressLookupServiceImpl {

  implicit val addressLookupResponseReads: Reads[Address] = (
    (JsPath \ "address" \ "lines").read[Array[String]](minLength[Array[String]](2)) and
      (JsPath \ "address" \ "postcode").read[String] and
      (JsPath \ "address" \ "country" \ "code").read[String].map(Country(_))
  ).apply((lines, postcode, country) =>
    lines match {
      case Array(line1, line2, line3, town) =>
        Address(line1, Some(line2), Some(line3), town, postcode, country)
      case Array(line1, line2, town)        =>
        Address(line1, Some(line2), None, town, postcode, country)
      case Array(line1, town)               =>
        Address(line1, None, None, town, postcode, country)
    }
  )
}

@ImplementedBy(classOf[AddressLookupServiceImpl])
trait AddressLookupService {
  def getYourAddressLookupRedirect(redirectUrl: Call)(implicit hc: HeaderCarrier, messages: Messages): EitherT[Future, Error, URL]
  def getIndividualAddressLookupRedirect(redirectUrl: Call)(implicit hc: HeaderCarrier, messages: Messages): EitherT[Future, Error, URL]
  def retrieveUserAddress(addressId: UUID)(implicit hc: HeaderCarrier): EitherT[Future, Error, Address]
}