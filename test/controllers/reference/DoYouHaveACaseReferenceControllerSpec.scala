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

package controllers.reference

import base.SpecBase
import forms.DoYouHaveACaseReferenceFormProvider
import models.{NormalMode, UserAnswers}
import navigation.{FakeReferenceNavigator, ReferenceNavigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.DoYouHaveACaseReferencePage
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.reference.DoYouHaveACaseReferenceView

import scala.concurrent.Future

class DoYouHaveACaseReferenceControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute: Call = Call("GET", "/foo")

  val formProvider        = new DoYouHaveACaseReferenceFormProvider()
  val form: Form[Boolean] = formProvider()

  lazy val doYouHaveACaseReferenceRoute: String = routes.DoYouHaveACaseReferenceController.onPageLoad(NormalMode).url
  val view: DoYouHaveACaseReferenceView         = application.injector.instanceOf[DoYouHaveACaseReferenceView]

  "DoYouHaveACaseReference Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, doYouHaveACaseReferenceRoute)

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId, "session-123").set(DoYouHaveACaseReferencePage, true).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, doYouHaveACaseReferenceRoute)

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(true), NormalMode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val applicationWithFakeReferenceNavigator = applicationBuilder
        .overrides(
          bind[ReferenceNavigator].toInstance(new FakeReferenceNavigator(onwardRoute))
        )
        .build()

      val request =
        FakeRequest(POST, doYouHaveACaseReferenceRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(applicationWithFakeReferenceNavigator, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, doYouHaveACaseReferenceRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, doYouHaveACaseReferenceRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to Index for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, doYouHaveACaseReferenceRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }
  }
}
