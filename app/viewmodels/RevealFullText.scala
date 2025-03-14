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

package viewmodels

import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import com.google.inject.{Inject, Singleton}
import views.html.components.revealFullText
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Content
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import play.api.i18n.Messages

@Singleton
class RevealFullText @Inject() (revealFullText: revealFullText) {

  val MAX_SIZE = 150

  def addRevealToText(text: String, summaryKey: String, arg0: String = "")(implicit messages: Messages): Content = {
    val fullText = HtmlFormat.escape(text).toString

    if (text.length > MAX_SIZE) {
      val shortenedText = HtmlFormat.escape(text.substring(0, MAX_SIZE) + "...").toString
      HtmlContent(revealFullText(shortenedText, fullText, messages(summaryKey, arg0)))
    } else {
      Text(fullText)
    }
  }

}
