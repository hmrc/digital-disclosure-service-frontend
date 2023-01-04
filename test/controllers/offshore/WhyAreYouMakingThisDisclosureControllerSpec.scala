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
import forms.WhyAreYouMakingThisDisclosureFormProvider
import models.{NormalMode, WhyAreYouMakingThisDisclosure, UserAnswers, RelatesTo}
import navigation.{FakeNotificationNavigator, NotificationNavigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages._
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SessionService
import views.html.offshore.WhyAreYouMakingThisDisclosureView

import scala.concurrent.Future

class WhyAreYouMakingThisDisclosureControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val whyAreYouMakingThisDisclosureRoute = offshore.routes.WhyAreYouMakingThisDisclosureController.onPageLoad(NormalMode).url

  val formProvider = new WhyAreYouMakingThisDisclosureFormProvider()
  val form = formProvider()

  "WhyAreYouMakingThisDisclosure Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = (for {
        userAnswer <- UserAnswers("id").set(AreYouTheIndividualPage, true)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
      } yield uaWithRelatesToPage).success.value

      val areTheyTheIndividual = isTheUserTheIndividual(userAnswers)
      val entity = userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, whyAreYouMakingThisDisclosureRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[WhyAreYouMakingThisDisclosureView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual view(form, NormalMode, areTheyTheIndividual, entity)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = (for {
        userAnswer <- UserAnswers("id").set(AreYouTheIndividualPage, true)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
        uaWithWhyAreYouMakingThisDisclosurePage <- uaWithRelatesToPage.set(WhyAreYouMakingThisDisclosurePage, WhyAreYouMakingThisDisclosure.values.toSet)
      } yield uaWithWhyAreYouMakingThisDisclosurePage).success.value

      val areTheyTheIndividual = isTheUserTheIndividual(userAnswers)
      val entity = userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, whyAreYouMakingThisDisclosureRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[WhyAreYouMakingThisDisclosureView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(WhyAreYouMakingThisDisclosure.values.toSet), NormalMode, areTheyTheIndividual, entity)(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val userAnswers = (for {
        userAnswer <- UserAnswers("id").set(AreYouTheIndividualPage, true)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
      } yield uaWithRelatesToPage).success.value

      val mockSessionService = mock[SessionService]

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilderWithSessionService(userAnswers = Some(userAnswers), mockSessionService)
          .overrides(
            bind[NotificationNavigator].toInstance(new FakeNotificationNavigator(onwardRoute))
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, whyAreYouMakingThisDisclosureRoute)
            .withFormUrlEncodedBody(("value[0]", WhyAreYouMakingThisDisclosure.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = (for {
        userAnswer <- UserAnswers("id").set(AreYouTheIndividualPage, true)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
      } yield uaWithRelatesToPage).success.value

      val areTheyTheIndividual = isTheUserTheIndividual(userAnswers)
      val entity = userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, whyAreYouMakingThisDisclosureRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[WhyAreYouMakingThisDisclosureView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, areTheyTheIndividual, entity)(request, messages(application)).toString
      }
    }

    "must redirect to Index for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, whyAreYouMakingThisDisclosureRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

    "must redirect to Index for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, whyAreYouMakingThisDisclosureRoute)
            .withFormUrlEncodedBody(("value[0]", WhyAreYouMakingThisDisclosure.values.head.toString))

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
