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

import java.time.{LocalDate, ZoneOffset}
import forms.behaviours.{PeriodEndBehaviours, StringFieldBehaviours}
import play.api.data.FormError

class PropertyIsNoLongerBeingLetOutFormProviderSpec extends PeriodEndBehaviours with StringFieldBehaviours {

  val form = new PropertyIsNoLongerBeingLetOutFormProvider()()

  ".stopDate" - {

    val fieldName = "stopDate"

    val data = Map(
      s"stopDate.day" -> "1",
      s"stopDate.month" -> "1",
      s"stopDate.year" -> LocalDate.now(ZoneOffset.UTC).minusDays(1).getYear.toString,
      s"whatHasHappenedToProperty" -> "Reason"
    )

    periodEndField(form, "propertyIsNoLongerBeingLetOut", data)
    periodEndFieldCheckingMaxDay(form, fieldName, data, FormError(fieldName + ".day", "propertyIsNoLongerBeingLetOut.stopDate.error.invalidDay"))
    periodEndFieldCheckingMaxMonth(form, fieldName, data, FormError(fieldName + ".month", "propertyIsNoLongerBeingLetOut.stopDate.error.invalidMonth"))
    periodEndFieldInFuture(form, fieldName, data, FormError(fieldName, "propertyIsNoLongerBeingLetOut.stopDate.error.invalidFutureDate"))
    periodEndFieldWithMin(form, fieldName, data, FormError(fieldName, "propertyIsNoLongerBeingLetOut.stopDate.error.invalidPastDate"))
  }

  ".whatHasHappenedToProperty" - {

    val fieldName = "whatHasHappenedToProperty"
    val maxLength = 5000

    val lengthKey = "propertyIsNoLongerBeingLetOut.whatHasHappenedToProperty.error.length"
    val requiredKey = "propertyIsNoLongerBeingLetOut.whatHasHappenedToProperty.error.required"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
