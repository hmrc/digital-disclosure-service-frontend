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
import repositories.SessionRepository
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

  private var appInstances: List[Application] = List.empty

  override protected def beforeEach(): Unit = {
    reset(mockSessionService)
    reset(mockAddressLookupService)
    reset(mockSessionRepository)
  }

  override protected def afterAll(): Unit = {
    appInstances.foreach(_.stop())
    appInstances = List.empty
  }

  val userAnswersId: String = "id"
  val sessionId             = "session-123"

  def emptyUserAnswers: UserAnswers = UserAnswers(userAnswersId, sessionId)

  val mockSessionService: SessionService             = mock[SessionService]
  val mockAddressLookupService: AddressLookupService = mock[AddressLookupService]
  val mockSessionRepository: SessionRepository       = mock[SessionRepository]

  when(mockSessionRepository.set(any())).thenReturn(Future.successful(true))
  when(mockSessionRepository.get(any(), any())).thenReturn(Future.successful(None))

  def setupMockSessionResponse(userAnswers: Option[UserAnswers] = None): OngoingStubbing[Future[Option[UserAnswers]]] =
    when(mockSessionService.getSession(any(), any())(any())).thenReturn(Future.successful(userAnswers))

  val applicationBuilder: GuiceApplicationBuilder = new GuiceApplicationBuilder().overrides(
    bind[DataRequiredAction].to[DataRequiredActionImpl],
    bind[IdentifierAction].to[FakeIdentifierAction],
    bind[DataRetrievalAction].to[DataRetrievalActionImpl],
    bind[SessionService].toInstance(mockSessionService),
    bind[SessionRepository].toInstance(mockSessionRepository), // ✅ Mock repository to avoid MongoDB
    bind[InternalAuthTokenInitialiser].to[NoOpInternalAuthTokenInitialiser],
    bind[AddressLookupService].toInstance(mockAddressLookupService)
  )

  // ✅ Changed from val to def - creates fresh instance each time
  def application: Application = {
    val app = applicationBuilder.build()
    appInstances = app :: appInstances
    app
  }

  def applicationWithFakeLettingNavigator(onwardRoute: Call): Application = {
    val app = applicationBuilder
      .overrides(
        bind[LettingNavigator].toInstance(new FakeLettingNavigator(onwardRoute))
      )
      .build()
    appInstances = app :: appInstances
    app
  }

  def applicationWithFakeLiabilitiesNavigator(onwardRoute: Call): Application = {
    val app = applicationBuilder
      .overrides(
        bind[OtherLiabilitiesNavigator].toInstance(new FakeOtherLiabilitiesNavigator(onwardRoute))
      )
      .build()
    appInstances = app :: appInstances
    app
  }

  def applicationWithFakeNavigator(onwardRoute: Call): Application = {
    val app = applicationBuilder
      .overrides(
        bind[Navigator].toInstance(new FakeNavigator(onwardRoute))
      )
      .build()
    appInstances = app :: appInstances
    app
  }

  def applicationWithFakeOnshoreNavigator(onwardRoute: Call): Application = {
    val app = applicationBuilder
      .overrides(
        bind[OnshoreNavigator].toInstance(new FakeOnshoreNavigator(onwardRoute))
      )
      .build()
    appInstances = app :: appInstances
    app
  }

  def applicationWithFakeOffshoreNavigator(onwardRoute: Call): Application = {
    val app = applicationBuilder
      .overrides(
        bind[OffshoreNavigator].toInstance(new FakeOffshoreNavigator(onwardRoute))
      )
      .build()
    appInstances = app :: appInstances
    app
  }

  def applicationWithFakeNotificationNavigator(onwardRoute: Call): Application = {
    val app = applicationBuilder
      .overrides(
        bind[NotificationNavigator].toInstance(new FakeNotificationNavigator(onwardRoute))
      )
      .build()
    appInstances = app :: appInstances
    app
  }

  def applicationWithFakeReasonNavigator(onwardRoute: Call): Application = {
    val app = applicationBuilder
      .overrides(
        bind[ReasonNavigator].toInstance(new FakeReasonNavigator(onwardRoute))
      )
      .build()
    appInstances = app :: appInstances
    app
  }

  val messages: Messages = application.injector.instanceOf[MessagesApi].preferred(FakeRequest())
}
