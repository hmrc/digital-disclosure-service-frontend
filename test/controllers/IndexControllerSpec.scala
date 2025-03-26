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
import generators.Generators
import models._
import models.address._
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import pages._
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.IndexView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class IndexControllerSpec extends SpecBase with Generators {

  def onwardRoute: Call = Call("GET", "/foo")
  val view: IndexView   = application.injector.instanceOf[IndexView]

  "Index Controller" - {

    "must return a view with a link to the next page from the navigator where the disclosure journey is configured" in {

      setupMockSessionResponse()
      when(mockSessionService.getIndividualUserAnswers(any(), any(), any())(any())).thenReturn(Future.successful(None))
      val applicationWithFakeNavigator = applicationBuilder
        .configure(
          "features.full-disclosure-journey" -> true
        )
        .overrides(
          bind[Navigator].toInstance(new FakeNavigator(onwardRoute))
        )
        .build()

      val request = FakeRequest(GET, routes.IndexController.onPageLoad.url)

      val result = route(applicationWithFakeNavigator, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual view(onwardRoute.url, isAgent = false)(request, messages).toString
    }

    "must set user answers where one doesn't exist" in {

      val request = FakeRequest(GET, routes.IndexController.onPageLoad.url)
      setupMockSessionResponse()

      route(application, request).value

      val sessionRepo = application.injector.instanceOf[SessionRepository]
      sessionRepo.get("id", "session-123").map(uaOpt => uaOpt mustBe Symbol("defined"))
    }

    "must retain existing user answers where one exists" in {

      for {
        userAnswers <- arbitrary[UserAnswers]
      } yield {
        val request = FakeRequest(GET, routes.IndexController.onPageLoad.url)
        setupMockSessionResponse(Some(userAnswers))

        route(application, request).value

        val sessionRepo = application.injector.instanceOf[SessionRepository]
        sessionRepo.get("id", "session-123").map(uaOpt => uaOpt mustBe Some(userAnswers))
      }
    }

  }

  val address: Address                                =
    Address("line 1", Some("line 2"), Some("line 3"), Some("line 4"), Some("postcode"), Country("GBR"))
  val contactSet: Set[HowWouldYouPreferToBeContacted] = Set(HowWouldYouPreferToBeContacted.Email)
  val incomeSet: Set[IncomeOrGainSource]              = Set(IncomeOrGainSource.Dividends)
  val completeUserAnswers: UserAnswers                = (for {
    ua1     <- UserAnswers("id", "session-123").set(ReceivedALetterPage, true)
    ua2     <- ua1.set(ReceivedALetterPage, false)
    ua3     <- ua2.set(RelatesToPage, RelatesTo.ATrust)
    ua4     <- ua3.set(AreYouTheEntityPage, AreYouTheEntity.YesIAm)
    ua5     <- ua4.set(OffshoreLiabilitiesPage, true)
    ua6     <- ua5.set(OnshoreLiabilitiesPage, true)
    ua7     <- ua6.set(WhatIsTheTrustNamePage, "Some trust")
    ua8     <- ua7.set(TrustAddressLookupPage, address)
    ua9     <- ua8.set(WhatIsYourFullNamePage, "My name")
    ua10    <- ua9.set(HowWouldYouPreferToBeContactedPage, contactSet)
    ua11    <- ua10.set(YourEmailAddressPage, "My email")
    ua12    <- ua11.set(IncomeOrGainSourcePage, incomeSet)
    finalUa <- ua12.set(YourAddressLookupPage, address)
  } yield finalUa).success.value
}
