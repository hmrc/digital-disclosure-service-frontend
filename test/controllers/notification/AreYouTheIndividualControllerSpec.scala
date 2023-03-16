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
import forms.AreYouTheIndividualFormProvider
import models._
import navigation.{FakeNotificationNavigator, NotificationNavigator}
import org.mockito.ArgumentMatchers.{refEq, any}
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages._
import pages.notification.SectionPages
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SessionService
import views.html.notification.AreYouTheIndividualView

import scala.concurrent.Future

class AreYouTheIndividualControllerSpec extends SpecBase with MockitoSugar with SectionPages {

  def onwardRoute = Call("GET", "/foo")

  lazy val areYouTheIndividualRoute = controllers.notification.routes.AreYouTheIndividualController.onPageLoad(NormalMode).url
  lazy val areYouTheIndividualRouteCheckMode = controllers.notification.routes.AreYouTheIndividualController.onPageLoad(CheckMode).url

  val formProvider = new AreYouTheIndividualFormProvider()
  val form = formProvider()

  "AreYouTheIndividual Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, areYouTheIndividualRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AreYouTheIndividualView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, false)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(AreYouTheIndividualPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, areYouTheIndividualRoute)

        val view = application.injector.instanceOf[AreYouTheIndividualView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(true), NormalMode, false)(request, messages(application)).toString
      }
    }

    "must redirect to offshore liabilities screen and clear the About-You and About-Individual section if AreYouTheIndividual answer changes from No to Yes in check mode" in {

      val previousAnswer = false
      val newAnswer = true

      val userAnswers = UserAnswers(userAnswersId).set(AreYouTheIndividualPage, previousAnswer).success.value  

      val mockSessionService = mock[SessionService]
      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)

      val expectedUserAnswers = userAnswers.remove(aboutYouPages:::aboutIndividualPages:::areYouTheOrganisationPages).get
        .set(AreYouTheIndividualPage, newAnswer).get

      val application = applicationBuilderWithSessionService(userAnswers = Some(userAnswers), mockSessionService)
        .build()

      val offshoreLiabilitiesRoute = controllers.notification.routes.OffshoreLiabilitiesController.onPageLoad(NormalMode).url

      running(application) {
        val request =
          FakeRequest(POST, areYouTheIndividualRouteCheckMode)
            .withFormUrlEncodedBody(("value", newAnswer.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual offshoreLiabilitiesRoute

        verify(mockSessionService, times(1)).set(refEq(expectedUserAnswers))(any())
      }
    }

    "must redirect to AreYouRepresentingAnOrganisation page and clear the About-You section if AreYouTheIndividual answer changes from Yes to No in check mode" in {

      val previousAnswer = true
      val newAnswer = false

      val userAnswers = UserAnswers(userAnswersId).set(AreYouTheIndividualPage, previousAnswer).success.value

      val mockSessionService = mock[SessionService]
      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)

      val expectedUserAnswers = userAnswers.remove(aboutYouPages).get
        .set(AreYouTheIndividualPage, newAnswer).get

      val application = applicationBuilderWithSessionService(userAnswers = Some(userAnswers), mockSessionService)
        .build()

      val areYouRepresentingAnOrganisationRoute = controllers.notification.routes.AreYouRepresentingAnOrganisationController.onPageLoad(NormalMode).url

      running(application) {
        val request =
          FakeRequest(POST, areYouTheIndividualRouteCheckMode)
            .withFormUrlEncodedBody(("value", newAnswer.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual areYouRepresentingAnOrganisationRoute

        verify(mockSessionService, times(1)).set(refEq(expectedUserAnswers))(any())
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionService = mock[SessionService]

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilderWithSessionService(userAnswers = Some(emptyUserAnswers), mockSessionService)
          .overrides(
            bind[NotificationNavigator].toInstance(new FakeNotificationNavigator(onwardRoute))
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, areYouTheIndividualRoute)
            .withFormUrlEncodedBody(("value", true.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, areYouTheIndividualRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[AreYouTheIndividualView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, false)(request, messages(application)).toString
      }
    }

    "must redirect to Index for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, areYouTheIndividualRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, areYouTheIndividualRoute)
            .withFormUrlEncodedBody(("value", true.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }
  }
}
