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
import forms.onshore.WhyDidYouNotNotifyOnshoreFormProvider
import models.WhyDidYouNotNotifyOnshore._
import models.{AreYouTheEntity, NormalMode, RelatesTo, UserAnswers, WhyDidYouNotNotifyOnshore}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages._
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.onshore.WhyDidYouNotNotifyOnshoreView

import scala.concurrent.Future

class WhyDidYouNotNotifyOnshoreControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val whyDidYouNotNotifyRoute =
    routes.WhyDidYouNotNotifyOnshoreController.onPageLoad(NormalMode).url

  val formProvider = new WhyDidYouNotNotifyOnshoreFormProvider()
  val form         = formProvider(true, RelatesTo.AnIndividual)

  "WhyDidYouNotNotifyOnshore Controller" - {

    "must return OK and the correct view for a GET" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        ua2 <- ua.set(RelatesToPage, RelatesTo.AnIndividual)
      } yield ua2).success.value

      val areTheyTheIndividual = userAnswers.isTheUserTheIndividual
      val entity               = userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, whyDidYouNotNotifyRoute)
      val result  = route(application, request).value
      val view    = application.injector.instanceOf[WhyDidYouNotNotifyOnshoreView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode, areTheyTheIndividual, entity)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        ua2 <- ua.set(RelatesToPage, RelatesTo.AnIndividual)
        ua3 <- ua2.set(WhyDidYouNotNotifyOnshorePage, WhyDidYouNotNotifyOnshore.values.toSet)
      } yield ua3).success.value

      val areTheyTheIndividual = userAnswers.isTheUserTheIndividual
      val entity               = userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, whyDidYouNotNotifyRoute)
      val result  = route(application, request).value
      val view    = application.injector.instanceOf[WhyDidYouNotNotifyOnshoreView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form.fill(WhyDidYouNotNotifyOnshore.values.toSet),
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
        FakeRequest(POST, whyDidYouNotNotifyRoute)
          .withFormUrlEncodedBody(("value[0]", WhyDidYouNotNotifyOnshore.values.head.toString))

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
        FakeRequest(POST, whyDidYouNotNotifyRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))
      val view      = application.injector.instanceOf[WhyDidYouNotNotifyOnshoreView]
      val result    = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, areTheyTheIndividual, entity)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {
      setupMockSessionResponse()

      val request = FakeRequest(GET, whyDidYouNotNotifyRoute)
      val result  = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to Index for a POST if no existing data is found" in {
      setupMockSessionResponse()

      val request =
        FakeRequest(POST, whyDidYouNotNotifyRoute)
          .withFormUrlEncodedBody(("value[0]", WhyDidYouNotNotifyOnshore.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }
  }

  "changedPages and getPages logic" - {

    "must return CDFOnshorePage when DeliberatelyDidNotNotifyOnshore is not selected" in {
      val controller = application.injector.instanceOf[WhyDidYouNotNotifyOnshoreController]
      val userAnswers = UserAnswers("id", "session-123")
        .set(WhyDidYouNotNotifyOnshorePage, Set[WhyDidYouNotNotifyOnshore](DeliberatelyDidNotNotifyOnshore))
        .success.value

      val reasons: Set[WhyDidYouNotNotifyOnshore] = Set(ReasonableExcuseOnshore)
      val (pages, changed) = controller.changedPages(userAnswers, reasons)

      pages must contain(CDFOnshorePage)
      changed mustBe true
    }

    "must return ReasonableExcuseOnshorePage when ReasonableExcuseOnshore is not selected" in {
      val controller = application.injector.instanceOf[WhyDidYouNotNotifyOnshoreController]
      val userAnswers = UserAnswers("id", "session-123")
        .set(WhyDidYouNotNotifyOnshorePage, Set[WhyDidYouNotNotifyOnshore](ReasonableExcuseOnshore))
        .success.value

      val reasons: Set[WhyDidYouNotNotifyOnshore] = Set(DeliberatelyDidNotNotifyOnshore)
      val (pages, changed) = controller.changedPages(userAnswers, reasons)

      pages must contain(ReasonableExcuseOnshorePage)
      changed mustBe true
    }

    "must return both pages when neither DeliberatelyDidNotNotifyOnshore nor ReasonableExcuseOnshore is selected" in {
      val controller = application.injector.instanceOf[WhyDidYouNotNotifyOnshoreController]
      val userAnswers = UserAnswers("id", "session-123")
        .set(WhyDidYouNotNotifyOnshorePage, Set[WhyDidYouNotNotifyOnshore](ReasonableExcuseOnshore))
        .success.value

      val reasons: Set[WhyDidYouNotNotifyOnshore] = Set(NotDeliberatelyNoReasonableExcuseOnshore)
      val (pages, changed) = controller.changedPages(userAnswers, reasons)

      pages must contain allOf(CDFOnshorePage, ReasonableExcuseOnshorePage)
      changed mustBe true
    }

    "must return empty list and false when values have not changed" in {
      val controller  = application.injector.instanceOf[WhyDidYouNotNotifyOnshoreController]
      val reasons: Set[WhyDidYouNotNotifyOnshore] = Set(DeliberatelyDidNotNotifyOnshore, ReasonableExcuseOnshore)
      val userAnswers = UserAnswers("id", "session-123")
        .set(WhyDidYouNotNotifyOnshorePage, reasons)
        .success.value

      val (pages, changed) = controller.changedPages(userAnswers, reasons)

      pages mustBe empty
      changed mustBe false
    }
  }
}