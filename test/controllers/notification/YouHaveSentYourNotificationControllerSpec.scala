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
import models.UserAnswers
import models.store.Metadata
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.notification.YouHaveSentYourNotificationView
import config.FrontendAppConfig

class YouHaveSentYourNotificationControllerSpec extends SpecBase {

  "YouHaveSentYourNotification Controller" - {

    "must return OK and the correct view for a GET when passed a user submitted reference" in {

      val reference = "CFSS-1234567"

      val userAnswers = UserAnswers(userAnswersId, sessionId, data = Json.obj("letterReference" -> reference))

      setupMockSessionResponse(Some(userAnswers))


      val request = FakeRequest(GET, routes.YouHaveSentYourNotificationController.onPageLoad.url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[YouHaveSentYourNotificationView]

      val appConfig: FrontendAppConfig = application.injector.instanceOf[FrontendAppConfig]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(isCaseReferenceAvailable = true, reference,
        isTheEntity = true, isDisclosure = false)(request, messages, appConfig).toString
    }

    "must return OK and the correct view for a GET when passed a generated reference" in {

      val reference = "CFSS-1234567"

      val userAnswers = UserAnswers(userAnswersId, sessionId, metadata = Metadata(Some(reference)))

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, routes.YouHaveSentYourNotificationController.onPageLoad.url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[YouHaveSentYourNotificationView]

      val appConfig: FrontendAppConfig = application.injector.instanceOf[FrontendAppConfig]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(isCaseReferenceAvailable = false,
        reference, isTheEntity = true, isDisclosure = false)(request, messages, appConfig).toString
    }
  }
}