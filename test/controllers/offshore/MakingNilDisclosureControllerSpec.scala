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

package controllers.offshore

import base.SpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.offshore.MakingNilDisclosureView
import models.{AreYouTheEntity, Behaviour, RelatesTo, UserAnswers, WhyAreYouMakingThisDisclosure}
import pages.{AreYouTheEntityPage, WhyAreYouMakingThisDisclosurePage}
import services.OffshoreWhichYearsService

class MakingNilDisclosureControllerSpec extends SpecBase {

  "MakingNilDisclosure Controller" - {

    "must return OK and the correct view for a GET" in {

      val areTheyTheIndividual = AreYouTheEntity.YesIAm
      val entity               = RelatesTo.AnIndividual

      val set: Set[WhyAreYouMakingThisDisclosure] = Set(WhyAreYouMakingThisDisclosure.DidNotNotifyNoExcuse)
      val userAnswers                             = (for {
        ua        <- UserAnswers(userAnswersId, "session-123").set(AreYouTheEntityPage, areTheyTheIndividual)
        updatedUa <- ua.set(WhyAreYouMakingThisDisclosurePage, set)
      } yield updatedUa).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, routes.MakingNilDisclosureController.onPageLoad.url)

      val result = route(application, request).value

      val service = application.injector.instanceOf[OffshoreWhichYearsService]
      val year    = service.getEarliestYearByBehaviour(Behaviour.Deliberate).toString

      val view = application.injector.instanceOf[MakingNilDisclosureView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(userAnswers.isTheUserTheIndividual, entity, year)(
        request,
        messages
      ).toString
    }
  }
}
