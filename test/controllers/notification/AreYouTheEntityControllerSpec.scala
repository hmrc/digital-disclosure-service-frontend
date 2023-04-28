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
import forms.AreYouTheEntityFormProvider
import models.{RelatesTo, NormalMode, AreYouTheEntity, UserAnswers}
import navigation.{FakeNotificationNavigator, NotificationNavigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{AreYouTheEntityPage, RelatesToPage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SessionService
import views.html.notification.AreYouTheEntityView
import pages.notification.SectionPages

import scala.concurrent.Future

class AreYouTheEntityControllerSpec extends SpecBase with MockitoSugar with SectionPages {

  def onwardRoute = Call("GET", "/foo")

  lazy val areYouTheEntityRoute = notification.routes.AreYouTheEntityController.onPageLoad(NormalMode).url

  val formProvider = new AreYouTheEntityFormProvider()

  "AreYouTheEntity Controller" - {

    RelatesTo.values.map{ entity => 
      s"must return OK and the correct view for a $entity" in {
        val userAnswers = UserAnswers(userAnswersId, "session-123").set(RelatesToPage, entity).success.value
        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val form = formProvider(entity)

        running(application) {
          val request = FakeRequest(GET, areYouTheEntityRoute)

          val result = route(application, request).value

          val view = application.injector.instanceOf[AreYouTheEntityView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(form, NormalMode, entity, false)(request, messages(application)).toString
        }
      }
    }

    RelatesTo.values.map{ entity => 
      s"must populate the view correctly for a $entity when the question has previously been answered" in {
        val userAnswers = (for {
          initialUa <- UserAnswers(userAnswersId, "session-123").set(AreYouTheEntityPage, AreYouTheEntity.values.head)
          ua <- initialUa.set(RelatesToPage, entity)
        } yield ua).success.value
        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val form = formProvider(entity)

        running(application) {
          val request = FakeRequest(GET, areYouTheEntityRoute)

          val view = application.injector.instanceOf[AreYouTheEntityView]

          val result = route(application, request).value

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(form.fill(AreYouTheEntity.values.head), NormalMode, entity, false)(request, messages(application)).toString
        }
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
          FakeRequest(POST, areYouTheEntityRoute)
            .withFormUrlEncodedBody(("value", AreYouTheEntity.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, areYouTheEntityRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val form = formProvider(RelatesTo.AnIndividual)
        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[AreYouTheEntityView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, RelatesTo.AnIndividual, false)(request, messages(application)).toString
      }
    }

    "must redirect to Index for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, areYouTheEntityRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, areYouTheEntityRoute)
            .withFormUrlEncodedBody(("value", AreYouTheEntity.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.IndexController.onPageLoad.url
      }
    }
  }

  "changedPages" - {

    import controllers.notification.AreYouTheEntityController

    "return nil where the answer hasn't changed" in {

      val existingUserAnswers = (for {
        ua <- UserAnswers(userAnswersId, "session-123").set(RelatesToPage, RelatesTo.AnIndividual)
        finalUa <- ua.set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
      } yield finalUa).success.value

      val application = applicationBuilder(userAnswers = Some(existingUserAnswers)).build()
      val sut = application.injector.instanceOf[AreYouTheEntityController]

      sut.changedPages(existingUserAnswers, AreYouTheEntity.YesIAm) mustEqual ((Nil, false))
    }

    "return about you, about the individual and organisation pages where it's an individual, the answer has changed, and it's not YesIAm" in {

      val existingUserAnswers = (for {
        ua <- UserAnswers(userAnswersId, "session-123").set(RelatesToPage, RelatesTo.AnIndividual)
        finalUa <- ua.set(AreYouTheEntityPage, AreYouTheEntity.IAmAnAccountantOrTaxAgent)
      } yield finalUa).success.value

      val application = applicationBuilder(userAnswers = Some(existingUserAnswers)).build()
      val sut = application.injector.instanceOf[AreYouTheEntityController]

      sut.changedPages(existingUserAnswers, AreYouTheEntity.YesIAm) mustEqual ((aboutYouPages ::: aboutIndividualPages ::: areYouTheOrganisationPages, true))
    }

    "return about you pages where it's an individual, the answer has changed, and it used to be YesIAm" in {

      val existingUserAnswers = (for {
        ua <- UserAnswers(userAnswersId, "session-123").set(RelatesToPage, RelatesTo.AnIndividual)
        finalUa <- ua.set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
      } yield finalUa).success.value

      val application = applicationBuilder(userAnswers = Some(existingUserAnswers)).build()
      val sut = application.injector.instanceOf[AreYouTheEntityController]

      sut.changedPages(existingUserAnswers, AreYouTheEntity.IAmAnAccountantOrTaxAgent) mustEqual ((aboutYouPages, true))
    }

    "return areYouTheOrganisationPages where it's not an individual, the answer has changed, and it used to be IAmAnAccountantOrTaxAgent" in {

      val existingUserAnswers = (for {
        ua <- UserAnswers(userAnswersId, "session-123").set(RelatesToPage, RelatesTo.ACompany)
        finalUa <- ua.set(AreYouTheEntityPage, AreYouTheEntity.IAmAnAccountantOrTaxAgent)
      } yield finalUa).success.value

      val application = applicationBuilder(userAnswers = Some(existingUserAnswers)).build()
      val sut = application.injector.instanceOf[AreYouTheEntityController]

      sut.changedPages(existingUserAnswers, AreYouTheEntity.YesIAm) mustEqual ((areYouTheOrganisationPages, true))
    }

  }

}
