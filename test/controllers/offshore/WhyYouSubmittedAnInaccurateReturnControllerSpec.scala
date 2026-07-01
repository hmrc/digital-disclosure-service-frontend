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
import forms.offshore.WhyYouSubmittedAnInaccurateOffshoreReturnFormProvider
import models.WhyYouSubmittedAnInaccurateReturn._
import models.{AreYouTheEntity, NormalMode, RelatesTo, UserAnswers, WhyYouSubmittedAnInaccurateReturn}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages._
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.offshore.WhyYouSubmittedAnInaccurateReturnView

import scala.concurrent.Future

class WhyYouSubmittedAnInaccurateReturnControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val whyYouSubmittedAnInaccurateReturnRoute =
    routes.WhyYouSubmittedAnInaccurateReturnController.onPageLoad(NormalMode).url

  val formProvider = new WhyYouSubmittedAnInaccurateOffshoreReturnFormProvider()
  val form         = formProvider("WhyYouSubmittedAnInaccurateReturn.error.required.you")

  "WhyYouSubmittedAnInaccurateReturn Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = (for {
        userAnswer          <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
      } yield uaWithRelatesToPage).success.value

      val areTheyTheIndividual = userAnswers.isTheUserTheIndividual
      val entity               = userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, whyYouSubmittedAnInaccurateReturnRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[WhyYouSubmittedAnInaccurateReturnView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual view(form, NormalMode, areTheyTheIndividual, entity)(using
        request,
        messages
      ).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = (for {
        userAnswer          <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
        uaWithWhyPage       <- uaWithRelatesToPage.set(
                                 WhyYouSubmittedAnInaccurateOffshoreReturnPage,
                                 WhyYouSubmittedAnInaccurateReturn.values.toSet
                               )
      } yield uaWithWhyPage).success.value

      val areTheyTheIndividual = userAnswers.isTheUserTheIndividual
      val entity               = userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, whyYouSubmittedAnInaccurateReturnRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[WhyYouSubmittedAnInaccurateReturnView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form.fill(WhyYouSubmittedAnInaccurateReturn.values.toSet),
        NormalMode,
        areTheyTheIndividual,
        entity
      )(using request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      val userAnswers = (for {
        userAnswer          <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
      } yield uaWithRelatesToPage).success.value

      when(mockSessionService.set(any())(using any())) `thenReturn` Future.successful(true)
      setupMockSessionResponse(Some(userAnswers))

      val request =
        FakeRequest(POST, whyYouSubmittedAnInaccurateReturnRoute)
          .withFormUrlEncodedBody(("value[0]", WhyYouSubmittedAnInaccurateReturn.values.head.toString))

      val result = route(applicationWithFakeOffshoreNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = (for {
        userAnswer          <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
      } yield uaWithRelatesToPage).success.value

      val areTheyTheIndividual = userAnswers.isTheUserTheIndividual
      val entity               = userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      setupMockSessionResponse(Some(userAnswers))

      val request =
        FakeRequest(POST, whyYouSubmittedAnInaccurateReturnRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[WhyYouSubmittedAnInaccurateReturnView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, areTheyTheIndividual, entity)(using
        request,
        messages
      ).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, whyYouSubmittedAnInaccurateReturnRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to Index for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, whyYouSubmittedAnInaccurateReturnRoute)
          .withFormUrlEncodedBody(("value[0]", WhyYouSubmittedAnInaccurateReturn.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }
  }

  "getPages" - {

    "must return ContractualDisclosureFacilityPage when DeliberatelyInaccurate is not selected" in {
      val reasons: Set[WhyYouSubmittedAnInaccurateReturn] = Set(ReasonableMistake)
      val result                                          = WhyYouSubmittedAnInaccurateReturnController.getPages(reasons)

      result must contain(ContractualDisclosureFacilityPage)
    }

    "must return WhatReasonableCareDidYouTakePage when ReasonableMistake is not selected" in {
      val reasons: Set[WhyYouSubmittedAnInaccurateReturn] = Set(DeliberatelyInaccurate)
      val result                                          = WhyYouSubmittedAnInaccurateReturnController.getPages(reasons)

      result must contain(WhatReasonableCareDidYouTakePage)
    }

    "must return both pages when neither DeliberatelyInaccurate nor ReasonableMistake is selected" in {
      val reasons: Set[WhyYouSubmittedAnInaccurateReturn] = Set(NoReasonableCare)
      val result                                          = WhyYouSubmittedAnInaccurateReturnController.getPages(reasons)

      result must contain allOf (ContractualDisclosureFacilityPage, WhatReasonableCareDidYouTakePage)
    }

    "must return empty list when both DeliberatelyInaccurate and ReasonableMistake are selected" in {
      val reasons: Set[WhyYouSubmittedAnInaccurateReturn] = Set(DeliberatelyInaccurate, ReasonableMistake)
      val result                                          = WhyYouSubmittedAnInaccurateReturnController.getPages(reasons)

      result mustBe empty
    }
  }

  "ClearingCondition" - {

    "must return true when condition is met (selections not in reasons)" in {
      val condition = WhyYouSubmittedAnInaccurateReturnController.ClearingCondition(
        Set(DeliberatelyInaccurate),
        List(ContractualDisclosureFacilityPage)
      )

      condition.isConditionMet(Set(ReasonableMistake)) mustBe true
    }

    "must return false when condition is not met (selections in reasons)" in {
      val condition = WhyYouSubmittedAnInaccurateReturnController.ClearingCondition(
        Set(DeliberatelyInaccurate),
        List(ContractualDisclosureFacilityPage)
      )

      condition.isConditionMet(Set(DeliberatelyInaccurate)) mustBe false
    }
  }
}
