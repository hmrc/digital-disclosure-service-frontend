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
import forms.TaxBeforeFiveYearsOnshoreFormProvider
import models.{Behaviour, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.TaxBeforeFiveYearsOnshorePage
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.OnshoreWhichYearsService
import uk.gov.hmrc.time.CurrentTaxYear
import views.html.onshore.TaxBeforeFiveYearsOnshoreView

import java.time.LocalDate
import scala.concurrent.Future

class TaxBeforeFiveYearsOnshoreControllerSpec extends SpecBase with MockitoSugar with CurrentTaxYear {

  def onwardRoute = Call("GET", "/foo")

  def now = () => LocalDate.now()

  val formProvider = new TaxBeforeFiveYearsOnshoreFormProvider()
  def form(year: String) = {
    formProvider(year)
  }

  lazy val taxBeforeFiveYearsOnshoreRoute = routes.TaxBeforeFiveYearsOnshoreController.onPageLoad(NormalMode).url

  val userAnswers = UserAnswers(
    userAnswersId,
    Json.obj(
      TaxBeforeFiveYearsOnshorePage.toString -> Json.obj(
        "taxBeforeFiveYearsOnshore" -> "value 1"
      )
    ).toString
  )

  "TaxBeforeFiveYearsOnshore Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, taxBeforeFiveYearsOnshoreRoute)

      val view = application.injector.instanceOf[TaxBeforeFiveYearsOnshoreView]

      val result = route(application, request).value

      val service = application.injector.instanceOf[OnshoreWhichYearsService]
      val year = service.getEarliestYearByBehaviour(Behaviour.Careless).toString

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form(year), NormalMode, year)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, taxBeforeFiveYearsOnshoreRoute)

      val view = application.injector.instanceOf[TaxBeforeFiveYearsOnshoreView]

      val result = route(application, request).value

      val service = application.injector.instanceOf[OnshoreWhichYearsService]
      val year = service.getEarliestYearByBehaviour(Behaviour.Careless).toString

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form(year).fill(""), NormalMode, year)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, taxBeforeFiveYearsOnshoreRoute)
          .withFormUrlEncodedBody(("value", "value 1"))

      val result = route(applicationWithFakeOnshoreNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, taxBeforeFiveYearsOnshoreRoute)
          .withFormUrlEncodedBody(("value", ""))

      val service = application.injector.instanceOf[OnshoreWhichYearsService]
      val year = service.getEarliestYearByBehaviour(Behaviour.Careless).toString

      val boundForm = form(year).bind(Map("value" -> ""))

      val view = application.injector.instanceOf[TaxBeforeFiveYearsOnshoreView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, year)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, taxBeforeFiveYearsOnshoreRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to Index for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, taxBeforeFiveYearsOnshoreRoute)
          .withFormUrlEncodedBody(("value", "value 1"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }
  }
}
