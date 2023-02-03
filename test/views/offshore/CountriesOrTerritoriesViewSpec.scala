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

package views.offshore

import base.ViewSpecBase
import config.Country
import forms.CountriesOrTerritoriesFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.offshore.CountriesOrTerritoriesView
import models.NormalMode
import viewmodels.offshore.CountryModels

class CountriesOrTerritoriesViewSpec extends ViewSpecBase with ViewMatchers {

  val form = new CountriesOrTerritoriesFormProvider()()
  val page: CountriesOrTerritoriesView = inject[CountriesOrTerritoriesView]

  private val country1 = Country("AAA", "Country 1")
  private val singleCountrySet = Set(country1)
  private val multipleCountrySet =  Set(country1, Country("BBB", "Country 2"))


  "view for a single country" should {

    def createView: Html = page(form, CountryModels.row(singleCountrySet), NormalMode)(request, messages)

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("countriesOrTerritories.title.single"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("countriesOrTerritories.heading.single")
    }

    "have yes" in {
      view.getElementsByClass("govuk-radios__label").first().text() mustBe messages("site.yes")
    }

    "have the a summary list with the country" in {
      view.getElementsByClass("govuk-summary-list__key").first().text() mustBe country1.name
    }

    "have the a label" in {
      view.getElementsByClass("govuk-radios__label").first().text() mustBe messages("site.yes")
    }

    "have no" in {
      view.getElementsByClass("govuk-radios__label").last().text() mustBe messages("site.no")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

    "have a task list link" in {
      view.getElementById("task-list-link").attr("href") mustBe controllers.routes.TaskListController.onPageLoad.url
    }
  }

  "view for multiple countries" should {

    def createView: Html = page(form, CountryModels.row(multipleCountrySet), NormalMode)(request, messages)

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("countriesOrTerritories.title.multi", 2))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("countriesOrTerritories.heading.multi", 2)
    }

  }

}