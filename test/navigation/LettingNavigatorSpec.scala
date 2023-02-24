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

package navigation

import base.SpecBase
import models.{LettingProperty, NormalMode, UserAnswers}
import pages.{LettingPropertyPage, Page, PropertyFirstLetOutPage, RentalAddressLookupPage}
import controllers.letting.routes

import java.time.LocalDate

class LettingNavigatorSpec extends SpecBase {

  val navigator = new LettingNavigator

  "LettingNavitator" - {

    "In normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, 0, NormalMode, UserAnswers("id")) mustBe controllers.routes.IndexController.onPageLoad
      }

      "must go from RentalAddressLookupPage to PropertyFirstLetOutController" in {
        val index = 0
        val lettingProperty = LettingProperty(dateFirstLetOut = Some(LocalDate.now().minusDays(1)))
        val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value

        navigator.nextPage(RentalAddressLookupPage, index, NormalMode,ua) mustBe routes.PropertyFirstLetOutController.onPageLoad(index, NormalMode)
      }

      "must go from PropertyFirstLetOutPage to PropertyStoppedBeingLetOutController" in {
        val index = 0
        val lettingProperty = LettingProperty(stoppedBeingLetOut = Some(true))
        val ua = UserAnswers(userAnswersId).addToSeq(LettingPropertyPage, lettingProperty).success.value

        navigator.nextPage(PropertyFirstLetOutPage, index, NormalMode, ua) mustBe routes.PropertyStoppedBeingLetOutController.onPageLoad(index, NormalMode)
      }





    }
  }

}
