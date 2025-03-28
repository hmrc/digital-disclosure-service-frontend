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

package views.notification

import base.ViewSpecBase
import forms.AreYouTheEntityFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.notification.AreYouTheEntityView
import models.{NormalMode, RelatesTo}

class AreYouTheEntityViewSpec extends ViewSpecBase with ViewMatchers {

  val page: AreYouTheEntityView = inject[AreYouTheEntityView]

  def form(entity: RelatesTo) = new AreYouTheEntityFormProvider()(entity)

  private def createView(entity: RelatesTo): Html = page(form(entity), NormalMode, entity, false)(request, messages)

  RelatesTo.values.map { entity =>
    s"view for a $entity" should {

      val view = createView(entity)

      "have title" in {
        view.select("title").text() must include(messages(s"areYouTheEntity.$entity.title"))
      }

      "contain header" in {
        view.getElementsByClass("govuk-heading-xl").text() mustBe messages(s"areYouTheEntity.$entity.heading")
      }

      "have all checkbox options" in {
        view.getElementsByClass("govuk-radios__label").get(0).text() mustBe messages(s"areYouTheEntity.$entity.yes")
        view.getElementsByClass("govuk-radios__label").get(1).text() mustBe messages(
          s"areYouTheEntity.$entity.accountant"
        )
        view.getElementsByClass("govuk-radios__label").get(2).text() mustBe messages(s"areYouTheEntity.$entity.friend")
        view.getElementsByClass("govuk-radios__label").get(3).text() mustBe messages(
          s"areYouTheEntity.$entity.voluntaryOrganisation"
        )
        view.getElementsByClass("govuk-radios__label").get(4).text() mustBe messages(
          s"areYouTheEntity.$entity.powerOfAttorney"
        )
      }

      "display the continue button" in {
        view.getElementsByClass("govuk-button").first() must haveId("continue")
        view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
      }

    }
  }

}
