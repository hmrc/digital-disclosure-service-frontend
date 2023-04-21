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
import play.api.i18n.Messages
import controllers.routes
import models._
import pages._

trait AddressLookupRequestHelper {

  val API_VERSION = 2

  def lookupRequestForYourAddress(timeout: Int, baseUrl: String, 
                                        redirectUrl: String, 
                                        proposalListLimit: Int, 
                                        userAnswers: UserAnswers,
                                        languageTranslationEnabled: Boolean)(implicit messages: Messages): AddressLookupRequest = {

    lookupRequestForAddress(timeout, baseUrl, redirectUrl, proposalListLimit, languageTranslationEnabled,
                            "yourCountryLookup.title", 
                            "yourCountryLookup.heading",
                            "yourCountryLookup.hint",
                            "yourAddressLookup.title", 
                            "yourAddressLookup.heading",
                            getAgentSpecificHeading(userAnswers),
                            "selectAddress.title", 
                            "selectAddress.heading",
                            "editAddress.title", 
                            "editAddress.heading",
                            "confirmAddress.title", 
                            "confirmAddress.heading")
  }

  def lookupRequestForIndividualAddress(timeout: Int, baseUrl: String, 
                                        redirectUrl: String, 
                                        proposalListLimit: Int,
                                        languageTranslationEnabled: Boolean)(implicit messages: Messages): AddressLookupRequest = {                                      

    lookupRequestForAddress(timeout, baseUrl, redirectUrl, proposalListLimit, languageTranslationEnabled,
                            "individualCountryLookup.title", 
                            "individualCountryLookup.heading",
                            "individualCountryLookup.hint",
                            "individualAddressLookup.title", 
                            "individualAddressLookup.heading",
                            None,
                            "selectIndividualAddress.title", 
                            "selectIndividualAddress.heading",
                            "editIndividualAddress.title", 
                            "editIndividualAddress.heading",
                            "confirmIndividualAddress.title", 
                            "confirmIndividualAddress.heading")
  }

  def lookupRequestForCompanyAddress(timeout: Int, baseUrl: String, 
                                     redirectUrl: String, 
                                     proposalListLimit: Int,
                                     languageTranslationEnabled: Boolean)(implicit messages: Messages): AddressLookupRequest = {                                      

    lookupRequestForAddress(timeout, baseUrl, redirectUrl, proposalListLimit, languageTranslationEnabled,
                            "companyCountryLookup.title", 
                            "companyCountryLookup.heading",
                            "companyCountryLookup.hint",
                            "companyAddressLookup.title", 
                            "companyAddressLookup.heading",
                            None,
                            "selectCompanyAddress.title", 
                            "selectCompanyAddress.heading",
                            "editCompanyAddress.title", 
                            "editCompanyAddress.heading",
                            "confirmCompanyAddress.title", 
                            "confirmCompanyAddress.heading")
  }

  def lookupRequestForLLPAddress(timeout: Int, baseUrl: String, 
                                     redirectUrl: String, 
                                     proposalListLimit: Int,
                                     languageTranslationEnabled: Boolean)(implicit messages: Messages): AddressLookupRequest = {                                      

    lookupRequestForAddress(timeout, baseUrl, redirectUrl, proposalListLimit, languageTranslationEnabled,
                            "llpCountryLookup.title", 
                            "llpCountryLookup.heading",
                            "llpCountryLookup.hint",
                            "llpAddressLookup.title", 
                            "llpAddressLookup.heading",
                            None,
                            "selectLLPAddress.title", 
                            "selectLLPAddress.heading",
                            "editLLPAddress.title", 
                            "editLLPAddress.heading",
                            "confirmLLPAddress.title", 
                            "confirmLLPAddress.heading")
  }

  def lookupRequestForTrustAddress(timeout: Int, baseUrl: String, 
                                     redirectUrl: String, 
                                     proposalListLimit: Int,
                                     languageTranslationEnabled: Boolean)(implicit messages: Messages): AddressLookupRequest = {                                      

    lookupRequestForAddress(timeout, baseUrl, redirectUrl, proposalListLimit, languageTranslationEnabled,
                            "trustCountryLookup.title", 
                            "trustCountryLookup.heading",
                            "trustCountryLookup.hint",
                            "trustAddressLookup.title", 
                            "trustAddressLookup.heading",
                            None,
                            "selectTrustAddress.title", 
                            "selectTrustAddress.heading",
                            "editTrustAddress.title", 
                            "editTrustAddress.heading",
                            "confirmTrustAddress.title", 
                            "confirmTrustAddress.heading")
  }

