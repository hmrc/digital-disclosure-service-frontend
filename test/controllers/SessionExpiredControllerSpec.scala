/*
 * Copyright 2025 HM Revenue & Customs
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
import org.scalatest.concurrent.ScalaFutures
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.SessionExpiredView

class SessionExpiredControllerSpec extends SpecBase with ScalaFutures {

  private val view: SessionExpiredView = application.injector.instanceOf[SessionExpiredView]

  "SessionExpiredController" - {

    "onPageLoad must return OK and render the session expired view" in {
      val request = FakeRequest(GET, routes.SessionExpiredController.onPageLoad.url)

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view()(messages, request).toString
    }
  }
}
