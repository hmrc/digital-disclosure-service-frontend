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
import models.{Behaviour, NormalMode}
import uk.gov.hmrc.time.CurrentTaxYear
import services.{OffshoreWhichYearsService, TimeService}

class WhichYearsViewSpec extends ViewSpecBase with ViewMatchers with CurrentTaxYear {

  val form                               = new WhichYearsFormProvider()()
  val page: WhichYearsView               = inject[WhichYearsView]
  val service: OffshoreWhichYearsService = inject[OffshoreWhichYearsService]
  val timeService: TimeService           = inject[TimeService]
  def now                                = () => timeService.date

  "view" should {

    val checkboxes       = service.checkboxItems(Behaviour.Deliberate)
    def createView: Html = page(form, NormalMode, checkboxes)(request, messages)
    val view             = createView

    "contain hint text" in {
      view.getElementById("value-hint").text() mustBe messages("whichYears.checkbox.header")
    }

    "contain checkboxes" in {
      view.getElementsByClass("govuk-checkboxes__item").get(0).text() mustBe messages(
        s"whichYears.checkbox",
        current.back(2).startYear.toString,
        current.back(2).finishYear.toString
      )
      view.getElementsByClass("govuk-checkboxes__item").get(1).text() mustBe messages(
        s"whichYears.checkbox",
        current.back(3).startYear.toString,
        current.back(3).finishYear.toString
      )
      view.getElementsByClass("govuk-checkboxes__item").get(2).text() mustBe messages(
        s"whichYears.checkbox",
        current.back(4).startYear.toString,
        current.back(4).finishYear.toString
      )
      view.getElementsByClass("govuk-checkboxes__item").get(3).text() mustBe messages(
        s"whichYears.checkbox",
        current.back(5).startYear.toString,
        current.back(5).finishYear.toString
      )
      view.getElementsByClass("govuk-checkboxes__item").get(4).text() mustBe messages(
        s"whichYears.checkbox",
        current.back(6).startYear.toString,
        current.back(6).finishYear.toString
      )
      view.getElementsByClass("govuk-checkboxes__item").get(5).text() mustBe messages(
        s"whichYears.checkbox",
        current.back(7).startYear.toString,
        current.back(7).finishYear.toString
      )
      view.getElementsByClass("govuk-checkboxes__item").get(6).text() mustBe messages(
        s"whichYears.checkbox",
        current.back(8).startYear.toString,
        current.back(8).finishYear.toString
      )
      view.getElementsByClass("govuk-checkboxes__item").get(7).text() mustBe messages(
        s"whichYears.checkbox",
        current.back(9).startYear.toString,
        current.back(9).finishYear.toString
      )
      view.getElementsByClass("govuk-checkboxes__item").get(8).text() mustBe messages(
        s"whichYears.checkbox",
        current.back(10).startYear.toString,
        current.back(10).finishYear.toString
      )
      view.getElementsByClass("govuk-checkboxes__item").get(9).text() mustBe messages(
        s"whichYears.checkbox",
        current.back(11).startYear.toString,
        current.back(11).finishYear.toString
      )
      view.getElementsByClass("govuk-checkboxes__item").get(10).text() mustBe messages(
        s"whichYears.checkbox",
        current.back(12).startYear.toString,
        current.back(12).finishYear.toString
      )
      view.getElementsByClass("govuk-checkboxes__item").get(11).text() mustBe messages(
        s"whichYears.checkbox",
        current.back(13).startYear.toString,
        current.back(13).finishYear.toString
      )
      view.getElementsByClass("govuk-checkboxes__item").get(12).text() mustBe messages(
        s"whichYears.checkbox",
        current.back(14).startYear.toString,
        current.back(14).finishYear.toString
      )
      view.getElementsByClass("govuk-checkboxes__item").get(13).text() mustBe messages(
        s"whichYears.checkbox",
        current.back(15).startYear.toString,
        current.back(15).finishYear.toString
      )
      view.getElementsByClass("govuk-checkboxes__item").get(14).text() mustBe messages(
        s"whichYears.checkbox",
        current.back(16).startYear.toString,
        current.back(16).finishYear.toString
      )
      view.getElementsByClass("govuk-checkboxes__item").get(15).text() mustBe messages(
        s"whichYears.checkbox",
        current.back(17).startYear.toString,
        current.back(17).finishYear.toString
      )
      view.getElementsByClass("govuk-checkboxes__item").get(16).text() mustBe messages(
        s"whichYears.checkbox",
        current.back(18).startYear.toString,
        current.back(18).finishYear.toString
      )
      view.getElementsByClass("govuk-checkboxes__item").get(17).text() mustBe messages(
        s"whichYears.checkbox",
        current.back(19).startYear.toString,
        current.back(19).finishYear.toString
      )
      view.getElementsByClass("govuk-checkboxes__item").get(18).text() mustBe messages(
        s"whichYears.checkbox",
        current.back(20).startYear.toString,
        current.back(20).finishYear.toString
      )
      view.getElementsByClass("govuk-checkboxes__item").get(19).text() mustBe messages(
        s"whichYears.checkbox.any",
        current.back(20).startYear.toString
      )
    }
  }

}
