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

package services

import models.address._
import play.api.i18n.Messages
import controllers.routes

trait AddressLookupRequestHelper {

  def lookupRequestForYourAddress(baseUrl: String, redirectUrl: String, proposalListLimit: Int)(implicit messages: Messages): AddressLookupRequest = {

    val selectPageConfig = SelectPageConfig(proposalListLimit = proposalListLimit)
    val addressLookupOptions = AddressLookupOptions(
      continueUrl = s"$baseUrl$redirectUrl",
      serviceHref = s"$baseUrl${routes.IndexController.onPageLoad.url}",
      showPhaseBanner = Some(true),
      alphaPhase = true,
      selectPageConfig = Some(selectPageConfig),
      includeHMRCBranding = Some(false)
    )

    val appLevelLabels = AppLevelLabels(messages("service.name"))
    val countryPickerLabels = CountryPickerLabels(
      messages("yourCountryLookup.title"), 
      messages("yourCountryLookup.heading"),
      messages("yourCountryLookup.hint"),
      messages("site.continue")
    )
    val lookupPageLabels = LookupPageLabels(
      messages("yourAddressLookup.title"), 
      messages("yourAddressLookup.heading"),
      messages("site.continue")
    )
    val selectPageLabels = SelectPageLabels(
      messages("selectAddress.title"), 
      messages("selectAddress.heading")
    )
    val editPageLabels = EditPageLabels(
      messages("editAddress.title"), 
      messages("editAddress.heading")
    )
    val confirmPageLabels = ConfirmPageLabels(
      messages("confirmAddress.title"), 
      messages("confirmAddress.heading")
    )
    val internationalLabels = InternationalLabels(
      lookupPageLabels, 
      selectPageLabels, 
      editPageLabels, 
      confirmPageLabels
    )
    val englishLabels = LabelsByLanguage(
      appLevelLabels, 
      countryPickerLabels, 
      lookupPageLabels, 
      selectPageLabels, 
      editPageLabels, 
      confirmPageLabels,
      internationalLabels
    )
    val labels = AddressLookupLabels(englishLabels)

    AddressLookupRequest(2, addressLookupOptions, labels)
  }

  def lookupRequestForIndividualAddress(baseUrl: String, redirectUrl: String, proposalListLimit: Int)(implicit messages: Messages): AddressLookupRequest = {

    val selectPageConfig = SelectPageConfig(proposalListLimit = proposalListLimit)
    val addressLookupOptions = AddressLookupOptions(
      continueUrl = s"$baseUrl$redirectUrl",
      serviceHref = s"$baseUrl${routes.IndexController.onPageLoad.url}",
      showPhaseBanner = Some(true),
      alphaPhase = true,
      selectPageConfig = Some(selectPageConfig),
      includeHMRCBranding = Some(false)
    )

    val appLevelLabels = AppLevelLabels(messages("service.name"))
    val countryPickerLabels = CountryPickerLabels(
      messages("individualCountryLookup.title"), 
      messages("individualCountryLookup.heading"),
      messages("individualCountryLookup.hint"),
      messages("site.continue")
    )
    val lookupPageLabels = LookupPageLabels(
      messages("individualAddressLookup.title"), 
      messages("individualAddressLookup.heading"),
      messages("site.continue")
    )
    val selectPageLabels = SelectPageLabels(
      messages("selectIndividualAddress.title"), 
      messages("selectIndividualAddress.heading")
    )
    val editPageLabels = EditPageLabels(
      messages("editIndividualAddress.title"), 
      messages("editIndividualAddress.heading")
    )
    val confirmPageLabels = ConfirmPageLabels(
      messages("confirmIndividualAddress.title"), 
      messages("confirmIndividualAddress.heading")
    )
    val internationalLabels = InternationalLabels(
      lookupPageLabels, 
      selectPageLabels, 
      editPageLabels, 
      confirmPageLabels
    )
    val englishLabels = LabelsByLanguage(
      appLevelLabels, 
      countryPickerLabels, 
      lookupPageLabels, 
      selectPageLabels, 
      editPageLabels, 
      confirmPageLabels,
      internationalLabels
    )
    val labels = AddressLookupLabels(englishLabels)

    AddressLookupRequest(2, addressLookupOptions, labels)
  }

}
