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

package views.onshore

import base.ViewSpecBase
import forms.AreYouAMemberOfAnyLandlordAssociationsFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.onshore.AreYouAMemberOfAnyLandlordAssociationsView
import models.{NormalMode, RelatesTo}

class AreYouAMemberOfAnyLandlordAssociationsViewSpec extends ViewSpecBase with ViewMatchers {

  val areTheyTheIndividual = true
  val entity = RelatesTo.AnIndividual
  
  val form = new AreYouAMemberOfAnyLandlordAssociationsFormProvider()(areTheyTheIndividual, entity)
  val page: AreYouAMemberOfAnyLandlordAssociationsView = inject[AreYouAMemberOfAnyLandlordAssociationsView]

  private def createView: Html = page(form, NormalMode, areTheyTheIndividual, entity)(request, messages)

  "view" should {

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("areYouAMemberOfAnyLandlordAssociations.you.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-fieldset__heading").text() mustBe messages("areYouAMemberOfAnyLandlordAssociations.you.heading")
    }

    "have yes" in {
      view.getElementsByClass("govuk-radios__label").first().text() mustBe messages("areYouAMemberOfAnyLandlordAssociations.yes")
    }

    "have no" in {
      view.getElementsByClass("govuk-radios__label").last().text() mustBe messages("areYouAMemberOfAnyLandlordAssociations.no")
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

  }

}