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

package services

import base.SpecBase
import cats.data.EitherT
import cats.implicits.catsSyntaxOptionId
import com.typesafe.config.ConfigFactory
import config.{AddressLookupConfig, FrontendAppConfig}
import connectors.AddressLookupConnector
import generators.ModelGenerators
import models._
import models.address._
import org.scalamock.handlers.CallHandler2
import org.scalamock.scalatest.MockFactory
import org.scalatest.{EitherValues, TryValues}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import play.api.Configuration
import play.api.http.Status.ACCEPTED
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsError, JsPath, Json, JsonValidationError}
import play.api.mvc.Call
import play.api.test.Helpers.{LOCATION, _}
import play.api.test.Injecting
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.net.URL
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AddressLookupServiceSpec
    extends SpecBase
    with ScalaCheckPropertyChecks
    with EitherValues
    with MockFactory
    with ModelGenerators
    with Injecting
    with TryValues
    with AddressLookupHelper {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  val app = application

  val addressUpdateCall: Call = Call("", "/redirect")

  val config: Configuration = Configuration(
    ConfigFactory.parseString(
      """
        |timeout-dialog.timeout = 900,
        |self.url = host1.com,
        |features.welsh-translation = false
        |microservice {
        |  services {
        |    address-lookup-frontend {
        |      max-addresses-to-show = 15
        |    }
        |  }
        |}
        |""".stripMargin
    )
  )

  implicit def messagesApi: MessagesApi = inject[MessagesApi]

  val frontendAppConfig = new FrontendAppConfig(config)
  val lookupConfig      = new AddressLookupConfig(new ServicesConfig(config))

  private val addressLookupConnector = mock[AddressLookupConnector]
  private val addressLookupService   =
    new AddressLookupServiceImpl(addressLookupConnector, lookupConfig, frontendAppConfig)

  def mockInitiateAddressLookupResponse(request: AddressLookupRequest)(
    response: Either[Error, HttpResponse]
  ): CallHandler2[AddressLookupRequest, HeaderCarrier, EitherT[Future, Error, HttpResponse]] =
    (addressLookupConnector
      .initialise(_: AddressLookupRequest)(_: HeaderCarrier))
      .expects(request, *)
      .returning(EitherT.fromEither[Future](response))

  def mockGetAddress(id: UUID)(
    response: Either[Error, HttpResponse]
  ): CallHandler2[UUID, HeaderCarrier, EitherT[Future, Error, HttpResponse]] =
    (addressLookupConnector
      .retrieveAddress(_: UUID)(_: HeaderCarrier))
      .expects(id, *)
      .returning(EitherT.fromEither[Future](response))

  "The address lookup service" - {

    def testAddressLookup(userAnswers: UserAnswers, addressLookupRequest: AddressLookupRequest) = {
      "succeed receiving user redirect URL" in {
        val locationUrl = new URL("http://someUrl:1234/redirect")

        mockInitiateAddressLookupResponse(addressLookupRequest)(
          Right(HttpResponse(ACCEPTED, Json.obj(), headers = Map(LOCATION -> Seq(locationUrl.toString))))
        )

        val response = await(addressLookupService.getYourAddressLookupRedirect(addressUpdateCall, userAnswers).value)
        response.isLeft must be(false)
      }

      "fail having no request accepted" in {
        mockInitiateAddressLookupResponse(addressLookupRequest)(
          Right(HttpResponse(INTERNAL_SERVER_ERROR, Json.obj().toString()))
        )

        await(
          addressLookupService.getYourAddressLookupRedirect(addressUpdateCall, userAnswers).value
        ).left.value must be(
          Error("The request was refused by the Address Lookup Service")
        )
      }

      "fail having no location header provided" in {
        mockInitiateAddressLookupResponse(addressLookupRequest)(
          Right(HttpResponse(ACCEPTED, Json.obj().toString()))
        )

        await(
          addressLookupService.getYourAddressLookupRedirect(addressUpdateCall, userAnswers).value
        ).left.value must be(
          Error("The Address Lookup Service user redirect URL is missing in the header")
        )
      }
    }

    "triggering a lookup for your address where they are not the individual" - {

      val userAnswers = (for {
        uaWithRelatesToPage <- UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.AnIndividual)
        ua                  <- uaWithRelatesToPage.set(AreYouTheEntityPage, AreYouTheEntity.IAmAnAccountantOrTaxAgent)
      } yield ua).success.value

      testAddressLookup(userAnswers, yourAddressIndividualBodyRequest)
    }

    "triggering a lookup for your address when no body text" - {

      val userAnswers = (for {
        uaWithRelatesToPage <- UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.AnIndividual)
        ua                  <- uaWithRelatesToPage.set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
      } yield ua).success.value

      testAddressLookup(userAnswers, yourAddressNoBodyRequest)
    }

    "triggering a lookup for your address when they are not an officer of the company" - {

      val userAnswers = (for {
        uaWithRelatesToPage <- UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.ACompany)
        ua                  <- uaWithRelatesToPage.set(AreYouTheEntityPage, AreYouTheEntity.IAmAnAccountantOrTaxAgent)
      } yield ua).success.value

      testAddressLookup(userAnswers, yourAddressCompanyBodyRequest)
    }

    "triggering a lookup for your address when they are an officer of the company" - {

      val userAnswers = (for {
        uaWithRelatesToPage <- UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.ACompany)
        ua                  <- uaWithRelatesToPage.set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
      } yield ua).success.value

      testAddressLookup(userAnswers, yourAddressNoBodyRequest)
    }

    "triggering a lookup for your address when they are not a limited liability partnership" - {

      val userAnswers = (for {
        uaWithRelatesToPage <-
          UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.ALimitedLiabilityPartnership)
        ua                  <- uaWithRelatesToPage.set(AreYouTheEntityPage, AreYouTheEntity.IAmAnAccountantOrTaxAgent)
      } yield ua).success.value

      testAddressLookup(userAnswers, yourAddressLLPBodyRequest)
    }

    "triggering a lookup for your address when they are a limited liability partnership" - {

      val userAnswers = (for {
        uaWithRelatesToPage <-
          UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.ALimitedLiabilityPartnership)
        ua                  <- uaWithRelatesToPage.set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
      } yield ua).success.value

      testAddressLookup(userAnswers, yourAddressNoBodyRequest)
    }

    "triggering a lookup for your address when they are not a trust" - {

      val userAnswers = (for {
        uaWithRelatesToPage <- UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.ATrust)
        ua                  <- uaWithRelatesToPage.set(AreYouTheEntityPage, AreYouTheEntity.IAmAnAccountantOrTaxAgent)
      } yield ua).success.value

      testAddressLookup(userAnswers, yourAddressTrustBodyRequest)
    }

    "triggering a lookup for your address when they are a trust" - {

      val userAnswers = (for {
        uaWithRelatesToPage <- UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.ATrust)
        ua                  <- uaWithRelatesToPage.set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
      } yield ua).success.value

      testAddressLookup(userAnswers, yourAddressNoBodyRequest)
    }

    "triggering a lookup for your address when they are not an estate" - {

      val userAnswers = (for {
        uaWithRelatesToPage <- UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.AnEstate)
        ua                  <- uaWithRelatesToPage.set(AreYouTheEntityPage, AreYouTheEntity.IAmAnAccountantOrTaxAgent)
      } yield ua).success.value

      testAddressLookup(userAnswers, yourAddressEstateBodyRequest)
    }

    "triggering a lookup for your address when they are an estate" - {

      val userAnswers = (for {
        uaWithRelatesToPage <- UserAnswers("id", "session-123").set(RelatesToPage, RelatesTo.AnEstate)
        ua                  <- uaWithRelatesToPage.set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
      } yield ua).success.value

      testAddressLookup(userAnswers, yourAddressNoBodyRequest)
    }

    "triggering a lookup for an individual address" - {

      "succeed receiving user redirect URL" in {
        val locationUrl = new URL("http://someUrl:1234/redirect")

        mockInitiateAddressLookupResponse(individualLookupRequest)(
          Right(HttpResponse(ACCEPTED, Json.obj(), headers = Map(LOCATION -> Seq(locationUrl.toString))))
        )

        val response = await(addressLookupService.getIndividualAddressLookupRedirect(addressUpdateCall).value)
        response.isLeft must be(false)
      }

      "fail having no request accepted" in {
        mockInitiateAddressLookupResponse(individualLookupRequest)(
          Right(HttpResponse(INTERNAL_SERVER_ERROR, Json.obj().toString()))
        )

        await(addressLookupService.getIndividualAddressLookupRedirect(addressUpdateCall).value).left.value must be(
          Error("The request was refused by the Address Lookup Service")
        )
      }

      "fail having no location header provided" in {
        mockInitiateAddressLookupResponse(individualLookupRequest)(
          Right(HttpResponse(ACCEPTED, Json.obj().toString()))
        )

        await(addressLookupService.getIndividualAddressLookupRedirect(addressUpdateCall).value).left.value must be(
          Error("The Address Lookup Service user redirect URL is missing in the header")
        )
      }
    }

    "triggering a lookup for an company address" - {

      "succeed receiving user redirect URL" in {
        val locationUrl = new URL("http://someUrl:1234/redirect")

        mockInitiateAddressLookupResponse(companyLookupRequest)(
          Right(HttpResponse(ACCEPTED, Json.obj(), headers = Map(LOCATION -> Seq(locationUrl.toString))))
        )

        val response = await(addressLookupService.getCompanyAddressLookupRedirect(addressUpdateCall).value)
        response.isLeft must be(false)
      }

      "fail having no request accepted" in {
        mockInitiateAddressLookupResponse(companyLookupRequest)(
          Right(HttpResponse(INTERNAL_SERVER_ERROR, Json.obj().toString()))
        )

        await(addressLookupService.getCompanyAddressLookupRedirect(addressUpdateCall).value).left.value must be(
          Error("The request was refused by the Address Lookup Service")
        )
      }

      "fail having no location header provided" in {
        mockInitiateAddressLookupResponse(companyLookupRequest)(
          Right(HttpResponse(ACCEPTED, Json.obj().toString()))
        )

        await(addressLookupService.getCompanyAddressLookupRedirect(addressUpdateCall).value).left.value must be(
          Error("The Address Lookup Service user redirect URL is missing in the header")
        )
      }
    }

    "triggering a lookup for an llp address" - {

      "succeed receiving user redirect URL" in {
        val locationUrl = new URL("http://someUrl:1234/redirect")

        mockInitiateAddressLookupResponse(llpLookupRequest)(
          Right(HttpResponse(ACCEPTED, Json.obj(), headers = Map(LOCATION -> Seq(locationUrl.toString))))
        )

        val response = await(addressLookupService.getLLPAddressLookupRedirect(addressUpdateCall).value)
        response.isLeft must be(false)
      }

      "fail having no request accepted" in {
        mockInitiateAddressLookupResponse(llpLookupRequest)(
          Right(HttpResponse(INTERNAL_SERVER_ERROR, Json.obj().toString()))
        )

        await(addressLookupService.getLLPAddressLookupRedirect(addressUpdateCall).value).left.value must be(
          Error("The request was refused by the Address Lookup Service")
        )
      }

      "fail having no location header provided" in {
        mockInitiateAddressLookupResponse(llpLookupRequest)(
          Right(HttpResponse(ACCEPTED, Json.obj().toString()))
        )

        await(addressLookupService.getLLPAddressLookupRedirect(addressUpdateCall).value).left.value must be(
          Error("The Address Lookup Service user redirect URL is missing in the header")
        )
      }
    }

    "triggering a lookup for a trust address" - {

      "succeed receiving user redirect URL" in {
        val locationUrl = new URL("http://someUrl:1234/redirect")

        mockInitiateAddressLookupResponse(trustLookupRequest)(
          Right(HttpResponse(ACCEPTED, Json.obj(), headers = Map(LOCATION -> Seq(locationUrl.toString))))
        )

        val response = await(addressLookupService.getTrustAddressLookupRedirect(addressUpdateCall).value)
        response.isLeft must be(false)
      }

      "fail having no request accepted" in {
        mockInitiateAddressLookupResponse(trustLookupRequest)(
          Right(HttpResponse(INTERNAL_SERVER_ERROR, Json.obj().toString()))
        )

        await(addressLookupService.getTrustAddressLookupRedirect(addressUpdateCall).value).left.value must be(
          Error("The request was refused by the Address Lookup Service")
        )
      }

      "fail having no location header provided" in {
        mockInitiateAddressLookupResponse(trustLookupRequest)(
          Right(HttpResponse(ACCEPTED, Json.obj().toString()))
        )

        await(addressLookupService.getTrustAddressLookupRedirect(addressUpdateCall).value).left.value must be(
          Error("The Address Lookup Service user redirect URL is missing in the header")
        )
      }
    }

    "triggering a lookup for an estate address" - {

      "succeed receiving user redirect URL" in {
        val locationUrl = new URL("http://someUrl:1234/redirect")

        mockInitiateAddressLookupResponse(estateLookupRequest)(
          Right(HttpResponse(ACCEPTED, Json.obj(), headers = Map(LOCATION -> Seq(locationUrl.toString))))
        )

        val response = await(addressLookupService.getEstateAddressLookupRedirect(addressUpdateCall).value)
        response.isLeft must be(false)
      }

      "fail having no request accepted" in {
        mockInitiateAddressLookupResponse(estateLookupRequest)(
          Right(HttpResponse(INTERNAL_SERVER_ERROR, Json.obj().toString()))
        )

        await(addressLookupService.getEstateAddressLookupRedirect(addressUpdateCall).value).left.value must be(
          Error("The request was refused by the Address Lookup Service")
        )
      }

      "fail having no location header provided" in {
        mockInitiateAddressLookupResponse(estateLookupRequest)(
          Right(HttpResponse(ACCEPTED, Json.obj().toString()))
        )

        await(addressLookupService.getEstateAddressLookupRedirect(addressUpdateCall).value).left.value must be(
          Error("The Address Lookup Service user redirect URL is missing in the header")
        )
      }
    }

    "triggering a lookup for a rental address" - {

      "succeed receiving user redirect URL" in {
        val locationUrl = new URL("http://someUrl:1234/redirect")

        mockInitiateAddressLookupResponse(rentalLookupRequest)(
          Right(HttpResponse(ACCEPTED, Json.obj(), headers = Map(LOCATION -> Seq(locationUrl.toString))))
        )

        val response = await(addressLookupService.getRentalAddressLookupRedirect(addressUpdateCall, 0).value)
        response.isLeft must be(false)
      }

      "fail having no request accepted" in {
        mockInitiateAddressLookupResponse(rentalLookupRequest)(
          Right(HttpResponse(INTERNAL_SERVER_ERROR, Json.obj().toString()))
        )

        await(addressLookupService.getRentalAddressLookupRedirect(addressUpdateCall, 0).value).left.value must be(
          Error("The request was refused by the Address Lookup Service")
        )
      }

      "fail having no location header provided" in {
        mockInitiateAddressLookupResponse(rentalLookupRequest)(
          Right(HttpResponse(ACCEPTED, Json.obj().toString()))
        )

        await(addressLookupService.getRentalAddressLookupRedirect(addressUpdateCall, 0).value).left.value must be(
          Error("The Address Lookup Service user redirect URL is missing in the header")
        )
      }
    }

    "retrieving address" - {

      "succeed having valid address ID" in forAll { (id: UUID, address: Address) =>
        val json = Json.obj(
          "id"      -> id,
          "address" -> Json.obj(
            "lines"    -> Set(
              address.line1.some.toList,
              address.line2.toList,
              address.line3.toList,
              address.line4.toList
            ).flatten.toSeq,
            "postcode" -> address.postcode,
            "country"  -> Json.obj(
              "code" -> address.country.code
            )
          )
        )

        mockGetAddress(id)(Right(HttpResponse(OK, json.toString())))

        await(addressLookupService.retrieveUserAddress(id).value).value must be(address)
      }

      "fail having invalid address ID" in forAll { id: UUID =>
        mockGetAddress(id)(Right(HttpResponse(NOT_FOUND, Json.obj().toString())))

        await(addressLookupService.retrieveUserAddress(id).value).isLeft must be(true)
      }
    }

    "an address with only one address line" - {
      import AddressLookupServiceImpl.addressLookupResponseReads

      "fail to deserialise" in {
        val addressJson = Json.parse("""{
                                       |    "auditRef": "101ca9ed-8dab-4868-80e3-024642e33df7",
                                       |    "address":
                                       |    {
                                       |        "lines":
                                       |        [],
                                       |        "country":
                                       |        {
                                       |            "code": "GB",
                                       |            "name": "United Kingdom"
                                       |        }
                                       |    }
                                       |}""".stripMargin)

        val path = JsPath \ "address" \ "lines"
        val err  = JsonValidationError("error.minLength", 1)
        addressJson.validate[Address] mustBe JsError(List((path, List(err))))
      }
    }
  }
}
