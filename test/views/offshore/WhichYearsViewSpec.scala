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
import forms.WhichYearsFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.offshore.WhichYearsView
import models.NormalMode
import uk.gov.hmrc.time.CurrentTaxYear
import java.time.LocalDate

class WhichYearsViewSpec extends ViewSpecBase with ViewMatchers with CurrentTaxYear {

  val form = new WhichYearsFormProvider()()
  val page: WhichYearsView = inject[WhichYearsView]
  def now = () => LocalDate.now()

  private def createView: Html = page(form, NormalMode, 5)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("whichYears.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("whichYears.heading")
    }

    "contain checkboxes" in {
      view.getElementsByClass("govuk-checkboxes__item").get(0).text() mustBe messages(s"whichYears.checkbox", current.back(0).startYear.toString, current.back(0).finishYear.toString)
      view.getElementsByClass("govuk-checkboxes__item").get(1).text() mustBe messages(s"whichYears.checkbox", current.back(1).startYear.toString, current.back(1).finishYear.toString)
      view.getElementsByClass("govuk-checkboxes__item").get(2).text() mustBe messages(s"whichYears.checkbox", current.back(2).startYear.toString, current.back(2).finishYear.toString)
      view.getElementsByClass("govuk-checkboxes__item").get(3).text() mustBe messages(s"whichYears.checkbox", current.back(3).startYear.toString, current.back(3).finishYear.toString)
      view.getElementsByClass("govuk-checkboxes__item").get(4).text() mustBe messages(s"whichYears.checkbox", current.back(4).startYear.toString, current.back(4).finishYear.toString)
      view.getElementsByClass("govuk-checkboxes__item").get(5).text() mustBe messages(s"whichYears.checkbox.any", current.back(4).startYear.toString)
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

  }

}