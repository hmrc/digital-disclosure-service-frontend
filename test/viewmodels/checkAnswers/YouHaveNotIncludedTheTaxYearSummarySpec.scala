/*
 * Copyright 2026 HM Revenue & Customs
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
import models.{TaxYearStarting, UserAnswers}
import pages.{WhichYearsPage, YouHaveNotIncludedTheTaxYearPage}
import play.api.i18n.Messages
import viewmodels.RevealFullText

class YouHaveNotIncludedTheTaxYearSummarySpec extends SpecBase {

  lazy val revealFullText     = application.injector.instanceOf[RevealFullText]
  implicit val mess: Messages = messages

  "YouHaveNotIncludedTheTaxYearSummary" - {

    "must return None when there are no offshore tax years" in {
      val userAnswers = UserAnswers("id", "session-123")
      YouHaveNotIncludedTheTaxYearSummary.row(userAnswers, revealFullText) mustBe None
    }

    "must return None when the page has no answer" in {
      val userAnswers = UserAnswers("id", "session-123")
        .set(WhichYearsPage, Set[models.OffshoreYears](TaxYearStarting(2020))).success.value
      YouHaveNotIncludedTheTaxYearSummary.row(userAnswers, revealFullText) mustBe None
    }

    "must return a row when both years and page answer exist" in {
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(WhichYearsPage, Set[models.OffshoreYears](TaxYearStarting(2020)))
        ua2 <- ua.set(YouHaveNotIncludedTheTaxYearPage, "Some reason")
      } yield ua2).success.value

      val result = YouHaveNotIncludedTheTaxYearSummary.row(userAnswers, revealFullText)
      result mustBe defined
    }

    "must handle long text that exceeds the reveal threshold" in {
      val longText = "A" * 200
      val userAnswers = (for {
        ua  <- UserAnswers("id", "session-123").set(WhichYearsPage, Set[models.OffshoreYears](TaxYearStarting(2020)))
        ua2 <- ua.set(YouHaveNotIncludedTheTaxYearPage, longText)
      } yield ua2).success.value

      val result = YouHaveNotIncludedTheTaxYearSummary.row(userAnswers, revealFullText)
      result mustBe defined
    }
  }
}