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

package controllers.notification

import base.ControllerSpecBase
import forms.DoYouHaveNationalInsuranceNumberFormProvider
import models.{CheckMode, DoYouHaveNationalInsuranceNumber, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.{DoYouHaveNationalInsuranceNumberPage, WhatIsYourNationalInsuranceNumberPage}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.notification.DoYouHaveNationalInsuranceNumberView

import scala.concurrent.Future

class DoYouHaveNationalInsuranceNumberControllerSpec extends ControllerSpecBase {

  def onwardRoute = Call("GET", "/foo")

  lazy val doYouHaveNationalInsuranceNumberRoute =
    routes.DoYouHaveNationalInsuranceNumberController.onPageLoad(NormalMode).url

  val formProvider = new DoYouHaveNationalInsuranceNumberFormProvider()
  val form         = formProvider()

  "DoYouHaveNationalInsuranceNumber Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, doYouHaveNationalInsuranceNumberRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[DoYouHaveNationalInsuranceNumberView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode, false)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId, "session-123")
        .set(DoYouHaveNationalInsuranceNumberPage, DoYouHaveNationalInsuranceNumber.values.head)
        .success
        .value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, doYouHaveNationalInsuranceNumberRoute)

      val view = application.injector.instanceOf[DoYouHaveNationalInsuranceNumberView]

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form.fill(DoYouHaveNationalInsuranceNumber.values.head),
        NormalMode,
        false
      )(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, doYouHaveNationalInsuranceNumberRoute)
          .withFormUrlEncodedBody(("value", DoYouHaveNationalInsuranceNumber.values.head.toString))

      val result = route(applicationWithFakeNotificationNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must redirect to WhatIsYourNationalInsuranceNumber screen  in check mode if DoYouHaveNationalInsuranceNumber page answer was change from No or YesButDontKnow to YesIKnow" in {
      val previousAnswers = Seq(DoYouHaveNationalInsuranceNumber.No, DoYouHaveNationalInsuranceNumber.YesButDontKnow)
      val newAnswer       = DoYouHaveNationalInsuranceNumber.YesIKnow

      val urlToTest        = routes.DoYouHaveNationalInsuranceNumberController.onPageLoad(CheckMode).url
      val destinationRoute = routes.WhatIsYourNationalInsuranceNumberController.onPageLoad(CheckMode).url

      previousAnswers.foreach(
        testChangeAnswerRouting(_, newAnswer, DoYouHaveNationalInsuranceNumberPage, urlToTest, destinationRoute)
      )
    }

    "must redirect to WhatIsYourNationalInsuranceNumber screen in check mode if the user answer was change from YesIKnow to No or YesButDontKnow" in {
      val previousAnswer = DoYouHaveNationalInsuranceNumber.YesIKnow
      val newAnswers     = Seq(DoYouHaveNationalInsuranceNumber.No, DoYouHaveNationalInsuranceNumber.YesButDontKnow)

      val urlToTest        = routes.DoYouHaveNationalInsuranceNumberController.onPageLoad(CheckMode).url
      val destinationRoute = routes.CheckYourAnswersController.onPageLoad.url

      val pageToClean = List(WhatIsYourNationalInsuranceNumberPage)

      newAnswers.foreach(
        testChangeAnswerRouting(
          previousAnswer,
          _,
          DoYouHaveNationalInsuranceNumberPage,
          urlToTest,
          destinationRoute,
          pageToClean
        )
      )
    }

    "must redirect to CheckYourAnswer screen if there are no changes in the user answer" in {
      val urlToTest        = routes.DoYouHaveNationalInsuranceNumberController.onPageLoad(CheckMode).url
      val destinationRoute = routes.CheckYourAnswersController.onPageLoad.url

      DoYouHaveNationalInsuranceNumber.values.foreach(value =>
        testChangeAnswerRouting(value, value, DoYouHaveNationalInsuranceNumberPage, urlToTest, destinationRoute)
      )
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, doYouHaveNationalInsuranceNumberRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[DoYouHaveNationalInsuranceNumberView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, false)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, doYouHaveNationalInsuranceNumberRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, doYouHaveNationalInsuranceNumberRoute)
          .withFormUrlEncodedBody(("value", DoYouHaveNationalInsuranceNumber.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }
  }
}
