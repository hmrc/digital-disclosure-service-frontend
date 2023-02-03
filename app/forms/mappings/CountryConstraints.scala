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

package forms.mappings

import config.{Countries, Country}
import play.api.Logging
import play.api.data.{FieldMapping, FormError}
import play.api.data.format.Formatter
import play.api.data.Forms._
abstract class CountryConstraints (countries: Countries) extends Formatters with Logging{

  private val allowedCountries = countries.countries.map(_.alpha3)

  protected def country(requiredKey: String): FieldMapping[Country] = {
    of(countryFormatter(requiredKey))
  }

  private def countryFormatter(requiredKey: String): Formatter[Country] = new Formatter[Country] {
    private val baseFormatter = stringFormatter(requiredKey, Seq.empty)

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Country] =
      baseFormatter
        .bind(key, data)
        .right.flatMap {
          code =>
            if(allowedCountries.contains(code)) {
              Right(Country(code, countries.getCountryNameFor(code)))
            }
            else {
              Left(Seq(FormError(key, requiredKey, Seq.empty)))
            }
      }

    override def unbind(key: String, value: Country): Map[String, String] = baseFormatter.unbind(key, value.toString)
  }
}
