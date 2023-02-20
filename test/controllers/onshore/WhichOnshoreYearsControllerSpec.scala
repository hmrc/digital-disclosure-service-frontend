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
import forms.WhichOnshoreYearsFormProvider
import models.{WhyAreYouMakingThisOnshoreDisclosure, NormalMode, OnshoreYears, UserAnswers, Behaviour, OnshoreYearStarting}
import navigation.{FakeOnshoreNavigator, OnshoreNavigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{WhyAreYouMakingThisOnshoreDisclosurePage, WhichOnshoreYearsPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{OnshoreWhichYearsService, SessionService}
import views.html.onshore.WhichOnshoreYearsView
import controllers.onshore.WhichOnshoreYearsController

import scala.concurrent.Future

class WhichOnshoreYearsControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val whichOnshoreYearsRoute = onshore.routes.WhichOnshoreYearsController.onPageLoad(NormalMode).url

  val formProvider = new WhichOnshoreYearsFormProvider()
  val form = formProvider()

  "determineBehaviour" - {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
    val controller = application.injector.instanceOf[WhichOnshoreYearsController]
    implicit val mess = messages(application)
    val service = application.injector.instanceOf[OnshoreWhichYearsService]

    "return 19"  - {
    
      "when a deliberate behaviour is selected alongside other values" in {
        val userAnswers = UserAnswers(userAnswersId).set(WhyAreYouMakingThisOnshoreDisclosurePage, WhyAreYouMakingThisOnshoreDisclosure.values.toSet).success.value
        controller.populateChecklist(userAnswers) mustEqual service.checkboxItems(Behaviour.Deliberate)
      }

      "when only DidNotNotifyNoExcuse is selected" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyNoExcuse)
        val userAnswers = UserAnswers(userAnswersId).set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        controller.populateChecklist(userAnswers) mustEqual service.checkboxItems(Behaviour.Deliberate)
      }

      "when only DeliberatelyDidNotNotify is selected" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.DeliberatelyDidNotNotify)
        val userAnswers = UserAnswers(userAnswersId).set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        controller.populateChecklist(userAnswers) mustEqual service.checkboxItems(Behaviour.Deliberate)
      }

      "when only DeliberateInaccurateReturn is selected" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.DeliberateInaccurateReturn)
        val userAnswers = UserAnswers(userAnswersId).set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        controller.populateChecklist(userAnswers) mustEqual service.checkboxItems(Behaviour.Deliberate)
      }

      "when only DeliberatelyDidNotFile is selected" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.DeliberatelyDidNotFile)
        val userAnswers = UserAnswers(userAnswersId).set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        controller.populateChecklist(userAnswers) mustEqual service.checkboxItems(Behaviour.Deliberate)
      }

    }

    "return 5"  - {
    
      "when InaccurateReturnNoCare is selected alongside other values" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(
          WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturnNoCare,
          WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHasExcuse,
          WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturnWithCare,
          WhyAreYouMakingThisOnshoreDisclosure.NotFileHasExcuse
        )
        val userAnswers = UserAnswers(userAnswersId).set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        controller.populateChecklist(userAnswers) mustEqual service.checkboxItems(Behaviour.Careless)
      }

      "when only InaccurateReturnNoCare is selected" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturnNoCare)
        val userAnswers = UserAnswers(userAnswersId).set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        controller.populateChecklist(userAnswers) mustEqual service.checkboxItems(Behaviour.Careless)
      }

    }

    "return 3"  - {
    
      "when all other values are selected" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(
          WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHasExcuse,
          WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturnWithCare,
          WhyAreYouMakingThisOnshoreDisclosure.NotFileHasExcuse
        )
        val userAnswers = UserAnswers(userAnswersId).set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        controller.populateChecklist(userAnswers) mustEqual service.checkboxItems(Behaviour.ReasonableExcuse)
      }

      "when only DidNotNotifyHasExcuse is selected" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHasExcuse)
        val userAnswers = UserAnswers(userAnswersId).set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        controller.populateChecklist(userAnswers) mustEqual service.checkboxItems(Behaviour.ReasonableExcuse)
      }

      "when only InaccurateReturnWithCare is selected" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturnWithCare)
        val userAnswers = UserAnswers(userAnswersId).set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        controller.populateChecklist(userAnswers) mustEqual service.checkboxItems(Behaviour.ReasonableExcuse)
      }

      "when only NotFileHasExcuse is selected" in {
        val set: Set[WhyAreYouMakingThisOnshoreDisclosure] = Set(WhyAreYouMakingThisOnshoreDisclosure.NotFileHasExcuse)
        val userAnswers = UserAnswers(userAnswersId).set(WhyAreYouMakingThisOnshoreDisclosurePage, set).success.value
        controller.populateChecklist(userAnswers) mustEqual service.checkboxItems(Behaviour.ReasonableExcuse)
      }

    }

  }

  "WhichOnshoreYears Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, whichOnshoreYearsRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[WhichOnshoreYearsView]
        val service = application.injector.instanceOf[OnshoreWhichYearsService]
        implicit val mess = messages(application)

        status(result) mustEqual OK

        contentAsString(result) mustEqual view(form, NormalMode, service.checkboxItems(Behaviour.ReasonableExcuse))(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val previousValue: Set[OnshoreYears] = Set(OnshoreYearStarting(2022))
      val userAnswers = UserAnswers(userAnswersId).set(WhichOnshoreYearsPage, previousValue).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, whichOnshoreYearsRoute)

        val view = application.injector.instanceOf[WhichOnshoreYearsView]
        val service = application.injector.instanceOf[OnshoreWhichYearsService]
        implicit val mess = messages(application)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(previousValue), NormalMode, service.checkboxItems(Behaviour.ReasonableExcuse))(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

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
          FakeRequest(POST, whichOnshoreYearsRoute)
            .withFormUrlEncodedBody(("value[0]", "2022"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, whichOnshoreYearsRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[WhichOnshoreYearsView]
        val service = application.injector.instanceOf[OnshoreWhichYearsService]
        implicit val mess = messages(application)

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, service.checkboxItems(Behaviour.ReasonableExcuse))(request, messages(application)).toString
      }
    }

    "must redirect to Index for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, whichOnshoreYearsRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

    "must redirect to Index for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, whichOnshoreYearsRoute)
            .withFormUrlEncodedBody(("value[0]", "2022"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }
  }
}
