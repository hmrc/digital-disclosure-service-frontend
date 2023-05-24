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

package services

import models.address._
import play.api.i18n.{MessagesApi, MessagesImpl, Messages, Lang}
import controllers.routes

trait AddressLookupHelper {

    def messagesApi: MessagesApi 

    lazy val englishMessages: Messages = MessagesImpl(Lang("en"), messagesApi)
    lazy val welshMessages: Messages = MessagesImpl(Lang("cy"), messagesApi)

    val selectPageConfig = SelectPageConfig(proposalListLimit = 15)
    def timeoutConfig = TimeoutConfig(timeoutAmount = 900, timeoutUrl = controllers.auth.routes.AuthController.signOut.url, timeoutKeepAliveUrl = Some(routes.KeepAliveController.keepAlive.url))
    def addressLookupOptions(ukMode: Option[Boolean] = None) = AddressLookupOptions(
      continueUrl = "host1.com/redirect",
      serviceHref = s"host1.com${routes.IndexController.onPageLoad.url}",
      signOutHref = Some(s"host1.com${controllers.auth.routes.AuthController.signOut.url}"),
      showPhaseBanner = Some(true),
      alphaPhase = false,
      disableTranslations = true,
      selectPageConfig = Some(selectPageConfig),
      includeHMRCBranding = Some(false),
      timeoutConfig = Some(timeoutConfig),
      ukMode = ukMode
    )

    def constructYourAddressRequest(afterHeadingText: Option[String]) = {
      val englishLabels = languageLabels(englishMessages, "yourCountryLookup", "yourAddressLookup", "selectAddress", "editAddress", "confirmAddress", afterHeadingText)
      val welshLabels = languageLabels(welshMessages, "yourCountryLookup", "yourAddressLookup", "selectAddress", "editAddress", "confirmAddress", afterHeadingText)
      val labels = AddressLookupLabels(englishLabels, welshLabels)
      AddressLookupRequest(2, addressLookupOptions(), labels)
    }

    def yourAddressNoBodyRequest = constructYourAddressRequest(None)
    def yourAddressIndividualBodyRequest = constructYourAddressRequest(Some("yourAddressLookup.individual.afterHeadingText"))
    def yourAddressCompanyBodyRequest = constructYourAddressRequest(Some("yourAddressLookup.company.afterHeadingText"))
    def yourAddressLLPBodyRequest = constructYourAddressRequest(Some("yourAddressLookup.llp.afterHeadingText"))
    def yourAddressTrustBodyRequest = constructYourAddressRequest(Some("yourAddressLookup.trust.afterHeadingText"))
    def yourAddressEstateBodyRequest = constructYourAddressRequest(Some("yourAddressLookup.estate.afterHeadingText"))

    def individualLookupRequest = {
      val englishLabels = languageLabels(englishMessages, "individualCountryLookup", "individualAddressLookup", "selectIndividualAddress", "editIndividualAddress", "confirmIndividualAddress")
      val welshLabels = languageLabels(welshMessages, "individualCountryLookup", "individualAddressLookup", "selectIndividualAddress", "editIndividualAddress", "confirmIndividualAddress")
      val labels = AddressLookupLabels(englishLabels, welshLabels)
      AddressLookupRequest(2, addressLookupOptions(), labels)
    }

    def companyLookupRequest = {
      val englishLabels = languageLabels(englishMessages, "companyCountryLookup", "companyAddressLookup", "selectCompanyAddress", "editCompanyAddress", "confirmCompanyAddress")
      val welshLabels = languageLabels(welshMessages, "companyCountryLookup", "companyAddressLookup", "selectCompanyAddress", "editCompanyAddress", "confirmCompanyAddress")
      val labels = AddressLookupLabels(englishLabels, welshLabels)
      AddressLookupRequest(2, addressLookupOptions(), labels)
    }

