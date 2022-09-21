/*
 * Copyright 2022 HM Revenue & Customs
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

package navigation

import base.SpecBase
import controllers.notification.routes
import pages._
import models._

import scala.util.{Success, Failure}

class NotificationNavigatorSpec extends SpecBase {

  val navigator = new NotificationNavigator

  "Notification Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, UserAnswers("id")) mustBe controllers.routes.IndexController.onPageLoad
      }

      "must go from the ReceivedALetter page to the RelatesTo controller when the user selects No" in {
        UserAnswers("id").set(ReceivedALetterPage, false) match {
          case Success(ua) => navigator.nextPage(ReceivedALetterPage, NormalMode, ua) mustBe routes.RelatesToController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the ReceivedALetter page to the LetterReference controller when the user selects Yes" in {
        UserAnswers("id").set(ReceivedALetterPage, true) match {
          case Success(ua) => navigator.nextPage(ReceivedALetterPage, NormalMode, ua) mustBe routes.LetterReferenceController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the LetterReferencePage page to the RelatesTo controller when the user enter any data" in {
        UserAnswers("id").set(LetterReferencePage, "test") match {
          case Success(ua) => navigator.nextPage(LetterReferencePage, NormalMode, ua) mustBe routes.RelatesToController.onPageLoad(NormalMode)
          case Failure(e) => throw e
        }
      }

      "must go from the RelatesTo page to the AreYouTheIndividual controller" in {
        navigator.nextPage(RelatesToPage, NormalMode, UserAnswers("id")) mustBe routes.AreYouTheIndividualController.onPageLoad(NormalMode)
      }
    }

    "must go from the AreYouTheIndividual page to the OffshoreLiabilities controller when the user selects Yes" in {
      UserAnswers("id").set(AreYouTheIndividualPage, AreYouTheIndividual.Yes) match {
        case Success(ua) => navigator.nextPage(AreYouTheIndividualPage, NormalMode, ua) mustBe routes.OffshoreLiabilitiesController.onPageLoad(NormalMode)
        case Failure(e) => throw e
      }
    }

    "must go from the AreYouTheIndividual page to the OffshoreLiabilities controller when the user selects No" in {
      UserAnswers("id").set(AreYouTheIndividualPage, AreYouTheIndividual.No) match {
        case Success(ua) => navigator.nextPage(AreYouTheIndividualPage, NormalMode, ua) mustBe routes.OffshoreLiabilitiesController.onPageLoad(NormalMode)
        case Failure(e) => throw e
      }
    }

    "must go from the OnshoreLiabilities page to the WhatIsYourFullName controller when the user selects Yes" in {
      UserAnswers("id").set(OnshoreLiabilitiesPage, OnshoreLiabilities.Yes) match {
        case Success(ua) => navigator.nextPage(OnshoreLiabilitiesPage, NormalMode, ua) mustBe routes.WhatIsYourFullNameController.onPageLoad(NormalMode)
        case Failure(e) => throw e
      }
    }

    "must go from the OnshoreLiabilities page to the WhatIsYourFullName controller when the user selects No" in {
      UserAnswers("id").set(OnshoreLiabilitiesPage, OnshoreLiabilities.No) match {
        case Success(ua) => navigator.nextPage(OnshoreLiabilitiesPage, NormalMode, ua) mustBe routes.WhatIsYourFullNameController.onPageLoad(NormalMode)
        case Failure(e) => throw e
      }
    }

    "in Check mode" - {

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, UserAnswers("id")) mustBe controllers.routes.CheckYourAnswersController.onPageLoad
      }
    }
  }
}
