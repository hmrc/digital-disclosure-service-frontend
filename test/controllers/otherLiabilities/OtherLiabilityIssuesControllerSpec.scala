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

package controllers.otherLiabilities

import base.SpecBase
import forms.OtherLiabilityIssuesFormProvider
import models.{CheckMode, NormalMode, OtherLiabilityIssues, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.OtherLiabilityIssuesPage
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.otherLiabilities.OtherLiabilityIssuesView

import scala.concurrent.Future

class OtherLiabilityIssuesControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute: Call = Call("GET", "/foo")

  lazy val otherLiabilityIssuesRoute: String = routes.OtherLiabilityIssuesController.onPageLoad(NormalMode).url

  val formProvider                          = new OtherLiabilityIssuesFormProvider()
  val form: Form[Set[OtherLiabilityIssues]] = formProvider()
  val view: OtherLiabilityIssuesView        = application.injector.instanceOf[OtherLiabilityIssuesView]

  "OtherLiabilityIssues Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, otherLiabilityIssuesRoute)

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual view(form, NormalMode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId, "session-123")
        .set(OtherLiabilityIssuesPage, OtherLiabilityIssues.values.toSet)
        .success
        .value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, otherLiabilityIssuesRoute)

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(OtherLiabilityIssues.values.toSet), NormalMode)(
        request,
        messages
      ).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, otherLiabilityIssuesRoute)
          .withFormUrlEncodedBody(("value[0]", OtherLiabilityIssues.values.head.toString))

      val result = route(applicationWithFakeLiabilitiesNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, otherLiabilityIssuesRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, otherLiabilityIssuesRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to Index for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, otherLiabilityIssuesRoute)
          .withFormUrlEncodedBody(("value[0]", OtherLiabilityIssues.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to WhatOtherLiabilityIssuesPage screen in check mode if Other checkbox selected" in {
      val previousPreferences: Set[OtherLiabilityIssues] = Set(OtherLiabilityIssues.InheritanceTaxIssues)
      val previousAnswers                                =
        UserAnswers("id", "session-123").set(OtherLiabilityIssuesPage, previousPreferences).success.value
      val newAnswer                                      = OtherLiabilityIssues.Other

      val urlToTest        = routes.OtherLiabilityIssuesController.onPageLoad(CheckMode).url
      val destinationRoute = routes.WhatOtherLiabilityIssuesController.onPageLoad(CheckMode).url
      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(previousAnswers))

      val request =
        FakeRequest(POST, urlToTest)
          .withFormUrlEncodedBody(("value[0]", newAnswer.toString))

      val result = route(application, request).value
      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual destinationRoute
    }

    "must redirect to DescribeTheGiftPage screen in check mode if InheritanceTaxIssues checkbox selected" in {
      val previousPreferences: Set[OtherLiabilityIssues] = Set(OtherLiabilityIssues.Other)
      val previousAnswers                                =
        UserAnswers("id", "session-123").set(OtherLiabilityIssuesPage, previousPreferences).success.value
      val newAnswer                                      = OtherLiabilityIssues.InheritanceTaxIssues

      val urlToTest        = routes.OtherLiabilityIssuesController.onPageLoad(CheckMode).url
      val destinationRoute = routes.DescribeTheGiftController.onPageLoad(CheckMode).url
      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(previousAnswers))

      val request =
        FakeRequest(POST, urlToTest)
          .withFormUrlEncodedBody(("value[0]", newAnswer.toString))

      val result = route(application, request).value
      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual destinationRoute
    }

    "must redirect to CheckYourAnswer screen if there are no changes in the user answer" in {
      val previousPreferences: Set[OtherLiabilityIssues] =
        Set(OtherLiabilityIssues.InheritanceTaxIssues, OtherLiabilityIssues.Other)
      val previousAnswers                                =
        UserAnswers("id", "session-123").set(OtherLiabilityIssuesPage, previousPreferences).success.value

      val newAnswerInheritanceTaxIssues: OtherLiabilityIssues = OtherLiabilityIssues.InheritanceTaxIssues
      val newAnswerOther: OtherLiabilityIssues                = OtherLiabilityIssues.Other

      val urlToTest        = routes.OtherLiabilityIssuesController.onPageLoad(CheckMode).url
      val destinationRoute = routes.CheckYourAnswersController.onPageLoad.url
      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(previousAnswers))

      val request =
        FakeRequest(POST, urlToTest)
          .withFormUrlEncodedBody(
            ("value[0]", newAnswerInheritanceTaxIssues.toString),
            ("value[1]", newAnswerOther.toString)
          )

      val result = route(application, request).value
      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual destinationRoute
    }
  }
}
