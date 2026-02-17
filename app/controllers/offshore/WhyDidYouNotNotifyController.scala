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

package controllers.offshore

import controllers.actions._
import forms.WhyDidYouNotNotifyFormProvider
import models.WhyDidYouNotNotify
import models.WhyDidYouNotNotify._
import models.{Mode, RelatesTo, UserAnswers}
import navigation.OffshoreNavigator
import pages._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.offshore.WhyDidYouNotNotifyView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class WhyDidYouNotNotifyController @Inject() (
                                               override val messagesApi: MessagesApi,
                                               sessionService: SessionService,
                                               navigator: OffshoreNavigator,
                                               identify: IdentifierAction,
                                               getData: DataRetrievalAction,
                                               requireData: DataRequiredAction,
                                               formProvider: WhyDidYouNotNotifyFormProvider,
                                               val controllerComponents: MessagesControllerComponents,
                                               view: WhyDidYouNotNotifyView
                                             )(implicit ec: ExecutionContext)
  extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.get(WhyDidYouNotNotifyPage) match {
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
              updatedAnswers <- Future.fromTry(request.userAnswers.set(WhyDidYouNotNotifyPage, value))
              clearedPages   <- Future.fromTry(updatedAnswers.remove(pagesToClear))
              _              <- sessionService.set(clearedPages)
            } yield Redirect(navigator.nextPage(WhyDidYouNotNotifyPage, mode, clearedPages, hasValueChanged))
          }
        )
  }

  def changedPages(answers: UserAnswers, value: Set[WhyDidYouNotNotify]): (List[QuestionPage[_]], Boolean) =
    answers.get(WhyDidYouNotNotifyPage) match {
      case Some(reasons) if reasons != value => (getPages(value), true)
      case _                                 => (Nil, false)
    }

  private def getPages(reasons: Set[WhyDidYouNotNotify]): List[QuestionPage[_]] = {

    case class ClearingCondition(selections: Set[WhyDidYouNotNotify], pagesToClear: List[QuestionPage[_]]) {
      def isConditionMet(reasons: Set[WhyDidYouNotNotify]): Boolean = reasons.intersect(selections).isEmpty
    }

    val deliberate = ClearingCondition(Set(DeliberatelyDidNotNotify), List(ContractualDisclosureFacilityPage))
    val hasExcuse  = ClearingCondition(Set(ReasonableExcuse), List(WhatIsYourReasonableExcusePage))

    val conditions = List(deliberate, hasExcuse)

    conditions.foldLeft[List[QuestionPage[_]]](List()) { (cleared, condition) =>
      if (condition.isConditionMet(reasons)) cleared ++ condition.pagesToClear else cleared
    }
  }
}