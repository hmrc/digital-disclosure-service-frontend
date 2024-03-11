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

package controllers.notification

import base.SpecBase
import models.NormalMode
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.notification.OnlyOnshoreLiabilitiesView

import scala.concurrent.Future

class OnlyOnshoreLiabilitiesControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  "OnlyOnshoreLiabilities Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, routes.OnlyOnshoreLiabilitiesController.onPageLoad(NormalMode).url)

      val result = route(applicationWithFakeNotificationNavigator(onwardRoute), request).value

      val view = application.injector.instanceOf[OnlyOnshoreLiabilitiesView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(onwardRoute.url, false)(request, messages).toString
    }
  }
}
