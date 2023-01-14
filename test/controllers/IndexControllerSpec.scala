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
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.IndexView
import repositories.SessionRepository
import org.scalacheck.Arbitrary.arbitrary
import models._
import models.address._
import scala.concurrent.ExecutionContext.Implicits.global
import generators.Generators
import pages._

class IndexControllerSpec extends SpecBase with Generators {

  "Index Controller" - {

    "must return a view with a link to the MakeANotificationOrDisclosureController where the disclosure journey is configured" in {

      val url = "/digital-disclosure/using-this-service"

      val application = applicationBuilder(userAnswers = None)
        .configure(
          "features.full-disclosure-journey" -> true,
        ).build()

      running(application) {
        val request = FakeRequest(GET, routes.IndexController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[IndexView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual view(url)(request, messages(application)).toString
      }
    }

    "must return a view with a link to the ReceivedALetterController where the disclosure journey is NOT configured and the notification journey is NOT complete" in {

      val url = controllers.notification.routes.ReceivedALetterController.onPageLoad(NormalMode).url

      val application = applicationBuilder(userAnswers = None)
        .configure(
          "features.full-disclosure-journey" -> false,
        ).build()

      running(application) {
        val request = FakeRequest(GET, routes.IndexController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[IndexView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual view(url)(request, messages(application)).toString
      }
    }

    "must return a view with a link to the ReceivedALetterController where the disclosure journey is NOT configured and the notification journey is complete" in {

      val url = controllers.notification.routes.CheckYourAnswersController.onPageLoad.url

      val application = applicationBuilder(userAnswers = Some(completeUserAnswers))
        .configure(
          "features.full-disclosure-journey" -> false,
        ).build()

      running(application) {
        val request = FakeRequest(GET, routes.IndexController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[IndexView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual view(url)(request, messages(application)).toString
      }
    }

    "must set user answers where one doesn't exist" in {

      val request = FakeRequest(GET, routes.IndexController.onPageLoad.url)
      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        route(application, request).value

        val sessionRepo = application.injector.instanceOf[SessionRepository]
        sessionRepo.get("id").map(uaOpt => uaOpt mustBe 'defined)
      }
    }

    "must retain existing user answers where one exists" in {

      for {
        userAnswers <- arbitrary[UserAnswers]
      } yield {
        val request = FakeRequest(GET, routes.IndexController.onPageLoad.url)
        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        running(application) {
          route(application, request).value
          
          val sessionRepo = application.injector.instanceOf[SessionRepository]
          sessionRepo.get("id").map(uaOpt => uaOpt mustBe Some(userAnswers))
        }
      }
    }
    
  }

  val address = Address("line 1", Some("line 2"), Some("line 3"), Some("line 4"), Some("postcode"), Country("GBR"))


  val contactSet: Set[HowWouldYouPreferToBeContacted] = Set(HowWouldYouPreferToBeContacted.Email)
  val completeUserAnswers = (for {
    ua1 <- UserAnswers("id").set(ReceivedALetterPage, true)
    ua2 <- ua1.set(ReceivedALetterPage, false)
    ua3 <- ua2.set(RelatesToPage, RelatesTo.ATrust)
    ua4 <- ua3.set(AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage, true)
    ua5 <- ua4.set(OffshoreLiabilitiesPage, true)
    ua6 <- ua5.set(OnshoreLiabilitiesPage, true)
    ua7 <- ua6.set(WhatIsTheTrustNamePage, "Some trust")
    ua8 <- ua7.set(TrustAddressLookupPage, address)
    ua9 <- ua8.set(WhatIsYourFullNamePage, "My name")
    ua10 <- ua9.set(HowWouldYouPreferToBeContactedPage, contactSet)
    ua11 <- ua10.set(YourEmailAddressPage, "My email")
    finalUa <- ua11.set(YourAddressLookupPage, address)
  } yield finalUa).success.value
}
