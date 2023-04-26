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
import forms.WhyAreYouMakingThisOnshoreDisclosureFormProvider
import models.{AreYouTheEntity, NormalMode, WhyAreYouMakingThisOnshoreDisclosure, UserAnswers, RelatesTo}
import navigation.{FakeOnshoreNavigator, OnshoreNavigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages._
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SessionService
import views.html.onshore.WhyAreYouMakingThisOnshoreDisclosureView
import controllers.onshore.WhyAreYouMakingThisOnshoreDisclosureController

import scala.concurrent.Future

class WhyAreYouMakingThisOnshoreDisclosureControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val WhyAreYouMakingThisOnshoreDisclosureRoute = onshore.routes.WhyAreYouMakingThisOnshoreDisclosureController.onPageLoad(NormalMode).url

  val formProvider = new WhyAreYouMakingThisOnshoreDisclosureFormProvider()
  val form = formProvider()

  "WhyAreYouMakingThisOnshoreDisclosure Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = (for {
        userAnswer <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
      } yield uaWithRelatesToPage).success.value

      val areTheyTheIndividual = userAnswers.isTheUserTheIndividual
      val entity = userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, WhyAreYouMakingThisOnshoreDisclosureRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[WhyAreYouMakingThisOnshoreDisclosureView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual view(form, NormalMode, areTheyTheIndividual, entity)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = (for {
        userAnswer <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
        uaWithWhyAreYouMakingThisOnshoreDisclosurePage <- uaWithRelatesToPage.set(WhyAreYouMakingThisOnshoreDisclosurePage, WhyAreYouMakingThisOnshoreDisclosure.values.toSet)
      } yield uaWithWhyAreYouMakingThisOnshoreDisclosurePage).success.value

      val areTheyTheIndividual = userAnswers.isTheUserTheIndividual
      val entity = userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, WhyAreYouMakingThisOnshoreDisclosureRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[WhyAreYouMakingThisOnshoreDisclosureView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(WhyAreYouMakingThisOnshoreDisclosure.values.toSet), NormalMode, areTheyTheIndividual, entity)(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val userAnswers = (for {
        userAnswer <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
      } yield uaWithRelatesToPage).success.value

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
          FakeRequest(POST, WhyAreYouMakingThisOnshoreDisclosureRoute)
            .withFormUrlEncodedBody(("value[0]", WhyAreYouMakingThisOnshoreDisclosure.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = (for {
        userAnswer <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        uaWithRelatesToPage <- userAnswer.set(RelatesToPage, RelatesTo.AnIndividual)
      } yield uaWithRelatesToPage).success.value

      val areTheyTheIndividual = userAnswers.isTheUserTheIndividual
      val entity = userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, WhyAreYouMakingThisOnshoreDisclosureRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[WhyAreYouMakingThisOnshoreDisclosureView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, areTheyTheIndividual, entity)(request, messages(application)).toString
      }
    }

    "must redirect to Index for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, WhyAreYouMakingThisOnshoreDisclosureRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

    "must redirect to Index for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, WhyAreYouMakingThisOnshoreDisclosureRoute)
            .withFormUrlEncodedBody(("value[0]", WhyAreYouMakingThisOnshoreDisclosure.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }
  }

  "getPages" - {

    "return deliberate pages when a deliberate option was not selected" in {
      val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(
        WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHasExcuse, 
        WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturnWithCare,
        WhyAreYouMakingThisOnshoreDisclosure.NotFileHasExcuse,
        WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturnNoCare
      )
      val expectedPages = List(CDFOnshorePage, TaxBeforeNineteenYearsOnshorePage)
      WhyAreYouMakingThisOnshoreDisclosureController.getPages(set) mustEqual expectedPages
    }

    "return tax before five years page when all options other than InaccurateReturnNoCare was selected" in {
      val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(
        WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturnNoCare
      )
      val expectedPages = List(CDFOnshorePage, TaxBeforeNineteenYearsOnshorePage, ReasonableExcuseOnshorePage, ReasonableCareOnshorePage, ReasonableExcuseForNotFilingOnshorePage, TaxBeforeThreeYearsOnshorePage)
      WhyAreYouMakingThisOnshoreDisclosureController.getPages(set) mustEqual expectedPages
    }

    "return all pages other than the deliberate ones where only a deliberate excuse is now selected" in {
      val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(
        WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyNoExcuse
      )
      val expectedPages = List(ReasonableExcuseOnshorePage, ReasonableCareOnshorePage, ReasonableExcuseForNotFilingOnshorePage, TaxBeforeThreeYearsOnshorePage, TaxBeforeFiveYearsOnshorePage)
      WhyAreYouMakingThisOnshoreDisclosureController.getPages(set) mustEqual expectedPages
    }

  }

}