    def llpLookupRequest = {
      val englishLabels = languageLabels(englishMessages, "llpCountryLookup", "llpAddressLookup", "selectLLPAddress", "editLLPAddress", "confirmLLPAddress")
      val welshLabels = languageLabels(welshMessages, "llpCountryLookup", "llpAddressLookup", "selectLLPAddress", "editLLPAddress", "confirmLLPAddress")
      val labels = AddressLookupLabels(englishLabels, welshLabels)
      AddressLookupRequest(2, addressLookupOptions(), labels)
    }

    def trustLookupRequest = {
      val englishLabels = languageLabels(englishMessages, "trustCountryLookup", "trustAddressLookup", "selectTrustAddress", "editTrustAddress", "confirmTrustAddress")
      val welshLabels = languageLabels(welshMessages, "trustCountryLookup", "trustAddressLookup", "selectTrustAddress", "editTrustAddress", "confirmTrustAddress")
      val labels = AddressLookupLabels(englishLabels, welshLabels)
      AddressLookupRequest(2, addressLookupOptions(), labels)
    }

    def estateLookupRequest = {
      val englishLabels = languageLabels(englishMessages, "estateCountryLookup", "estateAddressLookup", "selectEstateAddress", "editEstateAddress", "confirmEstateAddress")
      val welshLabels = languageLabels(welshMessages, "estateCountryLookup", "estateAddressLookup", "selectEstateAddress", "editEstateAddress", "confirmEstateAddress")
      val labels = AddressLookupLabels(englishLabels, welshLabels)
      AddressLookupRequest(2, addressLookupOptions(), labels)
    }

    def rentalLookupRequest = {
      val englishLabels = languageLabels(englishMessages, "yourCountryLookup", "addressLookup.rental", "selectAddress.rental", "editAddress.rental", "confirmAddress.rental", Some("addressLookup.rental.body"))
      val welshLabels = languageLabels(welshMessages, "yourCountryLookup", "addressLookup.rental", "selectAddress.rental", "editAddress.rental", "confirmAddress.rental", Some("addressLookup.rental.body"))
      val labels = AddressLookupLabels(englishLabels, welshLabels)
      AddressLookupRequest(2, addressLookupOptions(Some(true)), labels)
    }

    def languageLabels(messages: Messages, countryKey: String, lookupKey: String, selectKey: String, editKey: String, confirmKey: String, afterHeadingText: Option[String] = None) = {
      val appLevelLabels = AppLevelLabels(messages("service.name"))
      val countryPickerLabels = CountryPickerLabels(
        messages(s"$countryKey.title"), 
        messages(s"$countryKey.heading"),
        messages(s"$countryKey.hint"),
        messages("site.continue")
      )
      val lookupPageLabels = LookupPageLabels(
        messages(s"$lookupKey.title", 1), 
        messages(s"$lookupKey.heading", 1),
        messages("site.continue"),
        afterHeadingText.map(messages(_))
      )
      val selectPageLabels = SelectPageLabels(
        messages(s"$selectKey.title", 1), 
        messages(s"$selectKey.heading", 1)
      )
      val editPageLabels = EditPageLabels(
        messages(s"$editKey.title", 1), 
        messages(s"$editKey.heading", 1),
        messages("editAddress.line1Label"),
        messages("editAddress.line2Label"),
        messages("editAddress.line3Label"),
        messages("editAddress.townLabel"),
        messages("editAddress.postcodeLabel"),
        messages("editAddress.countryLabel")
      )
      val confirmPageLabels = ConfirmPageLabels(
        messages(s"$confirmKey.title"), 
        messages(s"$confirmKey.heading")
      )
      val internationalLabels = InternationalLabels(
        lookupPageLabels, 
        selectPageLabels, 
        editPageLabels, 
        confirmPageLabels
      )
      LabelsByLanguage(
        appLevelLabels, 
        countryPickerLabels, 
        lookupPageLabels, 
        selectPageLabels, 
        editPageLabels, 
        confirmPageLabels,
        internationalLabels
      )
    }

}

