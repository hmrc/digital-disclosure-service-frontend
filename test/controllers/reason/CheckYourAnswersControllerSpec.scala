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

package controllers.reason

import base.SpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.reason.CheckYourAnswersView
import viewmodels.reason.CheckYourAnswersViewModel
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import models._
import pages._

class CheckYourAnswersControllerSpec extends SpecBase {

  "CheckYourAnswers Controller" - {

    "must return OK and the correct view for a GET" in {

      val viewModel = CheckYourAnswersViewModel(SummaryList(rows = Nil) , None)

      setupMockSessionResponse(Some(emptyUserAnswers))

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad.url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckYourAnswersView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(viewModel)(request, messages).toString
    }

    "must return OK and the correct view for a GET where all the pages are populated" in {

      val reasonSet: Set[WhyAreYouMakingADisclosure] = Set(WhyAreYouMakingADisclosure.GovUkGuidance)
      val pages = List(
        PageWithValue(WhyAreYouMakingADisclosurePage, reasonSet),
        PageWithValue(WhatIsTheReasonForMakingADisclosureNowPage, "Some other"),
        PageWithValue(WhyNotBeforeNowPage, "Some reason"),
        PageWithValue(DidSomeoneGiveYouAdviceNotDeclareTaxPage, true),
        PageWithValue(PersonWhoGaveAdvicePage, "Some guy"),
        PageWithValue(AdviceBusinessesOrOrgPage, true),
        PageWithValue(AdviceBusinessNamePage, "Some business"),
        PageWithValue(AdviceProfessionPage, "Some profession"),
        PageWithValue(AdviceGivenPage, AdviceGiven("Some advice", MonthYear(12, 2012), AdviceContactPreference.No)),
        PageWithValue(WhichEmailAddressCanWeContactYouWithPage, WhichEmailAddressCanWeContactYouWith.values.head),
        PageWithValue(WhichTelephoneNumberCanWeContactYouWithPage, WhichTelephoneNumberCanWeContactYouWith.values.head),
        PageWithValue(WhatEmailAddressCanWeContactYouWithPage, "Email"),
        PageWithValue(WhatTelephoneNumberCanWeContactYouWithPage, "Telephone"),
      )
      val ua = PageWithValue.pagesToUserAnswers(pages, emptyUserAnswers).success.value

      setupMockSessionResponse(Some(ua))

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad.url)

      val result = route(application, request).value

      status(result) mustEqual OK
    }
  }
}
