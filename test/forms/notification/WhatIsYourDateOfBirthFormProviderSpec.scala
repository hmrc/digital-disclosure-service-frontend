/*
 * Copyright 2022 HM Revenue & Customs
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

import java.time.{LocalDate, Month, ZoneOffset}
import forms.behaviours.DateBehaviours
import play.api.data.FormError

class WhatIsYourDateOfBirthFormProviderSpec extends DateBehaviours {

  val form = new WhatIsYourDateOfBirthFormProvider()()

  ".value" - {
    "behave like dateField" - {

      val validData = datesBetween(
        min = LocalDate.of(1900, 1, 1),
        max = LocalDate.now(ZoneOffset.UTC).minusDays(1)
      )

      behave like dateField(form, "value", validData)

      behave like dateFieldWithMax(form, "value", LocalDate.now(ZoneOffset.UTC).minusDays(1), FormError("value", "whatIsYourDateOfBirth.error.invalidFutureDateOfBirth"))

      behave like dateFieldWithMin(form, "value", LocalDate.of(1850, Month.JANUARY, 1), FormError("value", "whatIsYourDateOfBirth.error.invalidPastDateOfBirth"))

      behave like mandatoryDateField(form, "value", "whatIsYourDateOfBirth.error.required.all")

      behave like dateFieldCheckingMaxDayAndMonth(form, "value", validData, FormError("value.day", "whatIsYourDateOfBirth.error.invalidDay"), FormError("value.month", "whatIsYourDateOfBirth.error.invalidMonth"))
    }
  }

  
}
