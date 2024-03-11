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

package controllers.letting

import base.SpecBase
import cats.data.EitherT
import generators.ModelGenerators
import models._
import models.address.Address
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.when
import org.mockito.stubbing.OngoingStubbing
import play.api.Application
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._

import java.net.URL
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RentalAddressLookupControllerSpec extends SpecBase with ModelGenerators {

  def addressLookupOnwardRoute: Call = Call("GET", "http://localhost:15003/foo")
  def onwardRoute: Call = Call("GET", "/foo")

  lazy val addressLookupRoute: String = routes.RentalAddressLookupController.lookupAddress(0, NormalMode).url

  def mockGetRentalAddressLookupRedirect(index: Int, redirectUrl: Call)
                                        (response: Either[Error, URL]): OngoingStubbing[EitherT[Future, Error, URL]] =
    when(mockAddressLookupService.getRentalAddressLookupRedirect(eqTo(redirectUrl), eqTo(index))(any(), any()))
      .thenReturn(EitherT.fromEither[Future](response))

  def mockRetrieveUserAddress(addressId: UUID)
                             (response: Either[Error, Address]): OngoingStubbing[EitherT[Future, Error, Address]] =
    when(mockAddressLookupService.retrieveUserAddress(eqTo(addressId))(any()))
      .thenReturn(EitherT.fromEither[Future](response))

  def mockServiceSet(response: Future[Boolean]): OngoingStubbing[Future[Boolean]] =
    when(mockSessionService.set(any())(any())).thenReturn(response)

  val app: Application = applicationWithFakeLettingNavigator(onwardRoute)

  "lookupAddress" - {

    "must redirect to the URL returned by the address lookup service" in {

      val request = FakeRequest(GET, addressLookupRoute)

      setupMockSessionResponse(Some(emptyUserAnswers))

      mockGetRentalAddressLookupRedirect(
        0, routes.RentalAddressLookupController.retrieveConfirmedAddress(0, NormalMode, None)
      )(Right(new URL("http://localhost:15003/foo")))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual addressLookupOnwardRoute.url
    }

    "must handle error scenarios appropriately" in {

      val request = FakeRequest(GET, addressLookupRoute)

      setupMockSessionResponse(Some(emptyUserAnswers))

      mockGetRentalAddressLookupRedirect(
        0, routes.RentalAddressLookupController.retrieveConfirmedAddress(0, NormalMode, None)
      )(Left(Error("Something went wrong")))

      the[Exception] thrownBy status(route(app, request).value) must have message "Something went wrong"
    }

  }

  "retrieveConfirmedAddress" - {

    "must update sessionRepo and redirect to the correct place when an address is returned by the address lookup service" in {

      val uuid = UUID.randomUUID()
      lazy val retrieveAddressRoute =
        routes.RentalAddressLookupController.retrieveConfirmedAddress(0, NormalMode, Some(uuid)).url

      val request = FakeRequest(GET, retrieveAddressRoute)

      mockServiceSet(Future.successful(true))
      setupMockSessionResponse(Some(emptyUserAnswers))

      mockRetrieveUserAddress(uuid)(Right(sampleAddress))
      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must redirect to the address lookup when an id isn't entered" in {

      lazy val retrieveAddressRoute = routes.RentalAddressLookupController.retrieveConfirmedAddress(0, NormalMode).url

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, retrieveAddressRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.RentalAddressLookupController.lookupAddress(0, NormalMode).url
    }

    "must handle error scenarios appropriately" in {

      val request = FakeRequest(GET, addressLookupRoute)

      setupMockSessionResponse(Some(emptyUserAnswers))

      mockGetRentalAddressLookupRedirect(
        0, routes.RentalAddressLookupController.retrieveConfirmedAddress(0, NormalMode, None)
      )(Left(Error("Something went wrong")))

      the[Exception] thrownBy status(route(app, request).value) must have message "Something went wrong"
    }
  }
}
