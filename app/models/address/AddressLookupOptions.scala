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

package models.address

import play.api.libs.json.Json
import play.api.libs.json.OFormat

final case class AddressLookupOptions(
  continueUrl: String,
  alphaPhase: Boolean,
  timeoutConfig: Option[TimeoutConfig] = None,
  signOutHref: Option[String] = None,
  accessibilityFooterUrl: Option[String] = None,
  selectPageConfig: Option[SelectPageConfig] = None,
  confirmPageConfig: ConfirmPageConfig = ConfirmPageConfig(),
  phaseFeedbackLink: Option[String] = None,
  deskProServiceName: Option[String] = None,
  showPhaseBanner: Option[Boolean] = None,
  ukMode: Option[Boolean] = None,
  includeHMRCBranding: Option[Boolean] = None
)

object AddressLookupOptions {
  implicit val addressLookupOptionsFormat: OFormat[AddressLookupOptions] = Json.format[AddressLookupOptions]
}

final case class SelectPageConfig(proposalListLimit: Int)

object SelectPageConfig {
  implicit val selectPageConfigFormat: OFormat[SelectPageConfig] = Json.format[SelectPageConfig]
}

final case class ConfirmPageConfig(
  showChangeLink: Option[Boolean] = None,
  showSearchAgainLink: Option[Boolean] = None,
  showConfirmChangeText: Option[Boolean] = None
)

object ConfirmPageConfig {
  implicit val confirmPageConfigFormat: OFormat[ConfirmPageConfig] = Json.format[ConfirmPageConfig]
}

final case class TimeoutConfig(
  timeoutAmount: Int,
  timeoutUrl: String,
  timeoutKeepAliveUrl: Option[String] = None
)

object TimeoutConfig {
  implicit val timeoutConfigFormat: OFormat[TimeoutConfig] = Json.format[TimeoutConfig]
}