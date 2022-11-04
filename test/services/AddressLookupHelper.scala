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

trait AddressLookupHelper {
   
    def constructYourAddressRequest(afterHeadingText: Option[String])(implicit messages: Messages) = {
      val selectPageConfig = SelectPageConfig(proposalListLimit = 15)
      val addressLookupOptions = AddressLookupOptions(
        continueUrl = "host1.com/redirect",
        serviceHref = s"host1.com${routes.IndexController.onPageLoad.url}",
        showPhaseBanner = Some(true),
        alphaPhase = false,
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
        messages("site.continue"),
        afterHeadingText
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

    def yourAddressNoBodyRequest(implicit messages: Messages) = constructYourAddressRequest(None)
    def yourAddressIndividualBodyRequest(implicit messages: Messages) = constructYourAddressRequest(Some(messages("yourAddressLookup.individual.afterHeadingText")))
    def yourAddressCompanyBodyRequest(implicit messages: Messages) = constructYourAddressRequest(Some(messages("yourAddressLookup.company.afterHeadingText")))

    def individualLookupRequest(implicit messages: Messages) = {
      val selectPageConfig = SelectPageConfig(proposalListLimit = 15)
      val addressLookupOptions = AddressLookupOptions(
        continueUrl = "host1.com/redirect",
        serviceHref = s"host1.com${routes.IndexController.onPageLoad.url}",
        showPhaseBanner = Some(true),
        alphaPhase = false,
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
        messages("site.continue"),
        None
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

    def companyLookupRequest(implicit messages: Messages) = {
      val selectPageConfig = SelectPageConfig(proposalListLimit = 15)
      val addressLookupOptions = AddressLookupOptions(
        continueUrl = "host1.com/redirect",
        serviceHref = s"host1.com${routes.IndexController.onPageLoad.url}",
        showPhaseBanner = Some(true),
        alphaPhase = false,
        selectPageConfig = Some(selectPageConfig),
        includeHMRCBranding = Some(false)
      )

      val appLevelLabels = AppLevelLabels(messages("service.name"))
      val countryPickerLabels = CountryPickerLabels(
        messages("companyCountryLookup.title"), 
        messages("companyCountryLookup.heading"),
        messages("companyCountryLookup.hint"),
        messages("site.continue")
      )
      val lookupPageLabels = LookupPageLabels(
        messages("companyAddressLookup.title"), 
        messages("companyAddressLookup.heading"),
        messages("site.continue"),
        None
      )
      val selectPageLabels = SelectPageLabels(
        messages("selectCompanyAddress.title"), 
        messages("selectCompanyAddress.heading")
      )
      val editPageLabels = EditPageLabels(
        messages("editCompanyAddress.title"), 
        messages("editCompanyAddress.heading")
      )
      val confirmPageLabels = ConfirmPageLabels(
        messages("confirmCompanyAddress.title"), 
        messages("confirmCompanyAddress.heading")
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