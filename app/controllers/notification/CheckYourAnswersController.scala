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

package controllers.notification

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import models.{SubmissionType, SummaryLists}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.govuk.summarylist._
import views.html.notification.CheckYourAnswersView
import viewmodels.checkAnswers._
import pages._
import models.{AreYouTheEntity, RelatesTo, UserAnswers}
import services.NotificationSubmissionService

import scala.concurrent.{ExecutionContext, Future}
import navigation.NotificationNavigator
import viewmodels.RevealFullText

class CheckYourAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  navigator: NotificationNavigator,
  val controllerComponents: MessagesControllerComponents,
  view: CheckYourAnswersView,
  notificationSubmissionService: NotificationSubmissionService,
  revealFullText: RevealFullText
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val ua = request.userAnswers

    val backgroundList = SummaryListViewModel(
      rows = Seq(
        ReceivedALetterSummary.row(ua),
        LetterReferenceSummary.row(ua),
        RelatesToSummary.row(ua),
        AreYouTheEntitySummary.row(ua),
        AreYouRepresentingAnOrganisationSummary.row(ua),
        WhatIsTheNameOfTheOrganisationYouRepresentSummary.row(ua),
        LiabilitiesSummary.row(ua),
        IncomeOrGainSourceSummary.row(ua),
        OtherIncomeOrGainSourceSummary.row(ua, revealFullText)
      ).flatten
    )

    val aboutYouList = SummaryListViewModel(
      rows = Seq(
        WhatIsYourFullNameSummary.row(ua),
        HowWouldYouPreferToBeContactedSummary.row(ua),
        YourEmailAddressSummary.row(ua),
        YourPhoneNumberSummary.row(ua),
        YourAddressLookupSummary.row(ua),
        WhatIsYourDateOfBirthSummary.row(ua),
        WhatIsYourMainOccupationSummary.row(ua),
        DoYouHaveNationalInsuranceNumberSummary.row(ua),
        WhatIsYourNationalInsuranceNumberSummary.row(ua),
        AreYouRegisteredForVATSummary.row(ua),
        WhatIsYourVATRegistrationNumberSummary.row(ua),
        AreYouRegisteredForSelfAssessmentSummary.row(ua),
        WhatIsYourUniqueTaxReferenceSummary.row(ua)
      ).flatten
    )

    val aboutTheIndividualList = request.userAnswers.get(RelatesToPage) match {
      case Some(RelatesTo.AnIndividual) if !request.userAnswers.isTheUserTheIndividual =>
        Some(
          SummaryListViewModel(
            rows = Seq(
              WhatIsTheIndividualsFullNameSummary.row(ua),
              WhatIsTheIndividualDateOfBirthSummary.row(ua),
              WhatIsTheIndividualOccupationSummary.row(ua),
              DoesTheIndividualHaveNationalInsuranceNumberSummary.row(ua),
              WhatIsIndividualsNationalInsuranceNumberSummary.row(ua),
              IsTheIndividualRegisteredForVATSummary.row(ua),
              WhatIsTheIndividualsVATRegistrationNumberSummary.row(ua),
              IsTheIndividualRegisteredForSelfAssessmentSummary.row(ua),
              WhatIsTheIndividualsUniqueTaxReferenceSummary.row(ua),
              IndividualAddressLookupSummary.row(ua)
            ).flatten
          )
        )
      case _                                                                           => None
    }

    val aboutTheCompanyList = request.userAnswers.get(RelatesToPage) match {
      case Some(RelatesTo.ACompany) =>
        Some(
          SummaryListViewModel(
            rows = Seq(
              WhatIsTheNameOfTheCompanyTheDisclosureWillBeAboutSummary.row(ua),
              WhatIsTheCompanyRegistrationNumberSummary.row(ua),
              CompanyAddressLookupSummary.row(ua)
            ).flatten
          )
        )
      case _                        => None
    }

    val aboutTheLLPList = request.userAnswers.get(RelatesToPage) match {
      case Some(RelatesTo.ALimitedLiabilityPartnership) =>
        Some(
          SummaryListViewModel(
            rows = Seq(
              WhatIsTheLLPNameSummary.row(ua),
              LLPAddressLookupSummary.row(ua)
            ).flatten
          )
        )
      case _                                            => None
    }

    val aboutTheTrust = request.userAnswers.get(RelatesToPage) match {
      case Some(RelatesTo.ATrust) =>
        Some(
          SummaryListViewModel(
            rows = Seq(
              WhatIsTheTrustNameSummary.row(ua),
              TrustAddressLookupSummary.row(ua)
            ).flatten
          )
        )
      case _                      => None
    }

    val aboutThePersonWhoDied = request.userAnswers.get(RelatesToPage) match {
      case Some(RelatesTo.AnEstate) =>
        Some(
          SummaryListViewModel(
            rows = Seq(
              WhatWasTheNameOfThePersonWhoDiedSummary.row(ua),
              WhatWasThePersonDateOfBirthSummary.row(ua),
              WhatWasThePersonOccupationSummary.row(ua),
              DidThePersonHaveNINOSummary.row(ua),
              WhatWasThePersonNINOSummary.row(ua),
              WasThePersonRegisteredForVATSummary.row(ua),
              WhatWasThePersonVATRegistrationNumberSummary.row(ua),
              WasThePersonRegisteredForSASummary.row(ua),
              WhatWasThePersonUTRSummary.row(ua),
              EstateAddressLookupSummary.row(ua)
            ).flatten
          )
        )
      case _                        => None
    }

    val list = SummaryLists(
      backgroundList,
      aboutYouList,
      aboutTheIndividualList,
      aboutTheCompanyList,
      aboutTheLLPList,
      aboutTheTrust,
      aboutThePersonWhoDied
    )

    val isTheEntity = isTheUserTheEntity(request.userAnswers)

    val isNotification = request.userAnswers.submissionType == SubmissionType.Notification
    Ok(view(list, isTheEntity, isNotification, request.isAgent))
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val userAnswers = request.userAnswers

    userAnswers.metadata.reference match {
      case Some(ref) => Future.successful(Redirect(navigator.submitPage(request.userAnswers, ref)))
      case None      =>
        for {
          reference    <- notificationSubmissionService.submitNotification(request.userAnswers)
          userReference = userAnswers.get(LetterReferencePage).getOrElse(reference)
        } yield Redirect(navigator.submitPage(request.userAnswers, userReference))
    }
  }

  def isTheUserTheEntity(userAnswers: UserAnswers): Boolean =
    userAnswers.get(AreYouTheEntityPage).map(_ == AreYouTheEntity.YesIAm).getOrElse(true)

}
