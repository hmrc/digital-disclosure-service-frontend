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

package controllers.onshore

import controllers.actions._
import forms.onshore.WhyDidYouNotFileAReturnOnTimeOnshoreFormProvider
import models.WhyDidYouNotFileAReturnOnTimeOnshore.{DeliberatelyWithheldInformation, DidNotWithholdInformationOnPurpose, ReasonableExcuse}
import models.WhyDidYouNotFileAReturnOnTimeOnshore

import javax.inject.Inject
import models.{Mode, RelatesTo, UserAnswers}
import navigation.OnshoreNavigator
import pages._
import pages.onshore.WhyDidYouNotFileAReturnOnTimeOnshorePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.onshore.WhyDidYouNotFileAReturnOnTimeOnshoreView

import scala.collection.immutable.Set
import scala.concurrent.{ExecutionContext, Future}

class WhyDidYouNotFileAReturnOnTimeOnshoreController @Inject() (
                                                                 override val messagesApi: MessagesApi,
                                                                 sessionService: SessionService,
                                                                 navigator: OnshoreNavigator,
                                                                 identify: IdentifierAction,
                                                                 getData: DataRetrievalAction,
                                                                 requireData: DataRequiredAction,
                                                                 formProvider: WhyDidYouNotFileAReturnOnTimeOnshoreFormProvider,
                                                                 val controllerComponents: MessagesControllerComponents,
                                                                 view: WhyDidYouNotFileAReturnOnTimeOnshoreView
                                                               )(implicit ec: ExecutionContext)
  extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.get(WhyDidYouNotFileAReturnOnTimeOnshorePage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    val areTheyTheIndividual = request.userAnswers.isTheUserTheIndividual
    val entity               = request.userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

    Ok(view(preparedForm, mode, areTheyTheIndividual, entity))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val areTheyTheIndividual = request.userAnswers.isTheUserTheIndividual
      val entity               = request.userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode, areTheyTheIndividual, entity))),
          value => {
            val (pagesToClear, hasValueChanged) = changedPages(request.userAnswers, value)
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(WhyDidYouNotFileAReturnOnTimeOnshorePage, value))
              clearedPages   <- Future.fromTry(updatedAnswers.remove(pagesToClear))
              _              <- sessionService.set(clearedPages)
            } yield Redirect(navigator.nextPage(WhyDidYouNotFileAReturnOnTimeOnshorePage, mode, clearedPages, hasValueChanged))
          }
        )
  }

  def changedPages(
                    answers: UserAnswers,
                    value: Set[WhyDidYouNotFileAReturnOnTimeOnshore]
                  ): (List[QuestionPage[_]], Boolean) =
    answers.get(WhyDidYouNotFileAReturnOnTimeOnshorePage) match {
      case Some(reasons) if reasons != value => (WhyDidYouNotFileAReturnOnTimeOnshoreController.getPages(value), true)
      case _                                 => (Nil, false)
    }

}

object WhyDidYouNotFileAReturnOnTimeOnshoreController {

  def getPages(reasons: Set[WhyDidYouNotFileAReturnOnTimeOnshore]): List[QuestionPage[_]] = {

    val deliberate = ClearingCondition(
      Set(DeliberatelyWithheldInformation),
      List(CDFOnshorePage)
    )

    val hasExcuse = ClearingCondition(
      Set(ReasonableExcuse),
      List(ReasonableExcuseForNotFilingOnshorePage)
    )

    val conditions = List(deliberate, hasExcuse)

    conditions.foldLeft[List[QuestionPage[_]]](List()) { (cleared, condition) =>
      if (condition.isConditionMet(reasons)) cleared ++ condition.pagesToClear else cleared
    }
  }

  case class ClearingCondition(
                                selections: Set[WhyDidYouNotFileAReturnOnTimeOnshore],
                                pagesToClear: List[QuestionPage[_]]
                              ) {
    def isConditionMet(reasons: Set[WhyDidYouNotFileAReturnOnTimeOnshore]): Boolean =
      reasons.intersect(selections).isEmpty
  }
}