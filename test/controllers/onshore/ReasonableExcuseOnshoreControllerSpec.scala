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
import forms.ReasonableExcuseOnshoreFormProvider
import models.{NormalMode, ReasonableExcuseOnshore, UserAnswers, RelatesTo}
import navigation.{FakeOnshoreNavigator, OnshoreNavigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages._
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SessionService
import views.html.onshore.ReasonableExcuseOnshoreView

import scala.concurrent.Future

class ReasonableExcuseOnshoreControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new ReasonableExcuseOnshoreFormProvider()
  val form = formProvider(true)

  lazy val whatIsYourReasonableExcuseRoute = onshore.routes.ReasonableExcuseOnshoreController.onPageLoad(NormalMode).url

  "ReasonableExcuseOnshore Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = (for {
        userAnswer <- UserAnswers("id").set(AreYouTheIndividualPage, true)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
      } yield uaWithRelatesToPage).success.value

      val areTheyTheIndividual = isTheUserTheIndividual(userAnswers)
      val entity = userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, whatIsYourReasonableExcuseRoute)

        val view = application.injector.instanceOf[ReasonableExcuseOnshoreView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, areTheyTheIndividual, entity)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(
        userAnswersId,
        Json.obj(
          ReasonableExcuseOnshorePage.toString -> Json.obj(
            "excuse" -> "value 1",
            "years" -> "value 2"
          )
        ).toString
      )

      val ua = (for {
        updatedAnswer <- userAnswers.set(AreYouTheIndividualPage, true)
        uaWithRelatesToPage <- updatedAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
      } yield uaWithRelatesToPage).success.value

      val areTheyTheIndividual = isTheUserTheIndividual(ua)
      val entity = ua.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      val application = applicationBuilder(userAnswers = Some(ua)).build()

      running(application) {
        val request = FakeRequest(GET, whatIsYourReasonableExcuseRoute)

        val view = application.injector.instanceOf[ReasonableExcuseOnshoreView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(ReasonableExcuseOnshore("", "")), NormalMode, areTheyTheIndividual, entity)(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val ua = (for {
        updatedAnswer <- UserAnswers("id").set(AreYouTheIndividualPage, true)
        uaWithRelatesToPage <- updatedAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
      } yield uaWithRelatesToPage).success.value

      val mockSessionService = mock[SessionService]

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilderWithSessionService(userAnswers = Some(ua), mockSessionService)
          .overrides(
            bind[OnshoreNavigator].toInstance(new FakeOnshoreNavigator(onwardRoute))
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, whatIsYourReasonableExcuseRoute)
            .withFormUrlEncodedBody(("excuse", "value 1"), ("years", "value 2"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val ua = (for {
        updatedAnswer <- UserAnswers("id").set(AreYouTheIndividualPage, true)
        uaWithRelatesToPage <- updatedAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
      } yield uaWithRelatesToPage).success.value

      val areTheyTheIndividual = isTheUserTheIndividual(ua)
      val entity = ua.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      val application = applicationBuilder(userAnswers = Some(ua)).build()

      running(application) {
        val request =
          FakeRequest(POST, whatIsYourReasonableExcuseRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[ReasonableExcuseOnshoreView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, areTheyTheIndividual, entity)(request, messages(application)).toString
      }
    }

    "must redirect to Index for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, whatIsYourReasonableExcuseRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

    "must redirect to Index for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, whatIsYourReasonableExcuseRoute)
            .withFormUrlEncodedBody(("excuse", "value 1"), ("years", "value 2"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }
  }

  def isTheUserTheIndividual(userAnswers: UserAnswers): Boolean = {
    userAnswers.get(AreYouTheIndividualPage) match {
      case Some(true) => true
      case _ => false
    }
  }
}