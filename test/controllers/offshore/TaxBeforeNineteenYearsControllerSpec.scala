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
import forms.TaxBeforeNineteenYearsFormProvider
import models.{Behaviour, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.TaxBeforeNineteenYearsPage
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.OffshoreWhichYearsService
import uk.gov.hmrc.time.CurrentTaxYear
import views.html.offshore.TaxBeforeNineteenYearsView

import java.time.LocalDate
import scala.concurrent.Future

class TaxBeforeNineteenYearsControllerSpec extends SpecBase with MockitoSugar with CurrentTaxYear {

  def onwardRoute = Call("GET", "/foo")

  def now = () => LocalDate.now()

  val formProvider       = new TaxBeforeNineteenYearsFormProvider()
  def form(year: String) =
    formProvider(year)

  lazy val taxBeforeNineteenYearRoute = routes.TaxBeforeNineteenYearsController.onPageLoad(NormalMode).url

  "TaxBeforeNineteenYears Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, taxBeforeNineteenYearRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[TaxBeforeNineteenYearsView]

      val service = application.injector.instanceOf[OffshoreWhichYearsService]
      val year    = service.getEarliestYearByBehaviour(Behaviour.Deliberate).toString

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form(year), NormalMode, year)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers =
        UserAnswers(userAnswersId, "session-123").set(TaxBeforeNineteenYearsPage, "answer").success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, taxBeforeNineteenYearRoute)

      val view = application.injector.instanceOf[TaxBeforeNineteenYearsView]

      val service = application.injector.instanceOf[OffshoreWhichYearsService]
      val year    = service.getEarliestYearByBehaviour(Behaviour.Deliberate).toString

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form(year).fill("answer"), NormalMode, year)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, taxBeforeNineteenYearRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(applicationWithFakeOffshoreNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, taxBeforeNineteenYearRoute)
          .withFormUrlEncodedBody(("value", ""))

      val service = application.injector.instanceOf[OffshoreWhichYearsService]
      val year    = service.getEarliestYearByBehaviour(Behaviour.Deliberate).toString

      val boundForm = form(year).bind(Map("value" -> ""))

      val view = application.injector.instanceOf[TaxBeforeNineteenYearsView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, year)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, taxBeforeNineteenYearRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to Index for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, taxBeforeNineteenYearRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }
  }
}
