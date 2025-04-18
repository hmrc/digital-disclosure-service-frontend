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
import forms.AccountingPeriodDLAddedFormProvider
import models.{DirectorLoanAccountLiabilities, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.DirectorLoanAccountLiabilitiesPage
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.onshore.DirectorLoanAccountLiabilityModel
import views.html.onshore.AccountingPeriodDLAddedView

import java.time.{LocalDate, ZoneOffset}
import scala.concurrent.Future

class AccountingPeriodDLAddedControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new AccountingPeriodDLAddedFormProvider()
  val form         = formProvider()

  lazy val accountingPeriodDLAddedRoute = routes.AccountingPeriodDLAddedController.onPageLoad(NormalMode).url

  val answer = Seq(
    DirectorLoanAccountLiabilities(
      name = "a Name",
      periodEnd = LocalDate.now(ZoneOffset.UTC).minusDays(1),
      overdrawn = BigInt(1000),
      unpaidTax = BigInt(1000),
      interest = BigInt(1000),
      penaltyRate = 20,
      penaltyRateReason = "Reason"
    )
  )

  val userAnswers =
    UserAnswers(userAnswersId, "session-123").set(DirectorLoanAccountLiabilitiesPage, answer).success.value

  "AccountingPeriodDLAdded Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, accountingPeriodDLAddedRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[AccountingPeriodDLAddedView]

      val periodEndDates = DirectorLoanAccountLiabilityModel.row(answer, NormalMode)(messages)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, periodEndDates, NormalMode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, accountingPeriodDLAddedRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(applicationWithFakeOnshoreNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, accountingPeriodDLAddedRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[AccountingPeriodDLAddedView]

      val result = route(application, request).value

      val periodEndDates = Seq.empty

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, periodEndDates, NormalMode)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, accountingPeriodDLAddedRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to Index for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, accountingPeriodDLAddedRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to the director’s loan account liability page if remove method is called and there are no more details" in {
      val removeDLRoute = routes.AccountingPeriodDLAddedController.remove(0, NormalMode).url

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, removeDLRoute)

      val result = route(applicationWithFakeOnshoreNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.DirectorLoanAccountLiabilitiesController
        .onPageLoad(0, NormalMode)
        .url
    }

    "must redirect to the same page if remove method is called and there are still details" in {
      val removeDLRoute = routes.AccountingPeriodDLAddedController.remove(0, NormalMode).url

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)

      val directorLoanAccountLiabilities: DirectorLoanAccountLiabilities = DirectorLoanAccountLiabilities(
        name = "Some Name 1",
        periodEnd = LocalDate.now(),
        overdrawn = BigInt(0),
        unpaidTax = BigInt(0),
        interest = BigInt(0),
        penaltyRate = 0,
        penaltyRateReason = "Some reason"
      )

      val directorLoanAccountLiabilities2: DirectorLoanAccountLiabilities = DirectorLoanAccountLiabilities(
        name = "Some Name 2",
        periodEnd = LocalDate.now(),
        overdrawn = BigInt(0),
        unpaidTax = BigInt(0),
        interest = BigInt(0),
        penaltyRate = 0,
        penaltyRateReason = "Some reason"
      )

      val userAnswers = UserAnswers("id", "session-123")
        .set(DirectorLoanAccountLiabilitiesPage, Seq(directorLoanAccountLiabilities, directorLoanAccountLiabilities2))
        .success
        .value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, removeDLRoute)

      val result = route(applicationWithFakeOnshoreNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.AccountingPeriodDLAddedController.onPageLoad(NormalMode).url
    }
  }
}
