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

package controllers

import base.SpecBase
import forms.TaxBeforeFiveYearsFormProvider
import models.{Behaviour, NormalMode, UserAnswers}
import navigation.{FakeOffshoreNavigator, OffshoreNavigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.TaxBeforeFiveYearsPage
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{OffshoreWhichYearsService, SessionService}
import views.html.offshore.TaxBeforeFiveYearsView
import uk.gov.hmrc.time.CurrentTaxYear
import java.time.LocalDate

import scala.concurrent.Future

class TaxBeforeFiveYearsControllerSpec extends SpecBase with MockitoSugar with CurrentTaxYear {

  def onwardRoute = Call("GET", "/foo")

  def now = () => LocalDate.now()

  val formProvider = new TaxBeforeFiveYearsFormProvider()
  def form(year: String) = {
    formProvider(year)
  }

  lazy val taxBeforeFiveYearsRoute = offshore.routes.TaxBeforeFiveYearsController.onPageLoad(NormalMode).url

  val userAnswers = UserAnswers(
    userAnswersId,
    Json.obj(
      TaxBeforeFiveYearsPage.toString -> Json.obj(
        "taxBeforeFiveYears" -> "value 1"
      )
    ).toString
  )

  "TaxBeforeFiveYears Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, taxBeforeFiveYearsRoute)

        val view = application.injector.instanceOf[TaxBeforeFiveYearsView]

        val result = route(application, request).value

        val service = application.injector.instanceOf[OffshoreWhichYearsService]
        val year = service.getEarliestYearByBehaviour(Behaviour.ReasonableExcuse).toString
        
        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form(year), NormalMode, year)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, taxBeforeFiveYearsRoute)

        val view = application.injector.instanceOf[TaxBeforeFiveYearsView]

        val result = route(application, request).value

        val service = application.injector.instanceOf[OffshoreWhichYearsService]
        val year = service.getEarliestYearByBehaviour(Behaviour.ReasonableExcuse).toString

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form(year).fill(""), NormalMode, year)(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionService = mock[SessionService]

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilderWithSessionService(userAnswers = Some(emptyUserAnswers), mockSessionService)
          .overrides(
            bind[OffshoreNavigator].toInstance(new FakeOffshoreNavigator(onwardRoute))
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, taxBeforeFiveYearsRoute)
            .withFormUrlEncodedBody(("value", "value 1"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, taxBeforeFiveYearsRoute)
            .withFormUrlEncodedBody(("value", ""))

        val service = application.injector.instanceOf[OffshoreWhichYearsService]
        val year = service.getEarliestYearByBehaviour(Behaviour.ReasonableExcuse).toString

        val boundForm = form(year).bind(Map("value" -> ""))

        val view = application.injector.instanceOf[TaxBeforeFiveYearsView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, year)(request, messages(application)).toString
      }
    }

    "must redirect to Index for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, taxBeforeFiveYearsRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

    "must redirect to Index for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, taxBeforeFiveYearsRoute)
            .withFormUrlEncodedBody(("value", "value 1"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }
  }
}
