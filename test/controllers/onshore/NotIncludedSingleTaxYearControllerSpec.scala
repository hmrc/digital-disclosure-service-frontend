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
import forms.NotIncludedSingleTaxYearFormProvider
import models.{NormalMode, UserAnswers, OnshoreYearStarting, OnshoreYears}
import navigation.{FakeOnshoreNavigator, OnshoreNavigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{NotIncludedSingleTaxYearPage, WhichOnshoreYearsPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SessionService
import views.html.onshore.NotIncludedSingleTaxYearView

import scala.concurrent.Future

class NotIncludedSingleTaxYearControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new NotIncludedSingleTaxYearFormProvider()
  val missingYear = "2020"
  val firstYear = "2019"
  val lastYear = "2021"
  val form = formProvider(missingYear)

  lazy val youHaveNotIncludedTheTaxYearRoute = onshore.routes.NotIncludedSingleTaxYearController.onPageLoad(NormalMode).url
  lazy val youHaveNotSelectedCertainTaxYearRoute = onshore.routes.NotIncludedMultipleTaxYearsController.onPageLoad(NormalMode).url
  lazy val whichYearsRoute = onshore.routes.WhichOnshoreYearsController.onPageLoad(NormalMode).url

  "NotIncludedSingleTaxYear Controller" - {

    "must return OK and the correct view for a GET" in {
    
      val whichYears: Set[OnshoreYears] = Set(OnshoreYearStarting(2020), OnshoreYearStarting(2018))
      val userAnswers = UserAnswers(userAnswersId).set(WhichOnshoreYearsPage, whichYears).success.value
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, youHaveNotIncludedTheTaxYearRoute)
        
        val result = route(application, request).value
        
        val view = application.injector.instanceOf[NotIncludedSingleTaxYearView]
        
        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, missingYear, firstYear, lastYear)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val whichYears: Set[OnshoreYears] = Set(OnshoreYearStarting(2020), OnshoreYearStarting(2018))
      val userAnswers = (for {
        ua <- UserAnswers(userAnswersId).set(WhichOnshoreYearsPage, whichYears)
        finalUa <- ua.set(NotIncludedSingleTaxYearPage, "answer")
      } yield finalUa).success.value
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, youHaveNotIncludedTheTaxYearRoute)

        val view = application.injector.instanceOf[NotIncludedSingleTaxYearView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill("answer"), NormalMode, missingYear, firstYear, lastYear)(request, messages(application)).toString
      }
    }

    "must redirect to the which onshore years page when it isnt set" in {

      val mockSessionService = mock[SessionService]

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilderWithSessionService(userAnswers = Some(emptyUserAnswers), mockSessionService)
          .overrides(
            bind[OnshoreNavigator].toInstance(new FakeOnshoreNavigator(onwardRoute))
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, youHaveNotIncludedTheTaxYearRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual whichYearsRoute
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val whichYears: Set[OnshoreYears] = Set(OnshoreYearStarting(2020), OnshoreYearStarting(2018))
      val userAnswers = UserAnswers(userAnswersId).set(WhichOnshoreYearsPage, whichYears).success.value

      val mockSessionService = mock[SessionService]

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilderWithSessionService(userAnswers = Some(userAnswers), mockSessionService)
          .overrides(
            bind[OnshoreNavigator].toInstance(new FakeOnshoreNavigator(onwardRoute))
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, youHaveNotIncludedTheTaxYearRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must redirect to the YouHaveNotSelectedCertainTaxYearPage when multiple years are missing" in {

      val whichYears: Set[OnshoreYears] = Set(OnshoreYearStarting(2020), OnshoreYearStarting(2018), OnshoreYearStarting(2016))
      val userAnswers = UserAnswers(userAnswersId).set(WhichOnshoreYearsPage, whichYears).success.value

      val mockSessionService = mock[SessionService]

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilderWithSessionService(userAnswers = Some(userAnswers), mockSessionService)
          .overrides(
            bind[OnshoreNavigator].toInstance(new FakeOnshoreNavigator(onwardRoute))
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, youHaveNotIncludedTheTaxYearRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual youHaveNotSelectedCertainTaxYearRoute
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val whichYears: Set[OnshoreYears] = Set(OnshoreYearStarting(2020), OnshoreYearStarting(2018))
      val userAnswers = UserAnswers(userAnswersId).set(WhichOnshoreYearsPage, whichYears).success.value
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, youHaveNotIncludedTheTaxYearRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[NotIncludedSingleTaxYearView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, missingYear, firstYear, lastYear)(request, messages(application)).toString
      }
    }

    "must redirect to Index for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, youHaveNotIncludedTheTaxYearRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

    "must redirect to Index for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, youHaveNotIncludedTheTaxYearRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }
  }
}
