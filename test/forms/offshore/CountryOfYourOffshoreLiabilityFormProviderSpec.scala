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

import config.{Countries, Country}
import forms.behaviours.FieldBehaviours
import org.mockito.MockitoSugar
import play.api.data.FormError

class CountryOfYourOffshoreLiabilityFormProviderSpec extends FieldBehaviours with MockitoSugar {

  val requiredKey = "countryOfYourOffshoreLiability.error.required"

  val countryIndex = Some(1)
  val country = Country("AAA", "Country")
  val countries = mock[Countries]
  when(countries.countries).thenReturn(Seq(country))
  when(countries.getCountryNameFor(country.alpha3)).thenReturn(country.name)

  val form = new CountryOfYourOffshoreLiabilityFormProvider(countries)()

  ".country" - {

    val fieldName = "country"

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      country.alpha3
    )
  }
}
