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

package controllers

import controllers.actions._
import javax.inject.Inject
import models.{NormalMode, UserAnswers}
import navigation.NotificationNavigator
import pages._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.TaskListView
import viewmodels.{TaskListRow, TaskListViewModel}
import play.api.i18n.Messages

import scala.concurrent.{ExecutionContext, Future}

class TaskListController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionService: SessionService,
                                        navigator: NotificationNavigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: TaskListView
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val ua = request.userAnswers

      val personalDetailsTasks = Seq(buildYourPersonalDetailsRow)

      val liabilitiesInformation = buildLiabilitiesInformationRow(ua)

      val additionalInformation = Seq(buildTheReasonForComingForwardNowRow)

      val list = TaskListViewModel(
        personalDetailsTasks,
        liabilitiesInformation,
        additionalInformation
      )

      Ok(view(list))
  }

  private def buildYourPersonalDetailsRow()(implicit messages: Messages): TaskListRow = {
    TaskListRow(
      id = "personal-detail-task-list", 
      operation = messages("taskList.op.add"),
      sectionTitle = messages("taskList.sectionTitle.first"), 
      status = messages("taskList.status.notStarted"), 
      link = routes.TaskListController.onPageLoad
    )
  }

  private def buildCaseReferenceRow()(implicit messages: Messages): TaskListRow = {
    TaskListRow(
      id = "case-reference-task-list", 
      operation = messages("taskList.op.add"),
      sectionTitle = messages("taskList.sectionTitle.second"), 
      status = messages("taskList.status.notStarted"), 
      link = routes.TaskListController.onPageLoad
    )
  }

  private def buildOnshoreLiabilitieDetailRow()(implicit messages: Messages): TaskListRow = {
    TaskListRow(
      id = "onshore-liabilitie-task-list", 
      operation = messages("taskList.op.add"),
      sectionTitle = messages("taskList.sectionTitle.third"), 
      status = messages("taskList.status.notStarted"), 
      link = routes.TaskListController.onPageLoad
    )
  }

  private def buildOffshoreLiabilitieDetailRow(userAnswers: UserAnswers)(implicit messages: Messages): TaskListRow = {
    TaskListRow(
      id = "offshore-liabilitie-task-list", 
      operation = messages("taskList.op.add"),
      sectionTitle = messages("taskList.sectionTitle.forth"), 
      status = messages("taskList.status.notStarted"), 
      link = offshore.routes.WhyAreYouMakingThisDisclosureController.onPageLoad(NormalMode)
    )
  }

  private def buildOtherLiabilityIssueRow()(implicit messages: Messages): TaskListRow = {
    TaskListRow(
      id = "other-liability-issue-task-list", 
      operation = messages("taskList.op.add"),
      sectionTitle = messages("taskList.sectionTitle.fifth"), 
      status = messages("taskList.status.notStarted"), 
      link = routes.TaskListController.onPageLoad
    )
  }

  private def buildTheReasonForComingForwardNowRow()(implicit messages: Messages): TaskListRow = {
    TaskListRow(
      id = "reason-for-coming-forward-now-liabilitie-task-list", 
      operation = messages("taskList.op.add"),
      sectionTitle = messages("taskList.sectionTitle.sixth"), 
      status = messages("taskList.status.notStarted"), 
      link = routes.TaskListController.onPageLoad
    )
  }

  private def buildLiabilitiesInformationRow(userAnswers: UserAnswers)(implicit messages: Messages): Seq[TaskListRow] = {
    val offshore = userAnswers.get(OffshoreLiabilitiesPage) 
    val onshore = userAnswers.get(OnshoreLiabilitiesPage)

    (offshore, onshore) match {
      case (None, None) => Seq(buildCaseReferenceRow, buildOtherLiabilityIssueRow)
      case (Some(true), None) => Seq(buildCaseReferenceRow, buildOtherLiabilityIssueRow)
      case (Some(true), Some(true)) => Seq(buildCaseReferenceRow, buildOnshoreLiabilitieDetailRow, buildOffshoreLiabilitieDetailRow(userAnswers), buildOtherLiabilityIssueRow)
      case (Some(true), Some(false)) => Seq(buildCaseReferenceRow, buildOffshoreLiabilitieDetailRow(userAnswers), buildOtherLiabilityIssueRow)
      case (Some(false), _) => Seq(buildCaseReferenceRow, buildOnshoreLiabilitieDetailRow, buildOtherLiabilityIssueRow)
    }
  }
}
