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

package viewmodels.govuk

import uk.gov.hmrc.govukfrontend.views.Aliases.Text


object text extends TextFluency
trait TextFluency {

  object TextViewModel {
    def apply(content:String): Text = {
      Text(content)
    }
  }

  implicit class FluentText(text:Text) {
    def withEllipsisOverflow(maxSize:Int):Text = {
      val value = if(text.value.length > maxSize) text.value.substring(0, maxSize) + "..." else text.value
      text copy (value = value)
    }
  }
}
