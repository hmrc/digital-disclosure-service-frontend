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
import forms.onshore.WhyDidYouNotFileAReturnOnTimeOnshoreFormProvider
import models.WhyDidYouNotFileAReturnOnTimeOnshore._
import models.{AreYouTheEntity, NormalMode, RelatesTo, UserAnswers, WhyDidYouNotFileAReturnOnTimeOnshore}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages._
import pages.onshore.WhyDidYouNotFileAReturnOnTimeOnshorePage
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.onshore.WhyDidYouNotFileAReturnOnTimeOnshoreView

import scala.concurrent.Future

class WhyDidYouNotFileAReturnOnTimeOnshoreControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val whyDidYouNotFileRoute =
    routes.WhyDidYouNotFileAReturnOnTimeOnshoreController.onPageLoad(NormalMode).url

  val formProvider = new WhyDidYouNotFileAReturnOnTimeOnshoreFormProvider()
  val form         = formProvider(true, RelatesTo.AnIndividual)

  "WhyDidYouNotFileAReturnOnTimeOnshore Controller" - {

    "must return OK and the correct view for a GET" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        ua2 <- ua.set(RelatesToPage, RelatesTo.AnIndividual)
      } yield ua2).success.value

      val areTheyTheIndividual = userAnswers.isTheUserTheIndividual
      val entity               = userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, whyDidYouNotFileRoute)
      val result  = route(application, request).value
      val view    = application.injector.instanceOf[WhyDidYouNotFileAReturnOnTimeOnshoreView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode, areTheyTheIndividual, entity)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        ua2 <- ua.set(RelatesToPage, RelatesTo.AnIndividual)
        ua3 <- ua2.set(WhyDidYouNotFileAReturnOnTimeOnshorePage, WhyDidYouNotFileAReturnOnTimeOnshore.values.toSet)
      } yield ua3).success.value

      val areTheyTheIndividual = userAnswers.isTheUserTheIndividual
      val entity               = userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, whyDidYouNotFileRoute)
      val result  = route(application, request).value
      val view    = application.injector.instanceOf[WhyDidYouNotFileAReturnOnTimeOnshoreView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form.fill(WhyDidYouNotFileAReturnOnTimeOnshore.values.toSet),
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
        FakeRequest(POST, whyDidYouNotFileRoute)
          .withFormUrlEncodedBody(("value[0]", WhyDidYouNotFileAReturnOnTimeOnshore.values.head.toString))

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
        FakeRequest(POST, whyDidYouNotFileRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))
      val view      = application.injector.instanceOf[WhyDidYouNotFileAReturnOnTimeOnshoreView]
      val result    = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, areTheyTheIndividual, entity)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {
      setupMockSessionResponse()

      val request = FakeRequest(GET, whyDidYouNotFileRoute)
      val result  = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to Index for a POST if no existing data is found" in {
      setupMockSessionResponse()

      val request =
        FakeRequest(POST, whyDidYouNotFileRoute)
          .withFormUrlEncodedBody(("value[0]", WhyDidYouNotFileAReturnOnTimeOnshore.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }
  }

  "changedPages and getPages logic" - {

    "must return CDFOnshorePage when DeliberatelyWithheldInformation is not selected" in {
      val controller = application.injector.instanceOf[WhyDidYouNotFileAReturnOnTimeOnshoreController]
      val userAnswers = UserAnswers("id", "session-123")
        .set(WhyDidYouNotFileAReturnOnTimeOnshorePage, Set[WhyDidYouNotFileAReturnOnTimeOnshore](DeliberatelyWithheldInformation))
        .success.value

      val reasons: Set[WhyDidYouNotFileAReturnOnTimeOnshore] = Set(ReasonableExcuse)
      val (pages, changed) = controller.changedPages(userAnswers, reasons)

      pages must contain(CDFOnshorePage)
      changed mustBe true
    }

    "must return ReasonableExcuseForNotFilingOnshorePage when ReasonableExcuse is not selected" in {
      val controller = application.injector.instanceOf[WhyDidYouNotFileAReturnOnTimeOnshoreController]
      val userAnswers = UserAnswers("id", "session-123")
        .set(WhyDidYouNotFileAReturnOnTimeOnshorePage, Set[WhyDidYouNotFileAReturnOnTimeOnshore](ReasonableExcuse))
        .success.value

      val reasons: Set[WhyDidYouNotFileAReturnOnTimeOnshore] = Set(DeliberatelyWithheldInformation)
      val (pages, changed) = controller.changedPages(userAnswers, reasons)

      pages must contain(ReasonableExcuseForNotFilingOnshorePage)
      changed mustBe true
    }

    "must return both pages when neither DeliberatelyWithheldInformation nor ReasonableExcuse is selected" in {
      val controller = application.injector.instanceOf[WhyDidYouNotFileAReturnOnTimeOnshoreController]
      val userAnswers = UserAnswers("id", "session-123")
        .set(WhyDidYouNotFileAReturnOnTimeOnshorePage, Set[WhyDidYouNotFileAReturnOnTimeOnshore](ReasonableExcuse))
        .success.value

      val reasons: Set[WhyDidYouNotFileAReturnOnTimeOnshore] = Set(DidNotWithholdInformationOnPurpose)
      val (pages, changed) = controller.changedPages(userAnswers, reasons)

      pages must contain allOf(CDFOnshorePage, ReasonableExcuseForNotFilingOnshorePage)
      changed mustBe true
    }

    "must return empty list and false when values have not changed" in {
      val controller  = application.injector.instanceOf[WhyDidYouNotFileAReturnOnTimeOnshoreController]
      val reasons: Set[WhyDidYouNotFileAReturnOnTimeOnshore] = Set(DeliberatelyWithheldInformation, ReasonableExcuse)
      val userAnswers = UserAnswers("id", "session-123")
        .set(WhyDidYouNotFileAReturnOnTimeOnshorePage, reasons)
        .success.value

      val (pages, changed) = controller.changedPages(userAnswers, reasons)

      pages mustBe empty
      changed mustBe false
    }
  }
}