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
import forms.IsTheIndividualRegisteredForSelfAssessmentFormProvider
import models._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages._
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.notification.IsTheIndividualRegisteredForSelfAssessmentView

import scala.concurrent.Future

class IsTheIndividualRegisteredForSelfAssessmentControllerSpec extends ControllerSpecBase {

  def onwardRoute = Call("GET", "/foo")

  lazy val isTheIndividualRegisteredForSelfAssessmentRoute =
    routes.IsTheIndividualRegisteredForSelfAssessmentController.onPageLoad(NormalMode).url

  val formProvider = new IsTheIndividualRegisteredForSelfAssessmentFormProvider()
  val form         = formProvider()

  "IsTheIndividualRegisteredForSelfAssessment Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, isTheIndividualRegisteredForSelfAssessmentRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[IsTheIndividualRegisteredForSelfAssessmentView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode, false)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId, "session-123")
        .set(IsTheIndividualRegisteredForSelfAssessmentPage, IsTheIndividualRegisteredForSelfAssessment.values.head)
        .success
        .value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, isTheIndividualRegisteredForSelfAssessmentRoute)

      val view = application.injector.instanceOf[IsTheIndividualRegisteredForSelfAssessmentView]

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form.fill(IsTheIndividualRegisteredForSelfAssessment.values.head),
        NormalMode,
        false
      )(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, isTheIndividualRegisteredForSelfAssessmentRoute)
          .withFormUrlEncodedBody(("value", IsTheIndividualRegisteredForSelfAssessment.values.head.toString))

      val result = route(applicationWithFakeNotificationNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, isTheIndividualRegisteredForSelfAssessmentRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[IsTheIndividualRegisteredForSelfAssessmentView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, false)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, isTheIndividualRegisteredForSelfAssessmentRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, isTheIndividualRegisteredForSelfAssessmentRoute)
          .withFormUrlEncodedBody(("value", IsTheIndividualRegisteredForSelfAssessment.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to WhatIsTheIndividualsUniqueTaxReferencePage page (change mode) if page answer changes from No to YesIKnow in check mode" in {

      val previousAnswer = IsTheIndividualRegisteredForSelfAssessment.No
      val newAnswer      = IsTheIndividualRegisteredForSelfAssessment.YesIKnow

      val urlToTest        = routes.IsTheIndividualRegisteredForSelfAssessmentController.onPageLoad(CheckMode).url
      val destinationRoute = routes.WhatIsTheIndividualsUniqueTaxReferenceController.onPageLoad(CheckMode).url

      testChangeAnswerRouting(
        previousAnswer,
        newAnswer,
        IsTheIndividualRegisteredForSelfAssessmentPage,
        urlToTest,
        destinationRoute,
        Nil
      )
    }

    "must redirect to WhatIsTheIndividualsUniqueTaxReferencePage page (change mode) if page answer changes from YesButDontKnow to YesIKnow in check mode" in {

      val previousAnswer = IsTheIndividualRegisteredForSelfAssessment.YesButDontKnow
      val newAnswer      = IsTheIndividualRegisteredForSelfAssessment.YesIKnow

      val urlToTest        = routes.IsTheIndividualRegisteredForSelfAssessmentController.onPageLoad(CheckMode).url
      val destinationRoute = routes.WhatIsTheIndividualsUniqueTaxReferenceController.onPageLoad(CheckMode).url

      testChangeAnswerRouting(
        previousAnswer,
        newAnswer,
        IsTheIndividualRegisteredForSelfAssessmentPage,
        urlToTest,
        destinationRoute,
        Nil
      )
    }

    "must redirect to CheckYourAnswers screen and clear WhatIsTheIndividualsUniqueTaxReferencePage if page answer changes from YesIKnow to No in check mode" in {

      val previousAnswer = IsTheIndividualRegisteredForSelfAssessment.YesIKnow
      val newAnswer      = IsTheIndividualRegisteredForSelfAssessment.No

      val urlToTest        = routes.IsTheIndividualRegisteredForSelfAssessmentController.onPageLoad(CheckMode).url
      val destinationRoute = routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(
        previousAnswer,
        newAnswer,
        IsTheIndividualRegisteredForSelfAssessmentPage,
        urlToTest,
        destinationRoute,
        List(WhatIsTheIndividualsUniqueTaxReferencePage)
      )
    }

    "must redirect to CheckYourAnswers screen and clear WhatIsTheIndividualsUniqueTaxReferencePagee if page answer changes from YesIKnow to YesButDontKnow in check mode" in {

      val previousAnswer = IsTheIndividualRegisteredForSelfAssessment.YesIKnow
      val newAnswer      = IsTheIndividualRegisteredForSelfAssessment.YesButDontKnow

      val urlToTest        = routes.IsTheIndividualRegisteredForSelfAssessmentController.onPageLoad(CheckMode).url
      val destinationRoute = routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(
        previousAnswer,
        newAnswer,
        IsTheIndividualRegisteredForSelfAssessmentPage,
        urlToTest,
        destinationRoute,
        List(WhatIsTheIndividualsUniqueTaxReferencePage)
      )
    }

    "must redirect to CheckYourAnswers screen if page answer is YesIKnow and doesn't change" in {

      val previousAnswer = IsTheIndividualRegisteredForSelfAssessment.YesIKnow
      val newAnswer      = IsTheIndividualRegisteredForSelfAssessment.YesIKnow

      val urlToTest        = routes.IsTheIndividualRegisteredForSelfAssessmentController.onPageLoad(CheckMode).url
      val destinationRoute = routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(
        previousAnswer,
        newAnswer,
        IsTheIndividualRegisteredForSelfAssessmentPage,
        urlToTest,
        destinationRoute,
        Nil
      )
    }

    "must redirect to CheckYourAnswers screen if page answer is No and doesn't change" in {

      val previousAnswer = IsTheIndividualRegisteredForSelfAssessment.No
      val newAnswer      = IsTheIndividualRegisteredForSelfAssessment.No

      val urlToTest        = routes.IsTheIndividualRegisteredForSelfAssessmentController.onPageLoad(CheckMode).url
      val destinationRoute = routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(
        previousAnswer,
        newAnswer,
        IsTheIndividualRegisteredForSelfAssessmentPage,
        urlToTest,
        destinationRoute,
        Nil
      )
    }
  }
}
