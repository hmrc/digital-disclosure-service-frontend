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
import config.{Countries, Country}
import forms.CountryOfYourOffshoreLiabilityFormProvider
import models.{NormalMode, UserAnswers}
import navigation.{FakeOffshoreNavigator, OffshoreNavigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.CountryOfYourOffshoreLiabilityPage
import play.api.Environment
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.{FakeRequest, Injecting}
import play.api.test.Helpers._
import services.SessionService
import views.html.offshore.CountryOfYourOffshoreLiabilityView

import scala.concurrent.Future

class CountryOfYourOffshoreLiabilityControllerSpec extends SpecBase with Injecting with MockitoSugar {

  val index =  Some(0)
  def onwardRoute = Call("GET", "/foo")

  val countrySet = Set(Country("AFG", "Afghanistan"))
  val userAnswers = UserAnswers(userAnswersId).set(CountryOfYourOffshoreLiabilityPage, countrySet).success.value
  val app = applicationBuilder(userAnswers = Some(userAnswers)).build()

  lazy val countryOfYourOffshoreLiabilityRoute = offshore.routes.CountryOfYourOffshoreLiabilityController.onPageLoad(index, NormalMode).url

  "CountryOfYourOffshoreLiability Controller" - {

    "must return OK and the correct view for a GET" in {
      lazy val env: Environment = inject[Environment]
      val countries = new Countries(env)

      val formProvider = new CountryOfYourOffshoreLiabilityFormProvider(countries)
      val form = formProvider()

      running(app) {
        val request = FakeRequest(GET, countryOfYourOffshoreLiabilityRoute)

        val result = route(app, request).value

        val view = app.injector.instanceOf[CountryOfYourOffshoreLiabilityView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(index, form, NormalMode)(request, messages(app)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      lazy val env: Environment = inject[Environment]
      val countries = new Countries(env)

      val formProvider = new CountryOfYourOffshoreLiabilityFormProvider(countries)
      val form = formProvider()

      val app = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(app) {
        val request = FakeRequest(GET, countryOfYourOffshoreLiabilityRoute)

        val view = app.injector.instanceOf[CountryOfYourOffshoreLiabilityView]

        val result = route(app, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(index, form.fill(countrySet), NormalMode)(request, messages(app)).toString
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
          FakeRequest(POST, countryOfYourOffshoreLiabilityRoute)
            .withFormUrlEncodedBody(("country", "AFG"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      lazy val env: Environment = inject[Environment]
      val countries = new Countries(env)

      val formProvider = new CountryOfYourOffshoreLiabilityFormProvider(countries)
      val form = formProvider()
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, countryOfYourOffshoreLiabilityRoute)
            .withFormUrlEncodedBody(("country", ""))

        val boundForm = form.bind(Map("country" -> ""))

        val view = application.injector.instanceOf[CountryOfYourOffshoreLiabilityView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(index, boundForm, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to Index for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, countryOfYourOffshoreLiabilityRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

    "must redirect to Index for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, countryOfYourOffshoreLiabilityRoute)
            .withFormUrlEncodedBody(("country", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }
  }
}
