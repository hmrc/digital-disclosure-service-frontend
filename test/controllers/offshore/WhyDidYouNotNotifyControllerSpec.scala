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
import forms.WhyDidYouNotNotifyFormProvider
import models.WhyDidYouNotNotify._
import models.{AreYouTheEntity, NormalMode, RelatesTo, UserAnswers, WhyDidYouNotNotify}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages._
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.offshore.WhyDidYouNotNotifyView

import scala.concurrent.Future

class WhyDidYouNotNotifyControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val whyDidYouNotNotifyRoute =
    routes.WhyDidYouNotNotifyController.onPageLoad(NormalMode).url

  val formProvider = new WhyDidYouNotNotifyFormProvider()
  val form         = formProvider()

  "WhyDidYouNotNotify Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = (for {
        userAnswer          <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
      } yield uaWithRelatesToPage).success.value

      val areTheyTheIndividual = userAnswers.isTheUserTheIndividual
      val entity               = userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, whyDidYouNotNotifyRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[WhyDidYouNotNotifyView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual view(form, NormalMode, areTheyTheIndividual, entity)(using request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = (for {
        userAnswer          <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
        uaWithWhyPage       <- uaWithRelatesToPage.set(
                                 WhyDidYouNotNotifyPage,
                                 WhyDidYouNotNotify.values.toSet
                               )
      } yield uaWithWhyPage).success.value

      val areTheyTheIndividual = userAnswers.isTheUserTheIndividual
      val entity               = userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, whyDidYouNotNotifyRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[WhyDidYouNotNotifyView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form.fill(WhyDidYouNotNotify.values.toSet),
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
        FakeRequest(POST, whyDidYouNotNotifyRoute)
          .withFormUrlEncodedBody(("value[0]", WhyDidYouNotNotify.values.head.toString))

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
        FakeRequest(POST, whyDidYouNotNotifyRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[WhyDidYouNotNotifyView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, areTheyTheIndividual, entity)(
        using request,
        messages
      ).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, whyDidYouNotNotifyRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to Index for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, whyDidYouNotNotifyRoute)
          .withFormUrlEncodedBody(("value[0]", WhyDidYouNotNotify.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }
  }

  "changedPages and getPages logic" - {

    "must return ContractualDisclosureFacilityPage when DeliberatelyDidNotNotify is not selected" in {
      val controller  = application.injector.instanceOf[WhyDidYouNotNotifyController]
      val userAnswers = UserAnswers("id", "session-123")
        .set(WhyDidYouNotNotifyPage, Set[WhyDidYouNotNotify](DeliberatelyDidNotNotify))
        .success
        .value

      val reasons: Set[WhyDidYouNotNotify] = Set(ReasonableExcuse)
      val (pages, changed)                 = controller.changedPages(userAnswers, reasons)

      pages must contain(ContractualDisclosureFacilityPage)
      changed mustBe true
    }

    "must return WhatIsYourReasonableExcusePage when ReasonableExcuse is not selected" in {
      val controller  = application.injector.instanceOf[WhyDidYouNotNotifyController]
      val userAnswers = UserAnswers("id", "session-123")
        .set(WhyDidYouNotNotifyPage, Set[WhyDidYouNotNotify](ReasonableExcuse))
        .success
        .value

      val reasons: Set[WhyDidYouNotNotify] = Set(DeliberatelyDidNotNotify)
      val (pages, changed)                 = controller.changedPages(userAnswers, reasons)

      pages must contain(WhatIsYourReasonableExcusePage)
      changed mustBe true
    }

    "must return both pages when neither DeliberatelyDidNotNotify nor ReasonableExcuse is selected" in {
      val controller  = application.injector.instanceOf[WhyDidYouNotNotifyController]
      val userAnswers = UserAnswers("id", "session-123")
        .set(WhyDidYouNotNotifyPage, Set[WhyDidYouNotNotify](ReasonableExcuse))
        .success
        .value

      val reasons: Set[WhyDidYouNotNotify] = Set(NotDeliberatelyNoReasonableExcuse)
      val (pages, changed)                 = controller.changedPages(userAnswers, reasons)

      pages must contain allOf (ContractualDisclosureFacilityPage, WhatIsYourReasonableExcusePage)
      changed mustBe true
    }

    "must return empty list and false when values haven't changed" in {
      val controller                       = application.injector.instanceOf[WhyDidYouNotNotifyController]
      val reasons: Set[WhyDidYouNotNotify] = Set(DeliberatelyDidNotNotify, ReasonableExcuse)
      val userAnswers                      = UserAnswers("id", "session-123")
        .set(WhyDidYouNotNotifyPage, reasons)
        .success
        .value

      val (pages, changed) = controller.changedPages(userAnswers, reasons)

      pages mustBe empty
      changed mustBe false
    }
  }
}
