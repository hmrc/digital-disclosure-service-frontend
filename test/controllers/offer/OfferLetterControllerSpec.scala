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

package controllers.offer

import base.SpecBase
import forms.OfferLetterFormProvider
import models.address._
import models.{AreYouTheEntity, RelatesTo, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages._
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.DisclosureSubmissionService
import uk.gov.hmrc.http.HeaderCarrier
import views.html.OfferLetterView

import scala.concurrent.{ExecutionContext, Future}

class OfferLetterControllerSpec extends SpecBase with MockitoSugar {

  val formProvider = new OfferLetterFormProvider()
  val form: Form[BigInt] = formProvider()

  def onwardRoute: Call = Call("GET", "/foo")

  val validAnswer = 0

  lazy val offerLetterRoute: String = controllers.routes.OfferLetterController.onPageLoad.url

  "OfferLetter Controller" - {

    "must return OK and the correct view for an empty user answers" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, offerLetterRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[OfferLetterView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, "", "", 0, "individual", areTheyTheIndividual = false)(request, messages).toString
    }

    "must return OK and the correct view for an individual filling it out for themselves" in {

      val userAnswers = (for {
        ua <- emptyUserAnswers.set(RelatesToPage, RelatesTo.AnIndividual)
        ua2 <- ua.set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
        ua3 <- ua2.set(WhatIsYourFullNamePage, "My name")
        ua4 <- ua3.set(YourAddressLookupPage, Address("my line 1", Some("line 2"), Some("line 3"), Some("line 4"), Some("postcode"), Country("GB")))
      } yield ua4).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, offerLetterRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[OfferLetterView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, "My name", "my line 1<br>line 2<br>line 3<br>line 4<br>postcode<br>United Kingdom", 0, "individual", areTheyTheIndividual = true)(request, messages).toString
    }

    "must return OK and the correct view for an individual by an agent" in {

      val userAnswers = (for {
        ua <- emptyUserAnswers.set(RelatesToPage, RelatesTo.AnIndividual)
        ua2 <- ua.set(AreYouTheEntityPage, AreYouTheEntity.IAmAnAccountantOrTaxAgent)
        ua3 <- ua2.set(WhatIsYourFullNamePage, "My name")
        ua4 <- ua3.set(IndividualAddressLookupPage, Address("ind line 1", Some("line 2"), Some("line 3"), Some("line 4"), Some("postcode"), Country("GB")))
        ua5 <- ua4.set(WhatIsTheIndividualsFullNamePage, "Individual's name")
      } yield ua5).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, offerLetterRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[OfferLetterView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, "Individual's name", "ind line 1<br>line 2<br>line 3<br>line 4<br>postcode<br>United Kingdom", 0, "individual", areTheyTheIndividual = false)(request, messages).toString
    }

    "must return OK and the correct view for a company" in {

      val userAnswers = (for {
        ua <- emptyUserAnswers.set(RelatesToPage, RelatesTo.ACompany)
        ua2 <- ua.set(WhatIsYourFullNamePage, "My name")
        ua3 <- ua2.set(CompanyAddressLookupPage, Address("com line 1", Some("line 2"), Some("line 3"), Some("line 4"), Some("postcode"), Country("GB")))
        ua4 <- ua3.set(WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutPage, "Company's name")
      } yield ua4).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, offerLetterRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[OfferLetterView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, "Company's name", "com line 1<br>line 2<br>line 3<br>line 4<br>postcode<br>United Kingdom", 0, "company", areTheyTheIndividual = false)(request, messages).toString
    }

    "must return OK and the correct view for a trust" in {

      val userAnswers = (for {
        ua <- emptyUserAnswers.set(RelatesToPage, RelatesTo.ATrust)
        ua2 <- ua.set(WhatIsYourFullNamePage, "My name")
        ua3 <- ua2.set(TrustAddressLookupPage, Address("trust line 1", Some("line 2"), Some("line 3"), Some("line 4"), Some("postcode"), Country("GB")))
        ua4 <- ua3.set(WhatIsTheTrustNamePage, "Trust's name")
      } yield ua4).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, offerLetterRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[OfferLetterView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, "Trust's name", "trust line 1<br>line 2<br>line 3<br>line 4<br>postcode<br>United Kingdom", 0, "trust", "My name", areTheyTheIndividual = false)(request, messages).toString
    }
    
    "must return OK and the correct view for an llp" in {

      val userAnswers = (for {
        ua <- emptyUserAnswers.set(RelatesToPage, RelatesTo.ALimitedLiabilityPartnership)
        ua2 <- ua.set(WhatIsYourFullNamePage, "My name")
        ua3 <- ua2.set(LLPAddressLookupPage, Address("llp line 1", Some("line 2"), Some("line 3"), Some("line 4"), Some("postcode"), Country("GB")))
        ua4 <- ua3.set(WhatIsTheLLPNamePage, "LLP's name")
      } yield ua4).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, offerLetterRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[OfferLetterView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, "LLP's name", "llp line 1<br>line 2<br>line 3<br>line 4<br>postcode<br>United Kingdom", 0, "llp", "My name", areTheyTheIndividual = false)(request, messages).toString
    }

    "must return OK and the correct view for an estate" in {

      val userAnswers = (for {
        ua <- emptyUserAnswers.set(RelatesToPage, RelatesTo.AnEstate)
        ua2 <- ua.set(WhatIsYourFullNamePage, "My name")
        ua3 <- ua2.set(EstateAddressLookupPage, Address("estate line 1", Some("line 2"), Some("line 3"), Some("line 4"), Some("postcode"), Country("GB")))
        ua4 <- ua3.set(WhatWasTheNameOfThePersonWhoDiedPage, "Estate's name")
      } yield ua4).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, offerLetterRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[OfferLetterView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form, "Estate's name", "estate line 1<br>line 2<br>line 3<br>line 4<br>postcode<br>United Kingdom", 0, "estate", "My name", areTheyTheIndividual = false)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId, "session-123").set(OfferLetterPage, BigInt(validAnswer)).success.value

      setupMockSessionResponse(Some(userAnswers))

      val request = FakeRequest(GET, offerLetterRoute)

      val view = application.injector.instanceOf[OfferLetterView]

      val result = route(application, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(form.fill(BigInt(validAnswer)), "", "", 0, "individual", areTheyTheIndividual = false)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {
      
      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)
      setupMockSessionResponse(Some(emptyUserAnswers))

      object FakeDisclosureSubmissionService extends DisclosureSubmissionService {
        def submitDisclosure(userAnswers: UserAnswers)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[String] =
          Future.successful("Reference")
      }

      val applicationWithOverrides = applicationBuilder.overrides(
          bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
          bind[DisclosureSubmissionService].toInstance(FakeDisclosureSubmissionService)
        ).build()

      val request =
        FakeRequest(POST, offerLetterRoute)
          .withFormUrlEncodedBody(("value", validAnswer.toString))

      val result = route(applicationWithOverrides, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, offerLetterRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[OfferLetterView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, "", "", 0, "individual", areTheyTheIndividual = false)(request, messages).toString
    }

    "must redirect to Index for a GET if no existing data is found" in {

      setupMockSessionResponse()

      val request = FakeRequest(GET, offerLetterRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }

    "must redirect to Index for a POST if no existing data is found" in {

      setupMockSessionResponse()

      val request =
        FakeRequest(POST, offerLetterRoute)
          .withFormUrlEncodedBody(("value", validAnswer.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.IndexController.onPageLoad.url
    }
  }
}
