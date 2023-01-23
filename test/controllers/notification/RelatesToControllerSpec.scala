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
import forms.RelatesToFormProvider
import models._
import navigation.{FakeNotificationNavigator, NotificationNavigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages._
import pages.notification.SectionPages
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SessionService
import views.html.notification.RelatesToView

import scala.concurrent.Future

class RelatesToControllerSpec extends ControllerSpecBase with SectionPages {

  def onwardRoute = Call("GET", "/foo")

  lazy val RelatesToRoute = controllers.notification.routes.RelatesToController.onPageLoad(NormalMode).url

  val formProvider = new RelatesToFormProvider()
  val form = formProvider()

  "RelatesTo Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, RelatesToRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[RelatesToView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(RelatesToPage, RelatesTo.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, RelatesToRoute)

        val view = application.injector.instanceOf[RelatesToView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(RelatesTo.values.head), NormalMode)(request, messages(application)).toString
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
          FakeRequest(POST, RelatesToRoute)
            .withFormUrlEncodedBody(("value", RelatesTo.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, RelatesToRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[RelatesToView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to Index for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, RelatesToRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, RelatesToRoute)
            .withFormUrlEncodedBody(("value", RelatesTo.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

    "must redirect to CheckYourAnswers screen if page answer is An Individual and doesn't change" in {

      val previousAnswer = RelatesTo.AnIndividual
      val newAnswer = RelatesTo.AnIndividual

      val urlToTest = controllers.notification.routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = controllers.notification.routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, Nil)
    }

    "must redirect to CheckYourAnswers screen if page answer is A Company and doesn't change" in {

      val previousAnswer = RelatesTo.ACompany
      val newAnswer = RelatesTo.ACompany

      val urlToTest = controllers.notification.routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = controllers.notification.routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, Nil)
    }

    "must redirect to CheckYourAnswers screen if page answer is AnEstate and doesn't change" in {

      val previousAnswer = RelatesTo.AnEstate
      val newAnswer = RelatesTo.AnEstate

      val urlToTest = controllers.notification.routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = controllers.notification.routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, Nil)
    }
    "must redirect to CheckYourAnswers screen if page answer is ALimitedLiabilityPartnership and doesn't change" in {

      val previousAnswer = RelatesTo.ALimitedLiabilityPartnership
      val newAnswer = RelatesTo.ALimitedLiabilityPartnership

      val urlToTest = controllers.notification.routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = controllers.notification.routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, Nil)
    }
    "must redirect to CheckYourAnswers screen if page answer is ATrust and doesn't change" in {

      val previousAnswer = RelatesTo.ATrust
      val newAnswer = RelatesTo.ATrust

      val urlToTest = controllers.notification.routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = controllers.notification.routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, Nil)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouTheIndividualPage with normal mode if page answer changes from RelatesTo.ACompany to RelatesTo.AnIndividual in check mode" in {
      val previousAnswer = RelatesTo.ACompany
      val newAnswer = RelatesTo.AnIndividual

      val urlToTest = controllers.notification.routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = controllers.notification.routes.AreYouTheIndividualController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages ::: aboutYouPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouTheIndividualPage with normal mode if page answer changes from RelatesTo.AnEstate to RelatesTo.AnIndividual in check mode" in {
      val previousAnswer = RelatesTo.AnEstate
      val newAnswer = RelatesTo.AnIndividual

      val urlToTest = controllers.notification.routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = controllers.notification.routes.AreYouTheIndividualController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages ::: aboutYouPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouTheIndividualPage with normal mode if page answer changes from RelatesTo.ALimitedLiabilityPartnership to RelatesTo.AnIndividual in check mode" in {
      val previousAnswer = RelatesTo.ALimitedLiabilityPartnership
      val newAnswer = RelatesTo.AnIndividual

      val urlToTest = controllers.notification.routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = controllers.notification.routes.AreYouTheIndividualController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages ::: aboutYouPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouTheIndividualPage with normal mode if page answer changes from RelatesTo.ATrust to RelatesTo.AnIndividual in check mode" in {
      val previousAnswer = RelatesTo.ATrust
      val newAnswer = RelatesTo.AnIndividual

      val urlToTest = controllers.notification.routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = controllers.notification.routes.AreYouTheIndividualController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages ::: aboutYouPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage with normal mode if page answer changes from RelatesTo.ATrust to RelatesTo.ACompany in check mode" in {
      val previousAnswer = RelatesTo.ATrust
      val newAnswer = RelatesTo.ACompany

      val urlToTest = controllers.notification.routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = controllers.notification.routes.AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage with normal mode if page answer changes from RelatesTo.ATrust to RelatesTo.ALimitedLiabilityPartnership in check mode" in {
      val previousAnswer = RelatesTo.ATrust
      val newAnswer = RelatesTo.ALimitedLiabilityPartnership

      val urlToTest = controllers.notification.routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = controllers.notification.routes.AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouTheExecutorOfTheEstatePage with normal mode if page answer changes from RelatesTo.ATrust to RelatesTo.AnEstate in check mode" in {
      val previousAnswer = RelatesTo.ATrust
      val newAnswer = RelatesTo.AnEstate

      val urlToTest = controllers.notification.routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = controllers.notification.routes.AreYouTheExecutorOfTheEstateController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage with normal mode if page answer changes from RelatesTo.AnEstate to RelatesTo.ACompany in check mode" in {
      val previousAnswer = RelatesTo.AnEstate
      val newAnswer = RelatesTo.ACompany

      val urlToTest = controllers.notification.routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = controllers.notification.routes.AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage with normal mode if page answer changes from RelatesTo.AnEstate to RelatesTo.ALimitedLiabilityPartnership in check mode" in {
      val previousAnswer = RelatesTo.AnEstate
      val newAnswer = RelatesTo.ALimitedLiabilityPartnership

      val urlToTest = controllers.notification.routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = controllers.notification.routes.AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage with normal mode if page answer changes from RelatesTo.AnEstate to RelatesTo.ATrust in check mode" in {
      val previousAnswer = RelatesTo.AnEstate
      val newAnswer = RelatesTo.ATrust

      val urlToTest = controllers.notification.routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = controllers.notification.routes.AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouTheExecutorOfTheEstatePage with normal mode if page answer changes from RelatesTo.ACompany to RelatesTo.AnEstate in check mode" in {
      val previousAnswer = RelatesTo.ACompany
      val newAnswer = RelatesTo.AnEstate

      val urlToTest = controllers.notification.routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = controllers.notification.routes.AreYouTheExecutorOfTheEstateController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage with normal mode if page answer changes from RelatesTo.ACompany to RelatesTo.ALimitedLiabilityPartnership in check mode" in {
      val previousAnswer = RelatesTo.ACompany
      val newAnswer = RelatesTo.ALimitedLiabilityPartnership

      val urlToTest = controllers.notification.routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = controllers.notification.routes.AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage with normal mode if page answer changes from RelatesTo.ACompany to RelatesTo.ATrust in check mode" in {
      val previousAnswer = RelatesTo.ACompany
      val newAnswer = RelatesTo.ATrust

      val urlToTest = controllers.notification.routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = controllers.notification.routes.AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouTheExecutorOfTheEstatePage with normal mode if page answer changes from RelatesTo.ALimitedLiabilityPartnership to RelatesTo.AnEstate in check mode" in {
      val previousAnswer = RelatesTo.ALimitedLiabilityPartnership
      val newAnswer = RelatesTo.AnEstate

      val urlToTest = controllers.notification.routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = controllers.notification.routes.AreYouTheExecutorOfTheEstateController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage with normal mode if page answer changes from RelatesTo.ALimitedLiabilityPartnership to RelatesTo.ACompany in check mode" in {
      val previousAnswer = RelatesTo.ALimitedLiabilityPartnership
      val newAnswer = RelatesTo.ACompany

      val urlToTest = controllers.notification.routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = controllers.notification.routes.AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }

    "must redirect to RelatesToPage page (change mode) and then AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage with normal mode if page answer changes from RelatesTo.ALimitedLiabilityPartnership to RelatesTo.ATrust in check mode" in {
      val previousAnswer = RelatesTo.ALimitedLiabilityPartnership
      val newAnswer = RelatesTo.ATrust

      val urlToTest = controllers.notification.routes.RelatesToController.onPageLoad(CheckMode).url
      val destinationRoute = controllers.notification.routes.AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutController.onPageLoad(NormalMode).url

      val pageToBeClear = allEntityPages

      testChangeAnswerRouting(previousAnswer, newAnswer, RelatesToPage, urlToTest, destinationRoute, pageToBeClear)
    }
  }
}
