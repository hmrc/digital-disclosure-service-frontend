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

import cats.data.EitherT
import cats.implicits.catsSyntaxOptionId
import org.scalamock.handlers.CallHandler2
import org.scalamock.scalatest.MockFactory
import org.scalatest.EitherValues
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.http.Status.ACCEPTED
import play.api.libs.json.JsError
import play.api.libs.json.JsPath
import play.api.libs.json.Json
import play.api.libs.json.JsonValidationError
import play.api.mvc.Call
import play.api.test.Helpers.LOCATION
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpResponse
import models.address._
import models._
import connectors.AddressLookupConnector
import generators.ModelGenerators
import com.typesafe.config.ConfigFactory
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import config.{FrontendAppConfig, AddressLookupConfig}
import play.api.test.{FakeRequest, Injecting}
import play.api.i18n.{Messages, MessagesApi}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.play.PlaySpec

import java.net.URL
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AddressLookupServiceSpec
    extends PlaySpec
    with GuiceOneAppPerSuite
    with ScalaCheckPropertyChecks
    with EitherValues
    with MockFactory
    with ModelGenerators 
    with Injecting {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  val addressUpdateCall: Call = Call("", "/redirect")

  val config: Configuration = Configuration(
    ConfigFactory.parseString(
      """
        |host = host1.com,
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

  protected val realMessagesApi: MessagesApi = inject[MessagesApi]
  implicit def messages: Messages = realMessagesApi.preferred(FakeRequest())

  val frontendAppConfig = new FrontendAppConfig(config)
  val lookupConfig = new AddressLookupConfig(new ServicesConfig(config))

  private val addressLookupConnector = mock[AddressLookupConnector]
  private val addressLookupService   = new AddressLookupServiceImpl(addressLookupConnector, lookupConfig, frontendAppConfig)

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

  "The address lookup service" when {

    "triggering a lookup for an individual" must {

      val selectPageConfig = SelectPageConfig(proposalListLimit = 15)
      val addressLookupOptions = AddressLookupOptions(
        continueUrl = s"host1.com/redirect",
        showPhaseBanner = Some(true),
        alphaPhase = true,
        selectPageConfig = Some(selectPageConfig),
        includeHMRCBranding = Some(false)
      )

      val appLevelLabels = AppLevelLabels(messages("service.name"))
      val countryPickerLabels = CountryPickerLabels(
        messages("yourCountryLookup.title"), 
        messages("yourCountryLookup.heading"),
        messages("yourCountryLookup.hint"),
        messages("site.continue")
      )
      val lookupPageLabels = LookupPageLabels(
        messages("yourAddressLookup.title"), 
        messages("yourAddressLookup.heading"),
        messages("site.continue")
      )
      val selectPageLabels = SelectPageLabels(
        messages("selectAddress.title"), 
        messages("selectAddress.heading")
      )
      val englishLabels = LabelsByLanguage(appLevelLabels, countryPickerLabels, lookupPageLabels, selectPageLabels)
      val labels = AddressLookupLabels(englishLabels)
      val addressLookupRequest = AddressLookupRequest(2, addressLookupOptions, labels)

      "succeed receiving user redirect URL" in {
        val locationUrl = new URL("http://someUrl:1234/redirect")

        mockInitiateAddressLookupResponse(addressLookupRequest)(
          Right(HttpResponse(ACCEPTED, Json.obj(), headers = Map(LOCATION -> Seq(locationUrl.toString))))
        )

        val response = await(addressLookupService.getIndividualAddressLookupRedirect(addressUpdateCall).value)
        response.isLeft must be(false)
      }

      "fail having no request accepted" in {
        mockInitiateAddressLookupResponse(addressLookupRequest)(
          Right(HttpResponse(INTERNAL_SERVER_ERROR, Json.obj().toString()))
        )

        await(addressLookupService.getIndividualAddressLookupRedirect(addressUpdateCall).value).left.value must be(
          Error("The request was refused by the Address Lookup Service")
        )
      }

      "fail having no location header provided" in {
        mockInitiateAddressLookupResponse(addressLookupRequest)(
          Right(HttpResponse(ACCEPTED, Json.obj().toString()))
        )

        await(addressLookupService.getIndividualAddressLookupRedirect(addressUpdateCall).value).left.value must be(
          Error("The Address Lookup Service user redirect URL is missing in the header")
        )
      }
    }

    "retrieving address" must {

      "succeed having valid address ID" in forAll { (id: UUID, address: Address) =>
        val json = Json.obj(
          "id"      -> id,
          "address" -> Json.obj(
            "lines"    -> Seq(
              address.line1.some.toList,
              address.line2.toList,
              address.line3.toList,
              address.line4.some.toList
            ).flatten.seq,
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

    "an address with only one address line" must {
      import AddressLookupServiceImpl.addressLookupResponseReads

      "fail to deserialise" in {
        val addressJson = Json.parse("""{
                                       |    "auditRef": "101ca9ed-8dab-4868-80e3-024642e33df7",
                                       |    "address":
                                       |    {
                                       |        "lines":
                                       |        [
                                       |            "Buckingham Palace"
                                       |        ],
                                       |        "country":
                                       |        {
                                       |            "code": "GB",
                                       |            "name": "United Kingdom"
                                       |        },
                                       |        "postcode": "SW1A 1AA"
                                       |    }
                                       |}""".stripMargin)

        val path = JsPath \ "address" \ "lines"
        val err  = JsonValidationError("error.minLength", 2)
        addressJson.validate[Address] mustBe JsError(List((path, List(err))))
      }
    }
  }
}