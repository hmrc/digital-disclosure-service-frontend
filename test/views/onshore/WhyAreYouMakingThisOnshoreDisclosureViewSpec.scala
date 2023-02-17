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
import forms.WhyAreYouMakingThisOnshoreDisclosureFormProvider
import play.twirl.api.Html
import support.ViewMatchers
import views.html.onshore.WhyAreYouMakingThisOnshoreDisclosureView
import models.{NormalMode, RelatesTo, UserAnswers}
import pages.AreYouTheIndividualPage

class WhyAreYouMakingThisOnshoreDisclosureViewSpec extends ViewSpecBase with ViewMatchers {

  val form = new WhyAreYouMakingThisOnshoreDisclosureFormProvider()()
  val page: WhyAreYouMakingThisOnshoreDisclosureView = inject[WhyAreYouMakingThisOnshoreDisclosureView]

  "view" should {
    
    val areTheyTheIndividual = true
    val entity = RelatesTo.AnIndividual

    def createView: Html = page(form, NormalMode, areTheyTheIndividual, entity)(request, messages)

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("whyAreYouMakingThisDisclosure.title"))
    }

    "contain header" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("whyAreYouMakingThisDisclosure.heading")
    }

    "contain first paragraph" in {
      view.getElementById("first-paragraph").text() mustBe messages("whyAreYouMakingThisDisclosure.paragraph.first.you")
    }

    "contain second paragraph" in {
      view.getElementById("second-paragraph").text() mustBe messages("whyAreYouMakingThisDisclosure.paragraph.second")
    }

    "contain multiple checkboxes when select Yes, I am the individual" in {
      constructMessageKey(view, areTheyTheIndividual, entity)
    }

    "display the continue button" in {
      view.getElementsByClass("govuk-button").first() must haveId ("continue")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.saveAndContinue")
    }

    "have a task list link" in {
      view.getElementById("task-list-link").attr("href") mustBe controllers.routes.TaskListController.onPageLoad.url
    }

  }

  "view" should {
    
    val areTheyTheIndividual = false
    val entity = RelatesTo.AnIndividual

    def createView: Html = page(form, NormalMode, areTheyTheIndividual, entity)(request, messages)

    val view = createView

    "contain multiple checkboxes when select No, I am disclosing on behalf of the individual" in {
      constructMessageKey(view, areTheyTheIndividual, entity)
    }
  }

  "view" should {
    
    val areTheyTheIndividual = false
    val entity = RelatesTo.AnEstate

    def createView: Html = page(form, NormalMode, areTheyTheIndividual, entity)(request, messages)

    val view = createView

    "contain multiple checkboxes when select AnEstate" in {
      constructMessageKey(view, areTheyTheIndividual, entity)
    }
  }

  "view" should {
    
    val areTheyTheIndividual = false
    val entity = RelatesTo.ACompany

    def createView: Html = page(form, NormalMode, areTheyTheIndividual, entity)(request, messages)

    val view = createView

    "contain multiple checkboxes when select ACompany" in {
      constructMessageKey(view, areTheyTheIndividual, entity)
    }
  }

  "view" should {
    
    val areTheyTheIndividual = false
    val entity = RelatesTo.ALimitedLiabilityPartnership

    def createView: Html = page(form, NormalMode, areTheyTheIndividual, entity)(request, messages)

    val view = createView

    "contain multiple checkboxes when select ALimitedLiabilityPartnership" in {
      constructMessageKey(view, areTheyTheIndividual, entity)
    }
  }

  "view" should {
    
    val areTheyTheIndividual = false
    val entity = RelatesTo.ATrust

    def createView: Html = page(form, NormalMode, areTheyTheIndividual, entity)(request, messages)

    val view = createView

    "contain multiple checkboxes when select ATrust" in {
      constructMessageKey(view, areTheyTheIndividual, entity)
    }
  }

  def isTheUserTheIndividual(userAnswers: UserAnswers): Boolean = {
    userAnswers.get(AreYouTheIndividualPage) match {
      case Some(true) => true
      case _ => false
    }
  }

  def constructMessageKey(view: Html, areTheyTheIndividual: Boolean, entity: RelatesTo) = {
    if (areTheyTheIndividual) {
      view.getElementsByClass("govuk-checkboxes__item").get(0).text() mustBe messages(s"whyAreYouMakingThisDisclosure.you.didNotNotifyHasExcuse")
      view.getElementsByClass("govuk-checkboxes__item").get(1).text() mustBe messages(s"whyAreYouMakingThisDisclosure.you.inaccurateReturnWithCare")
      view.getElementsByClass("govuk-checkboxes__item").get(2).text() mustBe messages(s"whyAreYouMakingThisDisclosure.you.notFileHasExcuse")
      view.getElementsByClass("govuk-checkboxes__item").get(3).text() mustBe messages(s"whyAreYouMakingThisDisclosure.you.inaccurateReturnNoCare")
      view.getElementsByClass("govuk-checkboxes__item").get(4).text() mustBe messages(s"whyAreYouMakingThisDisclosure.you.didNotNotifyNoExcuse")
      view.getElementsByClass("govuk-checkboxes__item").get(5).text() mustBe messages(s"whyAreYouMakingThisDisclosure.you.deliberatelyDidNotNotify")
      view.getElementsByClass("govuk-checkboxes__item").get(6).text() mustBe messages(s"whyAreYouMakingThisDisclosure.you.deliberateInaccurateReturn")
      view.getElementsByClass("govuk-checkboxes__item").get(7).text() mustBe messages(s"whyAreYouMakingThisDisclosure.you.deliberatelyDidNotFile")
    } else {
      view.getElementsByClass("govuk-checkboxes__item").get(0).text() mustBe messages(s"whyAreYouMakingThisDisclosure.${entity.toString}.didNotNotifyHasExcuse")
      view.getElementsByClass("govuk-checkboxes__item").get(1).text() mustBe messages(s"whyAreYouMakingThisDisclosure.${entity.toString}.inaccurateReturnWithCare")
      view.getElementsByClass("govuk-checkboxes__item").get(2).text() mustBe messages(s"whyAreYouMakingThisDisclosure.${entity.toString}.notFileHasExcuse")
      view.getElementsByClass("govuk-checkboxes__item").get(3).text() mustBe messages(s"whyAreYouMakingThisDisclosure.${entity.toString}.inaccurateReturnNoCare")
      view.getElementsByClass("govuk-checkboxes__item").get(4).text() mustBe messages(s"whyAreYouMakingThisDisclosure.${entity.toString}.didNotNotifyNoExcuse")
      view.getElementsByClass("govuk-checkboxes__item").get(5).text() mustBe messages(s"whyAreYouMakingThisDisclosure.${entity.toString}.deliberatelyDidNotNotify")
      view.getElementsByClass("govuk-checkboxes__item").get(6).text() mustBe messages(s"whyAreYouMakingThisDisclosure.${entity.toString}.deliberateInaccurateReturn")
      view.getElementsByClass("govuk-checkboxes__item").get(7).text() mustBe messages(s"whyAreYouMakingThisDisclosure.${entity.toString}.deliberatelyDidNotFile")
    }
  }

}