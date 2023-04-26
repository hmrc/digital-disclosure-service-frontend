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

package controllers.onshore

import base.SpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.onshore.MakingNilDisclosureView
import models.{AreYouTheEntity, RelatesTo, WhyAreYouMakingThisOnshoreDisclosure, UserAnswers}
import pages.{AreYouTheEntityPage, WhyAreYouMakingThisOnshoreDisclosurePage}

class MakingNilDisclosureControllerSpec extends SpecBase {

  "MakingNilDisclosure Controller" - {

    "must return OK and the correct view for a GET" in {

      val areTheyTheIndividual = AreYouTheEntity.YesIAm
      val entity = RelatesTo.AnIndividual
      val years = 20

      val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyNoExcuse)
      val userAnswers = (for{
        ua <- UserAnswers(userAnswersId, "session-123").set(AreYouTheEntityPage, areTheyTheIndividual)
        updatedUa <- ua.set(WhyAreYouMakingThisOnshoreDisclosurePage, set)
      } yield updatedUa).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.MakingNilDisclosureController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[MakingNilDisclosureView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(userAnswers.isTheUserTheIndividual, entity, years)(request, messages(application)).toString
      }
    }
  }
}
