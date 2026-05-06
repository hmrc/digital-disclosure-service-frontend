/*
 * Copyright 2026 HM Revenue & Customs
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
import forms.onshore.WhyYouSubmittedAnInaccurateOnshoreReturnFormProvider
import models.WhyYouSubmittedAnInaccurateOnshoreReturn._
import models.{AreYouTheEntity, NormalMode, RelatesTo, UserAnswers, WhyYouSubmittedAnInaccurateOnshoreReturn}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages._
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.onshore.WhyYouSubmittedAnInaccurateOnshoreReturnView

import scala.concurrent.Future

class WhyYouSubmittedAnInaccurateOnshoreReturnControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val whyInaccurateRoute =
    routes.WhyYouSubmittedAnInaccurateOnshoreReturnController.onPageLoad(NormalMode).url

  val formProvider = new WhyYouSubmittedAnInaccurateOnshoreReturnFormProvider()
  val form         = formProvider("WhyYouSubmittedAnInaccurateReturn.error.required.you")

  "WhyYouSubmittedAnInaccurateOnshoreReturn Controller" - {

    "must return OK and the correct view for a GET" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        ua2 <- ua.set(RelatesToPage, RelatesTo.AnIndividual)
      } yield ua2).success.value

      val areTheyTheIndividual = userAnswers.isTheUserTheIndividual
      val entity               = userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, whyInaccurateRoute)
      val result  = route(application, request).value
      val view    = application.injector.instanceOf[WhyYouSubmittedAnInaccurateOnshoreReturnView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode, areTheyTheIndividual, entity)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        ua2 <- ua.set(RelatesToPage, RelatesTo.AnIndividual)
        ua3 <-
          ua2.set(WhyYouSubmittedAnInaccurateOnshoreReturnPage, WhyYouSubmittedAnInaccurateOnshoreReturn.values.toSet)
      } yield ua3).success.value

      val areTheyTheIndividual = userAnswers.isTheUserTheIndividual
      val entity               = userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, whyInaccurateRoute)
      val result  = route(application, request).value
      val view    = application.injector.instanceOf[WhyYouSubmittedAnInaccurateOnshoreReturnView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form.fill(WhyYouSubmittedAnInaccurateOnshoreReturn.values.toSet),
        NormalMode,
        areTheyTheIndividual,
        entity
      )(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        ua2 <- ua.set(RelatesToPage, RelatesTo.AnIndividual)
      } yield ua2).success.value

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(userAnswers))

      val request =
        FakeRequest(POST, whyInaccurateRoute)
          .withFormUrlEncodedBody(("value[0]", WhyYouSubmittedAnInaccurateOnshoreReturn.values.head.toString))

      val result = route(applicationWithFakeOnshoreNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        ua2 <- ua.set(RelatesToPage, RelatesTo.AnIndividual)
      } yield ua2).success.value

      val areTheyTheIndividual = userAnswers.isTheUserTheIndividual
      val entity               = userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      setupMockSessionResponse(Some(userAnswers))

      val request =
        FakeRequest(POST, whyInaccurateRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))
      val view      = application.injector.instanceOf[WhyYouSubmittedAnInaccurateOnshoreReturnView]
      val result    = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, areTheyTheIndividual, entity)(
        request,
        messages
      ).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {
      setupMockSessionResponse()

      val request = FakeRequest(GET, whyInaccurateRoute)
      val result  = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to Index for a POST if no existing data is found" in {
      setupMockSessionResponse()

      val request =
        FakeRequest(POST, whyInaccurateRoute)
          .withFormUrlEncodedBody(("value[0]", WhyYouSubmittedAnInaccurateOnshoreReturn.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }
  }

  "changedPages and getPages logic" - {

    "must return CDFOnshorePage when DeliberatelyInaccurate is not selected" in {
      val controller  = application.injector.instanceOf[WhyYouSubmittedAnInaccurateOnshoreReturnController]
      val userAnswers = UserAnswers("id", "session-123")
        .set(
          WhyYouSubmittedAnInaccurateOnshoreReturnPage,
          Set[WhyYouSubmittedAnInaccurateOnshoreReturn](DeliberatelyInaccurate)
        )
        .success
        .value

      val reasons: Set[WhyYouSubmittedAnInaccurateOnshoreReturn] = Set(ReasonableMistake)
      val (pages, changed)                                       = controller.changedPages(userAnswers, reasons)

      pages must contain(CDFOnshorePage)
      changed mustBe true
    }

    "must return ReasonableCareOnshorePage when ReasonableMistake is not selected" in {
      val controller  = application.injector.instanceOf[WhyYouSubmittedAnInaccurateOnshoreReturnController]
      val userAnswers = UserAnswers("id", "session-123")
        .set(
          WhyYouSubmittedAnInaccurateOnshoreReturnPage,
          Set[WhyYouSubmittedAnInaccurateOnshoreReturn](ReasonableMistake)
        )
        .success
        .value

      val reasons: Set[WhyYouSubmittedAnInaccurateOnshoreReturn] = Set(DeliberatelyInaccurate)
      val (pages, changed)                                       = controller.changedPages(userAnswers, reasons)

      pages must contain(ReasonableCareOnshorePage)
      changed mustBe true
    }

    "must return both pages when neither DeliberatelyInaccurate nor ReasonableMistake is selected" in {
      val controller  = application.injector.instanceOf[WhyYouSubmittedAnInaccurateOnshoreReturnController]
      val userAnswers = UserAnswers("id", "session-123")
        .set(
          WhyYouSubmittedAnInaccurateOnshoreReturnPage,
          Set[WhyYouSubmittedAnInaccurateOnshoreReturn](ReasonableMistake)
        )
        .success
        .value

      val reasons: Set[WhyYouSubmittedAnInaccurateOnshoreReturn] = Set(NoReasonableCare)
      val (pages, changed)                                       = controller.changedPages(userAnswers, reasons)

      pages must contain allOf (CDFOnshorePage, ReasonableCareOnshorePage)
      changed mustBe true
    }

    "must return empty list and false when values have not changed" in {
      val controller                                             = application.injector.instanceOf[WhyYouSubmittedAnInaccurateOnshoreReturnController]
      val reasons: Set[WhyYouSubmittedAnInaccurateOnshoreReturn] = Set(DeliberatelyInaccurate, ReasonableMistake)
      val userAnswers                                            = UserAnswers("id", "session-123")
        .set(WhyYouSubmittedAnInaccurateOnshoreReturnPage, reasons)
        .success
        .value

      val (pages, changed) = controller.changedPages(userAnswers, reasons)

      pages mustBe empty
      changed mustBe false
    }
  }
}
