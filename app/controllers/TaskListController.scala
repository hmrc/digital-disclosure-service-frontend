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
import models.{AreYouTheEntity, NormalMode, UserAnswers}
import models.store.FullDisclosure
import models.store.notification._
import models.store.disclosure._
import pages._
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{UAToDisclosureService, UAToSubmissionService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.TaskListView
import viewmodels.{TaskListRow, TaskListViewModel}
import play.api.i18n.Messages
import play.api.mvc.Call

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class TaskListController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  uaToDisclosureService: UAToDisclosureService,
  uaToSubmissionService: UAToSubmissionService,
  val controllerComponents: MessagesControllerComponents,
  view: TaskListView
) extends FrontendBaseController
    with I18nSupport
    with Logging {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val ua             = request.userAnswers
    val fullDisclosure = uaToDisclosureService.uaToFullDisclosure(ua)

    val entity: String = ua.get(RelatesToPage).map(_.toString).getOrElse("none")
    val entityKey      = if (fullDisclosure.disclosingAboutThemselves) "agent" else entity

    val (notificationSectionKey, personalDetailsRow) =
      buildYourPersonalDetailsRow(fullDisclosure.personalDetails, entityKey)
    val liabilitiesRows                              = buildLiabilitiesInformationRow(
      fullDisclosure.personalDetails.background,
      fullDisclosure.onshoreLiabilities,
      fullDisclosure.offshoreLiabilities,
      ua.madeDeclaration
    )

    val list        = TaskListViewModel(
      Seq(personalDetailsRow),
      Seq(
        buildCaseReferenceRow(fullDisclosure.caseReference),
        buildDeclarationRow(ua.madeDeclaration)
      ) ++ liabilitiesRows,
      Seq(
        buildOtherLiabilityIssueRow(
          fullDisclosure.otherLiabilities,
          fullDisclosure.personalDetails.isAnIndividual,
          ua.madeDeclaration
        ),
        buildTheReasonForComingForwardNowRow(fullDisclosure.reasonForDisclosingNow, ua.madeDeclaration)
      )
    )
    val isUserAgent = request.isAgent

    val title   = getTitle(isUserAgent, "title", ua)
    val heading = getTitle(isUserAgent, "heading", ua)
    Ok(
      view(
        list,
        notificationSectionKey,
        isTheUserAgent(ua),
        entity,
        fullDisclosure.isComplete,
        sectionsComplete(fullDisclosure),
        request.isAgent,
        title,
        heading
      )
    )
  }

  private[controllers] def buildYourPersonalDetailsRow(personalDetails: PersonalDetails, entityKey: String)(implicit
    messages: Messages
  ): (String, TaskListRow) = {
    val isSectionComplete = personalDetails.isComplete
    val firstPage         = personalDetails.background.disclosureEntity
    val completeLink      = controllers.notification.routes.CheckYourAnswersController.onPageLoad
    val incompleteLink    = controllers.notification.routes.RelatesToController.onPageLoad(NormalMode)

    val operationKey           = getOperationKey(isSectionComplete, firstPage.isDefined)
    val notificationSectionKey = s"taskList.$operationKey.heading.first.$entityKey"

    (
      notificationSectionKey,
      buildRow(
        "personal-detail",
        s"first.$entityKey",
        isSectionComplete,
        firstPage.isDefined,
        completeLink,
        incompleteLink
      )
    )
  }

  private[controllers] def buildCaseReferenceRow(
    caseReference: CaseReference
  )(implicit messages: Messages): TaskListRow = {
    val isSectionComplete = caseReference.isComplete
    val firstPage         = caseReference.doYouHaveACaseReference
    val completeLink      = controllers.reference.routes.DoYouHaveACaseReferenceController.onPageLoad(NormalMode)
    val incompleteLink    = controllers.reference.routes.DoYouHaveACaseReferenceController.onPageLoad(NormalMode)

    buildRow("case-reference", "second", isSectionComplete, firstPage.isDefined, completeLink, incompleteLink)
  }

  private[controllers] def buildDeclarationRow(madeDeclaration: Boolean)(implicit messages: Messages): TaskListRow = {
    val declarationLink = controllers.routes.DeclarationController.onPageLoad

    buildRow("declaration", "declaration", madeDeclaration, madeDeclaration, declarationLink, declarationLink)
  }

  private[controllers] def buildOnshoreLiabilitiesDetailRow(
    onshoreLiabilities: Option[OnshoreLiabilities],
    madeDeclaration: Boolean
  )(implicit messages: Messages): TaskListRow = {
    val liabilities       = onshoreLiabilities.getOrElse(OnshoreLiabilities())
    val firstPage         = liabilities.behaviour
    val isSectionComplete = liabilities.isComplete
    val completeLink      = controllers.onshore.routes.CheckYourAnswersController.onPageLoad
    val incompleteLink    =
      controllers.onshore.routes.WhyAreYouMakingThisOnshoreDisclosureController.onPageLoad(NormalMode)

    buildRow(
      "onshore-liabilities",
      "third",
      isSectionComplete,
      firstPage.isDefined,
      completeLink,
      incompleteLink,
      madeDeclaration
    )
  }

  private[controllers] def buildOffshoreLiabilitiesDetailRow(
    offshoreLiabilities: OffshoreLiabilities,
    madeDeclaration: Boolean
  )(implicit messages: Messages): TaskListRow = {
    val firstPage         = offshoreLiabilities.behaviour
    val isSectionComplete = offshoreLiabilities.isComplete
    val completeLink      = controllers.offshore.routes.CheckYourAnswersController.onPageLoad
    val incompleteLink    = controllers.offshore.routes.WhyAreYouMakingThisDisclosureController.onPageLoad(NormalMode)

    buildRow(
      "offshore-liabilities",
      "forth",
      isSectionComplete,
      firstPage.isDefined,
      completeLink,
      incompleteLink,
      madeDeclaration
    )
  }

  private[controllers] def buildOtherLiabilityIssueRow(
    otherLiabilities: OtherLiabilities,
    isAnIndividual: Boolean,
    madeDeclaration: Boolean
  )(implicit messages: Messages): TaskListRow = {
    val firstPage         = otherLiabilities.issues
    val isSectionComplete = otherLiabilities.isComplete(isAnIndividual)
    val completeLink      = controllers.otherLiabilities.routes.CheckYourAnswersController.onPageLoad
    val incompleteLink    = controllers.otherLiabilities.routes.OtherLiabilityIssuesController.onPageLoad(NormalMode)

    buildRow(
      "other-liability-issues",
      "fifth",
      isSectionComplete,
      firstPage.isDefined,
      completeLink,
      incompleteLink,
      madeDeclaration
    )
  }

  private[controllers] def buildTheReasonForComingForwardNowRow(
    reasonForDisclosingNow: ReasonForDisclosingNow,
    madeDeclaration: Boolean
  )(implicit messages: Messages): TaskListRow = {
    val firstPage         = reasonForDisclosingNow.reason
    val isSectionComplete = reasonForDisclosingNow.isComplete
    val completeLink      = controllers.reason.routes.CheckYourAnswersController.onPageLoad
    val incompleteLink    = reason.routes.WhyAreYouMakingADisclosureController.onPageLoad(NormalMode)

    buildRow(
      "reason-for-coming-forward-now",
      "sixth",
      isSectionComplete,
      firstPage.isDefined,
      completeLink,
      incompleteLink,
      madeDeclaration
    )
  }

  private def buildRow(
    prefix: String,
    titleKeySuffix: String,
    isSectionComplete: Boolean,
    isFirstPageDefined: Boolean,
    completeLink: Call,
    incompleteLink: Call,
    madeDeclaration: Boolean = true
  )(implicit messages: Messages) = {
    val operationKey = getOperationKey(isSectionComplete, isFirstPageDefined)
    val titleKey     = s"taskList.$operationKey.sectionTitle.$titleKeySuffix"
    val link         = if (isSectionComplete) completeLink else incompleteLink
    TaskListRow(
      id = s"$prefix-task-list",
      sectionTitle = messages(titleKey),
      status = getStatusMessage(isSectionComplete, isFirstPageDefined),
      link = link,
      madeDeclaration = madeDeclaration
    )
  }

  private[controllers] def getOperationKey(isSectionComplete: Boolean, isFirstPageDefined: Boolean) =
    if (isSectionComplete || isFirstPageDefined) "edit" else "add"

  private[controllers] def buildLiabilitiesInformationRow(
    background: Background,
    onshoreLiabilities: Option[OnshoreLiabilities],
    offshoreLiabilities: OffshoreLiabilities,
    madeDeclaration: Boolean
  )(implicit messages: Messages): Seq[TaskListRow] =
    (background.offshoreLiabilities, background.onshoreLiabilities) match {
      case (Some(true), Some(true)) =>
        Seq(
          buildOnshoreLiabilitiesDetailRow(onshoreLiabilities, madeDeclaration),
          buildOffshoreLiabilitiesDetailRow(offshoreLiabilities, madeDeclaration)
        )
      case (Some(true), _)          => Seq(buildOffshoreLiabilitiesDetailRow(offshoreLiabilities, madeDeclaration))
      case (_, Some(true))          => Seq(buildOnshoreLiabilitiesDetailRow(onshoreLiabilities, madeDeclaration))
      case (Some(false), _)         => Seq(buildOnshoreLiabilitiesDetailRow(onshoreLiabilities, madeDeclaration))
      case (_, _)                   => Nil
    }

  private[controllers] def isTheUserAgent(userAnswers: UserAnswers): Boolean =
    userAnswers.get(AreYouTheEntityPage) match {
      case Some(AreYouTheEntity.YesIAm) => false
      case _                            => true
    }

  private[controllers] def sectionsComplete(fullDisclosure: FullDisclosure): Int = {
    import fullDisclosure._
    val section1Complete = personalDetails.isComplete
    val section2Complete =
      caseReference.isComplete && (!disclosingOffshoreLiabilities || offshoreLiabilities.isComplete) && (!disclosingOnshoreLiabilities || onshoreLiabilities
        .getOrElse(OnshoreLiabilities())
        .isComplete)
    val section3Complete =
      otherLiabilities.isComplete(personalDetails.isAnIndividual) && reasonForDisclosingNow.isComplete
    List(section1Complete, section2Complete, section3Complete).count(identity)
  }

  private[controllers] def getStatusMessage(isSectionComplete: Boolean, isInProgress: Boolean)(implicit
    messages: Messages
  ): String =
    messages(
      if (isSectionComplete) "taskList.status.completed"
      else if (isInProgress) "taskList.status.inProgress"
      else "taskList.status.notStarted"
    )

  private[controllers] def getTitle(isAgent: Boolean, section: String, userAnswers: UserAnswers)(implicit
    messages: Messages
  ): String = {
    val taskListSection = s"taskList.$section"
    if (!isAgent) {
      messages(taskListSection)
    } else {
      val submission = uaToSubmissionService.uaToSubmission(userAnswers)
      val reference  = submission.personalDetails.background.disclosureEntity.flatMap {
        case DisclosureEntity(Individual, _) =>
          submission.personalDetails.aboutTheIndividual
            .flatMap(_.fullName)
            .orElse(submission.personalDetails.aboutYou.fullName)
        case DisclosureEntity(Estate, _)     => submission.personalDetails.aboutTheEstate.flatMap(_.fullName)
        case DisclosureEntity(Company, _)    => submission.personalDetails.aboutTheCompany.flatMap(_.name)
        case DisclosureEntity(LLP, _)        => submission.personalDetails.aboutTheLLP.flatMap(_.name)
        case DisclosureEntity(Trust, _)      => submission.personalDetails.aboutTheTrust.flatMap(_.name)
      }

      reference match {
        case Some(ref) => messages(s"taskList.$section.reference", ref)
        case _         =>
          val date          = submission.created.atZone(ZoneId.systemDefault).toLocalDateTime
          val dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy HH:mma", new Locale(messages.lang.code))
          messages(s"taskList.$section.no.reference", date.format(dateFormatter))
      }
    }
  }
}
