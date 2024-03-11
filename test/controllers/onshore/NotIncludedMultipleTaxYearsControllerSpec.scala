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
import forms.NotIncludedMultipleTaxYearsFormProvider
import models.NormalMode
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.NotIncludedMultipleTaxYearsPage
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.onshore.NotIncludedMultipleTaxYearsView

import scala.concurrent.Future

class NotIncludedMultipleTaxYearsControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new NotIncludedMultipleTaxYearsFormProvider()
  val form = formProvider()

  val selectedYears = Nil
  val notSelectedYears = Nil
  lazy val youHaveNotSelectedCertainTaxYearRoute = routes.NotIncludedMultipleTaxYearsController.onPageLoad(NormalMode).url

  "NotIncludedMultipleTaxYears Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, youHaveNotSelectedCertainTaxYearRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[NotIncludedMultipleTaxYearsView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode, selectedYears, notSelectedYears)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(NotIncludedMultipleTaxYearsPage, "answer").success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, youHaveNotSelectedCertainTaxYearRoute)

      val view = application.injector.instanceOf[NotIncludedMultipleTaxYearsView]

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill("answer"), NormalMode, selectedYears, notSelectedYears)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, youHaveNotSelectedCertainTaxYearRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(applicationWithFakeOnshoreNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, youHaveNotSelectedCertainTaxYearRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[NotIncludedMultipleTaxYearsView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, selectedYears, notSelectedYears)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, youHaveNotSelectedCertainTaxYearRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to Index for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, youHaveNotSelectedCertainTaxYearRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }
  }
}
