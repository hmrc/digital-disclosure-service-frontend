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
import play.twirl.api.Html
import support.ViewMatchers
import views.html.notification.YouHaveSentYourNotificationView
import config.FrontendAppConfig

class YouHaveSentYourNotificationViewSpec extends ViewSpecBase with ViewMatchers {

  val page: YouHaveSentYourNotificationView = inject[YouHaveSentYourNotificationView]
  val appConfig: FrontendAppConfig          = inject[FrontendAppConfig]

  "view" should {
    def createView: Html = page(true, "CFSS-1234567", true, false)(request, messages, appConfig)
    val view             = createView

    "have title for entity" in {
      view.select("title").text() must include(messages("youHaveSentYourNotification.title.entity"))
    }

    "contain green box heading for entity" in {
      view.getElementsByClass("govuk-panel__title").text() mustBe messages("youHaveSentYourNotification.heading.entity")
    }

    "contain green box body text for entity" in {
      view.getElementsByClass("govuk-panel__body").text() mustBe messages(
        "youHaveSentYourNotification.body.caseRef.entity"
      ) + " CFSS-1234567"
    }

    "have a first paragraph for entity" in {
      view.getElementById("first-paragraph").text() mustBe messages(
        "youHaveSentYourNotification.paragraph.first"
      ) + messages("youHaveSentYourNotification.paragraph.link.entity") + messages("site.dot")
    }

    "contain second heading for entity" in {
      view.select("h2.govuk-heading-m:nth-child(1)").text() mustBe messages("exitSurvey.heading")
    }

    "have a second paragraph for entity" in {
      view.getElementById("second-paragraph").text() mustBe messages(
        "youHaveSentYourNotification.paragraph.second.caseRef"
      )
    }

    "have an exit survey paragraph" in {
      view.getElementById("exit-survey").text mustBe
        messages("exitSurvey.heading") + " " + messages("exitSurvey.p1") + " " + messages(
          "exitSurvey.link"
        ) + " " + messages("exitSurvey.p2")
    }

    "have an exit survey link" in {
      view.getElementsByClass("govuk-link").attr("href") mustBe controllers.auth.routes.AuthController.signOut.url
    }
  }

  "view" should {
    def createView: Html = page(true, "CFSS-1234567", false, false)(request, messages, appConfig)
    val view             = createView

    "have title for agent" in {
      view.select("title").text() must include(messages("youHaveSentYourNotification.title.agent"))
    }

    "contain green box heading for agent" in {
      view.getElementsByClass("govuk-panel__title").text() mustBe messages("youHaveSentYourNotification.heading.agent")
    }

    "contain green box body text for agent" in {
      view.getElementsByClass("govuk-panel__body").text() mustBe messages(
        "youHaveSentYourNotification.body.caseRef.agent"
      ) + " CFSS-1234567"
    }

    "have a first paragraph for agent" in {
      view.getElementById("first-paragraph").text() mustBe messages(
        "youHaveSentYourNotification.paragraph.first"
      ) + messages("youHaveSentYourNotification.paragraph.link.agent") + messages("site.dot")
    }

    "contain second heading for agent" in {
      view.getElementsByClass("govuk-heading-m").text() mustBe messages(
        "youHaveSentYourNotification.paragraph.header"
      ) + " " + messages("exitSurvey.heading")
    }

    "have a second paragraph for agent" in {
      view.getElementById("second-paragraph").text() mustBe messages(
        "youHaveSentYourNotification.paragraph.second.caseRef"
      )
    }

    "have an exit survey paragraph for an agent" in {
      view.getElementById("exit-survey").text mustBe
        messages("exitSurvey.heading") + " " + messages("exitSurvey.p1") + " " + messages(
          "exitSurvey.link"
        ) + " " + messages("exitSurvey.p2")
    }

    "have an exit survey link for an agent" in {
      view.getElementsByClass("govuk-link").attr("href") mustBe controllers.auth.routes.AuthController.signOut.url
    }
  }

  "view" should {
    def createView: Html = page(false, "CFSS-1234567", true, false)(request, messages, appConfig)
    val view             = createView

    "contain green box body text for entity with generated reference number" in {
      view.getElementsByClass("govuk-panel__body").text() mustBe messages(
        "youHaveSentYourNotification.body.generatedRef.entity"
      ) + " CFSS-1234567"
    }

    "have a second paragraph for entity with generated reference number" in {
      view.getElementById("second-paragraph").text() mustBe messages(
        "youHaveSentYourNotification.paragraph.second.generatedRef"
      )
    }
  }

  "view" should {
    def createView: Html = page(false, "CFSS-1234567", false, false)(request, messages, appConfig)
    val view             = createView

    "contain green box body text for agent with generated reference number" in {
      view.getElementsByClass("govuk-panel__body").text() mustBe messages(
        "youHaveSentYourNotification.body.generatedRef.agent"
      ) + " CFSS-1234567"
    }
  }
}
