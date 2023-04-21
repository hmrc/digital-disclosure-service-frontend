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
import forms.WhyAreYouMakingThisOnshoreDisclosureFormProvider
import javax.inject.Inject
import models.WhyAreYouMakingThisOnshoreDisclosure.{DidNotNotifyHasExcuse, InaccurateReturnWithCare, InaccurateReturnNoCare, NotFileHasExcuse, DidNotNotifyNoExcuse, DeliberatelyDidNotNotify, DeliberateInaccurateReturn, DeliberatelyDidNotFile}
import models.{Mode, UserAnswers, RelatesTo, WhyAreYouMakingThisOnshoreDisclosure}
import navigation.OnshoreNavigator
import pages._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.onshore.WhyAreYouMakingThisOnshoreDisclosureView

import scala.concurrent.{ExecutionContext, Future}

class WhyAreYouMakingThisOnshoreDisclosureController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionService: SessionService,
                                        navigator: OnshoreNavigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: WhyAreYouMakingThisOnshoreDisclosureFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: WhyAreYouMakingThisOnshoreDisclosureView
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(WhyAreYouMakingThisOnshoreDisclosurePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val areTheyTheIndividual = request.userAnswers.isTheUserTheIndividual
      val entity = request.userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      Ok(view(preparedForm, mode, areTheyTheIndividual, entity))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val areTheyTheIndividual = request.userAnswers.isTheUserTheIndividual
      val entity = request.userAnswers.get(RelatesToPage).getOrElse(RelatesTo.AnIndividual)

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, areTheyTheIndividual, entity))),

        value => {
          val (pagesToClear, hasValueChanged) = changedPages(request.userAnswers, value)
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(WhyAreYouMakingThisOnshoreDisclosurePage, value))
            clearedPages   <- Future.fromTry(updatedAnswers.remove(pagesToClear))
            _              <- sessionService.set(clearedPages)
          } yield Redirect(navigator.nextPage(WhyAreYouMakingThisOnshoreDisclosurePage, mode, clearedPages, hasValueChanged))
        }
      )
  }

  def changedPages(answers: UserAnswers, value: Set[WhyAreYouMakingThisOnshoreDisclosure]): (List[QuestionPage[_]], Boolean) = {
    answers.get(WhyAreYouMakingThisOnshoreDisclosurePage) match {
      case Some(reasons) if reasons != value => (WhyAreYouMakingThisOnshoreDisclosureController.getPages(value), true)
      case _ => (Nil, false)
    }
  }

}

object WhyAreYouMakingThisOnshoreDisclosureController {

  def getPages(reasons: Set[WhyAreYouMakingThisOnshoreDisclosure]): List[QuestionPage[_]] = {

    val deliberate = ClearingCondition(Set(DidNotNotifyNoExcuse, DeliberatelyDidNotNotify, DeliberateInaccurateReturn, DeliberatelyDidNotFile), List(CDFOnshorePage, TaxBeforeNineteenYearsOnshorePage))
    val didNotNotify = ClearingCondition(Set(DidNotNotifyHasExcuse), List(ReasonableExcuseOnshorePage))
    val inaccurate = ClearingCondition(Set(InaccurateReturnWithCare), List(ReasonableCareOnshorePage))
    val notFiled = ClearingCondition(Set(NotFileHasExcuse), List(ReasonableExcuseForNotFilingOnshorePage))

    val taxBeforeThreeYears = ClearingCondition(Set(DidNotNotifyHasExcuse, InaccurateReturnWithCare, NotFileHasExcuse), List(TaxBeforeThreeYearsOnshorePage))
    val taxBeforeFiveYears = ClearingCondition(Set(InaccurateReturnNoCare), List(TaxBeforeFiveYearsOnshorePage))

    val conditions = List(deliberate, didNotNotify, inaccurate, notFiled, taxBeforeThreeYears, taxBeforeFiveYears)

    conditions.foldLeft[List[QuestionPage[_]]](List()){
      (cleared, condition) => if(condition.isConditionMet(reasons)) cleared ++ condition.pagesToClear else cleared
    }
  }

}

case class ClearingCondition(selections: Set[WhyAreYouMakingThisOnshoreDisclosure], pagesToClear: List[QuestionPage[_]]) {
  def isConditionMet(reasons: Set[WhyAreYouMakingThisOnshoreDisclosure]): Boolean = reasons.intersect(selections).isEmpty
}
