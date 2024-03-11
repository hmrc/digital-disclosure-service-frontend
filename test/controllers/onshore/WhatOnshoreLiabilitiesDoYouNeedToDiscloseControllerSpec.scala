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
import forms.WhatOnshoreLiabilitiesDoYouNeedToDiscloseFormProvider
import models.{CheckMode, NormalMode, RelatesTo, UserAnswers, WhatOnshoreLiabilitiesDoYouNeedToDisclose}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{RelatesToPage, WhatOnshoreLiabilitiesDoYouNeedToDisclosePage}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.onshore.WhatOnshoreLiabilitiesDoYouNeedToDiscloseView

import scala.concurrent.Future

class WhatOnshoreLiabilitiesDoYouNeedToDiscloseControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val whatOnshoreLiabilitiesDoYouNeedToDiscloseRoute = routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(NormalMode).url
  lazy val whatOnshoreLiabilitiesDoYouNeedToDiscloseRouteChange = routes.WhatOnshoreLiabilitiesDoYouNeedToDiscloseController.onPageLoad(CheckMode).url

  val formProvider = new WhatOnshoreLiabilitiesDoYouNeedToDiscloseFormProvider()
  val form = formProvider()
  val isUserCompany = false

  "WhatOnshoreLiabilitiesDoYouNeedToDisclose Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, whatOnshoreLiabilitiesDoYouNeedToDiscloseRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[WhatOnshoreLiabilitiesDoYouNeedToDiscloseView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual view(form, NormalMode, isUserCompany)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId, "session-123").set(WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, WhatOnshoreLiabilitiesDoYouNeedToDisclose.values.toSet).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, whatOnshoreLiabilitiesDoYouNeedToDiscloseRoute)

      val view = application.injector.instanceOf[WhatOnshoreLiabilitiesDoYouNeedToDiscloseView]

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(WhatOnshoreLiabilitiesDoYouNeedToDisclose.values.toSet), NormalMode, isUserCompany)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, whatOnshoreLiabilitiesDoYouNeedToDiscloseRoute)
          .withFormUrlEncodedBody(("value[0]", WhatOnshoreLiabilitiesDoYouNeedToDisclose.values.head.toString))

      val result = route(applicationWithFakeOnshoreNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, whatOnshoreLiabilitiesDoYouNeedToDiscloseRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[WhatOnshoreLiabilitiesDoYouNeedToDiscloseView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, isUserCompany)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, whatOnshoreLiabilitiesDoYouNeedToDiscloseRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to Index for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, whatOnshoreLiabilitiesDoYouNeedToDiscloseRoute)
          .withFormUrlEncodedBody(("value[0]", WhatOnshoreLiabilitiesDoYouNeedToDisclose.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to the summary page if no changes are made" in {

      val previousAnswers = UserAnswers(userAnswersId, "session-123").addToSet(
        WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, WhatOnshoreLiabilitiesDoYouNeedToDisclose.values.head
      ).success.value

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(previousAnswers))

      val request =
        FakeRequest(POST, whatOnshoreLiabilitiesDoYouNeedToDiscloseRouteChange)
          .withFormUrlEncodedBody(("value[0]", WhatOnshoreLiabilitiesDoYouNeedToDisclose.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.CheckYourAnswersController.onPageLoad.url
    }

    "must redirect to Corporation Tax Page in Normal Mode if there was a change and the disclosure was for a Company " in {

      val previousAnswers = UserAnswers(userAnswersId, "session-123").addToSet(
        WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, WhatOnshoreLiabilitiesDoYouNeedToDisclose.values.head
      ).success.value
        .set(RelatesToPage, RelatesTo.ACompany).success.value

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(previousAnswers))

      val request =
        FakeRequest(POST, whatOnshoreLiabilitiesDoYouNeedToDiscloseRouteChange)
          .withFormUrlEncodedBody(("value[0]", WhatOnshoreLiabilitiesDoYouNeedToDisclose.CorporationTax.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.CorporationTaxLiabilityController.onPageLoad(0, NormalMode).url
    }


    "must redirect to Director's Loan Page in Normal Mode if there was a change, the Corporation Tax is not selected and the disclosure was for a Company" in {

      val previousAnswers = UserAnswers(userAnswersId, "session-123").addToSet(
        WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, WhatOnshoreLiabilitiesDoYouNeedToDisclose.values.head
      ).success.value
        .set(RelatesToPage, RelatesTo.ACompany).success.value

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(previousAnswers))

      val request =
        FakeRequest(POST, whatOnshoreLiabilitiesDoYouNeedToDiscloseRouteChange)
          .withFormUrlEncodedBody(("value[0]", WhatOnshoreLiabilitiesDoYouNeedToDisclose.DirectorLoan.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.DirectorLoanAccountLiabilitiesController.onPageLoad(0, NormalMode).url
    }

    "must redirect to Which Onshore Year Page in Normal Mode if there was a change, and Director's Loan or Corporation Tax are selected" in {

      val previousAnswers = UserAnswers(userAnswersId, "session-123").addToSet(
        WhatOnshoreLiabilitiesDoYouNeedToDisclosePage, WhatOnshoreLiabilitiesDoYouNeedToDisclose.CorporationTax
      ).success.value
        .set(RelatesToPage, RelatesTo.ACompany).success.value

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(previousAnswers))

      val request =
        FakeRequest(POST, whatOnshoreLiabilitiesDoYouNeedToDiscloseRouteChange)
          .withFormUrlEncodedBody(("value[0]", WhatOnshoreLiabilitiesDoYouNeedToDisclose.BusinessIncome.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.WhichOnshoreYearsController.onPageLoad(NormalMode).url
    }
  }
}
