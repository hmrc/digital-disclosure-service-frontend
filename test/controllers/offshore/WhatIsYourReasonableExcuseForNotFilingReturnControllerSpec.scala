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
import forms.WhatIsYourReasonableExcuseForNotFilingReturnFormProvider
import models.{NormalMode, WhatIsYourReasonableExcuseForNotFilingReturn, UserAnswers, RelatesTo}
import navigation.{FakeOffshoreNavigator, OffshoreNavigator}
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
import views.html.offshore.WhatIsYourReasonableExcuseForNotFilingReturnView

import scala.concurrent.Future

class WhatIsYourReasonableExcuseForNotFilingReturnControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new WhatIsYourReasonableExcuseForNotFilingReturnFormProvider()
  val form = formProvider(true)

  lazy val whatIsYourReasonableExcuseForNotFilingReturnRoute = offshore.routes.WhatIsYourReasonableExcuseForNotFilingReturnController.onPageLoad(NormalMode).url

  "WhatIsYourReasonableExcuseForNotFilingReturn Controller" - {

    "must return OK and the correct view for a GET" in {
      val userAnswers = (for {
        userAnswer <- UserAnswers("id").set(AreYouTheIndividualPage, true)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
      } yield uaWithRelatesToPage).success.value

      val areTheyTheIndividual = isTheUserTheIndividual(userAnswers)
      val entity = userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, whatIsYourReasonableExcuseForNotFilingReturnRoute)

        val view = application.injector.instanceOf[WhatIsYourReasonableExcuseForNotFilingReturnView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, areTheyTheIndividual, entity)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(
        userAnswersId,
        Json.obj(
          WhatReasonableCareDidYouTakePage.toString -> Json.obj(
            "reasonableExcuse" -> "value 1",
            "yearsThisAppliesTo" -> "value 2"
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
        val request = FakeRequest(GET, whatIsYourReasonableExcuseForNotFilingReturnRoute)

        val view = application.injector.instanceOf[WhatIsYourReasonableExcuseForNotFilingReturnView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(WhatIsYourReasonableExcuseForNotFilingReturn("", "")), NormalMode, areTheyTheIndividual, entity)(request, messages(application)).toString
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
            bind[OffshoreNavigator].toInstance(new FakeOffshoreNavigator(onwardRoute))
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, whatIsYourReasonableExcuseForNotFilingReturnRoute)
            .withFormUrlEncodedBody(("reasonableExcuse", "value 1"), ("yearsThisAppliesTo", "value 2"))

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
          FakeRequest(POST, whatIsYourReasonableExcuseForNotFilingReturnRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[WhatIsYourReasonableExcuseForNotFilingReturnView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, areTheyTheIndividual, entity)(request, messages(application)).toString
      }
    }

    "must redirect to Index for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, whatIsYourReasonableExcuseForNotFilingReturnRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

    "must redirect to Index for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, whatIsYourReasonableExcuseForNotFilingReturnRoute)
            .withFormUrlEncodedBody(("reasonableExcuse", "value 1"), ("yearsThisAppliesTo", "value 2"))

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