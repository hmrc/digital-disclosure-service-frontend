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
import forms.DirectorLoanAccountLiabilitiesFormProvider
import models.{DirectorLoanAccountLiabilities, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.DirectorLoanAccountLiabilitiesPage
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.onshore.DirectorLoanAccountLiabilitiesView

import java.time.{LocalDate, ZoneOffset}
import scala.concurrent.Future

class DirectorLoanAccountLiabilitiesControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new DirectorLoanAccountLiabilitiesFormProvider()
  val form = formProvider()

  val index = 0

  lazy val directorLoanAccountLiabilitiesRoute = routes.DirectorLoanAccountLiabilitiesController.onPageLoad(index, NormalMode).url

  val answer = DirectorLoanAccountLiabilities(
    name = "a Name",
    periodEnd = LocalDate.now(ZoneOffset.UTC).minusDays(1),
    overdrawn = BigInt(1000),
    unpaidTax = BigInt(1000),
    interest = BigInt(1000),
    penaltyRate = 20,
    penaltyRateReason = "Reason"
  )

  val userAnswers = UserAnswers(userAnswersId, "session-123").set(DirectorLoanAccountLiabilitiesPage, Seq(answer)).success.value

  "DirectorLoanAccountLiabilities Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, directorLoanAccountLiabilitiesRoute)

      val view = application.injector.instanceOf[DirectorLoanAccountLiabilitiesView]

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode, index)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, directorLoanAccountLiabilitiesRoute)

      val view = application.injector.instanceOf[DirectorLoanAccountLiabilitiesView]

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(answer), NormalMode, index)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, directorLoanAccountLiabilitiesRoute)
          .withFormUrlEncodedBody(
            ("name", "name"),
            ("periodEnd.day", "1"),
            ("periodEnd.month", "12"),
            ("periodEnd.year", "2012"),
            ("overdrawn", "1000"),
            ("unpaidTax", "1000"),
            ("interest", "1000"),
            ("penaltyRate", "20"),
            ("penaltyRateReason", "reason")
          )

      val result = route(applicationWithFakeOnshoreNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, directorLoanAccountLiabilitiesRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[DirectorLoanAccountLiabilitiesView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, index)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, directorLoanAccountLiabilitiesRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to Index for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, directorLoanAccountLiabilitiesRoute)
          .withFormUrlEncodedBody(("field1", "value 1"), ("field2", "value 2"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }
  }
}
