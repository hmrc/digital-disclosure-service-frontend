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
import forms.PropertyAddedFormProvider
import models.address.{Address, Country}
import models.{LettingProperty, NormalMode, UserAnswers}
import navigation.{FakeOnshoreNavigator, OnshoreNavigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.LettingPropertyPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SessionService
import views.html.onshore.PropertyAddedView
import viewmodels.onshore.LettingPropertyModel

import scala.concurrent.Future

class PropertyAddedControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new PropertyAddedFormProvider()
  val form = formProvider()

  lazy val propertyAddedRoute = onshore.routes.PropertyAddedController.onPageLoad(NormalMode).url

  val properties = Seq()
  val index = 0

  "PropertyAdded Controller" - {

    "must return OK and the correct view for a GET" in {


      val address: Address = Address(
        line1 = "Line 1",
        postcode = Some("AA112AA"),
        line2 = None,
        line3 = None,
        line4 = None,
        country = Country("AA")
      )
      val property = LettingProperty(address = Some(address))
      val userAnswers = UserAnswers("id").addToSeq(LettingPropertyPage, property).success.value
  
      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, propertyAddedRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[PropertyAddedView]

        val properties = LettingPropertyModel.row(Seq(property), NormalMode)(messages(application))

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, properties, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to the Property Address page if no property has been inserted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, propertyAddedRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual letting.routes.RentalAddressLookupController.lookupAddress(0, NormalMode).url
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
          FakeRequest(POST, propertyAddedRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, propertyAddedRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[PropertyAddedView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, properties, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to Index for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, propertyAddedRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

    "must redirect to Index for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, propertyAddedRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

    "must redirect to the address of the first property page if remove method is called and there are no more properties" in {
      val removePropertyRoute = onshore.routes.PropertyAddedController.remove(index, NormalMode).url

      val mockSessionService = mock[SessionService]

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilderWithSessionService(userAnswers = Some(emptyUserAnswers), mockSessionService)
          .overrides(
            bind[OnshoreNavigator].toInstance(new FakeOnshoreNavigator(onwardRoute))
          )
          .build()

      running(application) {
        val request = FakeRequest(GET, removePropertyRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual letting.routes.RentalAddressLookupController.lookupAddress(index, NormalMode).url
      }
    }

    "must redirect to the same page if remove method is called and there are still properties" in {
      val removePropertyRoute = onshore.routes.PropertyAddedController.remove(index, NormalMode).url

      val mockSessionService = mock[SessionService]

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)

      val address: Address = Address(
        line1 = "Line 1",
        postcode = Some("AA112AA"),
        line2 = None,
        line3 = None,
        line4 = None,
        country = Country("AA")
      )

      val address2: Address = Address(
        line1 = "Line 2",
        postcode = Some("AA112AA"),
        line2 = None,
        line3 = None,
        line4 = None,
        country = Country("AA")
      )

      val property = LettingProperty(address = Some(address))
      val property2 = LettingProperty(address = Some(address2))

      val userAnswers = UserAnswers("id")
        .addToSeq(LettingPropertyPage, property).success.value
        .addToSeq(LettingPropertyPage, property2).success.value

      val application =
        applicationBuilderWithSessionService(userAnswers = Some(userAnswers), mockSessionService)
          .overrides(
            bind[OnshoreNavigator].toInstance(new FakeOnshoreNavigator(onwardRoute))
          )
          .build()

      running(application) {
        val request = FakeRequest(GET, removePropertyRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onshore.routes.PropertyAddedController.onPageLoad(NormalMode).url
      }
    }



  }
}
