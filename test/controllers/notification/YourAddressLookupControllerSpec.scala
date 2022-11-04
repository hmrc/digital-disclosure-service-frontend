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

package controllers

import generators.ModelGenerators
import java.util.UUID
import base.SpecBase
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import models._
import models.address.Address
import services.AddressLookupService
import cats.data.EitherT
import org.scalamock.handlers.{CallHandler2, CallHandler4}
import org.scalamock.scalatest.MockFactory
import java.net.URL
import uk.gov.hmrc.http.HeaderCarrier
import scala.concurrent.ExecutionContext.Implicits.global
import navigation.{FakeNotificationNavigator, NotificationNavigator}
import repositories.SessionRepository
import play.api.i18n.Messages

import scala.concurrent.Future

class YourAddressLookupControllerSpec extends SpecBase with MockFactory with ModelGenerators {

  def addressLookupOnwardRoute = Call("GET", "http://localhost:9000/foo")
  def onwardRoute = Call("GET", "/foo")

  lazy val addressLookupRoute = notification.routes.YourAddressLookupController.lookupAddress(NormalMode).url
  lazy val mockSessionRepository = mock[SessionRepository]

  val addressLookupService = mock[AddressLookupService]

  def mockGetIndividualAddressLookupRedirect(redirectUrl: Call)(
    response: Either[Error, URL]
  ): CallHandler4[Call, UserAnswers, HeaderCarrier, Messages, EitherT[Future, Error, URL]] =
    (addressLookupService
      .getYourAddressLookupRedirect(_: Call, _: UserAnswers)(_: HeaderCarrier, _: Messages))
      .expects(redirectUrl, *, *, *)
      .returning(EitherT.fromEither[Future](response))

  def mockRetrieveUserAddress(addressId: UUID)(
    response: Either[Error, Address]
  ): CallHandler2[UUID, HeaderCarrier, EitherT[Future, Error, Address]] =
    (addressLookupService
      .retrieveUserAddress(_: UUID)(_: HeaderCarrier))
      .expects(addressId, *)
      .returning(EitherT.fromEither[Future](response))

  def buildApplication = applicationBuilder(userAnswers = Some(emptyUserAnswers)).overrides(
    bind[SessionRepository].toInstance(mockSessionRepository),
    bind[AddressLookupService].toInstance(addressLookupService),
    bind[NotificationNavigator].toInstance(new FakeNotificationNavigator(onwardRoute))
  ).build()

  "lookupAddress" - {

    "must redirect to the URL returned by the address lookup service" in {
      val application = buildApplication

      running(application) {
        val request = FakeRequest(GET, addressLookupRoute)

        mockGetIndividualAddressLookupRedirect(notification.routes.YourAddressLookupController.retrieveConfirmedAddress(NormalMode, None))(Right(new URL("http://localhost:9000/foo")))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual addressLookupOnwardRoute.url
      }
    }

    "must handle error scenarios appropriately" in {
      val application = buildApplication

      running(application) {
        val request = FakeRequest(GET, addressLookupRoute)

        mockGetIndividualAddressLookupRedirect(notification.routes.YourAddressLookupController.retrieveConfirmedAddress(NormalMode, None))(Left(Error("Something went wrong")))

        the [Exception] thrownBy status(route(application, request).value) must have message "Something went wrong"
      }
    }

  }

  "retrieveConfirmedAddress" - {

    "must update sessionRepo and redirect to the correct place when an address is returned by the address lookup service" in {

      val application = buildApplication

      val uuid = UUID.randomUUID()
      lazy val retrieveAddressRoute = notification.routes.YourAddressLookupController.retrieveConfirmedAddress(NormalMode, Some(uuid)).url

      running(application) {
        val request = FakeRequest(GET, retrieveAddressRoute)

        (mockSessionRepository.set _).expects(*).returning(Future.successful(true))

        mockRetrieveUserAddress(uuid)(Right(sampleAddress))
        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must redirect to the address lookup when an id isn't entered" in {

      val application = buildApplication
      lazy val retrieveAddressRoute = notification.routes.YourAddressLookupController.retrieveConfirmedAddress(NormalMode).url

      running(application) {
        val request = FakeRequest(GET, retrieveAddressRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual notification.routes.YourAddressLookupController.lookupAddress(NormalMode).url
      }
    }

    "must handle error scenarios appropriately" in {
      val application = buildApplication

      running(application) {
        val request = FakeRequest(GET, addressLookupRoute)

        mockGetIndividualAddressLookupRedirect(notification.routes.YourAddressLookupController.retrieveConfirmedAddress(NormalMode, None))(Left(Error("Something went wrong")))

        the [Exception] thrownBy status(route(application, request).value) must have message "Something went wrong"
      }
    }

  }
}
