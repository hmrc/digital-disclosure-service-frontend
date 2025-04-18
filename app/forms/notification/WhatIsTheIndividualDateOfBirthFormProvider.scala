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

import java.time.{LocalDate, Month}
import forms.mappings.Mappings

import javax.inject.Inject
import play.api.data.Form

class WhatIsTheIndividualDateOfBirthFormProvider @Inject() extends Mappings {

  def apply(): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey = "whatIsTheIndividualDateOfBirth.error.invalid",
        allRequiredKey = "whatIsTheIndividualDateOfBirth.error.required.all",
        requiredKey = "whatIsTheIndividualDateOfBirth.error.required",
        invalidDayKey = "whatIsTheIndividualDateOfBirth.error.invalidDay",
        invalidMonthKey = "whatIsTheIndividualDateOfBirth.error.invalidMonth"
      )
        .verifying(
          minDate(LocalDate.of(1850, Month.JANUARY, 1), "whatIsTheIndividualDateOfBirth.error.invalidPastDateOfBirth")
        )
        .verifying(
          maxDate(LocalDate.now().minusDays(1), "whatIsTheIndividualDateOfBirth.error.invalidFutureDateOfBirth")
        )
    )
}
