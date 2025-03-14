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

package viewmodels.offshore

import config.Country
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import play.api.i18n.Messages
import controllers.offshore.routes
import models.Mode

object CountryModels {

  def row(countries: Set[Country], mode: Mode)(implicit messages: Messages): Seq[SummaryListRow] =
    countries.map { country =>
      SummaryListRowViewModel(
        key = Key(country.name).withCssClass("govuk-!-font-weight-regular hmrc-summary-list__key"),
        value = ValueViewModel(""),
        actions = Seq(
          ActionItemViewModel("site.remove", routes.CountriesOrTerritoriesController.remove(country.alpha3, mode).url)
            .withVisuallyHiddenText(country.name)
        )
      )
    }.toSeq
}
