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

package views

import base.ViewSpecBase
import play.twirl.api.Html
import support.ViewMatchers
import views.html.CaseManagementView
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{Table, HeadCell}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text

class CaseManagementViewSpec extends ViewSpecBase with ViewMatchers {

  val page: CaseManagementView = inject[CaseManagementView]

  val table = Table(
    rows = Nil,
    head = Some(Seq(HeadCell(Text("Some header")))),
    attributes = Map("id" -> "case-table")
  )
  private def createView: Html = page(table, None)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("caseManagement.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("caseManagement.heading")
    }

    "contain the opening paragraphs" in {
      view.getElementById("first-paragraph").text() mustBe messages("caseManagement.paragraph1")
      view.getElementById("second-paragraph").text() mustBe messages("caseManagement.paragraph2")
      view.getElementById("third-paragraph").text() mustBe messages("caseManagement.paragraph4")
    }

    "display the create case button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("create-case")
    }

  }

}