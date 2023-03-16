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

import base.ControllerSpecBase
import forms.AreYouRegisteredForSelfAssessmentFormProvider
import models._
import navigation.{FakeNotificationNavigator, NotificationNavigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages._
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SessionService
import views.html.notification.AreYouRegisteredForSelfAssessmentView

import scala.concurrent.Future

class AreYouRegisteredForSelfAssessmentControllerSpec extends ControllerSpecBase {

  def onwardRoute = Call("GET", "/foo")

  lazy val areYouRegisteredForSelfAssessmentRoute = controllers.notification.routes.AreYouRegisteredForSelfAssessmentController.onPageLoad(NormalMode).url

  val formProvider = new AreYouRegisteredForSelfAssessmentFormProvider()
  val form = formProvider()

  "AreYouRegisteredForSelfAssessment Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, areYouRegisteredForSelfAssessmentRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AreYouRegisteredForSelfAssessmentView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, false)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(AreYouRegisteredForSelfAssessmentPage, AreYouRegisteredForSelfAssessment.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, areYouRegisteredForSelfAssessmentRoute)

        val view = application.injector.instanceOf[AreYouRegisteredForSelfAssessmentView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(AreYouRegisteredForSelfAssessment.values.head), NormalMode, false)(request, messages(application)).toString
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
          FakeRequest(POST, areYouRegisteredForSelfAssessmentRoute)
            .withFormUrlEncodedBody(("value", AreYouRegisteredForSelfAssessment.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, areYouRegisteredForSelfAssessmentRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[AreYouRegisteredForSelfAssessmentView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, false)(request, messages(application)).toString
      }
    }

    "must redirect to Index for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, areYouRegisteredForSelfAssessmentRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, areYouRegisteredForSelfAssessmentRoute)
            .withFormUrlEncodedBody(("value", AreYouRegisteredForSelfAssessment.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

    "must redirect to WhatIsYourUniqueTaxReference page (change mode) if page answer changes from No to YesIKnowMyUTR in check mode" in {

      val previousAnswer = AreYouRegisteredForSelfAssessment.No
      val newAnswer = AreYouRegisteredForSelfAssessment.YesIKnowMyUTR

      val urlToTest = controllers.notification.routes.AreYouRegisteredForSelfAssessmentController.onPageLoad(CheckMode).url
      val destinationRoute = controllers.notification.routes.WhatIsYourUniqueTaxReferenceController.onPageLoad(CheckMode).url

      testChangeAnswerRouting(previousAnswer, newAnswer, AreYouRegisteredForSelfAssessmentPage, urlToTest, destinationRoute, Nil)
    }

    "must redirect to WhatIsYourUniqueTaxReference page (change mode) if page answer changes from YesIDontKnowMyUTR to YesIKnowMyUTR in check mode" in {

      val previousAnswer = AreYouRegisteredForSelfAssessment.YesIDontKnowMyUTR
      val newAnswer = AreYouRegisteredForSelfAssessment.YesIKnowMyUTR

      val urlToTest = controllers.notification.routes.AreYouRegisteredForSelfAssessmentController.onPageLoad(CheckMode).url
      val destinationRoute = controllers.notification.routes.WhatIsYourUniqueTaxReferenceController.onPageLoad(CheckMode).url

      testChangeAnswerRouting(previousAnswer, newAnswer, AreYouRegisteredForSelfAssessmentPage, urlToTest, destinationRoute, Nil)
    }

    "must redirect to CheckYourAnswers screen and clear WhatIsYourUniqueTaxReference page if page answer changes from YesIKnowMyUTR to No in check mode" in {

      val previousAnswer = AreYouRegisteredForSelfAssessment.YesIKnowMyUTR
      val newAnswer = AreYouRegisteredForSelfAssessment.No

      val urlToTest = controllers.notification.routes.AreYouRegisteredForSelfAssessmentController.onPageLoad(CheckMode).url
      val destinationRoute = controllers.notification.routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, AreYouRegisteredForSelfAssessmentPage, urlToTest, destinationRoute, List(WhatIsYourUniqueTaxReferencePage))
    }

    "must redirect to CheckYourAnswers screen and clear WhatIsYourUniqueTaxReference page if page answer changes from YesIKnowMyUTR to YesIDontKnowMyUTR in check mode" in {

      val previousAnswer = AreYouRegisteredForSelfAssessment.YesIKnowMyUTR
      val newAnswer = AreYouRegisteredForSelfAssessment.YesIDontKnowMyUTR

      val urlToTest = controllers.notification.routes.AreYouRegisteredForSelfAssessmentController.onPageLoad(CheckMode).url
      val destinationRoute = controllers.notification.routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, AreYouRegisteredForSelfAssessmentPage, urlToTest, destinationRoute, List(WhatIsYourUniqueTaxReferencePage))
    }

    "must redirect to CheckYourAnswers screen if page answer is YesIKnowMyUTR and doesn't change" in {

      val previousAnswer = AreYouRegisteredForSelfAssessment.YesIKnowMyUTR
      val newAnswer = AreYouRegisteredForSelfAssessment.YesIKnowMyUTR

      val urlToTest = controllers.notification.routes.AreYouRegisteredForSelfAssessmentController.onPageLoad(CheckMode).url
      val destinationRoute = controllers.notification.routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, AreYouRegisteredForSelfAssessmentPage, urlToTest, destinationRoute, Nil)
    }

    "must redirect to CheckYourAnswers screen if page answer is No and doesn't change" in {

      val previousAnswer = AreYouRegisteredForSelfAssessment.No
      val newAnswer = AreYouRegisteredForSelfAssessment.No

      val urlToTest = controllers.notification.routes.AreYouRegisteredForSelfAssessmentController.onPageLoad(CheckMode).url
      val destinationRoute = controllers.notification.routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, AreYouRegisteredForSelfAssessmentPage, urlToTest, destinationRoute, Nil)
    }
  }
}
