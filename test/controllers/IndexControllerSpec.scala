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

import base.SpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.IndexView
import repositories.SessionRepository
import org.scalacheck.Arbitrary.arbitrary
import models.UserAnswers
import models.store.notification.Metadata
import scala.concurrent.ExecutionContext.Implicits.global
import generators.Generators
import java.time.LocalDateTime

class IndexControllerSpec extends SpecBase with Generators {

  "Index Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, routes.IndexController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[IndexView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual view()(request, messages(application)).toString
      }
    }

    "must set user answers where one doesn't exist" in {

      val request = FakeRequest(GET, routes.IndexController.onPageLoad.url)
      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        route(application, request).value

        val sessionRepo = application.injector.instanceOf[SessionRepository]
        sessionRepo.get("id").map(uaOpt => uaOpt mustBe 'defined)
      }
    }

    "must retain existing user answers where one exists" in {

      for {
        userAnswers <- arbitrary[UserAnswers]
      } yield {
        val request = FakeRequest(GET, routes.IndexController.onPageLoad.url)
        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        running(application) {
          route(application, request).value
          
          val sessionRepo = application.injector.instanceOf[SessionRepository]
          sessionRepo.get("id").map(uaOpt => uaOpt mustBe Some(userAnswers))
        }
      }
    }

    "must clear the session and start a new one where an existing draft exists but has been submitted" in {

      val userAnswers = UserAnswers("id", metadata = Metadata(submissionTime = Some(LocalDateTime.now)))
      val expectedUserAnswers = UserAnswers("id")
      val request = FakeRequest(GET, routes.IndexController.onPageLoad.url)
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        route(application, request).value
        
        val sessionRepo = application.injector.instanceOf[SessionRepository]
        sessionRepo.get("id").map(uaOpt => uaOpt mustBe Some(expectedUserAnswers))
      }
    }
  }
}
