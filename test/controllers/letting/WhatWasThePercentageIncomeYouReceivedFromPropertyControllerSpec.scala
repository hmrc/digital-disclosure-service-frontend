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

package controllers.letting

import base.SpecBase
import forms.WhatWasThePercentageIncomeYouReceivedFromPropertyFormProvider
import models.{LettingProperty, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.LettingPropertyPage
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.letting.WhatWasThePercentageIncomeYouReceivedFromPropertyView

import scala.concurrent.Future

class WhatWasThePercentageIncomeYouReceivedFromPropertyControllerSpec extends SpecBase with MockitoSugar {

  val formProvider    = new WhatWasThePercentageIncomeYouReceivedFromPropertyFormProvider()
  val form: Form[Int] = formProvider()

  def onwardRoute: Call = Call("GET", "/foo")

  val validAnswer = 1

  lazy val whatWasThePercentageIncomeYouReceivedFromPropertyRoute: String =
    routes.WhatWasThePercentageIncomeYouReceivedFromPropertyController.onPageLoad(0, NormalMode).url

  "WhatWasThePercentageIncomeYouReceivedFromProperty Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, whatWasThePercentageIncomeYouReceivedFromPropertyRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[WhatWasThePercentageIncomeYouReceivedFromPropertyView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, 0, NormalMode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val lettingProperty = LettingProperty(percentageIncomeOnProperty = Some(1))

      val userAnswers = UserAnswers(userAnswersId, "session-123")
        .setBySeqIndex(LettingPropertyPage, 0, lettingProperty)
        .success
        .value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, whatWasThePercentageIncomeYouReceivedFromPropertyRoute)

      val view = application.injector.instanceOf[WhatWasThePercentageIncomeYouReceivedFromPropertyView]

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(validAnswer), 0, NormalMode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, whatWasThePercentageIncomeYouReceivedFromPropertyRoute)
          .withFormUrlEncodedBody(("value", validAnswer.toString))

      val result = route(applicationWithFakeLettingNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, whatWasThePercentageIncomeYouReceivedFromPropertyRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[WhatWasThePercentageIncomeYouReceivedFromPropertyView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, 0, NormalMode)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, whatWasThePercentageIncomeYouReceivedFromPropertyRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to Index for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, whatWasThePercentageIncomeYouReceivedFromPropertyRoute)
          .withFormUrlEncodedBody(("value", validAnswer.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }
  }
}
