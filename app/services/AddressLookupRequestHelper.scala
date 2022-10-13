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

  val API_VERSION = 2

  def lookupRequestForYourAddress(baseUrl: String, 
                                        redirectUrl: String, 
                                        proposalListLimit: Int)(implicit messages: Messages): AddressLookupRequest = {

    lookupRequestForAddress(baseUrl, redirectUrl, proposalListLimit,
                            "yourCountryLookup.title", 
                            "yourCountryLookup.heading",
                            "yourCountryLookup.hint",
                            "yourAddressLookup.title", 
                            "yourAddressLookup.heading",
                            "selectAddress.title", 
                            "selectAddress.heading",
                            "editAddress.title", 
                            "editAddress.heading",
                            "confirmAddress.title", 
                            "confirmAddress.heading")

  }

  def lookupRequestForIndividualAddress(baseUrl: String, 
                                        redirectUrl: String, 
                                        proposalListLimit: Int)(implicit messages: Messages): AddressLookupRequest = {

    lookupRequestForAddress(baseUrl, redirectUrl, proposalListLimit,
                            "individualCountryLookup.title", 
                            "individualCountryLookup.heading",
                            "individualCountryLookup.hint",
                            "individualAddressLookup.title", 
                            "individualAddressLookup.heading",
                            "selectIndividualAddress.title", 
                            "selectIndividualAddress.heading",
                            "editIndividualAddress.title", 
                            "editIndividualAddress.heading",
                            "confirmIndividualAddress.title", 
                            "confirmIndividualAddress.heading")

  }

  def lookupRequestForAddress(baseUrl: String, 
                              redirectUrl: String, 
                              proposalListLimit: Int,
                              countryLookupTitle: String, 
                              countryLookupHeading: String,
                              countryLookupHint: String,
                              addressLookupTitle: String, 
                              addressLookupHeading: String,
                              selectAddressTitle: String, 
                              selectAddressHeading: String,
                              editAddressTitle: String, 
                              editAddressHeading: String,
                              confirmAddressTitle: String, 
                              confirmAddressHeading: String)(implicit messages: Messages): AddressLookupRequest = {

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
      messages(countryLookupTitle), 
      messages(countryLookupHeading),
      messages(countryLookupHint),
      messages("site.continue")
    )
    val lookupPageLabels = LookupPageLabels(
      messages(addressLookupTitle), 
      messages(addressLookupHeading),
      messages("site.continue")
    )
    val selectPageLabels = SelectPageLabels(
      messages(selectAddressTitle), 
      messages(selectAddressHeading)
    )
    val editPageLabels = EditPageLabels(
      messages(editAddressTitle), 
      messages(editAddressHeading)
    )
    val confirmPageLabels = ConfirmPageLabels(
      messages(confirmAddressTitle), 
      messages(confirmAddressHeading)
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

    AddressLookupRequest(API_VERSION, addressLookupOptions, labels)
  }

}
