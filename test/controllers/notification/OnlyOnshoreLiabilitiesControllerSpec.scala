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

package controllers

import base.SpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.notification.OnlyOnshoreLiabilitiesView
import navigation.{FakeNotificationNavigator, NotificationNavigator}
import play.api.inject.bind
import services.SessionService
import scala.concurrent.Future
import org.mockito.Mockito.when
import play.api.mvc.Call
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.ArgumentMatchers.any

class OnlyOnshoreLiabilitiesControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  "OnlyOnshoreLiabilities Controller" - {

    "must return OK and the correct view for a GET" in {

      val mockSessionService = mock[SessionService]

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilderWithSessionService(userAnswers = Some(emptyUserAnswers), mockSessionService)
          .overrides(
            bind[NotificationNavigator].toInstance(new FakeNotificationNavigator(onwardRoute))
          ).build()

      running(application) {
        val request = FakeRequest(GET, notification.routes.OnlyOnshoreLiabilitiesController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[OnlyOnshoreLiabilitiesView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(onwardRoute.url)(request, messages(application)).toString
      }
    }
  }
}
