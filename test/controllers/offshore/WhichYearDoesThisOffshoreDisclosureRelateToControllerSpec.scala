package controllers

import base.SpecBase
import forms.WhichYearDoesThisOffshoreDisclosureRelateToFormProvider
import models.{NormalMode, WhichYearDoesThisOffshoreDisclosureRelateTo, UserAnswers}
import navigation.{FakeOffshoreNavigator, OffshoreNavigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.WhichYearDoesThisOffshoreDisclosureRelateToPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SessionService
import views.html.offshore.WhichYearDoesThisOffshoreDisclosureRelateToView

import scala.concurrent.Future

class WhichYearDoesThisOffshoreDisclosureRelateToControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val whichYearDoesThisOffshoreDisclosureRelateToRoute = offshore.routes.WhichYearDoesThisOffshoreDisclosureRelateToController.onPageLoad(NormalMode).url

  val formProvider = new WhichYearDoesThisOffshoreDisclosureRelateToFormProvider()
  val form = formProvider()

  "WhichYearDoesThisOffshoreDisclosureRelateTo Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, whichYearDoesThisOffshoreDisclosureRelateToRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[WhichYearDoesThisOffshoreDisclosureRelateToView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual view(form, NormalMode)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(WhichYearDoesThisOffshoreDisclosureRelateToPage, WhichYearDoesThisOffshoreDisclosureRelateTo.values.toSet).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, whichYearDoesThisOffshoreDisclosureRelateToRoute)

        val view = application.injector.instanceOf[WhichYearDoesThisOffshoreDisclosureRelateToView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(WhichYearDoesThisOffshoreDisclosureRelateTo.values.toSet), NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionService = mock[SessionService]

      when(mockSessionService.set(any())(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilderWithSessionService(userAnswers = Some(emptyUserAnswers), mockSessionService)
          .overrides(
            bind[OffshoreNavigator].toInstance(new FakeOffshoreNavigator(onwardRoute))
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, whichYearDoesThisOffshoreDisclosureRelateToRoute)
            .withFormUrlEncodedBody(("value[0]", WhichYearDoesThisOffshoreDisclosureRelateTo.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, whichYearDoesThisOffshoreDisclosureRelateToRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[WhichYearDoesThisOffshoreDisclosureRelateToView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to Index for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, whichYearDoesThisOffshoreDisclosureRelateToRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

    "must redirect to Index for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, whichYearDoesThisOffshoreDisclosureRelateToRoute)
            .withFormUrlEncodedBody(("value[0]", WhichYearDoesThisOffshoreDisclosureRelateTo.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }
  }
}