  def lookupRequestForEstateAddress(timeout: Int, baseUrl: String, 
                                     redirectUrl: String, 
                                     proposalListLimit: Int,
                                     languageTranslationEnabled: Boolean)(implicit messages: Messages): AddressLookupRequest = {                                      

    lookupRequestForAddress(timeout, baseUrl, redirectUrl, proposalListLimit, languageTranslationEnabled,
                            "estateCountryLookup.title", 
                            "estateCountryLookup.heading",
                            "estateCountryLookup.hint",
                            "estateAddressLookup.title", 
                            "estateAddressLookup.heading",
                            None,
                            "selectEstateAddress.title", 
                            "selectEstateAddress.heading",
                            "editEstateAddress.title", 
                            "editEstateAddress.heading",
                            "confirmEstateAddress.title", 
                            "confirmEstateAddress.heading")
  }

  def lookupRequestForRentalAddress(timeout: Int, baseUrl: String, 
                                    redirectUrl: String, 
                                    proposalListLimit: Int, 
                                    languageTranslationEnabled: Boolean,
                                    addressIndex: Int)(implicit messages: Messages): AddressLookupRequest = {

    lookupRequestForAddress(timeout, baseUrl, redirectUrl, proposalListLimit, languageTranslationEnabled,
                            "yourCountryLookup.title", 
                            "yourCountryLookup.heading",
                            "yourCountryLookup.hint",
                            messages("addressLookup.rental.title", addressIndex+1), 
                            messages("addressLookup.rental.heading", addressIndex+1),
                            Some("addressLookup.rental.body"),
                            messages("selectAddress.rental.title", addressIndex+1), 
                            messages("selectAddress.rental.heading", addressIndex+1),
                            messages("editAddress.rental.title", addressIndex+1), 
                            messages("editAddress.rental.heading", addressIndex+1),
                            "confirmAddress.rental.title", 
                            "confirmAddress.rental.heading",
                            Some(true))
  }

  def lookupRequestForAddress(timeout: Int, baseUrl: String, 
                              redirectUrl: String, 
                              proposalListLimit: Int,
                              languageTranslationEnabled: Boolean,
                              countryLookupTitle: String, 
                              countryLookupHeading: String,
                              countryLookupHint: String,
                              addressLookupTitle: String, 
                              addressLookupHeading: String,
                              afterHeadingText: Option[String],
                              selectAddressTitle: String, 
                              selectAddressHeading: String,
                              editAddressTitle: String, 
                              editAddressHeading: String,
                              confirmAddressTitle: String, 
                              confirmAddressHeading: String,
                              ukMode: Option[Boolean] = None)(implicit messages: Messages): AddressLookupRequest = {

    val selectPageConfig = SelectPageConfig(proposalListLimit = proposalListLimit)
    val timeoutConfig = TimeoutConfig(timeoutAmount = timeout, timeoutUrl = controllers.auth.routes.AuthController.signOut.url, timeoutKeepAliveUrl = Some(routes.KeepAliveController.keepAlive.url))
    val addressLookupOptions = AddressLookupOptions(
      continueUrl = s"$baseUrl$redirectUrl",
      serviceHref = s"$baseUrl${routes.IndexController.onPageLoad.url}",
      signOutHref = Some(s"$baseUrl${controllers.auth.routes.AuthController.signOut.url}"),
      showPhaseBanner = Some(true),
      alphaPhase = false,
      disableTranslations = !languageTranslationEnabled,
      selectPageConfig = Some(selectPageConfig),
      includeHMRCBranding = Some(false),
      timeoutConfig = Some(timeoutConfig),
      ukMode = ukMode
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
      messages("site.continue"),
      afterHeadingText.map(messages(_))
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

  def getAgentSpecificHeading(ua: UserAnswers) = {
    ua.get(RelatesToPage) match {
      case Some(RelatesTo.AnIndividual) => getIndividualSpecificHeading(ua)
      case Some(RelatesTo.ACompany) => getCompanySpecificHeading(ua)
      case Some(RelatesTo.ALimitedLiabilityPartnership) => getLLPSpecificHeading(ua)
      case Some(RelatesTo.ATrust) => getTrustSpecificHeading(ua)
      case Some(RelatesTo.AnEstate) => getEstateSpecificHeading(ua)
      case _ => None
    } 
  }

  def getAgentHeading(ua: UserAnswers, agentKey: String): Option[String] = {
    ua.get(AreYouTheEntityPage) match {
      case Some(AreYouTheEntity.YesIAm) => None
      case _ => Some(agentKey)
    }
  }

  def getIndividualSpecificHeading(ua: UserAnswers): Option[String] = getAgentHeading(ua, "yourAddressLookup.individual.afterHeadingText")
  def getCompanySpecificHeading(ua: UserAnswers): Option[String] = getAgentHeading(ua, "yourAddressLookup.company.afterHeadingText")
  def getLLPSpecificHeading(ua: UserAnswers): Option[String] = getAgentHeading(ua, "yourAddressLookup.llp.afterHeadingText")
  def getTrustSpecificHeading(ua: UserAnswers): Option[String] = getAgentHeading(ua, "yourAddressLookup.trust.afterHeadingText")
  def getEstateSpecificHeading(ua: UserAnswers): Option[String] = getAgentHeading(ua, "yourAddressLookup.estate.afterHeadingText")
}
