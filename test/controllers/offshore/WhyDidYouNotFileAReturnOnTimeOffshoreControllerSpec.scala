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
import forms.offshore.WhyDidYouNotFileAReturnOnTimeOffshoreFormProvider
import models.WhyDidYouNotFileAReturnOnTimeOffshore._
import models.{AreYouTheEntity, NormalMode, RelatesTo, UserAnswers, WhyDidYouNotFileAReturnOnTimeOffshore}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages._
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.offshore.WhyDidYouNotFileAReturnOnTimeOffshoreView

import scala.concurrent.Future

class WhyDidYouNotFileAReturnOnTimeOffshoreControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val whyDidYouNotFileAReturnOnTimeOffshoreRoute =
    routes.WhyDidYouNotFileAReturnOnTimeOffshoreController.onPageLoad(NormalMode).url

  val formProvider = new WhyDidYouNotFileAReturnOnTimeOffshoreFormProvider()
  val form         = formProvider()

  "WhyDidYouNotFileAReturnOnTimeOffshore Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = (for {
        userAnswer          <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
      } yield uaWithRelatesToPage).success.value

      val areTheyTheIndividual = userAnswers.isTheUserTheIndividual
      val entity               = userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, whyDidYouNotFileAReturnOnTimeOffshoreRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[WhyDidYouNotFileAReturnOnTimeOffshoreView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual view(form, NormalMode, areTheyTheIndividual, entity)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = (for {
        userAnswer          <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
        uaWithWhyPage       <- uaWithRelatesToPage.set(
                                 WhyDidYouNotFileAReturnOnTimeOffshorePage,
                                 WhyDidYouNotFileAReturnOnTimeOffshore.values.toSet
                               )
      } yield uaWithWhyPage).success.value

      val areTheyTheIndividual = userAnswers.isTheUserTheIndividual
      val entity               = userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, whyDidYouNotFileAReturnOnTimeOffshoreRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[WhyDidYouNotFileAReturnOnTimeOffshoreView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form.fill(WhyDidYouNotFileAReturnOnTimeOffshore.values.toSet),
        NormalMode,
        areTheyTheIndividual,
        entity
      )(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      val userAnswers = (for {
        userAnswer          <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
      } yield uaWithRelatesToPage).success.value

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(userAnswers))

      val request =
        FakeRequest(POST, whyDidYouNotFileAReturnOnTimeOffshoreRoute)
          .withFormUrlEncodedBody(("value[0]", WhyDidYouNotFileAReturnOnTimeOffshore.values.head.toString))

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
        FakeRequest(POST, whyDidYouNotFileAReturnOnTimeOffshoreRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[WhyDidYouNotFileAReturnOnTimeOffshoreView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, areTheyTheIndividual, entity)(
        request,
        messages
      ).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, whyDidYouNotFileAReturnOnTimeOffshoreRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to Index for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, whyDidYouNotFileAReturnOnTimeOffshoreRoute)
          .withFormUrlEncodedBody(("value[0]", WhyDidYouNotFileAReturnOnTimeOffshore.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }
  }

  "getPages" - {

    "must return ContractualDisclosureFacilityPage when DeliberatelyWithheldInformation is not selected" in {
      val reasons: Set[WhyDidYouNotFileAReturnOnTimeOffshore] = Set(ReasonableExcuse)
      val result                                              = WhyDidYouNotFileAReturnOnTimeOffshoreController.getPages(reasons)

      result must contain(ContractualDisclosureFacilityPage)
    }

    "must return WhatIsYourReasonableExcuseForNotFilingReturnPage when ReasonableExcuse is not selected" in {
      val reasons: Set[WhyDidYouNotFileAReturnOnTimeOffshore] = Set(DeliberatelyWithheldInformation)
      val result                                              = WhyDidYouNotFileAReturnOnTimeOffshoreController.getPages(reasons)

      result must contain(WhatIsYourReasonableExcuseForNotFilingReturnPage)
    }

    "must return both pages when neither DeliberatelyWithheldInformation nor ReasonableExcuse is selected" in {
      val reasons: Set[WhyDidYouNotFileAReturnOnTimeOffshore] = Set(DidNotWithholdInformationOnPurpose)
      val result                                              = WhyDidYouNotFileAReturnOnTimeOffshoreController.getPages(reasons)

      result must contain allOf (ContractualDisclosureFacilityPage, WhatIsYourReasonableExcuseForNotFilingReturnPage)
    }

    "must return empty list when both DeliberatelyWithheldInformation and ReasonableExcuse are selected" in {
      val reasons: Set[WhyDidYouNotFileAReturnOnTimeOffshore] = Set(DeliberatelyWithheldInformation, ReasonableExcuse)
      val result                                              = WhyDidYouNotFileAReturnOnTimeOffshoreController.getPages(reasons)

      result mustBe empty
    }
  }

  "ClearingCondition" - {

    "must return true when condition is met (selections not in reasons)" in {
      val condition = WhyDidYouNotFileAReturnOnTimeOffshoreController.ClearingCondition(
        Set(DeliberatelyWithheldInformation),
        List(ContractualDisclosureFacilityPage)
      )

      condition.isConditionMet(Set(ReasonableExcuse)) mustBe true
    }

    "must return false when condition is not met (selections in reasons)" in {
      val condition = WhyDidYouNotFileAReturnOnTimeOffshoreController.ClearingCondition(
        Set(DeliberatelyWithheldInformation),
        List(ContractualDisclosureFacilityPage)
      )

      condition.isConditionMet(Set(DeliberatelyWithheldInformation)) mustBe false
    }
  }
}
