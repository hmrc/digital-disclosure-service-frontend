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
import forms.DoesTheIndividualHaveNationalInsuranceNumberFormProvider
import models._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages._
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.notification.DoesTheIndividualHaveNationalInsuranceNumberView

import scala.concurrent.Future

class DoesTheIndividualHaveNationalInsuranceNumberControllerSpec extends ControllerSpecBase {

  def onwardRoute = Call("GET", "/foo")

  lazy val doesTheIndividualHaveNationalInsuranceNumberRoute = routes.DoesTheIndividualHaveNationalInsuranceNumberController.onPageLoad(NormalMode).url

  val formProvider = new DoesTheIndividualHaveNationalInsuranceNumberFormProvider()
  val form = formProvider()

  "DoesTheIndividualHaveNationalInsuranceNumber Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, doesTheIndividualHaveNationalInsuranceNumberRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[DoesTheIndividualHaveNationalInsuranceNumberView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode, false)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId, "session-123").set(DoesTheIndividualHaveNationalInsuranceNumberPage, DoesTheIndividualHaveNationalInsuranceNumber.values.head).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, doesTheIndividualHaveNationalInsuranceNumberRoute)

      val view = application.injector.instanceOf[DoesTheIndividualHaveNationalInsuranceNumberView]

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(DoesTheIndividualHaveNationalInsuranceNumber.values.head), NormalMode, false)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, doesTheIndividualHaveNationalInsuranceNumberRoute)
          .withFormUrlEncodedBody(("value", DoesTheIndividualHaveNationalInsuranceNumber.values.head.toString))

      val result = route(applicationWithFakeNotificationNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, doesTheIndividualHaveNationalInsuranceNumberRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[DoesTheIndividualHaveNationalInsuranceNumberView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, false)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, doesTheIndividualHaveNationalInsuranceNumberRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, doesTheIndividualHaveNationalInsuranceNumberRoute)
          .withFormUrlEncodedBody(("value", DoesTheIndividualHaveNationalInsuranceNumber.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to WhatIsIndividualsNationalInsuranceNumber page (change mode) if page answer changes from No to YesIKnow in check mode" in {

      val previousAnswer = DoesTheIndividualHaveNationalInsuranceNumber.No
      val newAnswer = DoesTheIndividualHaveNationalInsuranceNumber.YesIKnow

      val urlToTest = routes.DoesTheIndividualHaveNationalInsuranceNumberController.onPageLoad(CheckMode).url
      val destinationRoute = routes.WhatIsIndividualsNationalInsuranceNumberController.onPageLoad(CheckMode).url

      testChangeAnswerRouting(previousAnswer, newAnswer, DoesTheIndividualHaveNationalInsuranceNumberPage, urlToTest, destinationRoute, Nil)
    }

    "must redirect to WhatIsIndividualsNationalInsuranceNumber page (change mode) if page answer changes from YesButDontKnow to YesIKnow in check mode" in {

      val previousAnswer = DoesTheIndividualHaveNationalInsuranceNumber.YesButDontKnow
      val newAnswer = DoesTheIndividualHaveNationalInsuranceNumber.YesIKnow

      val urlToTest = routes.DoesTheIndividualHaveNationalInsuranceNumberController.onPageLoad(CheckMode).url
      val destinationRoute = routes.WhatIsIndividualsNationalInsuranceNumberController.onPageLoad(CheckMode).url

      testChangeAnswerRouting(previousAnswer, newAnswer, DoesTheIndividualHaveNationalInsuranceNumberPage, urlToTest, destinationRoute, Nil)
    }

    "must redirect to CheckYourAnswers screen and clear WhatIsIndividualsNationalInsuranceNumber if page answer changes from YesIKnow to No in check mode" in {

      val previousAnswer = DoesTheIndividualHaveNationalInsuranceNumber.YesIKnow
      val newAnswer = DoesTheIndividualHaveNationalInsuranceNumber.No

      val urlToTest = routes.DoesTheIndividualHaveNationalInsuranceNumberController.onPageLoad(CheckMode).url
      val destinationRoute = routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, DoesTheIndividualHaveNationalInsuranceNumberPage, urlToTest, destinationRoute, List(WhatIsIndividualsNationalInsuranceNumberPage))
    }

    "must redirect to CheckYourAnswers screen and clear WhatIsIndividualsNationalInsuranceNumber if page answer changes from YesIKnow to YesButDontKnow in check mode" in {

      val previousAnswer = DoesTheIndividualHaveNationalInsuranceNumber.YesIKnow
      val newAnswer = DoesTheIndividualHaveNationalInsuranceNumber.YesButDontKnow

      val urlToTest = routes.DoesTheIndividualHaveNationalInsuranceNumberController.onPageLoad(CheckMode).url
      val destinationRoute = routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, DoesTheIndividualHaveNationalInsuranceNumberPage, urlToTest, destinationRoute, List(WhatIsIndividualsNationalInsuranceNumberPage))
    }

    "must redirect to CheckYourAnswers screen if page answer is YesIKnow and doesn't change" in {

      val previousAnswer = DoesTheIndividualHaveNationalInsuranceNumber.YesIKnow
      val newAnswer = DoesTheIndividualHaveNationalInsuranceNumber.YesIKnow

      val urlToTest = routes.DoesTheIndividualHaveNationalInsuranceNumberController.onPageLoad(CheckMode).url
      val destinationRoute = routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, DoesTheIndividualHaveNationalInsuranceNumberPage, urlToTest, destinationRoute, Nil)
    }

    "must redirect to CheckYourAnswers screen if page answer is No and doesn't change" in {

      val previousAnswer = DoesTheIndividualHaveNationalInsuranceNumber.No
      val newAnswer = DoesTheIndividualHaveNationalInsuranceNumber.No

      val urlToTest = routes.DoesTheIndividualHaveNationalInsuranceNumberController.onPageLoad(CheckMode).url
      val destinationRoute = routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, DoesTheIndividualHaveNationalInsuranceNumberPage, urlToTest, destinationRoute, Nil)
    }
  }
}
