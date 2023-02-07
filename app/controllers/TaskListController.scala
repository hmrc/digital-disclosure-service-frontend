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
import navigation.Navigator
import pages._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{UAToNotificationService, SessionService, UAToDisclosureService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.TaskListView
import viewmodels.{TaskListRow, TaskListViewModel}
import play.api.i18n.Messages

class TaskListController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionService: SessionService,
                                        navigator: Navigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        dataService: UAToNotificationService,
                                        dateDisclosureService: UAToDisclosureService,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: TaskListView
                                    ) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val ua = request.userAnswers

      val areTheyTheIndividual = isTheUserTheIndividual(ua)
      val entity = ua.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      val isSectionComplete = dataService.userAnswersToNotification(ua).isComplete
      val operationKey = if (isSectionComplete) "edit" else "add"
      val entityKey = if (areTheyTheIndividual) "agent" else entity
      val notificationSectionKey = s"taskList.$entityKey.$operationKey.heading.first"
      val notificationTitleKey = s"taskList.$entityKey.$operationKey.sectionTitle.first"
      
      val personalDetailsTasks = Seq(buildYourPersonalDetailsRow(notificationTitleKey, isSectionComplete))

      val liabilitiesInformation = buildLiabilitiesInformationRow(ua)

      val additionalInformation = Seq(buildOtherLiabilityIssueRow(ua), buildTheReasonForComingForwardNowRow(ua))

      val list = TaskListViewModel(
        personalDetailsTasks,
        liabilitiesInformation,
        additionalInformation
      )

      Ok(view(list, notificationSectionKey, isTheUserAgent(ua), entity, false))
  }

  def statusKey(isSectionComplete: Boolean): String = if (isSectionComplete) "taskList.status.completed" else "taskList.status.notStarted"
  
  private def buildYourPersonalDetailsRow(notificationTitleKey: String, isSectionComplete: Boolean)(implicit messages: Messages): TaskListRow = {

    val link = if (isSectionComplete) controllers.notification.routes.CheckYourAnswersController.onPageLoad
      else controllers.notification.routes.ReceivedALetterController.onPageLoad(NormalMode)

    TaskListRow(
      id = "personal-detail-task-list", 
      sectionTitle = messages(notificationTitleKey), 
      status = messages(statusKey(isSectionComplete)), 
      link = link
    )
  }

  private def buildCaseReferenceRow(userAnswers: UserAnswers)(implicit messages: Messages): TaskListRow = {

    val isSectionComplete = dateDisclosureService.uaToCaseReference(userAnswers).isComplete
    val firstPage = userAnswers.get(DoYouHaveACaseReferencePage)
    val operationKey = if (isSectionComplete) "edit" else "add"
    val caseReferenceTitleKey = s"taskList.$operationKey.sectionTitle.second"

    val link = controllers.reference.routes.DoYouHaveACaseReferenceController.onPageLoad(NormalMode)

    TaskListRow(
      id = "case-reference-task-list",
      sectionTitle = messages(caseReferenceTitleKey),
      status = getStatusMessage(isSectionComplete, firstPage.isDefined),
      link = link
    )
  }

  private def buildOnshoreLiabilitieDetailRow(implicit messages: Messages): TaskListRow = {
    TaskListRow(
      id = "onshore-liabilitie-task-list", 
      sectionTitle = messages("taskList.sectionTitle.third"), 
      status = messages("taskList.status.notStarted"), 
      link = routes.TaskListController.onPageLoad
    )
  }

  private def buildOffshoreLiabilitieDetailRow(userAnswers: UserAnswers)(implicit messages: Messages): TaskListRow = {

    val firstPage = userAnswers.get(WhyAreYouMakingThisDisclosurePage)
    val isSectionComplete = dateDisclosureService.uaToOffshoreLiabilities(userAnswers).isComplete
    val operationKey = if (isSectionComplete || firstPage.isDefined) "edit" else "add"
    val offshoreLiabilitieTitleKey = s"taskList.$operationKey.sectionTitle.forth"

    val link = if (isSectionComplete) controllers.offshore.routes.CheckYourAnswersController.onPageLoad
      else controllers.offshore.routes.WhyAreYouMakingThisDisclosureController.onPageLoad(NormalMode)

    TaskListRow(
      id = "offshore-liabilitie-task-list",
      sectionTitle = messages(offshoreLiabilitieTitleKey),
      status = getStatusMessage(isSectionComplete, firstPage.isDefined),
      link = link
    )
  }

  private def buildOtherLiabilityIssueRow(userAnswers: UserAnswers)(implicit messages: Messages): TaskListRow = {

    val firstPage = userAnswers.get(OtherLiabilityIssuesPage)
    val isAnIndividual = userAnswers.get(RelatesToPage) match {
      case Some(RelatesTo.AnIndividual) => true 
      case Some(RelatesTo.AnEstate) => true
      case _ => false
    }
    val isSectionComplete = dateDisclosureService.uaToOtherLiabilities(userAnswers).isComplete(isAnIndividual)
    val operationKey = if (isSectionComplete || firstPage.isDefined) "edit" else "add"
    val otherLiabilitiesTitleKey = s"taskList.$operationKey.sectionTitle.fifth"

    val link = if(isSectionComplete) controllers.otherLiabilities.routes.CheckYourAnswersController.onPageLoad
    else otherLiabilities.routes.OtherLiabilityIssuesController.onPageLoad(NormalMode)

    TaskListRow(
      id = "other-liability-issue-task-list",
      sectionTitle = messages(otherLiabilitiesTitleKey),
      status = getStatusMessage(isSectionComplete, firstPage.isDefined),
      link = link
    )
  }

  private def buildTheReasonForComingForwardNowRow(userAnswers: UserAnswers)(implicit messages: Messages): TaskListRow = {

    val firstPage = userAnswers.get(WhyAreYouMakingADisclosurePage)
    val isSectionComplete = dateDisclosureService.uaToReasonForDisclosingNow(userAnswers).isComplete
    val operationKey = if (isSectionComplete || firstPage.isDefined) "edit" else "add"
    val reasonForComingForwardNowTitleKey = s"taskList.$operationKey.sectionTitle.sixth"
    val link = if(isSectionComplete) controllers.reason.routes.CheckYourAnswersController.onPageLoad
    else reason.routes.WhyAreYouMakingADisclosureController.onPageLoad(NormalMode)

    TaskListRow(
      id = "reason-for-coming-forward-now-liabilitie-task-list",
      sectionTitle = messages(reasonForComingForwardNowTitleKey),
      status = getStatusMessage(isSectionComplete, firstPage.isDefined),
      link = link
    )
  }
  
  private def buildLiabilitiesInformationRow(userAnswers: UserAnswers)(implicit messages: Messages): Seq[TaskListRow] = {
    val offshore = userAnswers.get(OffshoreLiabilitiesPage) 
    val onshore = userAnswers.get(OnshoreLiabilitiesPage)

    (offshore, onshore) match {
      case (Some(true),  Some(true))  => Seq(buildCaseReferenceRow(userAnswers), buildOnshoreLiabilitieDetailRow, buildOffshoreLiabilitieDetailRow(userAnswers))
      case (Some(true),  _)           => Seq(buildCaseReferenceRow(userAnswers), buildOffshoreLiabilitieDetailRow(userAnswers))
      case (_,           Some(true))  => Seq(buildCaseReferenceRow(userAnswers), buildOnshoreLiabilitieDetailRow)
      case (Some(false), _)           => Seq(buildCaseReferenceRow(userAnswers), buildOnshoreLiabilitieDetailRow)
      case (_,           _)           => Seq(buildCaseReferenceRow(userAnswers))
    }
  }

  def isTheUserTheIndividual(userAnswers: UserAnswers): Boolean = {
    userAnswers.get(AreYouTheIndividualPage) match {
      case Some(true) => true
      case _ => false
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
