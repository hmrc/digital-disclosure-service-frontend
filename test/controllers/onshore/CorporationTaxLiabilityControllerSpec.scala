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
import forms.CorporationTaxLiabilityFormProvider
import models.{CorporationTaxLiability, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.CorporationTaxLiabilityPage
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.onshore.CorporationTaxLiabilityView

import java.time.{LocalDate, ZoneOffset}
import scala.concurrent.Future

class CorporationTaxLiabilityControllerSpec extends SpecBase with MockitoSugar {

  val formProvider = new CorporationTaxLiabilityFormProvider()
  private def form = formProvider()

  def onwardRoute = Call("GET", "/foo")

  val validAnswer = LocalDate.now(ZoneOffset.UTC)

  lazy val corporationTaxLiabilityRoute = routes.CorporationTaxLiabilityController.onPageLoad(0, NormalMode).url

  override val emptyUserAnswers = UserAnswers(userAnswersId, "session-123")

  val answer = CorporationTaxLiability(
    periodEnd = LocalDate.now(ZoneOffset.UTC),
    howMuchIncome = BigInt(100),
    howMuchUnpaid = BigInt(100),
    howMuchInterest = BigInt(100),
    penaltyRate = 5,
    penaltyRateReason = "Reason"
  )

  def getRequest(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, corporationTaxLiabilityRoute)

  "CorporationTaxLiability Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val result = route(application, getRequest()).value

      val view = application.injector.instanceOf[CorporationTaxLiabilityView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode, 0)(getRequest(), messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers =
        UserAnswers(userAnswersId, "session-123").set(CorporationTaxLiabilityPage, Seq(answer)).success.value

      setupMockSessionResponse(Some(userAnswers))

      val view = application.injector.instanceOf[CorporationTaxLiabilityView]

      val result = route(application, getRequest()).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(answer), NormalMode, 0)(getRequest(), messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, corporationTaxLiabilityRoute)
          .withFormUrlEncodedBody(
            ("periodEnd.day", "1"),
            ("periodEnd.month", "12"),
            ("periodEnd.year", "2012"),
            ("howMuchIncome", "100"),
            ("howMuchUnpaid", "100"),
            ("howMuchInterest", "100"),
            ("penaltyRate", "5"),
            ("penaltyRateReason", "Reason")
          )

      val result = route(applicationWithFakeOnshoreNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, corporationTaxLiabilityRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[CorporationTaxLiabilityView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, 0)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val result = route(application, getRequest()).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to Index for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, corporationTaxLiabilityRoute)
          .withFormUrlEncodedBody(("howMuchIncome", "100"), ("field2", "value 2"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

  }
}
