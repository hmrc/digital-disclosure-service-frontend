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

package config

import com.google.inject.Inject
import java.net.URL
import javax.inject.Singleton
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class AddressLookupConfig @Inject() (config: ServicesConfig) {

  private val serviceName = "address-lookup-frontend"

  lazy val serviceUrl: URL = new URL(config.baseUrl(serviceName))

  lazy val addressesShowLimit: Int = config.getInt(s"microservice.services.$serviceName.max-addresses-to-show")

  lazy val startLookupUrl: URL = LookupAddressUrl("init-endpoint")

  lazy val retrieveAddressUrl: URL = LookupAddressUrl("address-retrieve-endpoint")

  object LookupAddressUrl {
    def apply(pathConfigKey: String): URL =
      new URL(s"$serviceUrl${config.getString(s"microservice.services.$serviceName.$pathConfigKey")}")
  }
}