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

package controllers

import controllers.actions._
import javax.inject.Inject
import models.{NormalMode, UserAnswers, RelatesTo}
import models.store.FullDisclosure
import models.store.notification._
import models.store.disclosure._
import navigation.Navigator
import pages._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{UAToNotificationService, SessionService, UAToDisclosureService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.TaskListView
import viewmodels.{TaskListRow, TaskListViewModel}
import play.api.i18n.Messages
import play.api.mvc.Call

class TaskListController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionService: SessionService,
                                        navigator: Navigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        uaToNotificationService: UAToNotificationService,
                                        uaToDisclosureService: UAToDisclosureService,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: TaskListView
                                    ) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val ua = request.userAnswers
      val fullDisclosure = uaToDisclosureService.uaToFullDisclosure(ua)

      val entity: String = ua.get(RelatesToPage).map(_.toString).getOrElse("none")
      val entityKey = if (fullDisclosure.disclosingAboutThemselves) "agent" else entity
      
      val (notificationSectionKey, personalDetailsRow) = buildYourPersonalDetailsRow(fullDisclosure.personalDetails, entityKey)
      val liabilitiesRows = buildLiabilitiesInformationRow(fullDisclosure.personalDetails.background, fullDisclosure.offshoreLiabilities)

      val list = TaskListViewModel(
        Seq(personalDetailsRow),
        buildCaseReferenceRow(fullDisclosure.caseReference) +: liabilitiesRows,
        Seq(buildOtherLiabilityIssueRow(fullDisclosure), buildTheReasonForComingForwardNowRow(fullDisclosure.reasonForDisclosingNow))
      )
      
      Ok(view(list, notificationSectionKey, isTheUserAgent(ua), entity, fullDisclosure.isComplete))
  }

  private def buildYourPersonalDetailsRow(personalDetails: PersonalDetails, entityKey: String)(implicit messages: Messages): (String, TaskListRow) = {
    val isSectionComplete = personalDetails.isComplete
    val firstPage = personalDetails.background.haveYouReceivedALetter
    val completeLink = controllers.notification.routes.CheckYourAnswersController.onPageLoad
    val incompleteLink = controllers.notification.routes.ReceivedALetterController.onPageLoad(NormalMode)
    
    val operationKey = getOperationKey(isSectionComplete, firstPage.isDefined)
    val notificationSectionKey = s"taskList.$operationKey.heading.first.$entityKey"

    (notificationSectionKey, buildRow("personal-detail", s"first.$entityKey", isSectionComplete, firstPage.isDefined, completeLink, incompleteLink))
  }

  private def buildCaseReferenceRow(caseReference: CaseReference)(implicit messages: Messages): TaskListRow = {
    val isSectionComplete = caseReference.isComplete
    val firstPage = caseReference.doYouHaveACaseReference
    val completeLink = controllers.reference.routes.DoYouHaveACaseReferenceController.onPageLoad(NormalMode)
    val incompleteLink = controllers.reference.routes.DoYouHaveACaseReferenceController.onPageLoad(NormalMode)

    buildRow("case-reference", "second", isSectionComplete, firstPage.isDefined, completeLink, incompleteLink)
  }

  private def buildOnshoreLiabilitieDetailRow(implicit messages: Messages): TaskListRow = {
    val firstPage = None
    val isSectionComplete = false
    val completeLink = routes.TaskListController.onPageLoad
    val incompleteLink = routes.TaskListController.onPageLoad

    buildRow("onshore-liabilities", "third", isSectionComplete, firstPage.isDefined, completeLink, incompleteLink)
  }

  private def buildOffshoreLiabilitieDetailRow(offshoreLiabilities: OffshoreLiabilities)(implicit messages: Messages): TaskListRow = {
    val firstPage = offshoreLiabilities.behaviour
    val isSectionComplete = offshoreLiabilities.isComplete
    val completeLink = controllers.offshore.routes.CheckYourAnswersController.onPageLoad
    val incompleteLink = controllers.offshore.routes.WhyAreYouMakingThisDisclosureController.onPageLoad(NormalMode)

    buildRow("offshore-liabilities", "forth", isSectionComplete, firstPage.isDefined, completeLink, incompleteLink)
  }

  private def buildOtherLiabilityIssueRow(fullDisclosure: FullDisclosure)(implicit messages: Messages): TaskListRow = {
    val firstPage = fullDisclosure.otherLiabilities.issues
    val isSectionComplete = fullDisclosure.otherLiabilities.isComplete(fullDisclosure.personalDetails.isAnIndividual)
    val completeLink = controllers.otherLiabilities.routes.CheckYourAnswersController.onPageLoad
    val incompleteLink =  otherLiabilities.routes.OtherLiabilityIssuesController.onPageLoad(NormalMode)

    buildRow("other-liability-issues", "fifth", isSectionComplete, firstPage.isDefined, completeLink, incompleteLink)
  }

  private def buildTheReasonForComingForwardNowRow(reasonForDisclosingNow: ReasonForDisclosingNow)(implicit messages: Messages): TaskListRow = {
    val firstPage = reasonForDisclosingNow.reason
    val isSectionComplete = reasonForDisclosingNow.isComplete
    val completeLink = controllers.reason.routes.CheckYourAnswersController.onPageLoad
    val incompleteLink = reason.routes.WhyAreYouMakingADisclosureController.onPageLoad(NormalMode)

    buildRow("reason-for-coming-forward-now", "sixth", isSectionComplete, firstPage.isDefined, completeLink, incompleteLink)
  }

  private def buildRow(prefix: String, titleKeySuffix: String, isSectionComplete: Boolean, isFirstPageDefined: Boolean, completeLink: Call, incompleteLink: Call)(implicit messages: Messages) = {
    val operationKey = getOperationKey(isSectionComplete, isFirstPageDefined)
    val titleKey = s"taskList.$operationKey.sectionTitle.$titleKeySuffix"
    val link = if (isSectionComplete) completeLink else incompleteLink
    TaskListRow(
      id = s"$prefix-task-list",
      sectionTitle = messages(titleKey),
      status = getStatusMessage(isSectionComplete, isFirstPageDefined),
      link = link
    )
  }

  def getOperationKey(isSectionComplete: Boolean, isFirstPageDefined: Boolean) = if (isSectionComplete) "edit" else "add"
  
  private def buildLiabilitiesInformationRow(background: Background, offshoreLiabilities: OffshoreLiabilities)(implicit messages: Messages): Seq[TaskListRow] = {
    (background.offshoreLiabilities, background.onshoreLiabilities) match {
      case (Some(true),  Some(true))  => Seq(buildOnshoreLiabilitieDetailRow, buildOffshoreLiabilitieDetailRow(offshoreLiabilities))
      case (Some(true),  _)           => Seq(buildOffshoreLiabilitieDetailRow(offshoreLiabilities))
      case (_,           Some(true))  => Seq(buildOnshoreLiabilitieDetailRow)
      case (Some(false), _)           => Seq(buildOnshoreLiabilitieDetailRow)
      case (_,           _)           => Nil
    }
  }

  def isTheUserAgent(userAnswers: UserAnswers): Boolean ={
    userAnswers.get(RelatesToPage) match {
      case Some(RelatesTo.AnIndividual) => userAnswers.get(AreYouTheIndividualPage).getOrElse(true)
      case Some(RelatesTo.ACompany) => userAnswers.get(AreYouAnOfficerOfTheCompanyThatTheDisclosureWillBeAboutPage).getOrElse(true)
      case Some(RelatesTo.ALimitedLiabilityPartnership) => userAnswers.get(AreYouADesignatedMemberOfTheLLPThatTheDisclosureWillBeAboutPage).getOrElse(true)
      case Some(RelatesTo.ATrust) => userAnswers.get(AreYouTrusteeOfTheTrustThatTheDisclosureWillBeAboutPage).getOrElse(true)
      case Some(RelatesTo.AnEstate) => userAnswers.get(AreYouTheExecutorOfTheEstatePage).getOrElse(true)
      case _ => true
    }
  }

  private def getStatusMessage(isSectionComplete: Boolean, isInProgress: Boolean)(implicit messages: Messages): String =
    messages(
      if (isSectionComplete) "taskList.status.completed"
      else if (isInProgress) "taskList.status.inProgress"
      else "taskList.status.notStarted"
    )

}
