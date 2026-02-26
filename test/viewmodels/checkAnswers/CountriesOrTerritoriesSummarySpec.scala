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
import config.Country
import models.UserAnswers
import pages.CountryOfYourOffshoreLiabilityPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.Key
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.RevealFullText
import viewmodels.govuk.summarylist._

class CountriesOrTerritoriesSummarySpec extends SpecBase {

  lazy val app                    = application
  implicit val mess: Messages     = messages
  lazy val revealFullText         = application.injector.instanceOf[RevealFullText]

  "CountriesOrTerritoriesSummary.row" - {

    "must return None when the page has no answer" in {
      val userAnswers = UserAnswers("id", "session-123")
      CountriesOrTerritoriesSummary.row(userAnswers, revealFullText) mustBe None
    }

    "must return a row with the country name" in {
      val countriesMap = Map("AFG" -> Country("AFG", "Afghanistan"))
      val userAnswers  = UserAnswers("id", "session-123")
        .set(CountryOfYourOffshoreLiabilityPage, countriesMap).success.value

      CountriesOrTerritoriesSummary.row(userAnswers, revealFullText).map { row =>
        row.key mustBe Key(Text(mess("countriesOrTerritories.checkYourAnswersLabel")))
        row.value.content.toString must include("Afghanistan")
      }
    }

    "must return a row with multiple country names" in {
      val countriesMap = Map(
        "AFG" -> Country("AFG", "Afghanistan"),
        "ALB" -> Country("ALB", "Albania")
      )
      val userAnswers = UserAnswers("id", "session-123")
        .set(CountryOfYourOffshoreLiabilityPage, countriesMap).success.value

      CountriesOrTerritoriesSummary.row(userAnswers, revealFullText).map { row =>
        row.value.content.toString must include("Afghanistan")
        row.value.content.toString must include("Albania")
      }
    }

    "must handle long text that exceeds the reveal threshold" in {
      val longCountryName = "A" * 200
      val countriesMap    = Map("XXX" -> Country("XXX", longCountryName))
      val userAnswers     = UserAnswers("id", "session-123")
        .set(CountryOfYourOffshoreLiabilityPage, countriesMap).success.value

      CountriesOrTerritoriesSummary.row(userAnswers, revealFullText).map { row =>
        row.value.content.toString must include("...")
      }
    }
  }
}