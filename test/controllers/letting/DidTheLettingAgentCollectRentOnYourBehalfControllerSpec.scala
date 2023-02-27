package controllers

import base.SpecBase
import forms.DidTheLettingAgentCollectRentOnYourBehalfFormProvider
import models.{NormalMode, UserAnswers, LettingProperty}
import navigation.{FakeLettingNavigator, LettingNavigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.LettingPropertyPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SessionService
import views.html.letting.DidTheLettingAgentCollectRentOnYourBehalfView

import scala.concurrent.Future

class DidTheLettingAgentCollectRentOnYourBehalfControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new DidTheLettingAgentCollectRentOnYourBehalfFormProvider()
  val form = formProvider()

  lazy val didTheLettingAgentCollectRentOnYourBehalfRoute = letting.routes.DidTheLettingAgentCollectRentOnYourBehalfController.onPageLoad(0, NormalMode).url

  "DidTheLettingAgentCollectRentOnYourBehalf Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, didTheLettingAgentCollectRentOnYourBehalfRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[DidTheLettingAgentCollectRentOnYourBehalfView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, 0, NormalMode)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val lettingProperty = LettingProperty(didTheLettingAgentCollectRentOnYourBehalf = Some(true))

      val userAnswers = UserAnswers(userAnswersId)
        .setBySeqIndex(LettingPropertyPage, 0, lettingProperty).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, didTheLettingAgentCollectRentOnYourBehalfRoute)

        val view = application.injector.instanceOf[DidTheLettingAgentCollectRentOnYourBehalfView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(true), 0, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionService = mock[SessionService]

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilderWithSessionService(userAnswers = Some(emptyUserAnswers), mockSessionService)
          .overrides(
            bind[LettingNavigator].toInstance(new FakeLettingNavigator(onwardRoute))
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, didTheLettingAgentCollectRentOnYourBehalfRoute)
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
          FakeRequest(POST, didTheLettingAgentCollectRentOnYourBehalfRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[DidTheLettingAgentCollectRentOnYourBehalfView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, 0, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to Index for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, didTheLettingAgentCollectRentOnYourBehalfRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

    "must redirect to Index for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, didTheLettingAgentCollectRentOnYourBehalfRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }
  }
}
