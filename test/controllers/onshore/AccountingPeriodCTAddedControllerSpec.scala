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
import forms.AccountingPeriodCTAddedFormProvider
import models.{CorporationTaxLiability, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.CorporationTaxLiabilityPage
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.onshore.CorporationTaxLiabilityModel
import views.html.onshore.AccountingPeriodCTAddedView

import java.time.{LocalDate, ZoneOffset}
import scala.concurrent.Future

class AccountingPeriodCTAddedControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new AccountingPeriodCTAddedFormProvider()
  val form = formProvider()

  lazy val accountingPeriodCTAddedRoute = routes.AccountingPeriodCTAddedController.onPageLoad(NormalMode).url

  val answer = Seq(CorporationTaxLiability(
    periodEnd = LocalDate.now(ZoneOffset.UTC),
    howMuchIncome = BigInt(100),
    howMuchUnpaid = BigInt(100),
    howMuchInterest = BigInt(100),
    penaltyRate = 5,
    penaltyRateReason = "Reason"
  ))

  val userAnswers = UserAnswers(userAnswersId, "session-123").set(CorporationTaxLiabilityPage, answer).success.value


  "AccountingPeriodCTAdded Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, accountingPeriodCTAddedRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[AccountingPeriodCTAddedView]

      val periodEndDates = CorporationTaxLiabilityModel.row(answer, NormalMode)(messages)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, periodEndDates, NormalMode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, accountingPeriodCTAddedRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(applicationWithFakeOnshoreNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, accountingPeriodCTAddedRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[AccountingPeriodCTAddedView]

      val periodEndDates = Seq()

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, periodEndDates, NormalMode)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, accountingPeriodCTAddedRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to Index for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, accountingPeriodCTAddedRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to the corporation’s tax liabilities page if remove method is called and there are no more details" in {
      val removeDLRoute = routes.AccountingPeriodCTAddedController.remove(0, NormalMode).url

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, removeDLRoute)

      val result = route(applicationWithFakeOnshoreNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.CorporationTaxLiabilityController.onPageLoad(0, NormalMode).url
    }

     "must redirect to the same page if remove method is called and there are still details" in {
       val removeDLRoute = routes.AccountingPeriodCTAddedController.remove(0, NormalMode).url

       when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)

       val corporationTaxLiability: CorporationTaxLiability = CorporationTaxLiability(
         periodEnd = LocalDate.now(),
         howMuchIncome = BigInt(0),
         howMuchUnpaid = BigInt(0),
         howMuchInterest = BigInt(0),
         penaltyRate = 0,
         penaltyRateReason = "Some reason"
       )

       val corporationTaxLiability2: CorporationTaxLiability = CorporationTaxLiability(
         periodEnd = LocalDate.now().minusDays(1),
         howMuchIncome = BigInt(0),
         howMuchUnpaid = BigInt(0),
         howMuchInterest = BigInt(0),
         penaltyRate = 0,
         penaltyRateReason = "Some reason"
       )

       val userAnswers = UserAnswers("id", "session-123").set(CorporationTaxLiabilityPage, Seq(corporationTaxLiability, corporationTaxLiability2)).success.value

       setupMockSessionResponse(Some(userAnswers))

       val request = FakeRequest(GET, removeDLRoute)

       val result = route(applicationWithFakeOnshoreNavigator(onwardRoute), request).value

       status(result) mustEqual SEE_OTHER
       redirectLocation(result).value mustEqual routes.AccountingPeriodCTAddedController.onPageLoad(NormalMode).url
     }
  }
}
