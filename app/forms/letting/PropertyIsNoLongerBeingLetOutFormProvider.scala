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
import models.NoLongerBeingLetOut
import play.api.data.Form
import play.api.data.Forms._
import java.time.{LocalDate, Month}

class PropertyIsNoLongerBeingLetOutFormProvider @Inject() extends Mappings {

  def apply(): Form[NoLongerBeingLetOut] =
    Form(
      mapping(
        "stopDate" -> localDate(
          invalidKey     = "propertyIsNoLongerBeingLetOut.stopDate.error.invalid",
          allRequiredKey = "propertyIsNoLongerBeingLetOut.stopDate.error.required.all",
          requiredKey    = "propertyIsNoLongerBeingLetOut.stopDate.error.required",
          invalidDayKey    = "propertyIsNoLongerBeingLetOut.stopDate.error.invalidDay",
          invalidMonthKey  = "propertyIsNoLongerBeingLetOut.stopDate.error.invalidMonth"
        )
        .verifying(minDate(LocalDate.of(1850, Month.JANUARY, 1), "propertyIsNoLongerBeingLetOut.stopDate.error.invalidPastDate"))
        .verifying(maxDate(LocalDate.now().minusDays(1), "propertyIsNoLongerBeingLetOut.stopDate.error.invalidFutureDate")),

        "whatHasHappenedToProperty" -> text("propertyIsNoLongerBeingLetOut.whatHasHappenedToProperty.error.required")
        .verifying(maxLength(5000, "propertyIsNoLongerBeingLetOut.whatHasHappenedToProperty.error.length"))
      )(NoLongerBeingLetOut.apply)(NoLongerBeingLetOut.unapply)
    )
}
