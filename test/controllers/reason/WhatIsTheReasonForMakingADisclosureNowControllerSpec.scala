package controllers

import base.SpecBase
import forms.WhatIsTheReasonForMakingADisclosureNowFormProvider
import models.{NormalMode, UserAnswers}
import navigation.{FakeReasonNavigator, ReasonNavigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.WhatIsTheReasonForMakingADisclosureNowPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SessionService
import views.html.reason.WhatIsTheReasonForMakingADisclosureNowView

import scala.concurrent.Future

class WhatIsTheReasonForMakingADisclosureNowControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new WhatIsTheReasonForMakingADisclosureNowFormProvider()
  val form = formProvider()

  lazy val whatIsTheReasonForMakingADisclosureNowRoute = reason.routes.WhatIsTheReasonForMakingADisclosureNowController.onPageLoad(NormalMode).url

  "WhatIsTheReasonForMakingADisclosureNow Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, whatIsTheReasonForMakingADisclosureNowRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[WhatIsTheReasonForMakingADisclosureNowView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(WhatIsTheReasonForMakingADisclosureNowPage, "answer").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, whatIsTheReasonForMakingADisclosureNowRoute)

        val view = application.injector.instanceOf[WhatIsTheReasonForMakingADisclosureNowView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill("answer"), NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionService = mock[SessionService]

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilderWithSessionService(userAnswers = Some(emptyUserAnswers), mockSessionService)
          .overrides(
            bind[ReasonNavigator].toInstance(new FakeReasonNavigator(onwardRoute))
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, whatIsTheReasonForMakingADisclosureNowRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, whatIsTheReasonForMakingADisclosureNowRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[WhatIsTheReasonForMakingADisclosureNowView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to Index for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, whatIsTheReasonForMakingADisclosureNowRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

    "must redirect to Index for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, whatIsTheReasonForMakingADisclosureNowRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }
  }
}
