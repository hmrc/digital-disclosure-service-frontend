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

import models._
import models.OffshoreLiabilities._
import models.store.notification._
import pages._
import scala.util.Try
import play.api.libs.json.Writes

class NotificationDataService {
  
  // def userAnswersToNotification(ua: UserAnswers): Notification = {

  //     userId: String,
  //     notificationId: String,
  //     lastUpdated: Instant,
  //     metadata: Metadata,
  //     background: Background,
  //     aboutYou: AboutYou,
  //     aboutTheIndividual: Option[AboutTheIndividual] = None,
  //     aboutTheCompany: Option[AboutTheCompany] = None,
  //     aboutTheTrust: Option[AboutTheTrust] = None,
  //     aboutTheLLP: Option[AboutTheLLP] = None


  // }

  def notificationToUserAnswers(notification: Notification): Try[UserAnswers] = {

    //  userAnswers.set(page, value)


      //RelatesToPage -> notification.background.disclosureEntity.entity,
      //???  -> notification.background.disclosureEntity.areYouTheEntity,


    val backgroundPages: List[PageWithValue[_]] = List(
      notification.background.haveYouReceivedALetter.map(PageWithValue(ReceivedALetterPage, _)),
      notification.background.letterReferenceNumber.map(PageWithValue(LetterReferencePage, _)),
      notification.background.areYouRepresetingAnOrganisation.map(PageWithValue(AreYouRepresentingAnOrganisationPage, _)),
      notification.background.organisationName.map(PageWithValue(WhatIsTheNameOfTheOrganisationYouRepresentPage, _)),
      notification.background.offshoreLiabilities.map(bool => PageWithValue(OffshoreLiabilitiesPage, OffshoreLiabilities.booleanToOffshoreLiabilities(bool))),
      notification.background.onshoreLiabilities.map(bool => PageWithValue(OnshoreLiabilitiesPage, OnshoreLiabilities.booleanToOnshoreLiabilities(bool))) 
    ).flatten

    val entityPages: List[PageWithValue[_]] = notification.background.disclosureEntity.map(de => de.entity match {
      case Individual => List(Some(PageWithValue(RelatesToPage, RelatesTo.AnIndividual)), de.areYouTheEntity.map(PageWithValue(AreYouTheIndividualPage, AreYouTheIndividual.Yes)))
      case Company => List(Some(PageWithValue(RelatesToPage, RelatesTo.ACompany)), de.areYouTheEntity.map(PageWithValue(AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage, AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAbout.Yes)))
      case Estate => List(Some(PageWithValue(RelatesToPage, RelatesTo.AnIndividual)), de.areYouTheEntity.map(PageWithValue(AreYouTheIndividualPage, AreYouTheIndividual.Yes)))
      case LLP => List(Some(PageWithValue(RelatesToPage, RelatesTo.AnLLP)), de.areYouTheEntity.map(PageWithValue(AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage, AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAbout.Yes)))
      case Trust => List(Some(PageWithValue(RelatesToPage, RelatesTo.ATrust)), de.areYouTheEntity.map(PageWithValue(AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage, AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAbout.Yes)))
    })

    pagesToUserAnswers(pagesWithValues, UserAnswers(notification.userId))
  }

  def pagesToUserAnswers(pages: List[PageWithValue[_]], userAnswers: UserAnswers): Try[UserAnswers] = {
    pages.foldLeft(Try(userAnswers)){ (tryUa, page) => tryUa.flatMap(page.addToUserAnswers(_)) }
  }

}

case class PageWithValue[A](page: QuestionPage[A], value: A)(implicit writes: Writes[A]) {
  def addToUserAnswers(userAnswers: UserAnswers): Try[UserAnswers] = {
    userAnswers.set(page, value)
  }
}
