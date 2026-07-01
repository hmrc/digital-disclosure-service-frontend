/*
 * Copyright 2026 HM Revenue & Customs
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
import forms.onshore.WhyDidYouNotNotifyOnshoreFormProvider
import models.WhyDidYouNotNotifyOnshore
import models.WhyDidYouNotNotifyOnshore._
import models.{Mode, RelatesTo, UserAnswers}
import navigation.OnshoreNavigator
import pages._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.onshore.WhyDidYouNotNotifyOnshoreView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class WhyDidYouNotNotifyOnshoreController @Inject() (
  override val messagesApi: MessagesApi,
  sessionService: SessionService,
  navigator: OnshoreNavigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: WhyDidYouNotNotifyOnshoreFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: WhyDidYouNotNotifyOnshoreView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val areTheyTheIndividual = request.userAnswers.isTheUserTheIndividual
    val entity               = request.userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)
    val form                 = formProvider(areTheyTheIndividual, entity)

    val preparedForm = request.userAnswers.get(WhyDidYouNotNotifyOnshorePage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, mode, areTheyTheIndividual, entity))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val areTheyTheIndividual = request.userAnswers.isTheUserTheIndividual
      val entity               = request.userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)
      val form                 = formProvider(areTheyTheIndividual, entity)

      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode, areTheyTheIndividual, entity))),
          value => {
            val (pagesToClear, hasValueChanged) = changedPages(request.userAnswers, value)
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(WhyDidYouNotNotifyOnshorePage, value))
              clearedPages   <- Future.fromTry(updatedAnswers.remove(pagesToClear))
              _              <- sessionService.set(clearedPages)
            } yield Redirect(navigator.nextPage(WhyDidYouNotNotifyOnshorePage, mode, clearedPages, hasValueChanged))
          }
        )
  }

  def changedPages(answers: UserAnswers, value: Set[WhyDidYouNotNotifyOnshore]): (List[QuestionPage[?]], Boolean) =
    answers.get(WhyDidYouNotNotifyOnshorePage) match {
      case Some(reasons) if reasons != value => (getPages(value), true)
      case _                                 => (Nil, false)
    }

  private def getPages(reasons: Set[WhyDidYouNotNotifyOnshore]): List[QuestionPage[?]] = {

    case class ClearingCondition(selections: Set[WhyDidYouNotNotifyOnshore], pagesToClear: List[QuestionPage[?]]) {
      def isConditionMet(reasons: Set[WhyDidYouNotNotifyOnshore]): Boolean = reasons.intersect(selections).isEmpty
    }

    val deliberate = ClearingCondition(Set(DeliberatelyDidNotNotifyOnshore), List(CDFOnshorePage))
    val hasExcuse  = ClearingCondition(Set(ReasonableExcuseOnshore), List(ReasonableExcuseOnshorePage))

    val conditions = List(deliberate, hasExcuse)

    conditions.foldLeft[List[QuestionPage[?]]](List()) { (cleared, condition) =>
      if (condition.isConditionMet(reasons)) cleared ++ condition.pagesToClear else cleared
    }
  }
}
