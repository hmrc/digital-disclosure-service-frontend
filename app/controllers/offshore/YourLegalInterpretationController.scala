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
import forms.YourLegalInterpretationFormProvider
import javax.inject.Inject
import navigation.OffshoreNavigator
import models.{Mode, UserAnswers, YourLegalInterpretation, YourLegalInterpretationCheckboxes}
import models.YourLegalInterpretation._
import pages._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.offshore.YourLegalInterpretationView

import scala.concurrent.{ExecutionContext, Future}

class YourLegalInterpretationController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionService: SessionService,
                                        navigator: OffshoreNavigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: YourLegalInterpretationFormProvider,
                                        checkbox: YourLegalInterpretationCheckboxes,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: YourLegalInterpretationView
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(YourLegalInterpretationPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, checkbox.checkboxItems))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, checkbox.checkboxItems))),

        value => {

          val (pagesToClear, hasValueChanged) = changedPages(request.userAnswers, value)

          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(YourLegalInterpretationPage, value))
            clearedAnswers <- Future.fromTry(updatedAnswers.remove(pagesToClear))
            _              <- sessionService.set(clearedAnswers)
          } yield Redirect(navigator.nextPage(YourLegalInterpretationPage, mode, clearedAnswers, hasValueChanged))
        }
      )
  }

  def changedPages(userAnswers: UserAnswers, newValue: Set[YourLegalInterpretation]): (List[QuestionPage[_]], Boolean) =
    userAnswers.get(YourLegalInterpretationPage) match {
      case Some(oldValue) if (newValue.contains(NoExclusion)) => (List(UnderWhatConsiderationPage, HowMuchTaxHasNotBeenIncludedPage), true)
      case Some(oldValue) if (oldValue.contains(AnotherIssue) && !newValue.contains(AnotherIssue)) => (List(UnderWhatConsiderationPage), true)
      case Some(oldValue) if (oldValue != newValue) => (Nil, true)
      case _ => (Nil, false)
    }
}