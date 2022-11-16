/*
 * Copyright 2022 HM Revenue & Customs
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

import base.ControllerSpecBase
import forms.AreYouRegisteredForVATFormProvider
import models.{NormalMode, AreYouRegisteredForVAT, UserAnswers}
import navigation.{FakeNotificationNavigator, NotificationNavigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.{AreYouRegisteredForVATPage, WhatIsYourVATRegistrationNumberPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SessionService
import views.html.notification.AreYouRegisteredForVATView
import models._

import scala.concurrent.Future

class AreYouRegisteredForVATControllerSpec extends ControllerSpecBase {

  def onwardRoute = Call("GET", "/foo")

  lazy val areYouRegisteredForVATRoute = notification.routes.AreYouRegisteredForVATController.onPageLoad(NormalMode).url

  val formProvider = new AreYouRegisteredForVATFormProvider()
  val form = formProvider()

  "AreYouRegisteredForVAT Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, areYouRegisteredForVATRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AreYouRegisteredForVATView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(AreYouRegisteredForVATPage, AreYouRegisteredForVAT.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, areYouRegisteredForVATRoute)

        val view = application.injector.instanceOf[AreYouRegisteredForVATView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(AreYouRegisteredForVAT.values.head), NormalMode)(request, messages(application)).toString
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
          FakeRequest(POST, areYouRegisteredForVATRoute)
            .withFormUrlEncodedBody(("value", AreYouRegisteredForVAT.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, areYouRegisteredForVATRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[AreYouRegisteredForVATView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to Index for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, areYouRegisteredForVATRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, areYouRegisteredForVATRoute)
            .withFormUrlEncodedBody(("value", AreYouRegisteredForVAT.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

    "must redirect to WhatIsYourVATRegistrationNumber page (change mode) if page answer changes from No to YesIKnow in check mode" in {

      val previousAnswer = AreYouRegisteredForVAT.No
      val newAnswer = AreYouRegisteredForVAT.YesIKnow

      val urlToTest = notification.routes.AreYouRegisteredForVATController.onPageLoad(CheckMode).url
      val destinationRoute = notification.routes.WhatIsYourVATRegistrationNumberController.onPageLoad(CheckMode).url

      testChangeAnswerRouting(previousAnswer, newAnswer, AreYouRegisteredForVATPage, urlToTest, destinationRoute, Nil)
    }

    "must redirect to WhatIsYourVATRegistrationNumber page (change mode) if page answer changes from YesButDontKnow to YesIKnow in check mode" in {

      val previousAnswer = AreYouRegisteredForVAT.YesButDontKnow
      val newAnswer = AreYouRegisteredForVAT.YesIKnow

      val urlToTest = notification.routes.AreYouRegisteredForVATController.onPageLoad(CheckMode).url
      val destinationRoute = notification.routes.WhatIsYourVATRegistrationNumberController.onPageLoad(CheckMode).url

      testChangeAnswerRouting(previousAnswer, newAnswer, AreYouRegisteredForVATPage, urlToTest, destinationRoute, Nil)
    }

    "must redirect to CheckYourAnswers screen and clear WhatIsYourVATRegistrationNumberPage if page answer changes from YesIKnow to No in check mode" in {

      val previousAnswer = AreYouRegisteredForVAT.YesIKnow
      val newAnswer = AreYouRegisteredForVAT.No

      val urlToTest = notification.routes.AreYouRegisteredForVATController.onPageLoad(CheckMode).url
      val destinationRoute = notification.routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, AreYouRegisteredForVATPage, urlToTest, destinationRoute, List(WhatIsYourVATRegistrationNumberPage))
    }

    "must redirect to CheckYourAnswers screen and clear WhatIsYourVATRegistrationNumberPage if page answer changes from YesIKnow to YesButDontKnow in check mode" in {

      val previousAnswer = AreYouRegisteredForVAT.YesIKnow
      val newAnswer = AreYouRegisteredForVAT.YesButDontKnow

      val urlToTest = notification.routes.AreYouRegisteredForVATController.onPageLoad(CheckMode).url
      val destinationRoute = notification.routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, AreYouRegisteredForVATPage, urlToTest, destinationRoute, List(WhatIsYourVATRegistrationNumberPage))
    }

    "must redirect to CheckYourAnswers screen if page answer is YesIKnow and doesn't change" in {

      val previousAnswer = AreYouRegisteredForVAT.YesIKnow
      val newAnswer = AreYouRegisteredForVAT.YesIKnow

      val urlToTest = notification.routes.AreYouRegisteredForVATController.onPageLoad(CheckMode).url
      val destinationRoute = notification.routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, AreYouRegisteredForVATPage, urlToTest, destinationRoute, Nil)
    }

    "must redirect to CheckYourAnswers screen if page answer is No and doesn't change" in {

      val previousAnswer = AreYouRegisteredForVAT.No
      val newAnswer = AreYouRegisteredForVAT.No

      val urlToTest = notification.routes.AreYouRegisteredForVATController.onPageLoad(CheckMode).url
      val destinationRoute = notification.routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, AreYouRegisteredForVATPage, urlToTest, destinationRoute, Nil)
    }
  }
}
