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
import forms.WhereDidTheUndeclaredIncomeOrGainIncludedFormProvider
import javax.inject.Inject
import models.{Mode, UserAnswers, WhereDidTheUndeclaredIncomeOrGainIncluded}
import models.WhereDidTheUndeclaredIncomeOrGainIncluded._
import navigation.OffshoreNavigator
import pages.{WhereDidTheUndeclaredIncomeOrGainIncludedPage, QuestionPage, WhereDidTheUndeclaredIncomeOrGainPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.offshore.WhereDidTheUndeclaredIncomeOrGainIncludedView

import scala.concurrent.{ExecutionContext, Future}

class WhereDidTheUndeclaredIncomeOrGainIncludedController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionService: SessionService,
                                        navigator: OffshoreNavigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: WhereDidTheUndeclaredIncomeOrGainIncludedFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: WhereDidTheUndeclaredIncomeOrGainIncludedView
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(WhereDidTheUndeclaredIncomeOrGainIncludedPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),

        value => {

          val (pagesToClear, hasValueChanged) = changedPages(request.userAnswers, value)

          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(WhereDidTheUndeclaredIncomeOrGainIncludedPage, value))
            clearedAnswers <- Future.fromTry(updatedAnswers.remove(pagesToClear))
            _              <- sessionService.set(clearedAnswers)
          } yield Redirect(navigator.nextPage(WhereDidTheUndeclaredIncomeOrGainIncludedPage, mode, clearedAnswers, hasValueChanged))
        }
      )
  }

def changedPages(userAnswers: UserAnswers, newValue: Set[WhereDidTheUndeclaredIncomeOrGainIncluded]): (List[QuestionPage[_]], Boolean) =
    userAnswers.get(WhereDidTheUndeclaredIncomeOrGainIncludedPage) match {
      case Some(oldValue) if (!oldValue.contains(SomewhereElse) && newValue.contains(SomewhereElse)) => (Nil, true)
      case Some(oldValue) if (oldValue.contains(SomewhereElse) && !newValue.contains(SomewhereElse)) => (List(WhereDidTheUndeclaredIncomeOrGainPage), true)
      case Some(oldValue) if (oldValue != newValue) => (Nil, true)
      case _ => (Nil, false)
    }
}

