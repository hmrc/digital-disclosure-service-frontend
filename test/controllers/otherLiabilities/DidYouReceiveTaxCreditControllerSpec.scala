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
import forms.DidYouReceiveTaxCreditFormProvider
import models.{NormalMode, UserAnswers, RelatesTo}
import navigation.{FakeOtherLiabilitiesNavigator, OtherLiabilitiesNavigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages._
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SessionService
import views.html.otherLiabilities.DidYouReceiveTaxCreditView
import org.scalacheck.Arbitrary.arbitrary

import scala.concurrent.Future

class DidYouReceiveTaxCreditControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val didYouReceiveTaxCreditRoute = otherLiabilities.routes.DidYouReceiveTaxCreditController.onPageLoad(NormalMode).url

  val formProvider = new DidYouReceiveTaxCreditFormProvider()
  val form = formProvider(true, RelatesTo.AnIndividual)

  "DidYouReceiveTaxCredit Controller" - {

    "must return OK and the correct view for a GET" in {

      val areTheyTheIndividual = arbitrary[Boolean].sample.value 
      val entity = arbitrary[RelatesTo].sample.value  

      val userAnswers = (for {
        ua <- UserAnswers("id").set(AreYouTheIndividualPage, areTheyTheIndividual)
        updatedUa <- ua.set(RelatesToPage, entity)  
      } yield updatedUa).success.value  

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, didYouReceiveTaxCreditRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[DidYouReceiveTaxCreditView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, areTheyTheIndividual, entity)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val areTheyTheIndividual = arbitrary[Boolean].sample.value 
      val entity = arbitrary[RelatesTo].sample.value 

      val userAnswers = (for {
        ua <- UserAnswers("id").set(AreYouTheIndividualPage, areTheyTheIndividual)
        uaRelatesToPage <- ua.set(RelatesToPage, entity) 
        updatedUa <- uaRelatesToPage.set(DidYouReceiveTaxCreditPage, true)
      } yield updatedUa).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, didYouReceiveTaxCreditRoute)

        val view = application.injector.instanceOf[DidYouReceiveTaxCreditView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(true), NormalMode, areTheyTheIndividual, entity)(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionService = mock[SessionService]

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilderWithSessionService(userAnswers = Some(emptyUserAnswers), mockSessionService)
          .overrides(
            bind[OtherLiabilitiesNavigator].toInstance(new FakeOtherLiabilitiesNavigator(onwardRoute))
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, didYouReceiveTaxCreditRoute)
            .withFormUrlEncodedBody(("value", true.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val areTheyTheIndividual = arbitrary[Boolean].sample.value 
      val entity = arbitrary[RelatesTo].sample.value 

      val userAnswers = (for {
        ua <- UserAnswers("id").set(AreYouTheIndividualPage, areTheyTheIndividual)
        updatedUa <- ua.set(RelatesToPage, entity) 
      } yield updatedUa).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, didYouReceiveTaxCreditRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[DidYouReceiveTaxCreditView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, areTheyTheIndividual, entity)(request, messages(application)).toString
      }
    }

    "must redirect to Index for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, didYouReceiveTaxCreditRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, didYouReceiveTaxCreditRoute)
            .withFormUrlEncodedBody(("value", true.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }
  }
}
