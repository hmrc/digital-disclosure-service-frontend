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

package viewmodels.checkAnswers

import base.SpecBase
import pages._
import models._


class OnshoreLiabilitiesSummarySpec extends SpecBase {

  "OnshoreLiabilitiesSummary.row" - {
    "must return a row where the user selects No for offshore liabilities" - {
      val ua = UserAnswers("id").set(OffshoreLiabilitiesPage, OffshoreLiabilities.IDoNotWantTo).success.value
      val row = OnshoreLiabilitiesSummary.row(ua).success.value

      row.key mustBe "onshoreLiabilities.default.checkYourAnswersLabel"
      row.value mustBe ValueViewModel(HtmlContent(HtmlFormat.escape(messages("onshoreLiabilities.yes"))))
      row.actions mustBe Seq(ActionItemViewModel("site.change", routes.OffshoreLiabilitiesController.onPageLoad(CheckMode).url).withVisuallyHiddenText(messages("offshoreLiabilities.change.hidden")))
    }

    "must return a row where the user selects Yes for offshore liabilities and selects Yes for onshore liabilities" - {
      val ua = (for {
        offAnswer <- UserAnswers("id").set(OffshoreLiabilitiesPage, OffshoreLiabilities.IWantTo)
        onAnswer <- offAnswer.set(OnshoreLiabilitiesPage, OffshoreLiabilities.IWantTo)
      } yield onAnswer).success.value
      val row = OnshoreLiabilitiesSummary.row(ua).success.value

      row.key mustBe "onshoreLiabilities.default.checkYourAnswersLabel"
      row.value mustBe ValueViewModel(HtmlContent(HtmlFormat.escape(messages("onshoreLiabilities.yes"))))
      row.actions mustBe Seq(ActionItemViewModel("site.change", routes.OffshoreLiabilitiesController.onPageLoad(CheckMode).url).withVisuallyHiddenText(messages("offshoreLiabilities.change.hidden")))
    }

    "must return a row where the user selects Yes for offshore liabilities and selects No for onshore liabilities" - {
      val ua = (for {
        offAnswer <- UserAnswers("id").set(OffshoreLiabilitiesPage, OffshoreLiabilities.IWantTo)
        onAnswer <- offAnswer.set(OnshoreLiabilitiesPage, OffshoreLiabilities.IDoNotWantTo)
      } yield onAnswer).success.value
      val row = OnshoreLiabilitiesSummary.row(ua).success.value

      row.key mustBe "onshoreLiabilities.default.checkYourAnswersLabel"
      row.value mustBe ValueViewModel(HtmlContent(HtmlFormat.escape(messages("onshoreLiabilities.yes"))))
      row.actions mustBe Seq(ActionItemViewModel("site.change", routes.OffshoreLiabilitiesController.onPageLoad(CheckMode).url).withVisuallyHiddenText(messages("offshoreLiabilities.change.hidden")))
    }

  }
  
}
