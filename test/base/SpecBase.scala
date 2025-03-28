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

package base

import config.{InternalAuthTokenInitialiser, NoOpInternalAuthTokenInitialiser}
import controllers.actions._
import generators.Generators
import models.UserAnswers
import navigation._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, OptionValues, TryValues}
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.Application
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Call
import play.api.test.FakeRequest
import services.{AddressLookupService, SessionService}

import scala.concurrent.Future

trait SpecBase
    extends AnyFreeSpec
    with Matchers
    with TryValues
    with OptionValues
    with ScalaFutures
    with IntegrationPatience
    with Generators
    with BeforeAndAfterEach
    with BeforeAndAfterAll {

  override protected def beforeEach(): Unit = {
    reset(mockSessionService)
    reset(mockAddressLookupService)
  }

  override protected def afterAll(): Unit =
    application.stop()

  val userAnswersId: String = "id"
  val sessionId             = "session-123"

  def emptyUserAnswers: UserAnswers = UserAnswers(userAnswersId, sessionId)

  val mockSessionService: SessionService             = mock[SessionService]
  val mockAddressLookupService: AddressLookupService = mock[AddressLookupService]

  def setupMockSessionResponse(userAnswers: Option[UserAnswers] = None): OngoingStubbing[Future[Option[UserAnswers]]] =
    when(mockSessionService.getSession(any(), any())(any())).thenReturn(Future.successful(userAnswers))

  val applicationBuilder: GuiceApplicationBuilder = new GuiceApplicationBuilder().overrides(
    bind[DataRequiredAction].to[DataRequiredActionImpl],
    bind[IdentifierAction].to[FakeIdentifierAction],
    bind[DataRetrievalAction].to[DataRetrievalActionImpl],
    bind[SessionService].toInstance(mockSessionService),
    bind[InternalAuthTokenInitialiser].to[NoOpInternalAuthTokenInitialiser],
    bind[AddressLookupService].toInstance(mockAddressLookupService)
  )
  val application: Application                    = applicationBuilder.build()

  def applicationWithFakeLettingNavigator(onwardRoute: Call): Application = applicationBuilder
    .overrides(
      bind[LettingNavigator].toInstance(new FakeLettingNavigator(onwardRoute))
    )
    .build()

  def applicationWithFakeLiabilitiesNavigator(onwardRoute: Call): Application = applicationBuilder
    .overrides(
      bind[OtherLiabilitiesNavigator].toInstance(new FakeOtherLiabilitiesNavigator(onwardRoute))
    )
    .build()

  def applicationWithFakeNavigator(onwardRoute: Call): Application = applicationBuilder
    .overrides(
      bind[Navigator].toInstance(new FakeNavigator(onwardRoute))
    )
    .build()

  def applicationWithFakeOnshoreNavigator(onwardRoute: Call): Application = applicationBuilder
    .overrides(
      bind[OnshoreNavigator].toInstance(new FakeOnshoreNavigator(onwardRoute))
    )
    .build()

  def applicationWithFakeOffshoreNavigator(onwardRoute: Call): Application = applicationBuilder
    .overrides(
      bind[OffshoreNavigator].toInstance(new FakeOffshoreNavigator(onwardRoute))
    )
    .build()

  def applicationWithFakeNotificationNavigator(onwardRoute: Call): Application = applicationBuilder
    .overrides(
      bind[NotificationNavigator].toInstance(new FakeNotificationNavigator(onwardRoute))
    )
    .build()

  def applicationWithFakeReasonNavigator(onwardRoute: Call): Application = applicationBuilder
    .overrides(
      bind[ReasonNavigator].toInstance(new FakeReasonNavigator(onwardRoute))
    )
    .build()

  val messages: Messages = application.injector.instanceOf[MessagesApi].preferred(FakeRequest())
}
