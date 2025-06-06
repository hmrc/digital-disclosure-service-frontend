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
import forms.AreYouRepresentingAnOrganisationFormProvider
import models.{CheckMode, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.{any, refEq}
import org.mockito.Mockito.{times, verify, when}
import pages.{AreYouRepresentingAnOrganisationPage, WhatIsTheNameOfTheOrganisationYouRepresentPage}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.notification.AreYouRepresentingAnOrganisationView

import scala.concurrent.Future

class AreYouRepresentingAnOrganisationControllerSpec extends ControllerSpecBase {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new AreYouRepresentingAnOrganisationFormProvider()
  val form         = formProvider()

  lazy val areYouRepresentingAnOrganisationRoute =
    routes.AreYouRepresentingAnOrganisationController.onPageLoad(NormalMode).url

  "AreYouRepresentingAnOrganisation Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, areYouRepresentingAnOrganisationRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[AreYouRepresentingAnOrganisationView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode, false)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers =
        UserAnswers(userAnswersId, "session-123").set(AreYouRepresentingAnOrganisationPage, true).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, areYouRepresentingAnOrganisationRoute)

      val view = application.injector.instanceOf[AreYouRepresentingAnOrganisationView]

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(true), NormalMode, false)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, areYouRepresentingAnOrganisationRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(applicationWithFakeNotificationNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, areYouRepresentingAnOrganisationRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[AreYouRepresentingAnOrganisationView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, false)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, areYouRepresentingAnOrganisationRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to Index for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, areYouRepresentingAnOrganisationRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to WhatIsTheNameOfTheOrganisationYouRepresent page (change mode) if page answer changes from No to Yes in check mode" in {
      val previousAnswer = false
      val newAnswer      = true

      val urlToTest        = routes.AreYouRepresentingAnOrganisationController.onPageLoad(CheckMode).url
      val destinationRoute = routes.WhatIsTheNameOfTheOrganisationYouRepresentController.onPageLoad(CheckMode).url

      testChangeAnswerRouting(
        previousAnswer,
        newAnswer,
        AreYouRepresentingAnOrganisationPage,
        urlToTest,
        destinationRoute,
        Nil
      )
    }

    "must redirect to WhatIsTheNameOfTheOrganisationYouRepresent page (change mode) if page answer changes from No Answer to Yes in check mode" in {
      val newAnswer = true

      val urlToTest        = routes.AreYouRepresentingAnOrganisationController.onPageLoad(CheckMode).url
      val destinationRoute = routes.WhatIsTheNameOfTheOrganisationYouRepresentController.onPageLoad(CheckMode).url

      val userAnswers = UserAnswers("id", "session-123")

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)

      val previousUa = userAnswers.remove(AreYouRepresentingAnOrganisationPage).success.value
      val expectedUa = userAnswers.set(AreYouRepresentingAnOrganisationPage, newAnswer).success.value

      setupMockSessionResponse(Some(previousUa))

      val request =
        FakeRequest(POST, urlToTest)
          .withFormUrlEncodedBody(("value", newAnswer.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual destinationRoute

      verify(mockSessionService, times(1)).set(refEq(expectedUa))(any())
    }

    "must redirect to CheckYourAnswers page (change mode) and clean WhatIsTheNameOfTheOrganisationYouRepresentPage if page answer changes from Yes to No in check mode" in {
      val previousAnswer = true
      val newAnswer      = false

      val urlToTest        = routes.AreYouRepresentingAnOrganisationController.onPageLoad(CheckMode).url
      val destinationRoute = routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(
        previousAnswer,
        newAnswer,
        AreYouRepresentingAnOrganisationPage,
        urlToTest,
        destinationRoute,
        List(WhatIsTheNameOfTheOrganisationYouRepresentPage)
      )
    }

    "must redirect to CheckYourAnswers page (change mode) if page is true and doesn't change" in {
      val previousAnswer = true
      val newAnswer      = true

      val urlToTest        = routes.AreYouRepresentingAnOrganisationController.onPageLoad(CheckMode).url
      val destinationRoute = routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(
        previousAnswer,
        newAnswer,
        AreYouRepresentingAnOrganisationPage,
        urlToTest,
        destinationRoute,
        Nil
      )
    }

    "must redirect to CheckYourAnswers page (change mode) if page is false and doesn't change" in {
      val previousAnswer = false
      val newAnswer      = false

      val urlToTest        = routes.AreYouRepresentingAnOrganisationController.onPageLoad(CheckMode).url
      val destinationRoute = routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(
        previousAnswer,
        newAnswer,
        AreYouRepresentingAnOrganisationPage,
        urlToTest,
        destinationRoute,
        Nil
      )
    }
  }
}
