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
import forms.ContractualDisclosureFacilityFormProvider
import models._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{ContractualDisclosureFacilityPage, RelatesToPage}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.offshore.ContractualDisclosureFacilityView

import scala.concurrent.Future

class ContractualDisclosureFacilityControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val contractualDisclosureFacilityRoute =
    routes.ContractualDisclosureFacilityController.onPageLoad(NormalMode).url

  val formProvider = new ContractualDisclosureFacilityFormProvider()
  val form         = formProvider()

  val entity = RelatesTo.ACompany

  "ContractualDisclosureFacility Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = UserAnswers(userAnswersId, "session-123").set(RelatesToPage, entity).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, contractualDisclosureFacilityRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[ContractualDisclosureFacilityView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode, entity)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val relatesToPage = UserAnswers(userAnswersId, "session-123").set(RelatesToPage, entity).success.value
      val userAnswers   = relatesToPage.set(ContractualDisclosureFacilityPage, true).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, contractualDisclosureFacilityRoute)

      val view = application.injector.instanceOf[ContractualDisclosureFacilityView]

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(true), NormalMode, entity)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, contractualDisclosureFacilityRoute)
          .withFormUrlEncodedBody(("value", true.toString))

      val result = route(applicationWithFakeOffshoreNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val relatesToPage = UserAnswers(userAnswersId, "session-123").set(RelatesToPage, entity).success.value

      setupMockSessionResponse(Some(relatesToPage))

      val request =
        FakeRequest(POST, contractualDisclosureFacilityRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[ContractualDisclosureFacilityView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, entity)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, contractualDisclosureFacilityRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, contractualDisclosureFacilityRoute)
          .withFormUrlEncodedBody(("value", true.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }
  }
}
