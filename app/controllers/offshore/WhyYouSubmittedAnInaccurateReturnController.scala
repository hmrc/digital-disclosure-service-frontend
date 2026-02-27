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
import forms.offshore.WhyYouSubmittedAnInaccurateOffshoreReturnFormProvider
import models.{Mode, RelatesTo, UserAnswers, WhyYouSubmittedAnInaccurateReturn}
import models.WhyYouSubmittedAnInaccurateReturn.{DeliberatelyInaccurate, ReasonableMistake}
import navigation.OffshoreNavigator
import pages._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.offshore.WhyYouSubmittedAnInaccurateReturnView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class WhyYouSubmittedAnInaccurateReturnController @Inject() (
                                                              override val messagesApi: MessagesApi,
                                                              sessionService: SessionService,
                                                              navigator: OffshoreNavigator,
                                                              identify: IdentifierAction,
                                                              getData: DataRetrievalAction,
                                                              requireData: DataRequiredAction,
                                                              formProvider: WhyYouSubmittedAnInaccurateOffshoreReturnFormProvider,
                                                              val controllerComponents: MessagesControllerComponents,
                                                              view: WhyYouSubmittedAnInaccurateReturnView
                                                            )(implicit ec: ExecutionContext)
  extends FrontendBaseController
    with I18nSupport {

  private def getErrorKey(areTheyTheIndividual: Boolean, entity: RelatesTo): String =
    if (areTheyTheIndividual) "WhyYouSubmittedAnInaccurateReturn.error.required.you"
    else s"WhyYouSubmittedAnInaccurateReturn.error.required.${entity.toString.toLowerCase}"

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val areTheyTheIndividual = request.userAnswers.isTheUserTheIndividual
    val entity               = request.userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)
    val form                 = formProvider(getErrorKey(areTheyTheIndividual, entity))

    val preparedForm = request.userAnswers.get(WhyYouSubmittedAnInaccurateOffshoreReturnPage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, mode, areTheyTheIndividual, entity))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val areTheyTheIndividual = request.userAnswers.isTheUserTheIndividual
      val entity               = request.userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)
      val form                 = formProvider(getErrorKey(areTheyTheIndividual, entity))

      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode, areTheyTheIndividual, entity))),
          value => {
            val (pagesToClear, hasValueChanged) = changedPages(request.userAnswers, value)
            for {
              updatedAnswers <-
                Future.fromTry(request.userAnswers.set(WhyYouSubmittedAnInaccurateOffshoreReturnPage, value))
              clearedPages   <- Future.fromTry(updatedAnswers.remove(pagesToClear))
              _              <- sessionService.set(clearedPages)
            } yield Redirect(
              navigator.nextPage(WhyYouSubmittedAnInaccurateOffshoreReturnPage, mode, clearedPages, hasValueChanged)
            )
          }
        )
  }

  def changedPages(
                    answers: UserAnswers,
                    value: Set[WhyYouSubmittedAnInaccurateReturn]
                  ): (List[QuestionPage[_]], Boolean) =
    answers.get(WhyYouSubmittedAnInaccurateOffshoreReturnPage) match {
      case Some(reasons) if reasons != value => (WhyYouSubmittedAnInaccurateReturnController.getPages(value), true)
      case _                                 => (Nil, false)
    }

}

object WhyYouSubmittedAnInaccurateReturnController {

  def getPages(reasons: Set[WhyYouSubmittedAnInaccurateReturn]): List[QuestionPage[_]] = {

    val deliberate = ClearingCondition(
      Set(DeliberatelyInaccurate),
      List(ContractualDisclosureFacilityPage)
    )

    val reasonableCare = ClearingCondition(
      Set(ReasonableMistake),
      List(WhatReasonableCareDidYouTakePage)
    )

    val conditions = List(deliberate, reasonableCare)

    conditions.foldLeft[List[QuestionPage[_]]](List()) { (cleared, condition) =>
      if (condition.isConditionMet(reasons)) cleared ++ condition.pagesToClear else cleared
    }
  }

  case class ClearingCondition(
                                selections: Set[WhyYouSubmittedAnInaccurateReturn],
                                pagesToClear: List[QuestionPage[_]]
                              ) {
    def isConditionMet(reasons: Set[WhyYouSubmittedAnInaccurateReturn]): Boolean =
      reasons.intersect(selections).isEmpty
  }
}