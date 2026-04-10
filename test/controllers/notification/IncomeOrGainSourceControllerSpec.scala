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

package controllers.notification

import base.SpecBase
import forms.IncomeOrGainSourceFormProvider
import models.{IncomeOrGainSource, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{IncomeOrGainSourcePage, OtherIncomeOrGainSourcePage}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.notification.IncomeOrGainSourceView

import scala.concurrent.Future

class IncomeOrGainSourceControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val whereDidTheUndeclaredIncomeOrGainIncludedRoute =
    routes.IncomeOrGainSourceController.onPageLoad(NormalMode).url

  val formProvider = new IncomeOrGainSourceFormProvider()
  val form         = formProvider()

  "IncomeOrGainSource Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, whereDidTheUndeclaredIncomeOrGainIncludedRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[IncomeOrGainSourceView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual view(form, NormalMode, false)(using request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId, "session-123")
        .set(IncomeOrGainSourcePage, IncomeOrGainSource.values.toSet)
        .success
        .value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, whereDidTheUndeclaredIncomeOrGainIncludedRoute)

      val view = application.injector.instanceOf[IncomeOrGainSourceView]

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(IncomeOrGainSource.values.toSet), NormalMode, false)(
        using request,
        messages
      ).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(using any())) `thenReturn` Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, whereDidTheUndeclaredIncomeOrGainIncludedRoute)
          .withFormUrlEncodedBody(("value[0]", IncomeOrGainSource.values.head.toString))

      val result = route(applicationWithFakeNotificationNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, whereDidTheUndeclaredIncomeOrGainIncludedRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[IncomeOrGainSourceView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, false)(using request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, whereDidTheUndeclaredIncomeOrGainIncludedRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to Index for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, whereDidTheUndeclaredIncomeOrGainIncludedRoute)
          .withFormUrlEncodedBody(("value[0]", IncomeOrGainSource.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }
  }
  "changedPages logic" - {

    "must return (Nil, true) when SomewhereElse is added" in {
      val controller  = application.injector.instanceOf[IncomeOrGainSourceController]
      val userAnswers = UserAnswers(userAnswersId, "session-123")
        .set(IncomeOrGainSourcePage, Set[IncomeOrGainSource](IncomeOrGainSource.Dividends))
        .success
        .value

      val (pages, changed) =
        controller.changedPages(userAnswers, Set(IncomeOrGainSource.Dividends, IncomeOrGainSource.SomewhereElse))
      pages mustBe Nil
      changed mustBe true
    }

    "must return (List(OtherIncomeOrGainSourcePage), true) when SomewhereElse is removed" in {
      val controller  = application.injector.instanceOf[IncomeOrGainSourceController]
      val userAnswers = UserAnswers(userAnswersId, "session-123")
        .set(
          IncomeOrGainSourcePage,
          Set[IncomeOrGainSource](IncomeOrGainSource.Dividends, IncomeOrGainSource.SomewhereElse)
        )
        .success
        .value

      val (pages, changed) = controller.changedPages(userAnswers, Set(IncomeOrGainSource.Dividends))
      pages must contain(OtherIncomeOrGainSourcePage)
      changed mustBe true
    }

    "must return (Nil, true) when value changes but SomewhereElse not involved" in {
      val controller  = application.injector.instanceOf[IncomeOrGainSourceController]
      val userAnswers = UserAnswers(userAnswersId, "session-123")
        .set(IncomeOrGainSourcePage, Set[IncomeOrGainSource](IncomeOrGainSource.Dividends))
        .success
        .value

      val (pages, changed) = controller.changedPages(userAnswers, Set(IncomeOrGainSource.Interest))
      pages mustBe Nil
      changed mustBe true
    }

    "must return (Nil, false) when no previous answer exists" in {
      val controller       = application.injector.instanceOf[IncomeOrGainSourceController]
      val (pages, changed) = controller.changedPages(emptyUserAnswers, Set(IncomeOrGainSource.Dividends))
      pages mustBe Nil
      changed mustBe false
    }

    "must return (Nil, false) when value is unchanged" in {
      val controller  = application.injector.instanceOf[IncomeOrGainSourceController]
      val userAnswers = UserAnswers(userAnswersId, "session-123")
        .set(IncomeOrGainSourcePage, Set[IncomeOrGainSource](IncomeOrGainSource.Dividends))
        .success
        .value

      val (pages, changed) = controller.changedPages(userAnswers, Set(IncomeOrGainSource.Dividends))
      pages mustBe Nil
      changed mustBe false
    }
  }
}
