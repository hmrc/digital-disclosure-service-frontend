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

package viewmodels.govuk

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.dateinput.{DateInput, InputItem}
import uk.gov.hmrc.govukfrontend.views.viewmodels.fieldset.{Fieldset, Legend}
import uk.gov.hmrc.govukfrontend.views.viewmodels.hint.Hint
import viewmodels.ErrorMessageAwareness
import play.api.data.Form

object date extends DateFluency

trait DateFluency {

  object DateViewModel extends ErrorMessageAwareness {

    def apply(
               form: Form[_],
               id: String,
               legend: Legend
             )(implicit messages: Messages): DateInput =
      apply(
        form = form,
        id = id,
        fieldset = Fieldset(legend = Some(legend))
      )

    def apply(
               form: Form[_],
               id: String,
               fieldset: Fieldset
             )(implicit messages: Messages): DateInput = {

      val field = form(id)

      def errorClass(id: String) = if (errorMessage(field).isDefined || errorMessage(form(id)).isDefined) "govuk-input--error" else ""

      val dayId = s"${field.id}.day"
      val monthId = s"${field.id}.month"
      val yearId = s"${field.id}.year"

      val primaryError = Seq(errorMessage(field), errorMessage(form(dayId)), errorMessage(form(monthId)), errorMessage(form(yearId))).flatten.headOption

      val items = Seq(
        InputItem(
          id      = dayId,
          name    = s"${field.name}.day",
          value   = field("day").value,
          label   = Some(messages("date.day")),
          classes = s"govuk-input--width-2 ${errorClass(dayId)}".trim
        ),
        InputItem(
          id      = monthId,
          name    = s"${field.name}.month",
          value   = field("month").value,
          label   = Some(messages("date.month")),
          classes = s"govuk-input--width-2 ${errorClass(monthId)}".trim
        ),
        InputItem(
          id      = yearId,
          name    = s"${field.name}.year",
          value   = field("year").value,
          label   = Some(messages("date.year")),
          classes = s"govuk-input--width-4 ${errorClass(yearId)}".trim
        )
      )

      DateInput(
        fieldset     = Some(fieldset),
        items        = items,
        id           = field.id,
        errorMessage = primaryError
      )
    }
  }

  implicit class FluentDate(date: DateInput) {

    def withNamePrefix(prefix: String): DateInput =
      date copy (namePrefix = Some(prefix))

    def withHint(hint: Hint): DateInput =
      date copy (hint = Some(hint))

    def withFormGroupClasses(classes: String): DateInput =
      date copy (formGroupClasses = classes)

    def withCssClass(newClass: String): DateInput =
      date copy (classes = s"${date.classes} $newClass")

    def withAttribute(attribute: (String, String)): DateInput =
      date copy (attributes = date.attributes + attribute)

    def asDateOfBirth(): DateInput =
      date copy (
        items = date.items map {
          item =>
            val name = item.id.split('.').last
            item copy (autocomplete = Some(s"bday-$name"))
        })
  }
}
