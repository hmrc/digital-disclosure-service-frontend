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

import base.ControllerSpecBase
import forms.RelatesToFormProvider
import models._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages._
import pages.notification.SectionPages
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.notification.RelatesToView

import scala.concurrent.Future

class RelatesToControllerSpec extends ControllerSpecBase with SectionPages {

  def onwardRoute = Call("GET", "/foo")

  lazy val RelatesToRoute = routes.RelatesToController.onPageLoad(NormalMode).url

  val formProvider = new RelatesToFormProvider()
  val form         = formProvider()

  "RelatesTo Controller" - {

    "must return OK and the correct view for a GET" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, RelatesToRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[RelatesToView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode, false)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers =
        UserAnswers(userAnswersId, "session-123").set(RelatesToPage, RelatesTo.values.head).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, RelatesToRoute)

      val view = application.injector.instanceOf[RelatesToView]

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(RelatesTo.values.head), NormalMode, false)(
        request,
        messages
      ).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, RelatesToRoute)
          .withFormUrlEncodedBody(("value", RelatesTo.values.head.toString))

      val result = route(applicationWithFakeNotificationNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, RelatesToRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[RelatesToView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, false)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, RelatesToRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, RelatesToRoute)
          .withFormUrlEncodedBody(("value", RelatesTo.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to CheckYourAnswers screen if page answer is An Individual and doesn't change" in {

      val previousAnswer = RelatesTo.AnIndividual
      val newAnswer      = RelatesTo.AnIndividual

      val urlToTest        = routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, Nil)
    }

    "must redirect to CheckYourAnswers screen if page answer is A Company and doesn't change" in {

      val previousAnswer = RelatesTo.ACompany
      val newAnswer      = RelatesTo.ACompany

      val urlToTest        = routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, Nil)
    }

    "must redirect to CheckYourAnswers screen if page answer is AnEstate and doesn't change" in {

      val previousAnswer = RelatesTo.AnEstate
      val newAnswer      = RelatesTo.AnEstate

      val urlToTest        = routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, Nil)
    }
    "must redirect to CheckYourAnswers screen if page answer is ALimitedLiabilityPartnership and doesn't change" in {

      val previousAnswer = RelatesTo.ALimitedLiabilityPartnership
      val newAnswer      = RelatesTo.ALimitedLiabilityPartnership

      val urlToTest        = routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, Nil)
    }
    "must redirect to CheckYourAnswers screen if page answer is ATrust and doesn't change" in {

      val previousAnswer = RelatesTo.ATrust
      val newAnswer      = RelatesTo.ATrust

      val urlToTest        = routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, Nil)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouTheEntityPage with normal mode if page answer changes from RelatesTo.ACompany to RelatesTo.AnIndividual in check mode" in {
      val previousAnswer = RelatesTo.ACompany
      val newAnswer      = RelatesTo.AnIndividual

      val urlToTest        = routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = routes.AreYouTheEntityController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages ::: aboutYouPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouTheEntityPage with normal mode if page answer changes from RelatesTo.AnEstate to RelatesTo.AnIndividual in check mode" in {
      val previousAnswer = RelatesTo.AnEstate
      val newAnswer      = RelatesTo.AnIndividual

      val urlToTest        = routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = routes.AreYouTheEntityController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages ::: aboutYouPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouTheEntityPage with normal mode if page answer changes from RelatesTo.ALimitedLiabilityPartnership to RelatesTo.AnIndividual in check mode" in {
      val previousAnswer = RelatesTo.ALimitedLiabilityPartnership
      val newAnswer      = RelatesTo.AnIndividual

      val urlToTest        = routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = routes.AreYouTheEntityController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages ::: aboutYouPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouTheEntityPage with normal mode if page answer changes from RelatesTo.ATrust to RelatesTo.AnIndividual in check mode" in {
      val previousAnswer = RelatesTo.ATrust
      val newAnswer      = RelatesTo.AnIndividual

      val urlToTest        = routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = routes.AreYouTheEntityController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages ::: aboutYouPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouTheEntityPage with normal mode if page answer changes from RelatesTo.ATrust to RelatesTo.ACompany in check mode" in {
      val previousAnswer = RelatesTo.ATrust
      val newAnswer      = RelatesTo.ACompany

      val urlToTest        = routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = routes.AreYouTheEntityController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouTheEntityPage with normal mode if page answer changes from RelatesTo.ATrust to RelatesTo.ALimitedLiabilityPartnership in check mode" in {
      val previousAnswer = RelatesTo.ATrust
      val newAnswer      = RelatesTo.ALimitedLiabilityPartnership

      val urlToTest        = routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = routes.AreYouTheEntityController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouTheEntityPage with normal mode if page answer changes from RelatesTo.ATrust to RelatesTo.AnEstate in check mode" in {
      val previousAnswer = RelatesTo.ATrust
      val newAnswer      = RelatesTo.AnEstate

      val urlToTest        = routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = routes.AreYouTheEntityController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouTheEntityPage with normal mode if page answer changes from RelatesTo.AnEstate to RelatesTo.ACompany in check mode" in {
      val previousAnswer = RelatesTo.AnEstate
      val newAnswer      = RelatesTo.ACompany

      val urlToTest        = routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = routes.AreYouTheEntityController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouTheEntityPage with normal mode if page answer changes from RelatesTo.AnEstate to RelatesTo.ALimitedLiabilityPartnership in check mode" in {
      val previousAnswer = RelatesTo.AnEstate
      val newAnswer      = RelatesTo.ALimitedLiabilityPartnership

      val urlToTest        = routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = routes.AreYouTheEntityController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouTheEntityPage with normal mode if page answer changes from RelatesTo.AnEstate to RelatesTo.ATrust in check mode" in {
      val previousAnswer = RelatesTo.AnEstate
      val newAnswer      = RelatesTo.ATrust

      val urlToTest        = routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = routes.AreYouTheEntityController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouTheEntityPage with normal mode if page answer changes from RelatesTo.ACompany to RelatesTo.AnEstate in check mode" in {
      val previousAnswer = RelatesTo.ACompany
      val newAnswer      = RelatesTo.AnEstate

      val urlToTest        = routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = routes.AreYouTheEntityController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouTheEntityPage with normal mode if page answer changes from RelatesTo.ACompany to RelatesTo.ALimitedLiabilityPartnership in check mode" in {
      val previousAnswer = RelatesTo.ACompany
      val newAnswer      = RelatesTo.ALimitedLiabilityPartnership

      val urlToTest        = routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = routes.AreYouTheEntityController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouTheEntityPage with normal mode if page answer changes from RelatesTo.ACompany to RelatesTo.ATrust in check mode" in {
      val previousAnswer = RelatesTo.ACompany
      val newAnswer      = RelatesTo.ATrust

      val urlToTest        = routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = routes.AreYouTheEntityController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouTheEntityPage with normal mode if page answer changes from RelatesTo.ALimitedLiabilityPartnership to RelatesTo.AnEstate in check mode" in {
      val previousAnswer = RelatesTo.ALimitedLiabilityPartnership
      val newAnswer      = RelatesTo.AnEstate

      val urlToTest        = routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = routes.AreYouTheEntityController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouTheEntityPage with normal mode if page answer changes from RelatesTo.ALimitedLiabilityPartnership to RelatesTo.ACompany in check mode" in {
      val previousAnswer = RelatesTo.ALimitedLiabilityPartnership
      val newAnswer      = RelatesTo.ACompany

      val urlToTest        = routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = routes.AreYouTheEntityController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouTheEntityPage with normal mode if page answer changes from RelatesTo.ALimitedLiabilityPartnership to RelatesTo.ATrust in check mode" in {
      val previousAnswer = RelatesTo.ALimitedLiabilityPartnership
      val newAnswer      = RelatesTo.ATrust

      val urlToTest        = routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = routes.AreYouTheEntityController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }
  }
}
