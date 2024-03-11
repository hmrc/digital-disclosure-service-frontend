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

package controllers.reason

import base.ControllerSpecBase
import forms.DidSomeoneGiveYouAdviceNotDeclareTaxFormProvider
import models.{AreYouTheEntity, CheckMode, NormalMode, RelatesTo, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import pages._
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.reason.DidSomeoneGiveYouAdviceNotDeclareTaxView

import scala.concurrent.Future

class DidSomeoneGiveYouAdviceNotDeclareTaxControllerSpec extends ControllerSpecBase {

  def onwardRoute = Call("GET", "/foo")

  lazy val didSomeoneGiveYouAdviceNotDeclareTaxRoute = routes.DidSomeoneGiveYouAdviceNotDeclareTaxController.onPageLoad(NormalMode).url

  val formProvider = new DidSomeoneGiveYouAdviceNotDeclareTaxFormProvider()
  val form = formProvider(true, RelatesTo.AnIndividual)

  "DidSomeoneGiveYouAdviceNotDeclareTax Controller" - {

    "must return OK and the correct view for a GET" in {

      val areTheyTheIndividual = arbitrary[AreYouTheEntity].sample.value 
      val entity = arbitrary[RelatesTo].sample.value  

      val userAnswers = (for {
        ua <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, areTheyTheIndividual)
        updatedUa <- ua.set(RelatesToPage, entity)  
      } yield updatedUa).success.value  

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, didSomeoneGiveYouAdviceNotDeclareTaxRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[DidSomeoneGiveYouAdviceNotDeclareTaxView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, NormalMode, userAnswers.isTheUserTheIndividual, entity)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val areTheyTheIndividual = arbitrary[AreYouTheEntity].sample.value 
      val entity = arbitrary[RelatesTo].sample.value 

      val userAnswers = (for {
        ua <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, areTheyTheIndividual)
        uaRelatesToPage <- ua.set(RelatesToPage, entity) 
        updatedUa <- uaRelatesToPage.set(DidSomeoneGiveYouAdviceNotDeclareTaxPage, true)
      } yield updatedUa).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, didSomeoneGiveYouAdviceNotDeclareTaxRoute)

      val view = application.injector.instanceOf[DidSomeoneGiveYouAdviceNotDeclareTaxView]

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(true), NormalMode, userAnswers.isTheUserTheIndividual, entity)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, didSomeoneGiveYouAdviceNotDeclareTaxRoute)
          .withFormUrlEncodedBody(("value", true.toString))

      val result = route(applicationWithFakeReasonNavigator(onwardRoute), request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val areTheyTheIndividual = arbitrary[AreYouTheEntity].sample.value 
      val entity = arbitrary[RelatesTo].sample.value 

      val userAnswers = (for {
        ua <- UserAnswers("id", "session-123").set(AreYouTheEntityPage, areTheyTheIndividual)
        updatedUa <- ua.set(RelatesToPage, entity) 
      } yield updatedUa).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request =
        FakeRequest(POST, didSomeoneGiveYouAdviceNotDeclareTaxRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[DidSomeoneGiveYouAdviceNotDeclareTaxView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, NormalMode, userAnswers.isTheUserTheIndividual, entity)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, didSomeoneGiveYouAdviceNotDeclareTaxRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, didSomeoneGiveYouAdviceNotDeclareTaxRoute)
          .withFormUrlEncodedBody(("value", true.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to PersonWhoGaveAdvice page screen if page answer changes from No to Yes in check mode" in {
      val previousAnswer = false
      val newAnswer = true

      val urlToTest = routes.DidSomeoneGiveYouAdviceNotDeclareTaxController.onPageLoad(CheckMode).url
      val destinationRoute = routes.PersonWhoGaveAdviceController.onPageLoad(NormalMode).url

      testChangeAnswerRouting(previousAnswer, newAnswer, DidSomeoneGiveYouAdviceNotDeclareTaxPage, urlToTest, destinationRoute, Nil)
    }

    "must redirect to CheckYourAnswers page screen if page answer changes from Yes to No in check mode and clear Advice Given related pages" in {
      val previousAnswer = true
      val newAnswer = false

      val pageToClear = List(PersonWhoGaveAdvicePage,
        AdviceBusinessesOrOrgPage,
        AdviceBusinessNamePage,
        AdviceProfessionPage,
        AdviceGivenPage,
        WhichEmailAddressCanWeContactYouWithPage,
        WhatEmailAddressCanWeContactYouWithPage,
        WhichTelephoneNumberCanWeContactYouWithPage,
        WhatTelephoneNumberCanWeContactYouWithPage)

      val urlToTest = routes.DidSomeoneGiveYouAdviceNotDeclareTaxController.onPageLoad(CheckMode).url
      val destinationRoute = routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, DidSomeoneGiveYouAdviceNotDeclareTaxPage, urlToTest, destinationRoute, pageToClear)
    }

    "must redirect to CheckYourAnswers page screen if page answer is Yes and it does not change" in {
      val previousAnswer = true
      val newAnswer = true

      val urlToTest = routes.DidSomeoneGiveYouAdviceNotDeclareTaxController.onPageLoad(CheckMode).url
      val destinationRoute = routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, DidSomeoneGiveYouAdviceNotDeclareTaxPage, urlToTest, destinationRoute, Nil)
    }

    "must redirect to CheckYourAnswers page screen if page answer is No and it does not change" in {
      val previousAnswer = false
      val newAnswer = false

      val urlToTest = routes.DidSomeoneGiveYouAdviceNotDeclareTaxController.onPageLoad(CheckMode).url
      val destinationRoute = routes.CheckYourAnswersController.onPageLoad.url

      testChangeAnswerRouting(previousAnswer, newAnswer, DidSomeoneGiveYouAdviceNotDeclareTaxPage, urlToTest, destinationRoute, Nil)
    }
  }
}
