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

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.Forms._
import models.{AdviceContactPreference, AdviceGiven}
import java.time.LocalDate

class AdviceGivenFormProvider @Inject() extends Mappings {

   def apply(): Form[AdviceGiven] = Form(
     mapping(
      "adviceGiven" -> text("adviceGiven.error.adviceGiven.required")
        .verifying(maxLength(5000, "adviceGiven.error.adviceGiven.length")),
      "date.month" -> int("adviceGiven.error.date.month.required", "adviceGiven.error.format", "adviceGiven.error.format")
        .verifying(inRange(1, 12, "adviceGiven.error.date.month.max")),
      "date.year" -> int("adviceGiven.error.date.year.required", "adviceGiven.error.format", "adviceGiven.error.format")
        .verifying(minimumValue(1850, "adviceGiven.error.date.min"))
        .verifying(maximumValue(LocalDate.now().getYear(), "adviceGiven.error.date.past")),
      "contact" -> enumerable[AdviceContactPreference]("adviceGiven.error.contact.required")  
    )(AdviceGiven.apply)(AdviceGiven.unapply)
   )
 }
