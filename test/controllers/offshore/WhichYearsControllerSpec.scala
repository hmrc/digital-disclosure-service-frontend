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

package controllers.offshore

import base.SpecBase
import forms.WhichYearsFormProvider
import models.{Behaviour, NormalMode, OffshoreYears, TaxYearStarting, UserAnswers, WhyAreYouMakingThisDisclosure}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{WhichYearsPage, WhyAreYouMakingThisDisclosurePage}
import play.api.i18n.Messages
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.OffshoreWhichYearsService
import views.html.offshore.WhichYearsView

import scala.concurrent.Future

class WhichYearsControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val whichYearsRoute = routes.WhichYearsController.onPageLoad(NormalMode).url

  val formProvider = new WhichYearsFormProvider()
  val form         = formProvider()

  "determineBehaviour" - {

    setupMockSessionResponse(Some(emptyUserAnswers))
    val controller              = application.injector.instanceOf[WhichYearsController]
    implicit val mess: Messages = messages
    val service                 = application.injector.instanceOf[OffshoreWhichYearsService]

    "return ReasonableExcuse (5 years) by default" - {

      "when no Page 2 is answered" in {
        val userAnswers = UserAnswers(userAnswersId, "session-123")
        controller.populateChecklist(userAnswers) mustEqual service.checkboxItems(Behaviour.ReasonableExcuse)
      }

      "when only new Page 1 values are selected but no Page 2 answered" in {
        val set: Set[WhyAreYouMakingThisDisclosure] = Set(
          WhyAreYouMakingThisDisclosure.DidNotNotifyHMRC,
          WhyAreYouMakingThisDisclosure.DidNotFile,
          WhyAreYouMakingThisDisclosure.InaccurateReturn
        )
        val userAnswers                             =
          UserAnswers(userAnswersId, "session-123").set(WhyAreYouMakingThisDisclosurePage, set).success.value
        controller.populateChecklist(userAnswers) mustEqual service.checkboxItems(Behaviour.ReasonableExcuse)
      }

    }

  }

  "WhichYears Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, whichYearsRoute)

      val result = route(application, request).value

      val view                    = application.injector.instanceOf[WhichYearsView]
      val service                 = application.injector.instanceOf[OffshoreWhichYearsService]
      implicit val mess: Messages = messages

      status(result) mustEqual OK

      contentAsString(result) mustEqual view(form, NormalMode, service.checkboxItems(Behaviour.ReasonableExcuse))(using
        request,
        messages
      ).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val previousValue: Set[OffshoreYears] = Set(TaxYearStarting(2022))
      val userAnswers                       = UserAnswers(userAnswersId, "session-123").set(WhichYearsPage, previousValue).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, whichYearsRoute)

      val view                    = application.injector.instanceOf[WhichYearsView]
      val service                 = application.injector.instanceOf[OffshoreWhichYearsService]
      implicit val mess: Messages = messages

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form.fill(previousValue),
        NormalMode,
        service.checkboxItems(Behaviour.ReasonableExcuse)
      )(using request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(using any())) `thenReturn` Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, whichYearsRoute)
          .withFormUrlEncodedBody(("value[0]", "2022"))

      val result = route(applicationWithFakeOffshoreNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, whichYearsRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view                    = application.injector.instanceOf[WhichYearsView]
      val service                 = application.injector.instanceOf[OffshoreWhichYearsService]
      implicit val mess: Messages = messages

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, service.checkboxItems(Behaviour.ReasonableExcuse))(
        using
        request,
        messages
      ).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, whichYearsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to Index for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, whichYearsRoute)
          .withFormUrlEncodedBody(("value[0]", "2022"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }
  }
}
