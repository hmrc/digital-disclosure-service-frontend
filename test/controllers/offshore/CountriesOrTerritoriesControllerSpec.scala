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
import config.Country
import forms.CountriesOrTerritoriesFormProvider
import models.{NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.CountryOfYourOffshoreLiabilityPage
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.offshore.CountriesOrTerritoriesView

import scala.concurrent.Future

class CountriesOrTerritoriesControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new CountriesOrTerritoriesFormProvider()
  val form = formProvider()

  val countries = Seq()
  val countryCode = "AAA"

  lazy val countriesOrTerritoriesRoute = routes.CountriesOrTerritoriesController.onPageLoad(NormalMode).url

  "CountriesOrTerritories Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, countriesOrTerritoriesRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CountriesOrTerritoriesView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, countries, NormalMode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, countriesOrTerritoriesRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(applicationWithFakeOffshoreNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, countriesOrTerritoriesRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[CountriesOrTerritoriesView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, countries, NormalMode)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, countriesOrTerritoriesRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to Index for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, countriesOrTerritoriesRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }
  }

  "must redirect to the country of your offshore liabilities page if remove method is called and there are no more Countries" in {

    val removeCountryRoute = routes.CountriesOrTerritoriesController.remove(countryCode, NormalMode).url

    when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
    setupMockSessionResponse(Some(emptyUserAnswers))

    val request = FakeRequest(GET, removeCountryRoute)

    val result = route(applicationWithFakeOffshoreNavigator(onwardRoute), request).value

    status(result) mustEqual SEE_OTHER
    redirectLocation(result).value mustEqual routes.CountryOfYourOffshoreLiabilityController.onPageLoad(None, NormalMode).url
  }

  "must redirect to the country of your offshore liabilities page if remove method is called and there are Countries in the UserAnswers" in {

    when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)

    val country1 = Country(countryCode, "Country 1")
    val country2 = Country("BBB", "Country 2")
    val userAnswers = UserAnswers("id", "session-123")
      .setByKey(CountryOfYourOffshoreLiabilityPage, country1.alpha3 ,country1).success.value
      .setByKey(CountryOfYourOffshoreLiabilityPage, country2.alpha3 ,country2).success.value

    lazy val removeCountryRoute = routes.CountriesOrTerritoriesController.remove(countryCode, NormalMode).url

    setupMockSessionResponse(Some(userAnswers))

    val request = FakeRequest(GET, removeCountryRoute)

    val result = route(applicationWithFakeOffshoreNavigator(onwardRoute), request).value

    status(result) mustEqual SEE_OTHER
    redirectLocation(result).value mustEqual routes.CountriesOrTerritoriesController.onPageLoad(NormalMode).url
  }
}
