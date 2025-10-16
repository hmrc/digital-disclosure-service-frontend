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

package views.reason

import base.ViewSpecBase
import forms.DidSomeoneGiveYouAdviceNotDeclareTaxFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.reason.DidSomeoneGiveYouAdviceNotDeclareTaxView
import models.{NormalMode, RelatesTo}
import org.scalacheck.Arbitrary.arbitrary
import generators.Generators

class DidSomeoneGiveYouAdviceNotDeclareTaxViewSpec extends ViewSpecBase with ViewMatchers with Generators {

  val page: DidSomeoneGiveYouAdviceNotDeclareTaxView = inject[DidSomeoneGiveYouAdviceNotDeclareTaxView]

  val areTheyTheIndividual = arbitrary[Boolean].sample.value
  val entity               = arbitrary[RelatesTo].sample.value
  val form                 = new DidSomeoneGiveYouAdviceNotDeclareTaxFormProvider()(areTheyTheIndividual, entity)

  private def createView: Html = page(form, NormalMode, areTheyTheIndividual, entity)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(
        if (areTheyTheIndividual) messages("didSomeoneGiveYouAdviceNotDeclareTax.agent.title")
        else messages(s"didSomeoneGiveYouAdviceNotDeclareTax.$entity.title")
      )
    }

    "contain header" in {
      view
        .getElementsByClass("govuk-fieldset__heading")
        .text() mustBe (if (areTheyTheIndividual) messages("didSomeoneGiveYouAdviceNotDeclareTax.agent.heading")
                        else messages(s"didSomeoneGiveYouAdviceNotDeclareTax.$entity.heading"))
    }

    "display the continue button" in {
      view.getElementById("continue").text() mustBe messages("site.saveAndContinue")
    }

    "have a task list link" in {
      view.getElementById("task-list-link").attr("href") mustBe controllers.routes.TaskListController.onPageLoad.url
    }

  }

}
